package semantic;

import java.util.List;

import system.Node.NODE_TYPE;

public class Variable {
	public String name;
	public NODE_TYPE type;
	public Object value;
	public boolean isAssigned;
	
	public Variable(String name,NODE_TYPE type,Boolean isAssigned){
		this.name = name;
		this.type = type;
		this.isAssigned = isAssigned;
	}
	
	public Object getValue(){
		switch (this.type) {
		case INT:
			return (int) this.value;
		case REAL:
			return (double) this.value;
		case STRING:
			return (String) this.value;
		case INT_ARR:
			return (List<Integer>) this.value;
		case REAL_ARR:
			return (List<Double>) this.value;
		case STRING_ARR:
			return (List<String>) this.value;
		default:
			return null;
		}
	}
	
	public int getIntValue(){
		return (int) this.value;
	}
	
	public double getRealValue(){
		return (double) this.value;
	}
	
	public String getStringValue(){
		return (String) this.value;
	}
	
	public List<Integer> getIntArrValue(){
		return (List<Integer>) this.value;
	}
	
	public List<Double> getDoubleArrValue(){
		return (List<Double>) this.value;
	}
	
	public List<String> getStringArrValue(){
		return (List<String>)this.value;
	}
	
}
