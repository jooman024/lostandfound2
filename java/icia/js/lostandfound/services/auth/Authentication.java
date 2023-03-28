package icia.js.lostandfound.services.auth;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import icia.js.lostandfound.JsonWebTokenService;
import icia.js.lostandfound.SimpleTransactionManager;
import icia.js.lostandfound.TransactionAssistant;
import icia.js.lostandfound.beans.AccessLogBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.JWTBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.repository.userrepo.UserRepository;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Authentication extends TransactionAssistant  {
	@Autowired
	UserRepository urepo;
	@Autowired
	private ProjectUtils util;
	
	
	private SimpleTransactionManager tranManager;
	public Authentication(){
		
	}
	public void backController(String serviceCode,Model model) {
		switch(serviceCode) {
		case "U30":
			modUserInfo(serviceCode, model);
			break;
		case "U32":
			modUserInfo2(serviceCode, model);
			break;
		case "A07":
			this.issuanceJWTService(serviceCode,model);
			break;
		}
	}
	public ModelAndView backController(String serviceCode,ModelAndView mav) {
		ModelAndView m=null;
		switch(serviceCode) {
		case "A06-g":
			m=this.selNewMemberGoogle(serviceCode, mav);
			break;
		case "A06":
			m=this.selNewMember(serviceCode, mav);
			break;
		case "A06-k":
			m=this.selNewMemberKakao(serviceCode, mav);
			break;
		case "A03":
			m=this.insKakaoMemberBean(serviceCode, mav);
			break;
		case "A04":
			m=this.insNaverMemberBean(serviceCode, mav);
			break;
		case "A05":
			m=this.insGoogleMemberBean(serviceCode, mav);
			break;
		}
		return m;
	}
	public String backController(String serviceCode) {
		String page=null;
		switch(serviceCode) {
		case "A02":
			this.logOut(serviceCode);
			break;
		}
		return page;
	}
	private void logOut(String serviceCode) {
		try {
			MemberBean member=((MemberBean)this.util.getAttribute("AccessInfo"));
			member.getAcList().get(0).setAcType(-1);
			urepo.backController(serviceCode, member);
			this.util.removeAttribute("AccessInfo");
			log.info("{}","logOutEnd");
		}catch(Exception e) {
			log.info("{}","get or remove acInfo error");
		}
	}
	private void issuanceJWTService(String serviceCode,Model model) {
		MemberBean member=(MemberBean)model.getAttribute("member");
		this.util.issuanceJWT(member);
	}
	private ModelAndView selNewMember(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
		String page = "addInfo";
		
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		if(member != null) {
			if(member.getMmPhone() != null) {
				try {
					this.util.setAttribute("AccessInfo", member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
			}else {
				member.setMmSns("UN");
			}
		}
		mav.addObject("member", new Gson().toJson(member));
		mav.setViewName(page);
		return mav;
	}
	private ModelAndView selNewMemberGoogle(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
		String page = "addInfo";
		
		serviceCode = "A06";
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		
		if(member != null) {
			if(member.getMmPhone() != null) {
				try {
					this.util.setAttribute("AccessInfo", member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
			}else {
				member.setMmSns("UG");
			}
		}
		mav.addObject("member", new Gson().toJson(member));
		mav.setViewName(page);
		return mav;
	}
	private ModelAndView selNewMemberKakao(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
		String page = "addInfo";
		
		serviceCode = "A06";
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		
		if(member != null) {
			if(member.getMmPhone() != null) {
				try {
					this.util.setAttribute("AccessInfo", member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
			}else {
				member.setMmSns("UK");
			}
		}
		mav.addObject("member", new Gson().toJson(member));
		mav.setViewName(page);
		return mav;
	}
	
	private ModelAndView insNaverMemberBean(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
	
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcType(3);
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		if(member.getMmLimit() == null) {
			mav.setViewName("beforeAuth");
			
		}else {
			if(member != null) {
				try {
					this.util.setAttribute("AccessInfo", member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
				mav.addObject("member", new Gson().toJson(member));
			}
			mav.setViewName("addInfo");			
		}
		return mav;
	}
	private ModelAndView insKakaoMemberBean(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
		
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcType(3);
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		
		if(member.getMmLimit() == null) {
			mav.setViewName("beforeAuth");
			
		}else {
			if(member != null) {
				try {
					this.util.setAttribute("AccessInfo", member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
				mav.addObject("member", new Gson().toJson(member));
			}
			mav.setViewName("addInfo");			
		}
		
		return mav;
	}
	private ModelAndView insGoogleMemberBean(String serviceCode, ModelAndView mav) {
		MemberBean member = (MemberBean)mav.getModel().get("member");
		AccessLogBean ac = new AccessLogBean();
		ac.setAcUserId(member.getMmId());
		ac.setAcType(3);
		ac.setAcPrivateIp(util.getHeaderInfo(true));
		ac.setAcBroswer(util.getBrowserInfo(util.getHeaderInfo(false)));
		ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
		acList.add(ac);
		member.setAcList(acList);
		member =((MemberBean)urepo.backController(serviceCode, member));
		
		if(member.getMmLimit() == null) {
			mav.setViewName("beforeAuth");
			
		}else {
			if(member != null) {
				try {
					this.util.setAttribute("AccessInfo",  member);
				}catch (Exception e) {
					log.info("{}","setting acInfo error");
				}
				mav.addObject("member", new Gson().toJson(member));
			}
			mav.setViewName("addInfo");			
		}
		
		return mav;
	}
	private void modUserInfo(String serviceCode, Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items =((ItemsBean)urepo.backController(serviceCode,items));
		
		try {
			if(items!=null) {
				MemberBean acInfo=((MemberBean)this.util.getAttribute("AccessInfo"));
				acInfo.setMmPhone(items.getMember().getMmPhone());
				this.util.setAttribute("AccessInfo", acInfo);
				this.util.issuanceJWT(acInfo);
			}
		}catch(Exception e) {
			log.info("{}","get acInfo error");
		}
	}
	private void modUserInfo2(String serviceCode, Model model) {
		ItemsBean items=(ItemsBean)model.getAttribute("items");
		items =((ItemsBean)urepo.backController(serviceCode,items));
		
		try {
			if(items!=null) {
				MemberBean acInfo=((MemberBean)this.util.getAttribute("AccessInfo"));
				acInfo.setMmName(items.getMember().getMmName());
				this.util.setAttribute("AccessInfo", acInfo);
				this.util.issuanceJWT(acInfo);
			}
		}catch(Exception e) {
			log.info("{}","get acInfo error");
		}
	}
}
