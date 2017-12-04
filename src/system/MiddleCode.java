package system;

import java.io.Serializable;

import constant.Instructions;

public class MiddleCode implements Serializable{
	private Instructions ins;
	private Object v1;
	private Object v2;
	private Object res;
	
	
	public MiddleCode(Instructions ins, Object v1, Object v2, Object res) {
		super();
		this.ins = ins;
		this.v1 = v1;
		this.v2 = v2;
		this.res = res;
	}
	public Instructions getIns() {
		return ins;
	}
	public void setIns(Instructions ins) {
		this.ins = ins;
	}
	public Object getV1() {
		return v1;
	}
	public void setV1(Object v1) {
		this.v1 = v1;
	}
	public Object getV2() {
		return v2;
	}
	public void setV2(Object v2) {
		this.v2 = v2;
	}
	public Object getRes() {
		return res;
	}
	public void setRes(Object res) {
		this.res = res;
	}
	
}
