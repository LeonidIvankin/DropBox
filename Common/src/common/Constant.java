package common;

public interface Constant {
	int PORT = 8888;
	int MAX_NUMBER_CLIENTS = 100;
	String SERVER_IP = "localhost";

	//Default path and name
	String SERVER_ROOT = "Server\\src\\files";
	String DEFAULT_UPLOAD_DIR = "c:\\Users\\ILM\\Desktop\\";
	String DEFAULT_DOWNLOAD_DIR = "d:\\Downloads\\";
	String DEFAULT_NAME_NEW_FILE = "NewFile.txt";

	//System command
	String DOWNLOAD = "/download";
	String UPLOAD = "/upload";
	String END = "/end";
	String FILE_LIST = "/fileList";
	String SIGNIN = "/signIn";
	String AUTHOK = "/authok";
	String TEXT_MESSAGE = "/textMessage";
	String RELOAD = "/reload";
	String SIGNUP = "/signUp";
	String DELETE = "/delete";
	String MAKE_DIR = "/makeDir";
	String RENAME = "/rename";
	String NEW_FILE = "/newFile";
	String MOVE = "/moveOnTree";
	String DIR = "/dir";
	String FILE = "/file";
	String EXIT = "/exit";

	//system message
	String ENTER_NAME_NEW_FILE = "Введите имя нового файла";
	String ENTER_NAME_NEW_DIR = "Введите наименование каталога";
	String ENTER_NEW_NAME = "Введите новое имя";
	String ADD_FILE = "Добавлен файл: ";
	String ADD_DIR = "Добавлен каталог: ";
	String NEW_NAME = "Новое имя: ";
	String NAME_CHANGED_TO = "Имя изменено на: ";
	String YOUR_FOLDER_IS_EMPTY = ", ваша папка пока пуста";
	String ARE_YOU_SURE = "Вы уверены";
	String ACCOUNT_IS_USED = "Учетная запись уже используется";
	String INVALID_LOGIN_AND_PASSWORD = "Не верные логин/пароль";
	String ENTER_YOUR_LOGIN_AND_PASSWORD_AND_REPEAT = "Введите логин и пароль и повторите";
	String ENTER_LOGIN = "Введите логин";
	String LOGIN_BUSY = "Логин занят";
	String LOGIN_IS_FREE = "Логин свободен. Введите пароль и повторите";
	String YOU_HAVE_SUCCESSFULLY_REGISTERED = "Вы успешно зарегистрированы";

	//server message
	String SERVER_IS_RUNNING = "Сервер запущен, ждём клиентов";
	String CLIENT_CONNECTED = "Клиент подключился";
	String CLIENT_DISCONNECTED = "Клиент отключился";

	//title window
	String CREATE_NEW_FILE = "Создание нового файла";
	String CREATE_NEW_DIR = "Создание нового каталога";
	String TITLE_RENAME = "Введите новое имя";
	String TITLE_SAVE_FILE = "Save File";
	String TITLE_OPEN_FILE = "open File";
	String TITLE_CONFIRMATION_WINDOW = "Окно подтверждения";

	//application name
	String APPLICATION_NAME = "DropBox";

	//button name
	String BUTTON_NAME_SIGNIN = "SignIn";
	String BUTTON_NAME_SIGNUP = "SignUp";
	String BUTTON_NAME_UPLOAD = "Upload";
	String BUTTON_NAME_DOWNLOAD = "Download";
	String BUTTON_NAME_NEW_DIR = "New dir";
	String BUTTON_NAME_NEW_FILE = "New File";
	String BUTTON_NAME_DELETE = "Delete";
	String BUTTON_NAME_RELOAD = "Reload";
	String BUTTON_NAME_RENAME = "Rename";
	String BUTTON_NAME_EXIT = "Exit";

	//command name
	String COMMAND_NAME_YES = "Yes";
	String COMMAND_NAME_NO = "No";
	String COMMAND_NAME_CANCEL = "Cancel";

	//list display
	String ROOT_DIR = "[..]";
	char OPEN_BRACKET = '[';
	char CLOSE_BRACKET = ']';

}
