
package client;

import common.Constant;
import common.ReadAndWriteElement;
import common.WorkWithPacket;
import common.WorkWithString;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


class Control {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private File pathAbsoluteElementSave;
	private boolean isAuthorized = false;

	private Client client;
	private WorkWithPacket workWithPacket;
	private ReadAndWriteElement readAndWriteElement;
	private boolean isConnect = true;


	public Control(Client client) {
		start();
		this.client = client;
		workWithPacket = new WorkWithPacket(out);
		readAndWriteElement = new ReadAndWriteElement();

		//для debug
		String[] strings = {"leo", "1111"};
		workWithPacket.sendPacket(Constant.SIGNIN, strings);


		listenerSignIn();
		listenerSignUp();
		listenerDownload();
		listenerUpload();
		listenerReload();
		listenerDelete();
		listenerMakeDir();
		listenerRename();
		listenerCreateNewFile();
		listenerEnd();
		listenerExit();

		setAuthorized(false);
	}

	public void start() {
		try {
			socket = new Socket(Constant.SERVER_IP, Constant.PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
				while (isConnect) {
					takePacket(in.readObject());
				}
				System.out.println(isConnect);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void move(String str) {
		workWithPacket.sendPacket(Constant.MOVE, str);
	}

	public void listenerEnd() {
		client.addWindowListener(new WindowAdapter() { //отвечает за закрытие соединения при закрытии окна через крестик
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				isConnect = false;
				workWithPacket.sendPacket(Constant.END, null);
			}
		});
	}

	public void listenerExit() {
		client.jbExit.addActionListener(e -> {
			workWithPacket.sendPacket(Constant.EXIT, null);
			setAuthorized(false);
			client.defaultListModel.clear();

		});
	}

	private void listenerCreateNewFile() {
		client.jbCreateNewFile.addActionListener(e -> {
			String nameNewFile = (String) JOptionPane.showInputDialog(client,
					"Введите имя нового файла",
					"Создание нового файла",
					JOptionPane.QUESTION_MESSAGE,
					null, null, "NewFile.txt");

			if (nameNewFile != null) {
				workWithPacket.sendPacket(Constant.NEW_FILE, nameNewFile);
			}
		});
	}

	private void listenerRename() {//переименование
		client.jbRename.addActionListener(e -> {
			Object selectedElement;
			if ((selectedElement = client.list.getSelectedValue()) != null) {
				String nameOld = selectedElement.toString();
				nameOld = WorkWithString.withoutBrackets(nameOld);
				String nameNew = (String) JOptionPane.showInputDialog(client,
						"Введите новое имя",
						"Переименование",
						JOptionPane.QUESTION_MESSAGE,
						null, null, nameOld);
				System.out.println(nameNew);
				if (nameNew != null) {
					String[] body = {nameOld, nameNew};
					workWithPacket.sendPacket(Constant.RENAME, body);
				}
			}
		});
	}

	private void listenerMakeDir() {//создание каталога
		client.jbCreateNewDir.addActionListener(e -> {
			String str = JOptionPane.showInputDialog(client,
					new String[]{"Введите наименование каталога"},
					"Создание нового каталога",
					JOptionPane.WARNING_MESSAGE);
			if (str != null) {
				workWithPacket.sendPacket(Constant.MAKE_DIR, str);
			}
		});
	}

	public void listenerDownload() {//скачать файл с сервера
		client.jbDownload.addActionListener(e -> {
			Object elementSelected;
			if ((elementSelected = client.list.getSelectedValue()) != null) {//выделенный элемент в листе
				String elementSelectedString = elementSelected.toString();//наименование файла по умолчанию
				elementSelectedString = WorkWithString.withoutBrackets(elementSelectedString);//наименование файла по умолчанию
				File defaultPath = new File(Constant.DEFAULT_DOWNLOAD_DIR);//путь по умолчанию
				JFileChooser jFileChooser = new JFileChooser(defaultPath);
				jFileChooser.setSelectedFile(new File(elementSelectedString));
				jFileChooser.setDialogTitle("Save a File");
				int result = jFileChooser.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					pathAbsoluteElementSave = jFileChooser.getSelectedFile();//куда сохранять
					workWithPacket.sendPacket(Constant.DOWNLOAD, elementSelectedString);//какой файл выбрали в списке
				}
			}

		});
	}

	public void listenerUpload() {//закачать файл на сервер
		client.jbUpload.addActionListener(e -> {
			File defaultPath = new File(Constant.DEFAULT_DOWNLOAD_DIR);//путь по умолчанию
			JFileChooser jFileChooser = new JFileChooser(defaultPath);
			jFileChooser.setDialogTitle("Open a File");
			jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int result = jFileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jFileChooser.getSelectedFile();//выделенный файл
				Object object = readAndWriteElement.readElement(selectedFile);
				Object[] body = {selectedFile.getName(), object};

				workWithPacket.sendPacket(Constant.UPLOAD, body);
			}
		});
	}

	public void listenerReload() {//обновить список файлов
		client.jbReload.addActionListener(e -> workWithPacket.sendPacket(Constant.RELOAD, null));
	}

	public void listenerSignIn() {//авторизоваться
		client.jbSignIn.addActionListener(e -> {
			if (socket == null || socket.isClosed()) start();
			Object[] objects = {client.jtfLogin.getText(), client.jtfPassword.getText()};
			workWithPacket.sendPacket(Constant.SIGNIN, objects);
			client.jtfLogin.setText("");
			client.jtfPassword.setText("");
		});
	}

	public void listenerSignUp() {//зарегистрироваться
		client.jbSignUp.addActionListener(e -> {
			if (socket == null || socket.isClosed()) start();
			Object[] objects = {client.jtfLogin.getText(), client.jtfPassword.getText()};
			workWithPacket.sendPacket(Constant.SIGNUP, objects);
			client.jtfLogin.setText("");
			client.jtfPassword.setText("");
		});
	}

	private void listenerDelete() {//удаление файла
		client.jbDelete.addActionListener(e -> {
			List elementSelectedList;
			if ((elementSelectedList = client.list.getSelectedValuesList()) != null) {
				int result = JOptionPane.showConfirmDialog(client,
						"Вы уверены",
						"Окно подтверждения",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (result == 0) {
					for (Object elementSelected  : elementSelectedList) {
						workWithPacket.sendPacket(Constant.DELETE, elementSelected.toString());
					}

				}
			}
		});
	}

	public void setAuthorized(boolean authorized) { //скрываем панели для авторизованых и неавторизованых пользователей
		isAuthorized = authorized;
		client.topPanel.setVisible(!isAuthorized);
		client.rightPanel.setVisible(isAuthorized);
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
		readAndWriteElement.writeElement(body, pathAbsoluteElementSave);
	}
}