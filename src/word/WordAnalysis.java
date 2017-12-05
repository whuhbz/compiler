package word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import system.MyException;
import system.ReturnClass;
import constant.ErrorNum;
import constant.KeyWord;
import constant.NumberDFAState;
import constant.TokenType;
import constant.StringDFAState;
import constant.Symbol;

/**
 * 词法分析部分，负责从输入流中读取到一个个的Token
 * 
 * @author Administrator
 *
 */

public class WordAnalysis {

	private BufferedReader bufferedReader; // 输入流的缓冲
	private String line = null; // 行缓�?
	private int cc = 0; // 指示当前行缓冲的读取位置
	private int ll = 0; // 指示当前行缓冲的长度
	private boolean isEnd = false; // 指示输入流是否读取完�?
	private boolean isBack = false; // 指示是否�?要回�?�?个字�?
	private char backChar; // 回�??时的字符
	private ErrorNum errorNum = null; // 当前遇到的错误编�?

	private Pattern idenPattern = Pattern.compile("([a-z,A-Z])|(([a-z,A-Z])"
			+ "([a-z,A-Z]|[0-9]|_)*([a-z,A-Z]|[0-9]))"); // 标识符的正则表达式

	private int lineNum = 0; // 当前解析到的行数

	public int getLineNum() {
		return lineNum;
	}

	public int getCc() {
		return cc;
	}

	/**
	 * 构�?�函数，从一个输入流构�?�一个BufferedReader
	 * 
	 * @param inputStream
	 *            输入�?
	 */
	public WordAnalysis(InputStream inputStream) {
		super();
		try {
			this.bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取�?个字�?
	 * 
	 * @return 读取的下�?个字�?
	 */
	public char getch() {
		// 判断是否有回�?字符
		if (isBack) {
			isBack = false;
			return backChar;
		}
		// 当行缓冲为空或�?�读完了�?行时，从输入流中读取�?行到行缓�?
		if (line == null || cc >= ll) {
			try {
				line = bufferedReader.readLine();
				lineNum++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 读到了输入末�?,将isEnd设置为true然后返回
			if (line == null) {
				isEnd = true;
				return 0;
			}
			line += "\n";
			// 重新设置cc和ll的�??
			cc = 0;
			ll = line.length();
		}
		char ch = line.charAt(cc++);
		// 使用递归调用来漏掉空�?
		return ch;
	}

	/**
	 * 获取到行末尾
	 * 
	 * @return
	 */
	public String getUtilEndOfLine() {
		StringBuffer str = new StringBuffer();
		char ch;
		while ((ch = getch()) != 10) {
			str.append(ch);
		}
		return str.toString();
	}

	/**
	 * 判断�?�?
	 */

	/**
	 * 此法分析，获取一个Token
	 * 
	 */
	public ReturnClass getsym() {
		if (isEnd) {
			return null;
		}
		// 获取�?个字�?
		char ch;
		do {
			ch = getch();
			// 判读是否到达了输入末�?
			if (isEnd) {
				return null;
			}
		} while (ch == 9 || ch == 10 || ch == 32 || ch == 0); // 忽略空格，换行和TAB

		if (ch == 34) { // 字符串开始
			StringDFA stringDFA = new StringDFA();
			TokenType tt = TokenType.STRING_VALUE;
			tt.setValue("string_value");
			return new ReturnClass(tt, stringDFA.getOneString());
		} else if (ch >= '0' && ch <= '9') { // 数字开始
			isBack = true;
			backChar = ch;
			NumberDFA numberDFA = new NumberDFA();
			String str = numberDFA.getOneNumber();
			if (str.contains(".")) {
				TokenType tt = TokenType.REAL_VALUE;
				tt.setValue("real_value");
				return new ReturnClass(tt, str);
			} else {
				TokenType tt = TokenType.INT_VALUE;
				tt.setValue("int_value");
				return new ReturnClass(tt, str);
			}
		} else if (Symbol.symbolSet.contains(ch)) {
			char nextChar;
			switch (ch) {
			case '=':
				nextChar = getch();
				if (nextChar == '=') {
					return new ReturnClass(TokenType.EQUAL, "==");
				} else {
					isBack = true;
					backChar = nextChar;
					return new ReturnClass(TokenType.ASSIGN, "=");
				}
			case '<':
				nextChar = getch();
				if (nextChar == '>') {
					return new ReturnClass(TokenType.NOT_EQUAL, "<>");
				} else if (nextChar == '=') {
					return new ReturnClass(TokenType.NOT_GREATER, "<=");
				} else {
					isBack = true;
					backChar = nextChar;
					return new ReturnClass(TokenType.LESS, "<");
				}
			case '>':
				nextChar = getch();
				if (nextChar == '=') {
					return new ReturnClass(TokenType.NOT_LESS, ">=");
				} else {
					isBack = true;
					backChar = nextChar;
					return new ReturnClass(TokenType.GREATER, ">");
				}
			default:
				TokenType tt = TokenType.findTypeByValue(String.valueOf(ch));
				return new ReturnClass(tt, String.valueOf(ch));
			}
		}

		StringBuffer sb = new StringBuffer();
		do {
			sb.append(ch);
			String s = sb.toString();
			if (KeyWord.keyWordSet.contains(sb.toString())) {
				return new ReturnClass(TokenType.findTypeByValue(s), s);
			}
			if (isEnd) {
				// 标识符
				Matcher m = idenPattern.matcher(s);
				if (m.matches()) {
					TokenType tt = TokenType.IDENTIFIER;
					tt.setValue("identifier");
					return new ReturnClass(tt, s);
				}
			}
			ch = getch();
			
			if(!Character.isLetterOrDigit(ch) && ch != '_') {
				break;
			}
			
		} while (ch != 9 && ch != 10 && ch != 32 && ch != 0);

		isBack = true;
		backChar = ch;

		// 标识符
		Matcher m = idenPattern.matcher(sb.toString());
		if (m.matches()) {
			TokenType tt = TokenType.IDENTIFIER;
			tt.setValue("identifier");
			return new ReturnClass(tt, sb.toString());
		} else {
			errorNum = ErrorNum.ILLEGAL_CHAR_SEQUENCE;
			throw new MyException("line " + lineNum + ", position "
					+ (cc - sb.toString().length()) + ": "
					+ errorNum.toString());
		}

	}

	/**
	 * 识别String的DFA
	 */
	private class StringDFA {
		private Set<Character> escapes;
		private Map<String, String> escMap;
		private StringBuffer sb;

		public StringDFA() {
			super();
			escapes = new HashSet<Character>();
			escapes.addAll(Arrays.asList('"', '\\', 'b', 'f', 'n', 'r',
					't'));
			escMap = new HashMap<String, String>();
			escMap.put("\"", "\"");
			escMap.put("\\", "\\");
			escMap.put("b", "\b");
			escMap.put("f", "\f");
			escMap.put("n", "\n");
			escMap.put("r", "\r");
			escMap.put("t", "\t");
			sb = new StringBuffer("");
		}

		private StringDFAState nowState = StringDFAState.BEGIN_QUOTES;

		public String getOneString() {
			char ch = getch();
			// 判读是否到达了输入末�?
			if (isEnd) {
				// 不合法的结尾
				errorNum = ErrorNum.ILLEGAL_END;
				throw new MyException("line " + lineNum + ", position " + cc
						+ ": " + errorNum.toString());
			}

			strOut: do {
				switch (nowState) {
				case BEGIN_QUOTES:
				case AFETER_LEGAL_CHAR:
					if (ch == 92) { // 读到反斜�?
						nowState = StringDFAState.AFTER_SPRIT;
					} else if (ch == 34) { // 读到双引�?
						nowState = StringDFAState.END_QUOTES;
					} else {
						nowState = StringDFAState.AFETER_LEGAL_CHAR;
						sb.append(ch);
					}
					break;
				case AFTER_SPRIT:
					if (escapes.contains(ch)) {
						nowState = StringDFAState.AFETER_LEGAL_CHAR;
						sb.append(escMap.get(String.valueOf(ch)));
					} else {
						// 非法转义�?
						sb.append(ch);
						errorNum = ErrorNum.ILLEGAL_ESCAPE;
						throw new MyException("line " + lineNum + ", position "
								+ (cc - 1) + ": " + errorNum.toString());
					}
					break;
				case END_QUOTES:
					isBack = true;
					backChar = ch;
					// 读到了界符则直接跳出循环，否则继续读取下�?个字�?
					break strOut;
				default:
					break;
				}

				ch = getch();
				if (isEnd) { // 读到末尾，跳出循�?
					break;
				}
			} while (ch != 10 && ch != 13);

			if (nowState != StringDFAState.END_QUOTES) {
				// 非法字符�?
				errorNum = ErrorNum.ILLEGAL_STRING;
				throw new MyException("line " + lineNum + ", position "
						+ (cc - sb.length() + 1) + ": " + errorNum.toString());
			} else {
				return sb.toString();
			}
		}
	}

	/**
	 * 识别数字的DFA
	 * 
	 * @author Administrator
	 *
	 */
	private class NumberDFA {
		private StringBuffer sb;

		public NumberDFA() {
			super();
			this.sb = new StringBuffer();
		}

		private NumberDFAState nowState = NumberDFAState.BEGIN_STATE;

		public String getOneNumber() {
			char ch = getch();
			// 判读是否到达了输入末�?
			if (isEnd) {
				// 不合法的结尾
				errorNum = ErrorNum.ILLEGAL_END;
				throw new MyException("line " + lineNum + ", position " + cc
						+ ": " + errorNum.toString());
			}

			numberOut: do {
				switch (nowState) {
				case BEGIN_STATE:
					if (ch == '0') {
						nowState = NumberDFAState.BEGIN_ZERO;
						sb.append(ch);
					} else if (ch >= '1' && ch <= '9') {
						nowState = NumberDFAState.AFTER_DIGIT;
						sb.append(ch);
					} else {
						// 非法数字
						sb.append(ch);
						errorNum = ErrorNum.ILLEGAL_NUMBER;
						throw new MyException("line " + lineNum + ", position "
								+ (cc - sb.length() + 1) + ": "
								+ errorNum.toString());
					}
					break;
				case BEGIN_ZERO:
					if (ch == '.') {
						nowState = NumberDFAState.AFTER_POINT;
						sb.append(ch);
					} else if (ch == 'e' || ch == 'E') {
						nowState = NumberDFAState.AFTER_E;
						sb.append(ch);
					} else{
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case AFTER_DIGIT:
					if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.AFTER_DIGIT;
						sb.append(ch);
					} else if (ch == '.') {
						nowState = NumberDFAState.AFTER_POINT;
						sb.append(ch);
					} else if (ch == 'e' || ch == 'E') {
						nowState = NumberDFAState.AFTER_E;
						sb.append(ch);
					}else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case AFTER_POINT:
					if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.POINT_DIGIT;
						sb.append(ch);
					} else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case POINT_DIGIT:
					if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.POINT_DIGIT;
						sb.append(ch);
					} else if (ch == 'e' || ch == 'E') {
						nowState = NumberDFAState.AFTER_E;
						sb.append(ch);
					} else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case AFTER_E:
					if (ch == '+' || ch == '-') {
						nowState = NumberDFAState.E_ONE_STEP;
						sb.append(ch);
					} else if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.END_DIGIT;
						sb.append(ch);
					} else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case E_ONE_STEP:
					if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.END_DIGIT;
						sb.append(ch);
					} else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				case END_DIGIT:
					if (ch >= '0' && ch <= '9') {
						nowState = NumberDFAState.END_DIGIT;
						sb.append(ch);
					} else {
						isBack = true;
						backChar = ch;
						break numberOut;
					}
					break;
				default:
					break;
				}
				ch = getch();
				if (isEnd) { // 读到末尾，跳出循�?
					break;
				}
			} while (ch != 9 && ch != 10 && ch != 32 && ch != 0);

			if (nowState != NumberDFAState.END_DIGIT
					&& nowState != NumberDFAState.BEGIN_ZERO
					&& nowState != NumberDFAState.AFTER_DIGIT
					&& nowState != NumberDFAState.POINT_DIGIT) {
				// 非法数字
				errorNum = ErrorNum.ILLEGAL_NUMBER;
				throw new MyException("line " + lineNum + ", position "
						+ (cc - sb.length() + 1) + ": " + errorNum.toString());
			} else {
				return sb.toString();
			}
		}
	}

}
