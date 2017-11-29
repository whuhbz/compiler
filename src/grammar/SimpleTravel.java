package grammar;

import java.util.LinkedList;

import javax.swing.tree.TreeNode;

import system.Node;

public class SimpleTravel implements TravelGrammarTree {

	@Override
	public void travel(Node root) {
		if (root == null)
			return;
		LinkedList<Node> list = new LinkedList<Node>();
		list.add(root);
		Node currentNode;
		int curLevelCount = 1, nextLevelCount = 0;  
		while (!list.isEmpty()) {
			currentNode = list.poll();
			System.out.format("%-25s", currentNode.getType() + "<<" + ((currentNode.getValue() == null) ? "" : currentNode.getValue()) + ">>");
			curLevelCount--;  
			if (currentNode.getLinks() != null) {
				for (Node node : currentNode.getLinks()) {
					if (node != null) {
						list.add(node);
						 nextLevelCount++;
					}
				}
			}
			if(0 == curLevelCount)  
	        {  
	            System.out.println();
	            curLevelCount = nextLevelCount;  
	            nextLevelCount = 0;  
	        }  
		}
	}

}
