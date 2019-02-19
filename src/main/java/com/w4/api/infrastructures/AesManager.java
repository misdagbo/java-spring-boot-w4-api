/**
 * 
 */
package com.w4.api.infrastructures;

import java.nio.charset.Charset;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author frederic
 *
 */
public class AesManager {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);

	public AesManager() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public String encryptAes(String data_a_encoded, String secretCode) {
		String data_encoded = "";
		try {
			Cipher cipherAes = Cipher.getInstance(Global.ALGORITHM_AES_ECB_PKCS7, "BC");
			byte[] decodedKey = Base64.decodeBase64(secretCode.getBytes(Charset.forName("UTF-8")));
			SecretKeySpec secretKey = new SecretKeySpec(decodedKey, Global.ALGORITHM_AES);
			data_encoded = Aes.encrypt(data_a_encoded.getBytes(Charset.forName("UTF-8")), secretKey, cipherAes);
		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return data_encoded;
	}

	public String decryptAes(String data_a_encoded, String secretCode) {
		String data_decoded = "";
		try {
			Cipher cipherAes = Cipher.getInstance(Global.ALGORITHM_AES_ECB_PKCS7, "BC");
			byte[] decodedKey = Base64.decodeBase64(secretCode.getBytes(Charset.forName("UTF-8")));
			SecretKeySpec secretKey = new SecretKeySpec(decodedKey, Global.ALGORITHM_AES);
			data_decoded = Aes.decrypt(Base64.decodeBase64(data_a_encoded.getBytes(Charset.forName("UTF-8"))),
					secretKey, cipherAes);
		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return data_decoded;
	}
}
