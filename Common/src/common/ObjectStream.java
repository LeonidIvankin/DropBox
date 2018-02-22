package common;

import java.io.*;

public class ObjectStream {


	public void writeFile(byte[] barr, File filePath) {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), barr.length)) {
			out.write(barr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public byte[] readFile(File filePath){
		int fileSize = (int) filePath.length();
		byte[] barr = new byte[fileSize];
		try (InputStream in = new BufferedInputStream(new FileInputStream(filePath), fileSize)) {
			in.read(barr);

		} catch (Exception e1) {
			e1.printStackTrace();
		}finally {
			return barr;
		}
	}
}
