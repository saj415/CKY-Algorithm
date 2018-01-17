/**
 * Parser based on the CYK algorithm.
 * Project 3
 * CSE 498, NLP, Fall 2017
 * @author Sachin Joshi
 */

import java.io.*;
import java.util.*;

public class Parser {

	public static Grammar g;
	private static ArrayList<String> terminals; // ArrayList to store the production rules whose LHS is a Pre-terminal
	private static ArrayList<String> nonterminals; // ArrayList to store the production rules whose LHS is a Non-terminal
	private Chart[][] table = null; // A double array of type Chart which holds all of a table cell's information
	private int sent_len = 0; // Stores the length of the sentence 
	
	/**
	 * Constructor: read the grammar.
	 */
	public Parser(String grammar_filename) {
		g = new Grammar(grammar_filename);
		nonterminals = new ArrayList<String>();
		terminals = new ArrayList<String>();
	}

	/**
	 * Parse one sentence given in the array.
	 * CKY Algorithm
	 * Generates the Parse Tree
	 */
	public void parse(ArrayList<String> sentence) {
		sent_len = sentence.size(); // stores the length of the sentence 
		
		/**
		* Intialize the table of the parser (CKY algorithm) that stores each of the cell's information with the following structure 
		**/
		table = new Chart[sent_len][];
		for(int i = 0; i < sent_len; i++){
			table[i] = new Chart[sent_len];
			for(int j = i; j < sent_len; j++){
				table[i][j] = new Chart();
			}
		}
		
		/**
		* Intializes the entries of the table with the words (terminals) of the sentence
		* Finds the corresponding production rules that generated the terminal(word)
		* Adds its LHS (pre-terminal), its probability and the word (terminal) itself to the table	
		**/
		for(int i = 0; i < sent_len; i++){
			String word = sentence.get(i); // for each word in the sentence
			ArrayList<Chart> prodRule = findTerminalProd(word); // find the production rules that generates that word(terminal)
			for(Chart prod: prodRule){
				table[i][i].addEntry(prod, null, null); // add the entry (LHS, terminal and probability) to the table 
			}
		}
		
		/**
		* Combines the cell's entries of the table to from a new production rule 
		* Adds the LHS of the new production rule formed along with the new calculated probability
		* Bottom-up dynamic programming	
		**/
		for(int i = 1; i < sent_len; i++){
			for(int j = 0; j < sent_len - i; j++){
				for(int k = j; k < j + i; k++){
					combineCells(j, k, j+i); // call to the function to generate a new production rule 
				}
			}
		}
	}
	
	/**
	* Function to generate a new production rule from the current non-terminals/pre-terminals
	**/
	public void combineCells(int i, int k, int j){
		Chart chart1 = table[i][k];
        List<Chart> entries1 = chart1.getEntries();

        // find a non-terminal/pre-terminal B in cell[i][k]
        for (Chart ch1 : entries1){

            Chart chart2 = table[k + 1][j];
            List<Chart> entries2 = chart2.getEntries(); // Add the entries to the list

            // find another non-terminal/pre-terminal C in cell[k+1][j]
            for (Chart ch2 : entries2){
                // find a new non-terminal A in the Nonterminal production rules
                Chart newChart = findNonterminalProd(ch1, ch2); // find the production rules whose RHS consists of B and C N and LHS is A  
                // if there exists a production rule of the type A->BC
                if (newChart != null) {
                    // match and add the entries to (cell[i][j], A, B, C)
                    table[i][j].addEntry(newChart, ch1, ch2); // add the entries (new non terminal (LHS) and the corresponding probabilty) to the table
                }
            }
        }
	}
	
	/**
	* Function to find the production rules that generate a terminal (word) of the sentence
	* Fetches the LHS, RHS and the probability of the found production rule and adds it to the table 
	**/
	public ArrayList<Chart> findTerminalProd(String word) {

        ArrayList<Chart> terminalprod = new ArrayList<>();

        for(int i = 0; i < terminals.size(); i++){
			String temp[] = terminals.get(i).split(" ");
			if(temp[1].equals(word)){ // check if a production rule generates the given terminal (word)
				Chart prod = new Chart();
				prod.setTerminalSymbol(temp[1]); // fetch the RHS (terminal word)
				prod.setNonTerminalSymbol(temp[0]); // fetch the LHS
				prod.setProbability(Double.parseDouble(temp[2])); // fetch its corresponding probability 
				terminalprod.add(prod); // add the information to an arrayList of type Chart
			}
		}
		return terminalprod;
    }
	
	/**
	* Function to find a new production rule and fetch the new LHS and the probabilty
	* Given the current RHS elements
	**/
	public Chart findNonterminalProd(Chart ch1, Chart ch2) {
        for(int i = 0; i < nonterminals.size(); i++){
			String temp[] = nonterminals.get(i).split(" ");
			if(temp[1].equals(ch1.getNonTerminalSymbol()) && temp[2].equals(ch2.getNonTerminalSymbol())){ // check if the current production rule's RHS elements matches the nonterminal symbols of current the table's cell
				Chart prod = new Chart();
				prod.setNonTerminalSymbol(temp[0]); // fetch the new non-terminal symbol (LHS) from the production rule 
				prod.setProbability(((ch1.getProbability()) * (ch2.getProbability()))); // fetch the corresponding probability 
				return prod;
			}
		}
		
		return null;
    }
	
	/**
	 * Print the parse obtained after calling parse()
	 */
	public String PrintOneParse(StringBuffer stringbuffer) {   
		Chart endCell = table[0][sent_len - 1];
		endCell.getSolution(stringbuffer);
		return stringbuffer.toString();
	}
	
	public static void main(String[] args) {
		// read the grammar in the file args[0]
		Parser parser = new Parser(args[0]);
		ArrayList<String> sentence = new ArrayList<String>();
		char end = '.';
		// read a parse tree from a bash pipe
		try {
			InputStreamReader isReader = new InputStreamReader(System.in);
			BufferedReader bufReader = new BufferedReader(isReader);
			while(true) {
				String line = null;
				if((line=bufReader.readLine()) != null) {
					String []words = line.split(" ");
					for (String word : words) {
						word = word.replaceAll("[^a-zA-Z]", "");
						if (word.length() == 0) {
							continue;
						}
						// use the grammar to filter out non-terminals and pre-terminals
						if (parser.g.symbolType(word) == 0 && (!word.equals(".") && !word.equals("!"))) {
							sentence.add(word);
						}
					}
					end = (words[words.length - 1]).charAt(0);
					
				}
				else {
					break;
				}
			}
			bufReader.close();
			isReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		* A string array to store the LHS (Non terminals and Pre terminals) of the production rules
		**/
		String LHS[] = {"S", "NP", "PP", "VP", "Noun", "Verb", "Det", "Adj", "Prep"};
		
		ArrayList<RHS> prod; // An arrayList of type RHS to store the production rules for a given LHS
		
		RHS rs; // Object of class RHS to access the elements of the above defined ArrayList
		
		/**
		* Group the production rules into two different categories (ArrayLists)
		* Production Rules whose LHS is a Non-terminal are stored in an ArrayList nonterminals along with their probabilities
		* Production Rules whose LHS is a Pre-terminal are stored in an ArrayList terminals along with their probabilities
		**/
		for(int i = 0; i < LHS.length; i++){
			if(g.symbolType(LHS[i]) == 2){ // for all the production rules whose LHS is a Non-terminal
				prod = g.findProductions(LHS[i]); // find the corresponding production rules 
				for(int j = 0; j < prod.size(); j++){
					rs = prod.get(j); // fetch each rule from the arraylist
					nonterminals.add(LHS[i] + " " + rs.first() + " " + rs.second() + " " + rs.getProb()); // add the LHS, RHS elements and the probability to the arraylist
				}
			}
			else{
				prod = g.findProductions(LHS[i]); // find all the production rules whose LHS ia a pre-terminal
				for(int j = 0; j < prod.size(); j++){
					rs = prod.get(j); // fetch each rule from the arraylist
					if(rs.second() != null){
						nonterminals.add(LHS[i] + " " + rs.first() + " " + rs.second() + " " + rs.getProb()); // add the LHS, RHS elements and the probability to the arraylist
					}
					else{
						terminals.add(LHS[i] + " " + rs.first() + " " + rs.getProb()); // add the LHS, RHS elements and the probability to the arraylist
					}					
				}
			}
		}
		
		parser.parse(sentence); // calls the function parse that creates a parse tree for the generated sentence using CKY algorithm   
		
		/**
		* Use of StringBuffer ensures easy and convenient appending and modification of the parse tree as and when the desired symbols are encountered 
		**/
		StringBuffer sb = new StringBuffer();
        
		String parseTree = parser.PrintOneParse(sb); // fetch the generated parse tree and store it in a string variable
		
		/**
		* The Pasre Tree generated for the senetence is written to the file parse.txt in the desired format 
		**/
		try{
			BufferedWriter br = new BufferedWriter(new FileWriter("results/parse.txt"));
			br.write("(ROOT");
			for(int i = 0; i < parseTree.length(); i++){
				if((parseTree.charAt(i)) == '('){
					br.write(" " + parseTree.charAt(i));
				}
				else{
					br.write(parseTree.charAt(i));
				}
			}
			br.write(" " + end + ")");
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		/**
		* Prints the complete Parse Tree for the sentence
		**/
		System.out.println("(ROOT" + parseTree + end + ")");
	}
}
