package server;

import common.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

	private Server server = null;
	protected Socket socket = null;
	//private ObjectInputStream in;
	private ObjectOutputStream out;
	private String login;
	private String dirRootClient;
	private String dirCurrent = null;
	private boolean isConnect = true;

	private Authorization authorization;
	private WorkWithPacket workWithPacket;
	private ReadAndWriteElement readAndWriteElement;

	public ClientHandler(Socket socket, Server server) {
		readAndWriteElement = new ReadAndWriteElement();

		try {
			this.server = server;
			this.socket = socket;
			login = "undefined";

			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		workWithPacket = new WorkWithPacket(out);

		server.executorService.submit(() -> {

			try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
				while (isConnect) {
					takePacket(in.readObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		authorization = new Authorization(this, workWithPacket);
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
				reload(dirCurrent);
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
			case Constant.END:
				end();
				break;
			case Constant.EXIT:
				server.exit(this);
				break;

		}
	}

	private void end() {
		isConnect = false;
		server.exit(this);
	}

	public Server getServer() {
		return server;
	}

	public String getLogin() {
		return login;
	}

	public String[] getListFiles(String path) {//получение списка файлов на сервере. Сортировка каталогов и файлов
		File folder = new File(path);
		ArrayList<String> arrayList = new ArrayList<>();
		if (!dirCurrent.equals(dirRootClient)) {
			arrayList.add("[..]");
		}
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				arrayList.add(WorkWithString.withBrackets(file.getName()));
			}
		}
		for (File file : folder.listFiles()) {
			if (file.isFile()) arrayList.add(file.getName());
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
		File newFile = new File(WorkWithString.concatenation(dirCurrent, newFileName));
		try {
			boolean created = newFile.createNewFile();
			if (created) {
				reload(dirCurrent);
				//sendMessage("Создан новый файл " + newFileName);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void createNewDir(Object body) {
		String dirName = (String) body;
		makeDir(dirName);
		reload(dirCurrent);
	}

	public void rename(Object body) {//переименовать файл и каталог
		Object[] objects = (Object[]) body;
		String nameOld = (String) objects[0];
		String nameNew = (String) objects[1];

		File fileOld = new File(WorkWithString.concatenation(dirCurrent, nameOld));
		System.out.println(fileOld);
		File fileNew = new File(WorkWithString.concatenation(dirCurrent, nameNew));
		fileOld.renameTo(fileNew);
		reload(dirCurrent);
	}

	public void downloadFile(Object body) {//скачать файл с сервера
		String path = (String) body;
		File filePath = new File(WorkWithString.concatenation(dirRootClient, path));
		body = readAndWriteElement.readElement(filePath);
		workWithPacket.sendPacket(Constant.DOWNLOAD, body);
	}

	public void uploadFile(Object object) {//загрузить файл на сервер
		Object[] body = (Object[]) object;
		String elementName = (String) body[0];
		Object data = body[1];
		//readAndWriteElement.writeElement(data, new File(dirCurrent + "\\" + elementName));
		readAndWriteElement.writeElement(data, new File(WorkWithString.concatenation(dirCurrent, elementName)));
		reload(dirCurrent);
	}

	public void makeDir(String nameDir) {//создание каталога на сервере
		File file = new File(WorkWithString.concatenation(dirCurrent, nameDir));
		file.mkdir();
	}

	public void delete(Object body) {
		String fileName = (String) body;
		fileName = WorkWithString.withoutBrackets(fileName);
		File file = new File(WorkWithString.concatenation(dirCurrent, fileName));
		deleteDir(file);
		reload(dirCurrent);
	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				File f = new File(dir, children[i]);
				deleteDir(f);
			}
			dir.delete();
		} else dir.delete();
	}

	public void reload(String path) {
		String[] files = getListFiles(path);
		workWithPacket.sendPacket(Constant.FILE_LIST, files);
	}

	public void moveOnTree(Object body) {
		String dir = (String) body;

		if (dir.equals("[..]")) {
			dirCurrent = WorkWithString.separation(dirCurrent);
			reload(dirCurrent);
		} else if (dir.charAt(0) == '[' && dir.charAt(dir.length() - 1) == ']') {
			dir = dir.substring(1, dir.length() - 1);
			dirCurrent = WorkWithString.concatenation(dirCurrent, dir);
			reload(dirCurrent);
		}
	}

	public void loginToAccount(String login) {
		this.login = login;

		dirRootClient = WorkWithString.concatenation(Constant.SERVER_ROOT, login);
		dirCurrent = dirRootClient;
		String[] files = getListFiles(dirCurrent);
		if (files.length != 0) {
			reload(dirCurrent);
		} else sendMessage(login + ", ваша папка пока пуста");
	}

}
