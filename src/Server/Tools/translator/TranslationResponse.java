package Server.Tools.translator;

public class TranslationResponse {
	public data data;
	public error error;

	public String getMessage() {
		return error.message;
	}

	public String getTranslatedText() {
		return data.translations[0].translatedText;
	}

	public String getDetectedSourceLanguage() {
		return data.translations[0].detectedSourceLanguage;
	}

	public class data {
		public item[] translations;
	}

	public class item {
		public String translatedText;
		public String detectedSourceLanguage;
	}

	public class error {
		public int code;
		public String message;
	}
}
