import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Frame");
		frame.setSize(new Dimension(600, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());


		String[] str = {"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555",
				"11111", "22222", "33333", "44444", "55555"};


		JPanel panel1 = new JPanel(new FlowLayout());
		JPanel panel2 = new JPanel(new FlowLayout());

		panel1.setPreferredSize(new Dimension(600, 200));
		JTextField textField = new JTextField(15);
		JButton button1 = new JButton("Add");
		JButton button2 = new JButton("Remove");


		DefaultListModel dfm = new DefaultListModel();
		JList myList = new JList(dfm);
		JScrollPane myScrollPaneList = new JScrollPane(myList);
		myScrollPaneList.setPreferredSize(new Dimension(200, 200));
		myList.setLayoutOrientation(JList.VERTICAL);

		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//dfm.addElement(textField.getText());
				dfm.addElement(textField.getText());
			}
		});

		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dfm.removeElement(myList.getSelectedValue());
			}
		});

		panel2.add(myScrollPaneList);
		panel1.add(textField);
		panel1.add(button1);
		panel1.add(button2);

		dfm.addElement("1111");
		dfm.addElement("2222");
		dfm.addElement("3333");
		dfm.addElement("4444");

		frame.add(panel1, BorderLayout.NORTH);
		frame.add(panel2, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();
	}
}
