// Пример использования диалоговых окон работы с файлами и директориями

import javax.swing.*;
// import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;

public class JFileChooserTest extends JFrame
{
	private static final long serialVersionUID = 1L;

	private  JButton      btnSaveFile   = null;
	private  JButton      btnOpenDir    = null;
	private  JButton      btnFileFilter = null;

	private  JFileChooser fileChooser   = null;

	private final String[][] FILTERS = {{"docx", "Файлы Word (*.docx)"},
			{"pdf" , "Adobe Reader(*.pdf)"}};
	public JFileChooserTest() {
		super("Пример FileChooser");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Кнопка создания диалогового окна для выбора директории
		btnOpenDir = new JButton("Открыть директорию");
		// Кнопка создания диалогового окна для сохранения файла
		btnSaveFile = new JButton("Сохранить файл");
		// Кнопка создания диалогового окна для сохранения файла
		btnFileFilter = new JButton("Фильтрация файлов");

		// Создание экземпляра JFileChooser 
		fileChooser = new JFileChooser();
		// Подключение слушателей к кнопкам
		addFileChooserListeners();
		// Размещение кнопок в интерфейсе
		JPanel contents = new JPanel();
		contents.add(btnOpenDir   );
		contents.add(btnSaveFile  );
		contents.add(btnFileFilter);
		setContentPane(contents);
		// Вывод окна на экран
		setSize(360, 110);
		setVisible(true);
	}
	private void addFileChooserListeners()
	{
		btnOpenDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Выбор директории");
				// Определение режима - только каталог
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(JFileChooserTest.this);
				// Если директория выбрана, покажем ее в сообщении
				if (result == JFileChooser.APPROVE_OPTION )
					JOptionPane.showMessageDialog(JFileChooserTest.this,
							fileChooser.getSelectedFile());
			}
		});
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Сохранение файла");
				// Определение режима - только файл
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showSaveDialog(JFileChooserTest.this);
				// Если файл выбран, то представим его в сообщении
				if (result == JFileChooser.APPROVE_OPTION )
					JOptionPane.showMessageDialog(JFileChooserTest.this,
							"Файл '" + fileChooser.getSelectedFile() +
									" ) сохранен");
			}
		});
		btnFileFilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setDialogTitle("Выберите файл");
				// Определяем фильтры типов файлов
				for (int i = 0; i < FILTERS[0].length; i++) {
					FileFilterExt eff = new FileFilterExt(FILTERS[i][0],
							FILTERS[i][1]);
					fileChooser.addChoosableFileFilter(eff);
				}
//                FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                        "Word & Excel", "docx", "xlsx");
//                fileChooser.setFileFilter(filter);

				// Определение режима - только файл
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(JFileChooserTest.this);
				// Если файл выбран, покажем его в сообщении
				if (result == JFileChooser.APPROVE_OPTION )
					JOptionPane.showMessageDialog(JFileChooserTest.this,
							"Выбран файл ( " +
									fileChooser.getSelectedFile() + " )");
			}
		});
	}
	// Фильтр выбора файлов определенного типа
	class FileFilterExt extends javax.swing.filechooser.FileFilter
	{
		String extension  ;  // расширение файла
		String description;  // описание типа файлов

		FileFilterExt(String extension, String descr)
		{
			this.extension = extension;
			this.description = descr;
		}
		@Override
		public boolean accept(java.io.File file)
		{
			if(file != null) {
				if (file.isDirectory())
					return true;
				if( extension == null )
					return (extension.length() == 0);
				return file.getName().endsWith(extension);
			}
			return false;
		}
		// Функция описания типов файлов
		@Override
		public String getDescription() {
			return description;
		}
	}

	public static void main(String[] args)
	{
		// Локализация компонентов окна JFileChooser  
		UIManager.put("FileChooser.saveButtonText"      , "Сохранить"             );
		UIManager.put("FileChooser.openButtonText"      , "Открыть"               );
		UIManager.put("FileChooser.cancelButtonText"    , "Отмена"                );
		UIManager.put("FileChooser.fileNameLabelText"   , "Наименование файла"    );
		UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов"           );
		UIManager.put("FileChooser.lookInLabelText"     , "Директория"            );
		UIManager.put("FileChooser.saveInLabelText"     , "Сохранить в директории");
		UIManager.put("FileChooser.folderNameLabelText" , "Путь директории"       );

		new JFileChooserTest();
	}
}
