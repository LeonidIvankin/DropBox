package server;

import common.Constant;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
	private Server server = null;
	private Socket socket = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String name;
	private File filePath;
	private byte[] barr;

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

		server.executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						while(true){
							Object request = in.readObject();
							if (request instanceof String){
								String msg = (String) request;
								if(msg.startsWith("/auth")){
									String[] elements  = msg.split(" ");
									String name1 = elements[1];
									String pass = elements[2];
									System.out.println();
									boolean loggedIntoAccount = server.checkLoginAndPass(name1, pass);
									if(loggedIntoAccount){ // если пользователь указал правильные логин/пароль
										if(!server.isAccountBusy(name1)){
											sendMessage("/authok " + name1);
											name = name1;
											sendObject(Constant.FILE_LIST, getFiles(name));
											sendMessage(name + ", ваши файлы");
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
									if (msg.equalsIgnoreCase(Constant.END)) break;
									else sendMessage("Такой команды нет!");
								} else {
									sendMessage(name + " " + msg);
								}
							} else if(request instanceof String[]){
								String[] msg = (String[]) request;
								if(msg[0].equals(Constant.DOWNLOAD)){
									filePath = new File(Constant.SERVER_ROOT + name + "\\" + msg[1]);//откуда файл скачать с сервера
									try (InputStream in = new BufferedInputStream(new FileInputStream(filePath), Constant.BUFFER_SIZE)){
										barr = new byte[Constant.BUFFER_SIZE];
										in.read(barr);
										sendObject(Constant.DOWNLOAD, barr);
									}catch (Exception e1){
										e1.printStackTrace();
									}
								}else sendMessage("Такой команды нет!");
							}
						}
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public String[] getFiles(String name) {
		File folder = new File(Constant.SERVER_ROOT + name);
		return folder.list();
	}

	public void sendObject(Object ... obj){
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
