package grammar;

import system.Node;

public class SimpleTravel implements TravelGrammarTree {

	@Override
	public void travel(Node node) {
		// TODO Auto-generated method stub
		if (node == null) {
			return;
		}
		System.out.println(node.getType() + "  " + node.getValue());
		if (node.getLinks() != null) {
			for (Node childNode : node.getLinks()) {
				travel(childNode);
			}
		}
	}

}
