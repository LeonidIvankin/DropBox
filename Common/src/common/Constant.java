package common;

public interface Constant {
	int PORT = 8888;
	int MAX_NUMBER_CLIENTS = 100;
	String SERVER_IP = "localhost";

	//Default path
	String SERVER_ROOT = "Server\\src\\files";
	String DEFAULT_UPLOAD_DIR = "c:\\Users\\ILM\\Desktop\\";
	String DEFAULT_DOWNLOAD_DIR = "d:\\Downloads\\";


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

	//messages to the client

}
