package jaz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	
	Parser (String file) {
		this.parse(file);
		
	}

	private void parse(String file) { 
		// Reader
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String t;

			while ((t = br.readLine()) != null) {
				// Parser
				System.out.println("adding: " + t);
				String head = t.split(" ")[0];
				String rest = t.substring(t.split(" ")[0].length());
				SymbolTable.symbolTable.add(new JazList<String,String>(head, rest));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	
	


}
