package ru.miron.Enums;

import java.util.ArrayList;
import java.util.List;

public enum TypesOfEntity {
	USERS("users"), POSTS("posts"), POST_COMMENTS("postComments"), IP_ADDRESSES("ipAddress");
	
	private String name;
	
	TypesOfEntity(String name) {
		this.name = name;
	}
	
	public String getTypeName() {
		return name;
	}
	
	public static List<String> getTypesOfEntityList(){
		TypesOfEntity[] values = values();
		List<String> valuesList = new ArrayList<String>(values.length);
		for(TypesOfEntity value : values) {
			valuesList.add(value.getTypeName());
		}
		return valuesList;
	}
	
	public static TypesOfEntity getByString(String typeToGet) {
		for(TypesOfEntity s : values()) {
			if(s.getTypeName().equals(typeToGet)) {
				return s;
			}
		}
		return null;
	}
	
	public static boolean contains(String typeToCheck) {
		for(TypesOfEntity s : values()) {
			if(s.getTypeName().equals(typeToCheck)) {
				return true;
			}
		}
		return false;
	}
}
