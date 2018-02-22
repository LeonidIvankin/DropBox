package common;

public interface Constant {
	int PORT = 8888;
	int MAX_NUMBER_CLIENTS = 100;
	String SERVER_IP = "localhost";
	String SERVER_ROOT = "Server\\src\\files\\";
	int BUFFER_SIZE = 2097152;

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
	String NEW_FILE= "/newFile";

	//messages to the client

}
