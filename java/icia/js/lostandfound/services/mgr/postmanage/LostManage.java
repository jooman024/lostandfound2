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
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.LostCommentsBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LostManage {
	@Autowired
	ManagerRepository mrepo;
	@Value("${expiredMonth}")
	private int expiredMonth;
	@Value("${expiredDay}")
	private int expiredDay;
	
	public LostManage() {}
	
	public Object backController(String serviceCode,Model model) {
		Object object= null;
		switch(serviceCode) {
		case "M13":
			delLost(serviceCode,model);
			break;
		case "M12":
			getMgrLost(serviceCode,model);
			break;
		case "M32":
			object = selLostComments(serviceCode,model);
			break;
		case "M35":
			delLostComment(serviceCode,model);
			break;
		}
		return object;
	}
	private CommentsBean selLostComments(String serviceCode,Model model) {
		CommentsBean comments=(CommentsBean)model.getAttribute("comments");
		comments =((CommentsBean)mrepo.backController(serviceCode, comments));
		return comments;
	}
	private void delLostComment(String serviceCode , Model model) {
		LostCommentsBean lostcomment=(LostCommentsBean)model.getAttribute("lostcomment");
		lostcomment = ((LostCommentsBean)mrepo.backController(serviceCode, lostcomment));
	}
	private void delLost(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items.setMonthList(makeMonthBeanList(
				LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				,LocalDate.parse(items.getTemp2(),DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		items =((ItemsBean)mrepo.backController(serviceCode,items));
	}
	private void getMgrLost(String serviceCode,Model model) {
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
		String[] mlist = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DES"};
		ArrayList<MonthBean> list = new ArrayList<MonthBean>();
		List<LocalDate> between= getMonthsBetweenTwoMonths(sDate.withDayOfMonth(1), eDate.withDayOfMonth(1));
		for(LocalDate l : between) {
			list.add(new MonthBean(mlist[l.getMonthValue()-1]));
		}
		return list;
	}
}
