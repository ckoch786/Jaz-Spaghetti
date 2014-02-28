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
	private Stack<Integer> callee = new Stack<Integer>();

	private boolean inProcedure;
	/**
	 * Keep track of local variables by pushing them onto the stack.
	 * When we return to the caller we will pop the values off 
	 * and search the SymbolTable for them to pop them off as well.
	 * 
	 * storage for the names of local variables
	 */
	// TODO would this have been a better way?
	//private Stack<String> procedureStack = new Stack<String>();
	
	private boolean afterCall;
	private int recursive;
	private boolean returning;
	
	Env() {
		inProcedure = false;
		afterCall = false;
		recursive = 0;
		returning = false;
	}
	
	/**
	 * @param lineNumber 
	 * 
	 */
	public void startProcedure(int lineNumber) {
		callee.push(lineNumber);
	}
	
	/**
	 * 
	 * @return
	 */
	public int endProcedure() {
		return callee.pop();
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

	public void resetRecursive() {
		// TODO Auto-generated method stub
		recursive--;
		
	}

	public void incRecursive() {
		// TODO Auto-generated method stub
		recursive++;
	}
	
	public int getRecursive() {
		return recursive;
	}
	
	public boolean isRecursive() {
		return recursive > 0;
	}

	public boolean stillCalling() {
		// TODO Auto-generated method stub
		return this.isRecursive();
	}

	public void resetCallDepth() {
		// TODO Auto-generated method stub
		this.resetRecursive();
		
	}

	public void incCallDepth() {
		// TODO Auto-generated method stub
		this.incRecursive();
	}

	public void setReturning(boolean b) {
		// TODO Auto-generated method stub
		returning = b;
		
	}

	public boolean isReturning() {
		// TODO Auto-generated method stub
		return returning;
	}
} 
