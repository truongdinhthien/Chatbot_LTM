package Server.Tools.weather;

public class Weather {
	private String title;
	private String time;
	private String dubao;
	private String nhietdo;
	private String mua;
	private String khiap;
	private String gio;

	public Weather(String time, String dubao, String nhietdo, String mua, String khiap, String gio) {
		this.time = time;
		this.dubao = dubao;
		this.nhietdo = nhietdo;
		this.mua = mua;
		this.khiap = khiap;
		this.gio = gio;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDubao() {
		return dubao;
	}

	public void setDubao(String dubao) {
		this.dubao = dubao;
	}

	public String getNhietdo() {
		return nhietdo;
	}

	public void setNhietdo(String nhietdo) {
		this.nhietdo = nhietdo;
	}

	public String getMua() {
		return mua;
	}

	public void setMua(String mua) {
		this.mua = mua;
	}

	public String getKhiap() {
		return khiap;
	}

	public void setKhiap(String khiap) {
		this.khiap = khiap;
	}

	public String getGio() {
		return gio;
	}

	public void setGio(String gio) {
		this.gio = gio;
	}

	public String getInfo() {
		while (time.length() < 16) {
			time += " ";
		}
		while (dubao.length() < 14) {
			dubao += " ";
		}
		while (nhietdo.length() < 15) {
			nhietdo += " ";
		}
		if (mua.equals("0 mm"))
			mua = "0.0 mm";
		while (mua.length() < 10) {
			mua += " ";
		}
		while (khiap.length() < 18) {
			khiap += " ";
		}
		String str = time + dubao + "\t" + nhietdo + mua + khiap + gio;
		return str;
	}
}
