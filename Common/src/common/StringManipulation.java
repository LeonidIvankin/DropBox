package common;

public class StringManipulation {

	public String concatenation(String ... strs){
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			stringBuilder.append(strs[i]);
			if(i < strs.length - 1) stringBuilder.append("\\");
		}
		return stringBuilder.toString();
	}
}
