package Server.Tools.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeatherAPI {
	private final String diachi;
	private String title;

	public WeatherAPI() {
		this.diachi = "https://vnweather.net/?fc_city=";
	}

	public ArrayList<Weather> getWeather(String khuvuc, int thoigian) {
		// 0 today
		try {
			Document doc = Jsoup.connect(diachi + khuvuc).ignoreContentType(true).get();
			String checkFound = doc.getElementById("fc_weather").getElementsByTag("h3").first().text();
			this.title = checkFound;
			if (checkFound.equals("Dự báo thời tiết"))
				return null;
			Elements tables = doc.getElementsByTag("table");
			Element table = tables.get(thoigian + 2);
			Element tbody = table.getElementsByTag("tbody").first();
			Elements trows = tbody.getElementsByTag("tr");
			String time, dubao, nhietdo, mua, khiap, gio;
			ArrayList<Weather> array = new ArrayList<>();
			for (Element tr : trows) {
				time = tr.getElementsByTag("td").get(0).text();
				dubao = tr.getElementsByTag("td").get(1).text();
				nhietdo = tr.getElementsByTag("td").get(2).text();
				mua = tr.getElementsByTag("td").get(3).text();
				khiap = tr.getElementsByTag("td").get(4).text();
				gio = tr.getElementsByTag("td").get(5).text();
				Weather tt = new Weather(time, dubao, nhietdo, mua, khiap, gio);
				array.add(tt);
			}
			return array;
		} catch (IOException ex) {
			Logger.getLogger(WeatherAPI.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public String process(String input) {
		String result = "";

		if (!input.matches("[A-Z]{1,} [0-9]{1,}")) {
			result = "Dữ liệu không hợp lệ";
			return result;
		}
		String khuvuc = input.split(" ")[0];
		int songay = Integer.parseInt(input.split(" ")[1]);
		if (songay > 10)
			songay = 10;
		Calendar cal = Calendar.getInstance();

		for (int i = 0; i < songay; i++) {
			ArrayList<Weather> array = this.getWeather(khuvuc, i);
			if (array == null || array.size() == 0) {
				if (i == 0)
					return "Không có dữ liệu";
				else
					return result;
			}
			String str = (cal.get(Calendar.DAY_OF_WEEK) == 1 ? "Chủ nhật" : "Thứ " + cal.get(Calendar.DAY_OF_WEEK))
					+ ", Ngày " + cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR);
			result += str + "\n";
			str = String.format("%-20s%-20s\t%-15s%-12s%-22s%s", "Thời gian", "Dự báo", "Nhiệt độ", "Mưa", "Khí áp",
					"Gió");
			result += str + "\n";
			for (Weather tt : array) {
				result += tt.getInfo() + "\n";
			}
			cal.add(Calendar.DATE, 1);
			str = "------------------------------------------------------------------------------------------------------------------------------------------";
			result += str + "\n";
		}
		result = title + "\n" + result;
		return result;
	}

	public static void main(String[] args) {
		WeatherAPI api = new WeatherAPI();
		String result = api.process("HCM 10");
		System.out.println(result);
	}
}