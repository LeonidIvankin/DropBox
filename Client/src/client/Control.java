
package client;

import common.Constant;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;


class Control{
	Client client;
	private byte[] barr;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private File filePath;
	private boolean isAuthorized = false;


	public Control(Client client){
		this.client = client;

		client.addWindowListener(new WindowAdapter() { //отвечает за закрытие соединения при закрытии окна через крестик
			@Override
			public void windowClosing(WindowEvent e){
				super.windowClosing(e);
				try{
					out.writeObject(Constant.END);
					socket.close();
				}catch(IOException e1){
					e1.printStackTrace();
					setAuthorized(false);
				}
			}
		});

		listenerAuth();
		listenerDownload();
		listenerUpload();
		listenerReload();

		start();
		setAuthorized(false);
	}


	public void listenerDownload(){
		client.jbDownload.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("c:\\"));
			fs.setDialogTitle("Save a File");
			int result = fs.showSaveDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				filePath = fs.getSelectedFile();//куда сохранять файл
				sendPacket(Constant.DOWNLOAD, client.list.getSelectedValue().toString());//какой файл выбрали в списке
				//System.out.println(Constant.DOWNLOAD + " " + client.list.getSelectedValue());
				//System.out.println(filePath);
			}
		});
	}

	public void listenerUpload(){
		client.jbUpload.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("Server\\src\\files\\"));
			fs.setDialogTitle("Open a File");
			fs.setFileFilter(new FileTypeFilter(".docx", "WordFile"));
			fs.setFileFilter(new FileTypeFilter(".jpg", "JPEG File"));
			fs.setFileFilter(new FileTypeFilter(".txt", "TextFile"));
			int result = fs.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				File fi = fs.getSelectedFile();//выделенный файл
				try (InputStream in = new BufferedInputStream(new FileInputStream(fi.getPath()), Constant.BUFFER_SIZE)){
					barr = new byte[Constant.BUFFER_SIZE];
					in.read(barr);
					Object[] uploadFile = {fi.getName(), barr};
					sendPacket(Constant.UPLOAD, uploadFile);
				}catch (Exception e1){
					e1.printStackTrace();
				}
			}
		});
	}

	public void listenerReload(){
		client.jbReload.addActionListener(e ->{
			sendPacket(Constant.RELOAD, null);
		});
	}

	public void listenerAuth(){
		client.jbAuth.addActionListener(e -> auth());
	}

	public void start(){
		try{
			socket = new Socket(Constant.SERVER_IP, Constant.PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}catch(IOException e){
			e.printStackTrace();
		}
		Thread thread1 = new Thread(() -> {
			try{
				while(true){
					takePacket(in.readObject());
				}

			}catch(IOException e){
				e.printStackTrace();
				setAuthorized(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}finally{
				try{
					socket.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		thread1.start();
	}

	public void setAuthorized(boolean authorized){ //скрываем панели для авторизованых и неавторизованых пользователей
		isAuthorized = authorized;
		client.topPanel.setVisible(!isAuthorized);
		client.bottomPanel.setVisible(isAuthorized);
		client.rightPanel.setVisible(isAuthorized);
	}

	public void auth(){
		if(socket == null || socket.isClosed()) start();
		Object[] objects = {client.jtfLogin.getText(), client.jtfPassword.getText()};
		sendPacket(Constant.AUTH, objects);
		client.jtfLogin.setText("");
		client.jtfPassword.setText("");
	}

	public void sendPacket(String head, Object body){//принять заголовок, тело и отправить на сервер
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
			case Constant.AUTHOK:
				setAuthorized(true);
				break;
			case Constant.TEXT_MESSAGE:
				showMessageDialog((String)body);
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

	public void showMessageDialog(String msg){//показать у клиента диалоговое окно с сообщением
		JOptionPane.showMessageDialog(null, msg);
	}

	public void downloadFile(Object body){
		barr = (byte[]) body;
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), Constant.BUFFER_SIZE)){
			out.write(barr);
		}catch (Exception e1){
			e1.printStackTrace();
		}
	}
}