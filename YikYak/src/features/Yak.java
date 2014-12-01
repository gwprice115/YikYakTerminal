package features;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Yak {
	public double postTime;
	public School school;
	public int label;
	public int commentNum;
	public String yak;
	public List<String> yakWords;
	public String header;
	public int postDate, hour;

	public Yak(String yak, School school, int hour,  int label, int commentNum, int postDate, double postTime) {
		this(yak, school, hour, label, commentNum, postDate, postTime,  null);
	}
	
	public Yak(String yak, School school,int hour, int label, int commentNum, int postDate, double postTime,  String header) {
		this.postDate = postDate;
		this.postTime = postTime;
		this.school= school;
		this.label = label;
		this.commentNum = commentNum;
		this.yak = yak;
		this.header = header;
		this.hour = hour;
		yakWords = FeatureSelector.getWords(yak);
	}
	
	public enum School {
		CLAREMONT;
		
		public static School getSchool(String s){
			if (s.toUpperCase().equals("CLAREMONT")){
				return CLAREMONT;
			} else {
				throw new RuntimeException("Invalid schoolname");
			}
		}
	}
	
	public int hasHeader(){
		return header==null? 0: 1;
	}
	
	public int uniqueWords(Set<String> uniqVocab){
		int result = 0;
		for (String word: yakWords){
			if (uniqVocab.contains(word)){
				result++;
			}
		}
		return result;
	}
	
	public HashMap<String, Integer> getUnigrams(){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (String word: yakWords){
			int count = result.get(word)!=null ? result.get(word) : 0;
			result.put(word, count+1);
		}
		return result;
	}
	public HashMap<String, Integer> getBigrams(){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (int i= 0; i < yakWords.size()-1; i++){
			String bigram = yakWords.get(i)+"++"+yakWords.get(i+1);
			int count = result.get(bigram)==null ? 0: result.get(bigram);
			result.put(bigram, count+1);
		}
		return result;
	}
	
	public int numCapitalLetters(){
		int result = 0;
		for (int i = 0; i < yak.length(); i++){
			if (Character.isUpperCase(yak.charAt(i))){
				result+=1;
			}
		}
		return result;
	}

}
