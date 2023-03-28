package icia.js.lostandfound.beans;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JWTBean {
	//private String mmId;
	private String mmName;
	private String mmEmail;
	private String mmSns;
	private String mmPhone;
}
