package ru.miron.Enums;

import java.util.ArrayList;
import java.util.List;

public enum PossibleIMGsSuffixes {
	PNG("png"), JPEG("jpeg"), GIF("gif"), JPG("jpg");
	
	private String name;
	
	PossibleIMGsSuffixes(String name) {
		this.name = name;
	}
	
	public String getSuffix() {
		return name;
	}
	
	public static boolean contains(String suffixToCheck) {
		for(PossibleIMGsSuffixes s : values()) {
			if(s.getSuffix().equals(suffixToCheck)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<String> getSuffixesList(){
		PossibleIMGsSuffixes[] values = values();
		List<String> valuesList = new ArrayList<String>(values.length);
		for(PossibleIMGsSuffixes value : values) {
			valuesList.add(value.getSuffix());
		}
		return valuesList;
	}
}
