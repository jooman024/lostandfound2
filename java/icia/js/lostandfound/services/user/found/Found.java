package icia.js.lostandfound.services.user.found;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import icia.js.lostandfound.beans.EmailBean;
import icia.js.lostandfound.beans.FileBean;
import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.FoundArticleImages;
import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.MainCategoryBean;
import icia.js.lostandfound.beans.MatchingAlarmBean;
import icia.js.lostandfound.beans.MatchingBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.beans.SearchBean;
import icia.js.lostandfound.beans.SortBean;
import icia.js.lostandfound.repository.userrepo.UserRepository;
import icia.js.lostandfound.utils.EmailService;
import icia.js.lostandfound.utils.Kafka;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Found {
	@Autowired
	private UserRepository urepo;
	@Autowired
	private ProjectUtils util;
	@Autowired
	private Kafka kaf;
	@Autowired
	private EmailService es;
	
	@Value("${expiredMonth}")
	private int expiredMonth;
	@Value("${expiredDay}")
	private int expiredDay;
	@Value("${ImgPathUserFound}")
	private String ImgPathUserFound;
	private boolean matching=false;
	public Found() {}
	
	public void backController(String serviceCode,Model model) {
		switch(serviceCode) {
		case "U12":
			getFoundComment(serviceCode,model);
			break;
		case "U16":
			getFoundTotal(serviceCode,model);
			break;
		case "U17-B":
			userFoundBefore(serviceCode,model);
			break;	
		case "U17":
			userFoundRegister(serviceCode,model);
			break;
		case "U23":
			modMyFound(serviceCode,model);
			break;
		case "U24":
			ImageSearchF(serviceCode,model);
			break;
		case "U24-F":
			ImageSearchFF(serviceCode,model);
			break;
		case "U25":
			delMyFound(serviceCode,model);
			break;
		case "U26":
			getFoundDetailSearch(serviceCode,model);
			break;
		case "U27":
			getMyFound(serviceCode,model);
			break;
		case "U34":
			foundMatching(serviceCode,model);
			break;
		case "U44":
			getBannerData(serviceCode,model);
			break;
		case "U46":
			delAlarmRow(serviceCode,model);
			break;
		case "U47":
			getMgrsCate(serviceCode,model);
			break;
		}
	}
	public ModelAndView backController(String serviceCode,ModelAndView model) {
		ModelAndView m=null;
		switch(serviceCode) {
		case "U43":
			alertMSG(serviceCode,model);
			break;
		}
		return m;
	}
	public ModelAndView backController(String serviceCode) {
		ModelAndView m=null;
		switch(serviceCode) {
		case "U16":
			m=getFoundTotal(serviceCode);
			break;
		}
		return m;
	}
	private void getMgrsCate(String serviceCode,Model model) {
		MainCategoryBean mb = (MainCategoryBean)model.getAttribute("items");
		mb=(MainCategoryBean)urepo.backController("U47",mb);
		model.addAttribute("items",mb);
	}
	private void getFoundComment(String serviceCode,Model model) {
		FoundCommentsBean foundcommentlist = (FoundCommentsBean)model.getAttribute("foundcomment");
		FoundArticleBean fa=(FoundArticleBean)urepo.backController("U12",foundcommentlist);
		System.out.println(fa);
		model.addAttribute("foundcomment",fa);
	}
	private void modMyFound(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getFalist().get(0).getFaDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getFalist().get(0).getFaDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,false));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void delMyFound(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		matching=true;
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				));
		matching=false;
		String imagePath = System.getProperty("user.dir") +ImgPathUserFound + items.getTemp3();
		String imagePath2 = System.getProperty("user.dir") +ImgPathUserFound + items.getTemp()+"bannerImg."+items.getTemp3().split("\\.")[1];
		String imagePath3 = System.getProperty("user.dir") +ImgPathUserFound + items.getTemp()+"obj."+items.getTemp3().split("\\.")[1];
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
	private void getMyFound(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}private void delAlarmRow(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getBannerData(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
		
		try {
			MemberBean m=(MemberBean)this.util.getAttribute("AccessInfo");
			items.setMember(m);
			MatchingBean ml =(MatchingBean)urepo.backController("U45",m);
			items.setMInfo(ml);
		}catch(Exception e){
			log.info("{}","get session holder fail");
		}
	}
	private void ImageSearchFF(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);	
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		serviceCode="U24";
        items = ((ItemsBean) urepo.backController(serviceCode, items));	
	}
	private void ImageSearchF(String serviceCode,Model model) {
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
			items.setTotalCount((int)urepo.backController("C05",items));
		}
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
        items = ((ItemsBean) urepo.backController(serviceCode, items));	
	}
	private void alertMSG(String serviceCode,ModelAndView model) {
		ItemsBean items = (ItemsBean) model.getModel().get("item");
		ArrayList<String[]> receiverList = new ArrayList<String[]>();
		ArrayList<String> contentList = new ArrayList<String>();
		
		for(MatchingAlarmBean a:items.getMInfo().getAlarm()) {
			receiverList.add(new String[]{a.getMaMmName(), a.getMaMmEmail()});
			contentList.add(es.emailTemplate(a.getMaLaName(),a.getMaFaName()));
		}
		if(es.sendEmail(
					EmailBean.builder()
						     .sender("jhpodl@naver.com")
					  		 .receiver(receiverList)
					  		 .subject("[LostAndFound] 분실물에 대한 매칭 정보가 있습니다")
					  		 .contents(contentList)
					  		 .isHtml(true)
					  		 .build())) {
			log.info("{}","email send success");
			items =((ItemsBean)urepo.backController(serviceCode,items));
		}
	}
	private void userFoundBefore(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items= util.imageSavingMyProject(serviceCode,model);
		try {
			items.getFalist().get(0).setFaMmId(((MemberBean)this.util.getAttribute("AccessInfo")).getMmId());
		} catch (Exception e) {
			log.info("{}","get session holder fail");
		}
		kaf.produceImgAnalysisToken(serviceCode,items);
	}
	private void userFoundRegister(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		try {
			items.getFalist().get(0).setFaMmId(((MemberBean)this.util.getAttribute("AccessInfo")).getMmId());
		} catch (Exception e) {
			log.info("{}","get session holder fail");
		}
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)urepo.backController(serviceCode,items));
		items =((ItemsBean)urepo.backController("U17-S",items));
		util.imageMove(serviceCode,items);
		String faNo=items.getFalist().get(0).getFaCtNumber();
		String fileExt=items.getFalist().get(0).getFaImgList().get(0).getFiName().split("\\.")[1];
		String cCate=items.getFalist().get(0).getFaImgList().get(0).getFiColorCate();
		String cate=items.getFalist().get(0).getFaImgList().get(0).getFiCate();
		ArrayList<FoundArticleImages> faImgList = new ArrayList<FoundArticleImages>();
		for (int j = 0; j < 3; j++) {
			FoundArticleImages faImg = new FoundArticleImages();
			faImg.setFiLoc("resources/images/Userfound/"+
					faNo + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")+"."+fileExt);
			faImg.setFiName(
					faNo + (j == 1 ? "bannerImg" : j == 2 ? "obj" : "")+"."+fileExt);
			faImg.setFiFacode(faNo);
			faImg.setFiColorCate(cCate);
			faImg.setFiCate(cate);
			faImgList.add(faImg);
		}
		items.getFalist().get(0).setFaImgList(faImgList);
		log.info("{}", "img setBean complete");
		items =((ItemsBean)urepo.backController(serviceCode,items));
		items.setMonthList(null);
		foundMatching("U34",model);
	}
	private void foundMatching(String serviceCode,Model model) {
		matching=true;
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		matching=false;
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getFoundDetailSearch(String serviceCode,Model model) {
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
			items.setTotalCount((int)urepo.backController("C03",items));
		}
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private void getFoundTotal(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);
		items.setPageType('T');
		items.setTotalCount((int)urepo.backController("C02",items));
		items.setTotalPageNo((items.getTotalCount()/ items.getRowNum())+((items.getTotalCount()%items.getRowNum()==0)?0:1));
		items =((ItemsBean)urepo.backController(serviceCode,items));
	}
	private ModelAndView getFoundTotal(String serviceCode) {
		String page="userfound";
		ItemsBean items = new ItemsBean();
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items.setStartIdx(((items.getPageNo()-1)*items.getRowNum())+1);
		items.setSortInfo(new SortBean());
		items.setPageType('T');
		items.setTotalCount((int)urepo.backController("C02",items));
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
		LocalDateTime now = LocalDateTime.now();
		for(LocalDate l : between) {
			list.add(new MonthBean(mlist[l.getMonthValue()-1]));
		}
		if(!matching&&(ChronoUnit.DAYS.between(sDate,now.minusDays(expiredDay).toLocalDate())<=0
				||ChronoUnit.DAYS.between(eDate,now.minusDays(expiredDay).toLocalDate())<=0)) 
				{list.add(new MonthBean(mlist[12]));}
		return list;
	}
	private ArrayList<MonthBean> makeMonthBeanList(LocalDate sDate, LocalDate eDate, boolean flag){
		String[] mlist = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DES","PORTAL"};
		ArrayList<MonthBean> list = new ArrayList<MonthBean>();
		List<LocalDate> between= getMonthsBetweenTwoMonths(sDate.withDayOfMonth(1), eDate.withDayOfMonth(1));
		LocalDateTime now = LocalDateTime.now();
		for(LocalDate l : between) {
			list.add(new MonthBean(mlist[l.getMonthValue()-1]));
		}
		if(flag&&(ChronoUnit.DAYS.between(sDate,now.minusDays(expiredDay).toLocalDate())<=0
				||ChronoUnit.DAYS.between(eDate,now.minusDays(expiredDay).toLocalDate())<=0)) 
				{list.add(new MonthBean(mlist[12]));}
		return list;
	}
}
