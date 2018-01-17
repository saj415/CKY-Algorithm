 /**
  * Project 3 
  * CSE 498, NLP, Fall 2017
  * @author Sachin Joshi
  **/


import java.util.*;

public class Chart{

    private List<Chart> entries = null; // ArrayList of type Chart to store the entries of each cell in the table

    private String nonterm = null; // Stores the nonterminal symbol
    private String term = null; // Stores the terminal symbol
    private double probab = 0.0; // Stores the probabilty for the corresponding production rule

    // back pointers
    private Chart lhs = null;
    private Chart rhs = null;

    public Chart(){
        entries = new ArrayList<>();
    }

    /**
     * each Cell of the table may contains several rule 
     * Add the entries to the cell
     * Create a new cell
     * Add the LHS and RHS of the production rule
     */
    public void addEntry(Chart chart, Chart lhs, Chart rhs){
        chart.lhs = lhs; // adds the LHS 
        chart.rhs = rhs; // adds the RHS
        entries.add(chart); // add the entries to the chart
    }

    public List<Chart> getEntries(){
        return entries;
    }

    public String toString(){
        StringBuffer ret = new StringBuffer();
        for (Chart entry : entries) {
            ret.append(entry.nonterm + ",");
        }
        return ret.toString();
    }


    /**
     * Sort solution by probability and return it as StringBuffer
	 * Since multiple parse trees may be generated, select the most probable paree tree
     * Selects the entry at index entries.size() - 1, since its probabilty is the highest
	 * Calls the function to output the generated parse tree in the desired format 	
     */
    public void getSolution(StringBuffer sb){
        //sort by their probability
        Collections.sort(entries, new Comparator<Chart>() {// sorting the entries according to their probabilities
            public int compare(Chart o1, Chart o2) {
                if (o1.probab < o2.probab)
                    return 0;
                else return -1;
            }
        });

        Chart entry = entries.get(entries.size() - 1); // Get the most probable parse tree (one with highest probability)
        entry.getTrace(sb); // Output the parse tree in the desired format
            
    }

    /**
     * Outputs the parse tree in the desired format
     */
    public void getTrace(StringBuffer sb){
        sb.append("(" + nonterm);
        if (lhs != null) {
            lhs.getTrace(sb);
            rhs.getTrace(sb);
        } else {
            sb.append(" " + term);
        }
        sb.append(")");
    }
	
	/**
	* Getter and Setter methods  
	**/
    public void setEntries(List<Chart> entries){
        this.entries = entries;
    }

    public String getNonTerminalSymbol(){
        return nonterm;
    }

    public void setNonTerminalSymbol(String nonterm){
        this.nonterm = nonterm;
    }

    public String getTerminalSymbol(){
        return term;
    }

    public void setTerminalSymbol(String term){
        this.term = term;
    }

    public double getProbability(){
        return probab;
    }

    public void setProbability(Double probab){
        this.probab = probab;
    }

    public Chart getLeft(){
        return lhs;
    }

    public void setLeft(Chart lhs){
        this.lhs = lhs;
    }

    public Chart getRight(){
        return rhs;
    }

    public void setRight(Chart rhs){
        this.rhs = rhs;
    }
}

