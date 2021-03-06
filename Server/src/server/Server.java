package server;

import common.Constant;
import db.DBService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Server {
	private CopyOnWriteArrayList<ClientHandler> clients;
	private DBService dbService;
	public ExecutorService executorService;


	public Server() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		clients = new CopyOnWriteArrayList<>();
		executorService = new ThreadPoolExecutor(Constant.MAX_NUMBER_CLIENTS,
				Constant.MAX_NUMBER_CLIENTS,
				0L,
				TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<>(100));

		try {
			serverSocket = new ServerSocket(Constant.PORT);

			dbService = new DBService();
			dbService.connect();


			System.out.println(Constant.SERVER_IS_RUNNING);
			while (true) {
				socket = serverSocket.accept();
				clients.add(new ClientHandler(socket, this));
				System.out.println(Constant.CLIENT_CONNECTED);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dbService.disconnect();
				serverSocket.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isAccountBusy(String login) {
		for (ClientHandler c : clients) {
			if (c.getLogin().equals(login)) return true;
		}
		return false;
	}

	public boolean checkLoginAndPass(String login, String pass) {
		return dbService.checkLoginAndPass(login, pass);
	}

	public boolean checkLogin(String login) {
		return dbService.checkLogin(login);
	}

	public boolean setLoginAndPass(String login, String pass) {
		return dbService.addData(login, pass);
	}

	public void exit(ClientHandler clientHandler) {
		clients.remove(clientHandler);
		System.out.println(Constant.CLIENT_DISCONNECTED);
	}
}


