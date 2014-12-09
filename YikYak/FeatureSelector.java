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

// import features.Yak.School;

//make a list of vocab words that we can iterate in an order
//keep a set of unique words

public class FeatureSelector {
	protected static final String FEATURE_PREFIX = "";
	double stopListParam = 0.01;
	public static final String ALL_SCHOOLS = "all_schools";
	protected  Map<String, HashMap<String, Integer>> bigrams;
	protected  HashMap<String, Integer> unigrams = new HashMap<String, Integer>();
	protected  Set<String> vocabulary;
//	protected List<Yak> yaks;
	protected Set<String> uniqueVocab;
	protected Map<String, Integer> dictionaryMap;
	protected String fileExt; 
	protected int maxUnigrams;
	protected boolean[] featureFlags;

	/**
	 * for training
	 * and creating external dictionary file
	 * @param inputFiles
	 */
	public FeatureSelector(List<String> inputFiles, String filenameaddon, boolean[] featureFlags) {
		vocabulary = new HashSet<String>();
//		yaks = new ArrayList<Yak>();
		uniqueVocab = new HashSet<String>();
		dictionaryMap = new HashMap<String,Integer>();
		fileExt = filenameaddon;
		this.featureFlags=featureFlags;
		firstPass(inputFiles, stopListParam);
		secondPass(inputFiles, true);
	}
	
	/**
	 * for testing
	 * requires eternal dictionary file
	 * @param testFiles
	 * @param dictionaryFile
	 */
	public FeatureSelector(List<String> testFiles, String dictionaryFile, String filenameaddon, boolean[] featureFlags){		
		vocabulary = new HashSet<String>();
		dictionaryMap = new HashMap<String,Integer>();
		fileExt = filenameaddon;
		this.featureFlags=featureFlags;
		readDictionary(dictionaryFile);
		secondPass(testFiles, false);
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
					dictionaryMap.put(m.group(1),nextKey);
					if (maxUnigrams<nextKey){
						maxUnigrams = nextKey;
					};
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Issues loading dictionary file from training.");
		}
	}
	
	/**
	 * FirstPass is only called on training. 
	 * It iterates through all Yaks in all files and stores all unigrams and bigrams in a 
	 * dictionaryMap of String to feature index.
	 * It then writes this out to a file. 
	 * @param inputFiles
	 */
	private void firstPass(List<String> inputFiles, double commonFraction) {
		try {
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header;
			int likes, postDate, commentNum;
			double postTime;
			
			for (String inputFile: inputFiles){
				Matcher m = Pattern.compile("(.*)File.*").matcher(inputFile);
				School school = null;
				int hour = 0;
				if (!m.matches()){
					throw new RuntimeException("Problems with school/hour matching in filename");
				}
				reader = new BufferedReader(new FileReader(inputFile));
				while((sentence=reader.readLine())!=null){
					if (sentence.matches("_+")){
						//this is a new yikyak.
						reader.readLine(); //number
						reader.readLine(); //date collected & possibly header
						
						//this method reads the next line, which should be the Yak,
						//and stores the vocab correctly
						yakText = reader.readLine();
						parseWordsFirstPass(yakText);
						
	
						reader.readLine();//likeLine
						reader.readLine();//comments line
					} else {
						reader.close();
						throw new RuntimeException("Number of lines of a yikyak isn't what was expected");
					}
				}
				reader.close();
			}

			TreeMap<Double, String> sorted = new TreeMap<Double, String>(Collections.reverseOrder());

		for (String k: unigrams.keySet()){
			Double thisDouble = unigrams.get(k)+0.0;
			while (sorted.containsKey(thisDouble)){
				thisDouble = thisDouble+0.00000000001;
			}
			sorted.put(thisDouble, k);
		}
		for (int i = 0; i <unigrams.size()*commonFraction; i++){
			Entry<Double, String> next = sorted.pollFirstEntry();
			sorted.remove(next.getKey());
			dictionaryMap.remove(next.getValue());
		}
		for (String word: uniqueVocab){
			dictionaryMap.remove(word);
		}
			
			//write out dictionary
			PrintWriter writer = new PrintWriter(new FileWriter("dictionary.txt"));
			for(String word: dictionaryMap.keySet()){
				writer.println(word+":"+dictionaryMap.get(word));
			}
			writer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * for a single yak,
	 * appropriately parses and stores all unigrams and bigrams
	 * @param yakText
	 */
	private void parseWordsFirstPass(String yakText) {
		List<String> yakWords = getWords(yakText);
		//unigrams
		for (String word: yakWords){
			addToVocab(word);
		}
		//bigrams
		// for(int i = 0; i<yakWords.size()-1;i++){
		// 	int size = dictionaryMap.size();

		// 	String bigram = yakWords.get(i)+" "+yakWords.get(i+1);

		// 	if(!dictionaryMap.containsKey(bigram)){
		// 		dictionaryMap.put(yakWords.get(i)+" "+yakWords.get(i+1), size+1);		
		// 	}
		// }
	}
	
	/**
	 * used for storing unigrams
	 * always adds to vocabulary
	 * only adds to uniqueVocab and dictionaryMap if this is the first time we're seeing it
	 * appropriately removes from uniqueVocab if we're seeing a word we've seen already
	 * @param word
	 */
	private void addToVocab(String word){

		int count = unigrams.get(word) == null ? 0 : unigrams.get(word);
		unigrams.put(word,count+1);

		//since is set, add(word) returns true if successfully added, false if already there
		if (vocabulary.add(word)){
			uniqueVocab.add(word);
			dictionaryMap.put(word, dictionaryMap.size()+1);
		} else {
			uniqueVocab.remove(word); //not actually unique, because already in set of vocab
		}
		maxUnigrams = dictionaryMap.size();
	}
	
	/**
	 * secondPass is called in both training or testing, with a boolean parameter to specify which
	 * in training, the actual like numbers are written out to the files with the feature vector
	 * in testing, a 0 is written out as the score with the feature vector, and a separate file is created of the actual likes received
	 * @param inputFiles
	 * @param train
	 */
	private void secondPass(List<String> inputFiles, boolean train) {
		try {
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header;
			int likes, postDate, commentNum, uniqueCount=0, numWords, numChars, dayOfWeek;
			long postTime;
			TreeMap<Integer,Integer> gramFeatureMap;
			String featureFile;
			if(train){
				featureFile=fileExt+".features_train";
			}else{
				featureFile = fileExt+".features_test";
			}
			PrintWriter featureW = new PrintWriter(new FileWriter(featureFile+".txt")); //features of cases
			PrintWriter labelW = new PrintWriter(new FileWriter(fileExt+".labels.txt")); //labels of cases
			//get vocab & store yaks
			Pattern headerPattern = Pattern.compile(".*###\\s(.*)\\s###"); //group(1)== header
			Pattern likeLinePattern = Pattern.compile("\\s*(-?\\d+)\\slikes.*Posted\\s+([0123456789-]+)\\s+([0123456789:]+).*");
//			"\\s(-?\\d+)\\slikes\\s*|\\s*Posted\\s*(\\d[4]-\\d[2]-\\d[2])\\s*(\\d[2]:\\d[2]:\\d[2]).*");
			//group(1)==number of likes. group(2)==date posted. group(3)==time posted.
			Pattern commentPattern = Pattern.compile("\\s*Comments:\\s*(\\d+)"); //group(1)==commentNum
			
			Calendar calendar = Calendar.getInstance();
			
			for (String inputFile: inputFiles){
				Matcher m = Pattern.compile("(.*)File.*").matcher(inputFile);
				School school = null;
				int hour = 0;
				if (m.matches()){
					school = School.getSchool(m.group(1));
//					hour = Integer.parseInt(m.group(1));
				} else {
					throw new RuntimeException("Problems with school/hour matching in filename");
				}
				reader = new BufferedReader(new FileReader(inputFile));
				while((sentence=reader.readLine())!=null){
					if (sentence.matches("_+")){
						//this is a new yikyak.
						reader.readLine(); //number
						maybeHeader = reader.readLine(); //date collected & possibly header
						try{
							Matcher mHeader = headerPattern.matcher(maybeHeader);
							mHeader.matches();
							header = mHeader.group(1);
						}
						catch(IllegalStateException e){
							header = null;
						}
						
						
						//this method reads the next line, which should be the Yak,
						yakText = reader.readLine();
						HashMap<String,Integer> bagOfWords = new HashMap<String,Integer>();
						List<String> yakWords = getWords(yakText);
						numChars = yakText.length();
						numWords = yakWords.size();
						for (String word: yakWords){
							Integer wordCount = bagOfWords.get(word) != null ? bagOfWords.get(word) : 0;
							bagOfWords.put(word, wordCount+1);
						}
						gramFeatureMap = new TreeMap<Integer,Integer>();
						for(String word : bagOfWords.keySet()){
							Integer wordNum = dictionaryMap.get(word);
							if(wordNum!=null){
								gramFeatureMap.put(wordNum, bagOfWords.get(word));
							} else if (!train){ //this is for testing
								uniqueCount++;
							}
							if(train && uniqueVocab.contains(word)){ //this is for training
								uniqueCount++;
							}
						}
						//bigrams
//						HashMap<String,Integer> bigramBOW = new HashMap<String,Integer>();
//						for (int i= 0; i < yakWords.size()-1; i++){
//							String bigram = yakWords.get(i)+" "+yakWords.get(i+1);
//							int count = bigramBOW.get(bigram)==null ? 0: bigramBOW.get(bigram);
//							bigramBOW.put(bigram, count+1);
//						}
//						for(String bigram : bigramBOW.keySet()){
//							Integer wordNum = dictionaryMap.get(bigram);
//							if(wordNum!=null){
//								gramFeatureMap.put(wordNum, bigramBOW.get(bigram));
//							} 
//						}
						
						
						
						//likeLine has likes, date, time, GPS
						likeLine = reader.readLine();
						try{
							Matcher mLike = likeLinePattern.matcher(likeLine);
							mLike.matches();
							likes = Integer.parseInt(mLike.group(1));
							calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mLike.group(2)+" "+mLike.group(3)));
							dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;//date is 1-7, subtracting 1 to make 0-6
							postTime = calendar.get(Calendar.SECOND)+calendar.get(Calendar.MINUTE)*60+calendar.get(Calendar.HOUR_OF_DAY)*3600;
						}
						catch(Exception e){
							reader.close();
							throw new RuntimeException("Problem with parsing likes: "+e);
						}

						
						comments = reader.readLine();
						try{
							Matcher mComment = commentPattern.matcher(comments);
							mComment.matches();
							commentNum = Integer.parseInt(mComment.group(1));
						}
						catch(Exception e){
							reader.close();
							throw new RuntimeException("Problem with parsing comments: "+e);
						}
					} else {
						reader.close();
						throw new RuntimeException("Number of lines of a yikyak isn't what was expected");
					}
					
					//print out features, and likes, for each and every Yak
					boolean printingThisFeature = false;
					if(train){
						if (fileExt.contains(FeatureSelector.ALL_SCHOOLS)){
							if (likes <=2){
								featureW.print("-1"+" ");
								printingThisFeature = true;
							} else if (likes>23){
								featureW.print("+1"+" ");
								printingThisFeature = true;
							}
						} else {
							if (likes <=4){
								featureW.print("-1"+" ");
								printingThisFeature = true;
							} else if (likes>31){
								featureW.print("+1"+" ");
								printingThisFeature = true;
							}
						}
					}else{
						if (fileExt.equals(FeatureSelector.ALL_SCHOOLS)){
							if (likes <=2){
								featureW.print(0+" ");
								labelW.println("-1");	//prints likes only in separate file for testing
								printingThisFeature = true;
							} else if (likes>23){
								featureW.print(0+" ");
								labelW.println("+1");	//prints likes only in separate file for testing
								printingThisFeature = true;
							}
						} else {
							if (likes <=4){
								featureW.print(0+" ");
								labelW.println("-1");	//prints likes only in separate file for testing
								printingThisFeature = true;
							} else if (likes>31){
								featureW.print(0+" ");
								labelW.println("+1");	//prints likes only in separate file for testing
								printingThisFeature = true;
							}
						}
					}

					if (printingThisFeature){
						int nextIndex;
						//unigrams
						if(featureFlags[0]){
							while(!gramFeatureMap.isEmpty()){
								Entry<Integer, Integer> feature = gramFeatureMap.pollFirstEntry();
								featureW.print(feature.getKey()+":"+feature.getValue()+" ");
							}
							nextIndex = maxUnigrams+1;
						}
						else{
							nextIndex = 1;
						}
						if(featureFlags[1]){
							//dayOfWeek (each is a separate feature that receives a 1 if it is that day.)
							featureW.print(nextIndex+dayOfWeek+":1 ");
							nextIndex+=7; //increment next index over all possible day indices
						}

						if(featureFlags[2]){
							//posttime
							featureW.print(nextIndex+":"+postTime+" ");
							nextIndex++;
						}
						if(featureFlags[3]){
							//header?
							if(header != null){
								featureW.print(nextIndex+":1 ");
							}
							nextIndex++;
						}
						if(featureFlags[4]){
							//length (words)
							featureW.print(nextIndex+":"+numWords+" ");
							nextIndex++;
						}
						if(featureFlags[5]){
							//length (characters)
							featureW.print(nextIndex+":"+numChars+" ");
							nextIndex++;
						}
						if(featureFlags[6]){
							//unique words
							featureW.print(nextIndex+":"+uniqueCount+" ");
							nextIndex++;
						}
						if(featureFlags[7]){
							//num capital letters
							featureW.print(nextIndex+":"+numCapitalLetters(yakText)+" ");
							nextIndex++;	
						}
						 featureW.print("\n");
					}
					
				}
				reader.close();
			}
			featureW.close();	//must close out of file loop in order to write all features for all input files
			labelW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * calculates the number of capital letters in a yak 
	 * feature for regression
	 * @param yak
	 * @return
	 */
	private int numCapitalLetters(String yak){
		int result = 0;
		for (int i = 0; i < yak.length(); i++){
			if (Character.isUpperCase(yak.charAt(i))){
				result+=1;
			}
		}
		return result;
	}
	
	/**
	 * returns a List of strings that are the items in the YikYak, split on whitespace
	 * @param text
	 * @return
	 */
	public static List<String> getWords(String text){
		text = text.trim(); //eliminate trailing whitespace (which for some reason was being seen as its own word
		String[] words = text.toLowerCase().split("\\s+");
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < words.length; i++){
			String word = words[i];
			while (word.matches("[.,!?\"'&/(#:-].+")){
				result.add(word.substring(0,1));
				word = word.substring(1, word.length());
			}
			List<String> afterWords = new ArrayList<String>();
			while (word.matches(".+[.,!?\"'&/)#-:]")){
				afterWords.add(0,word.substring(word.length()-1));
				word = word.substring(0, word.length()-1);
			}
			result.add(word);
			result.addAll(afterWords);
		}
		return result;
	}
	
	/**
	 * enum for school
	 * use as feature
	 */
	public enum School {
		COLUMBIA, CLAREMONT, GEORGIA, TEXAS, CLEMSON, WAKE, STANFORD, COLGATE, UTAH;
		public static School getSchool(String s){
			String upper = s.toUpperCase();
			if (upper.equals("COLUMBIA")){
				return COLUMBIA;
			}else if (upper.equals("CLAREMONT")){
				return CLAREMONT;
			}else if (upper.equals("STANFORD")){
					return STANFORD;
			}else if (upper.equals("GEORGIA")){
				return GEORGIA;
			} else if (upper.equals("TEXAS")){
				return TEXAS;
			} else if (upper.equals("CLEMSON")){
				return CLEMSON;
			} else if (upper.equals("WAKE")){
				return WAKE;
			} else if (upper.equals("COLGATE")){
				return COLGATE;
			} else if (upper.equals("UTAH")){
				return UTAH;
			} else {
				throw new RuntimeException("Invalid schoolname");
			}
		}
	}
	
	public static void main(String[] args) {
		boolean[] featureFlags = new boolean[args.length];
		for(int i =0; i <args.length;i++){
			featureFlags[i]=(Integer.parseInt(args[i])==1);
		}
		
		
		// TODO Auto-generated method stub
		ArrayList<String> schools = new ArrayList<String>();
		schools.add("claremont");
//		schools.add("clemson");
//		schools.add("columbia");
//		schools.add("colgate");
//		schools.add("georgia");
//		schools.add("stanford");
//		schools.add("texas");
//		schools.add("utah");
//		schools.add("wake");

		//trains & tests for each individual school
		 for(String school : schools){

		 	List<String> inputFiles = new ArrayList<String>();
		 	inputFiles.add(school+"File.train");
		 	FeatureSelector trainData = new FeatureSelector(inputFiles, school, featureFlags);
			
			
		 	List<String> timeTestFiles = new ArrayList<String>();
		 	timeTestFiles.add(school+"File.time");
		 	FeatureSelector timeData = new FeatureSelector(timeTestFiles, "dictionary.txt", school+".time", featureFlags);

		 	List<String> randTestFiles = new ArrayList<String>();
		 	randTestFiles.add(school+"File.rand");

		 	FeatureSelector randData = new FeatureSelector(randTestFiles, "dictionary.txt", school+".rand", featureFlags);
		 }
		
		ArrayList<String> allSchoolsTrain = new ArrayList<String>();
		allSchoolsTrain.add("claremontFile.train");
		allSchoolsTrain.add("clemsonFile.train");
		allSchoolsTrain.add("columbiaFile.train");
		allSchoolsTrain.add("colgateFile.train");
		allSchoolsTrain.add("georgiaFile.train");
		allSchoolsTrain.add("stanfordFile.train");
		allSchoolsTrain.add("texasFile.train");
		allSchoolsTrain.add("utahFile.train");
		allSchoolsTrain.add("wakeFile.train");
		FeatureSelector allSchools = new FeatureSelector(allSchoolsTrain, FeatureSelector.ALL_SCHOOLS, featureFlags);
		
		 ArrayList<String> timeAll = new ArrayList<String>();
		 timeAll.add("claremontFile.time");
		 timeAll.add("clemsonFile.time");
		 timeAll.add("columbiaFile.time");
		 timeAll.add("colgateFile.time");
		 timeAll.add("georgiaFile.time");
		 timeAll.add("stanfordFile.time");
		 timeAll.add("texasFile.time");
		 timeAll.add("utahFile.time");
		 timeAll.add("wakeFile.time");
		 FeatureSelector allSchoolsTime = new FeatureSelector(timeAll, "dictionary.txt", "all_schools.time", featureFlags);
		
		 ArrayList<String> randAll = new ArrayList<String>();
		 randAll.add("claremontFile.rand");
		 randAll.add("clemsonFile.rand");
		 randAll.add("columbiaFile.rand");
		 randAll.add("colgateFile.rand");
		 randAll.add("georgiaFile.rand");
		 randAll.add("stanfordFile.rand");
		 randAll.add("texasFile.rand");
		 randAll.add("utahFile.rand");
		 randAll.add("wakeFile.rand");
		 FeatureSelector allSchoolsRand = new FeatureSelector(randAll, "dictionary.txt", "all_schools.rand", featureFlags);

		
		
	}

}
