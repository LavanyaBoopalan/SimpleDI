package com.queomedia.repository;

import java.util.HashSet;
import java.util.Set;

import com.queomedia.annotations.Bean;

/**
 * Repository class for storage and retrieval  of demo.
 */
@Bean
public class DemoRepository {
	
	private static Set<String> demoSet = new HashSet<>();
	
	/**
	 * Method to create demo
	 * @param name
	 */
	public void create(String name) {
		demoSet.add(name);
	}
	
	/**
	 * Method to retrive demo based on the demo name.
	 * @param name
	 */
	public String retrieve(String name) {
		return demoSet.contains(name)? name: null;
	}
	

}
