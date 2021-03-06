package client;

import common.Constant;

import javax.swing.*;
import java.awt.*;

public class Client extends JFrame{
	protected JList list;

	protected JTextField jtfLogin;
	protected JPasswordField jtfPassword;
	protected JPanel topPanel, rightPanel;
	protected JButton jbCreateNewDir, jbDelete, jbSignIn, jbSignUp, jbUpload, jbDownload, jbExit, jbReload, jbRename, jbCreateNewFile;
	protected DefaultListModel defaultListModel;


	public Client(){
		setTitle(Constant.APPLICATION_NAME);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setResizable(false);
		defaultListModel = new DefaultListModel();

		list = new JList(defaultListModel);
		list.setLayoutOrientation(JList.VERTICAL);
		JTextArea jtaUsers = new JTextArea();
		jtaUsers.setEditable(false);
		jtaUsers.setPreferredSize(new Dimension(150, 1));
		JScrollPane jScrollPane = new JScrollPane(list);
		jScrollPane.setPreferredSize(new Dimension(200, 200));

		rightPanel = new JPanel(new GridLayout(8, 1));

		jbSignIn = new JButton(Constant.BUTTON_NAME_SIGNIN);
		jbSignUp = new JButton(Constant.BUTTON_NAME_SIGNUP);

		jbUpload = new JButton(Constant.BUTTON_NAME_UPLOAD);
		jbDownload = new JButton(Constant.BUTTON_NAME_DOWNLOAD);
		jbCreateNewDir = new JButton(Constant.BUTTON_NAME_NEW_DIR);
		jbCreateNewFile = new JButton(Constant.BUTTON_NAME_NEW_FILE);
		jbDelete = new JButton(Constant.BUTTON_NAME_DELETE);
		jbReload = new JButton(Constant.BUTTON_NAME_RELOAD);
		jbRename = new JButton(Constant.BUTTON_NAME_RENAME);
		jbExit = new JButton(Constant.BUTTON_NAME_EXIT);

		rightPanel.add(jbUpload);
		rightPanel.add(jbDownload);
		rightPanel.add(jbCreateNewDir);
		rightPanel.add(jbCreateNewFile);
		rightPanel.add(jbDelete);
		rightPanel.add(jbReload);
		rightPanel.add(jbRename);
		rightPanel.add(jbExit);

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

		UIManager.put("OptionPane.yesButtonText", Constant.COMMAND_NAME_YES);
		UIManager.put("OptionPane.noButtonText", Constant.COMMAND_NAME_NO);
		UIManager.put("OptionPane.cancelButtonText", Constant.COMMAND_NAME_CANCEL);

		Control control = new Control(this);
		MouseListenerList mouseListenerList = new MouseListenerList(list, control);
		setVisible(true);
	}
}