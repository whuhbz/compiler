package system;

import constant.ErrorNum;

public class ThrowMyException {
	public static void throwMyException(Word word, ErrorNum errorNum) {
		String errorMessage = errorNum.toString()
				+ " Line:" + word.getLineNum() + " Position:"
				+ word.getStartLocation();
		throw new MyException(errorMessage);
	}
}
