package Server;

import java.io.IOException;

import Common.Config;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server(Config.PORT);
            server.Excute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
