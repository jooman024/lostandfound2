package icia.js.lostandfound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.services.auth.Authentication;
import icia.js.lostandfound.utils.ProjectUtils;

@Controller
public class KakaoController {
	@Autowired
	private Authentication auth;
	@Autowired
	ProjectUtils util;
	@Value("${KaKaoApp}")
	private String KaKaoApp;
	@Value("${privateIp}")
	private String privateIp;
	
	@RequestMapping(value = "/login/getKakaoAuthUrl")
	public @ResponseBody String getKakaoAuthUrl(
			HttpServletRequest request) throws Exception {
		
		String reqUrl = 
				"https://kauth.kakao.com/oauth/authorize"
				+ "?client_id="+KaKaoApp
				+ "&redirect_uri=http://"+privateIp+":8888/AuthStation"
				+ "&response_type=code";
		
		return reqUrl;
	}

	@RequestMapping(value = "/KaKaoLogout")
	public @ResponseBody String KakaoLogout(
			HttpServletRequest request) throws Exception {
		auth.backController("A02");
		String reqUrl = 
				"https://kauth.kakao.com/oauth/logout?"
				+ "client_id="+KaKaoApp
				+ "&logout_redirect_uri=ht"
				+ "tp://"+privateIp+":8888";
		return reqUrl;
	}
	@RequestMapping(value="/AuthStationKakao",method = RequestMethod.POST)
	public ModelAndView insKakaoLogin(ModelAndView mav, @ModelAttribute MemberBean member) {
		mav.addObject("member", member);
			mav = auth.backController("A03",mav);
		return mav;
	}
	
	// 카카오 연동정보 조회
	@RequestMapping(value = "/AuthStation")
	public ModelAndView oauthKakao(
			@RequestParam(value = "code", required = false) String code
			, ModelAndView mav, @ModelAttribute MemberBean member) throws Exception {
		
		System.out.println("#########" + code);
        String access_Token = getAccessToken(code); 
        System.out.println("###access_Token#### : " + access_Token);
        
        
        HashMap<String, Object> userInfo = getUserInfo(access_Token);
        System.out.println("###access_Token#### : " + access_Token);
        System.out.println("###userInfo#### : " + userInfo.get("email"));
        System.out.println("###nickname#### : " + userInfo.get("nickname"));
    
        
        JSONObject kakaoInfo =  new JSONObject(userInfo);
        
        String id = kakaoInfo.getString("id");
        String email = kakaoInfo.getString("email");
        String nickname = kakaoInfo.getString("nickname");
        
        member.setMmId(id);
        member.setMmEmail(email);
        member.setMmName(nickname);
        mav.addObject("kakaoInfo", kakaoInfo);		
        mav.addObject("member", member);
        
        
		mav = auth.backController("A06-k", mav);
				
        return mav; //본인 원하는 경로 설정
	}
	
    //토큰발급
	public String getAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //  URL연결은 입출력에 사용 될 수 있고, POST 혹은 PUT 요청을 하려면 setDoOutput을 true로 설정해야함.
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //	POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+KaKaoApp);  //본인이 발급받은 key
            sb.append("&redirect_uri=http://"+privateIp+":8888/AuthStation");     // 본인이 설정해 놓은 경로
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }
	
    //유저정보조회
    public HashMap<String, Object> getUserInfo (String access_Token) {

        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            String id = element.getAsJsonObject().get("id").getAsString();
            
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            //String id = kakao_account.getAsJsonObject().get("id").getAsString();
            
            userInfo.put("accessToken", access_Token);
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
            userInfo.put("id", id);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }
}
