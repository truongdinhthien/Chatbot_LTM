package Server.Tools;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author xenov
 */
public class PortScan {
	private String hostname;

	public PortScan(String ip) {
		hostname = ip;

	}

	public PortScan() {
	}

	public boolean isOpen(int port) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(hostname, port), 200);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean isOpen(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), 200);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}
	}

	public void scan(int from, int to) {
		System.out.println("Port scan: " + hostname + "\n" + "Đang dò...\n");
		for (int i = from; i <= to; i++) {
			if (isOpen(i))
				System.out.println("Port " + i + " đang mở");
		}

	}

	public String scan(String ip, int from, int to) {
		String result = "Hostname :" + ip + "\n";
		int size = result.length();
		for (int i = from; i <= to; i++) {
			if (isOpen(ip, i))
				result += "Port " + i + " đang mở\n";
		}
		if (result.length() == size)
			return "Không tìm thấy port đang mở";
		else
			result += "Hoàn tất";
		return result;
	}

	public String scan(String ip, String from, String to) {
		if (IP.isIP(ip)) {
			if (IP.isPrivateIP(ip))
				return "Private IP";
			if (IP.isLoopBackIP(ip))
				return "Loopback IP";
		}
		if (ip.equalsIgnoreCase("localhost))"))
			return "Không thể dò localhost!";
		try {
			int iFrom = Integer.parseInt(from);
			int iTo = Integer.parseInt(to);
			return scan(ip, iFrom, iTo);
		} catch (NumberFormatException e) {
			return "Port phải là số";
		}
	}

	public String scan(String params, String delim) {
		String[] str = params.split(delim);
		if (str.length == 3) {
			return scan(str[0], str[1], str[2]);
		} else
			return "Dữ liệu không hợp lệ";
	}

	public static void main(String[] args) {
		PortScan p = new PortScan();
		p.scan("localhost", 1, 445);
	}
}