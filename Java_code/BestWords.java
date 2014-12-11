import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

/**
 * Gets the most important features from a feature vector.
 * @author sarah jundt, shannon lubetich, george price
 *
 */
public class BestWords {
	protected Map<Integer, String> dictionaryMap;
	int maxUnigrams; //maximum feature number for unigrams

	/**
	 * Sorts features by weight & prints the most important.
	 * requires eternal dictionary file
	 * @param featureVector - features & weights
	 * @param dictionaryFile- map: (feature: feature number) 
	 */
	public BestWords(String dictionaryFile, String featureVector){		
		dictionaryMap = new HashMap<Integer,String>();
		readDictionary(dictionaryFile);
		getBestWords(featureVector);
		maxUnigrams=0;
	}
	
	/**
	 * used to read in the dictionary from external file and save in 
	 * dictionaryMap of String (unigram or bigram) to feature index
	 * @param dictionaryFile
	 */
	private void readDictionary(String dictionaryFile){
		//read in vocab
		String wordEntry;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dictionaryFile));
			while((wordEntry=reader.readLine())!=null){
				Matcher m = Pattern.compile("(.+):(.+)").matcher(wordEntry);
				if(m.matches()){
					//store in dictionary map as word --> feature number
					int nextKey = Integer.parseInt(m.group(2));
					dictionaryMap.put(nextKey, m.group(1));
					if (maxUnigrams<nextKey){
						maxUnigrams = nextKey;
					};
				}
			}
			int i = maxUnigrams+1;
			dictionaryMap.put(i, "DAY OF WEEK");
			i++;
			dictionaryMap.put(i, "HEADER?");
			i++;
			dictionaryMap.put(i, "NUMBER CAPITALS");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Issues loading dictionary file from training.");
		}
		//next index starts at maxUnigrams+!
	}

	/**
	 * Prints the 100 highest-weighted features (in the positive direction)
	 * @param featureVector- features numbers & weights
	 */
	private void getBestWords(String featureVector){
		try {
			//get absolute-value positive weighted features. remove Collections.reverseOrder() to get high absolute-value negative weights.
			TreeMap<Float, Set<String>> weights = new TreeMap<Float, Set<String>>(Collections.reverseOrder()); 
			BufferedReader reader = new BufferedReader(new FileReader(featureVector));
			String wordEntry;
			while((wordEntry=reader.readLine())!=null){
				Matcher m = Pattern.compile("(.+):(.+)").matcher(wordEntry);
				if(m.matches()){
					//store in dictionary map as word --> feature number
					int featureNum = Integer.parseInt(m.group(1));
					String thisFeature = dictionaryMap.get(featureNum);
					float weight = Float.parseFloat(m.group(2));
					Set<String> weightSet = weights.get(weight)!=null ? weights.get(weight) : new HashSet<String>();
					weightSet.add(thisFeature);
					weights.put(weight, weightSet);
				}
			}
			Iterator weightIter = weights.keySet().iterator();
			int i=0;
			while (weightIter.hasNext() && i<100){
				Float next = (Float) weightIter.next();
				System.out.println(weights.get(next)+"\t"+next);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints 100 best features for dictionary & feature vector passed in.
	 * @param args - dictionary, feature file.
	 */
	public static void main(String[] args){
		new BestWords(args[0], args[1]);
	}
}
