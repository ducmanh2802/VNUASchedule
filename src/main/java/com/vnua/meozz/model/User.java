package com.vnua.meozz.model;

import java.io.Serializable;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
//	private ArrayList<Tuan> 
	
	public User(String userName, String jsonString) {
		this.username=userName;
//		this.jsonString = jsonString;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

//	public String getJsonString() {
//		return jsonString;
//	}
//	
	@Override
	public String toString() {
		return new StringBuilder().append("User{").append("Input Code: ")
                .append(username).append(", Last name: ")
//                .append(lastName).append("}")
                .toString();
	}
}
