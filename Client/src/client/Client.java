package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame{
	private final int PORT = 8888;
	private final String SERVER_IP = "localhost";
	private JTextArea jTextArea;
	private JTextArea jtaUsers;
	//private JScrollPane jspUsers;
	private JTextField jTextField;
	private JTextField jtfLogin;
	private JPasswordField jtfPassword;
	private JPanel bottom, top;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isAuthorized;



	public Client(){
		setTitle("client.Client");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		jTextArea = new JTextArea(); //выводится история
		jtaUsers = new JTextArea();
		jtaUsers.setEditable(false);
		jtaUsers.setPreferredSize(new Dimension(150, 1));
		jTextArea.setEditable(false);
		jTextArea.setLineWrap(true);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		jTextField = new JTextField(); //окно для ввода текста
		jTextField.setPreferredSize(new Dimension(200, 20));
		bottom = new JPanel();
		JButton jButtonSend = new JButton("Send");
		bottom.add(jTextField, BorderLayout.CENTER);
		bottom.add(jButtonSend, BorderLayout.EAST);
		jtfLogin = new JTextField();
		jtfPassword = new JPasswordField();
		JButton jbAuth = new JButton("Login");
		top = new JPanel(new GridLayout(1,3));
		top.add(jtfLogin);
		top.add(jtfPassword);
		top.add(jbAuth);

		add(jScrollPane, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);

		//LISTNERS
		jButtonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		});
		jTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		});
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
					out.writeObject("/end");
					socket.close();
				}catch(IOException e1){
					e1.printStackTrace();
					setAuthorized(false);
				}
			}
		});
		start();
		setAuthorized(false);
		setVisible(true);
	}

	public void start(){
		try{
			socket = new Socket(SERVER_IP, PORT);
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
								sendSystemMessage("/show");
								break;
							}
							jTextArea.append(msg + "\n");
							jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
						}
					}
					while(true){
						Object answer = in.readObject();
						if (answer instanceof String){
							String msg = (String) answer;
							jTextArea.append(msg + "\n");
							jTextArea.setCaretPosition(jTextArea.getDocument().getLength()); //перемещает курсор на самый последний символ чата
						}else if(answer instanceof String[]){
							String[] files = (String[]) answer;
							for (String fileName : files) {
								System.out.println(fileName);
								jTextArea.append(fileName + "\n");
							}
							jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
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
		top.setVisible(!isAuthorized);
		bottom.setVisible(isAuthorized);
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
