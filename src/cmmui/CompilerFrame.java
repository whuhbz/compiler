package cmmui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.lang.model.type.NullType;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

public class CompilerFrame extends JFrame {
	/* 窗体菜单栏 */
	private final static JMenuBar MENUBAR = new JMenuBar();
	/* 窗体工具条 */
	private final static JToolBar TOOLBAR = new JToolBar();
	/* 状态条 */
	private final static JToolBar STATUSBAR = new JToolBar();
	/* 文件菜单 */
	private static JMenu fileMenu;
	/* 编辑菜单 */
	private static JMenu editMenu;
	/* 设置菜单 */
	private static JMenu setMenu;
	/* 帮助菜单 */
	private static JMenu helpMenu;
	/* 菜单子项 */
	private JMenuItem newItem;
	private JMenuItem openItem;
	private JMenuItem saveItem;
	private JMenuItem exitItem;
	private JMenuItem undoItem;
	private JMenuItem searchItem;
	private JMenuItem fontItem;
	private JMenuItem runItem;
	private JMenuItem aboutItem;
	/* 工具条按钮 */
	private JButton newButton;
	private JButton saveButton;
	private JButton runButton;
	private JButton searchButton;
    /*编辑部分面板*/
	private TabbedPaneUI editTabbedPane;
	private JTextPane editArea;
	/*控制台面板*/
	private JScrollPane consolePane;
	private JTextArea consoleArea;
	/*主面板*/
	private JPanel editPanel;
	/*显示行数面板*/
	private TextLineNumber tln;
	/*滚动条*/
	private JScrollPane scrollPane;
	/* 语法树和中间代码面板 */
	public static JTabbedPane treeAndMcodePanel;
	/* 保存和打开对话框 */
	private FileDialog filedialog_save, filedialog_load;
	
	private static HashMap<JScrollPane, JTextPane> map = new HashMap<JScrollPane, JTextPane>();
	/* Undo管理器 */
	private final UndoManager undo = new UndoManager();
	private UndoableEditListener undoHandler = new UndoHandler();
	/* 保存要查找的字符串 */
	private static String findStr = null;
	/* 当前文本编辑区字符串 */
	private static String text = null;
	/* 当前选择的文本的位置 */
	private static int position;
	/* 查找次数 */
	private static int time = 0;
	/*保存控制台用户的输入*/
	private static String str1,str2,userInput;
	/*字体*/
	private Font editFont = new Font("微软雅黑", Font.PLAIN, 15);
	public void initMenuBar() {
		setLayout(null);
		setJMenuBar(MENUBAR);
		// 初始化菜单项
		fileMenu = new JMenu("文件(F)");
		editMenu = new JMenu("编辑(E)");
		setMenu = new JMenu("运行(R)");
		helpMenu = new JMenu("帮助(H)");

		// 将菜单添加到菜单栏
		MENUBAR.add(fileMenu);
		MENUBAR.add(editMenu);
		MENUBAR.add(setMenu);
		MENUBAR.add(helpMenu);
		MENUBAR.setBackground(new Color(255,255,255));
		// 为文件菜单添加子项
		newItem = new JMenuItem("新 建");
		openItem = new JMenuItem("打 开");
		saveItem = new JMenuItem("保 存");
		exitItem = new JMenuItem("退 出");
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		// 为编辑菜单添加子项
		undoItem = new JMenuItem("撤  销");
		searchItem = new JMenuItem("查  找");
		fontItem=new JMenuItem("字体");
		editMenu.add(undoItem);
		editMenu.add(searchItem);
		editMenu.add(fontItem);
		// 为设置菜单添加子项
		runItem = new JMenuItem("运行");
		setMenu.add(runItem);
		// 为帮助菜单添加子项
		aboutItem = new JMenuItem("关 于");
		helpMenu.add(aboutItem);
		// 工具条
		newButton = new JButton(new ImageIcon(getClass().getResource("/images/new.png")));
		newButton.setToolTipText("新建");
		saveButton = new JButton(new ImageIcon(getClass().getResource("/images/save.png")));
		saveButton.setToolTipText("保存");
		runButton = new JButton(new ImageIcon(getClass().getResource("/images/run.png")));
		runButton.setToolTipText("运行");
		searchButton = new JButton(new ImageIcon(getClass().getResource("/images/search.png")));
		searchButton.setToolTipText("查找");
		TOOLBAR.setFloatable(false);
		TOOLBAR.add(newButton);
		TOOLBAR.addSeparator();
		TOOLBAR.add(saveButton);
		TOOLBAR.addSeparator();
		TOOLBAR.add(runButton);
		TOOLBAR.addSeparator();
		TOOLBAR.add(searchButton);

		add(TOOLBAR);
		TOOLBAR.setBackground(new Color(155,155,155));
		TOOLBAR.setBounds(0, 0, 1000, 27);
		TOOLBAR.setPreferredSize(getPreferredSize());
		// 设置状态条
		add(STATUSBAR);
		STATUSBAR.setBackground(new Color(155,255,255));
		STATUSBAR.setBounds(0, 630,1000, 20);
		STATUSBAR.setPreferredSize(getPreferredSize());
	}
    public CompilerFrame(String title) {
    	super();
		setTitle(title);
		initMenuBar();
		// 文件保存和打开对话框
		filedialog_save = new FileDialog(this, "保存文件", FileDialog.SAVE);
		filedialog_save.setVisible(false);
		filedialog_load = new FileDialog(this, "打开文件", FileDialog.LOAD);
		filedialog_load.setVisible(false);
		filedialog_save.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				filedialog_save.setVisible(false);
			}
		});
		/*编辑区和控制台区*/
		editPanel = new JPanel(null);
		editPanel.setBackground(new Color(255,255,255));
		
	    editTabbedPane =new TabbedPaneUI();
	    editArea = new JTextPane();
		 scrollPane = new JScrollPane(editArea);	
		 tln = new TextLineNumber(editArea);
		scrollPane.setRowHeaderView(tln);
		editArea.getDocument().addUndoableEditListener(undoHandler);
		editArea.getDocument().addDocumentListener(new SyntaxHighlighter(editArea));
		editArea.setFont(editFont);
		map.put(scrollPane, editArea);
		editTabbedPane.add(scrollPane,"new1.cmm");
		
		 consoleArea=new JTextArea();
		 consoleArea.setLineWrap(true);
		 consoleArea.addMouseListener(new DefaultMouseAdapter());
		 consolePane=new JScrollPane(consoleArea);
		consolePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		consolePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consolePane.setBackground(new Color(255, 255, 255));
		JTabbedPane ConsolePanel=new JTabbedPane();
		ConsolePanel.add(consolePane, "Console");
		editTabbedPane.setBounds(0, 0, 695, 360);
		
		ConsolePanel.setBounds(0, 360, 695, 245);
		editPanel.add(editTabbedPane);
		editPanel.add(ConsolePanel);
		editPanel.setBackground(getBackground());
		add(editPanel);
		editPanel.setBounds(0, TOOLBAR.getHeight(),695,600);
		editPanel.setPreferredSize(new Dimension(200, 120));
		
		/*语法树和中间代码区*/
		JTextArea treearea=new JTextArea();
		treearea.setEditable(false);
		JTextArea Mcodearea=new JTextArea();
		Mcodearea.setEditable(false);
		JScrollPane treePanel = new JScrollPane(treearea);
		JScrollPane McodePanel = new JScrollPane(Mcodearea);		
		treeAndMcodePanel = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		treeAndMcodePanel.add(treePanel, "语法树");
		treeAndMcodePanel.add(McodePanel, "中间代码");
		treeAndMcodePanel.setBounds(editPanel.getWidth(), TOOLBAR.getHeight(), 250, editPanel.getHeight());
		add(treeAndMcodePanel);
		// 为菜单项添加事件监听器
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				create(null);
			}
		});
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
		});
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		undoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undo();
			}
		});
		searchItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});
		fontItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setFont();
			}
		});
		runItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				//run();
			}
		});
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"java","CMM解释器器", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// 为工具条按钮添加事件监听器
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				create(null);
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				beforeRun();
			}
		});
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});
		consoleArea.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					if(str2!=null) {
						str1=str2;
					}
					try {
						str2=consoleArea.getText();
						if(str1.length()==0) {
							userInput=str2;
						}else {
							userInput=str2.substring(str1.length());
						}						
						System.out.println(userInput);
					}catch(Exception ex) {
						
					}					
				}
				if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_Z) {
					undo();
				}
			}
		});
		
    }
    //内部类：监听鼠标左键
    class DefaultMouseAdapter extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				str1=consoleArea.getText();
			}
		}
	} 
    // 内部类：Undo管理
 	class UndoHandler implements UndoableEditListener {
 		public void undoableEditHappened(UndoableEditEvent e) {
 			undo.addEdit(e.getEdit());
 		}
 	}
    // 新建
 	private void create(String filename) {
 		if (filename == null) {
 			filename = JOptionPane.showInputDialog(this,"请输入新建文件名");
 	 		
 			if (filename == null) {
 				return;
 			}		
 			filename += ".cmm";
 			if(filename.equals("")) {
 				JOptionPane.showMessageDialog(this, "文件名不能为空!");
 				return;
 			}
 		}

 		JTextPane editArea = new JTextPane();
 		editArea.setFont(editFont);
 		JScrollPane scrollPane = new JScrollPane(editArea);
 		TextLineNumber tln = new TextLineNumber(editArea);
 		scrollPane.setRowHeaderView(tln);
 		editArea.getDocument().addUndoableEditListener(undoHandler);
 		editArea.getDocument().addDocumentListener(new SyntaxHighlighter(editArea));
 		map.put(scrollPane, editArea);
 		editTabbedPane.add(scrollPane, filename);
 		editTabbedPane.setSelectedIndex(editTabbedPane.getTabCount() - 1);
 	}
	// 打开
	private void open() {
		boolean isOpened = false;
		String str = "", fileName = "";
		File file = null;
		StringBuilder text = new StringBuilder();
		filedialog_load.setVisible(true);
		if (filedialog_load.getFile() != null) {
			try {
				file = new File(filedialog_load.getDirectory(), filedialog_load
						.getFile());
				fileName = file.getName();
				if (fileName.endsWith(".cmm")|| fileName.endsWith(".CMM")|| fileName.endsWith(".txt")
						|| fileName.endsWith(".TXT")|| fileName.endsWith(".java")) 
				{
					FileReader file_reader = new FileReader(file);
					BufferedReader in = new BufferedReader(file_reader);
					while ((str = in.readLine()) != null)
						text.append(str + '\n');
					in.close();
					file_reader.close();
				}
				
			} catch (IOException e2) {
			}
			for (int i = 0; i < editTabbedPane.getComponentCount(); i++) {
				if (editTabbedPane.getTitleAt(i).equals(fileName)) {
					isOpened = true;
					editTabbedPane.setSelectedIndex(i);
				}
			}
			if (!isOpened) {
				create(fileName);
				editTabbedPane.setTitleAt(
						editTabbedPane.getComponentCount() - 1, fileName);
				map.get(editTabbedPane.getSelectedComponent()).setText(
						text.toString());
			}

		}
	}
	// 保存
	private void save() {
		JTextPane temp = map.get(editTabbedPane.getSelectedComponent());
		if (temp.getText() != null) {
			filedialog_save.setFile(editTabbedPane.getTitleAt(editTabbedPane.getSelectedIndex()));
			filedialog_save.setVisible(true);
			
		
			if (filedialog_save.getFile() != null) {
				try {
					File file = new File(filedialog_save.getDirectory(),
							filedialog_save.getFile());
					FileWriter fw = new FileWriter(file);
					fw.write(map.get(editTabbedPane.getSelectedComponent())
							.getText());
					fw.close();
				} catch (IOException e2) {
				}
			}
		}
	}

	// 撤销
	private void undo() {
		if (undo.canUndo()) {
			try {
				undo.undo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	// 查找
	private void search() {
		if(editTabbedPane.getSelectedIndex()==-1) {
			JOptionPane.showMessageDialog(this, "请新建文件");
		}else {
			JTextPane temp = map.get(editTabbedPane.getSelectedComponent());
			if (text == null)
				text = temp.getText();
			if (findStr == null)
				findStr = JOptionPane.showInputDialog(this, "请输入要找的字符串!");
			if (findStr != null) {
				position = text.indexOf(findStr);
				if (text.equals("")) {
					JOptionPane.showMessageDialog(this, "没有你要查找的字符串！");
					findStr = null;
				} else {
					if (position != -1) {
						temp.select(position + findStr.length() * time, position
								+ findStr.length() * (time + 1));
						temp.setSelectedTextColor(Color.RED);
						text = new String(text.substring(position
								+ findStr.length()));
						time += 1;
					} else {
						JOptionPane.showMessageDialog(this, "没有你要查找的字符串！");
						time = 0;
						text = null;
						findStr = null;
					}
				}
			}
		}
			
		}
	// 设置字体
	private void setFont() {
			Font font = JFontDialog
					.showDialog(getContentPane(), "字体设置", true, getFont());
			for (int i = 0; i < editTabbedPane.getComponentCount(); i++)
				map.get(editTabbedPane.getComponent(i)).setFont(font);
	}
	//运行预处理
	private void beforeRun() {
		BufferedWriter bw = null;
        try {
            OutputStream os = new FileOutputStream("src/cmmui/Test.txt");
            bw = new BufferedWriter(new OutputStreamWriter(os));
            for (String value : editArea.getText().split("\n")) {
                bw.write(value);
                //bw.newLine();//换行
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
	}
	//运行函数
	private void run() {
		
	}
    public static void main(String[] args) {
    	//改变界面外观
    	try {  
    		UIManager.setLookAndFeel(  
    		        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  
        } catch (Exception e) {}  
    	CompilerFrame frame = new CompilerFrame("CMM解释器"); 
		frame.setBounds(160,0, 950, 700);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}