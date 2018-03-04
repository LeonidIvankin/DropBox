package common;


public class WorkWithString {

	public static String prefixBusyName(String elementName, String[] elementList) {

		if (elementName.contains(".")) {
			int x = elementName.lastIndexOf('.');
			String fileExtension = elementName.substring(x + 1, elementName.length());
			String fileName = elementName.substring(0, x);

			int a = 1;
			while(true){
				String str = fileName + "(" + a + ")" + "." + fileExtension;
				if(!check(str, elementList)) return str;
				a++;
			}
		}else{
			int a = 1;
			while(true){
				String str = elementName + "(" + a + ")";
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

	public static String concatenation(String... strs) {
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
		if (str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']') {
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}

	public static String withBrackets(String str) {
		if (str.charAt(0) != '[') {
			return "[" + str + "]";
		}
		return str;
	}

	public static String findReletivePath(String dir, String file) {
		int x = dir.length();
		return file.substring(x + 1, file.length());
	}
}
