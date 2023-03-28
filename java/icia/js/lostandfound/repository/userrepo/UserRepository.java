package icia.js.lostandfound.repository.userrepo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import icia.js.lostandfound.SimpleTransactionManager;
import icia.js.lostandfound.TransactionAssistant;
import icia.js.lostandfound.beans.AccessLogBean;
import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.FoundArticleImages;
import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.LostArticleImages;
import icia.js.lostandfound.beans.LostCommentsBean;
import icia.js.lostandfound.beans.MainCategoryBean;
import icia.js.lostandfound.beans.MatchingAlarmBean;
import icia.js.lostandfound.beans.MatchingBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.MonthBean;
import icia.js.lostandfound.beans.PageVisitBean;
import icia.js.lostandfound.beans.SubCategoryBean;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserRepository extends TransactionAssistant  {
	
	private SimpleTransactionManager tranManager;
	
	public UserRepository() {}
	final public Object backController(String serviceCode, Object obj) {
		Object object=null;
		switch(serviceCode) {
		case "C01":
			object= getTotalLostNum(obj);
			break;
		case "C02":
			object= getTotalFoundNum(obj);
			break;
		case "C03":
			object= getTotalFoundSearchNum(obj);
			break;
		case "C04":
			object= getTotalLostSearchNum(obj);
			break;
		case "C05":
			object= getTotalFoundImgSearchNum(obj);
			break;
		case "C06":
			object= getTotalLostImgSearchNum(obj);
			break;
		case "U03":
			object = getLostTotal(obj);
			break;
		case "U07-B":
			userLostBefore(obj);
			break;
		case "U07-S":
			object = userLostBeforeSelect(obj);
			break;
		case "U07":
			object = userLostRegister(obj);
			break;
		case "U09":
			getLostDetailSearch(obj);
			break;
		case "U11":
			object = lostComment(obj);
			break;
		case "U12":
			object = foundComment(obj);
			break;
		case "U13":
			ImageSearchL(obj);
			break;
		case "U16":
			object = getFoundTotal(obj);
			break;
		case "U17-B":
			userFoundBefore(obj);
			break;
		case "U17-S":
			object = userFoundBeforeSelect(obj);
			break;
		case "U17":
			object = userFoundRegister(obj);
			break;
		case "U23":
			modMyFound(obj);
			break;
		case "U24":
			ImageSearchF(obj);
			break;
		case "U25":
			delMyFound(obj);
			break;
		case "U26":
			getFoundDetailSearch(obj);
			break;
		case "U27":
			getMyFound(obj);
			break;
		case "U28":
			getMyLost(obj);
			break;
		case "U30":
			object =modUserInfo(serviceCode,obj);
			break;
		case "U32":
			object =modUserInfo(serviceCode,obj);
			break;
		case "U33":
			lostMatching(obj);
			break;
		case "U34":
			foundMatching(obj);
			break;
		case "U35":
			delMyLost(obj);
			break;
		case "U38":
			getMyComment(obj);
			break;
		case "U39":
			delMyComment(obj);
			break;
		case "U40":
			object =modMyLost(obj);
			break;
		case "U43":
			alertMSG(obj);
			break;
		case "U44":
			object =getBannerData(obj);
			break;
		case "U45":
			object =getAlarmList(obj);
			break;
		case "U46":
			object =delAlarmRow(obj);
			break;
		case "U47":
			object =getMgrsCate(obj);
			break;
		case "A02":
			object = insLogout(obj);
			break;
		case "A03":
			object = insMember(obj);
			break;
		case "A04":
			object = insMember(obj);
			break;
		case "A05":
			object = insMember(obj);
			break;
		case "A06":
			object = selMember(obj);
			break;
		case "A08":
			object = insVisitLog(obj);
			break;
		case "A09":
			object = insRefreshLog(obj);
			break;
		}
		return object;
	}
	final synchronized int insRefreshLog(Object obj){
		int result=0;
		MemberBean member = ((MemberBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
				
			if (this.convertToBoolean(result=(int)this.session.insert("insAccessLog", member))) {
				log.info("{}", "insRefreshLog insert success");
				this.tranManager.commit();
			}
		} catch (Exception e) {
			log.info("{}", "insRefreshLog insert fail");
		} finally {
			this.tranManager.tranEnd();
		}
		return result;
	}
	final synchronized int insLogout(Object obj) {
		int result=0;
		MemberBean member = ((MemberBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
				
			if (this.convertToBoolean(result=(int)this.session.insert("insAccessLog", member))) {
				log.info("{}", "insLogout insert success");
				this.tranManager.commit();
			}
		} catch (Exception e) {
			log.info("{}", "insLogout insert fail");
		} finally {
			this.tranManager.tranEnd();
		}
		return result;
	}
	final synchronized int insVisitLog(Object obj) {
		int result=0;
		PageVisitBean pv =((PageVisitBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
				
			if (this.convertToBoolean(result=(int)this.session.insert("insVisitLog", pv))) {
				log.info("{}", "insVisitLog insert success");
				this.tranManager.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("{}", "insVisitLog insert fail");
		} finally {
			this.tranManager.tranEnd();
		}
		return result;
	}
	final synchronized MemberBean selMember(Object obj) {
		MemberBean member = ((MemberBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (((int) this.session.selectOne("getMemberInfoCnt", member)) == 0) {
				log.info("{}", "memberinfo x");
			} else {
				log.info("{}", "memberinfo o");
				if (((int) this.session.selectOne("getMemberIsReturning", member)) > 0) {
					log.info("{}", "member is Noob");
					member.getAcList().get(0).setAcType(3);
				}else {
					log.info("{}", "member is Returning");
					member.getAcList().get(0).setAcType(1);
				}
				if (this.convertToBoolean((int) this.session.insert("insAccessLog", member))) {
					log.info("{}", "accesslog insert success");
					ArrayList<AccessLogBean> acList = member.getAcList();
					member=(MemberBean)this.session.selectOne("getMemberInfo", member);
					member.setAcList(acList);
					log.info("{}", "getMemberInfo success");
					this.tranManager.commit();
				}else {
					log.info("{}", "accesslog insert fail");
				}
				
			}
		} catch (Exception e) {
			log.info("{}", "select fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return member;
	}
	final synchronized LostArticleBean lostComment(Object obj) {
		LostCommentsBean lost = ((LostCommentsBean) obj);
		LostArticleBean la = new LostArticleBean();
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			System.out.println("UserRepository=>lost=" + lost);

			if (this.convertToBoolean((int) this.session.insert("insLostComment", lost))) {
				log.info("{}", "lostComment insert success");
				la.setLaCtNumber(lost.getLcoCode());
				List<LostCommentsBean> commentList = this.session.selectList("lostcommentList", la);
				la.setLaCommentList((ArrayList<LostCommentsBean>) commentList);
				System.out.println("UserRepos commentList" + commentList);
				this.tranManager.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.tranManager.tranEnd();
		}
		return la;
	}
	final synchronized FoundArticleBean foundComment(Object obj) {
		FoundCommentsBean found = ((FoundCommentsBean) obj);
		FoundArticleBean fa = new FoundArticleBean();

		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
				
			if (this.convertToBoolean((int) this.session.insert("insFoundComment", found))) {
				log.info("{}", "foundComment insert success");
				fa.setFaCtNumber(found.getFcoCode());
				List<FoundCommentsBean> foundcommentlist = this.session.selectList("foundcommentList", fa);
				fa.setFaCommentList((ArrayList<FoundCommentsBean>) foundcommentlist);
				this.tranManager.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.tranManager.tranEnd();
		}
		return fa;
	}
	final synchronized MemberBean insMember(Object obj) {
		MemberBean member = ((MemberBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (member.getMmId() != null) {
				if (this.convertToBoolean((int) this.session.insert("insMemberInfo", member))) {
					log.info("{}", "member insert success");
					if (this.convertToBoolean((int) this.session.insert("insAccessLog", member))) {
						log.info("{}", "accesslog insert success");
						this.tranManager.commit();
						member.setMmLimit("3");
					}else {
						log.info("{}", "accesslog insert fail");
					}
					
				} else {
					log.info("{}", "member insert fail");
				}
			}
		} catch (Exception e) {
			log.info("{}", "insert fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return member;
	}
	final public Object backController(int serviceCode, Object obj) {
		Object object=null;
		switch(serviceCode) {
		case 1:
			object = getLostTotal(obj);
			break;
		}
		return object;
	}
	final synchronized ItemsBean modMyFound(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.update("modMyFound", items))) {
				log.info("{}","modMyFound success");
				this.tranManager.commit();
			}else {
				log.info("{}","modMyFound fail");
			}
		}catch(Exception e){
			log.info("{}","modMyFound fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean modMyLost(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.update("modMyLost", items))) {
				log.info("{}","modMyLost success");
				this.tranManager.commit();
			}else {
				log.info("{}","modMyLost fail");
			}
		}catch(Exception e){
			log.info("{}","modMyLost fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean delAlarmRow(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.delete("delAlarmRow", items))) {
				log.info("{}","delAlarmRow success");
				this.tranManager.commit();
			}else {
				log.info("{}","delAlarmRow fail");
			}
		}catch(Exception e){
			log.info("{}","delAlarmRow fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean delMyComment(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.delete(items.getTemp().contains("L")?"delMyCommentLost":"delMyCommentFound", items))) {
				log.info("{}","delMyComment success");
				this.tranManager.commit();
			}else {
				log.info("{}","delMyComment fail");
			}
		}catch(Exception e){
			log.info("{}","delMyComment fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean delMyFound(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			this.session.delete("delMyFoundComments", items);
			log.info("{}","delMyFoundComments success");
			if(this.convertToBoolean((int)this.session.delete("delMyFound", items))) {
				log.info("{}","delMyFound success");
				if(this.convertToBoolean((int)this.session.delete("delMyFoundImg", items))) {
					log.info("{}","delMyFoundImg success");
					this.tranManager.commit();
				}else {
					log.info("{}","delMyFoundImg fail");
				}
			}else {
				log.info("{}","delMyFound fail");
			}

		}catch(Exception e){
			log.info("{}","delMyFound fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean delMyLost(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			this.session.delete("delMyLostComments", items);
			log.info("{}","delMyLostComments success");
			if(this.convertToBoolean((int)this.session.delete("delMyLost", items))) {
				log.info("{}","delMyLost success");
				if(this.convertToBoolean((int)this.session.delete("delMyLostImg", items))) {
					log.info("{}","delMyLostImg success");
					this.tranManager.commit();
				}else {
					log.info("{}","delMyLostImg fail");
				}
			}else {
				log.info("{}","delMyLost fail");
			}
		}catch(Exception e){
			log.info("{}","delMyLost fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean modUserInfo(String serviceCode,Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			
			if(this.convertToBoolean((int)this.session.update((!serviceCode.equals("U30"))?
					"modUserName":"modUserInfo", items))) {
				log.info("{}","modUserInfo modify success");
				this.tranManager.commit();
			}else {
				log.info("{}","modUserInfo modify fail");
				items=null;
			}
		}catch(Exception e){
			log.info("{}","modUserInfo fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
			if(serviceCode.equals("U30"))
				items.getMember().setMmPhone(items.getTemp());
			else
				items.getMember().setMmName(items.getTemp());
		}
		return items;
	}
	final synchronized  MainCategoryBean getMgrsCate(Object obj){
		MainCategoryBean mb = ((MainCategoryBean)obj);
		List<SubCategoryBean> sbList = null;
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			sbList=this.session.selectList("getMgrsCate",mb);	
			sbList = this.session.selectList("getMgrsCate",mb);	
			if(sbList == null) {
			    log.error("{}","sbList is null");
			} else {
			    log.info("{}","getMgrsCate selet success");
			    mb.setSubCategoryList((ArrayList<SubCategoryBean>)sbList);
			}
		}catch(Exception e){
			log.info("{}","getMgrsCate fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return mb;
	}
	final synchronized MatchingBean getAlarmList(Object obj){
		MemberBean mb = ((MemberBean)obj);
		MatchingBean mat = new MatchingBean();
		List<MatchingAlarmBean> maList = null;
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			maList=this.session.selectList("getAlarmList",mb);	
			log.info("{}","getAlarmList selet success");
			mat.setAlarm((ArrayList<MatchingAlarmBean>)maList);
		}catch(Exception e){
			log.info("{}","getAlarmList fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return mat;
	}
	final synchronized ItemsBean getBannerData(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> BaList = null;
			BaList=this.session.selectList("getBannerData",items);	
			items.setBalist((ArrayList<FoundArticleBean>)BaList);
			log.info("{}","getBannerData selet success");
		}catch(Exception e){
			log.info("{}","getBannerData fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean ImageSearchL(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("ImageSearchL",items);	
			items.setLalist((ArrayList<LostArticleBean>)laList);
			items.setFalist(null);
			log.info("{}","ImageSearchL selet success");
		}catch(Exception e){
			log.info("{}","ImageSearchL fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean ImageSearchF(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			faList=this.session.selectList("ImageSearchF",items);	
			items.setFalist((ArrayList<FoundArticleBean>)faList);
			items.setLalist(null);
			log.info("{}","ImageSearchF selet success");
		}catch(Exception e){
			log.info("{}","ImageSearchF fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean alertMSG(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.insert("ALERTMSG", items.getMInfo()))) {
				log.info("{}","ALERTMSG insert success");
				this.tranManager.commit();
			}else {
				log.info("{}","ALERTMSG insert fail");
			}
		}catch(Exception e){
			log.info("{}","ALERTMSG fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized void userLostBefore(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.insert("userLostBefore", items))) {
				log.info("{}","temp insert success");
				this.tranManager.commit();
			}else {
				log.info("{}","temp insert fail");
			}
		}catch(Exception e){
			log.info("{}","temp insert fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized void userFoundBefore(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(this.convertToBoolean((int)this.session.insert("userFoundBefore", items))) {
				log.info("{}","temp insert success");
				this.tranManager.commit();
			}else {
				log.info("{}","temp insert fail");
			}
		}catch(Exception e){
			log.info("{}","temp insert fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized ItemsBean userLostBeforeSelect(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleImages> laList = null;
			laList=this.session.selectList("userLostBeforeSelect",items);	
			if(laList != null) {
				items.getLalist().get(0).setLaImgList((ArrayList<LostArticleImages>)laList);
				log.info("{}","userLostBeforeSelect sucess");
			}else {
				log.info("{}","userLostBeforeSelect fail");
			}
		}catch(Exception e){
			log.info("{}","userLostBeforeSelect fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean userFoundBeforeSelect(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleImages> faList = null;
			faList=this.session.selectList("userFoundBeforeSelect",items);	
			if(faList != null) {
				items.getFalist().get(0).setFaImgList((ArrayList<FoundArticleImages>)faList);
				log.info("{}","userFoundBeforeSelect sucess");
			}else {
				log.info("{}","userFoundBeforeSelect fail");
			}
		}catch(Exception e){
			log.info("{}","userFoundBeforeSelect fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean userFoundRegister(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(items.getFalist().get(0).getFaCtNumber()==null) {
				if(this.convertToBoolean((int)this.session.insert("userFoundRegister", items))) {
					log.info("{}","found insert success");
					
					this.tranManager.commit();
				}else {
					log.info("{}","found insert fail");
				}
			}else {
				if(this.convertToBoolean((int)this.session.insert("userFoundImgRegister", items.getFalist().get(0)))) {
					log.info("{}","found img insert success");
					if(this.convertToBoolean((int)this.session.delete("delImgFoundTemp",items))) {
						log.info("{}","delImgFoundTemp success");
						this.tranManager.commit();
					}
					else
						log.info("{}","delImgFoundTemp fail");
				}
				else
					log.info("{}","found img insert fail");
			}
		}catch(Exception e){
			log.info("{}","insert fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean userLostRegister(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if(items.getLalist().get(0).getLaCtNumber()==null) {
				if(this.convertToBoolean((int)this.session.insert("userLostRegister", items))) {
					log.info("{}","lost insert success");
					this.tranManager.commit();
				}else {
					log.info("{}","lost insert fail");
				}
			}else {
				if(this.convertToBoolean((int)this.session.insert("userLostImgRegister", items.getLalist().get(0)))) {
					log.info("{}","lost img insert success");
					if(this.convertToBoolean((int)this.session.delete("delImgLostTemp",items))) {
						log.info("{}","delImgLostTemp success");
						this.tranManager.commit();
					}
					else
						log.info("{}","delImgLostTemp fail");
				}
				else
					log.info("{}","lost img insert fail");
			}
		}catch(Exception e){
			log.info("{}","insert fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized void lostMatching(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			faList=this.session.selectList("lostMatching",items);				
			items.setFalist((ArrayList<FoundArticleBean>)faList);
			items.setLalist(null);
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized void foundMatching(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("foundMatching",items);
			log.info("{}","foundMatching success");
			items.setLalist((ArrayList<LostArticleBean>)laList);
			items.setMInfo(new MatchingBean());
			MatchingAlarmBean ma = new MatchingAlarmBean();
			ma.setMaFaName(items.getFalist().get(0).getFaName());
			ma.setMaFaCtNumber(items.getFalist().get(0).getFaCtNumber());
			ArrayList<MatchingAlarmBean> maList = new ArrayList<MatchingAlarmBean>();
			maList.add(ma);
			items.getMInfo().setAlarm(maList);
			items.setFalist(null);
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized void getLostDetailSearch(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("searchLostDetail",items);				
			items.setLalist((ArrayList<LostArticleBean>)laList);
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized int getTotalLostImgSearchNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalLostImgSearchNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
		
	}
	final synchronized int getTotalFoundImgSearchNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalFoundImgSearchNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
		
	}
	final synchronized int getTotalLostSearchNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalLostSearchNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
		
	}
	final synchronized void getFoundDetailSearch(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			faList=this.session.selectList("searchFoundDetail",items);				
			items.setFalist((ArrayList<FoundArticleBean>)faList);
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
	}
	final synchronized int getTotalFoundSearchNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalFoundSearchNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
		
	}
	final synchronized int getTotalFoundNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalFoundNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
	}
	final synchronized int getTotalLostNum(Object obj) {
		int result=-1;
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			
			if(convertToBoolean(result=(int)this.session.selectOne("getTotalLostNum",items))) {
				log.info("{}","total cnt select sucess");
				
			}else {
				log.info("{}","select fail");
			}
		}catch(Exception e){
			log.info("{}","select fail2");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return result;
	}
	final synchronized ItemsBean getFoundTotal(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			
			faList=this.session.selectList("getFoundTotal",items);
			items.setFalist((ArrayList<FoundArticleBean>)faList);
			
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		
		return items;
	}
	final synchronized ItemsBean getMyComment(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("getMyCommentLost",items);
			items.getMonthList().add(new MonthBean("PORTAL"));
			faList=this.session.selectList("getMyCommentFound",items);
			items.setFalist((ArrayList<FoundArticleBean>)faList);
			items.setLalist((ArrayList<LostArticleBean>)laList);
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		return items;
	}
	final synchronized ItemsBean getMyFound(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			faList=this.session.selectList("getMyFound",items);
			items.setFalist((ArrayList<FoundArticleBean>)faList);
			
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		
		return items;
	}
	final synchronized ItemsBean getMyLost(Object obj) {
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("getMyLost",items);
			items.setLalist((ArrayList<LostArticleBean>)laList);
			
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		
		return items;
	}
	final synchronized ItemsBean getLostTotal(Object obj){
		ItemsBean items =((ItemsBean)obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList=this.session.selectList("getLostTotal",items);
			items.setLalist((ArrayList<LostArticleBean>)laList);
			
		}catch(Exception e){
			log.info("{}","select fail");
			e.printStackTrace();this.tranManager.rollback();
		}finally {
			this.tranManager.tranEnd();
		}
		
		return items;
	}
	final boolean convertToBoolean(int value)
	{
		return value>0 ? true : false;
	}
}
