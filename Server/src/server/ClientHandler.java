package server;

import common.Authorization;
import common.Constant;
import common.ObjectStream;
import common.SendTakePacket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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

	private String clientDir;

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
				createNewDir(body);
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

	public void setClientDir(String clientDir) {
		this.clientDir = clientDir;
	}

	public void setName(String name){
		this.name = name;
	}

	public String[] getFiles() {//получение списка файлов на сервере. Сортировка каталогов и файлов
		File folder = new File(clientDir);
		ArrayList<String> arrayList = new ArrayList<>();
		for (File file : folder.listFiles()) {
			if(file.isDirectory()) {
				arrayList.add("[" + file.getName() + "]");
			}
		}
		for (File file : folder.listFiles()) {
			if(file.isFile()) arrayList.add(file.getName());
		}

		return arrayList.toArray(new String[arrayList.size()]);
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

	public void createNewFile(Object body) {//создать новый файл
		String newFileName = (String) body;
		//File newFile = new File(clientDir + "\\" + newFileName);
		File newFile = new File(concatenation(clientDir, newFileName));
		try {
			boolean created = newFile.createNewFile();
			if (created){
				reload();
				sendMessage("Создан новый файл " + newFileName);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void createNewDir(Object body){
		String dirName = (String) body;
		makeDir(concatenation(name, dirName));
		sendMessage("Создана новая папка " + dirName);
	}

	public void rename(Object body) {//переименовать файл и каталог
		Object[] objects = (Object[]) body;
		String nameOld = (String) objects[0];
		String nameNew = (String) objects[1];

		File fileOld = new File(concatenation(clientDir, nameOld));
		File fileNew = new File(concatenation(clientDir, nameNew));
		fileOld.renameTo(fileNew);
		reload();
	}

	public void downloadFile(Object body) {//скачать файл с сервера
		String path = (String) body;
		filePath = new File(concatenation(clientDir, path));//откуда файл скачать с сервера
		barr = objectStream.readFile(filePath);
		sendTakePacket.sendPacket(Constant.DOWNLOAD, barr);
	}

	public void uploadFile(Object body) {//загрузить файл на сервер
		Object[] uploadFile = (Object[]) body;
		String fileName = (String) uploadFile[0];
		barr = (byte[]) uploadFile[1];
		filePath = new File(concatenation(clientDir, fileName));
		objectStream.writeFile(barr, filePath);
		reload();
	}

	public void makeDir(String name) {//создание каталога на сервере
		File file = new File(Constant.SERVER_ROOT  + name);
		file.mkdir();
	}

	public void delete(Object body) {
		String fileName = (String) body;
		File file = new File(concatenation(clientDir, fileName));
		file.delete();
		reload();
		sendMessage("Файл " + fileName + " удалён");
	}

	public void reload(){
		sendTakePacket.sendPacket(Constant.FILE_LIST, getFiles());
	}

	public String concatenation(String ... strs){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if(i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}
}
