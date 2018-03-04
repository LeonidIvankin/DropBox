package server;

import common.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class ClientHandler {

	private Server server = null;
	protected Socket socket = null;
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

	public String[] getListSortedElements(String path) {//получение списка файлов на сервере. Сортировка каталогов и файлов
		File folder = new File(path);
		ArrayList<String> arrayList = new ArrayList<>();
		if (!dirCurrent.equals(dirRootClient)) {
			arrayList.add(Constant.ROOT_DIR);
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

	public String[] getListElements(String path){
		File folder = new File(path);
		return folder.list();
	}

	public boolean isFreeName(String name){
		for (String nameInList : getListElements(dirCurrent)) {
			if(name.equals(nameInList)) return false;
		}
		return true;
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
		if(!isFreeName(newFileName)) {
			newFileName = WorkWithString.prefixBusyName(newFileName, getListElements(dirCurrent));//если имя не свободно добавляем (1)
			//sendMessage(Constant.ADD_FILE + newFileName);
			sendMessage(String.format(Locale.getDefault(), "%s%s", Constant.ADD_FILE, newFileName));
		}
		File newFile = new File(WorkWithString.concatenationPath(dirCurrent, newFileName));
		try {
			boolean created = newFile.createNewFile();
			if (created) {
				reload(dirCurrent);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void createNewDir(Object body) {
		String dirName = (String) body;
		if(!isFreeName(dirName)){
			dirName = WorkWithString.prefixBusyName(dirName, getListElements(dirCurrent));//если имя не свободно добавляем (1)
			sendMessage(Constant.ADD_DIR + dirName);
		}
		makeDir(dirName);
		reload(dirCurrent);
	}

	public void rename(Object body) {//переименовать файл и каталог
		Object[] objects = (Object[]) body;
		String nameOld = (String) objects[0];
		String nameNew = (String) objects[1];
		if(!isFreeName(nameNew)){
			nameNew = WorkWithString.prefixBusyName(nameNew, getListElements(dirCurrent));//если имя не свободно добавляем (1)
			//sendMessage(Constant.NEW_NAME + nameNew);
			sendMessage(String.format(Locale.getDefault(), "%s%s", Constant.NEW_NAME, nameNew));
		}

		File fileOld = new File(WorkWithString.concatenationPath(dirCurrent, nameOld));
		File fileNew = new File(WorkWithString.concatenationPath(dirCurrent, nameNew));
		fileOld.renameTo(fileNew);
		reload(dirCurrent);
	}

	public void downloadFile(Object body) {//скачать файл с сервера
		String path = (String) body;
		File filePath = new File(WorkWithString.concatenationPath(dirRootClient, path));
		body = readAndWriteElement.readElement(filePath);
		workWithPacket.sendPacket(Constant.DOWNLOAD, body);
	}

	public void uploadFile(Object object) {//загрузить файл на сервер
		Object[] body = (Object[]) object;
		String elementName = (String) body[0];

		if(!isFreeName(elementName)){
			elementName = WorkWithString.prefixBusyName(elementName, getListElements(dirCurrent));//если имя не свободно добавляем (1)
			//sendMessage(Constant.NAME_CHANGED_TO + elementName);
			sendMessage(String.format(Locale.getDefault(), "%s%s", Constant.NAME_CHANGED_TO, elementName));
		}
		Object data = body[1];
		readAndWriteElement.writeElement(data, new File(WorkWithString.concatenationPath(dirCurrent, elementName)));
		reload(dirCurrent);
	}

	public void makeDir(String nameDir) {//создание каталога на сервере
		File file = new File(WorkWithString.concatenationPath(dirCurrent, nameDir));
		file.mkdir();
	}

	public void delete(Object body) {
		String fileName = (String) body;
		fileName = WorkWithString.withoutBrackets(fileName);
		File file = new File(WorkWithString.concatenationPath(dirCurrent, fileName));
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
		String[] files = getListSortedElements(path);
		workWithPacket.sendPacket(Constant.FILE_LIST, files);
	}

	public void moveOnTree(Object body) {
		String dir = (String) body;

		if (dir.equals(Constant.ROOT_DIR)) {
			dirCurrent = WorkWithString.separation(dirCurrent);
			reload(dirCurrent);
		} else if (dir.charAt(0) == Constant.OPEN_BRACKET && dir.charAt(dir.length() - 1) == Constant.CLOSE_BRACKET) {
			dir = dir.substring(1, dir.length() - 1);
			dirCurrent = WorkWithString.concatenationPath(dirCurrent, dir);
			reload(dirCurrent);
		}
	}

	public void loginToAccount(String login) {
		this.login = login;

		dirRootClient = WorkWithString.concatenationPath(Constant.SERVER_ROOT, login);
		dirCurrent = dirRootClient;
		String[] files = getListSortedElements(dirCurrent);
		if (files.length != 0) {
			reload(dirCurrent);
		//} else sendMessage(login + Constant.YOUR_FOLDER_IS_EMPTY);
		} else sendMessage(String.format(Locale.getDefault(), "%s%s", login, Constant.YOUR_FOLDER_IS_EMPTY));
	}
}
