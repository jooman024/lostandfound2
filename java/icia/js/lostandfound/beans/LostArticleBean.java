package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class LostArticleBean implements Serializable {
	private String laMcCode;
	private String laUcCode;
	private String laCtNumber;
	private String laName;
	private String laPlace;
	private String laDate;
	private String laStatus;
	private String laLocation;
	private String laUnique;
	private String laMmId;
	private String laHour;
	
	private MemberBean member;
	private ArrayList<LostCommentsBean> laCommentList = new ArrayList<LostCommentsBean>();
	private ArrayList<LostArticleImages> laImgList = new ArrayList<LostArticleImages>();
}
