package common;

public class WorkWithString {

	public String concatenation(String ... strs){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if(i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}

	public String separation(String str){
		int x = str.lastIndexOf("\\");
		return str.substring(0, x);
	}
}
