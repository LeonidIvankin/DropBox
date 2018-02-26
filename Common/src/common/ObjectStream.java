package common;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObjectStream {
	HashMap<String, Object> hashMap = new HashMap<>();//для файлов и путей
	ArrayList<String> arrayList = new ArrayList<>();//для папок
	File filePathRoot;


	public void writeElement(Object object, File filePath) {//D:\Downloads\leonid.txt		Server\src\files\leo\leonid.txt
		//System.out.println(filePath);
		Object[] body = (Object[]) object;
		String flag = (String) body[0];

		if(flag.equals(Constant.FILE)){
			byte[] data = (byte[]) body[2];
			writeFile(data, filePath);
		}else if(flag.equals(Constant.DIR)){
			ArrayList<String> dirs = (ArrayList<String>) body[1];
			HashMap<String, Object> data = (HashMap<String, Object>) body[2];
			//System.out.println(filePath);
			filePath.mkdir();
			for (String dirName : dirs) {
				//new File(filePath + "\\" + dirName).mkdir();
				new File(WorkWithString.concatenation(filePath.toString(), dirName)).mkdir();
			}
			for (Map.Entry entry : data.entrySet()) {
				//writeFile((byte[]) entry.getValue(), new File(filePath  + "\\" +  entry.getKey()));
				writeFile((byte[]) entry.getValue(), new File(WorkWithString.concatenation(filePath.toString(), entry.getKey().toString())));
			}
		}
	}

	public void writeFile(byte[] barr, File filePath){
		System.out.println(filePath);
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath), barr.length)) {
			out.write(barr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public Object readElement(File filePath){//Server\src\files\leo\galina  Server\src\files\leo\leonid.txt		d:\Downloads\leonid.txt		Server\src\files\leo\galina
		hashMap.clear();
		arrayList.clear();
		String flag = "";
		Object dirs = null;
		Object data = null;
		if(filePath != null){
			filePathRoot = filePath;
			if(filePath.isFile()){//Server\src\files\leo\leonid.txt
				flag = Constant.FILE;
				data = readFile(filePath);
			}else{//Server\src\files\leo\galina
				flag = Constant.DIR;
				readDir(filePath);
				dirs = arrayList;
				data = hashMap;
				System.out.println(hashMap);
				System.out.println(arrayList);

			}
		}

		Object[] body = {flag, dirs, data};
		return body;
	}

	public void readDir(File dirPathMain){//Server\src\files\leo\galina
		File[] elements = dirPathMain.listFiles();
		for (File element : elements) {
			if(element.isFile()){
				readFileToHashMap(element);
			}else{
				readDirToArrayList(element);
				readDir(element);
			}
		}
	}

	public void readDirToArrayList(File filePath){
		String dirName = WorkWithString.findReletivePath(filePathRoot.toString(), filePath.toString());
		arrayList.add(dirName);//[kefir, tvorog]
	}

	public void readFileToHashMap(File filePath){//Server\src\files\leo\galina\kefir\3.txt
		String fileName = filePath.getName().toString();
		fileName = WorkWithString.findReletivePath(filePathRoot.toString(), filePath.toString());
		hashMap.put(fileName, readFile(filePath));//kefir\3.txt + byte[]
	}

	public byte[] readFile(File filePath){//Server\src\files\leo\leonid.txt		d:\Downloads\leonid.txt
		int fileSize = (int) filePath.length();
		byte[] barr = new byte[fileSize];
		try (InputStream in = new BufferedInputStream(new FileInputStream(filePath), fileSize)) {
			in.read(barr);
			return barr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
