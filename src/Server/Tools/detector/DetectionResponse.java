package Server.Tools.detector;

public class DetectionResponse {

	public data data;

	public String getLanguage() {
		return data.detections[0][0].language;
	}

	public float getConfidence() {
		return data.detections[0][0].confidence;
	}

	public class data {
		public item[][] detections;
	}

	public class item {
		public String language;
		public float confidence;
		public boolean isReliable;
	}

}
