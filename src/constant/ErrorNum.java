package constant;

public enum ErrorNum {
	ILLEGAL_END("Illegal end character."),ILLEGAL_ESCAPE("Illegal escape character"),
	ILLEGAL_STRING("Illegal string."),ILLEGAL_NUMBER("Illegal number."),
	ILLEGAL_BOOLEAN("Illegal boolean."),ILLEGAL_NULL("Illegal null."),
	ILLEGAL_CHAR_SEQUENCE("Charsequence that are not valid."),EMPTY_DOC("Document is empty."),
	EXPECTED_START_SYMBOL("Expected <{ or [>."),ILLEGAL_ELEMENTS_END("Expected <, or ]>"),
	UNRECOGNIZED_VALUE("Unrecognized value type."),ILLEGAL_SENTENCE_START("Sentence starts with illegal word."),
	EXPECTED_SEMICOLON("Expected<;>."),EXPECTED_DIV_OR_MUL("Expected< / or * >"),
	EXPECTED_RIGHT_MEDIUM_BRACKET("Expected< ] >"), EXPECTED_IDENTIFIER("Expected< identifier  >"),
	EXPECTED_SEMI_OR_ASSIGN("Expected< ; or = >"), EXPECTED_INT_VAL("Expected< int value >"),
	EXPECTED_ASSIGN("Expected<=>"), ILLEGAL_RIGHT_VAL("Illegal right value."),
	EXPECTED_LEFT_BRACKET("Expected<(>"),ILLEGAL_TYPE_FOR_INPUT("Illegal type for input."),
	EXPECTED_RIGHT_BRACKET("Expected<)>"),ILLEGAL_TYPE_FOR_OUTPUT("Illegal type for output."),
	EXPECTED_LEFT_CURLY_BRA("Expected<{>"), ILLEGAL_LOGIC_EXP("Illegal logical expression."),
	ILLEGAL_LOGIC_OPE("Illegal logical operator."), ILLEGAL_ARR_ELE("Illegal array element."),
	EXPECTED_RCB_OR_COMMA("Expected< } or , >"),EXPECTED_DIGIT("Expected< int value or real value >"),
	ILLEGAL_PRO_START("Illegal program start."),ILLEGAL_ARI_ELEMENT("Illegal arithmetic element.");
	
	private ErrorNum(String description) {
		this.description = description;
	}

	private String description;

	@Override
	public String toString() {
		return description;
	}
}
