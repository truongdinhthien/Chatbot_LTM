package Security;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.lang.SerializationUtils;

public class Decryptor {
	public static String decrypt(String encString, PrivateKey priKey) {
		try {
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE, priKey);

			byte decryptOut[] = c.doFinal(Base64.getDecoder().decode(encString));
			return new String(decryptOut);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String decrypt(ArrayList<String> encMessage, PrivateKey priKey) {
		String decString = "";
		for (String str : encMessage) {
			decString += decrypt(str, priKey);
		}
		return decString;
	}

	public static String decrypt(byte[] arrayByte, PrivateKey priKey) {
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) SerializationUtils.deserialize(arrayByte);
		return decrypt(list, priKey);
	}
}
