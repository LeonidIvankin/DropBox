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
	private boolean isAuthorized = false;

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

		server.executorService.submit(() -> {
			try {
				while(true) {
					takePacket(in.readObject());
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		});
	}

	public String[] getFiles(String name) {//получение списка файлов на сервере
		File folder = new File(Constant.SERVER_ROOT + name);
		return folder.list();
	}

	public void sendMessage(String msg){//послать текстовое сообщение
		sendPacket(Constant.TEXT_MESSAGE, msg);
	}

	public void sendPacket(String head, Object body){//принять заголовок, тело и отправить клиенту
		Object[] packet = {head, body};
		try{
			out.writeObject(packet);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void takePacket(Object answer){//принять сообщение в виде массива Object
		if(answer instanceof Object[]){
			Object[] packet = (Object[]) answer;
			String head = (String) packet[0];
			Object body = packet[1];
			checkHead(head, body);
		}

	}

	public void checkHead(String head, Object body){//в зависимости от head, сделать с body
		switch (head){
			case Constant.AUTH:
				authorization(body);
				break;
			case Constant.DOWNLOAD:
				downloadFile(body);
				break;
		}
	}

	public void authorization(Object body){//проверка логина и пароля
		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loggedIntoAccount = server.checkLoginAndPass(name, pass);
		System.out.println(loggedIntoAccount);
		if(loggedIntoAccount){ // если пользователь указал правильные логин/пароль
			if(!server.isAccountBusy(name)){
				sendPacket(Constant.AUTHOK, null);
				this.name = name;
				sendPacket(Constant.FILE_LIST, getFiles(this.name));
				sendMessage(this.name + ", ваши файлы");
				isAuthorized = true;
			}else sendMessage("Учетная запись уже используется");
		}else sendMessage("Не верные логин/пароль");
	}

	public void downloadFile(Object body){
		String path = (String) body;
		filePath = new File(Constant.SERVER_ROOT + name + "\\" + path);//откуда файл скачать с сервера
		try (InputStream in = new BufferedInputStream(new FileInputStream(filePath), Constant.BUFFER_SIZE)){
			barr = new byte[Constant.BUFFER_SIZE];
			in.read(barr);
			sendPacket(Constant.DOWNLOAD, barr);
		}catch (Exception e1){
			e1.printStackTrace();
		}
	}
}
