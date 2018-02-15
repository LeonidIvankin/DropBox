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

	public String getName(){
		return name;
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
				signIn(body);
				break;
			case Constant.DOWNLOAD:
				downloadFile(body);
				break;
			case Constant.UPLOAD:
				uploadFile(body);
				break;
			case Constant.RELOAD:
				sendPacket(Constant.FILE_LIST, getFiles(this.name));
				break;
			case Constant.SIGNUP:
				signUp(body);
				break;
		}
	}

	public void signIn(Object body){//авторизация
		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loggedIntoAccount = server.checkLoginAndPass(name, pass);
		if(loggedIntoAccount){ // если пользователь указал правильные логин/пароль
			if(!server.isAccountBusy(name)){
				sendPacket(Constant.AUTHOK, null);
				this.name = name;
				String[] files = getFiles(this.name);
				if(files.length != 0){
					sendPacket(Constant.FILE_LIST, getFiles(this.name));
					sendMessage(this.name + ", ваши файлы");
				}else sendMessage(this.name + ", ваша папка пока пуста");
				isAuthorized = true;
			}else sendMessage("Учетная запись уже используется");
		}else sendMessage("Не верные логин/пароль");
	}

	public void signUp(Object body){//регистрация
		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loginIsReserved = server.checkLogin(name);
		if(name.equals("") && pass.equals("")){
			sendMessage("Введите логин и пароль и повторите");
		}else if(name.equals("")){
			sendMessage("Введите логин");
		}else{
			if(loginIsReserved){
				sendMessage("Логин занят");
			}else if(pass.equals("")){
				sendMessage("Логин свободен. Введите пароль и повторите");
			}else {
				System.out.println(server.setLoginAndPass(name, pass));
				makeDir(name);
				sendMessage("Вы успешно зарегистрированы");
				signIn(body);
			}
		}
	}

	public void downloadFile(Object body){//скачать файл с сервера
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

	public void uploadFile(Object body) {//загрузить файл на сервер
		Object[] uploadFile = (Object[]) body;
		String fileName = (String) uploadFile[0];
		System.out.println(fileName);
		barr = (byte[]) uploadFile[1];
		filePath = new File(Constant.SERVER_ROOT + name + "\\" + fileName);

		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), Constant.BUFFER_SIZE)){
			out.write(barr);
		}catch (Exception e1){
			e1.printStackTrace();
		}
		sendPacket(Constant.FILE_LIST, getFiles(this.name));
	}

	public void makeDir(String name){//создание каталога на сервере
		File file = new File(Constant.SERVER_ROOT + "\\" + name);
		file.mkdir();
	}


}
