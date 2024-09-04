package com.queomedia.service;

import com.queomedia.annotations.Bean;
import com.queomedia.annotations.Inject;
import com.queomedia.annotations.Named;
import com.queomedia.repository.DemoRepository;

/**
 * Implementation class for DemoService which takes demo name A.
 *  
 */
@Bean
@Named(value="demoServiceImplA")
public class DemoServiceImplA implements DemoService {
	
	@Inject
	DemoRepository demoRepository;	
	
	@Inject
	@Named(value="A")	
	String name;
	

	@Override
	public void createDemo() {
		demoRepository.create(name);
	}


	@Override
	public String retrieveDemo() {
		return demoRepository.retrieve(name);
		
	}

}
