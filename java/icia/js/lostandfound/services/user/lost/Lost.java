package icia.js.lostandfound.services.user.lost;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import icia.js.lostandfound.beans.FoundArticleImages;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.LostArticleImages;
import icia.js.lostandfound.beans.LostCommentsBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.beans.SearchBean;
import icia.js.lostandfound.beans.SortBean;
import icia.js.lostandfound.repository.userrepo.UserRepository;
import icia.js.lostandfound.utils.Kafka;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Lost {
	
	@Autowired
	UserRepository urepo;
	@Autowired
	ProjectUtils util;
	@Autowired
	Kafka kaf;
	
	@Value("${expiredMonth}")
	private int expiredMonth;
	@Value("${expiredDay}")
	private int expiredDay;
	@Value("${ImgPathUserLost}")
	private String ImgPathUserLost;
	private boolean matching=false;
	public Lost() {}
	
	public void backController(String serviceCode,Model model) {
		switch(serviceCode) {
		case "U03":
			getLostTotal(serviceCode,model);
			break;
		case "U07-B":
			userLostBefore(serviceCode,model);
			break;	
		case "U07":
			userLostRegister(serviceCode,model);
			break;
		case "U09":
			getLostDetailSearch(serviceCode,model);
			break;
		case "U11":
			getLostComment(serviceCode,model);
			break;
		case "U13-L":
			ImageSearchLL(serviceCode,model);
			break;
		case "U13":
			ImageSearchL(serviceCode,model);
			break;
		case "U28":
			getMyLost(serviceCode,model);
			break;
		case "U33":
			lostMatching(serviceCode,model);
			break;
		case "U35":
			delMyLost(serviceCode,model);
			break;
		case "U38":
			getMyComment(serviceCode,model);
			break;
		case "U39":
			delMyComment(serviceCode,model);
			break;
		case "U40":
			modMyLost(serviceCode,model);
			break;
//		case "U26":
//			getFoundDetailSearch(serviceCode,model);
//			break;
		}
	}
	public ModelAndView backController(String serviceCode) {
		ModelAndView m=null;
		switch(serviceCode) {
		case "U03":
			m=getLostTotal(serviceCode);
			break;
		}
		return m;
	}
	private void getLostComment(String serviceCode, Model model) {
		LostCommentsBean lostComment=(LostCommentsBean)model.getAttribute("lostcomment");
		System.out.println("Lost class=>Lost getLostComment="+lostComment);
		LostArticleBean la=(LostArticleBean)urepo.backController("U11",lostComment);
		model.addAttribute("lostcomment", la);
	}
	private void modMyLost(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		try {
			items.setMember(((MemberBean)this.util.getAttribute("AccessInfo")));
		}catch(Exception e){
			log.info("{}","get session holder fail");
		}
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getLalist().get(0).getLaDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getLalist().get(0).getLaDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void delMyComment(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void delMyLost(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				));
		
		String imagePath = System.getProperty("user.dir") +ImgPathUserLost + items.getTemp3();
		String imagePath2 = System.getProperty("user.dir") +ImgPathUserLost + items.getTemp()+"bannerImg."+items.getTemp3().split("\\.")[1];
		String imagePath3 = System.getProperty("user.dir") +ImgPathUserLost + items.getTemp()+"obj."+items.getTemp3().split("\\.")[1];
        File imageFile = new File(imagePath);
        File imageFile2 = new File(imagePath2);
        File imageFile3 = new File(imagePath3);
        
		if (imageFile.delete()) {
			log.info("{} is deleted.", imageFile.getName());
			if (imageFile2.delete()) {
				log.info("{} is deleted.", imageFile2.getName());
				if (imageFile3.delete()) {
					log.info("{} is deleted.", imageFile3.getName());
					items = ((ItemsBean) urepo.backController(serviceCode, items));
				} else {
					log.info("Delete operation is failed.");
				}
			} else {
				log.info("Delete operation is failed.");
			}
		} else {
			log.info("Delete operation is failed.");
		}
	}
	private void getMyComment(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getMyLost(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void ImageSearchLL(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);	
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		serviceCode="U13";
        items = ((ItemsBean) urepo.backController(serviceCode, items));	
	}
	private void ImageSearchL(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		if(items.getFileInfo().getFile() != null) {
			items= util.imageSavingMyProject(serviceCode,model);
			items= util.imageSave(serviceCode, items);
		}
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);	
		if(items.getTotalCount()==0) {
			items.setSortInfo(new SortBean());
			items.setPageType('I');
			items.setTotalCount((int)urepo.backController("C06",items));
		}
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
        items = ((ItemsBean) urepo.backController(serviceCode, items));	
	}
	private void userLostBefore(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items= util.imageSavingMyProject(serviceCode,model);
		try {
			items.getLalist().get(0).setLaMmId(((MemberBean)this.util.getAttribute("AccessInfo")).getMmId());
		}catch(Exception e){
			log.info("{}","get session holder fail");
		}
		kaf.produceImgAnalysisToken(serviceCode,items);
	}
	private void userLostRegister(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		try {
			items.getLalist().get(0).setLaMmId(((MemberBean)this.util.getAttribute("AccessInfo")).getMmId());
		} catch (Exception e) {
			log.info("{}","get session holder fail");
		}
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
		items =((ItemsBean)urepo.backController("U07-S",items));
		util.imageMove(serviceCode,items);
		String laNo=items.getLalist().get(0).getLaCtNumber();
		String fileExt=items.getLalist().get(0).getLaImgList().get(0).getLiName().split("\\.")[1];
		String cCate=items.getLalist().get(0).getLaImgList().get(0).getLiColorCate();
		String cate=items.getLalist().get(0).getLaImgList().get(0).getLiCate();
		ArrayList<LostArticleImages> laImgList = new ArrayList<LostArticleImages>();
		for (int j = 0; j < 3; j++) {
			LostArticleImages laImg = new LostArticleImages();
			laImg.setLiLoc("resources/images/Userlost/"+
					laNo + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")+"."+ fileExt);
			laImg.setLiName(
					laNo + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")+"."+fileExt);
			laImg.setLiLaCode(laNo);
			laImg.setLiColorCate(cCate);
			laImg.setLiCate(cate);
			laImgList.add(laImg);
		}
		items.getLalist().get(0).setLaImgList(laImgList);
		log.info("{}", "img setBean complete");
		items =((ItemsBean)urepo.backController(serviceCode,items));
		items.setMonthList(null);
		lostMatching("U33",model);
	}
	private void lostMatching(String serviceCode,Model model) {
		matching=true;
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getMInfo().getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd")).minusMonths(1)
				,LocalDate.parse(items.getMInfo().getDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1)
				));
		matching=false;
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getLostDetailSearch(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		if(items.getSInfo()!=null) {
			SearchBean sInfo = items.getSInfo();
			if(sInfo.getSDate()!=null&&sInfo.getEDate()!=null) 
				items.setMonthList(makeMonthBeanList(
						LocalDate.parse(items.getSInfo().getSDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
						,LocalDate.parse(items.getSInfo().getEDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
						));
			else if(sInfo.getSDate()!=null) items.setMonthList(makeMonthBeanList(
					LocalDate.parse(items.getSInfo().getSDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
					,LocalDateTime.now().toLocalDate()));
			else if(sInfo.getEDate()!=null) items.setMonthList(makeMonthBeanList(
					LocalDateTime.now().minusMonths(expiredMonth).toLocalDate()
					,LocalDate.parse(items.getSInfo().getEDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
			else
				items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		}else
			items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
			
		
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);
		if(items.getTotalCount()==0) {
			items.setSortInfo(new SortBean());
			items.setPageType('S');
			items.setTotalCount((int)urepo.backController("C04",items));
		}
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getLostTotal(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);
		items.setPageType('T');
		items.setTotalCount((int)urepo.backController("C01",items));
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private ModelAndView getLostTotal(String serviceCode) {
		String page="userlost";
		ItemsBean items = new ItemsBean();
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);
		items.setSortInfo(new SortBean());
		items.setPageType('T');
		items.setTotalCount((int)urepo.backController("C01",items));
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		items =((ItemsBean)urepo.backController(serviceCode,items));
		ModelAndView mav = new ModelAndView();
		mav.addObject("items", new Gson().toJson(items));
		mav.setViewName(page);
		return mav;
	}
	private static List<LocalDate> getMonthsBetweenTwoMonths(LocalDate startDate, LocalDate endDate) {
		long numOfMonthsBetween = ChronoUnit.MONTHS.between(startDate, endDate)+1;
		return IntStream.iterate(0, i -> i + 1)
        	.limit(numOfMonthsBetween)
        	.mapToObj(i -> startDate.plusMonths(i))
		.collect(Collectors.toList());
	}
	private ArrayList<MonthBean> makeMonthBeanList(LocalDate sDate, LocalDate eDate){
		String[] mlist = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DES","PORTAL"};
		ArrayList<MonthBean> list = new ArrayList<MonthBean>();
		List<LocalDate> between= getMonthsBetweenTwoMonths(sDate.withDayOfMonth(1), eDate.withDayOfMonth(1));
		for(LocalDate l : between) {
			list.add(new MonthBean(mlist[l.getMonthValue()-1]));
		}
		if(matching) {list.add(new MonthBean(mlist[12]));}
		return list;
	}
}
