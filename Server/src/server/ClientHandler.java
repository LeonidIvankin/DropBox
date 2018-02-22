package server;

import common.Authorization;
import common.Constant;
import common.ObjectStream;
import common.SendTakePacket;

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

	private Authorization authorization;
	private SendTakePacket sendTakePacket;
	private ObjectStream objectStream;

	public ClientHandler(Socket socket, Server server) {
		objectStream = new ObjectStream();
		try {
			this.server = server;
			this.socket = socket;
			name = "undefined";
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		sendTakePacket = new SendTakePacket(out);

		server.executorService.submit(() -> {
			try {
				while (true) {
					takePacket(in.readObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		authorization = new Authorization(this, sendTakePacket);
	}

	public Server getServer() {
		return server;
	}

	public void checkHead(String head, Object body) {//в зависимости от head, сделать с body
		switch (head) {
			case Constant.SIGNIN:
				authorization.signIn(body);
				break;
			case Constant.DOWNLOAD:
				downloadFile(body);
				break;
			case Constant.UPLOAD:
				uploadFile(body);
				break;
			case Constant.RELOAD:
				reload();
				break;
			case Constant.SIGNUP:
				authorization.signUp(body);
				break;
			case Constant.DELETE:
				delete(body);
				break;
			case Constant.MAKE_DIR:
				makeDir(name + "\\" + body);
				reload();
				break;
			case Constant.RENAME:
				rename(body);
				break;
			case Constant.NEW_FILE:
				createNewFile(body);
				break;

		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String[] getFiles(String name) {//получение списка файлов на сервере
		File folder = new File(Constant.SERVER_ROOT + name);
		return folder.list();
	}

	public void sendMessage(String msg) {//послать текстовое сообщение
		sendTakePacket.sendPacket(Constant.TEXT_MESSAGE, msg);
	}

	public void takePacket(Object answer) {//принять объект
		if (answer instanceof Object[]) {
			Object[] packet = (Object[]) answer;
			String head = (String) packet[0];
			Object body = packet[1];
			checkHead(head, body);
		}

	}

	private void createNewFile(Object body) {//создать новый файл
		String newFileName = (String) body;
		File newFile = new File(Constant.SERVER_ROOT + "\\" + this.name + "\\" + newFileName);
		try {
			boolean created = newFile.createNewFile();
			if (created)
				reload();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void rename(Object body) {//переименовать файл и каталог
		Object[] objects = (Object[]) body;
		String nameOld = (String) objects[0];
		String nameNew = (String) objects[1];

		File fileOld = new File(Constant.SERVER_ROOT  + this.name + "\\" + nameOld);
		File fileNew = new File(Constant.SERVER_ROOT  + this.name + "\\" + nameNew);
		fileOld.renameTo(fileNew);
		reload();
	}

	public void downloadFile(Object body) {//скачать файл с сервера
		String path = (String) body;
		filePath = new File(Constant.SERVER_ROOT + name + "\\" + path);//откуда файл скачать с сервера
		barr = objectStream.readFile(filePath);
		sendTakePacket.sendPacket(Constant.DOWNLOAD, barr);
	}

	public void uploadFile(Object body) {//загрузить файл на сервер
		Object[] uploadFile = (Object[]) body;
		String fileName = (String) uploadFile[0];
		barr = (byte[]) uploadFile[1];
		filePath = new File(Constant.SERVER_ROOT + name + "\\" + fileName);


		objectStream.writeFile(barr, filePath);
		reload();
	}

	public void makeDir(String name) {//создание каталога на сервере
		File file = new File(Constant.SERVER_ROOT  + name);
		file.mkdir();
	}

	public void delete(Object body) {
		File file = new File(Constant.SERVER_ROOT  + name + "\\" + body);
		file.delete();
		reload();
	}

	public void reload(){
		sendTakePacket.sendPacket(Constant.FILE_LIST, getFiles(this.name));
	}
}
