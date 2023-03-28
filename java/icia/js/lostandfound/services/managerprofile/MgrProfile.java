package icia.js.lostandfound.services.managerprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import icia.js.lostandfound.TransactionAssistant;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;
import icia.js.lostandfound.services.auth.Authentication;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MgrProfile extends TransactionAssistant {
	@Autowired
	ManagerRepository mgr;
	@Autowired
	private ProjectUtils util;
	
	public void backController(String serviceCode,Model model) {
		switch(serviceCode) {
		case "M29":
			getAllMemberData(serviceCode, model);
			break;
		case "M30":
			updateMemberData(serviceCode, model);
			break;
		}
	}
	
	private void getAllMemberData(String serviceCode , Model model) {
		MemberBean member=(MemberBean)model.getAttribute("member");
		member = ((MemberBean)mgr.backController(serviceCode, member));
	}
	private void updateMemberData(String serviceCode , Model model) {
		MemberBean member=(MemberBean)model.getAttribute("member");
		try {
			MemberBean acInfo=((MemberBean)this.util.getAttribute("AccessInfo"));
			if(member.getMmSns().equals("UN") || member.getMmSns().equals("UK") || member.getMmSns().equals("UG")) {
				member.setMmSns(member.getMmSns().replace("U", "M"));
				member = ((MemberBean)mgr.backController(serviceCode, member));
			}else if(member.getMmSns().equals("MN") || member.getMmSns().equals("MK") || member.getMmSns().equals("MG")) {
				member.setMmSns(member.getMmSns().replace("M", "U"));
				member = ((MemberBean)mgr.backController(serviceCode, member));
			}
			acInfo.setMmSns(member.getMmSns());
			this.util.setAttribute("AccessInfo", acInfo);
			this.util.issuanceJWT(acInfo);
		}catch(Exception e) {
			log.info("{}","get acInfo error");
		}
		
		
	}
}
