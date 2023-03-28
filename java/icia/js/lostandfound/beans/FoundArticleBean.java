package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class FoundArticleBean implements Serializable {
	private String faMcCode;
	private String faUcCode;
	private String faCtNumber;
	private String faName; 
	private String faPlace; 
	private String faDate; 
	private String faMmId; 
	private String faStatus;
	private String faUnique;
	private String faHour;
	private String faColor;
	private String faDepPlace;
	//private String faOrName;
	//private String faOrTel;
	
	private MemberBean member;
	private ArrayList<FoundCommentsBean> faCommentList = new ArrayList<FoundCommentsBean>();
	private ArrayList<FoundArticleImages> faImgList = new ArrayList<FoundArticleImages>();
}
