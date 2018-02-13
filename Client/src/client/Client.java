package client;

import common.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame{
	private JList list;
	private JTextArea jtaUsers;
	private JScrollPane jScrollPane;
	private JTextField jTextField;
	private JTextField jtfLogin;
	private JPasswordField jtfPassword;
	private JPanel bottomPanel, topPanel, rightPanel;
	private Socket socket;
	private JButton jButtonAdd, jButtonDelete, jbAuth, upload, download, exit;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isAuthorized = false;
	private DefaultListModel defaultListModel;
	private File filePath;
	private byte[] barr;



	public Client(){
		setTitle("Client");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setResizable(false);
		defaultListModel = new DefaultListModel();

		list = new JList(defaultListModel);
		list.setLayoutOrientation(JList.VERTICAL);
		jtaUsers = new JTextArea();
		jtaUsers.setEditable(false);
		jtaUsers.setPreferredSize(new Dimension(150, 1));
		jScrollPane = new JScrollPane(list);
		jScrollPane.setPreferredSize(new Dimension(200, 200));

		jTextField = new JTextField(); //окно для ввода текста
		jTextField.setPreferredSize(new Dimension(200, 20));
		bottomPanel = new JPanel();
		rightPanel = new JPanel(new GridLayout(5, 1));


		jButtonAdd = new JButton("Add");
		jButtonDelete = new JButton("Delete");
		upload = new JButton("Upload");
		buttonDownload();
		exit = new JButton("Exit");


		bottomPanel.add(jTextField, BorderLayout.CENTER);
		rightPanel.add(jButtonAdd);
		rightPanel.add(jButtonDelete);
		rightPanel.add(upload);
		rightPanel.add(download);

		jtfLogin = new JTextField();
		jtfPassword = new JPasswordField();
		jbAuth = new JButton("Login");
		topPanel = new JPanel(new GridLayout(1,3));
		topPanel.add(jtfLogin);
		topPanel.add(jtfPassword);
		topPanel.add(jbAuth);



		add(jScrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		add(rightPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		jbAuth.addActionListener(e -> auth());

		addWindowListener(new WindowAdapter() { //отвечает за закрытие соединения при закрытии окна через крестик
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

		upload.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("Server\\src\\files\\"));
			fs.setDialogTitle("Open a File");
			fs.setFileFilter(new FileTypeFilter(".txt", "TextFile"));
			fs.setFileFilter(new FileTypeFilter(".docx", "WordFile"));
			fs.setFileFilter(new FileTypeFilter(".jpg", "JPEG File"));
			int result = fs.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				//пусто

			}
		});



		start();
		setAuthorized(false);
		setVisible(true);
	}
	
	public void buttonDownload(){
		download = new JButton("Download");
		download.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("c:\\"));
			fs.setDialogTitle("Save a File");
			int result = fs.showSaveDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				filePath = fs.getSelectedFile();//куда сохранять файл
				sendPacket(Constant.DOWNLOAD, list.getSelectedValue().toString());//какой файл выбрали в списке
				System.out.println(Constant.DOWNLOAD + " " + list.getSelectedValue());
				System.out.println(filePath);

			}
		});
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
		topPanel.setVisible(!isAuthorized);
		bottomPanel.setVisible(isAuthorized);
		rightPanel.setVisible(isAuthorized);
	}

	public void auth(){
		if(socket == null || socket.isClosed()) start();
		Object[] objects = {jtfLogin.getText(), jtfPassword.getText()};
		sendPacket(Constant.AUTH, objects);
		jtfLogin.setText("");
		jtfPassword.setText("");
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
		String[] fileList = (String[]) body;
		for (String fileName : fileList) {
			defaultListModel.addElement(fileName);
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
