package client;

import javax.swing.*;
import java.awt.*;

public class Client extends JFrame{
	protected JList list;
	private JTextArea jtaUsers;
	private JScrollPane jScrollPane;

	protected JTextField jtfLogin;
	protected JPasswordField jtfPassword;
	protected JPanel topPanel, rightPanel;
	protected JButton jbAdd, jbDelete, jbSignIn, jbSignUp, jbUpload, jbDownload, jbExit, jbReload, jbConfimation;
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

		rightPanel = new JPanel(new GridLayout(6, 1));

		jbAdd = new JButton("Add");
		jbDelete = new JButton("Delete");
		jbUpload = new JButton("Upload");
		jbDownload = new JButton("Download");
		jbExit = new JButton("Exit");
		jbReload = new JButton("Reload");
		jbSignIn = new JButton("SignIn");
		jbSignUp = new JButton("SignUp");
		jbConfimation = new JButton("Точно?");

		rightPanel.add(jbAdd);
		rightPanel.add(jbDelete);
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
		add(rightPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		UIManager.put("OptionPane.yesButtonText", "Да");
		UIManager.put("OptionPane.noButtonText", "Нет");
		UIManager.put("OptionPane.cancelButtonText", "Отмена");

		control = new Control(this);
		setVisible(true);
	}
}