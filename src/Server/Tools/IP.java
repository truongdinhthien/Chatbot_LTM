package Server.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xenov
 */
public class IP {
	private String ipadress;

	public IP(String ipadress) {
		this.ipadress = ipadress;
	}

	public IP() {
	}

	public String getIpadress() {
		return ipadress;
	}

	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}

	public String getInformation(String ip) {
		if (isIP(ip)) {
			if (isPrivateIP(ip))
				return "Private IP";
			if (isLoopBackIP(ip))
				return "Loopback IP";
		}
		try {
			String result = "";
			URL url = new URL(
					"http://ip-api.com/line/" + ip + "?fields=status,continent,country,regionName,city,lat,lon");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			if (r.readLine().equals("success")) {
				result += "IP/Hostname: " + ip + "\n";
				result += "Châu lục:  " + r.readLine() + "\n";
				result += "Quốc gia:  " + r.readLine() + "\n";
				result += "Khu vực:  " + r.readLine() + "\n";
				result += "Thành phố: " + r.readLine() + "\n";
				result += "Tọa độ: " + r.readLine() + ", " + r.readLine();
				return result;
			}
		} catch (MalformedURLException ex) {
			Logger.getLogger(IP.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(IP.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "Không tìm thấy thông tin";
	}

	public static boolean isIP(String ip) {
		return ip.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$");
	}

	public static boolean isPrivateIP(String ip) {
		return ip.matches("(10)(\\.([2]([0-5][0-5]|[01234][6-9])|[1][0-9][0-9]|[1-9][0-9]|[0-9])){3}")
				|| ip.matches(
						"(172)\\.(1[6-9]|2[0-9]|3[0-1])(\\.(2[0-4][0-9]|25[0-5]|[1][0-9][0-9]|[1-9][0-9]|[0-9])){2}")
				|| ip.matches("(192)\\.(168)(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){2}");
	}

	public static boolean isLoopBackIP(String ip) {
		return ip.startsWith("127.");
	}
}
