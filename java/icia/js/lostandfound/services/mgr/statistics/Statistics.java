package icia.js.lostandfound.services.mgr.statistics;

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

import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;

@Service
public class Statistics {

	@Autowired
	ManagerRepository mrepo;
	@Value("${expiredMonth}")
	private int expiredMonth;
	@Value("${expiredDay}")
	private int expiredDay;
	public Statistics() {}
	
	public void backController(String serviceCode,Model model) {
		switch(serviceCode) {
		case "M01":
			Statisticss(serviceCode,model);
			break;
		case "M02":
			Statisticss(serviceCode,model);
			break;
		case "M03":
			Statisticss(serviceCode,model);
			break;
		}
	}
	private void Statisticss(String serviceCode,Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		if(serviceCode.equals("M01"))
			items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusMonths(expiredMonth).toLocalDate(),LocalDateTime.now().toLocalDate()));
		else
			items.setMonthList(makeMonthBeanList(LocalDateTime.now().minusDays(expiredDay).toLocalDate(),LocalDateTime.now().toLocalDate()));
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
		LocalDateTime now = LocalDateTime.now();
		for(LocalDate l : between) {
			list.add(new MonthBean(mlist[l.getMonthValue()-1]));
		}
		
		return list;
	}
}
