package simpledi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.queomedia.annotations.Bean;
import com.queomedia.app.ApplicationConfig;
import com.queomedia.app.DemoApplication;
import com.queomedia.core.DependencyException;
import com.queomedia.core.DependencyManager;

class DemoApplicationTest {
	
	static DemoApplication app;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
			DependencyManager context = new DependencyManager(ApplicationConfig.class);
			app = context.getInstance(DemoApplication.class);
			if(app == null) {
				throw new DependencyException("DemoApplication class is not initialized");
			}
			app.createdemo();
	}
	
	/**
	 * check whether the DemoServiceImplA object and field is injected
	 */
	@Test
	void testRetreiveDemo1() {
		assertEquals(app.retrieveDemo1(), "A");
	}
	
	
	/**
	 * check whether the DemoServiceImplB object and field is injected
	 */
	@Test
	void testRetreiveDemo2() {
		assertEquals(app.retrieveDemo2(), "B");
	}

}
