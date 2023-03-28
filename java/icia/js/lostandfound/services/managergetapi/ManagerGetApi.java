package icia.js.lostandfound.services.managergetapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import icia.js.lostandfound.beans.FoundArticleBean;
import icia.js.lostandfound.beans.LostArticleBean;
import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.repository.managerrepo.ManagerRepository;
import icia.js.lostandfound.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ManagerGetApi{
	@Autowired
	private ManagerRepository repo;
	@Autowired
	private ProjectUtils util;
	@Value("${PoliceKey}")
	private String PoliceKey;
	@Value("${SeoulSiKey}")
	private String SeoulSiKey;
	@Value("${jsonPath}")
	private String jsonPath;
	
	private int ePoint=5;
	private int sPoint=1;
	
	public ManagerGetApi() {}
	
	public void backController(String serviceCode) {
		switch (serviceCode) {
		case "M7":case "M8":case "M9":
			try {
				getPoliceData(serviceCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "":
			try {
				//getSeoulFaData(serviceCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	/*get file*/
	/*private void getSeoulFaData(String serviceCode) {
		JSONParser parser = new JSONParser();
		try {
			String Path =System.getProperty("user.dir")+jsonPath+"SeoulPublicData.json";
			
			FileInputStream input=new FileInputStream(Path);
	        InputStreamReader reader=new InputStreamReader(input,"UTF-8");
	        BufferedReader in=new BufferedReader(reader);

			JSONObject object = (JSONObject) parser.parse(in);
			reader.close();
			
			JSONArray jArray = (JSONArray)object.get("DATA");

			for (int i = 0; i < jArray.size(); i++) {
				JSONObject obj = (JSONObject) jArray.get(i);
				FoundArticleBean fas = new FoundArticleBean();
				if (((String) obj.get("status")) == null) {
					continue;
				}
				fas.setFasMcCode((String) obj.get("cate"));
				fas.setFasUcCode((String) obj.get("cate"));
				String regDate= (String) obj.get("reg_date");
				fas.setFasCtNumber("S"+(Long) obj.get("id")+regDate.split("-")[0]+regDate.split("-")[1]+regDate.split("-")[2]);
				fas.setFasName((String) obj.get("get_name"));
				String get_area = (String) obj.get("get_area");
				String get_position = (String) obj.get("get_position");
				String take_place =(String) obj.get("take_place");
				String get_good =(String) obj.get("get_good");
				fas.setFasPlace(get_area+get_position+take_place+get_good);
				long timestamp = (Long)obj.get("get_date");
			    Date date = new java.util.Date(timestamp); 
			    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			    String formattedDate = sdf.format(date);
				fas.setFasDate(formattedDate.split(" ")[0]);
				fas.setFasOrgId((String) obj.get("take_id"));
				fas.setFasStatus((String) obj.get("status"));
				fas.setFasUnique((String) obj.get("get_thing"));
				fas.setFasHour(formattedDate.split(" ")[1].split(":")[0]);
				fas.setFasColor("none");
				fas.setFasDepPlace((String) obj.get("get_position"));
				fas.setFasOrName(fas.getFasDepPlace());
				fas.setFasOrTel("none");
				repo.backController(serviceCode, fas);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}*/
	private URL makeUrl(int pagenum, String serviceCode, String testDay) throws IOException  {
		StringBuilder urlBuilder=null;
		if (serviceCode.equals("M8"))
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd"); /*URL*/
		else if(serviceCode.equals("M9"))
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LosPtfundInfoInqireService/getPtLosfundInfoAccToClAreaPd");
		else if(serviceCode.equals("M7")) {
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LostGoodsInfoInqireService/getLostGoodsInfoAccToClAreaPd");
		}
		urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") +"=" +PoliceKey); /*Service Key*/
		/*LocalDateTime now = LocalDateTime.now();
		String nowstr=(now.toLocalDate().getYear()
				+""+String.format("%02d", now.toLocalDate().getMonthValue())+""
				+String.format("%02d", now.toLocalDate().getDayOfMonth()));
		String b7d =(now.minusDays(7).toLocalDate().getYear()
				+""+String.format("%02d", now.minusDays(7).toLocalDate().getMonthValue())+""
				+String.format("%02d", now.minusDays(7).toLocalDate().getDayOfMonth()));
		String b6m =(now.minusMonths(6).toLocalDate().getYear()
				+""+String.format("%02d", now.minusMonths(6).toLocalDate().getMonthValue())+""
				+String.format("%02d", now.minusMonths(6).toLocalDate().getDayOfMonth()));*/
		
        //urlBuilder.append("&" + URLEncoder.encode("START_YMD","UTF-8") + "=" + URLEncoder.encode((serviceCode==0||serviceCode==3)?b6m:b7d, "UTF-8")); 
        //urlBuilder.append("&" + URLEncoder.encode("END_YMD","UTF-8") + "=" + URLEncoder.encode(nowstr, "UTF-8")); 
		urlBuilder.append("&" + URLEncoder.encode("START_YMD","UTF-8") + "=" + URLEncoder.encode(testDay, "UTF-8")); 
		urlBuilder.append("&" + URLEncoder.encode("END_YMD","UTF-8") + "=" + URLEncoder.encode(testDay, "UTF-8")); 
        //urlBuilder.append("&" + URLEncoder.encode("END_YMD","UTF-8") + "=" + URLEncoder.encode(nowstr, "UTF-8")); 
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pagenum+"", "UTF-8")); 
        return new URL(urlBuilder.toString());
	}
	private URL makeUrl(String actId,String fdSn, String serviceCode) throws IOException  {
		StringBuilder urlBuilder=null;
		if (serviceCode.equals("M8"))
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LosfundInfoInqireService/getLosfundDetailInfo"); /*URL*/
		else if(serviceCode.equals("M9"))
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LosPtfundInfoInqireService/getPtLosfundDetailInfo"); 
		else if(serviceCode.equals("M7")) {
			urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LostGoodsInfoInqireService/getLostGoodsDetailInfo");
		}
		urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") +"=" +PoliceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ATC_ID","UTF-8") + "=" + URLEncoder.encode(actId, "UTF-8")); 
        if (serviceCode.equals("M8") || serviceCode.equals("M9")) urlBuilder.append("&" + URLEncoder.encode("FD_SN","UTF-8") + "=" + URLEncoder.encode(fdSn, "UTF-8")); /*�뒿�뱷�닚踰�*/
        
        return new URL(urlBuilder.toString());
	}
	private void getPoliceData(String serviceCode) throws IOException {
		//String[] test= {"20230207","20230107","20221207","20221107","20221007","20220907","20220807"};
		//String[] test= {"20230301"}; 습득물 
		String[] test= {"20230303","20230304","20230305","20230306","20230308","20230309","20230310"};
		for(int j=0; j<(serviceCode.equals("M9")?1:test.length);j++) {
			String xml=callPoliceApi(makeUrl(sPoint,serviceCode,test[j]));
			parserXmlDay("item",xml,serviceCode);
			int totalCount = Integer.parseInt(xml.split("<totalCount>")[1].split("</totalCount>")[0]);
			totalCount = (totalCount / 10) + ((totalCount % 10) == 0 ? 0 : 1);
			
			for(int i=sPoint+1; i<ePoint ; i++) {
				log.info("{}","totalcound : "+totalCount+"  current call : "+i+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				xml=callPoliceApi(makeUrl(i,serviceCode,test[j]));
				if(parserXmlDay("item",xml,serviceCode)) break;
			}
		}
	}
	/*private void getPoliceData(String serviceCode) throws IOException {
		String xml=callPoliceApi(makeUrl(sPoint,serviceCode));
		parserXmlDay("item",xml,serviceCode);
		int totalCount = Integer.parseInt(xml.split("<totalCount>")[1].split("</totalCount>")[0]);
		totalCount = (totalCount / 10) + ((totalCount % 10) == 0 ? 0 : 1);
		
		for(int i=sPoint+1; i<ePoint ; i++) {
			log.info("{}","totalcound : "+totalCount+"  current call : "+i+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			xml=callPoliceApi(makeUrl(i,serviceCode));
			if(parserXmlDay("item",xml,serviceCode)) break;
		}
		for(int i=startpoint+1; i<totalCount+1 ; i++) {
			log.info("{}","totalcound : "+totalCount+"  current call : "+i+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			xml=callPoliceApi(makeUrl(i,serviceCode));
			if(parserXmlDay("item",xml,serviceCode)) break;
		}
		
	}*/
	private String callPoliceApi(URL url) throws IOException  {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
            System.out.println(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
	}
	private boolean parserXmlDetail(String tagname, String xmlstr, String serviceCode, String Color, String filePath) {
		boolean isError = false;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xmlstr)));
			NodeList nodelist = document.getElementsByTagName(tagname);
			boolean isImg = false;

			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				if (serviceCode.equals("M9") || serviceCode.equals("M8")) {
					String[] fcategorydetail = { "atcId", "csteSteNm", "depPlace", "fdHor", "fdPlace", "fdPrdtNm",
							"fdYmd", "fndKeepOrgnSeNm", "orgId", "orgNm", "prdtClNm", "tel", "uniq" };
					FoundArticleBean fa = new FoundArticleBean();
					MemberBean mb = new MemberBean();
					String fdFilePathImg = filePath;

					fa.setFaColor(Color);
					for (int j = 0; j < node.getChildNodes().getLength(); j++) {
						for (int k = 0; k < fcategorydetail.length; k++) {
							if (node.getChildNodes().item(j).getNodeName().equals(fcategorydetail[k])) {
								if (k == 0)
									fa.setFaCtNumber(
											node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 1)
									fa.setFaStatus(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue().equals("반입중")?"C":"S");
								else if (k == 2)
									fa.setFaDepPlace(
											node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 3)
									fa.setFaHour(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 4)
									fa.setFaPlace(makePlCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue())
											);
								else if (k == 5)
									fa.setFaName(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 6)
									fa.setFaDate(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 7)
									fa.setFaDepPlace(fa.getFaDepPlace() + "&"
											+ node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 8) {
									fa.setFaMmId(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								} else if (k == 9)
									mb.setMmName(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 10) {fa.setFaMcCode(
										makeMcCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue()
												.split(" > ")[0])
										);
									fa.setFaUcCode(makeUcCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue()
											.split(" > ")[1])
											);
								} else if (k == 11)
									mb.setMmPhone(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 12)
									fa.setFaUnique(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
							}
						}
					}
					mb.setMmId(fa.getFaMmId());
					mb.setMmSns("AG");
					fa.setMember(mb);
					Class<?> cls = fa.getClass();
					Field allFields[] = cls.getDeclaredFields();
					try {
						for (Field field : allFields) {
							field.setAccessible(true);
							if (field.get(fa) == null) {
								if(fa.getFaMcCode().equals("EL"))
									field.set(fa, "ET5");
								else
									field.set(fa, "none");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						isError = true;break;
					}
					if (!fdFilePathImg.contains("no_img")) {
						isImg = true;
						fa=(FoundArticleBean)util.imageSave(serviceCode, fa, fdFilePathImg);
					}else log.info("{}", "this item noimg");
					repo.backController(serviceCode, fa, isImg);
				} else {
					String[] lcategorydetail = { "atcId", "lstSteNm", "lstLctNm", "lstHor", "lstPlace", "lstSbjt",
							"lstYmd", "lstPlaceSeNm", "orgId", "orgNm", "prdtClNm", "tel", "uniq", "lstFilePathImg" };
					LostArticleBean la = new LostArticleBean();
					MemberBean mb = new MemberBean();
					String lstFilePathImg = null;
					for (int j = 0; j < node.getChildNodes().getLength(); j++) {
						for (int k = 0; k < lcategorydetail.length; k++) {
							if (node.getChildNodes().item(j).getNodeName().equals(lcategorydetail[k])) {
								if (k == 0)
									la.setLaCtNumber(
											node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 1)
									la.setLaStatus(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue().equals("온라인 접수")?"O":"P");
								else if (k == 2)
									la.setLaLocation(makeLocationCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue())
											);
								else if (k == 3)
									la.setLaHour(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 4)
									la.setLaPlace(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 5)
									la.setLaName(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 6)
									la.setLaDate(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 7)
									la.setLaPlace(la.getLaPlace() + "&"
											+ node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 8) {
									la.setLaMmId(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								} else if (k == 9)
									mb.setMmName(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 10) {
									la.setLaMcCode(makeMcCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue()
											.split(" > ")[0])
											);
									la.setLaUcCode(makeUcCode(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue()
											.split(" > ")[1])
											);
								} else if (k == 11)
									mb.setMmPhone(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 12)
									la.setLaUnique(node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue());
								else if (k == 13)
									lstFilePathImg = node.getChildNodes().item(j).getChildNodes().item(0)
											.getNodeValue();
							}
						}
					}
					mb.setMmId(la.getLaMmId());
					mb.setMmSns("AG");
					la.setMember(mb);
					Class<?> cls = la.getClass();
					Field allFields[] = cls.getDeclaredFields();
					try {
						for (Field field : allFields) {
							field.setAccessible(true);
							if (field.get(la) == null) {
								if(la.getLaMcCode().equals("EL"))
									field.set(la, "ET5");
								else
									field.set(la, "none");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						isError = true;break;
					}
					if (!lstFilePathImg.contains("no_img")) {
						isImg = true;
						la=(LostArticleBean)util.imageSave(serviceCode, la, lstFilePathImg);
					}else log.info("{}", "this item noimg");
					repo.backController(serviceCode, la, isImg);
					log.info("{}", "repo end");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("{}", "xml detail parser error");
			isError = true;
		}
		return isError;
	}
	public String makePlCode(String place) {
		String Code=null;
		switch (place) {
		  case "우체국(통)":
		    Code = "P01";
		    break;
		  case "노상":
		    Code = "P02";
		    break;
		  case "기차":
		    Code = "P03";
		    break;
		  case "지하철":
		    Code = "P04";
		    break;
		  case "백화점/매장":
		    Code = "P05";
		    break;
		  case "택시":
		    Code = "P06";
		    break;
		  case "음식점(업소포함)":
		    Code = "P07";
		    break;
		  case "공공기관":
		    Code = "P08";
		    break;
		  case "버스":
		    Code = "P09";
		    break;
		  case "주택":
		    Code = "P10";
		    break;
		  case "공항":
		    Code = "P11";
		    break;
		  case "상점":
		    Code = "P12";
		    break;
		  case "영화관":
		    Code = "P13";
		    break;
		  case "놀이공원":
		    Code = "P14";
		    break;
		  case "유원지":
		    Code = "P15";
		    break;
		  case "학교":
		    Code = "P16";
		    break;
		  case "회사":
		    Code = "P17";
		    break;
		  case "기타":
		    Code = "P18";
		    break;
		  case "불상":
		    Code = "P19";
		    break;
		  default:
		    break;
		}
		return Code;
	}
	public String makeUcCode(String subCate) {
		String Code=null;
		switch (subCate) {
		case "여성용가방":
		    Code = "BA0";
		    break;
		  case "남성용가방":
		    Code = "BA1";
		    break;
		  case "기타가방":
		    Code = "BA2";
		    break;
		  case "반지":
		    Code = "OR0";
		    break;
		  case "목걸이":
		    Code = "OR1";
		    break;
		  case "귀걸이":
		    Code = "OR2";
		    break;
		  case "시계":
		    Code = "OR3";
		    break;
		  case "학습서적":
		    Code = "BO0";
		    break;
		  case "소설":
		    Code = "BO1";
		    break;
		  case "컴퓨터서적":
		    Code = "BO2";
		    break;
		  case "만화책":
		    Code = "BO3";
		    break;
		  case "기타서적":
		    Code = "BO4";
		    break;
		  case "서류":
		    Code = "DO0";
		    break;
		  case "쇼핑백":
		    Code = "SH0";
		    break;
		  case "스포츠용품":
		    Code = "SP0";
		    break;
		  case "건반악기":
		    Code = "IN0";
		    break;
		  case "관악기":
		    Code = "IN1";
		    break;
		  case "타악기":
		    Code = "IN2";
		    break;
		  case "현악기":
		    Code = "IN3";
		    break;
		  case "기타악기":
		    Code = "IN4";
		    break;
		  case "어음":
		    Code = "ST0";
		    break;
		  case "상품권":
		    Code = "ST1";
		    break;
		  case "채권":
		    Code = "ST2";
		    break;
		  case "여성의류":
		    Code = "CL0";
		    break;
		  case "남성의류":
		    Code = "CL1";
		    break;
		  case "아기의류":
		    Code = "CL2";
		    break;
		  case "모자":
		    Code = "CL3";
		    break;
		  case "신발":
		    Code = "CL4";
		    break;
		  case "기타의류":
			    Code = "CL5";
			    break;  
		  case "자동차열쇠":
		    Code = "CA0";
		    break;
		  case "네비게이션":
		    Code = "CA1";
		    break;
		  case "자동차번호판":
		    Code = "CA2";
		    break;
		  case "임시번호판":
		    Code = "CA3";
		    break;
		  case "태블릿":
			    Code = "EL0";
			    break;
			  case "스마트워치":
			    Code = "EL1";
			    break;
			  case "무선 이어폰":
			    Code = "EL2";
			    break;
			  case "카메라":
			    Code = "EL3";
			    break;
			  case "여성용 지갑":
			    Code = "WA0";
			    break;
			  case "남성용 지갑":
			    Code = "WA1";
			    break;
			  case "기타 지갑":
			    Code = "WA2";
			    break;
			  case "신분증":
			    Code = "CE0";
			    break;
			  case "면허증":
			    Code = "CE1";
			    break;
			  case "여권":
			    Code = "CE2";
			    break;
			  case "삼성노트북":
			    Code = "CO0";
			    break;
			  case "LG노트북":
			    Code = "CO1";
			    break;
			  case "애플노트북":
			    Code = "CO2";
			    break;
			  case "신용(체크)카드":
			    Code = "CR0";
			    break;
			  case "일반카드":
			    Code = "CR1";
			    break;
			  case "기타카드":
			    Code = "CR2";
			    break;
			  case "현금":
			    Code = "CS0";
			    break;
			  case "수표":
			    Code = "CS1";
			    break;
			  case "외화":
			    Code = "CS2";
			    break;
			  case "삼성휴대폰":
			    Code = "SM0";
			    break;
			  case "LG휴대폰":
			    Code = "SM1";
			    break;
			  case "아이폰":
			    Code = "SM2";
			    break;
			  case "기타휴대폰":
			    Code = "SM3";
			    break;
			  case "기타통신기기":
			    Code = "SM4";
			    break;
			  case "안경":
			    Code = "ET0";
			    break;
			  case "선글라스":
				    Code = "ET1";
				    break;
				  case "매장문화재":
				    Code = "ET2";
				    break;
				  case "기타용품":
						Code ="ET5";
						break; 
				  case "기타물품":
						Code ="ET4";
						break;  
				  case "기타":
						Code ="ET3";
						break;
				  case "유류품":
				    Code = "LE0";
				    break;
				  default:
				    break;
		}
		return Code;
	}
	public String makeMcCode(String mainCate) {
		String Code=null;
		switch (mainCate) {
		  case "가방":
		    Code ="BA";
		    break;
		  case "귀금속":
		    Code ="OR";
		    break;
		  case "도서용품":
		    Code ="BO";
		    break;
		  case "서류":
		    Code ="DO";
		    break;
		  case "산업용품":
		    Code ="IY";
		    break;
		  case "쇼핑백":
		    Code ="SH";
		    break;
		  case "스포츠용품":
		    Code ="SP";
		    break;
		  case "악기":
		    Code ="IN";
		    break;
		  case "유가증권":
		    Code ="ST";
		    break;
		  case "의류":
		    Code ="CL";
		    break;
		  case "자동차":
		    Code ="CA";
		    break;
		  case "전자기기":
		    Code ="EL";
		    break;
		  case "지갑":
		    Code ="WA";
		    break;
		  case "증명서":
		    Code ="CE";
		    break;
		  case "컴퓨터":
		    Code ="CO";
		    break;
		  case "카드":
		    Code ="CR";
		    break;
		  case "현금":
		    Code ="CS";
		    break;
		  case "휴대폰":
		    Code ="SM";
		    break;
		  case "기타물품":
		    Code ="ET";
		    break;
		  case "유류품":
		    Code ="LE";
		    break;
		  default:
		    Code = mainCate;
		    break;
		}
		return Code;
	}
	public String makeLocationCode(String location) {
		String Code=null;
		switch (location) {
		case "서울특별시":
		    Code = "LSE";
		    break;
		  case "강원도":
		    Code = "LGA";
		    break;
		  case "경기도":
		    Code = "LGU";
		    break;
		  case "경상남도":
		    Code = "LGS";
		    break;
		  case "경상북도":
		    Code = "LGN";
		    break;
		  case "광주광역시":
		    Code = "LGJ";
		    break;
		  case "대구광역시":
		    Code = "LDU";
		    break;
		  case "대전광역시":
		    Code = "LDN";
		    break;
		  case "부산광역시":
		    Code = "LBN";
		    break;
		  case "울산광역시":
		    Code = "LUN";
		    break;
		  case "인천광역시":
		    Code = "LIN";
		    break;
		  case "전라남도":
		    Code = "LJS";
		    break;
		  case "전라북도":
		    Code = "LJN";
		    break;
		  case "충청남도":
		    Code = "LCS";
		    break;
		  case "충청북도":
		    Code = "LCN";
		    break;
		  case "제주특별자치도":
		    Code = "LJU";
		    break;
		  case "세종특별자치시":
		    Code = "LSG";
		    break;
		  case "기타":
		    Code = "LEC";
		    break;
		  case "해외":
			 Code ="LAB";
			 break;
		default:
			Code = location;
			break;
		}
		return Code;
	}
	private boolean parserXmlDay(String tagname, String xmlstr, String serviceCode) {
		boolean isError=false;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xmlstr)));
			NodeList nodelist = document.getElementsByTagName(tagname);

			System.out.println(nodelist.getLength());
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);// 泥ル쾲吏� element �뼸湲�

				if (serviceCode.equals("M8") || serviceCode.equals("M9")) {
					String[] fcategory = { "atcId", "fdSn","clrNm","fdSbjt","fdFilePathImg"};
					String atcId = null;
					String fdSn = null;
					String clrNm = null;
					String fdSbjt = null;
					String fdFilePathImg=null;
					for (int j = 0; j < node.getChildNodes().getLength(); j++) {
						for (int k = 0; k < fcategory.length; k++) {
							if (node.getChildNodes().item(j).getNodeName()
									.equals(fcategory[k])) {
								if (k == 0)
									atcId = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
								else if (k == 1)
									fdSn = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
								else if (k == 2 )
									clrNm = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
								else if (k == 3)
									fdSbjt = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
								else if (k == 4)
									fdFilePathImg = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
							}
						}
					}
					if (atcId != null && fdSn != null) {
						if(clrNm == null)
							if(fdSbjt != null)
								{clrNm = fdSbjt.split("\\)색")[0];// split input : color -> hangul 
								String temp =clrNm.substring(clrNm.lastIndexOf("(")+1);
								clrNm= temp;
								}
							else 
								clrNm = "color info is null";
						String xml = callPoliceApi(makeUrl(atcId, fdSn, serviceCode));
						if(parserXmlDetail("item", xml, serviceCode,clrNm,fdFilePathImg)) {isError=true;break;}
					}
				} else if (serviceCode.equals("M7")) {
					String atcId = null;
					for (int j = 0; j < node.getChildNodes().getLength(); j++) {
						if (node.getChildNodes().item(j).getNodeName().equals("atcId")) {
							atcId = node.getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
						}
					}
					if(atcId != null) {
						String xml = callPoliceApi(makeUrl(atcId, "", serviceCode));
						if(parserXmlDetail("item", xml, serviceCode,null,"ImgPath")) {isError=true;break;}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			isError=true;
			log.info("{}", "xml day parser error");
		}
		return isError;
	}
	/*get api seoulsi
	private void getSeoulFaDataApi() throws IOException {
		while(SeoulSiFlag) {
			String json=callSeoulFound(SeoulSiTotalCount);
			SeoulSiTotalCount-=10;
			parserJsonString("row",json,10);
		}
	}
	private void parserJsonString(String tagName, String JsonStr, int rownum) {
		
		JSONObject jObject = new JSONObject(JsonStr);
		JSONObject lfObject = jObject.getJSONObject("lostArticleInfo");
		if(SeoulSiTotalCount==0)
			SeoulSiTotalCount=lfObject.getInt("list_total_count");
		else
		{
			JSONArray jArray = lfObject.getJSONArray("row");
			try {
				this.tranManager = getTransaction(false);
				this.tranManager.tranStart();
				
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject obj = jArray.getJSONObject(i);
					FoundArticleBeanSeoul fas= new FoundArticleBeanSeoul();
					fas.setFasRegdate(obj.getString("REG_DATE"));
					LocalDate now = LocalDate.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime date = LocalDateTime.parse(fas.getFasRegdate()+" 00:00:00", formatter);
					if(ChronoUnit.DAYS.between(date, now.atStartOfDay())>=8) {
						SeoulSiFlag=false;
						break;
					}
					fas.setFasName(obj.getString("GET_NAME"));
					fas.setFasdate(obj.getString("GET_DATE"));
					fas.setFasplace(obj.getString("TAKE_PLACE"));
					fas.setFasDepGu(obj.getString("GET_AREA"));
					fas.setFasDepPlace(obj.getString("GET_POSITION"));
					fas.setFasDetail(obj.getString("GET_THING"));
					fas.setFasMainCategory(obj.getString("CATE"));
					fas.setFasStatus(obj.getString("STATUS"));
					fas.setFasId(obj.getString("ID"));
					if(this.convertToBoolean((int)this.session.insert("insFoundSeoulData", fas))) {
						log.info("{}","insert�꽦怨�");
					}
				}
			}catch(Exception e){
				log.info("{}","insert�떎�뙣 濡ㅻ갚");
				e.printStackTrace();this.tranManager.rollback();
			}finally {
				this.tranManager.commit();
				this.tranManager.tranEnd();
			}
		}
	}
	private String callSeoulFound(int pageno) throws IOException {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); 
		urlBuilder.append("/" +  URLEncoder.encode(SeoulSiKey,"UTF-8") ); 
		urlBuilder.append("/" +  URLEncoder.encode("json","UTF-8") ); 
		urlBuilder.append("/" + URLEncoder.encode("lostArticleInfo","UTF-8")); 
		urlBuilder.append("/" + URLEncoder.encode((pageno-9)+"","UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(pageno+"","UTF-8")); 
		
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/xml");
		System.out.println("Response code: " + conn.getResponseCode()); 
		BufferedReader rd;

		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
				sb.append(line);
		}
		rd.close();
		conn.disconnect();
		return sb.toString();
	}*/
}
