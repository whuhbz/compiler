package constant;

public enum TokenType {
	LEFT_CURLY_BRACES("{"), RIGHT_CURLY_BRACES("}"), 
	LEFT_MEDIUM_BRACKET("["), RIGHT_MEDIUM_BRACKET("]"), COMMA(","), 
	PLUS("+"),MINUS("-"),MULTI("*"),DIVISION("/"),ASSIGN("="),
	LESS("<"),GREATER(">"),EQUAL("=="),NOT_EQUAL("<>"),NOT_GREATER("<="),
	NOT_LESS(">="),LEFT_BRACKET("("),RIGHT_BRACKET(")"),SEMICOLON(";"),
	STRING("string"),IF("if"),ELSE("else"),ELSEIF("elif"),WHILE("while"),READ("read"),WRITE("write"),
	REAL("real"),INT("int"),IDENTIFIER,STRING_VALUE,INT_VALUE,REAL_VALUE;
	
	private String value;
	
	private TokenType(String value){
		this.value = value;
	}
	
	private TokenType(){
		this.value = null;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value;
	}
	
	public static TokenType findTypeByValue(String val){
		for(TokenType tt : values()){
			if(tt.value != null && tt.value.equals(val)){
				return tt;
			}
		}
		return null;
	}
}
