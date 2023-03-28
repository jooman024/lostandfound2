package icia.js.lostandfound.beans;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ImageSearchBean {
	private String imageColorCate;
	private String imageCate;
	private ArrayList<String> keyword;
}
