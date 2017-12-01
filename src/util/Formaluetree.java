package util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import system.Node;
import system.Node.NODE_TYPE;
import system.NumNode;

public class Formaluetree {

	private static Pattern numP = Pattern.compile("[+,-]?[0-9]+\\.?[0-9]*");
	private static Pattern idenPattern = Pattern
			.compile("([a-z,A-Z])|(([a-z,A-Z])"
					+ "([a-z,A-Z]|[0-9]|_)*([a-z,A-Z]|[0-9]))"); // 标识符的正则表达式

	/**
	 * 将算术表达式转化成二叉树
	 * 
	 * @param expression
	 *            为了方便，使用字符串数组来存储表达式
	 * @return 二叉树的根节点
	 */
	public static Node createBinaryTree(String[] expression) {

		// 存储操作数的栈
		Stack<String> opStack = new Stack<String>();
		// 存储转换后的逆波兰式的队列
		Queue<String> reversePolish = new LinkedList<String>();

		for (String s : expression) {

			// 如果是数字
			if (isDigit(s) || idenPattern.matcher(s).find()) {

				reversePolish.offer(s);
				// 如果是操作符
			} else if (isOperator(s)) {

				// 是左括号直接入栈
				if ("(".equals(s)) {

					opStack.push(s);
					// 如果是右括号
				} else if (")".equals(s)) {

					// 把离上一个“（”之间的操作符全部弹出来放入逆波兰式的队列中
					while (!opStack.isEmpty()) {

						String op = opStack.peek();
						if (op.equals("(")) {

							opStack.pop();
							break;

						} else {

							reversePolish.offer(opStack.pop());
						}
					}
				} else {

					while (!opStack.isEmpty()) {
						// 如果栈顶元素为"("直接入栈
						if ("(".equals(opStack.peek())) {

							opStack.push(s);
							break;
							// 如果栈顶元素优先级大于s
						} else if (isGreat(opStack.peek(), s)) {

							reversePolish.offer(opStack.pop());

						} else if (isGreat(s, opStack.peek())) {

							opStack.push(s);
							break;
						}

					}
					// 如果栈为空，直接入栈
					if (opStack.isEmpty())

						opStack.push(s);
				}
			}
		}
		// 将剩余的操作符入队
		while (!opStack.isEmpty()) {

			reversePolish.offer(opStack.pop());
		}
		Stack<Node> nodeStack = new Stack<Node>();

		// 将逆波兰式转化成二叉树
		while (!reversePolish.isEmpty()) {

			String s = reversePolish.poll();
			// 以当前的元素的值新建一个节点
			Node node = new Node();
			node.setValue(s);
			// 如果是数字
			if (isDigit(s)) {
				if (s.contains(".")) {
					node.setType(NODE_TYPE.REAL_VAL);
				} else {
					node.setType(NODE_TYPE.INT_VAL);
				}
				nodeStack.push(node);
				// 如果是操作符
			} else if (isOperator(s)) {
				node.setType(NODE_TYPE.ARI_OPERATOR);
				// 从栈里弹出两个节点作为当前节点的左右子节点
				Node rightNode = nodeStack.pop();
				Node leftNode = nodeStack.pop();
				node.addLink(leftNode);
				node.addLink(rightNode);
				// 入栈
				nodeStack.push(node);
			} else {
				if(s.contains("[") && s.contains("]")) {
					node.setValue(null);
					node.setType(NODE_TYPE.IDENTI_ARR_ELEMENT);
					int index = s.indexOf("[");
					
					Node childNode1 = new Node(NODE_TYPE.IDENTIFIER,
							s.substring(0, index));
					Node childNode2 = new Node(NODE_TYPE.INT_VAL, s.substring(index+1,s.length() - 1));
					node.addLink(childNode1);
					node.addLink(childNode2);
				}else {
					node.setType(NODE_TYPE.IDENTIFIER);
				}
				nodeStack.push(node);
			}

		}

		return nodeStack.pop();
	}

	/**
	 * 判断是否为运算符（暂时只判断四则运算的运算符）
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isOperator(String s) {

		if ("(".equals(s) || ")".equals(s) || "+".equals(s) || "-".equals(s)
				|| "*".equals(s) || "/".equals(s))

			return true;

		else

			return false;
	}

	/**
	 * 判断是否是除去括号的运算符
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isAriOperator(String s) {

		if ("+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s))

			return true;

		else

			return false;
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isDigit(String s) {

		Matcher m = numP.matcher(s);

		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAriElement(String s) {
		if (isDigit(s) || isOperator(s) || idenPattern.matcher(s).matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断op1和op2的优先级，如果op1>op2，返回true，如果op1<=op2，返回false
	 * 
	 * @param op1
	 * @param op2
	 * @return
	 */
	static boolean isGreat(String op1, String op2) {

		if (getPriority(op1) > getPriority(op2))

			return true;

		else

			return false;
	}

	/**
	 * 获取运算符的优先级
	 * 
	 * @param op
	 * @return
	 */
	static int getPriority(String op) {

		if ("+".equals(op) || "-".equals(op))

			return 1;

		else if ("*".equals(op) || "/".equals(op))

			return 2;

		else

			throw new IllegalArgumentException("Unsupported operator!");
	}

	/**
	 * 打印出还原的算术表达式
	 * 
	 * @param root
	 */
	static void printMathExpression(NumNode root) {

		if (root != null) {
			if (isOperator(root.getValue()))
				System.out.print("(");
			printMathExpression((NumNode) root.getLchild());
			System.out.print(root.getValue());
			printMathExpression((NumNode) root.getRchild());
			if (isOperator(root.getValue()))
				System.out.print(")");
		}
	}
}
