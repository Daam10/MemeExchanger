package ru.miron.Enums;

import java.util.ArrayList;
import java.util.List;

public enum TypesOfIdentificator {
	LOGIN("login"), ID("id");
	
	private String name;
	
	TypesOfIdentificator(String name) {
		this.name = name;
	}
	
	public String getTypeName() {
		return name;
	}
	
	public static List<String> getTypesOfIdentificatorList(){
		TypesOfIdentificator[] values = values();
		List<String> valuesList = new ArrayList<String>(values.length);
		for(TypesOfIdentificator value : values) {
			valuesList.add(value.getTypeName());
		}
		return valuesList;
	}
	
	public static boolean contains(String typeToCheck) {
		for(TypesOfIdentificator s : values()) {
			if(s.getTypeName().equals(typeToCheck)) {
				return true;
			}
		}
		return false;
	}
	
	public static TypesOfIdentificator getByString(String typeToGet) {
		for(TypesOfIdentificator s : values()) {
			if(s.getTypeName().equals(typeToGet)) {
				return s;
			}
		}
		return null;
	}
}
