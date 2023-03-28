package icia.js.lostandfound.beans;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FileBean implements Serializable {
	public FileBean(MultipartFile file) {
		this.file=file;
	}
	private transient MultipartFile file;
	private String fileExt;
	private String desPath;
	private String fileName;
}
