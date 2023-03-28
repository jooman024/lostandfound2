package icia.js.lostandfound.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class SearchBean implements Serializable {
	private String mcCode;
	private String ucCode;
	private String place;
	private String color;
	private String name;
	private String depPlace;
	private String sDate;
	private String eDate;
	//lostArticle
	private String location;
	private String orname;
}
