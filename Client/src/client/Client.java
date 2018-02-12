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
	//private finag SERVER_IP = "localhost";
	private JList list;
	private JTextArea jtaUsers;
	private JScrollPane jScrollPane;
	private JTextField jTextField;
	private JTextField jtfLogin;
	private JPasswordField jtfPassword;
	private JPanel bottomPanel, topPanel, rightPanel;
	private Socket socket;
	private JButton jButtonAdd, jButtonDelete, jbAuth, upload, download;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isAuthorized;
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
		rightPanel = new JPanel(new GridLayout(4, 1));


		jButtonAdd = new JButton("Add");
		jButtonDelete = new JButton("Delete");
		upload = new JButton("Upload");
		download = new JButton("Download");


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

		jbAuth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				auth();
			}
		});
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
				defaultListModel.addElement(fs.getName());

			}
		});

		download.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("c:\\Users\\ILM\\Desktop\\"));
			fs.setDialogTitle("Save a File");
			//fs.setFileFilter(new server.FileTypeFilter(".txt", "TextFile"));
			int result = fs.showSaveDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				filePath = fs.getSelectedFile();//куда сохранять файл
				sendSystemMessageArray(Constant.DOWNLOAD, list.getSelectedValue().toString());//какой файл выбрали в списке
				System.out.println(Constant.DOWNLOAD + " " + list.getSelectedValue());
				System.out.println(filePath);

			}
		});

		start();
		setAuthorized(false);


		setVisible(true);
	}

	public void start(){
		try{
			socket = new Socket(Constant.SERVER_IP, Constant.PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}catch(IOException e){
			e.printStackTrace();
		}
		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run(){
				try{
					while(true){
						Object answer = in.readObject();
						if (answer instanceof String){
							String msg = (String) answer;
							if(msg.startsWith("/authok")){
								setAuthorized(true);
								break;
							}
						}
					}
					while(true){
						Object answer = in.readObject();
						if (answer instanceof String){
							String msg = (String) answer;
							JOptionPane.showMessageDialog(null, msg);
						}else if(answer instanceof Object[]){
							Object[] objects = (Object[]) answer;
							String head = (String) objects[0];
							if(head.equals(Constant.FILE_LIST)) {
								String[] body = (String[]) objects[1];
								for (String fileName : body) {
									defaultListModel.addElement(fileName);
								}
							}else if(head.equals(Constant.DOWNLOAD)){
								barr = (byte[]) objects[1];
								try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), Constant.BUFFER_SIZE)){
									out.write(barr);
								}catch (Exception e1){
									e1.printStackTrace();
								}
							}
						}
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
		try{
			out.writeObject("/auth " + jtfLogin.getText() + " " + jtfPassword.getText());
			jtfLogin.setText("");
			jtfPassword.setText("");
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void sendSystemMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void sendSystemMessageArray(String head, String body){
		String[] msg = {head, body};
		try{
			out.writeObject(msg);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void sendMessage(){
		String msg = jTextField.getText();
		jTextField.setText("");
		try{
			out.writeObject(msg);
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
