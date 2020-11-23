package Server.Tools.simsimi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimsimiResponse {
	public int status;
	public String statusMessage;
	public String atext;
	public String lang;
}