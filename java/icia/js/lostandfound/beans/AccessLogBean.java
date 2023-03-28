package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class AccessLogBean implements Serializable {
	private String acId;
	private String acUserId;
	private String acPrivateIp;
	private int acType;
	private String acDate;
	private String acBroswer;
	
	private ArrayList<PageVisitBean> pvList;
}
