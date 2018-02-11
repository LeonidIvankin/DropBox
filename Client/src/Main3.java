import javax.swing.*;
import java.awt.*;


public class Main3 extends JFrame {
	private JButton save, open;
	private JTextArea jTextArea;
	private JPanel jPanelNorth, jPanelSouth;
	private static final String TEXT = "All ok1!";


	public Main3(){
		setTitle("DropBox");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		open = new JButton("Open");
		save = new JButton("Save");
		jTextArea = new JTextArea();
		jTextArea.setText(TEXT);

		jPanelNorth = new JPanel();
		jPanelSouth = new JPanel();

		jPanelNorth.add(open);
		jPanelNorth.add(save);
		jPanelSouth.add(jTextArea);

		save.addActionListener(e -> {
			JOptionPane.showMessageDialog(null, "Молодец!", "Нажималка", JOptionPane.INFORMATION_MESSAGE);
		});


		open.addActionListener(e -> {

		});



		add(jPanelNorth, BorderLayout.NORTH);
		add(jPanelSouth, BorderLayout.CENTER);
		setVisible(true);
	}



	public static void main(String[] args) {
		new Main3();
	}

}