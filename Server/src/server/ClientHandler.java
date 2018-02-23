package server;

import common.*;

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
	private File folder;
	private byte[] barr;
	private String clientDir;
	private String clientAbsolutePath = null;

	private Authorization authorization;
	private WorkWithPacket workWithPacket;
	private ObjectStream objectStream;
	private WorkWithString stringManipulation;

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

		workWithPacket = new WorkWithPacket(out);

		server.executorService.submit(() -> {
			try {
				while (true) {
					takePacket(in.readObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		authorization = new Authorization(this, workWithPacket);
		stringManipulation = new WorkWithString();

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
				reload(clientAbsolutePath);
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
			case Constant.MOVE:
				moveOnTree(body);
				break;

		}
	}

	public String getName() {
		return name;
	}

	public String[] getListFiles(String path) {//получение списка файлов на сервере. Сортировка каталогов и файлов
		folder = new File(path);
		ArrayList<String> arrayList = new ArrayList<>();
		if(!clientAbsolutePath.equals(clientDir)){
			arrayList.add("[..]");
		}
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
		workWithPacket.sendPacket(Constant.TEXT_MESSAGE, msg);
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
				reload(clientAbsolutePath);
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
		reload(clientAbsolutePath);
	}

	public void downloadFile(Object body) {//скачать файл с сервера
		String path = (String) body;
		filePath = new File(concatenation(clientDir, path));//откуда файл скачать с сервера
		barr = objectStream.readFile(filePath);
		workWithPacket.sendPacket(Constant.DOWNLOAD, barr);
	}

	public void uploadFile(Object body) {//загрузить файл на сервер
		Object[] uploadFile = (Object[]) body;
		String fileName = (String) uploadFile[0];
		barr = (byte[]) uploadFile[1];
		filePath = new File(concatenation(clientDir, fileName));
		objectStream.writeFile(barr, filePath);
		reload(clientAbsolutePath);
	}

	public void makeDir(String name) {//создание каталога на сервере
		File file = new File(Constant.SERVER_ROOT  + name);
		file.mkdir();
	}

	public void delete(Object body) {
		String fileName = (String) body;
		File file = new File(concatenation(clientDir, fileName));
		file.delete();
		reload(clientAbsolutePath);
		sendMessage("Файл " + fileName + " удалён");
	}

	public void reload(String path){
		workWithPacket.sendPacket(Constant.FILE_LIST, getListFiles(path));
	}

	public void moveOnTree(Object body){
		String dir = (String) body;

		if(dir.equals("[..]")){
			clientAbsolutePath = separation(clientAbsolutePath);
			reload(clientAbsolutePath);
		} else if(dir.charAt(0) == '[' && dir.charAt(dir.length() - 1) == ']'){
			dir = dir.substring(1, dir.length() - 1);
			clientAbsolutePath = concatenation(clientAbsolutePath, dir);
			reload(clientAbsolutePath);
		}
	}

	public void loginToAccount(String name){
		this.name = name;

		clientDir = concatenation(Constant.SERVER_ROOT, name);
		clientAbsolutePath = clientDir;
		String[] files = getListFiles(clientAbsolutePath);
		if (files.length != 0) {
			reload(clientAbsolutePath);
		} else sendMessage(name + ", ваша папка пока пуста");
	}

	public String concatenation(String ... strs){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if(i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}

	public String separation(String str){
		int x = str.lastIndexOf("\\");
		return str.substring(0, x);
	}
}
