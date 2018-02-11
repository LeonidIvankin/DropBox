import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class SaveFile extends JFrame {
	private JButton save, open;
	private JTextArea jTextArea;
	private JFileChooser jFileChooser;
	private JPanel jPanelNorth, jPanelSouth;
	private static final String TEXT = "All ok!";
	private Object object;


	public SaveFile(){
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
			JFileChooser fs = new JFileChooser(new File("c:\\Users\\ILM\\Desktop\\"));
			fs.setDialogTitle("Save a File");
			fs.setFileFilter(new FileTypeFilter(".txt", "TextFile"));
			int result = fs.showSaveDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				String content = jTextArea.getText();
				File fi = fs.getSelectedFile();
				try {
					FileWriter fw = new FileWriter(fi.getPath());
					fw.write(content);
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});


		open.addActionListener(e -> {
			JFileChooser fs = new JFileChooser(new File("c:\\Users\\ILM\\Desktop\\"));
			fs.setDialogTitle("Open a File");
			fs.setFileFilter(new FileTypeFilter(".txt", "TextFile"));
			fs.setFileFilter(new FileTypeFilter(".docx", "WordFile"));
			fs.setFileFilter(new FileTypeFilter(".jpg", "JPEG File"));
			int result = fs.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION){
				try {
					File fi = fs.getSelectedFile();
					BufferedReader br = new BufferedReader(new FileReader(fi.getPath()));
					String line = null;
					String s = null;
					while((line = br.readLine()) != null){
						s += line;
						jTextArea.setText(s);
						if(br != null){
							br.close();
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});








		add(jPanelNorth, BorderLayout.NORTH);
		add(jPanelSouth, BorderLayout.CENTER);
		setVisible(true);
	}



	public static void main(String[] args) {
		new SaveFile();
	}

}