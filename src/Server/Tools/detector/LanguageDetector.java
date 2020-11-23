package Server.Tools.detector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Scanner;

import com.google.gson.Gson;

import Common.Config;

public class LanguageDetector {
	public LinkedHashMap<String, String> countries;
	public String codeLanguage;

	public LanguageDetector() {
		map();
		codeLanguage = getCodeLanguages();
	}

	public DetectionResponse getResult(String language) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2/detect"))
				.header("content-type", "application/x-www-form-urlencoded")
				.header("accept-encoding", "application/gzip").header("x-rapidapi-key", Config.X_RapidAPI_Key)
				.header("x-rapidapi-host", "google-translate1.p.rapidapi.com")
				.method("POST", HttpRequest.BodyPublishers.ofString("q=" + language)).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		Gson json = new Gson();
		return (DetectionResponse) json.fromJson(response.body(), DetectionResponse.class);
	}

	public String process(String q) {
		if (q.length() < 1)
			return "Bạn chưa nhập dữ liệu để tra cứu";
		try {
			DetectionResponse lang = getResult(q);
			if (lang.getLanguage().equals("und"))
				return "Ngôn ngữ không xác định";
			String whatLanguage = countries.get(lang.getLanguage());
			if (whatLanguage == null)
				return "Ngôn ngữ: " + lang.getLanguage() + " - " + "Độ chính xác: "
						+ (int) Math.ceil(lang.getConfidence() * 100) + "%";
			else
				return "Ngôn ngữ: " + whatLanguage + " (" + lang.getLanguage() + ") " + " - " + "Độ chính xác: "
						+ (int) Math.ceil(lang.getConfidence() * 100) + "%";
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Chưa rõ";
		}
	}

	public void map() {
		countries = new LinkedHashMap<>();
		Scanner file = null;
		try {
			file = new Scanner(new FileReader("countries.txt"));
			String line = "";
			while (file.hasNext()) {
				line = file.nextLine();
				countries.put(line.split("[,]")[0], line.split("[,]")[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (file != null)
				file.close();
		}
	}

	public String getCodeLanguages() {
		String res = "Danh sách mã ngôn ngữ\n";
		for (String str : countries.keySet()) {
			res += str + "->" + countries.get(str) + "\n";
		}
		return res;
	}
}
