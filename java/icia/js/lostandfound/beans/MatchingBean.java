package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class MatchingBean implements Serializable {
	private ArrayList<String> keyword;
	private String mcCode;
	private String ucCode;
	private String loCode;
	private String plCode;
	private String date;
	
	private ArrayList<MatchingAlarmBean> alarm;
}

