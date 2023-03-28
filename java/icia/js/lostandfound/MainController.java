package icia.js.lostandfound;



import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.MatchingAlarmBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.services.auth.Authentication;
import icia.js.lostandfound.services.reg.Registration;
import icia.js.lostandfound.services.user.found.Found;
import icia.js.lostandfound.services.user.lost.Lost;
@Controller
@RequestMapping(value="/",method = RequestMethod.GET)
public class MainController{
	@Autowired
	private Lost la;
	@Autowired
	private Found fa;
	@Autowired
	private Registration reg;
	@Autowired
	private Authentication auth;
	@Value("${NaverClientId}")
	private String NaverClientId;
	@Value("${NaverClientPw}")
	private String NaverClientPw;
	
	public MainController() {
	}
	@RequestMapping(value="/View/MgrComments",method = RequestMethod.POST)
	public String moveMgrComments() {
		return "mgrcomments";
	}
	@RequestMapping(value = "/NaverLogout",method = RequestMethod.POST)
	public String insNaverLoginCheck(HttpServletRequest req) {
		String page="user";
		try
		{
			URL url =new URL("https://nid.naver.com/oauth2.0/token?grant_type=delete&"
					+ "client_id="+NaverClientId+
					"&client_secret="+NaverClientPw
					+ "&access_token=" 
								+ req.getParameter("na") + "&service_provider=NAVER");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
	        System.out.println("Response code: " + conn.getResponseCode());
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	        	auth.backController("A02");
	        	page="beforeAuth";
	        } else {
	        	System.out.println("logout error");
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return page;
	}
	@RequestMapping(value="/NaverLoginCheck",method = RequestMethod.POST)
	public ModelAndView insNaverLoginCheck(ModelAndView mav, @ModelAttribute MemberBean member) {
		mav.addObject("member", member);		
		mav = auth.backController("A06", mav);
		return mav;
	}
	@RequestMapping(value="/AuthStationNaver",method = RequestMethod.POST)
	public ModelAndView insNaverLogin(ModelAndView mav, @ModelAttribute MemberBean member) {
		mav.addObject("member", member);		
		mav = auth.backController("A04", mav);
		return mav;
	}
	@RequestMapping(value="/AddInfo",method = RequestMethod.POST)
	public ModelAndView moveAddInfo(HttpMethod httpMethod) {
		ModelAndView mav = reg.backController("A06");
		return mav;
	}
	@RequestMapping(value="/View/ALERTMSG",method = RequestMethod.POST)
	public ModelAndView alertMSG(ModelAndView mav,@ModelAttribute ItemsBean item) {
		mav.addObject("item", item);
		mav.setViewName("user");
		fa.backController("U43",mav);
		return mav;
	}
	@RequestMapping(value="/View/UserSCenter",method = RequestMethod.POST)
	public String moveUserSCenter(HttpMethod httpMethod) {
		String page=null;
		page="userscenter";
		return page;
	}
	@RequestMapping(value="/View/UserMyPage", method = RequestMethod.POST)
	public String moveUserMyPage() {
	    return "usermypage";
	}
	@RequestMapping(value="/View/UserMyLost", method = RequestMethod.POST)
	public String UserMyLost() {
	    return "usermylost";
	}
	@RequestMapping(value="/View/UserMyFound", method = RequestMethod.POST)
	public String UserMyFound() {
	    return "usermyfound";
	}
	@RequestMapping(value="/View/UserMyComment",method = RequestMethod.POST)
	public String UserMyComment() {
	    return "usermycomment";
	}
	@RequestMapping(value="/View/UserLostReg",method = RequestMethod.POST)
	public String moveUserLostReg(HttpMethod httpMethod) {
		String page=null;
		page="userlostreg";
		return page;
	}
	@RequestMapping(value="/View/UserFoundReg",method = RequestMethod.POST)
	public String moveUserFoundReg(HttpMethod httpMethod) {
		String page=null;
		page="userfoundreg";
		return page;
	}
	@RequestMapping(value="/View/MgrProfile",method = RequestMethod.POST)
	public String moveMgrProfile(HttpMethod httpMethod) {
		String page=null;
		page="mgrprofile";
		return page;
	}
	@RequestMapping(value="/View/MgrCate",method = RequestMethod.POST)
	public ModelAndView moveMgrCate(@RequestParam(name="clientData",required=false) String clientData) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("mgrcate");
		mav.addObject("serverData", clientData);
		return mav;
	}
	@RequestMapping(value="/View/Mgr",method = RequestMethod.POST)
	public String moveMgr(HttpMethod httpMethod) {
		String page=null;
		page="mgr";
		return page;
	}
	@RequestMapping(value="/View/User",method = RequestMethod.POST)
	public String moveSales(HttpMethod httpMethod) {
		String page=null;
		page="user";
		return page;
	}
	@RequestMapping(value="/AccessOut",method = RequestMethod.POST)
	public void loout() {	
	}
	@RequestMapping(value="/View/MgrLost",method = RequestMethod.POST)
	public String moveMgrLost() {
		return "mgrlost";
	}
	@RequestMapping(value="/View/MgrFound",method = RequestMethod.POST)
	public String moveMgrFound() {
		return "mgrfound";
	}
	@RequestMapping(value="/View/UserFound",method = RequestMethod.POST)
	public ModelAndView moveUserFound() {
		ModelAndView mav = fa.backController("U16");
		return mav;
	}
	@RequestMapping(value="/View/UserLost",method = RequestMethod.POST)
	public ModelAndView moveUserLost() {
		ModelAndView mav = la.backController("U03");
		return mav;
	}
	@RequestMapping(value ="/", method = {RequestMethod.GET, RequestMethod.POST})
	public String index() {
		return "beforeAuth";
	}
	@RequestMapping(value ="/Index", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("redirect:/");
		//mav.getModel().put("message", message);
		return mav;
	}
}
