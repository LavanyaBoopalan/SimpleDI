package com.queomedia.app;

import com.queomedia.annotations.Bean;
import com.queomedia.annotations.Inject;
import com.queomedia.annotations.Named;
import com.queomedia.core.DependencyManager;
import com.queomedia.service.DemoService;

/**
 * class DemoApplication create and retrieve Demo using the service.
 */
@Bean
public class DemoApplication {
	
	@Inject
	@Named(value="demoServiceImplA")	
	DemoService demoService1;
	
	@Inject
	@Named(value="demoServiceImplB")	
	DemoService demoService2;
	
	public void createdemo() {
		demoService1.createDemo();
		demoService2.createDemo();
	}
	
	public String retrieveDemo1() {
		return demoService1.retrieveDemo();
	}
	
	public String retrieveDemo2() {
		return demoService2.retrieveDemo();
	}

}
