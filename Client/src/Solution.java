import javax.swing.*;
import java.io.File;

public class Solution {
	public static void main(String[] args) {
		new MyFrame();
	}
}

class MyFrame extends JFrame {

	String ext,description;

	MyFrame()
	{
		setBounds(0, 0, 500, 500);
		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		dialog.setApproveButtonText("Выбрать");//выбрать название для кнопки согласия
		dialog.setDialogTitle("Выберите файл для загрузки");// выбрать название
		dialog.setDialogType(JFileChooser.OPEN_DIALOG);// выбрать тип диалога Open или Save
		dialog.setMultiSelectionEnabled(true); // Разрегить выбор нескольки файлов
		dialog.showOpenDialog(this);
		File[] file = dialog.getSelectedFiles();
		setVisible(true);
	}


}
