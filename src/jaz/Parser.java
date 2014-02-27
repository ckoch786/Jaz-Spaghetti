package jaz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	
	static Env environment = new Env();
	
	Parser (String file) {
		this.parse(file);
		//environment ;
	}

	private void parse(String file) { 
		// Reader TODO refactor into separate class
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String t;
			int lineNumber = 0;
			while ((t = br.readLine()) != null) {
				// Parser
				System.out.println("adding: " + t);
				t = t.trim();// remove white space from indentation
				String head = t.split(" ")[0].trim();
				String rest = t.substring(t.split(" ")[0].length()).trim();
				if (!head.isEmpty()) {
					if (head.equals("label")) {
						environment.putLabel(rest, lineNumber);
						SymbolTable.symbolTable.add(new JazList<String,String>(head, rest));
					} else {
						SymbolTable.symbolTable.add(new JazList<String,String>(head, rest));
					}
					lineNumber++;
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	
	


}
