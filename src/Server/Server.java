package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Common.Config;
import Server.Tools.IP;
import Server.Tools.PortScan;
import Server.Tools.detector.LanguageDetector;
import Server.Tools.simsimi.SimsimiAPI;
import Server.Tools.translator.LanguageTranslator;
import Server.Tools.weather.WeatherAPI;

public class Server {

	private int port;
	private ServerSocket server = null;
	public static ThreadPoolExecutor executor;
	public static ArrayBlockingQueue<Runnable> queue;
	public static Vector<Worker> workers = new Vector<Worker>();

	public static IP IPAPI;
	public static PortScan portScan;
	public static SimsimiAPI simsimi;
	public static WeatherAPI weatherAPI;
	public static LanguageDetector detector;
	public static LanguageTranslator tranlator;

	public Server(int port) {
		this.port = port;
	}

	public void Excute() throws IOException, InterruptedException {
		int i = 0;
		IPAPI = new IP();
		portScan = new PortScan();
		weatherAPI = new WeatherAPI();
		detector = new LanguageDetector();
		tranlator = new LanguageTranslator();
		simsimi = new SimsimiAPI(Config.SIMSIMI_KEY);
		queue = new ArrayBlockingQueue<>(Config.queueCapacity);
		executor = new ThreadPoolExecutor(Config.corePoolSize, Config.maxPoolSize, 30, TimeUnit.SECONDS, queue);

		try {
			server = new ServerSocket(port);
			System.out.println("Server binding at port " + port);
			System.out.println("Waiting for client...");
			while (true) {
				i++;
				Socket socket = server.accept();
				Worker client = new Worker(socket, Integer.toString(i));
				workers.add(client);
				executor.execute(client);
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (server != null)
				server.close();
		}
	}
}
