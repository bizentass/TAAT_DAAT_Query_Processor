import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;


class Computations {
	
	String current_line;
	String[] sub_str_terms = null;
	String[] sub_str_postings_list_size = null;
	String[] sub_str_postings_list_arr = null;
	String[] sub_str_postings_list_sub_arr = null;
	String sub_str_postings_list = null;
	String document_id = null;
	String document_term_frequency = null;
	HashMap<String,TermInfo> terms = new HashMap<String,TermInfo>();
	ArrayList<String> postings_list_size = new ArrayList<String>();
	ArrayList<String> postings_list = new ArrayList<String>();
	HashMap<String,LinkedList<Integer>> term_doc_id = new HashMap<String,LinkedList<Integer>>();
	HashMap<String,LinkedList<Integer>> term_doc_freq = new HashMap<String,LinkedList<Integer>>();
	HashMap<String,Integer> term_posting_size = new HashMap<String,Integer>();
	LinkedList<Integer> posting_1 = new LinkedList<Integer>();
	LinkedList<Integer> posting_2 = new LinkedList<Integer>();
	ArrayList<ListIterator<Integer>> posting_daat_iter = new ArrayList<ListIterator<Integer>>();
	ListIterator<Integer> posting_1_iterator = null;
	ListIterator<Integer> posting_2_iterator = null;
	int diff = 0;
	int comparisons = 0;
	
	public Computations(BufferedReader br_idx) {
		
		try {
			while((current_line = br_idx.readLine()) != null) {
				
				//Reading the file and getting the terms line by line
				sub_str_terms = current_line.split("\\\\c");
				sub_str_postings_list_size = sub_str_terms[1].split("\\\\m");
				postings_list_size.add(sub_str_postings_list_size[0]);
				sub_str_postings_list = sub_str_postings_list_size[1];
				postings_list.add(sub_str_postings_list);
				sub_str_postings_list_arr = sub_str_postings_list.replace("[", "").replace("]","").replace(" ","").split(",");
				LinkedList<Integer> doc_id_ll = new LinkedList<Integer>();
				LinkedList<Integer> doc_term_frequency_ll = new LinkedList<Integer>();
				HashMap<String, Integer> doc_id_term_frequency_map = new HashMap<String, Integer>();
				for(String value : sub_str_postings_list_arr)
				{
					sub_str_postings_list_sub_arr = value.split("/");
					document_id = sub_str_postings_list_sub_arr[0];
					document_term_frequency = sub_str_postings_list_sub_arr[1];
					
					//Mapping the doc_id and frequency
					doc_id_term_frequency_map.put(document_id,Integer.parseInt(document_term_frequency));
				    doc_id_ll.add(Integer.parseInt(document_id));
				}
				
				TermInfo term_obj = new TermInfo();
				
				// Adding to Term_Doc_ID HashMap
			    term_obj.TermInfo_ID(sub_str_terms[0],doc_id_ll);
			    term_doc_id.put(term_obj.Term,term_obj.docu_id);
				//term_doc_id.put(sub_str_terms[0],doc_id_ll);
			    
				// Adding to Term_Doc_Frequency HashMap
			    doc_id_term_frequency_map = comparatorSortingDesc(doc_id_term_frequency_map);
			    // Here we get the value of the Document ID's sorted in ascending order of term frequency
			    for (Map.Entry<String, Integer> entry : doc_id_term_frequency_map.entrySet()) {
			    	doc_term_frequency_ll.add(Integer.parseInt(entry.getKey()));
			    }
			    //Now you reverse the order of the linked-list, making it descending
			    term_obj.TermInfo_TDF(sub_str_terms[0],doc_term_frequency_ll);
			    term_doc_freq.put(term_obj.Term,term_obj.docu_freq);
			    //term_doc_freq.put(sub_str_terms[0],doc_term_frequency_ll);
			    
			    // Term_Posting_Size
			    term_posting_size.put(term_obj.Term, Integer.parseInt(sub_str_postings_list_size[0]));
			    //term_posting_size.put(sub_str_terms[0], Integer.parseInt(sub_str_postings_list_size[0]));
			    term_posting_size = comparatorSortingDesc(term_posting_size);
			    
				terms.put(term_obj.Term,term_obj);
			}
		} 
		
		catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	/*---- Arrange a HashMap in descending order --------*/
	
	public HashMap<String, Integer> comparatorSortingDesc(HashMap<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	/*---- Arrange a HashMap in ascending order --------*/
	
	public HashMap<String, Integer> comparatorSortingAsc(HashMap<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	/*---- Printing the top K terms --------*/
	
	public void getTopK(int k_val) {
		
		CSE535Assignment.writer.println("FUNCTION: getTopK "+k_val);
			
		Iterator<Entry<String, Integer>> term_key = term_posting_size.entrySet().iterator();
		CSE535Assignment.writer.print("Result: ");
		for(int i=0; i<k_val; i++) {
			if(i<k_val-1) {
				CSE535Assignment.writer.print(term_key.next().getKey()+",");
			}
			else {
				CSE535Assignment.writer.print(term_key.next().getKey());
				CSE535Assignment.writer.println("");
			}
		}
	}
	
	/*---- Printing the postings list of the terms --------*/
	
	public void getPostings(String term) {
		
		CSE535Assignment.writer.println("FUNCTION: getPostings "+term);
		try{
		    String value = term_doc_id.get(term).toString();  
		    CSE535Assignment.writer.println("Ordered by doc IDs: " + arrayStringifier(value));  
		    value = term_doc_freq.get(term).toString();  
		    CSE535Assignment.writer.println("Ordered by TF: " + arrayStringifier(value)); 
		}catch (NullPointerException ne){
			CSE535Assignment.writer.println("Term is not found!");
		}
	}
	
	/*------- TAAT AND --------*/
	
	public void termAtATimeQueryAnd(String[] query_terms){
		
		long startTime = System.currentTimeMillis();
		
		LinkedList<Integer> result = new LinkedList<Integer>();
		
		for(String s: query_terms){
			if(!term_doc_freq.containsKey(s)){
				CSE535Assignment.writer.println("FUNCTION: termAtATimeQueryAnd "+ arrayStringifier(Arrays.toString(query_terms)));
				CSE535Assignment.writer.println("terms not found");
				return;
			}
		}
		
		comparisons = 0;
		ArrayList<String> opt_query_terms_list = new ArrayList<String>();
		String[] opt_query_terms_array;
		int opt_comparisons = 0;
		HashMap<String,Integer> opt_posting_count = new HashMap<String,Integer>();
		
		result = term_doc_freq.get(query_terms[0]);
		
		// Comparing two postings_lists
		for(int i = 1; i < query_terms.length; i++) {
			result = merge(result, term_doc_freq.get(query_terms[i]));			
		}
		
		long endTime = System.currentTimeMillis();
		
		Collections.sort(result);
		
		//------------ FOR BONUS POINTS -----------------//
		for(String s:query_terms) {
			opt_posting_count.put(s,term_posting_size.get(s));
		}
		
		// Sorting by ascending order of posting size
		opt_posting_count = comparatorSortingAsc(opt_posting_count);
		
		for (Entry<String, Integer> term_posting_count : opt_posting_count.entrySet()) {
			opt_query_terms_list.add(term_posting_count.getKey());
		}
		
		opt_query_terms_array = opt_query_terms_list.toArray(new String[opt_query_terms_list.size()]);
		opt_comparisons = optTermAtATimeQueryAnd(opt_query_terms_array);
		
		CSE535Assignment.writer.println("FUNCTION: termAtATimeQueryAnd "+ arrayStringifier(Arrays.toString(query_terms)));
		CSE535Assignment.writer.println(result.size() +" documents are found");
		CSE535Assignment.writer.println(comparisons + " comparisons are made");
		CSE535Assignment.writer.println((endTime - startTime)/100 + " seconds are used");
		CSE535Assignment.writer.println(opt_comparisons + " comparisons are made with optimization");
		CSE535Assignment.writer.println("Result: "+ arrayStringifier(Arrays.toString(result.toArray())));
	}
	
	private LinkedList<Integer> merge(LinkedList<Integer> a, LinkedList<Integer> b) {
		LinkedList<Integer> result = new LinkedList<>();
		
		for(int i: a) {
			for(int j: b) {
				comparisons++;
				if(i == j) {
					result.add(i);
				}
			}
		}
		
		return result;
	}
	
	/*-------- TAAT AND (Optimal Arrangement of query terms) --------*/
	
	public int optTermAtATimeQueryAnd(String[] query_terms){
		LinkedList<Integer> result = null;
		comparisons = 0;
		
		// Comparing two postings_lists
		result = term_doc_freq.get(query_terms[0]);
		
		// Comparing two postings_lists
		for(int i = 1; i < query_terms.length; i++) {
			result = merge(result, term_doc_freq.get(query_terms[i]));			
		}
		return comparisons;
	}
	
	
	/*------- TAAT OR --------*/
	
	public void termAtATimeQueryOr(String[] query_terms){
		
		query_terms = hasPostings(query_terms);
		if(query_terms.length == 0) {
			CSE535Assignment.writer.println("FUNCTION: termAtATimeQueryOr "+ arrayStringifier(Arrays.toString(query_terms)));
			CSE535Assignment.writer.println("terms not found");
			return;
		}
		long startTime = System.currentTimeMillis();
		LinkedList<Integer> result = new LinkedList<Integer>();		
		result = term_doc_freq.get(query_terms[0]); //we get the posting list values sorted by document frequency
		comparisons = 0;
		ArrayList<String> opt_query_terms_list = new ArrayList<String>();
		String[] opt_query_terms_array;
		int opt_comparisons = 0;
		HashMap<String,Integer> opt_posting_count = new HashMap<String,Integer>();
		
		for(int i = 1; i < query_terms.length; i++) {
			
			for(int a: term_doc_freq.get(query_terms[i])) {
				if(!hasDocID(result, a))
					result.add(a);
			}		
		}
		
		Collections.sort(result);
		
		long endTime = System.currentTimeMillis();
		
		//------------ FOR BONUS POINTS -----------------//
		for(String s:query_terms) {
			opt_posting_count.put(s,term_posting_size.get(s));
		}
		
		opt_posting_count = comparatorSortingAsc(opt_posting_count);
		
		for (Entry<String, Integer> term_posting_count : opt_posting_count.entrySet()) {
			opt_query_terms_list.add(term_posting_count.getKey());
		}
		
		opt_query_terms_array = opt_query_terms_list.toArray(new String[opt_query_terms_list.size()]);
		opt_comparisons = optTermAtATimeQueryOr(opt_query_terms_array);
		
		CSE535Assignment.writer.println("FUNCTION: termAtATimeQueryOr "+ arrayStringifier(Arrays.toString(query_terms)));
		CSE535Assignment.writer.println(result.size() +" documents are found");
		CSE535Assignment.writer.println(comparisons + " comparisons are made");
		CSE535Assignment.writer.println((endTime - startTime)/100 + " seconds are used");
		CSE535Assignment.writer.println(opt_comparisons + " comparisons are made with optimization");
		CSE535Assignment.writer.println("Result: "+ arrayStringifier(Arrays.toString(result.toArray())));
		
	}
	
	/*------- TAAT OR (Optimal Arrangement of query terms) --------*/
	
	public int optTermAtATimeQueryOr(String[] query_terms) {
		LinkedList<Integer> result = new LinkedList<Integer>();
		result = term_doc_freq.get(query_terms[0]);
		comparisons = 0;
		
		for(int i = 1; i < query_terms.length; i++) {
			
			for(int a: term_doc_freq.get(query_terms[i])) {
				if(!hasDocID(result, a))
					result.add(a);
			}		
		}
		
		return comparisons;		
	}
	
	/*------- DAAT AND --------*/
	
	public void docAtATimeQueryAnd(String[] query_terms){
		
		long startTime = System.currentTimeMillis();
		
		for(String s: query_terms){
			if(!term_doc_freq.containsKey(s)){
				CSE535Assignment.writer.println("FUNCTION: docAtATimeQueryAnd "+ arrayStringifier(Arrays.toString(query_terms)));
				CSE535Assignment.writer.println("terms not found");
				return;
			}
		}
		
		LinkedList<Integer> result = new LinkedList<Integer>(); 
		HashMap<String, LinkedList<Integer>> posting_daat = new HashMap<String,LinkedList<Integer>>();
		HashMap<String,Integer> docu_post_pointer = new HashMap<String,Integer>();
		int ptr_value = 0;
		
		// Retrieving all the query_terms and putting them in a hash map. Initially set all the pointers to zero.
		for (String s: query_terms) {
			posting_daat.put(s,terms.get(s).getDocID());
			docu_post_pointer.put(s,ptr_value);
		}
		
		int comparisons = 0;
		
		int max_daat_value = 0; // Initial declaration of max_value
		
		while_loop:
		while(true) {
			
			int equal_counter = 1;
			
			for (int i=0; i<query_terms.length; i++) {
				
					int linked_list_size = posting_daat.get(query_terms[i]).size();
					
					// Exits the while loop when at-least one of the terms has run out of postings list
					if(docu_post_pointer.get(query_terms[i]) < linked_list_size) {
						
						// Check if the document_id is greater than the maximum value. If true, make that the maximum
						if(posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i])) > max_daat_value) {
							comparisons++;
							max_daat_value = posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i]));
						}
						
						// Check if the document_id is lesser than the maximum value. If true, increment the pointer
						else if(posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i])) < max_daat_value) {
							comparisons++;
							docu_post_pointer.put(query_terms[i],docu_post_pointer.get(query_terms[i])+1);
						}
						
						// Check if the document_id is equal to the maximum value.
						else if(posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i])) == (max_daat_value)) {
							equal_counter++;
							// If the equal counter is equal to the number of terms compared, then it is an intersection. 
							// Add it in the result set.
							if(equal_counter >= query_terms.length) {
								result.add(posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i])));
								for(int j = 0;j<query_terms.length; j++) {
									docu_post_pointer.put(query_terms[j],docu_post_pointer.get(query_terms[j])+1);
								}
								equal_counter = 1;
							}								
							comparisons++;
						}
					}
					else{
						break while_loop;
					}
			}
		}	
			
		long endTime = System.currentTimeMillis();
		
		CSE535Assignment.writer.println("FUNCTION: docAtATimeQueryAnd " +arrayStringifier(Arrays.toString(query_terms)));
		CSE535Assignment.writer.println(result.size() +" documents are found");
		CSE535Assignment.writer.println(comparisons + " comparisons are made");
		CSE535Assignment.writer.println((endTime - startTime)/100 + " seconds are used");
		CSE535Assignment.writer.println("Result: "+ arrayStringifier(Arrays.toString(result.toArray())));
		
	}
	
	/*------- DAAT OR --------*/
	
	public void docAtATimeQueryOr(String[] query_terms) {
		
		long startTime = System.currentTimeMillis();
		
		query_terms = hasPostings(query_terms);
		if(query_terms.length == 0) {
			CSE535Assignment.writer.println("FUNCTION: docAtATimeQueryOr "+ arrayStringifier(Arrays.toString(query_terms)));
			CSE535Assignment.writer.println("terms not found");
			return;
		}
		
		LinkedList<Integer> result = new LinkedList<Integer>();
		HashMap<String, LinkedList<Integer>> posting_daat = new HashMap<String,LinkedList<Integer>>();
		HashMap<String,Integer> docu_post_pointer = new HashMap<String,Integer>();
		int ptr_value = 0;
		
		// Retrieving all the query_terms and putting them in a hash map. Initially set all the pointers to zero.
		for (String s: query_terms) {
			posting_daat.put(s,terms.get(s).getDocID());
			docu_post_pointer.put(s,ptr_value);
		}
		
		int comparisons = 0;
		
		int min_daat_value = 0;
		
		while_loop:
		while(true) {
			
			ArrayList<Integer> min_daat_list = new ArrayList<Integer>(); 
			
			for(String s: query_terms) {
				if(docu_post_pointer.get(s) < posting_daat.get(s).size()) {
					min_daat_list.add(posting_daat.get(s).get(docu_post_pointer.get(s)));
				}
			}
			
			// Finding out the minimum of the compared values.
			if(min_daat_list.isEmpty()) {
				break while_loop;
			}
			min_daat_value = Collections.min(min_daat_list);
			result.add(min_daat_value);
			
			for (int i=0; i<query_terms.length; i++) {
				
					int linked_list_size = posting_daat.get(query_terms[i]).size();
					
					if(docu_post_pointer.get(query_terms[i]) < linked_list_size) {
						
						// Increment if minimum value equal to the current value. Add it to the result only once. Remove multiple occurrences.
						if(posting_daat.get(query_terms[i]).get(docu_post_pointer.get(query_terms[i])).equals(min_daat_value)) {
							docu_post_pointer.put(query_terms[i],docu_post_pointer.get(query_terms[i])+1);
							comparisons++;
						}
					}
					else {
						
						//Condition to check if all the postings list have been traversed.
						int finished_counter = 0;
						for(String s: query_terms) {
							if(docu_post_pointer.get(s) == posting_daat.get(s).size()) {
								finished_counter++;
								if(finished_counter == query_terms.length - 1) {
									break while_loop;
								}
							}
						}
					}
			}
		}	
			
		long endTime = System.currentTimeMillis();
		
		Collections.sort(result);
		
		CSE535Assignment.writer.println("FUNCTION: docAtATimeQueryOr "+ arrayStringifier(Arrays.toString(query_terms)));
		CSE535Assignment.writer.println(result.size() +" documents are found");
		CSE535Assignment.writer.println(comparisons + " comparisons are made");
		CSE535Assignment.writer.println((endTime - startTime)/100 + " seconds are used");
		CSE535Assignment.writer.println("Result: "+ arrayStringifier(Arrays.toString(result.toArray())));
		
	}
	
	// Checks for duplicate documents
	public Boolean hasDocID(LinkedList<Integer> list, int a) {
		for(int i: list) {
			comparisons++;
			if(i == a) {
				return true;
			}
		}
		return false;
	}
	
	//Checks for Posting's list
	public String[] hasPostings(String[] query_terms) {
		ArrayList<String> list = new ArrayList<>();
		for(String s: query_terms){
			if(term_doc_freq.containsKey(s)){
				list.add(s);
			}
		}
		return list.toArray(new String[list.size()]);
		
	}
	
	// Beautify the result
	public String arrayStringifier(String original_string) {
		
		String modified_string = original_string.replace("[","").replace("]", "");
		return modified_string;
	}
}

/* TERMINFO Class which stores the term information --*/

class TermInfo {
	
	public String Term;
	public LinkedList<Integer> docu_id = new LinkedList<Integer>();
	public LinkedList<Integer> docu_freq = new LinkedList<Integer>();
	
	public void TermInfo_ID(String Term,LinkedList<Integer> docu_id) {
		this.Term = Term;
		this.docu_id = docu_id;
	}
	
	public void TermInfo_TDF(String Term,LinkedList<Integer> docu_freq) {
		this.Term = Term;
		this.docu_freq = docu_freq;
	}

	public String getTerm() {
		return Term;
	}
	
	public LinkedList<Integer> getDocID() {
		return docu_id;
	}
	public LinkedList<Integer> getDocFreq() {
		return docu_freq;
	}
}

class FileLogger {
	
	private File logFile;
	
	public FileLogger(String filename) {
		this.logFile = new File(filename);
		if(this.logFile.exists())
			this.logFile.delete();
		try {
			this.logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void println(String message) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFile.getAbsoluteFile(), true));
			writer.write(message + "\n");
			writer.close();
		} catch(Exception e) {}
	}
	
	public void print(String message) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFile.getAbsoluteFile(), true));
			writer.write(message);
			writer.close();
		} catch(Exception e) {}
	}
}

public class TAATDAATQProc {
	
	static FileLogger writer;
	
	public static void main(String[] args) {
		
		String index_file_name = args[0];
		String log_file_name = args[1];
		int k_value = Integer.parseInt(args[2]);
		String query_terms_file = args[3];
		String[] query_terms = null;
		String current_line = null;
		
		if(args.length < 4) {
			System.out.println("Please pass four arguments!");
			System.exit(1);
		}
		
		writer = new FileLogger(log_file_name);
		
		BufferedReader br_idx = null; 
		BufferedReader br_que_fi = null; 

		try {
			br_idx = new BufferedReader(new FileReader(index_file_name));
			br_que_fi =  new BufferedReader(new FileReader(query_terms_file));
			Computations com_oj = new Computations(br_idx);
			com_oj.getTopK(k_value);
			while((current_line = br_que_fi.readLine()) != null) {
				query_terms = current_line.split(" ");
				for (int i=0;i<query_terms.length;i++) {
					com_oj.getPostings(query_terms[i]);
				}
				com_oj.termAtATimeQueryAnd(query_terms); 
				com_oj.termAtATimeQueryOr(query_terms);
				com_oj.docAtATimeQueryAnd(query_terms);
				com_oj.docAtATimeQueryOr(query_terms);
			}
			br_idx.close();
			br_que_fi.close();
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
