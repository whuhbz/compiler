package Main;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import grammar.GrammerAnalysis;
import grammar.SimpleTravel;
import grammar.TravelGrammarTree;
import system.Node;
import system.ReturnClass;
import system.Word;
import word.WordAnalysis;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		InputStream is = new FileInputStream("TestFile.txt");
		GrammerAnalysis ga = new GrammerAnalysis(is);
		Node root = ga.oneProgram();
		TravelGrammarTree tgt = new SimpleTravel();
		tgt.travel(root);
	}
}
