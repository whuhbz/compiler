package system;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法树节点
 * @author 10330
 *
 */
public class Node {
	public static enum NODE_TYPE{
		PROGRAM,BLOCK,ASSIGN_WITH_TYPE,
		ASSIGN_WITHOUT_TYPE,SEMICOLON,COMMENT,
		STRING,INT,REAL,STRING_ARR,
		INT_ARR,REAL_ARR,DEFINE,IDENTIFIER,
		STRING_VAL,INT_VAL,REAL_VAL,
		IDENTI_ARR_ELEMENT,ASSIGN,INPUT,
		OUTPUT,ONE_SELECT_BRANCH,SELECT,
		LOOP,LOGIC,LOGIC_OPERATOR,
		ARR_VAL,ARITHMETIC,ARI_OPERATOR,
		BOOLEAN
	}
	protected NODE_TYPE type;
	
	

	public Node(NODE_TYPE type) {
		super();
		this.type = type;
	}

	public Node(NODE_TYPE type, String value) {
		super();
		this.type = type;
		this.value = value;
	}
	protected ArrayList<Node> links;
	protected String value;
	
	public Node() {
		super();
	}
	
	public NODE_TYPE getType() {
		return type;
	}
	
	public void setType(NODE_TYPE type) {
		this.type = type;
	}
	public ArrayList<Node> getLinks() {
		return links;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void addLink(Node node) {
		if(links == null) {
			links = new ArrayList<Node>();
		}
		this.links.add(node);
	}
}
