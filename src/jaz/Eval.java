package jaz;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Eval {
	
	private Stack<Object> executionStack = new Stack<Object>();
	private Map<String, String> memory = new HashMap<String, String>();
	
	Eval () {
		
		String prompt = "=>"; // factor this out to a function.
		// Symbol table contains (head, rest) where head is the "operator"
		for (JazList<String, String> l : SymbolTable.symbolTable) {
			System.out.println("Handling token: "+l);
			if (l.head().equals("show")) { 
				String message = l.rest(); 
				System.out.println(prompt + message);
			} else if (l.head().equals("push")) {
				executionStack.push(l.rest().trim());
				System.out.println("The stack now contains: "+ executionStack.peek());
			}else if (l.head().equals("copy")) {
				executionStack.push(executionStack.peek());
				System.out.println("The stack now contains: "+ executionStack.peek());
			} else if (l.head().equals("print")) {
				System.out.println(prompt+executionStack.pop());
			} else if (l.head().equals("lvalue")) {
				// Add the (varname, value) tuple to memory
				memory.put(l.rest().trim(), "0");
				executionStack.push(l.rest().trim());
			} else if (l.head().equals("rvalue")) {
				Object value = memory.get(l.rest().trim());
				executionStack.push(value == null?0:value);
			} else if (l.head().equals(":=")) {
				String value = (String) executionStack.pop();
				String varname = (String) executionStack.pop();
				memory.put(varname, value);
			} else if (l.head().equals("+")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 + op2;
				executionStack.push(x);
			} else if (l.head().equals("-")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 - op2;
				executionStack.push(x);
			} else if (l.head().equals("*")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 * op2;
				executionStack.push(x);
			} else if (l.head().equals("/")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 / op2;
				executionStack.push(x);
			} else if (l.head().equals("div")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 % op2;
				executionStack.push(x);
			} else if (l.head().equals("&")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 & op2;
				executionStack.push(x);
			} else if (l.head().equals("|")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int op2 = Integer.parseInt((String)executionStack.pop());
				int x = op1 | op2;
				executionStack.push(x);
			} else if (l.head().equals("!")) {
				int op1 = Integer.parseInt((String)executionStack.pop());
				int x = op1 * -1;
				executionStack.push(x);
			} else if (l.head().equals("goto")) {
				
			}
		}
		
	}
	

}
