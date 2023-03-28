package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class MemberBean implements Serializable {
	private String mmId;
	private String mmName;
	private String mmEmail;
	private String mmPhone;
	private String mmSns;
	private String mmLimit;
	private String mmDate;
	
	private ArrayList<AccessLogBean> acList;
	private ArrayList<MemberBean> memberList;
}
