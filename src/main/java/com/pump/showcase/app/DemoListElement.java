package com.pump.showcase.app;

import java.util.Objects;

public class DemoListElement {
	String name;
	String classSimpleName;

	public DemoListElement(String name, String classSimpleName) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(classSimpleName);
		this.name = name;
		this.classSimpleName = classSimpleName;
	}

	public String getDemoName() {
		return name;
	}

	public String getDemoSimpleClassName() {
		return classSimpleName;
	}
}
