package icia.js.lostandfound;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import icia.js.lostandfound.beans.CommentsBean;
import icia.js.lostandfound.beans.FileBean;
import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.LostCommentsBean;
import icia.js.lostandfound.beans.MainCategoryBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.services.auth.Authentication;
import icia.js.lostandfound.services.managergetapi.ManagerGetApi;
import icia.js.lostandfound.services.managerprofile.MgrProfile;
import icia.js.lostandfound.services.mgr.catemanage.Catemanage;
import icia.js.lostandfound.services.mgr.postmanage.FoundManage;
import icia.js.lostandfound.services.mgr.postmanage.LostManage;
import icia.js.lostandfound.services.mgr.statistics.Statistics;
import icia.js.lostandfound.services.reg.Registration;
import icia.js.lostandfound.services.user.found.Found;
import icia.js.lostandfound.services.user.lost.Lost;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
public class APIController {
	@Autowired
	private Authentication auth;
	@Autowired
	private ManagerGetApi mgrgetapi;
	@Autowired
	private Registration reg;
	@Autowired
	private Found fa;
	@Autowired
	private Lost la;
	@Autowired
	private LostManage lm;
	@Autowired
	private FoundManage fm;
	@Autowired
	private ProjectUtils util;
	@Autowired
	private Catemanage cm;
	@Autowired
	private Statistics st;
	@Autowired
	private MgrProfile mp;
	
	@GetMapping("/test") 
	public void test(Model model, @ModelAttribute ItemsBean items) {
    }
	/*login*/
	@PostMapping(value="/IssuanceJWT")
	public String issuanceJWT(Model model, @ModelAttribute MemberBean member) {
		model.addAttribute("member",member);
		auth.backController("A07", model);
		return "{\"result\":\"success\"}";
	}
	
	/*mgr*/
	@PostMapping("/Api/updateMgr")
	public MemberBean updateMgr(Model model, @ModelAttribute MemberBean member) {
		model.addAttribute("member",member);
		mp.backController("M30", model);
		return (MemberBean)model.getAttribute("member");
	} 
	@PostMapping("/Api/delFoundComment")
	public FoundCommentsBean delFoundComment(Model model, @ModelAttribute FoundCommentsBean foundcomment) {
		model.addAttribute("foundcomment",foundcomment);
		fm.backController("M34", model);
		return (FoundCommentsBean)model.getAttribute("foundcomment");
	} 		
	@PostMapping("/Api/delLostComment")
	public LostCommentsBean delLostComment(Model model, @ModelAttribute LostCommentsBean lostcomment) {
		System.out.println(lostcomment.getLcoCode());
		model.addAttribute("lostcomment",lostcomment);
		lm.backController("M35", model);
		return (LostCommentsBean)model.getAttribute("lostcomment");
	} 		
	@PostMapping("/Api/getMgrComments")
	public CommentsBean getMgrComments(Model model, @ModelAttribute CommentsBean comment) {
		model.addAttribute("comment",comment);
		CommentsBean cb = new CommentsBean();
		cb.setLcList(((CommentsBean)lm.backController("M32", model)).getLcList());
		cb.setFcList(((CommentsBean)fm.backController("M33", model)).getFcList());
		return cb;
	} 
	@PostMapping("/Api/getMgrProfile")
	public MemberBean getMgrProfile(Model model, @ModelAttribute MemberBean member) {
		model.addAttribute("member",member);
		mp.backController("M29", model);
		return (MemberBean)model.getAttribute("member");
	}
	@PostMapping("/Api/getMgrsCate")
	public MainCategoryBean getMgrsCate(Model model, @ModelAttribute MainCategoryBean items) {
		model.addAttribute("items", items);
		fa.backController("U47", model);
		return (MainCategoryBean)model.getAttribute("items");
    }
	@PostMapping("/Api/delAlarmRow") 
	public ItemsBean delAlarmRow(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U46", model);
		return getBannerData(model,items);
    }
	@PostMapping("/Api/getStatDay") 
	public ItemsBean getStatDay(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		st.backController("M03", model);
		return (ItemsBean)model.getAttribute("items");
    }
	@PostMapping("/Api/getStatWeek") 
	public ItemsBean getStatWeek(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		st.backController("M02", model);
		return (ItemsBean)model.getAttribute("items");
    }
	@PostMapping("/Api/getStat") 
	public ItemsBean getStat(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		st.backController("M01", model);
		return (ItemsBean)model.getAttribute("items");
    }
	@PostMapping("/certificationMMS") 
	public String certificationMMS(HttpServletRequest req){
		String phone =req.getParameter("Phone");
		return reg.backController("U02",phone);
    }
	
	@PostMapping("/Api/updSubCategory")
	public MainCategoryBean updSubCategory(Model model,@ModelAttribute MainCategoryBean mcate) {
		model.addAttribute("mcate",mcate);
		System.out.println((MainCategoryBean)model.getAttribute("mcate")+"1");
		cm.backController("M19", model);
		System.out.println((MainCategoryBean)model.getAttribute("mcate")+"2");
		return (MainCategoryBean)model.getAttribute("mcate");
	}
	@PostMapping("/Api/addSubCategory")
	public MainCategoryBean addSubCategory(Model model,@ModelAttribute MainCategoryBean mcate) {
		model.addAttribute("mcate",mcate);
		cm.backController("M18", model);
		return (MainCategoryBean)model.getAttribute("mcate");
	}
	@PostMapping("/Api/delSubCategory")
	public MainCategoryBean delSubCategory(Model model,@ModelAttribute MainCategoryBean mcate) {
		model.addAttribute("mcate",mcate);
		cm.backController("M20", model);
		return (MainCategoryBean)model.getAttribute("mcate");
	}
	@PostMapping("/Api/getMainCategory")
	public MainCategoryBean getMainCategory(Model model,@ModelAttribute MainCategoryBean mcate) {
		model.addAttribute("mcate",mcate);
		cm.backController("M16", model);
		return (MainCategoryBean)model.getAttribute("mcate");
	}
	@PostMapping("/Api/getSubCategory")
	public MainCategoryBean getSubCategory(Model model,@ModelAttribute MainCategoryBean mcate) {
		System.out.println(model);
		model.addAttribute("mcate",mcate);
		cm.backController("M17", model);
		return (MainCategoryBean)model.getAttribute("mcate");
	}
	@PostMapping("/Api/lostComment")
	public LostArticleBean lostcomment(Model model, @ModelAttribute LostCommentsBean lostcomment) {
		model.addAttribute("lostcomment", lostcomment);
		System.out.println("APIcontroller=>" + lostcomment.getLcoCode());
		System.out.println("APIcontroller=>" + lostcomment.getLcoComment());
		System.out.println("APIcontroller=>" + lostcomment.getLcoMmId());
		la.backController("U11", model);
		
		return (LostArticleBean)model.getAttribute("lostcomment");
	}
	@PostMapping("/Api/foundComment")
	public FoundArticleBean foundComment(Model model,@ModelAttribute FoundCommentsBean foundcomment) {
		model.addAttribute("foundcomment",foundcomment);
		System.out.println("APIcontroller=>" + foundcomment.getFcoCode());
		System.out.println("APIcontroller=>" + foundcomment.getFcoComment());
		System.out.println("APIcontroller=>" + foundcomment.getFcoMmId());
		fa.backController("U12",model);
		return (FoundArticleBean)model.getAttribute("foundcomment");
	}
	@PostMapping("/Api/modMyFound")
	public ItemsBean modMyFound(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U23", model);
		fa.backController("U27", model);
		return (ItemsBean)model.getAttribute("items");
	}		
	@PostMapping("/Api/delLost")
	public ItemsBean delLost(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		lm.backController("M13", model);
		lm.backController("M12", model);
		return (ItemsBean)model.getAttribute("items");
	} 		
	@PostMapping("/Api/modMyLost")
	public ItemsBean modMyLost(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U40", model);
		la.backController("U28", model);
		return (ItemsBean)model.getAttribute("items");
	}	
	@PostMapping("/Api/getMgrLost")
	public ItemsBean getMgrLost(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		lm.backController("M12", model);
		return (ItemsBean)model.getAttribute("items");
	} 	
	@PostMapping("/Api/delFound")
	public ItemsBean delFound(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fm.backController("M15", model);
		fm.backController("M14", model);
		return (ItemsBean)model.getAttribute("items");
	} 		
	@PostMapping("/Api/getMgrFound")
	public ItemsBean getMgrFound(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fm.backController("M14", model);
		return (ItemsBean)model.getAttribute("items");
	} 	
    @PostMapping("/Api/delMyComment")
	public ItemsBean delMyComment(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U39", model);
		la.backController("U38", model);
		return (ItemsBean)model.getAttribute("items");
	} 
	@PostMapping("/Api/delMyFound")
	public ItemsBean delMyFound(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U25", model);
		fa.backController("U27", model);
		return (ItemsBean)model.getAttribute("items");
	}  
	@PostMapping("/Api/delMyLost")
	public ItemsBean delMyLost(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U35", model);
		la.backController("U28", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/modUserInfo")
	public ItemsBean modUserInfo(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U30", model);
		return (ItemsBean)model.getAttribute("items");
	}
    @PostMapping("/Api/getMyComment")
	public ItemsBean getMyComment(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U38", model);
		return (ItemsBean)model.getAttribute("items");
	} 
	@PostMapping("/Api/getMyFound")
	public ItemsBean getMyFound(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U27", model);
		return (ItemsBean)model.getAttribute("items");
	} 
    @PostMapping("/Api/getMyLost")
	public ItemsBean getMyLost(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U28", model);
		return (ItemsBean)model.getAttribute("items");
	} 
	@PostMapping("/Api/getBannerData")
	public ItemsBean getBannerData(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		try {
			items.setMember(((MemberBean)this.util.getAttribute("AccessInfo")));
		}catch(Exception e){
			log.info("{}","get session holder fail");
		}
		if(items.getServiceCode()!= null && items.getServiceCode().equals("U23")) 
		{fa.backController("U23", model);fa.backController("U27", model);}
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U28")) 
			la.backController("U28", model);
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U25"))
		{fa.backController("U25", model);fa.backController("U27", model);}
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U27")) 
			fa.backController("U27", model);
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U30")) 
			auth.backController("U30", model);
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U32")) 
			auth.backController("U32", model);
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U35")) 
		{la.backController("U35", model);la.backController("U28", model);}
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U38")) 
			la.backController("U38", model);
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U39"))
		{la.backController("U39", model);la.backController("U38", model);}
		else if(items.getServiceCode()!= null && items.getServiceCode().equals("U40"))
		{la.backController("U40", model);la.backController("U28", model);}
		
		fa.backController("U44", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/ImageSearchFF")
	public ItemsBean ImageSearchFF(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U24-F", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/ImageSearchF")
	public ItemsBean ImageSearchF(@RequestParam("file") MultipartFile file,Model model, @ModelAttribute ItemsBean items) {
		items.setFileInfo(new FileBean(file));
		model.addAttribute("items", items);
		fa.backController("U24", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/ImageSearchLL")
	public ItemsBean ImageSearchLL(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U13-L", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/ImageSearchL")
	public ItemsBean ImageSearchL(@RequestParam("file") MultipartFile file,Model model, @ModelAttribute ItemsBean items) {
		items.setFileInfo(new FileBean(file));
		model.addAttribute("items", items);
		la.backController("U13", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/userLostBefore")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void userLostBefore(@RequestParam("file") MultipartFile file,Model model, @ModelAttribute ItemsBean items) {
		items.setFileInfo(new FileBean(file));
		model.addAttribute("items", items);
		la.backController("U07-B", model);
	}
	@PostMapping("/Api/userFoundBefore")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void userFoundBefore(@RequestParam("file") MultipartFile file,Model model, @ModelAttribute ItemsBean items) {
		items.setFileInfo(new FileBean(file));
		model.addAttribute("items", items);
		fa.backController("U17-B", model);
	}
	@PostMapping("/Api/userFoundRegister")
	public ItemsBean userFoundRegister(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U17", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/userLostRegister")
	public ItemsBean userLostRegister(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U07", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/lostMatching")
	public ItemsBean lostMatching(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U33", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/foundMatching")
	public ItemsBean foundMatching(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U34", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/getUserLostByPageNo")
	public ItemsBean getUserLostByPageNo(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U03", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/getLostDetailSearch")
	public ItemsBean getLostDetailSearch(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		la.backController("U09",model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/getUserFoundByPageNo")
	public ItemsBean getUserFoundByPageNo(Model model, @ModelAttribute ItemsBean items
			,HttpServletRequest req) {
		String jwtToken =  req.getHeader("JWTForJSFramework");
		String test =req.getParameter("totalCount");
		model.addAttribute("items", items);
		fa.backController("U16", model);
		return (ItemsBean)model.getAttribute("items");
	}
	@PostMapping("/Api/getFoundDetailSearch")
	public ItemsBean getFoundDetailSearch(Model model, @ModelAttribute ItemsBean items) {
		model.addAttribute("items", items);
		fa.backController("U26",model);
		return (ItemsBean)model.getAttribute("items");
	}
	
//	@PostMapping("/Api/getMgrFoundByPageNo")
//	public ItemsBean getMgrFoundByPageNo(Model model, @ModelAttribute ItemsBean items) {
//		model.addAttribute("items", items);
//		fm.backController(10, model);
//		return (ItemsBean)model.getAttribute("items");
//	}
//	@PostMapping("/Api/getMgrLostByPageNo")
//	public ItemsBean getMgrLostByPageNo(Model model, @ModelAttribute ItemsBean items) {
//		model.addAttribute("items", items);
//		lm.backController(0, model);
//		
//		return (ItemsBean)model.getAttribute("items");
//	}
	
	@GetMapping("/getLostDataPolice")
	public void getLostDataPolice(){
		mgrgetapi.backController("M7");
    }
	@GetMapping("/getFoundDataPolice")
	public void getFoundDataPolice(){
		mgrgetapi.backController("M8");
    }
	@GetMapping("/getFoundDataPolicePortal")
	public void getFoundDataPolicePortal(){
		mgrgetapi.backController("M9");
    }
	@PostMapping("/Api/NaverLogin")
	public MemberBean insNaverLogin(Model model, @ModelAttribute MemberBean member) {
		model.addAttribute("member", member);
		auth.backController("A04",model);
		return ((MemberBean)model.getAttribute("member"));
	}
	@PostMapping("/Api/GoogleLogin")
	public MemberBean insGoogleLogin(Model model, @ModelAttribute MemberBean member) {
		System.out.println(member.getMmId());
		model.addAttribute("member", member);
		auth.backController("A05",model);
		return ((MemberBean)model.getAttribute("member"));
	}
//	@GetMapping("/getFoundDataSeoul")
//	public void getFoundDataSeoul(){
//		//mgrgetapi.backController("M35");
//    }	
}
