package features;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import features.Yak.School;

//make a list of vocab words that we can iterate in an order
//keep a set of unique words

public class FeatureSelector {
	protected  Map<String, HashMap<String, Integer>> bigrams;
	protected  Map<String, Integer> unigrams;
	protected  Set<String> vocabulary;
//	protected List<Yak> yaks;
	protected Set<String> uniqueVocab;
	protected Map<String, Integer> dictionaryMap;

	public FeatureSelector(List<String> inputFiles) {
		vocabulary = new HashSet<String>();
//		yaks = new ArrayList<Yak>();
		uniqueVocab = new HashSet<String>();
		dictionaryMap = new HashMap<String,Integer>();

		
		firstPass(inputFiles);
		
		secondPass(inputFiles);
		

	}
	
	private void firstPass(List<String> inputFiles) {
		try {
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header;
			int likes, postDate, commentNum;
			double postTime;
			
			for (String inputFile: inputFiles){
				Matcher m = Pattern.compile("(\\d)(.*)File.*").matcher(inputFile);
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
			
			//write out dictionary
			PrintWriter writer = new PrintWriter(new FileWriter("vocabulary.txt"));
			int featureNum = 1;
			for(String word: vocabulary){
				writer.println(word+":"+featureNum);
				featureNum++;
			}
			writer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseWordsFirstPass(String yakText) {
		for (String word: getWords(yakText)){
			addToVocab(word);
		}
	}
	

	private void secondPass(List<String> inputFiles) {
		try {
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header, unigramFeatures="";
			int likes, postDate, commentNum, uniqueCount=0, numWords, numChars;
			double postTime;
			PrintWriter featureW = new PrintWriter(new FileWriter("features.txt")); //features of cases
			PrintWriter labelW = new PrintWriter(new FileWriter("labels.txt")); //labels of cases
			
			//get vocab & store yaks
			Pattern headerPattern = Pattern.compile(".*###\\s(.*)\\s###"); //group(1)== header
			Pattern likeLinePattern = Pattern.compile("\\s*(-?\\d+)\\slikes.*");
//			"\\s(-?\\d+)\\slikes\\s*|\\s*Posted\\s*(\\d[4]-\\d[2]-\\d[2])\\s*(\\d[2]:\\d[2]:\\d[2]).*");
			//group(1)==number of likes. group(2)==date posted. group(3)==time posted.
			Pattern commentPattern = Pattern.compile("\\s*Comments:\\s*(\\d+)"); //group(1)==commentNum
			
			//read in vocab
			String wordEntry;
			reader = new BufferedReader(new FileReader("vocabulary.txt"));
			while((wordEntry=reader.readLine())!=null){
				Matcher m = Pattern.compile("(.+):(.+)").matcher(wordEntry);
				if(m.matches()){
					//store in dictionary map as word --> feature number
					dictionaryMap.put(m.group(1),Integer.parseInt(m.group(2)));
				}
			}
			
			
			for (String inputFile: inputFiles){
				Matcher m = Pattern.compile("(\\d)(.*)File.*").matcher(inputFile);
				School school = null;
				int hour = 0;
				if (m.matches()){
					school = School.getSchool(m.group(2));
					hour = Integer.parseInt(m.group(1));
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
						
						
						//SHANNON WORKING ON THIS
						//this method reads the next line, which should be the Yak,
						//and stores the vocab correctly
						yakText = reader.readLine();
						HashMap<String,Integer> bagOfWords = new HashMap<String,Integer>();
						List<String> yakWords = getWords(yakText);
						numChars = yakText.length();
						numWords = yakWords.size();
						unigramFeatures = "";
						for (String word: yakWords){
							Integer wordCount = bagOfWords.get(word) != null ? bagOfWords.get(word) : 0;
							bagOfWords.put(word, wordCount+1);
						}
						for(String word : bagOfWords.keySet()){
							Integer wordNum = dictionaryMap.get(word);
							if(wordNum!=null){
								unigramFeatures += wordNum+":"+bagOfWords.get(word)+" ";
							}
							if(uniqueVocab.contains(word)){
								uniqueCount++;
							}
						}
						
						
						likeLine = reader.readLine();
						try{
							Matcher mLike = likeLinePattern.matcher(likeLine);
							mLike.matches();
							likes = Integer.parseInt(mLike.group(1));
						}
						catch(Exception e){
							reader.close();
							throw new RuntimeException("Problem with parsing likes: "+e);
						}
						//TODO: postdate, posttime
						comments = reader.readLine();
						try{
							Matcher mComment = commentPattern.matcher(comments);
							mComment.matches();
							commentNum = Integer.parseInt(mComment.group(1));
						}
						catch(Exception e){
							reader.close();
							throw new RuntimeException("Problem with parsing comments: "+e);
						}//TODO: post date, post time
					} else {
						reader.close();
						throw new RuntimeException("Number of lines of a yikyak isn't what was expected");
					}
					
					
					
					//SHANNON WORKING ON THIS
					//print out features, and likes, for each and every Yak
					//unigrams
					featureW.print(unigramFeatures); //has space on end
					
					int nextIndex = vocabulary.size()+1;
					
					//header?
					if(header != null){
						featureW.print(nextIndex+":1 ");
					}
					nextIndex++;
					//length (words)
					featureW.print(nextIndex+":"+numWords+" ");
					nextIndex++;
					//length (characters)
					featureW.print(nextIndex+":"+numChars+" ");
					nextIndex++;
					//unique words
					featureW.print(nextIndex+":"+uniqueCount+" ");
					nextIndex++;
					//num capital letters
					featureW.print(nextIndex+":"+numCapitalLetters(yakText)+" ");
					nextIndex++;

					//TODO
					//bigrams
//					HashMap<String, Integer> bigrams = yak.getUnigrams();
//					for (String word1: vocabulary){
//						for (String word2: vocabulary){
//							String bigram = word1+"++"+word2;
//							if (bigrams.get(bigram)!=null){
//								featureW.print(bigrams.get(bigram)+" ");
//							} else {
//								featureW.print(0+" ");
//							}
//						}
//					}
					featureW.print("\n");
					
					//prints out likes in label file (only thing to label file each time
					labelW.println(likes);
				}
				featureW.close();
				labelW.close();
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private int numCapitalLetters(String yak){
		int result = 0;
		for (int i = 0; i < yak.length(); i++){
			if (Character.isUpperCase(yak.charAt(i))){
				result+=1;
			}
		}
		return result;
	}

	
//	//NOT using this right now - SNL
//	public void printFeatures(String outputFeatures, String outputLabels, String features){
//		try{
//			PrintWriter typeW = new PrintWriter(new FileWriter(features)); //types of features we're using
//			PrintWriter featureW = new PrintWriter(new FileWriter(outputFeatures)); //features of cases
//			PrintWriter labelW = new PrintWriter(new FileWriter(outputLabels)); //labels of cases
//			//print the types of features, IN ORDER. SUPER IMPORTANT THAT THEY'RE IN ORDER
//			typeW.println("header exists?");
//			typeW.println("length: words");
//			typeW.println("length: chars");
//			typeW.println("unique words");
//			typeW.println("capitals");
//			for (String word: vocabulary){
//				typeW.println("unigram:"+word);
//			}
//			for (String word1: vocabulary){
//				for (String word2: vocabulary){
//					typeW.println("bigram:"+word1+"++"+word2);
//				}
//			}
//			for (Yak yak: yaks){
//				//header?
//				featureW.print(yak.hasHeader()+" ");
//				//length (words)
//				featureW.print(getWords(yak.yak).size()+" ");
//				//length (characters)
//				featureW.print(yak.yak.length()+" ");
//				//unique words
//				featureW.print(yak.uniqueWords(uniqueVocab)+" ");
//				//num capital letters
//				featureW.print(yak.numCapitalLetters()+" ");
//				//unigrams
//				HashMap<String, Integer> unigrams = yak.getUnigrams();
//				for (String word: vocabulary){
//					if (unigrams.get(word)!=null){
//						featureW.print(unigrams.get(word)+" ");
//					} else {
//						featureW.print(0+" ");
//					}
//				}
//				//bigrams
//				HashMap<String, Integer> bigrams = yak.getUnigrams();
//				for (String word1: vocabulary){
//					for (String word2: vocabulary){
//						String bigram = word1+"++"+word2;
//						if (bigrams.get(bigram)!=null){
//							featureW.print(bigrams.get(bigram)+" ");
//						} else {
//							featureW.print(0+" ");
//						}
//					}
//				}
//				featureW.print("\n");
//				//print label!
//				labelW.println(yak.label);
//			}
//			labelW.close();
//			featureW.close();
//			typeW.close();
//		} catch(IOException e){
//			e.printStackTrace();
//		}
//	}
	
	//gets words, in order.
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
	
	private void addToVocab(String word){
		//since is set, add(word) returns true if successfully added, false if already there
		if (vocabulary.add(word)){
			uniqueVocab.add(word);
		} else {
			uniqueVocab.remove(word); //not actually unique, because already in set of vocab
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> inputFiles = new ArrayList<String>();
		inputFiles.add("4claremontFile.txt");
		
		FeatureSelector f = new FeatureSelector(inputFiles);
//		f.printFeatures("features", "labels", "types");
	}

}
