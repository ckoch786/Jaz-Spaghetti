package jaz;

public class Interpreter {
	public static void main(String[] args) {
		if (!(args.length > 0)) {
			System.out.println("Must provide a jaz file");
			System.exit(1);
		}
		// repl
		Parser parser = new Parser(args[0]);
		Eval eval = new Eval();
	}
}



   