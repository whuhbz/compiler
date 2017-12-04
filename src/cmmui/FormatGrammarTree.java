package cmmui;

import java.util.List;
import system.Node;
import system.Node.NODE_TYPE;

public class FormatGrammarTree {
	/**
	 * 遍历语法树形成字符串形式
	 * @param node 语法树结点
	 * @return 排版后的语法树字符串
	 */
	public String travel(Node node){
		StringBuffer stringBuffer = new StringBuffer();
		// 如果根结点为空
		if (node == null) {
			return null;
		}
		// 如果传入的结点不是根结点
		if (node.getType() != NODE_TYPE.PROGRAM) {
			return null;
		}
		stringBuffer.append(node.getType()+"\n");
		List<Node> childs = node.getLinks();
		
		for (Node child : childs) {
			stringBuffer.append(indent(child, 1));
			//stringBuffer.append("\n");
		}
		return stringBuffer.toString();

		
	    
	}
	
	/**
	 * 获取一个语法树子节点的字符串
	 * @param node 语法树子节点
	 * @param level 该结点的层级，用于缩进
	 * @return 子节点字符串
	 */
	private String indent(Node node,int level){
		StringBuffer sBuffer = new StringBuffer();
		//缩进
		for(int i = 0;i<level;i++){
			if(i==level-1){
				sBuffer.append(" |");
				break;
			}
			sBuffer.append("  ");
		}
		//
		//如果是一个终结符
		if(node.getLinks() == null || node.getLinks().size() == 0){
			sBuffer.append(node.getType()+"<"+node.getValue()+">"+"\n");
		}
		//如果是一个非终结符
		else{
			sBuffer.append(node.getType()+"\n");
			List<Node> childs = node.getLinks();
			for (Node child : childs) {
				sBuffer.append(indent(child, level+1));
			}
		}
		
		return sBuffer.toString();
	}
}
