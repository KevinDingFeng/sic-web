package com.shenghesun.sic.ad.model;

public enum LimitType {
	Day("天"), Week("周"), Month("月"), No("不限");
	
	private String text;
	
	public String getText() {
		return this.text;
	}
	private LimitType(String text) {
		this.text = text;
	}
}
