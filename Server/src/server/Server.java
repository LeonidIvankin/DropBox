package server;

import db.DBService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Server {
	private final int PORT = 8888;
	private final int MAX_POOL_SIZE = 100;
	private CopyOnWriteArrayList<ClientHandler> clients;
	private DBService dbService;
	public ExecutorService executorService;


	public Server(){
		ServerSocket serverSocket = null;
		Socket socket = null;
		clients = new CopyOnWriteArrayList<>();
		final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
		executorService = new ThreadPoolExecutor(MAX_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, queue);
		//executorService = Executors.newCachedThreadPool();

		try {
			serverSocket = new ServerSocket(PORT);

			dbService = new DBService();
			dbService.connect();


			System.out.println("Сервер запущен, ждем клиентов");
			while(true){
				socket = serverSocket.accept();
				clients.add(new ClientHandler(socket, this));
				System.out.println("Клиент подключился");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				dbService.disconnect();
				serverSocket.close();
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public boolean isAccountBusy(String name){
		for(ClientHandler c: clients){
			if(c.getName().equals(name)) return true;
		}
		return false;
	}

	public boolean checkLoginAndPass(String name, String pass){
		return dbService.getData(name, pass);
	}



}


