/**
 * 
 */
package com.w4.api.infrastructures;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author frederic
 *
 */
public class Aes {

	public Aes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String encrypt(byte[] textBytes, SecretKeySpec secretKey, Cipher aesCipher)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		// Initialize the cipher for encryption. Use the secret key.
		aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

		// Perform the encryption using doFinal
		byte[] encByte = aesCipher.doFinal(textBytes);

		// converts to base64 for easier display.
		byte[] base64Cipher = Base64.encodeBase64(encByte);

		return new String(base64Cipher);
	}

	public static String decrypt(byte[] cipherBytes, SecretKeySpec secretKey, Cipher aesCipher)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {

		aesCipher.init(Cipher.DECRYPT_MODE, secretKey);

		// Perform the encryption using doFinal
		byte[] decByte = aesCipher.doFinal(cipherBytes);

		return new String(decByte);
	}
}
