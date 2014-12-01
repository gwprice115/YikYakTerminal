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
	protected  List<String> vocabulary;
	protected List<Yak> yaks;
	protected Set<String> uniqueVocab;

	public FeatureSelector(List<String> inputFiles) {
		try {
			//get vocab & store yaks
			vocabulary = new ArrayList<String>();
			yaks = new ArrayList<Yak>();
			uniqueVocab = new HashSet<String>();
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header;
			int likes, postDate, commentNum;
			double postTime;
			Pattern headerPattern = Pattern.compile(".*###\\s(.*)\\s###"); //group(1)== header
			Pattern likeLinePattern = Pattern.compile("\\s*(-?\\d+)\\slikes.*");
//			"\\s(-?\\d+)\\slikes\\s*|\\s*Posted\\s*(\\d[4]-\\d[2]-\\d[2])\\s*(\\d[2]:\\d[2]:\\d[2]).*");
			//group(1)==number of likes. group(2)==date posted. group(3)==time posted.
			Pattern commentPattern = Pattern.compile("\\s*Comments:\\s*(\\d+)"); //group(1)==commentNum
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
						yakText = reader.readLine();
						//add words to vocab
						String[] words = yakText.toLowerCase().split("\\s");
						for (String word: words){
							for (String subWord: getWords(word)){
								addToVocab(subWord);
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
						yaks.add(new Yak(yakText, school, hour, likes, commentNum, 0, 0, header));
					} else {
						reader.close();
						throw new RuntimeException("Number of lines of a yikyak isn't what was expected");
					}
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printFeatures(String outputFeatures, String outputLabels, String features){
		try{
			PrintWriter typeW = new PrintWriter(new FileWriter(features)); //types of features we're using
			PrintWriter featureW = new PrintWriter(new FileWriter(outputFeatures)); //features of cases
			PrintWriter labelW = new PrintWriter(new FileWriter(outputLabels)); //labels of cases
			for (Yak yak: yaks){
				//TODO: currently working on this.
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		
		//header?
		//length (words)
		//length (characters)
		//unigrams (including UNIQUE_count)
		//bigrams

	}
	
	private List<String> getWords(String word){
		List<String> result = new ArrayList<String>();
		while (word.matches(".+[.,!?\"'&/)#-:]")){
			result.add(word.substring(word.length()-1));
			word = word.substring(0, word.length()-1);
		} while (word.matches("[.,!?\"'&/(#:-].+")){
			result.add(word.substring(0,1));
			word = word.substring(1, word.length());
		}
		result.add(word);
		return result;
	}
	
	private void addToVocab(String word){
		if (!vocabulary.contains(word)){
			vocabulary.add(word);
			uniqueVocab.add(word);
		} else if (uniqueVocab.contains(word)) {
			uniqueVocab.remove(word); //not actually unique
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> inputFiles = new ArrayList<String>();
		inputFiles.add("4claremontFile.txt");
		new FeatureSelector(inputFiles);

	}

}
