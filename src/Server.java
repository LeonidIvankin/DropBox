import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket = null;
		String[] files = getFiles("Leonid");


		try{
			server = new ServerSocket(8888);
			System.out.println("Сервер запущен, ждём клиента");
			socket = server.accept();
			System.out.println("Клиент подключился");

			//ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(files);
			out.flush();
			out.close();
		}catch(IOException exception){
			exception.printStackTrace();
		} finally {
			try {
				server.close();//закрыть сервер
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String[] getFiles(String name) {

		File folder = new File("src\\files\\" + name);
		String[] files = folder.list();
		//for (String fileName : files ) System.out.println(fileName);

		return files;
	}
}
