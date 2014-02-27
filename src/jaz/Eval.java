package jaz;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Eval {
	
	private Stack<Object> executionStack = new Stack<Object>();
	private Map<String, Stack<String>> memory = new HashMap<String, Stack<String>>();
	
	Eval () {
		
		//String prompt = "=>"; // factor this out to a function.
		String prompt = ""; 
		// Symbol table contains (head, rest) where head is the "operator"
		int lineNumber = 0;
		//for (JazList<String, String> l : SymbolTable.symbolTable) {
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
				// Add the (varname, value) tuple to memory
				if (memory.get(l.rest().trim()) == null) {
					memory.put(l.rest(), new Stack<String>());
				}
				//memory.get(l.rest().trim()).push("0");
				executionStack.push(l.rest().trim());
			} else if (l.head().equals("rvalue")) {
				if (memory.get(l.rest().trim()) == null) {
					memory.put(l.rest(), new Stack<String>());
					memory.get(l.rest().trim()).push("0");
				}
				String value = memory.get(l.rest().trim()).peek();
				executionStack.push(value == null?"0":value);
			} else if (l.head().equals(":=")) {
				String value = (String) executionStack.pop();
				String varname = (String) executionStack.pop();
				if (memory.get(l.rest().trim()) == null) {
					memory.put(l.rest(), new Stack<String>());
				}
				memory.get(varname).push(value);
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
			} else if (l.head().equals("end")){
				//TODO
				
			} else if (l.head().equals("return")) {
				lineNumber = Parser.environment.endProcedure(); //return to caller
			} else if (l.head().equals("call")) {
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
