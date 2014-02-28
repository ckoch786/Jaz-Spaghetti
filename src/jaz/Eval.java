package jaz;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Eval {
	
	// TODO refactor these to the Env class
	private Stack<Object> executionStack = new Stack<Object>();
	private Map<String, Stack<String>> memory = new HashMap<String, Stack<String>>();
	private Map<String, Stack<String>> procMemory = new HashMap<String, Stack<String>>();;
	
	// TODO refactor this crap to use iterator and move the looping to Iterpreter
	Eval () {
		
		String prompt; // factor this out to a function.
		//prompt = "=>";
		prompt = ""; 
		//Symbol table contains (head, rest) where head is the "operator"
		int lineNumber = 0;
		JazList<String, String> l = SymbolTable.symbolTable.get(lineNumber);
		while (l.head() != "halt") {
			l = SymbolTable.symbolTable.get(lineNumber);
			//System.out.println("Handling token: "+l.head() + ", " + l.rest());
			if (l.head().equals("show")) { 
				show(prompt, l);
			} else if (l.head().equals("push")) {
				executionStack.push(l.rest().trim());
				//System.out.println("The stack now contains: "+ executionStack.peek());
			}else if (l.head().equals("copy")) {
				executionStack.push(executionStack.peek());
				//System.out.println("The stack now contains: "+ executionStack.peek());
			} else if (l.head().equals("print")) {
				System.out.println(prompt+executionStack.pop());
			} else if (l.head().equals("lvalue")) {
				lvalue(l);
			} else if (l.head().equals("rvalue")) {
				rvalue(l);
			} else if (l.head().equals(":=")) {
				assign(l);
			} else if (l.head().equals("+")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 + op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("-")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 - op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("*")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 * op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("/")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 / op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("div")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 % op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("&")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 & op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("|")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 | op2;
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("!")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = 1;
				if (op1 == 0) {
					x = 1;
				}
				executionStack.push(String.valueOf(x));
			} else if (l.head().equals("goto")) {
				int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
				lineNumber = lineNumberOfLabel;
			} else if (l.head().equals("begin")){
				Parser.environment.setInProcedure(true);
			} else if (l.head().equals("end")){
				end();
			} else if (l.head().equals("return")) {
				lineNumber = returnProc();
			} else if (l.head().equals("call")) {
				lineNumber = call(lineNumber, l);
			} else if (l.head().equals("halt")) {
				System.exit(0);
			} else if (l.head().equals("gotrue")){
				lineNumber = gotrue(lineNumber, l);
			} else if (l.head().equals("gofalse")) {
				lineNumber = gofalse(lineNumber, l);
			} else if (l.head().equals("<>")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 != op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} else if (l.head().equals("<=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 <= op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} else if (l.head().equals(">=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 >= op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} else if (l.head().equals("<")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 < op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} else if (l.head().equals(">")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 > op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} else if (l.head().equals("=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 == op2) {
					handleTrue(op2, op1);
				} else {
					handleFalse(op2, op1);
				}
			} 
			lineNumber++;
		}
		
	}

	private void assign(JazList<String, String> l) {
		String value = (String) executionStack.pop();
		String varname = (String) executionStack.pop();
		
		if (Parser.environment.isRecursive()) {
			procMemoryPut(value, varname, l);
		} else if (Parser.environment.isInProcedure() &&
				!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
			procMemoryPut(value, varname, l);	
		} else if (Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()) { // We are in the procedure before the return statement
			procMemoryPut(value, varname, l);
		} else if (!Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
			globalMemoryPut(l, value, varname);
		} else { // In global scope or callee scope 
			globalMemoryPut(l, value, varname);
		}
	}

	//TODO find a better way to implement assign, lvalue, and rvalue; this smells like poo.
	private void rvalue(JazList<String, String> l) {
		/*For rvalue we want to use the global scope if we are in parameter passing mode or anywhere else
		 * outside a function.
		 * if we are in returning mode then we want to take rvalue from the procedures scope
		 * and if we are in the procedure we use procedures scoping*/
		// If we are in a procedure then use the procedure memory stack.
		/* in call block of code 
		 * e.g. if call foo then we are in between label foo and return*/
//				if (Parser.environment.isInProcedure() 
//						&& !Parser.environment.isAfterCall()) { // in param passing or function 
//					
//				} 
		if (Parser.environment.isRecursive()) {
			procMemoryGet(l);
		} else if (!Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
			procMemoryGet(l);
		} else if (Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()){ // We are in the procedure before the return statement
			procMemoryGet(l);
		} else if (Parser.environment.isInProcedure() && 
				!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
			globalMemoryGet(l);
		} else { // In global scope or callee scope 
			globalMemoryGet(l);
		}
	}

	private void lvalue(JazList<String, String> l) {
		// If we are in a procedure then use the procedure memory stack.
		if (Parser.environment.isRecursive()) {
			procMemoryLValue(l);
		} else if (Parser.environment.isInProcedure() && 
				!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
			procMemoryLValue(l);
		} else if (Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()) { // We are in the procedure before the return statement
			procMemoryLValue(l);
		} else if (!Parser.environment.isInProcedure() &&
				Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
			// Add the (varname, value) tuple to memory
			globalMemoryLValue(l);
		} else { // We are in global scope.
			globalMemoryLValue(l);
		}
	}

	private void show(String prompt, JazList<String, String> l) {
		String message = l.rest(); 
		System.out.println(prompt + message);
	}

	private void handleFalse(int op2, int op1) {
		executionStack.push(String.valueOf(op2));
		executionStack.push(String.valueOf(op1));
		executionStack.push(String.valueOf(0));
	}

	private void handleTrue(int op2, int op1) {
		executionStack.push(String.valueOf(op2));
		executionStack.push(String.valueOf(op1));
		executionStack.push(String.valueOf(1));
	}

	private int gofalse(int lineNumber, JazList<String, String> l) {
		int op1 = Integer.parseInt((String)executionStack.pop());
		if (op1 == 0) {
			int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
			lineNumber = lineNumberOfLabel;
		}
		return lineNumber;
	}

	private int gotrue(int lineNumber, JazList<String, String> l) {
		int op1 = Integer.parseInt((String)executionStack.pop());
		if (op1 != 0) {
			int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
			lineNumber = lineNumberOfLabel;
		}
		return lineNumber;
	}

	private void end() {
		//TODO
		if (!Parser.environment.stillCalling()) {
			Parser.environment.setInProcedure(false);
			Parser.environment.setAfterCall(false);
		}
	}

	private int returnProc() {
		int lineNumber;
		lineNumber = Parser.environment.endProcedure(); //return to caller
		Parser.environment.resetRecursive();
		Parser.environment.resetCallDepth();
		Parser.environment.setInProcedure(false);
		Parser.environment.setReturning(true);
		return lineNumber;
	}

	private int call(int lineNumber, JazList<String, String> l) {
		Parser.environment.setAfterCall(true);
		Parser.environment.setInProcedure(true);
		Parser.environment.startProcedure(lineNumber); // Save line number of callee
		Parser.environment.incRecursive();
		Parser.environment.incCallDepth();
		Parser.environment.setReturning(false);
		int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
		lineNumber = lineNumberOfLabel;
		return lineNumber;
	}

	private void globalMemoryLValue(JazList<String, String> l) {
		if (memory.get(l.rest().trim()) == null) {
			memory.put(l.rest(), new Stack<String>());
		}
		//memory.get(l.rest().trim()).push("0");
		executionStack.push(l.rest().trim());
	}

	private void procMemoryLValue(JazList<String, String> l) {
		if (procMemory.get(l.rest().trim()) == null) {
			procMemory.put(l.rest(), new Stack<String>());
		}
		executionStack.push(l.rest().trim());
	}

	private void globalMemoryGet(JazList<String, String> l) {
		if (memory.get(l.rest().trim()) == null) {
			memory.put(l.rest(), new Stack<String>());
			memory.get(l.rest().trim()).push("0");
		}
		String value = memory.get(l.rest().trim()).peek();
		executionStack.push(value == null?"0":value);
	}

	private void procMemoryGet(JazList<String, String> l) {
		String value;
		if (procMemory.get(l.rest().trim()) == null) {
			procMemory.put(l.rest(), new Stack<String>());
			procMemory.get(l.rest().trim()).push("0");
		}
		if (Parser.environment.isRecursive()&& Parser.environment.isReturning()) {
			value = procMemory.get(l.rest().trim()).pop();
		} else {
			value = procMemory.get(l.rest().trim()).peek();
		}
		executionStack.push(value == null?"0":value);
	}

	private void globalMemoryPut(JazList<String, String> l, String value,
			String varname) {
		if (memory.get(l.rest().trim()) == null) {
			memory.put(l.rest(), new Stack<String>());
		}
		memory.get(varname).push(value);
	}

	private void procMemoryPut(String value, String varname, JazList<String, String> l) {
		if (procMemory.get(l.rest().trim()) == null) {
			procMemory.put(l.rest(), new Stack<String>());
		}
		procMemory.get(varname).push(value);
	}
	

}
