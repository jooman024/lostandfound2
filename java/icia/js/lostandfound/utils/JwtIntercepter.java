package icia.js.lostandfound.utils;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import icia.js.lostandfound.JsonWebTokenService;
import icia.js.lostandfound.beans.AccessLogBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.PageVisitBean;
import icia.js.lostandfound.repository.userrepo.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtIntercepter implements HandlerInterceptor{
	@Autowired
	private JsonWebTokenService jwtService;
	@Autowired
	private ProjectUtils holder;
	@Autowired
	private UserRepository urepo;

	@Override
	public boolean preHandle(HttpServletRequest req,HttpServletResponse res,
			Object handler) throws Exception{
	
		String userKey=null;
		boolean result=false;
		long remainingTime = 0L;
		
		if(!req.getMethod().equals("OPTIONS")) { 
			if(!req.getMethod().equals("GET")) {
				if(((MemberBean)this.holder.getAttribute("AccessInfo")) != null) {
					MemberBean member=((MemberBean)this.holder.getAttribute("AccessInfo"));
					userKey = member.getMmId();
					log.info("{}","req URI : "+req.getRequestURI());
					String[] uriInfo=req.getRequestURI().split("\\/");
					String jwtToken = uriInfo[1].equals("View")? 
					req.getParameter("JWTForJSFramework") : req.getHeader("JWTForJSFramework");
					if(jwtToken != null	&& jwtToken.length() > 0) {
						Claims claims =this.jwtService.getTokenInfo(jwtToken, userKey);
						if((result=((remainingTime=this.jwtService.getTokenRemainingTime(claims))!=0)
								&& this.jwtService.tokenIsValid(claims, member)))
						{
							log.info("{}","Token Valid :"+remainingTime);
							if(!uriInfo[1].equals("View")&&remainingTime<(600000L)) {
								AccessLogBean ac = new AccessLogBean();
								ac.setAcUserId(member.getMmId());
								ac.setAcType(2);
								ac.setAcPrivateIp(this.holder.getHeaderInfo(true));
								ac.setAcBroswer(this.holder.getBrowserInfo(this.holder.getHeaderInfo(false)));
								ArrayList<AccessLogBean> acList = new ArrayList<AccessLogBean>();
								acList.add(ac);
								member.setAcList(acList);
								if((result=((int)urepo.backController("A09", member)>0)?true:false)) {
									this.holder.issuanceJWT(member);
									log.info("{}","Token Refresh sucess");
								}
							}
							if(this.holder.isMyPage(uriInfo[2])) {
								PageVisitBean pv= new PageVisitBean();
								pv.setPvAcId(member.getAcList().get(0).getAcId());
								pv.setPvPageName(uriInfo[2]);
								if((result=((int)urepo.backController("A08", pv)>0)?true:false)) {
									log.info("{}","Token Visit sucess");
								}else {
									log.info("{}",pv.getPvAcId()+pv.getPvPageName());
								}
							}
						}
					}else log.info("{}","Token Error:Token does Not Exist or Token has Expired");
				}else log.info("{}","Session Error:error");
			}else log.info("{}","get method:error");
		}else result = true;
		if(!result || userKey == null) {
			log.info("{}","token not exist or expired or error -> redirect main");
			res.sendRedirect("/Index?");
		}
		return result;
	}
	@Override
	public void postHandle( HttpServletRequest req, HttpServletResponse res,
			Object handler, ModelAndView mav)throws Exception { // Before View Rendering
		//log.info(">>> postHandle <<< ");
//		MemberBean member = ((MemberBean)this.holder.getAttribute("AccessInfo"));
//		if(member.getMmSns().contains("U")) {
//			res.setContentType("application/json;charset=UTF-8");
//			String json = new ObjectMapper().writeValueAsString(api.getBannerData(new ItemsBean()));
//	        String callback = req.getParameter("callback"); // 콜백 함수 이름 가져오기
//	        if (callback != null && !callback.isEmpty()) { // 콜백 함수가 지정된 경우
//	            json = callback + "(" + json + ")"; // 콜백 함수 호출 코드 생성
//	            res.setContentType("application/javascript;charset=UTF-8"); // MIME 타입 변경
//	        }
//	        res.getWriter().write(json); // 응답 데이터 전송
//		}
				
	}	

	@Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse res, 
			Object handler, Exception ex)throws Exception { // After View Rendering
		//log.info(">>> afterComplection <<<");
	}
	public void afterConcurrentHandlingStarted(HttpServletRequest req, HttpServletResponse res, 
			Object handler, Exception ex) {
	}

}
