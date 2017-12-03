package execute;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import semantic.SymbolTable;
import semantic.Variable;
import system.MiddleCode;
import system.Node.NODE_TYPE;

public class Execute {

	public static void main(String[] args)
			throws IOException, ClassNotFoundException {

	}

	private ObjectInputStream ois = null;
	private int counter = 0; // 程序计数器
	private List<MiddleCode> codeList = new ArrayList<MiddleCode>();
	private ExeIns exeIns = new MyExeIns();
	private SymbolTable symbolTable = new SymbolTable();

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public Variable getVarible(String name) {
		return symbolTable.symbolTable.get(name);
	}

	public void addVarible(String name, NODE_TYPE type, Object value,
			boolean isAssigned) {
		Variable v = new Variable(name, type, isAssigned);
		v.value = value;
		symbolTable.symbolTable.put(name, v);
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	// 重置程序计数器
	private void resetCounter() {
		counter = 0;
	}

	// 计数器加一
	public void counterPlusOne() {
		counter++;
	}

	public Execute(String fileName) {
		try {
			loadMiddleCodes(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 加载中间代码文件
	private void loadMiddleCodes(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream("test.txt");
		ois = new ObjectInputStream(fis);
	}

	// 从输入流中获取下一条中间代码
	private MiddleCode getNextCode()
			throws ClassNotFoundException, IOException {
		if (ois == null) {
			return null;
		}
		try {
			MiddleCode mc = (MiddleCode) ois.readObject();
			return mc;
		} catch (EOFException e) {
			return null;
		}
	}

	// 加载所有的中间代码
	private void loadAllCodes() throws ClassNotFoundException, IOException {
		MiddleCode mc = getNextCode();
		while (mc != null) {
			codeList.add(mc);
			mc = getNextCode();
		}
	}

	public void execute() throws ClassNotFoundException, IOException {
		loadAllCodes();
		resetCounter();

		while (counter < codeList.size()) {
			exeIns.selectIns(this, codeList.get(counter));
		}
	}

	/**
	 * 解释执行每一条指令的接口
	 * 
	 * @author 10330
	 *
	 */
	public interface ExeIns {
		void selectIns(Execute exe, MiddleCode mc);

		void forJMP(Execute exe, Object v1, Object v2, Object res);

		void forMOV(Execute exe, Object v1, Object v2, Object res);

		void forADD(Execute exe, Object v1, Object v2, Object res);

		void forMIN(Execute exe, Object v1, Object v2, Object res);

		void forMUL(Execute exe, Object v1, Object v2, Object res);

		void forDIV(Execute exe, Object v1, Object v2, Object res);

		void forGT(Execute exe, Object v1, Object v2, Object res);

		void forLT(Execute exe, Object v1, Object v2, Object res);

		void forGET(Execute exe, Object v1, Object v2, Object res);

		void forLET(Execute exe, Object v1, Object v2, Object res);

		void forEQ(Execute exe, Object v1, Object v2, Object res);

		void forNEQ(Execute exe, Object v1, Object v2, Object res);

		void forWRI(Execute exe, Object v1, Object v2, Object res);

		void forREA(Execute exe, Object v1, Object v2, Object res);

		void forDEC(Execute exe, Object v1, Object v2, Object res);

		void forEND(Execute exe, Object v1, Object v2, Object res);

		void forEOA(Execute exe, Object v1, Object v2, Object res);
	}
}
