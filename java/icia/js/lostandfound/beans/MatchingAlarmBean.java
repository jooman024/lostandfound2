package icia.js.lostandfound.beans;

import lombok.Data;

@Data
public class MatchingAlarmBean {
	private String maMmId;
	private String maMmName;
	private String maFaCtNumber;
	private String maLaCtNumber;
	private String maLaName;
	private String maFaName;
	private String maDate;
	private String maMmEmail;
	
	private FoundArticleBean found;
}
