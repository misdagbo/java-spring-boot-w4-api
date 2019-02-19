/**
 * 
 */
package com.w4.api.infrastructures;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tomcat.util.codec.binary.Base64;

import eu.w4.bpm.BPMVariableMap;
import eu.w4.common.configuration.NetConfigurationKey;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	private final static ResourceBundle resource = ResourceBundle.getBundle("application");

	public static Map<NetConfigurationKey, String> params(String host, String port, String mode) {

		Map<NetConfigurationKey, String> parameters = new HashMap<>();

		parameters.put(NetConfigurationKey.RMI_REGISTRY_HOST, host);
		parameters.put(NetConfigurationKey.RMI_REGISTRY_PORT, port);
		parameters.put(NetConfigurationKey.COMMUNICATION_MODE, mode);

		return parameters;
	}

	public static String getKey() {
		return resource.getString("app.key");
	}

	public static String translateState(String etat) {
		Map<String, String> stat = new HashMap<>();
		stat.put("CANCELLED", "Annulé");
		stat.put("CREATED", "Créé");
		stat.put("DELAGATED", "Delegué");
		stat.put("DONE", "Terminé");
		stat.put("EXPIRED", "Expiré");
		stat.put("OFFERED", "Offert");
		stat.put("REASSIGNED", "Reassigné");
		stat.put("RUNNING", "En cours");
		stat.put("SAVED", "Sauvegardé");
		stat.put("SUSPENDED", "Suspendu");
		try {
			return (String) stat.get(etat);
		} catch (Exception e) {
		}
		return etat;
	}

	public static boolean saveImage(String base64String, String nomCompletImage, String extension) {

		try {
			BufferedImage image = decodeToImage(base64String);

			if (image == null) {
				return false;
			}

			File f = new File(nomCompletImage);

			// write the image
			ImageIO.write(image, extension, f);
		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return true;
	}

	public static String generatedDataFromBase64(String base64) {
		String data = base64;

		if (base64.contains(",")) {
			String[] parts = base64.split(",");
			data = parts[1];
		}

		return data;
	}

	public static String generatedMimeTypeFromBase64(String base64) {
		String data = base64;

		if (base64.contains(",")) {
			String[] parts = base64.split(",");
			data = parts[0];
			if (data.contains(";")) {
				String[] partsInfoFile = data.split(";");
				data = partsInfoFile[0];
				if (data.contains(":")) {
					String[] partsMimeType = data.split(":");
					data = partsMimeType[1];
				}
			}
		}

		return data;
	}

	public static String getReportLocation(String reportTemplateName, String fileExtension) {
		String filesDirectory = "/tmp/";
		Utility.createDirectory(filesDirectory);

		if (!filesDirectory.endsWith("/"))
			filesDirectory += "/";
		reportTemplateName = (reportTemplateName != null) ? reportTemplateName : "";

		return filesDirectory + reportTemplateName + "." + fileExtension;
	}

	public static void savePiece(String base64String, String cheminComplet) {

		try {
			byte[] fileByte;
			String data = generatedDataFromBase64(base64String);
			fileByte = Base64.decodeBase64(data);

			try (OutputStream stream = new FileOutputStream(cheminComplet)) {
				stream.write(fileByte);
			} catch (Exception Ex) {
				logger.warn(Ex.getMessage());
			}

		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
	}

	public static BufferedImage decodeToImage(String base64) {

		BufferedImage image = null;
		try {

			String data = generatedDataFromBase64(base64);
			byte[] imageByte = Base64.decodeBase64(data);

			try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
				image = ImageIO.read(bis);
			} catch (Exception Ex) {
				logger.warn("EXCEPTION : " + Ex.getMessage());
			}
		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return image;
	}

	public static byte[] inputStreamToByteArray(InputStream is) {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] byteArray = null;

		try {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			byteArray = buffer.toByteArray();
		} catch (Exception ex) {
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return byteArray;
	}

	public static Fichier downloadFile(String filePath) {

		Fichier download = new Fichier();
		download.setFile(new File(filePath));
		int fileNbBytes = new Long(download.getFile().length()).intValue();
		download.setBytes(new byte[fileNbBytes]);

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(download.getFile());
		} catch (FileNotFoundException exception) {
			logger.warn("Fichier introuvable : " + exception.getMessage());
		}
		try {
			inputStream.read(download.getBytes());
		} catch (IOException exception) {
			logger.warn("Erreur I/O : " + exception.getMessage());
		}
		return download;
	}

	public static String convertFileToBase64(String pathFichier) {

		File originalFile = new File(pathFichier);
		String encodedBase64 = null;
		FileInputStream fileInputStreamReader = null;
		try {
			fileInputStreamReader = new FileInputStream(originalFile);
			byte[] bytes = new byte[(int) originalFile.length()];
			fileInputStreamReader.read(bytes);
			encodedBase64 = new String(Base64.encodeBase64(bytes));
		} catch (FileNotFoundException e) {
			logger.warn("Exception : " + e.getMessage());
		} catch (IOException e) {
			logger.warn("Exception : " + e.getMessage());
		} finally {
			try {
				fileInputStreamReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Exception : " + e.getMessage());
			}
		}
		return encodedBase64;
	}

	public static String formatToBase64(String base64, String mimeType) throws IOException {
		base64 = "data:" + mimeType + ";base64," + base64;
		return base64;
	}

	public static String convertInputStreamToFile(InputStream file, String fileName) {

		byte[] buffer = new byte[1024];
		BufferedInputStream bis;
		String fileNameBase64 = null;
		try {
			String folderName = "/tmp";
			bis = new BufferedInputStream(file);

			if (!new File(folderName).exists()) {
				new File(folderName).mkdirs();
			}

			File newFile = new File(folderName + "/" + fileName);

			// Download file
			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();

			fileNameBase64 = convertFileToBase64(folderName + "/" + fileName);
		} catch (Exception ex) {
			logger.warn("EXCEPTION " + ex.getMessage());
		}
		return fileNameBase64;
	}

	public static void deleteFile(String chemin) {

		File file = new File(chemin);
		try {
			if (file.exists() && file.getName() != null && !file.getName().isEmpty()) {
				FileUtils.forceDelete(new File(chemin));
			}
		} catch (IOException e) {
			logger.warn("EXCEPTION : " + e.getMessage());
		}
	}

	public static Map<String, Object> setTaskVariable(BPMVariableMap taskVariables) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			for (String variableName : taskVariables.keySet()) {
				map.put(variableName, taskVariables.get(variableName).getValue());
			}
		} catch (Exception e) {
			logger.warn("EXCEPTION : " + e.getMessage());
		}
		return map;
	}

	public static List<Item> setTaskVariable(Map<String, Object> variable) {

		List<Item> variables = new ArrayList<>();

		if (variable != null) {
			try {
				for (String key : variable.keySet()) {
					variables.add(new Item(key, variable.get(key)));
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION  : " + e.getMessage());
			}
		}
		return variables;
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static HostSetting getHostSetting(String process) {
		HostSetting hostSetting = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(new File(resource.getString("setting.xml.path")));
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			Element element = (Element) xpath.evaluate("//*[@id='" + process + "']", document, XPathConstants.NODE);
			hostSetting = Utility.setHostSetting(element);
		} catch (Exception ex) {
			logger.warn("Erreur lors de larécupération des informations dans le fichier xml :" + ex.getMessage());
		}
		return hostSetting;
	}

	public static HostSetting setHostSetting(Element element) {
		HostSetting setting = new HostSetting();
		try {
			if (element.getNodeType() == Node.ELEMENT_NODE) {
				setting.setHost(getTagValue("host", element));
				setting.setMode(getTagValue("mode", element));
				setting.setPort(getTagValue("port", element));
			}
		} catch (Exception ex) {
			logger.warn("Erreur: " + ex.getMessage());
		}
		return setting;
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodeList.item(0);
		return node.getNodeValue();
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	private static SecretKey generateKey(String password, byte[] saltBytes, int interation, int keyLength)
			throws GeneralSecurityException {
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), saltBytes, interation, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}

	private static String convertBase64ToString(String base64, int iteration) {
		String pwd = "";
		int i = 0;
		while (i < iteration) {
			pwd = new String(Base64.decodeBase64(base64));
			base64 = pwd;
			i++;
		}
		return pwd;
	}

	private static String convertStringToBase64(String message, int iteration) {
		String pwd = "";
		int i = 0;
		while (i < iteration) {
			pwd = new String(Base64.encodeBase64(message.getBytes()));
			message = pwd;
			i++;
		}
		return pwd;
	}

	public static String generateRandomWord(int wordLength) {
		Random r = new Random(); // Intialize a Random Number Generator with
									// SysTime as the seed
		StringBuilder sb = new StringBuilder(wordLength);
		for (int i = 0; i < wordLength; i++) { // For each letter in the word
			char tmp = (char) ('a' + r.nextInt('z' - 'a')); // Generate a letter
															// between
			// a and z
			sb.append(tmp); // Add it to the String
		}
		return sb.toString();
	}

	public static String decrypt(String message) {
		String finalPassword = "";
		int keySize = 128;
		int iterations = 100;
		try {
			String salt = message.substring(0, 32);
			String iv = message.substring(32, 64);
			String key = message.substring(64, 96);
			int nbreIteration = Integer.parseInt(message.substring(96, 97));
			String encrypted = message.substring(97);
			byte[] saltBytes = hexStringToByteArray(salt);
			byte[] ivBytes = hexStringToByteArray(iv);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
			SecretKeySpec sKey = (SecretKeySpec) generateKey(key, saltBytes, iterations, keySize);
			Cipher c = Cipher.getInstance(Global.ALGORITHM_AES_CBC_PKCS5);
			c.init(Cipher.DECRYPT_MODE, sKey, ivParameterSpec);
			byte[] decordedValue = Base64.decodeBase64(encrypted);
			byte[] decValue = c.doFinal(decordedValue);
			String decryptedValue = new String(decValue);
			finalPassword = convertBase64ToString(decryptedValue, nbreIteration);
		} catch (Exception ex) {
			logger.warn("Erreur: " + ex.getMessage());
		}
		return finalPassword;
	}

	public static String encrypt(String message) {
		String credential = "";
		int keySize = 128;
		int iterations = 100;
		try {
			int nbreIteration = (int) (Math.random() * (9 - 1) + 1);
			message = Utility.convertStringToBase64(message, nbreIteration);
			String salt = generateRandomWord(32);
			String iv = generateRandomWord(32);
			String secret = generateRandomWord(32);
			byte[] saltBytes = hexStringToByteArray(salt);
			byte[] ivBytes = hexStringToByteArray(iv);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
			SecretKeySpec sKey = (SecretKeySpec) generateKey(secret, saltBytes, iterations, keySize);
			Cipher c = Cipher.getInstance(Global.ALGORITHM_AES_CBC_PKCS5);
			c.init(Cipher.ENCRYPT_MODE, sKey, ivParameterSpec);
			byte[] encByte = c.doFinal(message.getBytes(Charset.forName("UTF-8")));
			byte[] base64Cipher = Base64.encodeBase64(encByte);
			String base64String = new String(base64Cipher);
			credential = salt + iv + secret + nbreIteration + base64String;
		} catch (Exception ex) {
			logger.warn("Erreur: " + ex.getMessage());
		}
		return credential;
	}

	public static void createDirectory(String chemin) {
		File file = new File(chemin);
		if (!file.exists()) {
			try {
				FileUtils.forceMkdir(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Date getCurrentDate() {
		return new Date();
	}

	public static Map<String, String> uncryptCredentials(String encryptChaine) {
		Map<String, String> credentials = new HashMap<>();
		try {
			AesManager AES_INC = new AesManager();
			String chaineDecrypted = AES_INC.decryptAes(encryptChaine, getKey());

			String[] tab = chaineDecrypted.split("£");
			String[] q = new String[2];
			if (tab[0].contains("Â")) {
				q = tab[0].split("Â");
			} else {
				q[0] = tab[0];
				q[1] = tab[1];
			}

			credentials.put("login", q[0]);
			credentials.put("password", q[1]);

		} catch (Exception e) {
			logger.warn("ManageAes : erreur sur  " + e.getCause() + " " + e.getMessage());
		}
		return credentials;
	}
}
