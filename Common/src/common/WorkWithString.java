package common;

public class WorkWithString {

	public static String concatenation(String ... strs){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if(i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}

	public static String separation(String str){
		int x = str.lastIndexOf("\\");
		return str.substring(0, x);
	}

	public static String withoutBrackets(String str){
		if(str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']'){
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}

	public static String withBrackets(String str){
		if(str.charAt(0) != '['){
			return "[" + str + "]";
		}
		return str;
	}

	public static String findReletivePath(String dir, String file){
		int x = dir.length();
		return file.substring(x + 1, file.length());
	}
}
