package Server.Tools.translator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

import Common.Config;
import Server.Server;

public class LanguageTranslator {

	public TranslationResponse getResult(String q) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
				.header("content-type", "application/x-www-form-urlencoded")
				.header("accept-encoding", "application/gzip").header("x-rapidapi-key", Config.X_RapidAPI_Key)
				.header("x-rapidapi-host", "google-translate1.p.rapidapi.com")
				.method("POST", HttpRequest.BodyPublishers.ofString("q=" + q)).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		Gson json = new Gson();
		return json.fromJson(response.body(), TranslationResponse.class);
	}

	public TranslationResponse getResultWithSource(String q, String from, String to) {
		String request = q + "&source=" + from + "&target=" + to;
		try {
			return getResult(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public TranslationResponse getResultWithoutSource(String q, String to) {
		String request = q + "&target=" + to;
		try {
			return getResult(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String process(String command) {
		String[] str = command.split("[#]");
		TranslationResponse result;
		switch (str.length) {
		case 3:
			if (str[2].trim().length() < 1)
				return "Không có gì để dịch";
			else
				result = getResultWithSource(str[2], str[0], str[1]);
			break;
		case 2:
			if (str[1].trim().length() < 1)
				return "Không có gì để dịch";
			else
				result = getResultWithoutSource(str[1], str[0]);
			break;
		case 1:
			result = getResultWithoutSource(str[0], "vi");
			break;
		default:
			return "Chưa đúng cú pháp";
		}

		if (result == null)
			return "Không tìm thấy kết quả";
		else {
			if (result.error != null) {
				if (result.getMessage().equals("Invalid Value")) {
					return "Ngôn ngữ không tồn tại";
				} else
					return result.getMessage();
			} else {
				if (str.length == 2) {
					return "Phát hiện ngôn ngữ: " + Server.detector.countries.get(result.getDetectedSourceLanguage())
							+ " (" + result.getDetectedSourceLanguage() + ")\n" + "Dịch: " + result.getTranslatedText();
				} else {
					return result.getTranslatedText();
				}
			}
		}
	}

}
