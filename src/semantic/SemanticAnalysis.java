package semantic;

import java.util.List;

import constant.ErrorNum;
import grammar.TravelGrammarTree;
import system.Node;
import system.ThrowMyException;
import system.Node.NODE_TYPE;
/**
 * 语义分析
 * 构建符号表
 * 判断变量是否声明
 * 判断变量是否已初始化
 * 判断类型正确
 * @author miaoyu
 *
 */
public class SemanticAnalysis implements TravelGrammarTree{

	/**
	 * 从根结点开始遍历语法树
	 * @param node 此处应传入根结点
	 */
	@Override
	public void travel(Node node) {
		// TODO Auto-generated method stub
		//如果根结点为空
		if(node==null){
			return ;
		}
		//如果传入的结点不是根结点
		if(node.getType()!=NODE_TYPE.PROGRAM){
			return;
		}
		//由根结点开始遍历语法树
		travelNode(node);
	}
	
	private void travelNode(Node node){
		
		switch (node.getType()) {
		//如果读到根结点或Block
		case PROGRAM:
		case BLOCK:
			for(Node n:node.getLinks()){
				travelNode(n);
			}
			break;
			
		//如果读到声明语句
		case DEFINE:
			define(node);
			break;
		
		//如果读到带有赋值的声明语句
		case ASSIGN_WITH_TYPE:
			assign_with_type(node);
			break;
		
		//如果读到单纯的赋值语句
		case ASSIGN_WITHOUT_TYPE:
			assign_without_type(node);
			break;
			
		//如果读到while循环
		case LOOP:
			loop(node);
			break;
		//如果读到选择逻辑
		case SELECT:
			select(node);
			break;
			
		//如果读到输入语句
		case INPUT:
			read(node);
			break;
			
		//如果读到输出语句
		case OUTPUT:
			write(node);
			break;

		//如果读到注释
		case COMMENT:
			break;
		default:
			System.out.println(node.getType().toString());
			break;
		}//end switch
	}//end travelNode()
	
	
	/**
	 * 对声明语句的语义分析
	 * @param node 声明语句结点
	 * 子节点序列： 声明类型   变量名
	 */
	private void define(Node node){
		//声明的变量
		Variable variable = null;
		//声明语句子节点序列
		List<Node> childs = node.getLinks();
		//声明的类型
		NODE_TYPE variableType = childs.get(0).getType();
		//变量名
		String variableName = childs.get(1).getValue();
		//如果符号表中已存在该符号
		if(SymbolTable.symbolTable.get(variableName)!=null){
			ThrowMyException.throwMyExcepton(ErrorNum.EXISTED_SYMBOL);
		}
		variable = new Variable(variableName, variableType, false);
		SymbolTable.symbolTable.put(variableName, variable);
	}
	

	/**
	 * 对带声明的赋值语句的语义分析
	 * @param node 带声明的赋值语句结点
	 * 子节点序列：声明类型  变量名  等号 
	 * 表达式 （表达式构成元素为常量，整形、实数变量（不为单独存在）或整形、实数数组元素  ）或字符串  或数组  或read语句
	 */
	private void assign_with_type(Node node){
		//声明的变量
		Variable variable = null;
		//声明的类型与赋值的类型
		NODE_TYPE variableType = null;
		//变量名
		String variableName = null;
		//带声明的赋值语句子节点序列
		List<Node> childs = node.getLinks();
		//记录声明类型与变量名
		variableType = childs.get(0).getType();
		variableName = childs.get(1).getValue();
		
		//检查变量是否已经被声明过
		if(SymbolTable.symbolTable.get(variableName)!=null){
			ThrowMyException.throwMyExcepton(ErrorNum.EXISTED_SYMBOL);
		}
		
		//根据用于赋值的结点的类型进行分析
		switch (childs.get(3).getType()) {
		//如果是表达式
		case ARITHMETIC:
			NODE_TYPE arithmeticType = arithmetic(childs.get(3));
			if (arithmeticType != variableType
					&& !(arithmeticType == NODE_TYPE.INT && variableType == NODE_TYPE.REAL)) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		//如果是字符串
		case STRING_VAL:
			//如果数据类型不匹配
			if(variableType!=NODE_TYPE.STRING_VAL){
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		//如果是数组
		case ARR_VAL:
			NODE_TYPE arrType = arr_val(childs.get(3));
			if (arrType != variableType && !(arrType == NODE_TYPE.INT && variableType == NODE_TYPE.REAL)) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
			
		//如果是输入语句
		case INPUT:
			String inputType = childs.get(3).getValue();
			switch (inputType) {
			case "A int from input.":
				if(variableType!=NODE_TYPE.INT&&variableType!=NODE_TYPE.REAL){
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;
			case "A real from input":
				if(variableType!=NODE_TYPE.REAL){
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;
			case "A string from input.":
				if(variableType!=NODE_TYPE.STRING){
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;

			default:
				break;
			}
			break;
		

		default:
			System.out.println("带声明的赋值语句用于赋值的结点的类型还有："+childs.get(3).getType().toString());
			break;
		}
		variable = new Variable(variableName, variableType,true);
		SymbolTable.symbolTable.put(variableName, variable);
	}
	
	
	/**
	 * 对单纯赋值语句的语义分析
	 * @param node 单纯赋值语句结点
	 * 子节点序列：标识符或数组元素   等号   表达式 （表达式构成元素为常量，整形、实数变量（不为单独存在）或整形、实数数组元素  ）或字符串  或数组  或read语句
	 */
	private void assign_without_type(Node node){
		//声明的变量
		Variable variable = null;
		//声明的类型
		NODE_TYPE variableType = null;
		//变量名
		String variableName = null;
		//赋值语句子节点序列
		List<Node> childs = node.getLinks();
		
		//判断被赋值的变量的数据类型
		//如果是对标识符赋值
		if(childs.get(0).getType()==NODE_TYPE.IDENTIFIER){
			//记录变量名
			variableName = childs.get(0).getValue();
			variable = SymbolTable.symbolTable.get(variableName);
			//如果变量未声明
			if(variable==null){
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			variableType = variable.type;
		}
		//如果是对数组元素赋值
		else if(childs.get(0).getType()==NODE_TYPE.IDENTI_ARR_ELEMENT){
			Node element = childs.get(0);
			variableType = identi_arr_element(element);
		}else{
			System.out.println("还可以对以下类型结点赋值："+childs.get(0).getType().toString());
		}
		
		
		// 根据用于赋值的结点的类型进行分析
		switch (childs.get(2).getType()) {
		// 如果是表达式
		case ARITHMETIC:
			NODE_TYPE arithmeticType = arithmetic(childs.get(2));
			if (arithmeticType != variableType
					&& !(arithmeticType == NODE_TYPE.INT && variableType == NODE_TYPE.REAL)) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		// 如果是字符串
		case STRING_VAL:
			// 如果数据类型不匹配
			if (variableType != NODE_TYPE.STRING_VAL) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		// 如果是数组
		case ARR_VAL:
			NODE_TYPE arrType = arr_val(childs.get(2));
			if (arrType != variableType && !(arrType == NODE_TYPE.INT && variableType == NODE_TYPE.REAL)) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		// 如果是输入语句
		case INPUT:
			String inputType = childs.get(2).getValue();
			switch (inputType) {
			case "A int from input.":
				if (variableType != NODE_TYPE.INT&&variableType!=NODE_TYPE.REAL) {
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;
			case "A real from input":
				if (variableType != NODE_TYPE.REAL) {
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;
			case "A string from input.":
				if (variableType != NODE_TYPE.STRING) {
					ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
				}
				break;

			default:
				break;
			}
			break;

		default:
			System.out.println("带声明的赋值语句用于赋值的结点的类型还有：" + childs.get(3).getType().toString());
			break;
		}
		variable = new Variable(variableName, variableType,true);
		SymbolTable.symbolTable.put(variableName, variable);
		
	}
	
	
	/**
	 * 对逻辑结构的语义分析
	 * 子节点序列 ： ONE_SELECT_BRANCH+
	 * @param node 逻辑结构结点
	 */
	private void select(Node node){
		//size
		for(Node branch: node.getLinks()){
			one_select_branch(branch);
		}
		
	}
	
	/**
	 * 对一条逻辑分支的语义分析
	 * 对于if(){}  elif(){} 子节点序列: LOGIC BLOCK
	 * 对于 else{} 子节点序列： BLOCk
	 * @param node 逻辑分支结点
	 */
	private void one_select_branch(Node node){
		//根据逻辑分支类型进行分析
		//如果是else
		if(node.getLinks().size()==1){
			//对BLOCK结点进行分析
			travelNode(node.getLinks().get(0));
		}
		//如果是if或elif
		else{
			//分支的逻辑运算结点
			Node logic = node.getLinks().get(0);
			
			
			//对分支的逻辑运算结点进行分析
			logic(logic);
			//对BLOCK结点进行分析
			travelNode(node.getLinks().get(1));
		
		}
		
	}
	
	
	/**
	 * 对循环结构的语义分析
	 * 循环结构子结点序列： LOGIC BLOCK
	 * @param node 循环结构结点
	 */
	private void loop(Node node){
		//对逻辑运算结点进行分析
		logic(node.getLinks().get(0));
		//对BLOCK结点进行分析
		travelNode(node.getLinks().get(1));
	}
	
	/**
	 * 对逻辑运算的语义分析
	 * 对于LOGIC 子节点序列：ARITHMETIC   LOGIC_OPERATOR  ARITHMETIC
	 * @param node 逻辑运算结点
	 */
	private void logic(Node node){
		//逻辑运算节点的子节点序列
		List<Node> childs = node.getLinks();
		//左结点
		Node left = childs.get(0);
		//右结点
		Node right = childs.get(2);
		NODE_TYPE left_type =null,right_type = null;
		//判断逻辑运算结点中进行判断的双方数据类型是否一致
		//判断逻辑运算符左侧部分
		if(left.getType()==NODE_TYPE.ARITHMETIC){
			left_type = arithmetic(left);
		}else{
			ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_LOGICAL);
		}
		
		//判断逻辑运算符右侧部分
		if(right.getType()==NODE_TYPE.ARITHMETIC){
			right_type = arithmetic(right);
		}else{
			ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_LOGICAL);
		}
		
		//判断逻辑运算左右两侧数据类型是否相匹配
		//如果匹配
		if(left_type==right_type||
				(left_type==NODE_TYPE.INT&&right_type==NODE_TYPE.REAL)||
				(left_type==NODE_TYPE.REAL&&right_type==NODE_TYPE.INT)){
			//中间代码
		}
		//如果不匹配
		else{
			ThrowMyException.throwMyExcepton(ErrorNum.MISMATCHED_TYPE_IN_LOGIC);
		}
	}
	
	/**
	 * 对输入语句的分析，主要目的为添加相应的中间代码
	 * @param node 输入语句结点，此处的输入语句为单独成句的read(),不参与赋值
	 */
	private void read(Node node){
		
	}
	
	/**
	 * 对输出语句的分析，主要目的为添加相应的中间代码
	 * @param node 输出语句结点，此处的输入语句为单独成句的write()
	 */
	private void write(Node node){
		
	}
	

	/**
	 * 对表达式的分析
	 * 表达式元素可以为（整型、实数型常数，标识符，数组元素），特殊情况为整个表达式只有一个标识符为字符串类型
	 * @param ari_node 表达式结点
	 * @return 表达式的数据类型  INT REAL STRING
	 */
	private NODE_TYPE arithmetic(Node ari_node){
		//算术表达式的类型
		NODE_TYPE ari_type = null;
		//获取二叉树的根结点
		Node root = ari_node.getLinks().get(0);
		//判断根结点类型,分两种情况，算数运算符或其他
		switch (root.getType()) {
		case INT_VAL:
			ari_type = NODE_TYPE.INT;			
			break;
		case REAL_VAL:
			ari_type = NODE_TYPE.REAL;
			break;
		case IDENTIFIER:
			NODE_TYPE t = identifier(root);
			switch (t) {
			case INT:
			case REAL:
			case STRING:
				ari_type = t;
				break;
			default:
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
				break;
			}
			break;
		case IDENTI_ARR_ELEMENT:
			ari_type = identi_arr_element(root);
			break;
		//如果是操作运算符
		case ARI_OPERATOR:
			//获取操作运算符的两个表达式子式
			List<Node> childs = root.getLinks();
			//两个表达式子式的数据类型
			NODE_TYPE child_type1 = null,child_type2 = null;
			child_type1 = child_arithmetic(childs.get(0));
			child_type2 = child_arithmetic(childs.get(1));
			
			if(child_type1==child_type2){
				ari_type = child_type1;
			}else {
				ari_type = NODE_TYPE.REAL;
			}
			break;
		default:
			ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
			break;
		}
		
		
		return ari_type;
	}
	
	/**
	 * 递归函数，用于获得一个子表达式的类型 子表达式中不能再出现STRING类型
	 * @param ari_node 子表达式根结点
	 * @return 子表达式的类型  INT REAL
	 */
	private NODE_TYPE child_arithmetic(Node ari_node){
		NODE_TYPE ari_type = null;

		// 判断根结点类型,分两种情况，算数运算符或其他
		switch (ari_node.getType()) {
		case INT_VAL:
			ari_type = NODE_TYPE.INT;
			break;
		case REAL_VAL:
			ari_type = NODE_TYPE.REAL;
			break;
		case IDENTIFIER:
			switch (SymbolTable.symbolTable.get(ari_node.getValue()).type) {
			case INT:
			case REAL:
				ari_type = SymbolTable.symbolTable.get(ari_node.getValue()).type;
				break;
			default:
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
				break;
			}
			break;
		case IDENTI_ARR_ELEMENT:
			NODE_TYPE type = identi_arr_element(ari_node);
			if(type==NODE_TYPE.INT||type==NODE_TYPE.REAL){
				ari_type = type;
			}else{
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
			}
			break;
		// 如果是操作运算符
		case ARI_OPERATOR:
			// 获取操作运算符的两个表达式子式
			List<Node> childs = ari_node.getLinks();
			// 两个表达式子式的数据类型
			NODE_TYPE child_type1 = null, child_type2 = null;
			child_type1 = child_arithmetic(childs.get(0));
			child_type2 = child_arithmetic(childs.get(1));

			if (child_type1 == child_type2) {
				ari_type = child_type1;
			} else {
				ari_type = NODE_TYPE.REAL;
			}
			break;
		default:
			ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
			break;
		}
		return ari_type;
	}
	

	/**
	 * 对数组的分析 
	 * 数组的元素可以为 字符串，(标识符)->表达式
	 * @param node
	 * @return
	 */
	private NODE_TYPE arr_val(Node node){
		NODE_TYPE arrType = null;
		//前一个元素的类型
		NODE_TYPE pre_type = null;
		List<Node> elements = node.getLinks();
		for(Node element: elements){
			
			//记录第一个element的type
			if(pre_type==null){
				switch (element.getType()) {
				case STRING_VAL:
					pre_type = NODE_TYPE.STRING;
					break;
				/*case IDENTIFIER:
					//判断标识符是否已声明
					if(!SymbolTable.symbolTable.containsKey(element.getValue())){
						ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
					}
					Variable identifier = SymbolTable.symbolTable.get(element.getValue());
					//判断标识符是否已赋值
					if(!identifier.isAssigned){
						ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
					}
					switch (identifier.type) {
					case INT:
					case REAL:
					case STRING:
						pre_type = identifier.type;
						break;

					default:
						ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARRAY);
						break;
					}
					
					break;*/
				case ARITHMETIC:
					pre_type = arithmetic(element);
					break;

				default:
					ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARRAY);
					break;
				}
			}else{
				// 查看后面的元素类型是否一致
				switch (element.getType()) {
				case STRING_VAL:
					if (pre_type == NODE_TYPE.STRING) {
						break;
					} else {
						ThrowMyException.throwMyExcepton(ErrorNum.DIFFENRENT_TYPES_IN_ARRAY);
					}
				case ARITHMETIC:
					if(pre_type==arithmetic(element)){
						break;
					}else{
						ThrowMyException.throwMyExcepton(ErrorNum.DIFFENRENT_TYPES_IN_ARRAY);
					}			

				default:
					ThrowMyException.throwMyExcepton(ErrorNum.DIFFENRENT_TYPES_IN_ARRAY);
					break;
				}
			}
			
		}
		
		switch (pre_type) {
		case INT:
			arrType = NODE_TYPE.INT_ARR;
			break;
		case REAL:
			arrType = NODE_TYPE.REAL_ARR;
			break;
		case STRING:
			arrType = NODE_TYPE.STRING_ARR;
			break;
		default:
			break;
		}
		
		return arrType;
	}
	
	/**
	 * 对数组元素的分析
	 * @param node 数组元素结点 子节点序列：数组名IDENTIFIER  下标（表达式）
	 * @return 该数组元素的数据类型
	 */
	private NODE_TYPE identi_arr_element(Node node){
		NODE_TYPE type = null;
		NODE_TYPE indexType = null;
		//数组下标结点，可以是标识符，可以是表达式
		Node indexnode = null;
		
		//判断数组是否已声明
		if(SymbolTable.symbolTable.get(node.getLinks().get(0).getValue())==null){
			ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
		}
		//判断数组是否已赋值
		if(SymbolTable.symbolTable.get(node.getLinks().get(0).getValue()).isAssigned==false){
			ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
		}
		//根据数组的类型获得变量类型
		switch (SymbolTable.symbolTable.get(node.getLinks().get(0).getValue()).type) {
		case INT_ARR:
			type = NODE_TYPE.INT;
			break;
		case REAL_ARR:
			type = NODE_TYPE.REAL;
			break;
		case STRING_ARR:
			type = NODE_TYPE.STRING;
			break;
		default:
			break;
		}
		
		//检查数组下标是否是整型
		indexnode = node.getLinks().get(1);
		switch (indexnode.getType()) {
		case INT_VAL:
			break;
		//如果是表达式
		case ARITHMETIC:
			indexType= arithmetic(indexnode);
			if(indexType!=NODE_TYPE.INT){
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_FOR_ARR_INDEX);
			}
			break;

		default:
			ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_FOR_ARR_INDEX);
			break;
		}
		
		return type;
	}
	


	/**
	 * 对标识符的分析
	 * 
	 * @param node
	 *            标识符结点
	 * @return 标识符数据类型
	 */
	private NODE_TYPE identifier(Node node) {
		Variable identifier = null;
		// 判断标识符是否声明
		if (!SymbolTable.symbolTable.containsKey(node.getValue())) {
			ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
		}
		identifier = SymbolTable.symbolTable.get(node.getValue());
		//标识符是否已赋值
		if(!identifier.isAssigned){
			ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
		}
		return identifier.type;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
