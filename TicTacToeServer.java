
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeServer {
	private static List<PrintWriter> clientOutputStreams = new ArrayList<>();
	private static Game game = new Game();

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("Server port needs to be specified!");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		new TicTacToeServer().startServer(port);
	}

	private void startServer(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("TicTacToe Server started on port " + port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				new RequestHandler(clientSocket).start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	static class RequestHandler extends Thread {
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter response;
		private char id;

		public RequestHandler(Socket clientSocket) {
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
				response = new PrintWriter(socket.getOutputStream());
				clientOutputStreams.add(response);
				setClientId();
				sendWelcomeScreen();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void setClientId() {
			this.id = clientOutputStreams.size() == 1 ? Game.CIRCLE : Game.CROSS;
		}

		private void sendWelcomeScreen() {
			writeToClient(response, "**** Welcome to TicTacToe! **** ");
			writeToClient(response, game.getBoard());
			writeToClient(response, "Your symbol is: " + id);
			writeToClient(response, "Move needs to be made by " + Game.CIRCLE);
		}

		@Override
		public void run() {
			try {
				String message;
				while ((message = reader.readLine()) != null) {
					if ("restart".equalsIgnoreCase(message)) {
						game.resetBoard();
						broadCastGame("+++ Game has been restarted! +++\n" + game.getBoard());
						continue;
					}
					processMove(message);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		private void processMove(String message) {
			try {
				int markedPosition = Integer.parseInt(message);
				String result = game.makeMove(id, markedPosition);
				broadCastGame(result);
			} catch (NumberFormatException ex) {
				writeToClient(response, "Invalid Input! Please enter a valid move (e.g., 11, 22).");
			}
		}

		private void broadCastGame(String data) {
			for (PrintWriter client : clientOutputStreams) {
				writeToClient(client, data);
			}
		}

		private void writeToClient(PrintWriter client, String data) {
			client.println(data);
			client.flush();
		}
	}
}
