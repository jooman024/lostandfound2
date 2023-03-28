package icia.js.lostandfound.services.reg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import icia.js.lostandfound.SimpleTransactionManager;
import icia.js.lostandfound.TransactionAssistant;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Registration extends TransactionAssistant  {
	private SimpleTransactionManager tranManager;
	
	@Value("${Sens.Access}")
	private String SensAcess;
	@Value("${Sens.Secret}")
	private String SensSecret;
	@Value("${Sens.Id}")
	private String SensId;
	@Value("${Sens.Regp}")
	private String SensRegp;
	
	public Registration() {}
	
	public String backController(String serviceCode, String Phone) {
		String r=null;
		switch(serviceCode) {	
			case "U02":
			r=sendSms(Phone);
			break;
		}
		return r;
	}
	public ModelAndView backController(String serviceCode) {
		ModelAndView m=null;
		switch(serviceCode) {
		case "A06":
			m=getAddInfo(serviceCode);
			break;
		}
		return m;
	}
	private ModelAndView getAddInfo(String serviceCode) {
		ModelAndView mav = new ModelAndView();
		String page="addInfo";
		mav.setViewName(page);
		return mav;
	}
	private String sendSms(String Phone) {
		String tel=Phone;
	    Random rand = new Random();
	    String numStr = "";
	    for (int i = 0; i < 6; i++) {
	        String ran = Integer.toString(rand.nextInt(10));
	        numStr += ran;
	    }
	    log.info("{}", "random number generate"  + numStr);
	    this.send_msg(tel, numStr);
	    return "{\"numStr\":\""+numStr+"\"}";
	}
	@SuppressWarnings("unchecked")
	private void send_msg(String tel, String rand) {
		String hostNameUrl = "https://sens.apigw.ntruss.com";     		
		String requestUrl= "/sms/v2/services/";                   		
		String requestUrlType = "/messages";                      		
		String accessKey = SensAcess;                     	
		String secretKey = SensSecret;  	
		String serviceId = SensId;       
		String method = "POST";											
		String timestamp = Long.toString(System.currentTimeMillis()); 	
		requestUrl += serviceId + requestUrlType;
		String apiUrl = hostNameUrl + requestUrl;

		JSONObject bodyJson = new JSONObject();
		JSONObject toJson = new JSONObject();
	    JSONArray  toArr = new JSONArray();
	    
	    toJson.put("to",tel);
	    toArr.add(toJson);
	    bodyJson.put("type","SMS");	
	    bodyJson.put("from",SensRegp);	
	    bodyJson.put("content","LostAndFound Certification Number : "+rand+"");
	    bodyJson.put("messages", toArr);		
	    String body = bodyJson.toJSONString();
	    System.out.println(body);
        try {
        	URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            con.setRequestProperty("x-ncp-iam-access-key", accessKey);
            con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
            con.setRequestMethod(method);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            
            wr.write(body.getBytes());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            
            BufferedReader br;
            System.out.println("responseCode" +" " + responseCode);
            if(responseCode==202) { 
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            
            System.out.println(response.toString());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
	private String makeSignature(String url, String timestamp, String method, String accessKey,
			String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
		String space = " ";
		String newLine = "\n";

		String message = new StringBuilder().append(method).append(space).append(url).append(newLine)
				.append(timestamp).append(newLine).append(accessKey).toString();

		SecretKeySpec signingKey;
		String encodeBase64String;
		try {
			signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
			encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
		} catch (UnsupportedEncodingException e) {
			encodeBase64String = e.toString();
		}

		return encodeBase64String;
	}
}
