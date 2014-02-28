package jaz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author cory
 *
 */
public class Env {
	private Map<String,Integer> environment  = new HashMap<String, Integer>();
	private int callee;

	private boolean inProcedure;
	/**
	 * Keep track of local variables by pushing them onto the stack.
	 * When we return to the caller we will pop the values off 
	 * and search the SymbolTable for them to pop them off as well.
	 * 
	 * storage for the names of local variables
	 */
	private Stack<String> procedureStack = new Stack<String>();
	
	private List<JazList<String, String>> tape;
	private boolean afterCall;
	
	Env() {
		tape = new ArrayList<JazList <String, String>>();
		inProcedure = false;
		afterCall = false;
	}
	
	/**
	 * @param lineNumber 
	 * 
	 */
	public void startProcedure(int lineNumber) {
		callee = lineNumber;
	}
	
	/**
	 * 
	 * @return
	 */
	public int endProcedure() {
		return callee;
	}
	
	public int getLabel(String lableName) {
		return environment.get(lableName);
	}
	
	/**
	 * put: method for adding labelName->lineNumber to the environment.
	 * @param labelName
	 * @param lineNumber
	 */
	public void putLabel(String labelName, int lineNumber) {
		environment.put(labelName, lineNumber);
	}
	
	public boolean isInProcedure() {
		return inProcedure;
	}

	public void setInProcedure(boolean b) {
		inProcedure = b;
	}

	public boolean isAfterCall() {
		// TODO Auto-generated method stub
		return afterCall;
	}
	
	public void setAfterCall(boolean b) {
		afterCall = b;
	}
	
} 
