import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TestFrame extends JFrame {

	public TestFrame() {
		String[] data = { "Chrome", "Firefox", "Internet Explorer", "Safari",
				"Opera", "Morrowind", "Oblivion", "NFS", "Half Life 2",
				"Hitman", "Morrowind", "Oblivion", "NFS", "Half Life 2",
				"Hitman", "Morrowind", "Oblivion", "NFS", "Half Life 2",
				"Hitman", "Morrowind", "Oblivion", "NFS", "Half Life 2",
				"Hitman", "Morrowind", "Oblivion", "NFS", "Half Life 2",
				"Hitman", "IL-2", "CMR", "NFS Undercover",
				"Star Wars", "Call of Duty", "IL-2", "CMR",
				"NFS Undercover", "Star Wars"
		};

		setTitle("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setResizable(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		DefaultListModel dlm = new DefaultListModel();
		//for (String str : data) {
			dlm.addElement("1111");
			dlm.addElement("1111");
		//}

		JList list = new JList(dlm);
		list.setLayoutOrientation(JList.VERTICAL);
		JScrollPane northScroll = new JScrollPane(list);
		mainPanel.add(northScroll);

		JTextField jTextField = new JTextField();
		mainPanel.add(jTextField);

		JButton jButton1 = new JButton("Add");
		mainPanel.add(jButton1);
		jButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				dlm.addElement(jTextField.getText());
			}
		});

		JButton jButton2 = new JButton("Remove");
		mainPanel.add(jButton2);
		jButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlm.removeElement(list.getSelectedValue());
			}
		});



		getContentPane().add(mainPanel);
		setVisible(true);
	}

	public static void main(String[] args) {
		new TestFrame();
	}
}