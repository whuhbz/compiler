package constant;

import java.util.regex.Pattern;

public class Machers {
	public static Pattern numP = Pattern.compile("[+,-]?[0-9]+\\.?[0-9]*");
	public static Pattern idenPattern = Pattern
			.compile("([a-zA-Z])|(([a-zA-Z])"
					+ "([a-zA-Z]|[0-9]|_)*([a-zA-Z]|[0-9]))"); // 标识符的正则表达式
	public static Pattern idenArrPattern = Pattern
			.compile("(([a-zA-Z])|(([a-zA-Z])"
					+ "([a-zA-Z]|[0-9]|_)*([a-zA-Z]|[0-9])))"
					+ "\\[[0-9]+\\]"); // 标识符数组的正则表达式
}
