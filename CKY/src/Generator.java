/**
 * Generate sentences from a CFG
 * Project 3
 * CSE 498 NLP, Fall 2017
 * @author Sachin Joshi
 *
 */

import java.io.*;
import java.util.*;

public class Generator {
	
	private Grammar grammar;

	/**
	 * Constructor: read the grammar.
	 */
	public Generator(String grammar_filename) {
		grammar = new Grammar(grammar_filename);
	}

	/**
	 * Generate a number of sentences.
	 */
	public ArrayList<String> generate(int numSentences) throws IOException {
		
		// An array of strings to store the LHS of the production rules (includes terminals and preterminals)
		String LHS[] = {"ROOT", "S", "NP", "VP", "PP", "Noun", "Verb", "Det", "Prep", "Adj"};
		
		// ArrayList of type string to store the productions rules corresponding to each of the LHS
		ArrayList<String> Prod_R = new ArrayList<String>(); // Store the production rules whose LHS is ROOT
		ArrayList<String> Prod = new ArrayList<String>(); // Store the production rules whose LHS is S or VP or PP
		ArrayList<String> Prod_NP = new ArrayList<String>(); // Store the production rules whose LHS is NP
		ArrayList<String> Prod_N = new ArrayList<String>(); // Store the production rules whose LHS is Noun
		ArrayList<String> Prod_V = new ArrayList<String>(); // Store the production rules whose LHS is Verb
		ArrayList<String> Prod_D = new ArrayList<String>(); // Store the production rules whose LHS is Determinant
		ArrayList<String> Prod_A = new ArrayList<String>(); // Store the production rules whose LHS is Adjective
		ArrayList<String> Prod_P = new ArrayList<String>(); // Store the production rules whose LHS is Preposition
		
		/**
		* Find the productions corresponding to each of the LHS
		* Store the productions in an ArrayList of type RHS 
		* Run a switch case 
		* Define an object of RHS class to fetch the first and second symbol of the production rule
		* Add the LHS, first symbol and second symbol to the corresponding ArrayList defined above	
		**/
		for(int i = 0; i < LHS.length; i++){
			
			ArrayList<RHS> rhs = grammar.findProductions(LHS[i]); // finds the productions for the given LHS and stores it an ArrayList of type RHS
			
			/**
			* switch case to store the production rules in an ArrayList corresponding to the given LHS
			**/
			
			switch(LHS[i]){
				case "ROOT": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS 
								Prod_R.add(LHS[i] + " " + rs.first() + " " + rs.second()); // Fetches the first and second symbol of the production rule and adds it to the corresponding ArrayList		
							 }
							 break;
				
				case "NP": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod_NP.add(LHS[i] + " " + rs.first() + " " + rs.second()); // Fetches the first and second symbol of the production rule and adds it to the corresponding ArrayList				
							}
							break;
								
				case "Noun": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								if(rs.second() == null){
									Prod_N.add(LHS[i] + " " + rs.first()); // Adding the elements to the ArrayList
								}
								else{
									Prod_N.add(LHS[i] + " " + rs.first() + " " + rs.second()); // Adding the elements to the ArrayList
								}	
							 }
							 break;
				
				case "Verb": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod_V.add(LHS[i] + " " + rs.first()); // Fetches the first symbol of the production rule, since its a terminal and adds it to the corresponding ArrayList				
							 }
							 break;

				case "Det": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod_D.add(LHS[i] + " " + rs.first()); // Fetches the first symbol of the production rule and adds it to the corresponding ArrayList		
							}
							break;
							
				case "Prep": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod_P.add(LHS[i] + " " + rs.first()); // Adding the elements to the ArrayList		
							}
							 break;
								
				case "Adj": for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod_A.add(LHS[i] + " " + rs.first()); // Adding the elements to the ArrayList		
							}
							 break;
							 
				default: for(int j = 0; j < rhs.size(); j++){
								RHS rs = rhs.get(j); // Object of RHS class to fetch the elements of the ArrayList of type RHS
								Prod.add(LHS[i] + " " + rs.first() + " " + rs.second()); // Fetches the first and second symbol of the production rule and adds it to the corresponding ArrayList		
						 }
			}
		}
		
		/**
		* Generate a sentence
		* Start from the ROOT and store the production in a linkedlist
		* Expand the ROOT and store the new Non-terminals at the starting of the linkedlist (index 0)
		* Keep expanding the non terminal at index 0 and add the new symbols at the current position in the linkedlist until a terminal symbol is found
		* Move to the next index and repeat the same process until there are no non terminals left in the linkedlist to be expanded
		* Generate a Parse Tree for the sentence generated
		**/
		
		/**
		* Variable Declaration
		**/
		int flag = 0; // flag variable to ensure that the production NP->NP PP has some probability of being selected
		String temp; // String Variable used to store an element of the ArrayList 
		String temp1[]; // A string array to store the words (terminals/nonterminals) of the above string
		LinkedList<String> sent = new LinkedList<String>(); // Linked list to store the generated sentence  
		LinkedList<String> parseTree = new LinkedList<String>(); // Linked list to store the pasre tree for the sentence generated
		
		temp = Prod_R.get((new Random().nextInt(Prod_R.size()))); // Selects any of the 2 production rules randomly whose LHS is ROOT	
		temp1 = temp.split(" "); // Stores the LHS and the RHS of the production rule into an array
		sent.add(temp1[1]); sent.add(temp1[2]); // Adds the non terminal S and the terminal (! or .) to the linked list 
		parseTree.add("("); parseTree.add("ROOT"); parseTree.add("S"); parseTree.add(temp1[2]); parseTree.add(")"); // Generates an initial parse tree in the desired format  
		
		for(int i = 0; i < sent.size(); i++){
			while(grammar.symbolType(sent.get(i)) != 0){ // Keep expanding the non termianls until a terminal is found 
				if(((sent.get(i)).compareTo("S")) == 0){
					temp1 = (Prod.get(0)).split(" ");
					int index = parseTree.lastIndexOf("S");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, temp1[2]); parseTree.add(index+3, ")"); parseTree.add(index, "("); // Building the parse tree 
					sent.remove(i); sent.add(i, temp1[2]); sent.add(i, temp1[1]); // Adding elements (nonterminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("NP")) == 0){
					int pos = (new Random().nextInt(Prod_NP.size())); // Random selection of production rules
					if(pos == 1 && flag == 0){
						temp = Prod_NP.get(1);
						flag = 1; // set flag to 1 once the production NP->NP PP has been selected, will be picked up again only when flag = 0 (after PP has been expanded) 
					}
					else{
						temp = Prod_NP.get(0);
					}
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("NP");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, temp1[2]); parseTree.add(index+3, ")"); parseTree.add(index, "("); // Building the parse tree 
					sent.remove(i); sent.add(i, temp1[2]); sent.add(i, temp1[1]); // Adding elements (preterminals/nonterminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("VP")) == 0){
					temp1 = (Prod.get(1)).split(" ");
					int index = parseTree.lastIndexOf("VP");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, temp1[2]); parseTree.add(index+3, ")"); parseTree.add(index, "("); // Building the parse tree
					sent.remove(i); sent.add(i, temp1[2]); sent.add(i, temp1[1]); // Adding elements (preterminals/nonterminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("PP")) == 0){
					temp1 = (Prod.get(2)).split(" ");
					int index = parseTree.lastIndexOf("PP");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, temp1[2]); parseTree.add(index+3, ")"); parseTree.add(index, "("); // Building the parse tree
					sent.remove(i); sent.add(i, temp1[2]); sent.add(i, temp1[1]); // Adding elements (preterminals/nonterminals) to the linked list to generate the sentence
					flag = 0; // set flag = 0, once PP has been expanded, so that NP->NP PP can be again picked
				}
				
				if(((sent.get(i)).compareTo("Noun")) == 0){
					temp = Prod_N.get((new Random().nextInt(Prod_V.size()))); // Random selection of production rules
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("Noun");
					if(temp1.length == 3){						
						parseTree.add(index+1, temp1[1]); parseTree.add(index+2, temp1[2]); parseTree.add(index+3, ")"); parseTree.add(index, "("); 
						sent.remove(i); sent.add(i, temp1[2]); sent.add(i, temp1[1]);
					}
					else{
						parseTree.add(index+1, temp1[1]); parseTree.add(index+2, ")"); parseTree.add(index, "(");  // Build the parse tree
						sent.remove(i); sent.add(i, temp1[1]); // Add elements (terminals) to the linked list to generate the sentence
					}					
				}
				
				if(((sent.get(i)).compareTo("Verb")) == 0){
					temp = Prod_V.get((new Random().nextInt(Prod_V.size()))); // Random selection of production rules
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("Verb");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, ")"); parseTree.add(index, "("); // Build the parse tree
					sent.remove(i); sent.add(i, temp1[1]); // Adding elements (terminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("Det")) == 0){
					temp = Prod_D.get((new Random().nextInt(Prod_D.size()))); // Random selection of production rules
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("Det");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, ")"); parseTree.add(index, "("); // Build the parse tree
					sent.remove(i); sent.add(i, temp1[1]); // Adding elements (terminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("Prep")) == 0){
					temp = Prod_P.get((new Random().nextInt(Prod_P.size()))); // Random selection of production rules
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("Prep");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, ")"); parseTree.add(index, "("); // Build the parse tree
					sent.remove(i); sent.add(i, temp1[1]); // Adding elements (terminals) to the linked list to generate the sentence
				}
				
				if(((sent.get(i)).compareTo("Adj")) == 0){
					temp = Prod_A.get((new Random().nextInt(Prod_A.size()))); // Random selection of production rules
					temp1 = temp.split(" ");
					int index = parseTree.lastIndexOf("Adj");
					parseTree.add(index+1, temp1[1]); parseTree.add(index+2, ")"); parseTree.add(index, "("); // Build the parse tree
					sent.remove(i); sent.add(i, temp1[1]); // Adding elements (terminals) to the linked list to generate the sentence
				}
			}
		}	
		
		/**
		* Stores the elements of the linked list into an arraylist since return type is an arraylist
		**/
		ArrayList<String> sentence = new ArrayList<String>();
		for(int i = 0; i < sent.size(); i++){
			sentence.add(sent.get(i));
		}
		
		/**
		* The parse tree used to generate the sentence is written in the text file sentence.txt in the desired format
		**/
		BufferedWriter br = new BufferedWriter(new FileWriter("results/sentence.txt")); // Creates a new text file sentence.txt
		br.write(parseTree.get(0));
		for(int i = 1; i < parseTree.size(); i++){
			if((parseTree.get(i).compareTo("(")) == 0){
				br.write(" " + parseTree.get(i));
			}
			else if((grammar.symbolType(parseTree.get(i))) == 0 && (parseTree.get(i).compareTo(")")) != 0){
				br.write(" " + parseTree.get(i));
			}
			else{
				br.write(parseTree.get(i));
			}
		}
		// System.out.println(parseTree);
		br.close();
		
		return sentence;
	}

	
	public static void main(String[] args) throws IOException {
		// the first argument is the path to the grammar file.
		Generator g = new Generator(args[0]);
		ArrayList<String> res = g.generate(1);
		for (String s : res) {
			System.out.println(s);
		}
	}
}
