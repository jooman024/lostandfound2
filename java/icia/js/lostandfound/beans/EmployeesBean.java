package icia.js.lostandfound.beans;

import java.util.ArrayList;

public class EmployeesBean {
	private String empCode;
	private String empLevCode;
	private String empName;
	private String empPin;
	private String empImgCode;
	private String empLevName;
	private ArrayList<AccessLogBean> accessList;
	
	public String getEmpLevName() {
		return empLevName;
	}
	public void setEmpLevName(String empLevName) {
		this.empLevName = empLevName;
	}
	
	public ArrayList<AccessLogBean> getAccessList() {
		return accessList;
	}
	public void setAccessList(ArrayList<AccessLogBean> accessList) {
		this.accessList = accessList;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getEmpLevCode() {
		return empLevCode;
	}
	public void setEmpLevCode(String empLevCode) {
		this.empLevCode = empLevCode;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmpPin() {
		return empPin;
	}
	public void setEmpPin(String empPin) {
		this.empPin = empPin;
	}
	public String getEmpImgCode() {
		return empImgCode;
	}
	public void setEmpImgCode(String empImgCode) {
		this.empImgCode = empImgCode;
	}
}
