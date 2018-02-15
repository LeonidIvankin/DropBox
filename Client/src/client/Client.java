package client;

import javax.swing.*;
import java.awt.*;

public class Client extends JFrame{
	protected JList list;
	private JTextArea jtaUsers;
	private JScrollPane jScrollPane;
	private JTextField jTextField;

	protected JTextField jtfLogin;
	protected JPasswordField jtfPassword;
	protected JPanel bottomPanel, topPanel, rightPanel;
	protected JButton jButtonAdd, jButtonDelete, jbSignIn, jbSignUp, jbUpload, jbDownload, jbExit, jbReload;
	protected DefaultListModel defaultListModel;

	private Control control;



	public Client(){
		setTitle("DropBox");
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
		rightPanel = new JPanel(new GridLayout(6, 1));

		jButtonAdd = new JButton("Add");
		jButtonDelete = new JButton("Delete");
		jbUpload = new JButton("Upload");
		jbDownload = new JButton("Download");
		jbExit = new JButton("Exit");
		jbReload = new JButton("Reload");
		jbSignIn = new JButton("SignIn");
		jbSignUp = new JButton("SignUp");

		bottomPanel.add(jTextField, BorderLayout.CENTER);
		rightPanel.add(jButtonAdd);
		rightPanel.add(jButtonDelete);
		rightPanel.add(jbUpload);
		rightPanel.add(jbDownload);
		rightPanel.add(jbReload);

		jtfLogin = new JTextField();
		jtfPassword = new JPasswordField();

		topPanel = new JPanel(new GridLayout(1,3));
		topPanel.add(jtfLogin);
		topPanel.add(jtfPassword);
		topPanel.add(jbSignIn);
		topPanel.add(jbSignUp);

		add(jScrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		add(rightPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		control = new Control(this);
		setVisible(true);
	}
}