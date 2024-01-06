import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {
    private String serverAddress = "localhost";
    private int port = 5999;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Player() {
        try {
            socket = new Socket(serverAddress, port);
            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStream);
            writer = new PrintWriter(socket.getOutputStream());
            this.start();
            takeInput();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void takeInput() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String move;
            while ((move = br.readLine()) != null) {
                sendDataToServer(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDataToServer(String data) {
        writer.println(data);
        writer.flush();
    }

    @Override
    public void run() {
        try {
            String stream;
            while ((stream = reader.readLine()) != null) {
                System.out.println(stream); // Server response
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Player();
    }
}
