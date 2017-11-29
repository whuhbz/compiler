package grammar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import constant.ErrorNum;
import constant.TokenType;
import system.MyException;
import system.Node;
import system.Node.NODE_TYPE;
import system.ReturnClass;
import system.ThrowMyException;
import system.Word;
import util.Formaluetree;
import util.ListUtil;
import word.WordAnalysis;

/**
 * 语法分析
 * 
 * @author 10330
 *
 */
public class GrammerAnalysis {
	// 词法分析器
	private WordAnalysis wordAnalysis;
	// 词缓冲队列
	private LinkedList<Word> wordBuffer;

	public GrammerAnalysis(InputStream inputStream) {
		super();
		wordAnalysis = new WordAnalysis(inputStream);
		this.wordBuffer = new LinkedList<Word>();
	}

	/**
	 * 返回类型转word
	 * 
	 * @param rc
	 * @return 转换后的词
	 */
	private Word rc2Word(ReturnClass rc) {
		if(rc == null) {
			return null;
		}
		Word word = new Word();
		word.setType(rc.type);
		word.setValue(rc.value);
		word.setLineNum(wordAnalysis.getLineNum());
		word.setPosition(wordAnalysis.getCc());
		word.setStartLocation(word.getPosition() - word.getValue().length());
		return word;
	}

	/**
	 * 获取下一个词 解析到末尾返回null
	 * 
	 * @return 下一个词
	 */
	private Word nextWord() {
		Word word = null;
		if ((word = wordBuffer.poll()) == null) {
			ReturnClass rc = wordAnalysis.getsym();
			word = rc2Word(rc);
		}
		return word;
	}

	/**
	 * 解析一个程序
	 */
	public Node oneProgram() {
		Node root = new Node();
		root.setType(Node.NODE_TYPE.PROGRAM);
		Word beginWord = nextWord();

		boolean flag = false; // 是否遇到非注释代码

		out: do {

			if (flag || beginWord == null) {
				break out;
			}

			switch (beginWord.getType()) {
			// 遇到左括号
			case LEFT_CURLY_BRACES:
				root.addLink(oneBlock());
				flag = true;
				break;
			case STRING:
			case INT:
			case REAL:
			case IDENTIFIER:
			case READ:
			case WRITE:
			case IF:
			case WHILE:
				root.addLink(oneSentence(beginWord));
				flag = true;
				break;
			case DIVISION:
				root.addLink(oneComment());
				break;
			default:
				ThrowMyException.throwMyException(beginWord,
						ErrorNum.ILLEGAL_PRO_START);
			}
			
			beginWord = nextWord();

		} while (true);

		return root;
	}

	/**
	 * 解析一个程序块
	 */
	private Node oneBlock() {
		Node node = new Node();
		node.setType(Node.NODE_TYPE.BLOCK);

		Word word = nextWord();
		out: while (true) {
			switch (word.getType()) {
			case STRING:
			case INT:
			case REAL:
			case IDENTIFIER:
			case READ:
			case WRITE:
			case IF:
			case WHILE:
				node.addLink(oneSentence(word));
				break;
			case LEFT_BRACKET:
				node.addLink(oneBlock());
				break;
			case DIVISION:
				node.addLink(oneComment());
				break;
			case RIGHT_CURLY_BRACES:
				break out;
			default:
				ThrowMyException.throwMyException(word,
						ErrorNum.ILLEGAL_SENTENCE_START);
			}
			word = nextWord();
		}
		return node;
	}

	/**
	 * 解析一个程序语句
	 */
	private Node oneSentence(Word preWord) {
		Node node = null;

		switch (preWord.getType()) {
		case STRING:
		case INT:
		case REAL:
		case IDENTIFIER:
			node = oneAssign(preWord);
			break;
		case READ:
			node = oneInput();
			Word word = nextWord();
			if (word.getType() != TokenType.SEMICOLON) {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_SEMICOLON);
			}
			break;
		case WRITE:
			node = oneOutput();
			break;
		case IF:
			node = oneSelect();
			break;
		case WHILE:
			node = oneLoop();
			break;
		case SEMICOLON:
			node = new Node();
			node.setType(Node.NODE_TYPE.SEMICOLON);
			node.setValue(TokenType.SEMICOLON.toString());
			break;
		default:
			ThrowMyException.throwMyException(preWord,
					ErrorNum.ILLEGAL_SENTENCE_START);
		}
		return node;
	}

	/**
	 * 注释
	 */
	private Node oneComment() {
		Node node = null;
		Word word = nextWord();

		switch (word.getType()) {
		case DIVISION:
			node = oneLineComment();
			break;
		case MULTI:
			node = oneBlockComment();
			break;
		default:
			System.out.println(word.getType() + " " + word.getValue());
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_DIV_OR_MUL);
		}

		return node;
	}

	/**
	 * 行注释
	 */
	private Node oneLineComment() {
		Node node = new Node();
		node.setType(Node.NODE_TYPE.COMMENT);
		node.setValue(wordAnalysis.getUtilEndOfLine());
		return node;
	}

	/**
	 * 块注释
	 */
	private Node oneBlockComment() {
		Node node = new Node();
		node.setType(Node.NODE_TYPE.COMMENT);
		StringBuffer sb = new StringBuffer();
		
		int state = 0;
		while (true) {
			char ch = wordAnalysis.getch();
			if (ch == TokenType.MULTI.toString().charAt(0)) {
				state = 1;
				continue;
			} else if (ch == TokenType.DIVISION.toString().charAt(0)) {
				if (state == 1) {
					break;
				} else {
					sb.append(ch);
				}
			} else {
				if (state == 1) {
					state = 0;
					sb.append(TokenType.MULTI.toString());
				}
				sb.append(ch);
			}
		}
		node.setValue(sb.toString());
		return node;
	}

	/**
	 * 赋值语句
	 */
	private Node oneAssign(Word preWord) {
		Node node = null;

		switch (preWord.getType()) {
		case STRING:
		case INT:
		case REAL:
			node = assignWithType(preWord);
			break;
		case IDENTIFIER:
			node = assignWithoutType(preWord);
			break;
		default:
			ThrowMyException.throwMyException(preWord,
					ErrorNum.ILLEGAL_SENTENCE_START);
		}
		return node;
	}

	/**
	 * 带类型声明的赋值语句
	 * 
	 * @return
	 */
	private Node assignWithType(Word preWord) {
		Node node = new Node();
		Word word = nextWord();
		if (word.getType() == TokenType.LEFT_MEDIUM_BRACKET) {
			word = nextWord();
			if (word.getType() == TokenType.RIGHT_MEDIUM_BRACKET) {
				switch (preWord.getType()) {
				case STRING:
					node.addLink(new Node(NODE_TYPE.STRING_ARR,
							preWord.getValue() + "[]"));
					break;
				case INT:
					node.addLink(new Node(NODE_TYPE.INT_ARR,
							preWord.getValue() + "[]"));
					break;
				case REAL:
					node.addLink(new Node(NODE_TYPE.REAL_ARR,
							preWord.getValue() + "[]"));
					break;
				default:
					ThrowMyException.throwMyException(preWord,
							ErrorNum.ILLEGAL_SENTENCE_START);
				}
				word = nextWord();
			} else {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_RIGHT_MEDIUM_BRACKET);
			}
		} else {
			switch (preWord.getType()) {
			case STRING:
				node.addLink(new Node(NODE_TYPE.STRING, preWord.getValue()));
				break;
			case INT:
				node.addLink(new Node(NODE_TYPE.INT, preWord.getValue()));
				break;
			case REAL:
				node.addLink(new Node(NODE_TYPE.REAL, preWord.getValue()));
				break;
			default:
				ThrowMyException.throwMyException(preWord,
						ErrorNum.ILLEGAL_SENTENCE_START);
			}
		}

		if (word.getType() != TokenType.IDENTIFIER) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_IDENTIFIER);
		}

		node.addLink(new Node(NODE_TYPE.IDENTIFIER, word.getValue()));
		word = nextWord();

		if (word.getType() == TokenType.SEMICOLON) {
			node.setType(NODE_TYPE.DEFINE);
		} else if (word.getType() == TokenType.ASSIGN) {
			node.setType(NODE_TYPE.ASSIGN_WITH_TYPE);
			node.addLink(new Node(NODE_TYPE.ASSIGN, word.getValue()));
			assignCommon(node);
		} else {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_SEMI_OR_ASSIGN);
		}

		word = nextWord();
		if (word.getType() != TokenType.SEMICOLON) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_SEMICOLON);
		}

		return node;
	}

	/**
	 * 不带类型声明的赋值语句
	 * 
	 * @return
	 */
	private Node assignWithoutType(Word preWord) {
		Node node = new Node();
		node.setType(NODE_TYPE.ASSIGN_WITHOUT_TYPE);

		Word word = nextWord();

		if (word.getType() == TokenType.LEFT_MEDIUM_BRACKET) {
			word = nextWord();
			if (word.getType() == TokenType.INT_VALUE) {
				String index = word.getValue();
				word = nextWord();
				if (word.getType() == TokenType.RIGHT_MEDIUM_BRACKET) {
					node.addLink(new Node(NODE_TYPE.IDENTI_ARR_ELEMENT,
							preWord.getValue() + "[" + index + "]"));
					word = nextWord();
				} else {
					String errorMessage = ErrorNum.EXPECTED_RIGHT_MEDIUM_BRACKET
							.toString() + " Line:" + word.getLineNum()
							+ " Position:" + word.getStartLocation();
					throw new MyException(errorMessage);
				}
			} else {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_INT_VAL);
			}
		} else {
			node.addLink(new Node(NODE_TYPE.IDENTIFIER, preWord.getValue()));
		}

		if (word.getType() == TokenType.ASSIGN) {
			node.addLink(new Node(NODE_TYPE.ASSIGN, word.getValue()));
			assignCommon(node);
		} else {
			ThrowMyException.throwMyException(word, ErrorNum.EXPECTED_ASSIGN);
		}

		word = nextWord();
		if (word.getType() != TokenType.SEMICOLON) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_SEMICOLON);
		}

		return node;
	}

	/**
	 * 赋值语句公共操作
	 * 
	 * @param node
	 */
	private void assignCommon(Node node) {
		Word word = nextWord();
		switch (word.getType()) {
		case STRING_VALUE:
			node.addLink(new Node(NODE_TYPE.STRING_VAL, word.getValue()));
			break;
		case READ:
			node.addLink(oneInput());
			break;
		case LEFT_CURLY_BRACES:
			node.addLink(oneArr());
			break;
		case IDENTIFIER:
			isArrElement(node, word);
			break;
		case INT_VALUE:
		case REAL_VALUE:
		case PLUS:
		case MINUS:
		case LEFT_BRACKET:
			node.addLink(oneArithmetic(word));
			break;
		default:
			ThrowMyException.throwMyException(word, ErrorNum.ILLEGAL_RIGHT_VAL);
		}
	}

	/**
	 * 输入语句
	 */
	private Node oneInput() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.INPUT);

		if (word.getType() != TokenType.LEFT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_LEFT_BRACKET);
		}
		word = nextWord();
		switch (word.getType()) {
		case STRING:
			node.setValue("A string from input.");
			break;
		case INT:
			node.setValue("A int from input.");
			break;
		case REAL:
			node.setValue("A real from input");
			break;
		default:
			ThrowMyException.throwMyException(word,
					ErrorNum.ILLEGAL_TYPE_FOR_INPUT);
		}

		word = nextWord();
		if (word.getType() != TokenType.RIGHT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_RIGHT_BRACKET);
		}

		return node;
	}

	/**
	 * 输出语句
	 */
	private Node oneOutput() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.OUTPUT);

		if (word.getType() != TokenType.LEFT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_LEFT_BRACKET);
		}
		word = nextWord();
		switch (word.getType()) {
		case STRING_VALUE:
			node.addLink(new Node(NODE_TYPE.STRING, word.getValue()));
			break;
		case INT_VALUE:
		case REAL_VALUE:
		case PLUS:
		case MINUS:
		case LEFT_BRACKET:
			node.addLink(oneArithmetic(word));
			break;
		case IDENTIFIER:
			isArrElement(node, word);
			break;
		default:
			ThrowMyException.throwMyException(word,
					ErrorNum.ILLEGAL_TYPE_FOR_OUTPUT);
		}

		word = nextWord();
		if (word.getType() != TokenType.RIGHT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_RIGHT_BRACKET);
		}
		word = nextWord();
		if (word.getType() != TokenType.SEMICOLON) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_SEMICOLON);
		}

		return node;
	}

	/**
	 * 选择逻辑
	 */
	private Node oneSelect() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.SELECT);
		if (word.getType() != TokenType.LEFT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_LEFT_BRACKET);
		}
		int type = 0;
		do {
			node.addLink(oneSelectBranch(type));
			word = nextWord();

			// 如果已经遇到了一个else分支，即type==1,则该选择逻辑结束
			if ((type == 1) || ((word.getType() != TokenType.ELSE)
					&& (word.getType() != TokenType.ELSEIF))) {
				wordBuffer.push(word);
				break;
			}
			if (word.getType() == TokenType.ELSE) {
				type = 1;
			}

			if (type == 0) {
				word = nextWord();
				if (word.getType() != TokenType.LEFT_BRACKET) {
					ThrowMyException.throwMyException(word,
							ErrorNum.EXPECTED_LEFT_BRACKET);
				}
			}

		} while (true);

		return node;

	}

	/**
	 * 一个选择分支
	 * 
	 * @param type
	 *            0代表有条件的选择分支，1代表无条件选择分支
	 * @return
	 */
	private Node oneSelectBranch(int type) {
		Word word = null;
		Node node = new Node(NODE_TYPE.ONE_SELECT_BRANCH);

		if (type == 0) {
			node.addLink(oneLogic());

			word = nextWord();
			if (word.getType() != TokenType.RIGHT_BRACKET) {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_RIGHT_BRACKET);
			}

			word = nextWord();
			if (word.getType() != TokenType.LEFT_CURLY_BRACES) {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_LEFT_CURLY_BRA);
			}

			node.addLink(oneBlock());
		} else {
			word = nextWord();
			if (word.getType() != TokenType.LEFT_CURLY_BRACES) {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_LEFT_CURLY_BRA);
			}

			node.addLink(oneBlock());
		}

		return node;
	}

	/**
	 * 循环
	 */
	private Node oneLoop() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.LOOP);

		if (word.getType() != TokenType.LEFT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_LEFT_BRACKET);
		}
		node.addLink(oneLogic());
		word = nextWord();
		if (word.getType() != TokenType.RIGHT_BRACKET) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_RIGHT_BRACKET);
		}
		word = nextWord();
		if (word.getType() != TokenType.LEFT_CURLY_BRACES) {
			ThrowMyException.throwMyException(word,
					ErrorNum.EXPECTED_LEFT_CURLY_BRA);
		}
		node.addLink(oneBlock());

		return node;

	}

	/**
	 * 逻辑表达式
	 * 
	 * @return
	 */
	private Node oneLogic() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.LOGIC);

		switch (word.getType()) {
		case INT_VALUE:
		case REAL_VALUE:
		case PLUS:
		case MINUS:
		case LEFT_BRACKET:
			node.addLink(oneArithmetic(word));
			break;
		case IDENTIFIER:
			isArrElement(node, word);
			break;
		default:
			ThrowMyException.throwMyException(word, ErrorNum.ILLEGAL_LOGIC_EXP);
		}

		word = nextWord();
		switch (word.getType()) {
		case EQUAL:
		case GREATER:
		case NOT_GREATER:
		case LESS:
		case NOT_LESS:
		case NOT_EQUAL:
			node.addLink(new Node(NODE_TYPE.LOGIC_OPERATOR, word.getValue()));
			break;
		default:
			ThrowMyException.throwMyException(word, ErrorNum.ILLEGAL_LOGIC_OPE);
		}

		word = nextWord();
		switch (word.getType()) {
		case INT_VALUE:
		case REAL_VALUE:
		case PLUS:
		case MINUS:
		case LEFT_BRACKET:
			node.addLink(oneArithmetic(word));
			break;
		case IDENTIFIER:
			isArrElement(node, word);
			break;
		default:
			ThrowMyException.throwMyException(word, ErrorNum.ILLEGAL_LOGIC_EXP);
		}

		return node;
	}

	/**
	 * 数组
	 * 
	 * @return
	 */
	private Node oneArr() {
		Word word = nextWord();
		Node node = new Node(NODE_TYPE.ARR_VAL);

		out:do {
			switch (word.getType()) {
			case INT_VALUE:
				node.addLink(new Node(NODE_TYPE.INT_VAL, word.getValue()));
				break;
			case REAL_VALUE:
				node.addLink(new Node(NODE_TYPE.REAL_VAL, word.getValue()));
				break;
			case STRING_VALUE:
				node.addLink(new Node(NODE_TYPE.STRING_VAL, word.getValue()));
				break;
			case RIGHT_CURLY_BRACES:
				break out;
			default:
				ThrowMyException.throwMyException(word,
						ErrorNum.ILLEGAL_ARR_ELE);
			}
			word = nextWord();
			if (word.getType() == TokenType.RIGHT_CURLY_BRACES) {
				break;
			} else if (word.getType() == TokenType.COMMA) {
				word = nextWord();
			} else {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_RCB_OR_COMMA);
			}
		} while (true);

		return node;
	}

	/**
	 * 在遇到标识符时判断有没有跟中括号表示数组元素
	 */
	private void isArrElement(Node node, Word word) {
		String identifier = word.getValue();
		word = nextWord();
		if (word.getType() == TokenType.LEFT_MEDIUM_BRACKET) {
			word = nextWord();
			if (word.getType() == TokenType.INT_VALUE) {
				String index = word.getValue();
				word = nextWord();
				if (word.getType() == TokenType.RIGHT_MEDIUM_BRACKET) {
					node.addLink(new Node(NODE_TYPE.IDENTI_ARR_ELEMENT,
							identifier + "[" + index + "]"));
				} else {
					ThrowMyException.throwMyException(word,
							ErrorNum.EXPECTED_RIGHT_MEDIUM_BRACKET);
				}
			} else {
				ThrowMyException.throwMyException(word,
						ErrorNum.EXPECTED_INT_VAL);
			}
		} else {
			node.addLink(new Node(NODE_TYPE.IDENTIFIER, identifier));
			wordBuffer.push(word);
		}
	}

	/**
	 * 算数表达式
	 * 
	 * @return
	 */
	private Node oneArithmetic(Word preWord) {
		Node node = new Node(NODE_TYPE.ARITHMETIC);
		List<String> strs = new ArrayList<String>();
		List<Word> words = new ArrayList<Word>();
		Word word = preWord;

		do {
			String value = word.getValue();
			if (Formaluetree.isDigit(value)) {
				strs.add(word.getValue());
				words.add(word);
			} else if (value.equals("+") || value.equals("-")) {
				// 做是正负号还是加减号的判断,flag为true时是正负号，为false时是加减号
				boolean flag = false;
				if (strs.size() == 0) {
					flag = true;
				} else {
					String preStr = strs.get(strs.size() - 1);
					if (Formaluetree.isAriOperator(preStr)
							|| preStr.equals("(")) {
						flag = true;
					}
				}

				if (flag) {
					word = nextWord();
					if (!Formaluetree.isDigit(word.getValue())) {
						ThrowMyException.throwMyException(word,
								ErrorNum.EXPECTED_DIGIT);
					}
					strs.add(value + word.getValue());
					words.add(word);
				} else {
					strs.add(word.getValue());
					words.add(word);
				}

			} else {
				strs.add(word.getValue());
				words.add(word);
			}

			word = nextWord();
			if (!Formaluetree.isAriElement(word.getValue())) {
				wordBuffer.push(word);
				break;
			}

		} while (true);

		opeRightBracket(strs, words);

		node.addLink(
				Formaluetree.createBinaryTree(strs.toArray(new String[] {})));

		return node;
	}

	/**
	 * 处理多读入的右括号
	 * 
	 * @param strs
	 */
	private void opeRightBracket(List<String> strs, List<Word> words) {
		// 判断是否多了右括号
		if (strs.get(strs.size() - 1)
				.equals(TokenType.RIGHT_BRACKET.toString())) {
			if (ListUtil.count(strs, ")") > ListUtil.count(strs, "(")) {
				strs.remove(strs.size() - 1);
				Word word = words.get(words.size() - 1);
				wordBuffer.push(word);
				words.remove(word);
				opeRightBracket(strs, words);
			}
		}
	}
}
