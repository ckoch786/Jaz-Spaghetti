package jaz;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Eval {
	
	private Stack<Object> executionStack = new Stack<Object>();
	private Map<String, Stack<String>> memory = new HashMap<String, Stack<String>>();
	private Map<String, String> procMemory = new HashMap<String, String>();;
	
	Eval () {
		
		String prompt; // factor this out to a function.
		//prompt = "=>";
		prompt = ""; 
		//Symbol table contains (head, rest) where head is the "operator"
		int lineNumber = 0;
		JazList<String, String> l = SymbolTable.symbolTable.get(lineNumber);
		while (l.head() != "halt") {
			l = SymbolTable.symbolTable.get(lineNumber);
			System.out.println("Handling token: "+l.head() + ", " + l.rest());
			if (l.head().equals("show")) { 
				String message = l.rest(); 
				System.out.println(prompt + message);
			} else if (l.head().equals("push")) {
				executionStack.push(l.rest().trim());
				//System.out.println("The stack now contains: "+ executionStack.peek());
			}else if (l.head().equals("copy")) {
				executionStack.push(executionStack.peek());
				//System.out.println("The stack now contains: "+ executionStack.peek());
			} else if (l.head().equals("print")) {
				System.out.println(prompt+executionStack.pop());
			} else if (l.head().equals("lvalue")) {
				// If we are in a procedure then use the procedure memory stack.
				if (Parser.environment.isInProcedure() && 
						!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
					if (procMemory.get(l.rest().trim()) == null) {
						procMemory.put(l.rest(), "0");
					}
					executionStack.push(l.rest().trim());
				} else if (Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()) { // We are in the procedure before the return statement
					if (procMemory.get(l.rest().trim()) == null) {
						procMemory.put(l.rest(), "0");
					}
					executionStack.push(l.rest().trim());
				} else if (!Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
					// Add the (varname, value) tuple to memory
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
					}
					//memory.get(l.rest().trim()).push("0");
					executionStack.push(l.rest().trim());
				} else { // We are in global scope.
					// Add the (varname, value) tuple to memory
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
					}
					//memory.get(l.rest().trim()).push("0");
					executionStack.push(l.rest().trim());
				}
			} else if (l.head().equals("rvalue")) {
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
				if (!Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
					String value = procMemory.get(l.rest().trim());
					executionStack.push(value == null?"0":value);
				} else if (Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()){ // We are in the procedure before the return statement
					String value = procMemory.get(l.rest().trim());
					executionStack.push(value == null?"0":value);
				} else if (Parser.environment.isInProcedure() && 
						!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
						memory.get(l.rest().trim()).push("0");
					}
					String value = memory.get(l.rest().trim()).peek();
					executionStack.push(value == null?"0":value);
				} else { // In global scope or callee scope 
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
						memory.get(l.rest().trim()).push("0");
					}
					String value = memory.get(l.rest().trim()).peek();
					executionStack.push(value == null?"0":value);
				}
			} else if (l.head().equals(":=")) {
				String value = (String) executionStack.pop();
				String varname = (String) executionStack.pop();
				
				if (Parser.environment.isInProcedure() &&
						!Parser.environment.isAfterCall()) { // We are in procedure(in top of begin block) and before the call.
					procMemory.put(varname, value);	
				} else if (Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()) { // We are in the procedure before the return statement
					procMemory.put(varname, value);
				} else if (!Parser.environment.isInProcedure() &&
						Parser.environment.isAfterCall()) { // We are under the call at the bottom of the begin block, returning values to global scope
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
					}
					memory.get(varname).push(value);
				} else { // In global scope or callee scope 
					if (memory.get(l.rest().trim()) == null) {
						memory.put(l.rest(), new Stack<String>());
					}
					memory.get(varname).push(value);
				}
			} else if (l.head().equals("+")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
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
				//TODO
				Parser.environment.setAfterCall(false);
				Parser.environment.setInProcedure(true);
				//procMemory = new HashMap<String, String>(); // Create a new procedure memory
			} else if (l.head().equals("end")){
				//TODO
				Parser.environment.setInProcedure(false);
				Parser.environment.setAfterCall(false);
				
			} else if (l.head().equals("return")) {
				lineNumber = Parser.environment.endProcedure(); //return to caller
				Parser.environment.setInProcedure(false);
			} else if (l.head().equals("call")) {
				Parser.environment.setAfterCall(true);
				Parser.environment.startProcedure(lineNumber); // Save line number of callee
				int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
				lineNumber = lineNumberOfLabel;
			 
			} else if (l.head().equals("halt")) {
				System.exit(0);
			} else if (l.head().equals("gotrue")){
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 != 0) {
					int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
					lineNumber = lineNumberOfLabel;
				}
			} else if (l.head().equals("gofalse")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 == 0) {
					int lineNumberOfLabel = Parser.environment.getLabel(l.rest());
					lineNumber = lineNumberOfLabel;
				}
			} else if (l.head().equals("<>")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 != op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} else if (l.head().equals("<=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 <= op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} else if (l.head().equals(">=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 >= op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} else if (l.head().equals("<")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 < op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} else if (l.head().equals(">")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 > op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} else if (l.head().equals("=")) {
				int op2 = Integer.parseInt((String)executionStack.pop());
				int op1 = Integer.parseInt((String)executionStack.pop());
				if (op1 == op2) {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(1));
				} else {
					executionStack.push(String.valueOf(op2));
					executionStack.push(String.valueOf(op1));
					executionStack.push(String.valueOf(0));
				}
			} 
			lineNumber++;
		}
		
	}
	

}
