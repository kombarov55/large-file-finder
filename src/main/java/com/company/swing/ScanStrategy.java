package com.company.swing;

import java.util.Arrays;

public enum ScanStrategy {
	DIRECTORIES("Сканирование папок"),
	FILES("Сканирование файлов");
	
	public String description;

	private ScanStrategy(String description) {
		this.description = description;
	}
	
	public static String[] descriptions() {
		String[] result = new String[values().length];
		
		for (int i = 0; i < values().length; i++) {
			result[i] = values()[i].description;
		}
		
		return result;
	}
	
	public static ScanStrategy findByDescription(Object description) {
		return Arrays.stream(values())
			.filter(v -> v.description.equals(description))
			.findAny()
			.get();
	}
}
