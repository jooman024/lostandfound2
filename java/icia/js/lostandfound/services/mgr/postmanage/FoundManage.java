package icia.js.lostandfound.services.mgr.postmanage;

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

import icia.js.lostandfound.beans.CommentsBean;
import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;

@Service
public class FoundManage {
	@Autowired
	ManagerRepository mrepo;
	@Value("${expiredMonth}")
	private int expiredMonth;
	@Value("${expiredDay}")
	private int expiredDay;
	
	public FoundManage() {}
	
	public Object backController(String serviceCode,Model model) {
		Object object= null;
		switch(serviceCode) {
		case "M14":
			getMgrFound(serviceCode,model);
			break;
		case "M15":
			delFound(serviceCode,model);
			break;
		case "M33":
			object=selFoundComments(serviceCode,model);
			break;
		case "M34":
			delFoundComment(serviceCode,model);
			break;
		}
		return object;
	}
	private CommentsBean selFoundComments(String serviceCode,Model model) {
		CommentsBean comments=(CommentsBean)model.getAttribute("comments");
		comments =((CommentsBean)mrepo.backController(serviceCode, comments));
		return comments;
	}
	
	private void delFoundComment(String serviceCode , Model model) {
		FoundCommentsBean foundcomment=(FoundCommentsBean)model.getAttribute("foundcomment");
		foundcomment = ((FoundCommentsBean)mrepo.backController(serviceCode, foundcomment));
	}
	private void delFound(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd")),
				false));
		items =((ItemsBean)mrepo.backController(serviceCode,items));
	}
	private void getMgrFound(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		items =((ItemsBean)mrepo.backController(serviceCode,items));
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
		if((ChronoUnit.DAYS.between(sDate,now.minusDays(expiredDay).toLocalDate())<=0
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
