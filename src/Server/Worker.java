package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import Security.Decryptor;
import Security.Encryptor;
import Security.SecurityKeyPairGenerator;

public class Worker implements Runnable {
	private String myName;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private PublicKey myPublicKey;
	private PrivateKey myPrivateKey;
	private PublicKey clientPublicKey;

	private String status;

	public Worker(Socket s, String name) throws IOException {
		this.socket = s;
		this.myName = name;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());

		if (exchangeSecurityKey() == 0) {
			System.out.println("Failed to exchange security key. Thread " + name + " finished.");
			removeFromQueue();
			this.Close();
			return;
		}

		status = "waiting";

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					while (status.equals("waiting")) {
						send(status);
						Thread.sleep(2000);
					}
					send(status);
				} catch (Exception e) {
					removeFromQueue();
					Close();
				}
			}
		}).start();
	}

	private int exchangeSecurityKey() {
		KeyPair keyPair = SecurityKeyPairGenerator.getKeyPair();
		myPublicKey = keyPair.getPublic();
		myPrivateKey = keyPair.getPrivate();
		try {
			byte[] b = new byte[32000];
			dis.read(b);
			clientPublicKey = SecurityKeyPairGenerator.getPublicKey(b);
			dos.write(myPublicKey.getEncoded());
			return 1;
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void run() {

		System.out.println("Client " + socket.toString() + " accepted");
		status = "running";
		String text = "";
		byte[] encText = new byte[32000];

		try {
			while (true) {

				dis.read(encText);
				text = Decryptor.decrypt(encText, myPrivateKey);

				if (text.equals("bye")) {
					break;
				}
				String params = text;
				var result = "";

				if (text.matches("#weather#[^/ ].*")) {
					params = text.split("(#weather#)")[1];
					result = Server.weatherAPI.process(params.toUpperCase());

				} else if (text.matches("#scan#[^/ ].*")) {
					params = text.split("(#scan#)")[1];
					result = Server.portScan.scan(params, " ");

				} else if (text.matches("#ip#[^/ ].*")) {
					params = text.split("(#ip#)")[1];
					result = Server.IPAPI.getInformation(params);

				} else if (text.matches("#detect#[^#]+")) {
					params = text.split("(#detect#)")[1].trim();
					result = Server.detector.process(params);

				} else if (text.matches("#translate#[a-zA-Z-]+#[a-zA-Z-]+#[^#]+")
						|| text.matches("#translate#[a-zA-Z-]+#[^#]+") || text.matches("#translate#[^#]+")) {
					params = text.split("(#translate#)")[1].trim();
					result = Server.tranlator.process(params);

				} else if (text.matches("#code#")) {
					result = Server.detector.codeLanguage;

				} else if (text.matches("#help#")) {
					result = helpInformation;
				} else {
					result = Server.simsimi.Excute(params);
				}

				if (result == null)
					result = "Hết 100 tin nhắn rồi";

				send(result);
			}

		} catch (IOException e) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Finished " + socket.toString());
			this.Close();
			Server.workers.remove(this);
		}
	}

	private void send(String message) throws IOException {
		byte[] decResult = Encryptor.encrypt(message, clientPublicKey);
		dos.write(decResult);
		dos.flush();
	}

	private void Close() {
		try {
			dis.close();
			dos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeFromQueue() {
		Server.workers.remove(this);
		Server.executor.remove(this);
	}

	private static final String helpInformation = "Thông tin hướng dẫn\n" + "Lấy thông tin địa chỉ IP:\n"
			+ "#ip# + IP/Hostname\n" + "Ví dụ: \n" + "#ip#25.45.78.52\n" + "#ip#facebook.com\n" + "\n" + "Scan port\n"
			+ "#scan# + IP/Hostname + startPort + endPort\n" + "Ví dụ:\n" + "#scan#facebook.com 440 445\n"
			+ "#scan#25.41.22.55 10 100\n" + "\n" + "Lấy thông tin thời tiết\n"
			+ "#weather# + Tên tỉnh thành +  Số ngày(tối đa 10 ngày)\n" + "Ví dụ:\n" + "#weather#hanoi 10\n"
			+ "#weather#hcm 5\n" + "\n" + "Phát hiện ngôn ngữ:\n" + "#detect#văn bản\n" + "Ví dụ\n"
			+ "#detect#chào bạn\n" + "#detect#hello everyone\n" + "\n" + "Chuyển đổi ngôn ngữ:\n"
			+ "Dịch văn bản sang tiếng việt tự động: #translate#văn bản\n"
			+ "Dịch văn bản sang ngôn ngữ khác tự động: #translate#mã ngôn ngữ đích#văn bản\n"
			+ "Dịch từ ngôn ngữ này sang ngôn ngữ khác:\n#translate#mã ngôn ngữ nguồn#mã ngôn ngữ đích#văn bản\n"
			+ "Ví dụ\n" + "#translate#hi everyone\n" + "#translate#fr#chào mọi người\n"
			+ "#translate#vi#en#trong nhà\n\n" + "Lấy thông tin mã ngôn ngữ:\n" + "#code#\n\n"
			+ "Ngoài cú pháp, mặc định chat với SimSiMi";
}
