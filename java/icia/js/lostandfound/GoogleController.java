package icia.js.lostandfound;


import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import icia.js.lostandfound.beans.MemberBean;
import icia.js.lostandfound.config.ConfigUtils;
import icia.js.lostandfound.dtovo.GoogleLoginDto;
import icia.js.lostandfound.dtovo.GoogleLoginRequest;
import icia.js.lostandfound.dtovo.GoogleLoginResponse;
import icia.js.lostandfound.services.auth.Authentication;
import icia.js.lostandfound.utils.ProjectUtils;

@Controller
public class GoogleController {

	@Autowired
	private Authentication auth;
	@Autowired
	private ProjectUtils util;
	@Value("${privateIp}")
	private String privateIp;
	
    private final ConfigUtils configUtils;
    GoogleController(ConfigUtils configUtils) {
        this.configUtils = configUtils;
    }
    @RequestMapping(value="/AuthStationGoogle",method = RequestMethod.POST)
	public ModelAndView insKakaoLogin(ModelAndView mav, @ModelAttribute MemberBean member) {
		mav.addObject("member", member);
		mav = auth.backController("A05",mav);
		return mav;
	}
    @GetMapping(value = "/loging")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = configUtils.googleInitUrl();
        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
    @RequestMapping(value="/GoogleLogout",method = RequestMethod.POST)
	public String GoogleLogout(ModelAndView mav, @ModelAttribute MemberBean member) {
		String page="redirect:http://"+privateIp+":8888/";
		try
		{
			URL url =new URL("https://accounts.google.com/o/oauth2/revoke?token="+ (String)util.getAttribute("gtoken"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
	        System.out.println("Response code: " + conn.getResponseCode());
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	        	util.removeAttribute("gtoken");
	        	auth.backController("A02");
	        } else {
	        	System.out.println("logout error");
	        }
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
		return page;
	}
    @GetMapping(value = "/AuthStationg")
    public ModelAndView redirectGoogleLogin(
            @RequestParam(value = "code") String authCode, ModelAndView mav, @ModelAttribute MemberBean member
    ) {
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(configUtils.getGoogleClientId())
                .clientSecret(configUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(configUtils.getGoogleRedirectUri())
                .grantType("authorization_code")
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
            ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(configUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL�씠 �븘�땶 媛믩쭔 �쓳�떟諛쏄린(NULL�씤 寃쎌슦�뒗 �깮�왂)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            String jwtToken = googleLoginResponse.getIdToken();
            util.setAttribute("gtoken", googleLoginResponse.getAccessToken());
            
            String requestUrl = UriComponentsBuilder.fromHttpUrl(configUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            if(resultJson != null) {
                GoogleLoginDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
               
                member.setMmId(userInfoDto.getKid());
                member.setMmEmail( userInfoDto.getEmail());
                member.setMmName(userInfoDto.getName());
                mav.addObject("googleInfo", userInfoDto);		
                mav.addObject("member", member);
                mav = auth.backController("A06-g", mav);
            }
            else {
                throw new Exception("Google OAuth failed!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(mav);
        return mav;
    }
}
