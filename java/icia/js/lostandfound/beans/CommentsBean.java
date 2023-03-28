package icia.js.lostandfound.beans;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CommentsBean {
	private ArrayList<FoundCommentsBean> fcList;
	private ArrayList<LostCommentsBean> lcList;
}
