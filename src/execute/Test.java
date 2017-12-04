package execute;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import constant.Instructions;
import system.MiddleCode;
import system.Node.NODE_TYPE;

public class Test {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		output();
		Execute exe = new Execute("test2.txt");
		exe.execute();
	}
	
	public static void output() throws IOException {
		FileOutputStream fos = new FileOutputStream("test2.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(new MiddleCode(Instructions.DEC, NODE_TYPE.INT, "a", null));
		oos.writeObject(new MiddleCode(Instructions.CON, "a", 30, null));
		oos.writeObject(new MiddleCode(Instructions.DEC,  NODE_TYPE.INT, "b", null));
		oos.writeObject(new MiddleCode(Instructions.CON, "b", 20, null));
		oos.writeObject(new MiddleCode(Instructions.DEC, NODE_TYPE.BOOLEAN, "c", null));	
		oos.writeObject(new MiddleCode(Instructions.LT, "a", "b", "c"));
		oos.writeObject(new MiddleCode(Instructions.JMP, "c", null, 8));
		oos.writeObject(new MiddleCode(Instructions.WRI, "a", null, null));
		oos.writeObject(new MiddleCode(Instructions.WRI, "b", null, null));
	}
}
