package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.ArrayList;

import icia.js.lostandfound.services.mgr.statistics.Statistics;
import lombok.Data;

@Data
public class ItemsBean implements Serializable {
	public ItemsBean() {
		rowNum=10;
		pageNo=1;
	}
	private int rowNum;
	private int pageNo;
	private int startIdx;
	private int totalCount;
	private int totalPageNo;
	private char pageType;
	private String serviceCode;
	private MemberBean member;
	private String temp;
	private String temp2;
	private String temp3;
	private MatchingBean mInfo;
	private ImageSearchBean isInfo;
	private SearchBean sInfo;
	private SortBean sortInfo;
	private FileBean fileInfo;
	private StatisticsBean stat;
	private ArrayList<FoundArticleBean> balist;
	private ArrayList<FoundArticleBean> falist;
	private ArrayList<LostArticleBean> lalist;
	private ArrayList<MonthBean> monthList;
}
