package execute;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import cmmui.FormatGrammarTree;
import constant.Instructions;
import grammar.GrammerAnalysis;
import system.MiddleCode;
import system.Node;
import system.Node.NODE_TYPE;

public class Test {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//output();
//		Execute exe = new Execute("test_my.txt");
//		exe.execute();
		InputStream is = null;
		try {
			is = new FileInputStream("test.cmm");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		GrammerAnalysis ga = new GrammerAnalysis(is);
		Node root = ga.oneProgram();
		System.out.println(new FormatGrammarTree().travel(root));

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
