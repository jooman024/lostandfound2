package icia.js.lostandfound.repository.managerrepo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import icia.js.lostandfound.SimpleTransactionManager;
import icia.js.lostandfound.TransactionAssistant;
import icia.js.lostandfound.beans.CommentsBean;
import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.FoundCommentsBean;
import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.LostCommentsBean;
import icia.js.lostandfound.beans.MainCategoryBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.beans.StatisticsBean;
import icia.js.lostandfound.beans.SubCategoryBean;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ManagerRepository extends TransactionAssistant {

	private SimpleTransactionManager tranManager;

	public ManagerRepository() {
	}

	final public Object backController(String serviceCode, Object obj) {
		Object object = null;
		switch (serviceCode) {
		case "M01":
			object = getStat(serviceCode,(ItemsBean) obj);
			break;
		case "M02":
			object = getStat(serviceCode,(ItemsBean) obj);
			break;
		case "M03":
			object = getStat(serviceCode,(ItemsBean) obj);
			break;
		case "M12":
			object = getMgrLost((ItemsBean) obj);
			break;
		case "M13":
			object = delLost((ItemsBean) obj);
			break;
		case "M14":
			object = getMgrFound((ItemsBean) obj);
			break;
		case "M15":
			object = delFound((ItemsBean) obj);
			break;
		case "M29":
			object = selMember((MemberBean) obj);
			break;
		case "M30":
			object = updMember((MemberBean) obj);
			break;
		case "M32":
			object = selLostComments((CommentsBean) obj);
			break;
		case "M33":
			object = selFoundComments((CommentsBean) obj);
			break;
		case "M34":
			object = delFoundComment((FoundCommentsBean) obj);
			break;
		case "M35":
			object = delLostComment((LostCommentsBean) obj);
			break;
		}
		return object;
	}

	final public void backController(String serviceCode, Object obj, boolean isImg) {
		switch (serviceCode) {
		case "M8":
		case "M9":
			insFoundPoliceData((FoundArticleBean) obj, isImg, serviceCode);
			break;
		case "M7":
			insLostPoliceData((LostArticleBean) obj, isImg, serviceCode);
			break;
		}
	}

	final public Object backController2(String serviceCode, Object obj) {
		Object object = null;
		switch (serviceCode) {
		case "M16":
			object = getMainCategoryList(obj);
			break;
		case "M17":
			object = getSubCategoryList(obj);
			break;
		case "M18":
			object = addSubCategory(obj);
			break;
		case "M19":
			object = updSubCategory(obj);
			break;
		case "M20":
			object = delSubCategory(obj);
			break;
		}
		return object;
	}

	final synchronized ItemsBean getStat(String serviceCode,Object obj) {
		ItemsBean item = ((ItemsBean) obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			StatisticsBean Stat = (StatisticsBean) this.session.selectOne(
					(serviceCode.equals("M01"))?"getStat":
						(serviceCode.equals("M02"))?"getStatWeek":
							"getStatDay", item);
			this.tranManager.commit();
			item.setStat(Stat);
			log.info("{}", "getStat success");
		} catch (Exception e) {
			log.info("{}", "getStat fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return item;
	}

	final synchronized MainCategoryBean updSubCategory(Object obj) {
		MainCategoryBean mcate = ((MainCategoryBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.update("updSubCategory", mcate))) {
				log.info("{}", "updSubCategory success");
				List<SubCategoryBean> SubCategoryList = null;
				SubCategoryList = this.session.selectList("selectSubCategories", mcate);
				mcate.setSubCategoryList((ArrayList<SubCategoryBean>) SubCategoryList);
				log.info("{}", "SelSubCategory success");
				this.tranManager.commit();
			} else {
				log.info("{}", "updSubCategory fail");
			}
		} catch (Exception e) {
			log.info("{}", "updSubCategory fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return mcate;
	}

	final synchronized MainCategoryBean addSubCategory(Object obj) {
		MainCategoryBean mcate = ((MainCategoryBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("addSubCategory", mcate))) {
				log.info("{}", "addSubCategory success");
				List<SubCategoryBean> SubCategoryList = null;
				SubCategoryList = this.session.selectList("selectSubCategories", mcate);
				mcate.setSubCategoryList((ArrayList<SubCategoryBean>) SubCategoryList);
				log.info("{}", "SelSubCategory success");
				this.tranManager.commit();
			} else {
				log.info("{}", "addSubCategory fail");
			}
		} catch (Exception e) {
			log.info("{}", "addSubCategory fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return mcate;
	}
	final synchronized MainCategoryBean delSubCategory(Object obj) {
		MainCategoryBean mcate = ((MainCategoryBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("delSubCategory", mcate))) {
				log.info("{}", "delSubCategory success");
				List<SubCategoryBean> SubCategoryList = null;
				SubCategoryList = this.session.selectList("selectSubCategories", mcate);
				mcate.setSubCategoryList((ArrayList<SubCategoryBean>) SubCategoryList);
				log.info("{}", "SelSubCategory success");
				this.tranManager.commit();
			} else {
				log.info("{}", "delSubCategory fail");
			}
		} catch (Exception e) {
			log.info("{}", "delSubCategory fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return mcate;
	}
	final synchronized CommentsBean selLostComments(Object obj) {
		CommentsBean comments = ((CommentsBean) obj);
		CommentsBean cb= new CommentsBean();
		List<LostCommentsBean> commentsList = null;
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			commentsList = this.session.selectList("selLostComments", comments);
			this.tranManager.commit();
			System.out.println(commentsList);
			cb.setLcList((ArrayList<LostCommentsBean>) commentsList);
			comments=cb;
			log.info("{}", "selLostComments success");
		} catch (Exception e) {
			log.info("{}", "selLostComments fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return comments;
	}
	final synchronized LostCommentsBean delLostComment(Object obj) {
		LostCommentsBean lostcomment = ((LostCommentsBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("delLostComment", lostcomment))) {
				log.info("{}", "delLostComment success");
				this.tranManager.commit();
			} else {
				log.info("{}", "delLostComment fail");
			}
		} catch (Exception e) {
			log.info("{}", "delLostComment fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return lostcomment;
	}
	final synchronized CommentsBean selFoundComments(Object obj) {
		CommentsBean comments = ((CommentsBean) obj);
		CommentsBean cb= new CommentsBean();
		List<FoundCommentsBean> commentsList = null;
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			commentsList = this.session.selectList("selFoundComments", comments);
			this.tranManager.commit();
			cb.setFcList((ArrayList<FoundCommentsBean>) commentsList);
			comments=cb;
			log.info("{}", "selFoundComments success");
		} catch (Exception e) {
			log.info("{}", "selFoundComments fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return comments;
	}
	final synchronized FoundCommentsBean delFoundComment(Object obj) {
		FoundCommentsBean foundcomment = ((FoundCommentsBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("delFoundComment", foundcomment))) {
				log.info("{}", "delFoundComment success");
				this.tranManager.commit();
			} else {
				log.info("{}", "delFoundComment fail");
			}
		} catch (Exception e) {
			log.info("{}", "delFoundComment fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return foundcomment;
	}
	final synchronized MemberBean selMember(Object obj) {
		MemberBean member = ((MemberBean) obj);
		List<MemberBean> memberList = null;
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			memberList = this.session.selectList("selMember", member);
			this.tranManager.commit();
			member.setMemberList((ArrayList<MemberBean>) memberList);
			log.info("{}", "getMember success");
		} catch (Exception e) {
			log.info("{}", "getMember fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return member;
	}

	final synchronized MemberBean updMember(Object obj) {
		MemberBean member = ((MemberBean) obj);
		List<MemberBean> memberList = null;
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
				if (this.convertToBoolean((int) this.session.update("updMember", member))) {
					log.info("{}", "updMember success");
					memberList = this.session.selectList("selMember", member);
					this.tranManager.commit();
					member.setMemberList((ArrayList<MemberBean>) memberList);
					log.info("{}", "selMember success");
				} else {
					log.info("{}", "updMember fail");
				}
		} catch (Exception e) {
			log.info("{}", "updSubCategory fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return member;
	}

	final synchronized MainCategoryBean getMainCategoryList(Object obj) {
		MainCategoryBean mcate = ((MainCategoryBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			List<MainCategoryBean> MaincategoryList = null;

			MaincategoryList = this.session.selectList("selectMainCategories", mcate);
			System.out.println(MaincategoryList);
			mcate.setMainCategoryList((ArrayList<MainCategoryBean>) MaincategoryList);

		} catch (Exception e) {
			log.info("{}", "select fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return mcate;
	}

	final synchronized MainCategoryBean getSubCategoryList(Object obj) {
		MainCategoryBean mcate = ((MainCategoryBean) obj);

		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			List<MainCategoryBean> MaincategoryList = null;
			MaincategoryList = this.session.selectList("selectMainCategories", mcate);
			log.info("{}", MaincategoryList);
			List<SubCategoryBean> SubCategoryList = null;

			SubCategoryList = this.session.selectList("selectSubCategories", mcate);
			log.info("{}", SubCategoryList);
			mcate.setMainCategoryList((ArrayList<MainCategoryBean>) MaincategoryList);
			mcate.setSubCategoryList((ArrayList<SubCategoryBean>) SubCategoryList);

		} catch (Exception e) {
			log.info("{}", "select fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return mcate;
	}

	final synchronized ItemsBean delLost(Object obj) {
		ItemsBean items = ((ItemsBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("delLost", items))) {
				log.info("{}", "delLost success");
				this.tranManager.commit();
			} else {
				log.info("{}", "delLost fail");
			}
		} catch (Exception e) {
			log.info("{}", "delLost fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return items;
	}

	final synchronized private ItemsBean getMgrLost(Object obj) {
		ItemsBean items = ((ItemsBean) obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<LostArticleBean> laList = null;
			laList = this.session.selectList("getMgrLost", items);
			items.setLalist((ArrayList<LostArticleBean>) laList);
			log.info("{}", "getMgrLost selet success");
		} catch (Exception e) {
			log.info("{}", "getMgrLost fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return items;
	}

	final synchronized ItemsBean delFound(Object obj) {
		ItemsBean items = ((ItemsBean) obj);
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (this.convertToBoolean((int) this.session.delete("delFound", items))) {
				log.info("{}", "delFound success");
				this.tranManager.commit();
			} else {
				log.info("{}", "delFound fail");
			}
		} catch (Exception e) {
			log.info("{}", "delFound fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return items;
	}

	final synchronized private ItemsBean getMgrFound(Object obj) {
		ItemsBean items = ((ItemsBean) obj);
		try {
			this.tranManager = getTransaction(true);
			this.tranManager.tranStart();
			List<FoundArticleBean> faList = null;
			faList = this.session.selectList("getMgrFound", items);
			items.setFalist((ArrayList<FoundArticleBean>) faList);
			log.info("{}", "getMgrFound selet success");
		} catch (Exception e) {
			log.info("{}", "getMgrFound fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
		return items;
	}

	final  private String MonthCtl(Object obj, String serviceCode) {
		String Month = (serviceCode.equals("M8")) ? ((FoundArticleBean) obj).getFaDate().split("-")[1].split("-")[0]
				: ((LostArticleBean) obj).getLaDate().split("-")[1].split("-")[0];
		String dmlName = null;
		if (Month.equals("01")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataJAN" : "insLostPoliceDataJAN";
		} else if (Month.equals("02")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataFEB" : "insLostPoliceDataFEB";
		} else if (Month.equals("03")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataMAR" : "insLostPoliceDataMAR";
		} else if (Month.equals("04")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataAPR" : "insLostPoliceDataAPR";
		} else if (Month.equals("05")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataMAY" : "insLostPoliceDataMAY";
		} else if (Month.equals("06")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataJUN" : "insLostPoliceDataJUN";
		} else if (Month.equals("07")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataJUL" : "insLostPoliceDataJUL";
		} else if (Month.equals("08")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataAUG" : "insLostPoliceDataAUG";
		} else if (Month.equals("09")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataSEP" : "insLostPoliceDataSEP";
		} else if (Month.equals("10")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataOCT" : "insLostPoliceDataOCT";
		} else if (Month.equals("11")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataNOV" : "insLostPoliceDataNOV";
		} else if (Month.equals("12")) {
			dmlName = (serviceCode.equals("M8")) ? "insFoundPoliceDataDES" : "insLostPoliceDataDES";
		}
		log.info("{}", dmlName + " name");
		return dmlName;
	}

	final private void insFoundPoliceData(FoundArticleBean fa, boolean isImg, String serviceCode) {
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();
			if (serviceCode.equals("M8")) {
				if (this.convertToBoolean((int) this.session.insert(MonthCtl(fa, serviceCode), fa))) {
					log.info("{}", "found insert success");
					this.session.insert("insMemberData", fa.getMember());
					log.info("{}", "member insert success");
					if (isImg) {
						if (this.convertToBoolean((int) this.session.insert("insFoundPoliceImgData", fa))) {
							log.info("{}", "foundimg insert success");
							this.tranManager.commit();
						} else {
							log.info("{}", "found insert fail");
						}
					} else {
						this.tranManager.commit();
					}
				} else {
					log.info("{}", "found insert fail");
				}
			} else if (serviceCode.equals("M9")) {
				if (this.convertToBoolean((int) this.session.insert("insFoundPolicePortalData", fa))) {
					log.info("{}", "found insert success");
					this.session.insert("insMemberData", fa.getMember());
					log.info("{}", "member insert success");
					if (isImg) {
						if (this.convertToBoolean((int) this.session.insert("insFoundPoliceImgData", fa))) {
							log.info("{}", "foundimg insert success");
							this.tranManager.commit();
						} else {
							log.info("{}", "found insert fail");
						}
					} else {
						this.tranManager.commit();
					}
				}
			}
		} catch (Exception e) {
			log.info("{}", "insert fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}

	}

	final private void insLostPoliceData(LostArticleBean la, boolean isImg, String serviceCode) {
		try {
			this.tranManager = getTransaction(false);
			this.tranManager.tranStart();

			if (this.convertToBoolean((int) this.session.insert(MonthCtl(la, serviceCode), la))) {
				log.info("{}", "lost insert success");
				this.session.insert("insMemberData", la.getMember());
				log.info("{}", "member insert success");
				if (isImg) {
					if (this.convertToBoolean((int) this.session.insert("insLostPoliceImgData", la))) {
						log.info("{}", "lostimg insert success");
						this.tranManager.commit();
					} else {
						log.info("{}", "lostimg insert fail");
					}
				} else {
					this.tranManager.commit();
				}
			} else {
				log.info("{}", "lost insert fail");
			}
		} catch (Exception e) {
			log.info("{}", "insert fail");
			e.printStackTrace();
			this.tranManager.rollback();
		} finally {
			this.tranManager.tranEnd();
		}
	}

	/*
	 * final private void insFoundSeoulData(FoundArticleBeanSeoul fas) { try {
	 * this.tranManager = getTransaction(false); this.tranManager.tranStart();
	 * 
	 * if (this.convertToBoolean((int) this.session.insert("insFoundSeoulData",
	 * fas))) { log.info("{}", "insert success"); this.tranManager.commit(); } }
	 * catch (Exception e) { log.info("{}", "insert fail"); e.printStackTrace();
	 * this.tranManager.rollback(); } finally { this.tranManager.tranEnd(); } }
	 */

	private boolean convertToBoolean(int value) {
		return value > 0 ? true : false;
	}
}
