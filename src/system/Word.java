package system;

import constant.TokenType;

/**
 * 词模型
 */
public class Word {
	
	private TokenType type;	//类型
	private String value;	//值
	private int lineNum;	//所在行
	private int position;	//行偏移
	public TokenType getType() {
		return type;
	}
	public void setType(TokenType type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	
	
}
