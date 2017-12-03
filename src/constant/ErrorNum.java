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
	ILLEGAL_PRO_START("Illegal program start."),ILLEGAL_ARI_ELEMENT("Illegal arithmetic element."),
	EXISTED_SYMBOL("Already existed symbol,can't decleare twice"),MISSMATCHED_DATA_TYPE("Missmatched data type"),
	UNDECLARED_IDENTIFIER("Can't use undeclared variable"),UNASSIGNED_IDENTIFIER("Can't use unassigned variable"),
	UNEXPECT_ERROR("Unexpect error"),ILLEGAL_TYPE_FOR_ARR_INDEX("Illegal type for array index"),
	MISMATCHED_TYPE_IN_LOGIC("Can't compare two mismatched types in logic"),
	ILLEGAL_TYPE_IN_ARITHMETIC("An illegal type of element in arthmetic"),
	ILLEGAL_TYPE_IN_ARRAY("An illegal type of element in array"),
	DIFFENRENT_TYPES_IN_ARRAY("There're different types of")
	;
	
	private ErrorNum(String description) {
		this.description = description;
	}

	private String description;

	@Override
	public String toString() {
		return description;
	}
}
