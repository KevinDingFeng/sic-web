package com.shenghesun.sic.information.model;

import lombok.Data;

@Data
public class InformationTypeModel {

	private String code;
	private String name;
	
	private String index;
	
	public InformationTypeModel() {
		
	}
	
	public InformationTypeModel(String code, String name) {
		this.code = code;
		this.name = name;
	}
}
