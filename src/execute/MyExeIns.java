package execute;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import cmmui.CompilerFrame;
import semantic.Variable;
import system.MiddleCode;
import system.Node.NODE_TYPE;

public class MyExeIns implements Execute.ExeIns {

	@Override
	public void selectIns(Execute exe, MiddleCode mc) {
		// TODO Auto-generated method stub
		Object v1 = mc.getV1();
		Object v2 = mc.getV2();
		Object res = mc.getRes();
		switch (mc.getIns()) {
		case JMP:
			forJMP(exe, v1, v2, res);
			break;
		case ADD:
			forADD(exe, v1, v2, res);
			break;
		case MOV:
			forMOV(exe, v1, v2, res);
			break;
		case MIN:
			forMIN(exe, v1, v2, res);
			break;
		case MUL:
			forMUL(exe, v1, v2, res);
			break;
		case DIV:
			forDIV(exe, v1, v2, res);
			break;
		case GT:
			forGT(exe, v1, v2, res);
			break;
		case LT:
			forLT(exe, v1, v2, res);
			break;
		case GET:
			forGET(exe, v1, v2, res);
			break;
		case LET:
			forLET(exe, v1, v2, res);
			break;
		case EQ:
			forEQ(exe, v1, v2, res);
			break;
		case NEQ:
			forNEQ(exe, v1, v2, res);
			break;
		case WRI:
			forWRI(exe, v1, v2, res);
			break;
		case REA:
			forREA(exe, v1, v2, res);
			break;
		case DEC:
			forDEC(exe, v1, v2, res);
			break;
		case END:
			forEND(exe, v1, v2, res);
			break;
		case EOA:
			forEOA(exe, v1, v2, res);
			break;
		case CON:
			forCON(exe, v1, v2, res);
			break;
		}
	}

	@Override
	public void forJMP(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		if (v1V.type != NODE_TYPE.BOOLEAN) {
			throw new RuntimeException("ss");
		}
		if ((Boolean) v1V.value) {
			exe.counterPlusOne();
		} else {
			Integer falseTo = (Integer) res;
			exe.setCounter(falseTo);
		}
	}

	@Override
	public void forMOV(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);

		if (v2V.type == NODE_TYPE.REAL_ARR || v2V.type == NODE_TYPE.INT_ARR
				|| v2V.type == NODE_TYPE.STRING_ARR) {

			if (v1V.type == NODE_TYPE.REAL_ARR || v1V.type == NODE_TYPE.INT_ARR
					|| v1V.type == NODE_TYPE.STRING_ARR) {
				v2V.value = v1V.value;
			} else {

				if (res != null) {
					Variable resV = exe.getVarible((String) res);
					Integer index = (Integer) resV.value;

					if (index >= ((List<Object>) (v2V.value)).size()) { // 数组越界
						throw new RuntimeException();
					}

					((List<Object>) (v2V.value)).set(index, v1V.value);
				} else {
					if (v2V.isAssigned) {
						((List<Object>) (v2V.value)).clear();
						v2V.isAssigned = false;
					}
					((List<Object>) (v2V.value)).add(v1V.value);
				}

			}

		} else {
			v2V.value = v1V.value;
		}

		exe.counterPlusOne();
	}

	@Override
	public void forADD(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			resV.value = (Integer) (v1V.value) + (Integer) (v2V.value);
		} else {
			resV.value = (Double) (v1V.value) + (Double) (v2V.value);
		}
		exe.counterPlusOne();
	}

	@Override
	public void forMIN(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			resV.value = (Integer) (v1V.value) - (Integer) (v2V.value);
		} else {
			resV.value = (Double) (v1V.value) - (Double) (v2V.value);
		}
		exe.counterPlusOne();
	}

	@Override
	public void forMUL(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			resV.value = (Integer) (v1V.value) * (Integer) (v2V.value);
		} else {
			resV.value = (Double) (v1V.value) * (Double) (v2V.value);
		}
		exe.counterPlusOne();
	}

	@Override
	public void forDIV(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if ((Double) (v2V.value) == 0.0) {
			throw new RuntimeException();
		}

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			resV.value = (Integer) (v1V.value) / (Integer) (v2V.value);
		} else {
			resV.value = (Double) (v1V.value) / (Double) (v2V.value);
		}
		exe.counterPlusOne();
	}

	@Override
	public void forGT(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) > (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if ((Integer) (v1V.value) > (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if ((Double) (v1V.value) > (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) > (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forLT(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) < (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if ((Integer) (v1V.value) < (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if ((Double) (v1V.value) < (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) < (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forGET(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) >= (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if ((Integer) (v1V.value) >= (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if ((Double) (v1V.value) >= (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) >= (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forLET(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) <= (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if ((Integer) (v1V.value) <= (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if ((Double) (v1V.value) <= (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) <= (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forEQ(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) == (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if (((Integer) (v1V.value)).intValue() == (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if (((Double) (v1V.value)).doubleValue() == (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) == (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forNEQ(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		Variable v2V = exe.getVarible((String) v2);
		Variable resV = exe.getVarible((String) res);

		if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.INT) {
			if ((Integer) (v1V.value) != (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.INT && v2V.type == NODE_TYPE.REAL) {
			if (((Integer) (v1V.value)).intValue() < (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else if (v1V.type == NODE_TYPE.REAL && v2V.type == NODE_TYPE.INT) {
			if (((Double) (v1V.value)).doubleValue() != (Integer) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		} else {
			if ((Double) (v1V.value) != (Double) (v2V.value)) {
				resV.value = true;
			} else {
				resV.value = false;
			}
		}
		exe.counterPlusOne();
	}

	@Override
	public void forWRI(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setForeground(attr, Color.black);
		try {
			CompilerFrame.frame.d.insertString(
					CompilerFrame.frame.d.getLength(), v1V.value.toString(),
					attr);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		exe.counterPlusOne();
	}

	@Override
	public void forREA(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		NODE_TYPE v1V = (NODE_TYPE) v1;
		Variable v2V = exe.getVarible((String) v2);
		String inStr = null;
		CompilerFrame.isReadyInput = false;
		CompilerFrame.ee = this;

		if (!CompilerFrame.isReadyInput) {
			synchronized (this) {
				try {
					wait();
					inStr = CompilerFrame.userInput;
					CompilerFrame.ee = null;
					CompilerFrame.isReadyInput = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			inStr = CompilerFrame.userInput;
			CompilerFrame.isReadyInput = false;
		}

		System.out.println(inStr);

		NODE_TYPE type = v1V;
		switch (type) {
		case INT:
			int inputInt = Integer.parseInt(inStr.trim());
			exe.getVarible("$IN_INT").value = inputInt;
			break;
		case REAL:
			double inputReal = Double.parseDouble(inStr.trim());
			exe.getVarible("$IN_REAL").value = inputReal;
			break;
		case STRING:
			String inputString = inStr.trim();
			exe.getVarible("$IN_STRING").value = inputString;
			break;
		default:
			break;
		}
		exe.counterPlusOne();
	}

	@Override
	public void forDEC(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		NODE_TYPE type = (NODE_TYPE) v1;
		String name = (String) v2;

		switch (type) {
		case INT:
			exe.addVarible(name, type, new Integer(0), false);
			break;
		case REAL:
			exe.addVarible(name, type, new Double(0), false);
			break;
		case STRING:
			exe.addVarible(name, type, new String(""), false);
			break;
		case INT_ARR:
			exe.addVarible(name, type, new ArrayList<Integer>(), false);
			break;
		case REAL_ARR:
			exe.addVarible(name, type, new ArrayList<Double>(), false);
			break;
		case STRING_ARR:
			exe.addVarible(name, type, new ArrayList<String>(), false);
			break;
		case BOOLEAN:
			exe.addVarible(name, type, new Boolean(false), false);
			break;
		}
		exe.counterPlusOne();
	}

	@Override
	public void forEND(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);
		v1V.isAssigned = true;
	}

	@Override
	public void forEOA(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1); // 数组名
		Variable v2V = exe.getVarible((String) v2); // 下标
		Variable resV = exe.getVarible((String) res); // 变量值要存入的位置
		if (!v1V.isAssigned) { // 数组未初始化便使用
			throw new RuntimeException();
		}
		List<Object> l = (List<Object>) (v1V.value);
		Integer index = (Integer) (v2V.value);
		if (index >= l.size()) { // 数组越界
			throw new RuntimeException();
		}

		resV.value = l.get(index);
		exe.counterPlusOne();
	}

	@Override
	public void forCON(Execute exe, Object v1, Object v2, Object res) {
		// TODO Auto-generated method stub
		Variable v1V = exe.getVarible((String) v1);

		v1V.value = v2;
		exe.counterPlusOne();
	}

}
