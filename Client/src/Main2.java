import java.io.*;

public class Main2 {
	public static void main(String[] args) throws IOException{
		File file = new File("Client\\src\\1.jpg");
		try (InputStream in = new BufferedInputStream(new FileInputStream(file));
			 OutputStream out = new BufferedOutputStream(new FileOutputStream("Client\\src\\2.jpg"))){//8192

			long ms = System.currentTimeMillis();

			byte[] barr = new byte[1024];
			while(in.read(barr) > 0){
				out.write(barr);
			}

			System.out.println(System.currentTimeMillis() - ms);
		}
	}
}
