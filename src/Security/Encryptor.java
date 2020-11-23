package Security;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.lang.SerializationUtils;

public class Encryptor {

	public static String encryptString(String message, PublicKey pubKey) {
		try {
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.ENCRYPT_MODE, pubKey);

			byte encryptOut[] = c.doFinal(message.getBytes());
			String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);
			return strEncrypt;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static ArrayList<String> encrypt(String str, PublicKey publicKey, int chunkSize) {
		ArrayList<String> list = new ArrayList<>();
		int size = str.length();
		int idx = 0, len;
		String subStr;
		while (idx < size) {
			len = Math.min(size - idx, chunkSize);
			subStr = str.substring(idx, idx + len);
			list.add(encryptString(subStr, publicKey));
			idx += len;
		}
		return list;
	}

	public static byte[] encrypt(String str, PublicKey publicKey) {
		return SerializationUtils.serialize(encrypt(str, publicKey, 50));
	}
}