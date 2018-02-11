import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileIO extends JFrame
{
	public final static int WIDTH = 600;
	public final static int HEIGHT = 400;

	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JButton jSaveButton = null;
	private JButton jReadButton = null;
	private JLabel jLabel = null;
	private JPanel jPanel1 = null;
	private JTextField jFilePath = null;
	private JButton jBrowseButton = null;
	private JScrollPane jScrollPane = null;
	private JTextArea jTextArea = null;

	public FileIO(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocation((screenSize.width - WIDTH)/2, (screenSize.height - HEIGHT)/2);

		setContentPane(getJContentPane());
		setTitle("Simple reader");
		setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 */
	private JPanel getJContentPane(){
		//Основной контейнер
		if (jContentPane == null){
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			//Добавляем контейнер с текст. полем и кнопкой Browse
			jContentPane.add(getJPanel1(), BorderLayout.NORTH);
			//Добавляем контейнер с кнопками Save и Read
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
			//Добавляем контейнер с TextArea
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/*
	 * Создание панели с кнопками Save и Read
	 */
	private JPanel getJPanel(){
		if (jPanel == null){
			GridLayout gridLayout = new GridLayout();
			gridLayout.setColumns(4);
			jLabel = new JLabel();
			jPanel = new JPanel();
			jPanel.setLayout(gridLayout);
			jPanel.add(jLabel, null);
			jPanel.add(getSaveButton(), new GridLayout());
			jPanel.add(getReadButton(), new GridLayout());
		}
		return jPanel;
	}

	/*
	 * Создание кнопки и с событием
	 * сохранения текста в указанный файл
	 */
	private JButton getSaveButton(){
		if (jSaveButton == null){
			jSaveButton = new JButton();
			jSaveButton.setText("Save");
			jSaveButton.setFont(new Font("Dialog", Font.BOLD, 10));
			jSaveButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String strFileName = jFilePath.getText().trim();
					String strText = jTextArea.getText().trim();
					if(strFileName.length() > 0){
						try{
							FileWriter fw = new FileWriter(strFileName);
							fw.write(strText);
							fw.flush();
							fw.close();
							JOptionPane.showMessageDialog(jSaveButton, "File saved.", "Title", JOptionPane.INFORMATION_MESSAGE);
							jTextArea.setText("");
						}
						catch (IOException ex){
							JOptionPane.showMessageDialog(jSaveButton, e.toString(), "Title", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return jSaveButton;
	}

	/*
	 * Создание кнопки с событием
	 * загрузки текста из указанного файла
	 */
	private JButton getReadButton()
	{
		if (jReadButton == null)
		{
			jReadButton = new JButton();
			jReadButton.setText("Read");
			jReadButton.setFont(new Font("Dialog", Font.BOLD, 10));

			jReadButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String selectedFileName = jFilePath.getText().trim();
					if(selectedFileName.length() > 0)
					{
						readFile(selectedFileName);
					}
				}
			});
		}
		return jReadButton;
	}

	/*
	 * Метод чтения файла и загрузки его содержимого
	 * в TextArea
	 */
	private void readFile(String selectedFileName){
		try
		{
			FileReader fr = new FileReader(selectedFileName);
			StringBuffer sb = new StringBuffer();
			int symbol;
			while((symbol = fr.read()) != -1)
			{
				sb.append((char)symbol);
			}
			jTextArea.setText(sb.toString());
		}
		catch (FileNotFoundException ex)
		{
			JOptionPane.showMessageDialog(jSaveButton, ex.toString(), "Title", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(jSaveButton, ex.toString(), "Title", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Создание панели с текстовым полем и кнопкой Browse
	 */
	private JPanel getJPanel1(){
		if (jPanel1 == null)
		{
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getFilePath(), gridBagConstraints);
			jPanel1.add(getBrowseButton(), new GridBagConstraints());
		}
		return jPanel1;
	}

	/*
	 * Создание текстового поля для пути файла.
	 */
	private JTextField getFilePath(){
		if (jFilePath == null){
			jFilePath = new JTextField();
		}
		return jFilePath;
	}

	/*
	 * Создание кнопки Browse с событием открытия и загрузки файла
	 */
	private JButton getBrowseButton(){
		if (jBrowseButton == null){
			jBrowseButton = new JButton();
			jBrowseButton.setText("Browse");
			jBrowseButton.setFont(new Font("Dialog", Font.BOLD, 10));
			jBrowseButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//Создание диалога выбора файла
					JFileChooser fileChooser = new JFileChooser();
					//Добавление фильтра в диалог выбора файла
					fileChooser.addChoosableFileFilter(new TxtFilter());

					fileChooser.showOpenDialog(jBrowseButton);
					File selectedFile = fileChooser.getSelectedFile();
					if(selectedFile != null)
					{
						//Выбранный файл: имя записываем в текстовое поле для пути
						jFilePath.setText(selectedFile.getAbsolutePath());
						//содержимое загружаем в TextArea
						readFile(selectedFile.getAbsolutePath());
					}
				}

				//Класс, фильтрующий текстовые файлы
				class TxtFilter extends javax.swing.filechooser.FileFilter
				{
					public String getDescription()
					{
						return "*.txt";
					}
					public boolean accept(File f)
					{
						String filename = f.getName();
						return f.isDirectory() || filename.endsWith(".txt");
					}
				}
			});
		}
		return jBrowseButton;
	}

	/*
	 * Создание панели с полосами прокрутки для TextArea
	 */
	private JScrollPane getJScrollPane(){
		if (jScrollPane == null)
		{
			jScrollPane = new JScrollPane();
			//Добавление TextArea в панель
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/*
	 * Создание TextArea
	 */
	private JTextArea getJTextArea(){
		if (jTextArea == null)
		{
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
		}
		return jTextArea;
	}

	public static void main(String[] args) {
		new FileIO();
	}

}