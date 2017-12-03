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
	 * 表达式 （表达式构成元素为常量，整形、实数变量（不为单独存在）或整形、实数数组元素  ）或字符串  或数组  或标识符（可能是单项表达式或字符串）或read语句
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
			if(arithmeticType!=variableType){
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
			if(arrType!=variableType){
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		//如果是标识符
		case IDENTIFIER:
			//判断标识符对应的变量是否已经声明过
			if(!SymbolTable.symbolTable.containsKey(childs.get(3).getValue())){
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			//判断该变量是否已经赋值
			Variable identifier = SymbolTable.symbolTable.get(childs.get(3).getValue());
			if(!identifier.isAssigned){
				ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
			}
			//判断该变量数据类型是否匹配
			if(variableType!=identifier.type){
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
		/*	switch (variableType) {
			case INT:
				variable = new Variable(variableName, variableType, identifier.getIntValue());
				break;
			case REAL:
				variable = new Variable(variableName, variableType, identifier.getRealValue());
				break;
			case STRING:
				variable = new Variable(variableName, variableType, identifier.getStringValue());
			case INT_ARR:
				variable = new Variable(variableName, variableType, identifier.getIntArrValue());
				break;
			case REAL_ARR:
				variable = new Variable(variableName, variableType, identifier.getDoubleArrValue());
				break;
			case STRING_ARR:
				variable = new Variable(variableName, variableType, identifier.getStringArrValue());
				break;
			default:
				variable = new Variable(null, null, null);
				break;
			}*/
			break;
			
		//如果是输入语句
		case INPUT:
			String inputType = childs.get(3).getValue();
			switch (inputType) {
			case "A int from input.":
				if(variableType!=NODE_TYPE.INT){
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
	 * 子节点序列：标识符或数组元素   等号   表达式 （表达式构成元素为常量，整形、实数变量（不为单独存在）或整形、实数数组元素  ）或字符串  或数组  或标识符（可能是单项表达式或字符串）或read语句
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
			if (arithmeticType != variableType) {
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
			if (arrType != variableType) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}
			break;
		// 如果是标识符
		case IDENTIFIER:
			// 判断标识符对应的变量是否已经声明过
			if (!SymbolTable.symbolTable.containsKey(childs.get(2).getValue())) {
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			// 判断该变量是否已经赋值
			Variable identifier = SymbolTable.symbolTable.get(childs.get(2).getValue());
			if (!identifier.isAssigned) {
				ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
			}
			// 判断该变量数据类型是否匹配
			if (variableType != identifier.type) {
				ThrowMyException.throwMyExcepton(ErrorNum.MISSMATCHED_DATA_TYPE);
			}

			break;

		// 如果是输入语句
		case INPUT:
			String inputType = childs.get(2).getValue();
			switch (inputType) {
			case "A int from input.":
				if (variableType != NODE_TYPE.INT) {
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
	 * 对于LOGIC 子节点序列：ARITHMETIC/IDENTIFIER/STRING   LOGIC_OPERATOR  ARITHMETIC/IDENTIFIER/STRING
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
		switch (left.getType()) {
		//如果是字符串
		case STRING_VAL:
			left_type = NODE_TYPE.STRING;
			break;
		//如果是标识符
		case IDENTIFIER:
			//判断标识符是否声明
			if(!SymbolTable.symbolTable.containsKey(left.getValue())){
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			//判断标识符是否赋值
			if(!SymbolTable.symbolTable.get(left.getValue()).isAssigned){
				ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
			}
			//获得标识符类型
			left_type = SymbolTable.symbolTable.get(left.getValue()).type;
			break;
		//如果是表达式
		case ARITHMETIC:
			left_type = arithmetic(left);
			break;

		default:
			System.out.println("逻辑运算的参与者还可以是以下类型结点："+ left.getType().toString());
			break;
		}
		
		//判断逻辑运算符右侧部分
		switch (right.getType()) {
		//如果是字符串
		case STRING_VAL:
			right_type = NODE_TYPE.STRING;
			break;
		//如果是标识符
		case IDENTIFIER:
			//判断标识符是否声明
			if(!SymbolTable.symbolTable.containsKey(right.getValue())){
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			//判断标识符是否赋值
			if(!SymbolTable.symbolTable.get(right.getValue()).isAssigned){
				ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
			}
			//获得标识符类型
			right_type = SymbolTable.symbolTable.get(right.getValue()).type;
			break;
		//如果是表达式
		case ARITHMETIC:
			right_type = arithmetic(right);
			break;

		default:
			System.out.println("逻辑运算的参与者还可以是以下类型结点："+ left.getType().toString());
			break;
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
	 * 表达式元素可以为 整型、实数型常数，可以为标识符，可以为数组元素，但不能包含任一字符串
	 * @param node 表达式结点
	 * @return 表达式的数据类型
	 */
	private NODE_TYPE arithmetic(Node node){
		//算术表达式的类型,默认为int,有real 则隐式转换为REAL
		NODE_TYPE ari_type = NODE_TYPE.INT;
		//算数表达式的全部元素
		List<Node> list = node.getLinks();
		for(Node element: list){
			//过滤算数运算符
			if(element.getType()==NODE_TYPE.ARI_OPERATOR){
				continue;
			}else{
				switch (element.getType()) {
				case INT_VAL:
					break;
				case REAL_VAL:
					ari_type = NODE_TYPE.REAL;
					break;
				case IDENTIFIER:
					//判断标识符是否已声明
					if(!SymbolTable.symbolTable.containsKey(element.getValue())){
						ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
					}
					//判断标识符是否已赋值
					if(!SymbolTable.symbolTable.get(element.getValue()).isAssigned){
						ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
					}
					//判断标识符类型是否合适
					switch (SymbolTable.symbolTable.get(element.getValue()).type) {
					case INT:						
						break;
					case REAL:
						ari_type = NODE_TYPE.REAL;
						break;
					default:
						ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
						break;
					}
					break;
				case IDENTI_ARR_ELEMENT:
					NODE_TYPE type = identi_arr_element(element);
					if(type==NODE_TYPE.INT){
						
					}else if(type==NODE_TYPE.REAL){
						ari_type = NODE_TYPE.REAL;
					}else{
						ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
					}
					break;				
				default:
					ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_IN_ARITHMETIC);
					break;
				}
				
				
			}
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
			}
			
			//查看后面的元素类型是否一致
			switch (element.getType()) {
			case STRING_VAL:
				if(pre_type==NODE_TYPE.STRING){
					break;
				}else{
					ThrowMyException.throwMyExcepton(ErrorNum.DIFFENRENT_TYPES_IN_ARRAY);
				}
			case ARITHMETIC:
				pre_type = arithmetic(element);
				break;

			default:
				break;
			}
			
			
			
			
		}
		
		return arrType;
	}
	
	/**
	 * 对数组元素的分析
	 * @param node 数组元素结点 子节点序列：数组名IDENTIFIER  下标（标识符或表达式）
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
		switch (node.getLinks().get(0).getType()) {
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
		//如果是标识符
		case IDENTIFIER:
			//检查标识符对应变量是否声明
			if(!SymbolTable.symbolTable.containsKey(indexnode.getValue())){
				ThrowMyException.throwMyExcepton(ErrorNum.UNDECLARED_IDENTIFIER);
			}
			//检查标识符对应变量是否已赋值
			if(SymbolTable.symbolTable.get(indexnode.getValue()).isAssigned==false){
				ThrowMyException.throwMyExcepton(ErrorNum.UNASSIGNED_IDENTIFIER);
			}
			indexType = SymbolTable.symbolTable.get(indexnode.getValue()).type;
			if(indexType!=NODE_TYPE.INT){
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_FOR_ARR_INDEX);
			}
			break;
		//如果是表达式
		case ARITHMETIC:
			indexType= arithmetic(indexnode);
			if(indexType!=NODE_TYPE.INT){
				ThrowMyException.throwMyExcepton(ErrorNum.ILLEGAL_TYPE_FOR_ARR_INDEX);
			}
			break;

		default:
			break;
		}
		
		
		return type;
	}
	

}
