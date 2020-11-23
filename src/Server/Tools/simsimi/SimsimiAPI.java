package Server.Tools.simsimi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimsimiAPI {
	private String key;

	public SimsimiAPI(String key) {
		this.key = key;
	}

	public String Excute(String text) throws IOException, InterruptedException {
		String requestBody = "{ \"utext\": \"" + text + "\", \"lang\": \"vn\"}";
		System.out.println(requestBody);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://wsapi.simsimi.com/190410/talk"))
				.header("Content-type", "application/json").header("accept", "application/json")
				.header("x-api-key", this.key).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		ObjectMapper mapper = new ObjectMapper();
		SimsimiResponse simsimi = mapper.readValue(response.body(), new TypeReference<SimsimiResponse>() {
		});

		return simsimi.atext;
	}
}
