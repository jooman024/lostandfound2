package icia.js.lostandfound.services.mgr.catemanage;

import java.io.IOException; 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import icia.js.lostandfound.SimpleTransactionManager;
import icia.js.lostandfound.TransactionAssistant;
import icia.js.lostandfound.beans.CategoriesBean;
import icia.js.lostandfound.beans.MainCategoryBean;
import icia.js.lostandfound.beans.SubCategoryBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Catemanage extends TransactionAssistant {
	private SimpleTransactionManager tranmanager;
	@Autowired
	public Catemanage cm;
	@Autowired
	public ManagerRepository mr;
	@Autowired
	private ProjectUtils pu;

	public void backController(String serviceCode, Model model) {
		
		switch (serviceCode) {
		case "M16":
			System.out.println(serviceCode);
			getMainCategories(serviceCode, model);
			break;
		case "M17":
			System.out.println(serviceCode);
			getSubCategories(serviceCode, model);
			break;
		case "M18":
			System.out.println(serviceCode);
			addSubCategory(serviceCode, model);
			break;
		case "M19":
			System.out.println(serviceCode);
			updSubCategory(serviceCode, model);
			break;	
		case "M20":
			delSubCategory(serviceCode, model);
			break;
		} 
	}
	private void updSubCategory(String serviceCode , Model model) {
		MainCategoryBean mcate=(MainCategoryBean)model.getAttribute("mcate");
		mcate = ((MainCategoryBean)mr.backController2(serviceCode, mcate));
	}
	private void addSubCategory(String serviceCode , Model model) {
		MainCategoryBean mcate=(MainCategoryBean)model.getAttribute("mcate");
		mcate = ((MainCategoryBean)mr.backController2(serviceCode, mcate));
	}
	private void delSubCategory(String serviceCode , Model model) {
		MainCategoryBean mcate=(MainCategoryBean)model.getAttribute("mcate");
		mcate = ((MainCategoryBean)mr.backController2(serviceCode, mcate));
	}
	private void getMainCategories(String serviceCode , Model model) {
		MainCategoryBean mcate=(MainCategoryBean)model.getAttribute("mcate");
		mcate = ((MainCategoryBean)mr.backController2(serviceCode, mcate));
	}
	private void getSubCategories(String serviceCode , Model model) {
		MainCategoryBean mcate=(MainCategoryBean)model.getAttribute("mcate");
		mcate = ((MainCategoryBean)mr.backController2(serviceCode, mcate));
	}
}