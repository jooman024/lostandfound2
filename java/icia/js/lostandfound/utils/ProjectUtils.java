package icia.js.lostandfound.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.WebDetection;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.protobuf.ByteString;

import icia.js.lostandfound.JsonWebTokenService;
import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.FoundArticleImages;
import icia.js.lostandfound.beans.ImageSearchBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.JWTBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.LostArticleImages;
import icia.js.lostandfound.beans.MemberBean;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Component
@Slf4j
public class ProjectUtils {
	
	@Value("${ImgPathFound}")
	private String ImgPathFound;
	@Value("${ImgPathLost}")
	private String ImgPathLost;
	@Value("${ImgPathUserFound}")
	private String ImgPathUserFound;
	@Value("${ImgPathUserLost}")
	private String ImgPathUserLost;
	@Value("${ImgPathSearch}")
	private String ImgPathSearch;
	@Autowired
	private JsonWebTokenService jwts;
	@Autowired
    ProjectUtils util;
	
    public Object getAttribute(String name) throws Exception {
        return (Object) RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }
    public void setAttribute(String name, Object object) throws Exception {
        RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
    }
    public void removeAttribute(String name) throws Exception {
        RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }
    public String getSessionId() throws Exception  {
        return RequestContextHolder.getRequestAttributes().getSessionId();
    }
	public void transferJWTByResponse(String jwt) {
		((ServletRequestAttributes)RequestContextHolder
				.getRequestAttributes())
				.getResponse().setHeader("JWTForJSFramework", jwt);
	}
   	public String getHeaderInfo(boolean flag) {
   		HttpServletRequest req = 
   				((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
   		return (flag)? req.getRemoteAddr() : req.getHeader("user-agent");
   	}
   	public void issuanceJWT(MemberBean member) {
		JWTBean jwtBody = JWTBean.builder()
				.mmName(member.getMmName())
				.mmEmail(member.getMmEmail())
				.mmSns(member.getMmSns())
				.mmPhone(member.getMmPhone())
				.build();
		this.transferJWTByResponse(this.jwts.tokenIssuance(jwtBody, member.getMmId()));
		log.info("{}","issuanceJWT end");
	}
   	public boolean isMyPage(String action) {
   		return action.equals("Mgr")
   				||action.equals("MgrCate")
   				||action.equals("MgrFound")
   				||action.equals("MgrLost")
   				||action.equals("MgrProfile")
   				||action.equals("MgrComments")
   				||action.equals("User")
   				||action.equals("UserFound")
   				||action.equals("UserFoundReg")
   				||action.equals("UserLost")
   				||action.equals("UserLostReg")
   				||action.equals("UserMyComment")
   				||action.equals("UserMyFound")
   				||action.equals("UserMyLost")
   				||action.equals("UserMyPage")
   				||action.equals("UserSCenter");
   	}
	public String getBrowserInfo(String header) {
		String browser = null;
		String version = null;

		if (header.toLowerCase().indexOf("windows") != -1) {
			if (header.toLowerCase().indexOf("firefox") != -1) {
				browser = "W-FIREFOX";
				version = header.toLowerCase().split("firefox/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("opera") != -1 || header.toLowerCase().indexOf("opr") != -1) {
				browser = "W-OPERA";
				if (header.toLowerCase().indexOf("opera") != 1)
					version = header.toLowerCase().split("opera/")[1].split(" ")[0];
				else
					version = header.toLowerCase().split("opr/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("whale") != -1) {
				browser = "W-WHALE";
				version = header.toLowerCase().split("whale/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("naver") != -1) {
				browser = "W-NAVER";
				version = "inapp";
			} else if (header.toLowerCase().indexOf("trident") != -1) {
				browser = "W-IE";
				version = header.toLowerCase().split("rv:")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("edge") != -1 || header.toLowerCase().indexOf("edg") != -1) {
				browser = "W-EDGE";
				if (header.toLowerCase().indexOf("edge") != -1)
					version = header.toLowerCase().split("edge/")[1].split("\\.")[0];
				else
					version = header.toLowerCase().split("edg/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("chrome") != -1) {
				browser = "W-CHROME";
				version = header.toLowerCase().split("chrome/")[1].split("\\.")[0];
			}
		} else {
			if (header.toLowerCase().indexOf("fxios") != -1) {
				browser = "M-FIREFOX";
				version = header.toLowerCase().split("fxios/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("opt") != -1 || header.toLowerCase().indexOf("opr") != -1) {
				browser = "M-OPERA";
				version = header.toLowerCase().split("opt/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("whale") != -1) {
				browser = "M-WHALE";
				version = header.toLowerCase().split("whale/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("naver") != -1) {
				browser = "M-NAVER";
				version = "inapp";
			} else if (header.toLowerCase().indexOf("edge") != -1 || header.toLowerCase().indexOf("edg") != -1) {
				browser = "M-EDGE";
				if (header.toLowerCase().indexOf("edge") != -1)
					version = header.toLowerCase().split("edge/")[1].split("\\.")[0];
				else
					version = header.toLowerCase().split("edg/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("crios") != -1) {
				browser = "M-CHROME";
				version = header.toLowerCase().split("crios/")[1].split("\\.")[0];
			} else if (header.toLowerCase().indexOf("duckduckgo") != -1) {
				browser = "M-DUCKDUCKGO";
				version = header.toLowerCase().split("duckduckgo/")[1].split(" ")[0];
			} else if (header.toLowerCase().indexOf("safari") != -1) {
				browser = "M-SAFARI";
				version = header.toLowerCase().split("safari/")[1].split("\\.")[0];
			}
		}
		return browser + "(v." + version + ")";
	}
	public Object imageSave(String serviceCode,Object obj,String sourcePath) {
		Object result=null;
		try {
			if(serviceCode.equals("M8")||serviceCode.equals("M9")) {
				ArrayList<FoundArticleImages> faImgList = new ArrayList<FoundArticleImages>();
				FoundArticleBean fa=(FoundArticleBean)obj;
				String fileExt = "." + (sourcePath.split("images")[1].split("\\.")[1]);
				String Path = System.getProperty("user.dir") + ImgPathFound;
				if(!imageDownload(sourcePath, Path, fa.getFaCtNumber() + fileExt)) {}
				log.info("{}", "img down complete");
				String ColorCate=null;
				if(makeBackgroundRemoveImg(Path, fa.getFaCtNumber(), fileExt)) {
					log.info("{}", "backgroundremover complete");
					ColorCate = detectProperties(Path + fa.getFaCtNumber() + "obj" + fileExt);
					log.info("{}", "getColorCate complete");
				}else {
					ColorCate ="none";
				}
				String imgCate = detectLabels(Path + fa.getFaCtNumber() + fileExt);
				imgCate += detectWebDetections(Path + fa.getFaCtNumber() + fileExt);
				log.info("{}", "getImgCate complete");
				if (imgCate != null && ColorCate != null) {
					for (int j = 0; j < 3; j++) {
						FoundArticleImages faImg = new FoundArticleImages();
						faImg.setFiCate(imgCate);
						faImg.setFiLoc("resources/images/found/"+ fa.getFaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")
								+ fileExt);
						faImg.setFiName(
								fa.getFaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "") + fileExt);
						faImg.setFiFacode(fa.getFaCtNumber());
						faImg.setFiColorCate(ColorCate);
						faImgList.add(faImg);
					}
					fa.setFaImgList(faImgList);
					result=fa;
					log.info("{}", "img setBean complete");
				} else {
					log.info("{}", "img setting fail");
				}
			}
			else if(serviceCode.equals("M7")) {
				ArrayList<LostArticleImages> laImgList = new ArrayList<LostArticleImages>();
				LostArticleBean la=(LostArticleBean)obj;
				String fileExt = "." + (sourcePath.split("images")[1].split("\\.")[1]);
				String Path = System.getProperty("user.dir") + ImgPathLost;
				if(!imageDownload(sourcePath, Path, la.getLaCtNumber() + fileExt)) {}
				log.info("{}", "img down complete");
				
				String ColorCate=null;
				if(makeBackgroundRemoveImg(Path, la.getLaCtNumber(), fileExt)) {
					log.info("{}", "backgroundremover complete");
					ColorCate = detectProperties(Path + la.getLaCtNumber() + "obj" + fileExt);
					log.info("{}", "getColorCate complete");
				}else {
					ColorCate ="none";
				}
				String imgCate = detectLabels(Path + la.getLaCtNumber() + fileExt);
				imgCate += detectWebDetections(Path + la.getLaCtNumber() + fileExt);
				log.info("{}", "getImgCate complete");
				if (imgCate != null && ColorCate != null) {
					for (int j = 0; j < 3; j++) {
						LostArticleImages laImg = new LostArticleImages();
						laImg.setLiCate(imgCate);
						laImg.setLiLoc("resources/images/lost/"+ la.getLaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")
								+ fileExt);
						laImg.setLiName(
								la.getLaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "") + fileExt);
						laImg.setLiLaCode(la.getLaCtNumber());
						laImg.setLiColorCate(ColorCate);
						laImgList.add(laImg);
					}
					la.setLaImgList(laImgList);
					result=la;
					log.info("{}", "img setBean complete");
				} else {
					log.info("{}", "img setting fail");
				}
			}
		}catch(Exception e) {
   		 e.printStackTrace();
   		 log.info("{}", "imageSave fail");
   	 	}
		return result;
	}
	public void imageMove(String serviceCode,ItemsBean item) {
		if(serviceCode.equals("U17")) {
			String fileExt=item.getFalist().get(0).getFaImgList().get(0).getFiName().split("\\.")[1];
			String imgPath=System.getProperty("user.dir") + ImgPathUserFound;		
	   		
			Path sourcePath = Paths.get(imgPath+"foundtemp."+fileExt);
			Path targetPath = Paths.get(imgPath+item.getFalist().get(0).getFaCtNumber()+"."+fileExt);
			Path sourcePath2 = Paths.get(imgPath+"foundtempbannerImg."+fileExt);
			Path targetPath2 = Paths.get(imgPath+item.getFalist().get(0).getFaCtNumber()+"bannerImg."+fileExt);
			Path sourcePath3 = Paths.get(imgPath+"foundtempobj."+fileExt);
			Path targetPath3 = Paths.get(imgPath+item.getFalist().get(0).getFaCtNumber()+"obj."+fileExt);
			try {
				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				Files.move(sourcePath2, targetPath2, StandardCopyOption.REPLACE_EXISTING);
				Files.move(sourcePath3, targetPath3, StandardCopyOption.REPLACE_EXISTING);
				log.info("{}", "imageMove sucess");
			}catch(Exception e) {
				log.info("{}", "imageMove fail" + e.getMessage());
			}
		}else if(serviceCode.equals("U07")) {
			String fileExt=item.getLalist().get(0).getLaImgList().get(0).getLiName().split("\\.")[1];
			String imgPath=System.getProperty("user.dir") + ImgPathUserLost;		
	   		
			Path sourcePath = Paths.get(imgPath+"losttemp."+fileExt);
			Path targetPath = Paths.get(imgPath+item.getLalist().get(0).getLaCtNumber()+"."+fileExt);
			Path sourcePath2 = Paths.get(imgPath+"losttempbannerImg."+fileExt);
			Path targetPath2 = Paths.get(imgPath+item.getLalist().get(0).getLaCtNumber()+"bannerImg."+fileExt);
			Path sourcePath3 = Paths.get(imgPath+"losttempobj."+fileExt);
			Path targetPath3 = Paths.get(imgPath+item.getLalist().get(0).getLaCtNumber()+"obj."+fileExt);
			try {
				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				Files.move(sourcePath2, targetPath2, StandardCopyOption.REPLACE_EXISTING);
				Files.move(sourcePath3, targetPath3, StandardCopyOption.REPLACE_EXISTING);
				log.info("{}", "imageMove sucess");
			}catch(Exception e) {
				log.info("{}", "imageMove fail" + e.getMessage());
			}
		}
	}
     public ItemsBean imageSave(String serviceCode,ItemsBean item) {
    	 try {
    		 if(serviceCode.equals("U07-B")) {
        		 LostArticleBean la = item.getLalist().get(0);
    			String ColorCate = null;
    			if (makeBackgroundRemoveImg(item.getFileInfo().getDesPath(), la.getLaCtNumber(), item.getFileInfo().getFileExt())) {
    				log.info("{}", "backgroundremover complete");
    				ColorCate = detectProperties(item.getFileInfo().getDesPath() + la.getLaCtNumber() + "obj" + item.getFileInfo().getFileExt());
    				log.info("{}", "getColorCate complete");
    			} else {
    				ColorCate = "none";
    			}
    			String imgCate = detectLabels(item.getFileInfo().getDesPath() + la.getLaCtNumber() + item.getFileInfo().getFileExt());
    	 		imgCate += detectWebDetections(item.getFileInfo().getDesPath() + la.getLaCtNumber() + item.getFileInfo().getFileExt());
    	 		log.info("{}", "getImgCate complete");
    	 		ArrayList<LostArticleImages> laImgList = new ArrayList<LostArticleImages>();
    	 		if (imgCate != null && ColorCate != null) {
    	 			for (int j = 0; j < 3; j++) {
    	 				LostArticleImages laImg = new LostArticleImages();
    	 				laImg.setLiCate(imgCate);
    	 				laImg.setLiLoc("resources/images/Userlost/"+ la.getLaCtNumber()+ (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")
    	 						+ item.getFileInfo().getFileExt());
    	 				laImg.setLiName(
    	 						la.getLaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "") + item.getFileInfo().getFileExt());
    	 				laImg.setLiLaCode(la.getLaCtNumber());
    	 				laImg.setLiColorCate(ColorCate);
    	 				laImgList.add(laImg);
    	 			}
    	 			item.getLalist().get(0).setLaImgList(laImgList);
    	 			log.info("{}", "img setBean complete");
    	 		} else {
    	 			log.info("{}", "img setting fail");
    	 		}
        	 }else if(serviceCode.equals("U17-B")){
        		 FoundArticleBean fa = item.getFalist().get(0);
     			String ColorCate = null;
     			if (makeBackgroundRemoveImg(item.getFileInfo().getDesPath(), fa.getFaCtNumber(), item.getFileInfo().getFileExt())) {
     				log.info("{}", "backgroundremover complete");
     				ColorCate = detectProperties(item.getFileInfo().getDesPath() + fa.getFaCtNumber() + "obj" + item.getFileInfo().getFileExt());
     				log.info("{}", "getColorCate complete");
     			} else {
     				ColorCate = "none";
     			}
     			String imgCate = detectLabels(item.getFileInfo().getDesPath() + fa.getFaCtNumber() + item.getFileInfo().getFileExt());
     	 		imgCate += detectWebDetections(item.getFileInfo().getDesPath() + fa.getFaCtNumber() +item.getFileInfo().getFileExt());
     	 		log.info("{}", "getImgCate complete");
     	 		ArrayList<FoundArticleImages> faImgList = new ArrayList<FoundArticleImages>();
     	 		if (imgCate != null && ColorCate != null) {
     	 			for (int j = 0; j < 3; j++) {
     	 				FoundArticleImages faImg = new FoundArticleImages();
     	 				faImg.setFiCate(imgCate);
     	 				faImg.setFiLoc("resources/images/Userfound/"+ fa.getFaCtNumber()+ (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")
     	 						+ item.getFileInfo().getFileExt());
     	 				faImg.setFiName(
     	 						fa.getFaCtNumber() + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "") + item.getFileInfo().getFileExt());
     	 				faImg.setFiFacode(fa.getFaCtNumber());
     	 				faImg.setFiColorCate(ColorCate);
     	 				faImgList.add(faImg);
     	 			}
     	 			item.getFalist().get(0).setFaImgList(faImgList);
     	 			log.info("{}", "img setBean complete");
     	 		} else {
     	 			log.info("{}", "img setting fail");
     	 		}
        	 }else if(serviceCode.equals("U24")||serviceCode.equals("U13")){
        		 String ColorCate = null;
     			if (makeBackgroundRemoveImg(item.getFileInfo().getDesPath(), item.getFileInfo().getFileName(), item.getFileInfo().getFileExt())) {
     				log.info("{}", "backgroundremover complete");
     				ColorCate = detectProperties(item.getFileInfo().getDesPath() + item.getFileInfo().getFileName() + "obj" + item.getFileInfo().getFileExt());
     				log.info("{}", "getColorCate complete");
     			} else {
     				ColorCate = "none";
     			}
     			String imgCate = detectLabels(item.getFileInfo().getDesPath() + item.getFileInfo().getFileName() + item.getFileInfo().getFileExt());
     	 		imgCate += detectWebDetections(item.getFileInfo().getDesPath() + item.getFileInfo().getFileName() + item.getFileInfo().getFileExt());
     	 		log.info("{}", "getImgCate complete");
     	 		if (imgCate != null && ColorCate != null) {
     	 			item.setIsInfo(new ImageSearchBean());
     	 			item.getIsInfo().setImageCate(imgCate);
     	 			item.getIsInfo().setImageColorCate(ColorCate);
     	 			ArrayList<String> keyword = new ArrayList<String>();

     	 			if (imgCate.contains(",")) {
     	 			    for (String s : imgCate.trim().split(",")) {
     	 			        String[] splitStr = s.trim().split(" ");
     	 			        if (splitStr.length > 1) {
     	 			            keyword.add(splitStr[0]);
     	 			            keyword.add(splitStr[1]);
     	 			        } else {
     	 			            keyword.add(s.trim());
     	 			        }
     	 			    }
     	 			} else {
     	 			    String[] splitStr = imgCate.trim().split(" ");
     	 			    if (splitStr.length > 1) {
     	 			        keyword.add(splitStr[0]);
     	 			        keyword.add(splitStr[1]);
     	 			    } else {
     	 			        keyword.add(imgCate.trim());
     	 			    }
     	 			}
     	 			item.getIsInfo().setKeyword(keyword);
     	 			log.info("{}", "img setBean complete");
     	 		} else {
     	 			log.info("{}", "img setting fail");
     	 		}
        	 }
    		 
    	 }catch(Exception e) {
    		 e.printStackTrace();
    		 log.info("{}", "imageSave fail");
    	 }
    	 return item;
    }
     public boolean makeBackgroundRemoveImg(String Path,String fileName,String fileExt){
 		boolean result=true;
 		Mat source = Imgcodecs.imread(Path+fileName+fileExt);
 		if(!source.empty()) {
 			Mat original = source.clone();
 			Mat destination = new Mat(source.rows(), source.cols(), source.type());
 			Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
 			Imgproc.threshold(destination, destination, 100, 255, Imgproc.THRESH_BINARY);
 			Mat colorImage = new Mat();
 			Imgproc.cvtColor(destination, colorImage, Imgproc.COLOR_GRAY2RGB);
 			Mat mask = new Mat();
 			Core.bitwise_not(destination, mask);
 			Mat foreground = new Mat();
 			original.copyTo(foreground, mask);
 			Imgcodecs.imwrite(Path+fileName+"obj"+fileExt, foreground);
 		}else {
 			result=false;
 			log.info("{}", "The source image is empty.");
 		}
 		return result;
 	}
     public ItemsBean imageSavingMyProject(String serviceCode,Model model)
     {
    	 ItemsBean items=(ItemsBean)model.getAttribute("items");
    	 if(serviceCode.equals("U17-B")) {
    		 items.getFileInfo().setDesPath(System.getProperty("user.dir") + ImgPathUserFound);
    		 FoundArticleBean fa=new FoundArticleBean();
    		 fa.setFaCtNumber("foundtemp");
    		 ArrayList<FoundArticleBean> faList = new ArrayList<FoundArticleBean>();
    		 faList.add(fa);
    		 items.setFalist(faList);
    		items.getFileInfo().setFileName(items.getFalist().get(0).getFaCtNumber());
    	 }else if(serviceCode.equals("U07-B")) {
    		 items.getFileInfo().setDesPath(System.getProperty("user.dir") + ImgPathUserLost);
    		 LostArticleBean la=new LostArticleBean();
    		 la.setLaCtNumber("losttemp");
    		 ArrayList<LostArticleBean> laList = new ArrayList<LostArticleBean>();
    		 laList.add(la);
    		 items.setLalist(laList);
    		 items.getFileInfo().setFileName(items.getLalist().get(0).getLaCtNumber());
    	 }else if(serviceCode.equals("U24")||serviceCode.equals("U13")) {
    		 items.getFileInfo().setDesPath(System.getProperty("user.dir") + ImgPathSearch);
    		 items.getFileInfo().setFileName("sourceimg");
    	 }
	     MultipartFile file = items.getFileInfo().getFile();
	     if (!file.isEmpty()) {
	         try {
	        	 if(serviceCode.equals("U24")||serviceCode.equals("U13")) {
	        		 byte[] fileBytes = file.getBytes();
	        		 String fileName = file.getOriginalFilename();
	        		 String fileType = file.getContentType();
	        		 items.getFileInfo().setFileExt("." + fileType.split("\\/")[1]);
	        		 Path path = Paths.get(items.getFileInfo().getDesPath()+items.getFileInfo().getFileName()+items.getFileInfo().getFileExt());
	        		 Files.write(path, fileBytes);
	        		 log.info("{}", "File Saving success");
	        	 }else {
	        		 String fileName = file.getOriginalFilename();
		             items.getFileInfo().setFileExt("." + fileName.split("\\.")[1]);
		             items.getFileInfo().setFileName(items.getFileInfo().getFileName()+items.getFileInfo().getFileExt());
		             byte[] bytes = file.getBytes();
		             Path path = Paths.get(items.getFileInfo().getDesPath()+items.getFileInfo().getFileName());
		             Files.write(path, bytes);
		             log.info("{}", "File Saving success");
		             createThumbnailImage(items.getFileInfo().getDesPath(),items.getFileInfo().getFileName());
	        	 }
	         } catch (IOException e) {
	             e.printStackTrace();
	             log.info("{}", "File Saving fail");
	         }
	     } else {
	    	 log.info("{}", "File empty");
	     }
	     items.getFileInfo().setFile(null);
	     return items;
     }
     private boolean imageDownload(String url,String savePath,String fileName) throws MalformedURLException,IOException{
 		boolean result=true;
 		File f = new File(savePath, fileName);
 		try {
 		    FileUtils.copyURLToFile(new URL(url), f);
 		    if (f.exists() && f.length() > 0) {
 		    	log.info("{}", "Image received successfully");
 		    } else {
 		    	result=false;
 		    	log.info("{}", "Failed to receive image");
 		    }
 		} catch (IOException e) {
 			log.info("{}", "Failed to receive image"+ e.getMessage());
 		}
 		createThumbnailImage(savePath,fileName);
 		return result;
 	}
 	public static String detectLabels(String filePath) throws IOException {
 		String imgCate=null;
 	    List<AnnotateImageRequest> requests = new ArrayList<>();

 	    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

 	    Image img = Image.newBuilder().setContent(imgBytes).build();
 	    Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
 	    AnnotateImageRequest request =
 	        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
 	    requests.add(request);

 	    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
 	      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
 	      List<AnnotateImageResponse> responses = response.getResponsesList();

 	      for (AnnotateImageResponse res : responses) {
 	        if (res.hasError()) {
 	          System.out.format("Error: %s%n", res.getError().getMessage());
 	          return null;
 	        }
 	        int count=0;
 	        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
 	        	 if(count==2) break;
 	          if(imgCate!=null) imgCate+=","+annotation.getDescription();
 	          else imgCate=annotation.getDescription();
 	          count++;
 	        }
 	      }
 	    }
 	    return translateText(imgCate);
 	  }
 	private static String detectWebDetections(String filePath) throws IOException {
 		String imgCate=null;
 		List<AnnotateImageRequest> requests = new ArrayList<>();
 	    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
 	    Image img = Image.newBuilder().setContent(imgBytes).build();
 	    Feature feat = Feature.newBuilder().setType(Type.WEB_DETECTION).build();
 	    AnnotateImageRequest request =
 	        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
 	    requests.add(request);
 	    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
 	      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
 	      List<AnnotateImageResponse> responses = response.getResponsesList();

 	      for (AnnotateImageResponse res : responses) {
 	        if (res.hasError()) {
 	          System.out.format("Error: %s%n", res.getError().getMessage());
 	          return null;
 	        }
 	        WebDetection annotation = res.getWebDetection();
 	        int count=0;
 	        for (WebEntity entity : annotation.getWebEntitiesList()) {
 	          if(count==3) break;
 	          if(imgCate!=null) imgCate+=","+entity.getDescription();
 	          else imgCate=","+entity.getDescription();
 	          count++;
 	        }
 	      }
 	      return translateText(imgCate);
 	    }
 	}
 	 public static String translateText(String text) throws IOException {
	    String projectId = "lostportal-lostandfound";
	    String targetLanguage = "ko";
	    return translateText(projectId, targetLanguage, text);
	  }

	  public static String translateText(String projectId, String targetLanguage, String text)
	      throws IOException {
		  StringBuffer sb= new StringBuffer();
	    try (TranslationServiceClient client = TranslationServiceClient.create()) {
	      LocationName parent = LocationName.of(projectId, "global");
	      TranslateTextRequest request =
	          TranslateTextRequest.newBuilder()
	              .setParent(parent.toString())
	              .setMimeType("text/plain")
	              .setTargetLanguageCode(targetLanguage)
	              .addContents(text)
	              .build();

	      TranslateTextResponse response = client.translateText(request);
	      
	      for (Translation translation : response.getTranslationsList()) {
	    	  sb.append(translation.getTranslatedText());
	      }
	      log.info("{}", "trans successfully");
	    }catch(Exception e) {
	    	e.printStackTrace();
	    	log.info("{}", "trans error");
	    }
	    return sb.toString();
	  }
 	//vision get color 
 	private static String detectProperties(String filePath) throws IOException {
 		String ColorCate=null;
 		List<AnnotateImageRequest> requests = new ArrayList<>();

 	    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

 	    Image img = Image.newBuilder().setContent(imgBytes).build();
 	    Feature feat = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).build();
 	    AnnotateImageRequest request =
 	        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
 	    requests.add(request);
 	    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
 	      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
 	      List<AnnotateImageResponse> responses = response.getResponsesList();

 	      for (AnnotateImageResponse res : responses) {
 	        if (res.hasError()) {
 	          System.out.format("Error: %s%n", res.getError().getMessage());
 	          return null;
 	        }
 	        DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
 	        for (ColorInfo color : colors.getColorsList()) {
 		          if(ColorCate!=null) ColorCate+="-"+ color.getColor().getRed()+":"+color.getColor().getGreen()+":"+color.getColor().getBlue();
 		          else ColorCate=color.getColor().getRed()+":"+color.getColor().getGreen()+":"+color.getColor().getBlue();
 	        }
 	        
 	      }
 	      
 	    }
 	    return ColorCate;
 	  } 
private void createThumbnailImage(String savePath, String fileName) {
		
		int[][] thumbImageSize = { { 250, 150 }, { 80, 48 } };
		int[] cropImageSize = new int[2];

		try {
			BufferedImage bufferImage = ImageIO.read(new File(savePath+fileName));
			int[] rootSize = { bufferImage.getWidth(), bufferImage.getHeight() };
			cropImageSize[0] = rootSize[0];
			cropImageSize[1] = (rootSize[0] * thumbImageSize[0][1]) / thumbImageSize[0][0];
			if (cropImageSize[1] > rootSize[1]) {
				cropImageSize[0] = (rootSize[1] * thumbImageSize[0][0]) / thumbImageSize[0][1];
				cropImageSize[1] = rootSize[1];
			}
			BufferedImage cropImage = Thumbnails.of(bufferImage).crop(Positions.CENTER)
					.size(cropImageSize[0], cropImageSize[1]).asBufferedImage();

			String[] thumb = { "orgin","bannerImg", "thumSmall"};
			String filename=fileName.split("\\.")[0];
			String fileExt = "." + fileName.split("\\.")[1];
			
			for (int idx = 1; idx < 2; idx++) {
				Thumbnails.of(cropImage).size(thumbImageSize[idx - 1][0], thumbImageSize[idx - 1][1])
						.toFile(savePath + "\\"+ filename+ thumb[idx] + fileExt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
