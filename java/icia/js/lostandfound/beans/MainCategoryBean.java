package icia.js.lostandfound.beans;

import java.util.ArrayList;

import lombok.Data;

@Data
public class MainCategoryBean {

	private String mcCode;
	private String mcName;
	private ArrayList<SubCategoryBean> subCategoryList;
	private ArrayList<MainCategoryBean> MainCategoryList;
}
