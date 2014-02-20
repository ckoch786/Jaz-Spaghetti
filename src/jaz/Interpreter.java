package jaz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Interpreter {

	public static void main(String[] args) {
		//String s = "show Hello World! \n push 13 \n print \n";
		// This can be replaced with a file.
		String s = "show Hello World!\n"
				+ "push 13\n"
				+ "print\n"
				+ "lvalue bar\n" //Create variables bar and baz, with values 5 and 3, then add them, and print the result.
				+ "push 5\n"
				+ ":=\n"
				+ "lvalue baz\n"
				+ "push 3\n"
				+ ":=\n"
				+ "rvalue bar\n"
				+ "rvalue baz\n"
				+ "+\n"
				+ "print";
		Stack<Object> executionStack = new Stack<Object>();
		List<JazList<String, String>> symbolTable = new ArrayList<JazList<String, String>>();
		//List<JazList <String, Integer>> memory =  new ArrayList<JazList<String, Integer>>();
		Map<String, Integer> memory = new HashMap<String, Integer>();
		
		// Reader
		for (String t : s.split("\n")) {
			// Parser
			System.out.println("adding: " + t);
			String head = t.split(" ")[0];
			String rest = t.substring(t.split(" ")[0].length());
			symbolTable.add(new JazList<String,String>(head, rest));
		}
		
		// Eval
		String prompt = "=>"; // factor this out to a function.
		// Symbol table contains (head, rest) where head is the "operator"
		for (JazList<String, String> l : symbolTable) {
			System.out.println("Handling token: "+l);
			if (l.head().equals("show")) { 
				String message = l.rest(); 
				System.out.println(prompt + message);
			} else if (l.head().equals("push")) {
				executionStack.push(l.rest().trim());
				System.out.println("The stack now contains: "+ executionStack.peek());
			} else if (l.head().equals("print")) {
				System.out.println(prompt+executionStack.pop());
			} else if (l.head().equals("lvalue")) {
				// Add the (varname, value) tuple to memory
				memory.put(l.rest().trim(), 0);
				executionStack.push(l.rest().trim());
			} else if (l.head().equals("rvalue")) {
				Object value = memory.get(l.rest().trim());
				executionStack.push(value == null?0:value);
			} else if (l.head().equals(":=")) {
				int value = Integer.parseInt((String) executionStack.pop());
				String varname = (String) executionStack.pop();
				memory.put(varname, value);
			} else if (l.head().equals("+")) {
				int op1 = (int)(executionStack.pop());
				int op2 = (int)(executionStack.pop());
				int sum = op1 + op2;
				executionStack.push(sum);
			}
		}

		
			
	}

}
