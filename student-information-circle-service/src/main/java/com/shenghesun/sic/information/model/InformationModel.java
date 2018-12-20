package com.shenghesun.sic.information.model;

import lombok.Data;

@Data
public class InformationModel {

	private String uuid;
	private String title;
	private String context;
	private String imgs;
	private boolean cost;
	
	//以后可能会带有 作者等信息
	private String author;
	
}
