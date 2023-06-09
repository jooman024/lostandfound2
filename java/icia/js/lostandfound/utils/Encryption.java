package icia.js.lostandfound.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;


/*@Component
public class Encryption implements PasswordEncoder{
	private PasswordEncoder passwordEncoder;

	public Encryption() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public Encryption(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String encode(CharSequence originPassword) {
		return passwordEncoder.encode(originPassword);
	}

	@Override
	public boolean matches(CharSequence originPassword, String encodedPassword) {
		return passwordEncoder.matches(originPassword, encodedPassword);
	}

	public String TripleDesEncoding(String data, String hint) throws Exception {
		if (data == null || data.length() == 0) {	return "";}

		String instance = (key(hint).length() == 24) ? "DESede/ECB/PKCS5Padding"
				: "DES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, getKey(key(hint)));
		String amalgam = data;

		byte[] inputBytes1 = amalgam.getBytes("UTF8");
		byte[] outputBytes1 = cipher.doFinal(inputBytes1);

		Base64.encodeBase64(outputBytes1);

		String encryptionData = new String(Base64.encodeBase64(outputBytes1));
		return encryptionData;
	}

	public String TripleDesDecoding(String encryptionData, String hint) throws Exception {
		if (encryptionData == null || encryptionData.length() == 0)
			return "";

		String instance = (key(hint).length() == 24) ? "DESede/ECB/PKCS5Padding"
				: "DES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, getKey(key(hint)));

		byte[] inputBytes1 = Base64.decodeBase64(encryptionData);
		byte[] outputBytes2 = cipher.doFinal(inputBytes1);

		String decryptionData = new String(outputBytes2, "UTF8");
		return decryptionData;
	}

	public String aesEncode(String str, String hint) throws java.io.UnsupportedEncodingException, 
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
	InvalidAlgorithmParameterException, IllegalBlockSizeException, 
	BadPaddingException {

		String keyValue = key(hint);
		HashMap<String, Object> mapAES = AES256Key(keyValue);

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, (Key)mapAES.get("key"), new IvParameterSpec(mapAES.get("iv").toString().getBytes()));

		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));

		return enStr;
	}

	public String aesDecode(String encryptionData, String hint) throws java.io.UnsupportedEncodingException,
	NoSuchAlgorithmException,
	NoSuchPaddingException, 
	InvalidKeyException, 
	InvalidAlgorithmParameterException,
	IllegalBlockSizeException, 
	BadPaddingException {
		String keyValue = key(hint);
		HashMap<String, Object> mapAES = AES256Key(keyValue);

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, (Key)mapAES.get("key"), new IvParameterSpec(mapAES.get("iv").toString().getBytes("UTF-8")));

		byte[] byteStr = Base64.decodeBase64(encryptionData.getBytes());

		return new String(c.doFinal(byteStr),"UTF-8");
	}

	private String key(String hint) {  
		char[] compareValue = ("k1cj4w@3ib9!lhv#sd7$x0a%qtm^rg2&y6?epu5zn8fo").toCharArray();
		char[] addRootKey = ("KoreaHoonZzangVictoryWin").toCharArray();
		
		String keyValue = "";

		char[] keyValueArray = new char[24];
		char[] charHint = hint.toCharArray();
		
		for(int index = 0; index < charHint.length; index++) {
			keyValueArray[index] = charHint[index]; 
		}
		
		for(int i=0; i < 24 - hint.length(); i++){
			keyValueArray[hint.length()+i] = addRootKey[i]; 
		}
		
		for(int i=0; i<keyValueArray.length; i++){
			for(int j=0; j<compareValue.length; j++){ 
				if (keyValueArray[i] == compareValue[j]){
					int index = j % 24;
					keyValue += addRootKey[index];
					break;
				} 
			}
		}
		return keyValue;
	}

	private Key getKey(String keyValue) throws Exception {
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyValue.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		Key key = keyFactory.generateSecret(desKeySpec);
		return key;
	}

	private HashMap<String, Object> AES256Key(String keyValue) throws UnsupportedEncodingException {
		String iv;
		Key key;
		HashMap<String, Object> mapAES = new HashMap<String, Object>();

		iv = keyValue.substring(0, 16);
		mapAES.put("iv", iv);

		byte[] keyBytes = new byte[16];
		byte[] b = keyValue.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		key = keySpec;
		mapAES.put("key", key);
		return mapAES;
	}

}*/
