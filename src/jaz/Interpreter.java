package jaz;

public class Interpreter {

	public static void main(String[] args) {
		//TODO check for args
		// repl
		Parser parser = new Parser(args[0]);
		Eval eval = new Eval();

	}
}



