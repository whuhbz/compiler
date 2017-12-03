package grammar;

import system.Node;

public class SimpleTravel2 implements TravelGrammarTree {

	@Override
	public void travel(Node node) {
		// TODO Auto-generated method stub
		if (node == null) {
			return;
		}
		
		System.out.println(printNode(node));
		if(node.getLinks() == null || node.getLinks().size() == 0) {
			System.out.println("This node has no child node.");
			return;
		}
		StringBuffer sb = new StringBuffer("This node's child node(s): ");
		for(Node cnode : node.getLinks()) {
			sb.append(printNode(cnode) + "  ");
		}
		System.out.println(sb.toString());
		for(Node cnode : node.getLinks()) {
			travel(cnode);
		}
	}

	private String printNode(Node node) {
		if (node == null) {
			return null;
		}
		
		return node.getType() + ((node.getValue() == null) ? ""
				: ("<" + node.getValue() + ">"));
	}

}
