package com.queomedia.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.queomedia.annotations.Bean;
import com.queomedia.annotations.ComponentScan;
import com.queomedia.annotations.Configuration;
import com.queomedia.annotations.Inject;
import com.queomedia.annotations.Named;
import com.queomedia.repository.DemoRepository;
import com.queomedia.service.DemoService;
import com.queomedia.service.DemoServiceImplA;


/**
 * DependencyManager responsible for 
 *  1. Perform component scanning based on package name
 *  2. initialize and inject objects based on Bean and Named Annotation
 *  3. initialize and inject object fields based on Inject and Named Annotation 
 * 
 */
public class DependencyManager {

	//Map to store class and its object
	Map<Class<?>, Object> objectRegister = new HashMap<>();
	//Map to store NamedValue and class
	Map<String, Class<?>> objFieldValue = new HashMap<>();
	//objects created only for the purpose of identifying the packages  
	DemoService service = new DemoServiceImplA();
	DemoRepository repository = new DemoRepository();
	
	private static final Logger logger = Logger
			.getLogger(DependencyManager.class.getName());

	public DependencyManager(Class<?> clazz) {
		initializeObjRegister(clazz);
		initializeObjFields();
	}

	/**
	 * This method perform component scanning based on Application configuration
	 * initializes the class based on Bean and Named Annotation 
	 */
	private void initializeObjRegister(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Configuration.class)) {
			logger.info("Performs Component scanning");
			ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
			String packageValue = componentScan.value();
			logger.info("Retrieves all the classes under the package : "+packageValue);
			Set<Class<?>> classes = getClasses(packageValue);
			for (Class<?> loadingClass : classes) {
				try {
					if (loadingClass.isAnnotationPresent(Bean.class)) {
						logger.info("Instantiate the classes annotated with Bean: " +loadingClass.getName());
						Constructor<?> constructor = loadingClass.getDeclaredConstructor();
						Object newInstance = constructor.newInstance();						
						objectRegister.put(loadingClass, newInstance);						
						if (loadingClass.isAnnotationPresent(Named.class)) {
							logger.info("Maps the classes annotated with Bean and Named: " +loadingClass.getName());
							String namedValue = loadingClass.getAnnotation(Named.class).value();
							if (null == objFieldValue.get(namedValue)) {
								objFieldValue.put(namedValue, loadingClass);
							} else {
								throw new DependencyException(
										"More than one Bean with the same name found " + namedValue);
							}
						}
					}
				} catch (Exception e) {
					logger.severe(e.getMessage());
				}
			}
		}
	}

	/**
	 * 
	 * Retrieve all the classes under the specified package
	 * @param packageName
	 * @return Set
	 */
	private Set<Class<?>> getClasses(String packageName) {
		Set<Class<?>> classSet = new HashSet<>();
		List<String> pkgList = findSubPackageNames(packageName);
		for (String pkg : pkgList) {
			logger.info("Retrieves all the classes under the sub package : "+pkg);
			classSet.addAll(getClassesFromSubPackage(pkg));
		}
		return classSet;

	}

	private Set<Class<?>> getClassesFromSubPackage(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Method retrieves all the packages with the given name
	 * @param packageName
	 * @return List<String>
	 */
	public List<String> findSubPackageNames(String packageName) {
	    Package[] pkgs = Package.getPackages();
	    List<Package> pkgList = Arrays.asList(pkgs);
	    return pkgList.stream().map(Package::getName)
	        .filter(n -> n.startsWith(packageName)).toList();
	        
	}

	/**
	 * Retrieve class based on the className and packageName
	 * @param className
	 * @param packageName
	 * @return
	 */
	private Class<?> getClass(String className, String packageName) {
		try {
			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			logger.severe(e.getMessage());
		}
		return null;
	}

	/**
	 * Provides the instance of Demo Application
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public <T> T getInstance(Class<T> clazz) throws Exception {
		T object = (T) objectRegister.get(clazz);
		return object;
	}

	/**
	 * Initializes object fields 
	 */
	private void initializeObjFields() {
		Set<Class<?>> classes = objectRegister.keySet();
		for (Class<?> cl : classes) {
			Object object = objectRegister.get(cl);
			Field[] declaredFields = cl.getDeclaredFields();
			try {
				injectFields(object, declaredFields);
			} catch (Exception e) {
				logger.severe(e.getMessage());
			}
		}
	}

	/**
	 * Injects object fields 
	 * @param <T>
	 * @param object
	 * @param fields
	 * @throws Exception
	 */
	private <T> void injectFields(T object, Field[] fields) throws Exception {
		for (Field field : fields) {
			if (field.isAnnotationPresent(Inject.class)) {
				logger.info("Inject annotated fields  : "+field.getName() );
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (field.isAnnotationPresent(Named.class)) {
					String namedValue = field.getAnnotation(Named.class).value();
					type = objFieldValue.get(namedValue);
					if (null == type) {
						field.set(object, namedValue);
					}
				}
				Object innerObject = objectRegister.get(type);
				if (null != innerObject) {
					field.set(object, innerObject);
				}
			}
		}
	}

}
