package com.queomedia.core;

/**
 * Custom Exception that occurs during dependency initialization. 
 */
public class DependencyException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DependencyException(String message) {
		super(message);
	}

}
