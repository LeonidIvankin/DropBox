package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
	private Server server = null;
	private Socket socket = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String name;
	private final String SERVER_ROOT = "Server\\src\\files\\";
	String nick;
	public String getName(){
		return name;
	}

	public ClientHandler(Socket socket, Server server){
		try {
			this.server = server;
			this.socket = socket;
			name = "undefined";
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new Thread(() -> {
				try {
					while(true){
						while(true){
							Object request = in.readObject();
							if (request instanceof String){
								String msg = (String) request;
								if(msg.startsWith("/auth")){
									String[] elements  = msg.split(" ");
									nick = server.getAuthService().getNickByLoginPass(elements[1], elements[2]);
									System.out.println(nick);
									if(nick != null){ // если пользователь указал правильные логин/пароль
										if(!server.isNickBusy(nick)){
											sendMessage("/authok " + nick);
											this.name = nick;
											sendMessage(this.name + ", ваши файлы");
											break;
										}else sendMessage("Учетная запись уже используется");
									}else sendMessage("Не верные логин/пароль");
								}else sendMessage("Для начала надо авторизоваться!");
							}
						}
						while(true) {
							Object request = in.readObject();
							if (request instanceof String){
								String msg = (String) request;
								System.out.println("client: " + msg);
								if (msg.startsWith("/")) {
									if (msg.equalsIgnoreCase("/end")) break;
									else if(msg.equals("/show")){
										sendObject(getFiles(nick));


									} else sendMessage("Такой команды нет!");
								} else {
									server.broadcast(this.name + " " + msg);
								}
							}
						}
					}
				} catch (Exception e){
					e.printStackTrace();
				}
		}).start();

	}

	public String[] getFiles(String name) {
		File folder = new File(SERVER_ROOT + name);
		return folder.list();
	}

	public void sendObject(Object obj){
		try{
			out.writeObject(obj);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}



}
