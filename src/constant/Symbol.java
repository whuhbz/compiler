package constant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Symbol {
	private static final Character[] symbolArray = { '+', '-', '*', '/', '=', '<', '>',
			'(', ')', '{', '}', ',', '[', ']', ';' };
	public static final Set<Character> symbolSet = new HashSet<Character>();
	static {
		Collections.addAll(symbolSet, symbolArray);
	}
}
