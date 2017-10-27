package constant;

public enum ErrorNum {
	ILLEGAL_END("Illegal end character."),ILLEGAL_ESCAPE("Illegal escape character"),
	ILLEGAL_STRING("Illegal string."),ILLEGAL_NUMBER("Illegal number."),
	ILLEGAL_BOOLEAN("Illegal bolean."),ILLEGAL_NULL("Illegal null."),
	ILLEGAL_CHAR_SEQUENCE("Charsequence that are not valid."),EMPTY_DOC("Document is empty."),
	EXPECTED_START_SYMBOL("Expected <{ or [>."),ILLEGAL_ELEMENTS_END("Expected <, or ]>"),
	ILLEGAL_OBJ_CON("Illegal token of json object."),
	ILLEGAL_PAIR_END("Expected<, or }>."),ILLEGAL_PAIR_START("Key of the pair must be a string."),
	PAIR_WITHOUT_COLON("Expected<:>."),ILLEGAL_ARR_CON("Illegal token of json array."),
	UNRECOGNIZED_VALUE("Unrecognized value type.");
	
	private ErrorNum(String description) {
		this.description = description;
	}

	private String description;

	@Override
	public String toString() {
		return description;
	}
}
