
package client;

import common.Constant;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;


class Control {
	Client client;
	private byte[] barr;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private File filePath;
	private boolean isAuthorized = false;


	public Control(Client client) {
		this.client = client;

		client.addWindowListener(new WindowAdapter() { //отвечает за закрытие соединения при закрытии окна через крестик
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					out.writeObject(Constant.END);
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					setAuthorized(false);
				}
			}
		});

		listenerSignIn();
		listenerSignUp();
		listenerDownload();
		listenerUpload();
		listenerReload();
		listenerDelete();
		listenerMakeDir();
		listenerRename();
		listenerCreateNewFile();


		start();
		setAuthorized(false);
	}

	private void listenerCreateNewFile() {
		client.jbCreateNewFile.addActionListener(e ->{
			String nameNewFile = (String) JOptionPane.showInputDialog(client,
					"Введите имя нового файла",
					"Создание нового файла",
					JOptionPane.QUESTION_MESSAGE,
					null, null, "NewFile.txt");

			if(nameNewFile != null){
				sendPacket(Constant.NEW_FILE, nameNewFile);
			}
		});
	}

	private void listenerRename() {//переименование
		client.jbRename.addActionListener(e -> {
			if ((client.list.getSelectedValue()) != null) {
				String nameOld = client.list.getSelectedValue().toString();
				String nameNew = (String) JOptionPane.showInputDialog(client,
						"Введите новое имя",
						"Переименование",
						JOptionPane.QUESTION_MESSAGE,
						null, null, nameOld);
				if(nameNew != null){
					String[] body = {nameOld, nameNew};
					sendPacket(Constant.RENAME, body);
				}
			}
		});
	}

	private void listenerMakeDir() {//создание каталога
		client.jbMakeDir.addActionListener(e -> {
			String str = JOptionPane.showInputDialog(client,
					new String[]{"Введите наименование каталога"},
					"Создание нового каталога",
					JOptionPane.WARNING_MESSAGE);
			if(str != null){
				sendPacket(Constant.MAKE_DIR, str);
			}
		});
	}

	public void listenerDownload() {//скачать файл с сервера
		client.jbDownload.addActionListener(e -> {
			Object str;
			if((str = client.list.getSelectedValue()) != null){
				JFileChooser fs = new JFileChooser(new File("c:\\"));
				fs.setSelectedFile(new File(str.toString()));
				fs.setDialogTitle("Save a File");
				int result = fs.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					filePath = fs.getSelectedFile();//куда сохранять файл
					sendPacket(Constant.DOWNLOAD, str.toString());//какой файл выбрали в списке
				}
			}

		});
	}

	public void listenerUpload() {//закачать файл на сервер
		client.jbUpload.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("c:\\Users\\ILM\\Desktop\\"));
			fs.setDialogTitle("Open a File");
			fs.setFileFilter(new FileTypeFilter(".docx", "WordFile"));
			fs.setFileFilter(new FileTypeFilter(".jpg", "JPEG File"));
			fs.setFileFilter(new FileTypeFilter(".txt", "TextFile"));
			int result = fs.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File fi = fs.getSelectedFile();//выделенный файл
				int a = (int) fi.length();
				try (InputStream in = new BufferedInputStream(new FileInputStream(fi.getPath()), a)) {
					barr = new byte[a];
					in.read(barr);
					Object[] uploadFile = {fi.getName(), barr};
					sendPacket(Constant.UPLOAD, uploadFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public void listenerReload() {//обновить список файлов
		client.jbReload.addActionListener(e -> {
			sendPacket(Constant.RELOAD, null);
		});
	}

	public void listenerSignIn() {//авторизоваться
		client.jbSignIn.addActionListener(e -> {
			if (socket == null || socket.isClosed()) start();
			Object[] objects = {client.jtfLogin.getText(), client.jtfPassword.getText()};
			sendPacket(Constant.AUTH, objects);
			client.jtfLogin.setText("");
			client.jtfPassword.setText("");
		});
	}

	public void listenerSignUp() {//зарегистрироваться
		client.jbSignUp.addActionListener(e -> {
			if (socket == null || socket.isClosed()) start();
			Object[] objects = {client.jtfLogin.getText(), client.jtfPassword.getText()};
			sendPacket(Constant.SIGNUP, objects);
			client.jtfLogin.setText("");
			client.jtfPassword.setText("");
		});
	}

	private void listenerDelete() {//удаление файла
		client.jbDelete.addActionListener(e -> {
			if (client.list.getSelectedValue() != null) {
				int result = JOptionPane.showConfirmDialog(client,
						"Вы уверены",
						"Окно подтверждения",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (result == 0) {
					sendPacket(Constant.DELETE, client.list.getSelectedValue().toString());
				}
			}
		});
	}

	public void start() {
		try {
			socket = new Socket(Constant.SERVER_IP, Constant.PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread thread1 = new Thread(() -> {
			try {
				while (true) {
					takePacket(in.readObject());
				}

			} catch (IOException e) {
				e.printStackTrace();
				setAuthorized(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread1.start();
	}

	public void setAuthorized(boolean authorized) { //скрываем панели для авторизованых и неавторизованых пользователей
		isAuthorized = authorized;
		client.topPanel.setVisible(!isAuthorized);
		client.rightPanel.setVisible(isAuthorized);
	}

	public void sendPacket(String head, Object body) {//принять заголовок, тело и отправить на сервер
		Object[] packet = {head, body};
		try {
			out.writeObject(packet);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void takePacket(Object answer) {//принять сообщение в виде массива Object
		if (answer instanceof Object[]) {
			Object[] packet = (Object[]) answer;
			String head = (String) packet[0];
			Object body = packet[1];
			checkHead(head, body);
		}

	}

	public void checkHead(String head, Object body) {//в зависимости от head, сделать с body
		switch (head) {
			case Constant.AUTHOK:
				setAuthorized(true);
				break;
			case Constant.TEXT_MESSAGE:
				showMessageDialog((String) body);
				break;
			case Constant.FILE_LIST:
				showFileList(body);
				break;
			case Constant.DOWNLOAD:
				downloadFile(body);
				break;
		}
	}

	private void showFileList(Object body) {
		client.defaultListModel.clear();
		String[] fileList = (String[]) body;
		for (String fileName : fileList) {
			client.defaultListModel.addElement(fileName);
		}
	}

	public void showMessageDialog(String msg) {//показать у клиента диалоговое окно с сообщением
		JOptionPane.showMessageDialog(null, msg);
	}

	public void downloadFile(Object body) {
		barr = (byte[]) body;
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), barr.length)) {
			out.write(barr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}