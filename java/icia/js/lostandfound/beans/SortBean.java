package icia.js.lostandfound.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class SortBean implements Serializable {
	public SortBean() {
		sortNum=1;
		asc=true;
	}
	private int sortNum;
	private boolean asc;
}
