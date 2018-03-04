package common;


import java.util.Locale;

public class WorkWithString {

	public static String prefixBusyName(String elementName, String[] elementList) {

		if (elementName.contains(".")) {
			int x = elementName.lastIndexOf('.');
			String fileExtension = elementName.substring(x + 1, elementName.length());
			String fileName = elementName.substring(0, x);

			int a = 1;
			while(true){
				String str = String.format(Locale.getDefault(), "%s(%d).%s", fileName, a, fileExtension);
				if(!check(str, elementList)) return str;
				a++;
			}
		}else{
			int a = 1;
			while(true){
				String str = String.format(Locale.getDefault(), "%s(%d)", elementName, a);
				if(!check(str, elementList)) return str;
				a++;
			}
		}
	}

	private static boolean check(String name, String[] array) {
		for (String str : array) {
			if(str.equals(name)) return true;
		}
		return false;
	}

	public static String concatenationPath(String... strs) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if (i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}

	public static String separation(String str) {
		int x = str.lastIndexOf("\\");
		return str.substring(0, x);
	}

	public static String withoutBrackets(String str) {
		if (str.charAt(0) == Constant.OPEN_BRACKET && str.charAt(str.length() - 1) == Constant.CLOSE_BRACKET) {
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}

	public static String withBrackets(String str) {
		if (str.charAt(0) != Constant.OPEN_BRACKET) {
			return String.format(Locale.getDefault(), "%s%s%s", Constant.OPEN_BRACKET, str, Constant.CLOSE_BRACKET);
		}
		return str;
	}

	public static String findRelativePath(String dir, String file) {
		int x = dir.length();
		return file.substring(x + 1, file.length());
	}
}
