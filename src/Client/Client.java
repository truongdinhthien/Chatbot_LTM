package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import Security.Decryptor;
import Security.Encryptor;
import Security.SecurityKeyPairGenerator;

public class Client {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private PublicKey myPublicKey;
	private PrivateKey myPrivateKey;
	private PublicKey serverPublicKey;

	public Client(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
	}

	public String Execute(String text) throws IOException {
		byte[] encText = Encryptor.encrypt(text, serverPublicKey);
		dos.write(encText);

		byte[] response = new byte[32000];
		dis.read(response);
		return Decryptor.decrypt(response, myPrivateKey);
	}

	public String getRespone() {
		byte[] response = new byte[32000];
		try {
			dis.read(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Decryptor.decrypt(response, myPrivateKey);
	}

	public void SendBye() throws IOException {
		byte[] encText = Encryptor.encrypt("bye", serverPublicKey);
		dos.write(encText);
	}

	public void Close() throws IOException {
		dis.close();
		dos.close();
		socket.close();
	}

	public int exchangeSecurityKey() {
		KeyPair keyPair = SecurityKeyPairGenerator.getKeyPair();
		myPublicKey = keyPair.getPublic();
		myPrivateKey = keyPair.getPrivate();
		try {
			dos.write(myPublicKey.getEncoded());
			byte[] b = new byte[2048];
			dis.read(b);
			serverPublicKey = SecurityKeyPairGenerator.getPublicKey(b);
			return 1;
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
