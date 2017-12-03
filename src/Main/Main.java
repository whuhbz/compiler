package Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import grammar.GrammerAnalysis;
import grammar.SimpleTravel2;
import grammar.TravelGrammarTree;
import system.Node;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		// 如果指定了输出文件路径
		PrintStream newPs = null;
		if (args.length > 0) {
			newPs = new PrintStream(args[0]);
		}
		PrintStream oldPs = System.out;

		Scanner s = new Scanner(System.in);
		while (true) {
			System.out.println(
					"Please input the source file path, input q to exit.");
			String str = s.next();
			if (str.equals("q")) {
				System.out.println("Bye~");
				System.exit(0);
			}
			InputStream is = null;
			try {
				is = new FileInputStream(str);
			} catch (FileNotFoundException e) {
				System.out.println("The source file was not found.");
				continue;
			}

			GrammerAnalysis ga = new GrammerAnalysis(is);
			Node root = ga.oneProgram();
			TravelGrammarTree tgt = new SimpleTravel2();

			if (newPs != null) {
				System.setOut(newPs);
			}

			tgt.travel(root);

			System.setOut(oldPs);

			System.out.println("Done.");
		}

	}
}
