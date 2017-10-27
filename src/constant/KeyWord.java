package constant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyWord {
	private static final String[] symbolArray = {"if", "else", "while", "String", "int",
			"real", "read", "write"};
	public static final Set<String> keyWordSet = new HashSet<String>();
	static {
		Collections.addAll(keyWordSet, symbolArray);
	}
}
