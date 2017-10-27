package system;

import java.util.ArrayList;
import java.util.Arrays;

public class NumNode extends Node {
	
	public NumNode(String value) {
		super();
		this.value = value;
		this.links = new ArrayList<Node>();
		links.addAll(Arrays.asList(null, null));
	}
	
	public NumNode(String value, Node left, Node right) {
		super();
		this.value = value;
		this.links = new ArrayList<Node>();
		links.addAll(Arrays.asList(left, right));
	}
	
	public Node getLchild() {
		return links.get(0);
	}
	
	public Node getRchild() {
		return links.get(1);
	}
	
	public void setLchild(NumNode node) {
		links.add(0, node);
	}

	public void setRchild(NumNode node) {
		links.add(1, node);
	}
}
