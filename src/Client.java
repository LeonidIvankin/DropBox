import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		Socket socket;

		try{
			socket = new Socket("localhost", 8888);

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			//bjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			String[] files = (String[]) in.readObject();
			for (String file : files) {
				System.out.println(file);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
