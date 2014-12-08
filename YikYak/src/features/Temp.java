package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import features.Yak.School;

public class Temp {
	public Temp(String inputFile){
		try {
			LinkedHashMap<Integer, Integer> aboveNum = new LinkedHashMap<Integer, Integer>();
			//get vocab & store yaks
			BufferedReader reader;
			String sentence, maybeHeader, yakText, likeLine, comments, header;
			HashMap<String,Integer> map = new HashMap<String, Integer>();
			int likes, postDate, commentNum;
			double postTime; 
			PrintWriter p = new PrintWriter(new FileWriter("topWords.txt"));
			Pattern headerPattern = Pattern.compile(".*###\\s(.*)\\s###"); //group(1)== header
			Pattern likeLinePattern = Pattern.compile("\\s*(-?\\d+)\\slikes.*");
//			"\\s(-?\\d+)\\slikes\\s*|\\s*Posted\\s*(\\d[4]-\\d[2]-\\d[2])\\s*(\\d[2]:\\d[2]:\\d[2]).*");
			//group(1)==number of likes. group(2)==date posted. group(3)==time posted.
			Pattern commentPattern = Pattern.compile("\\s*Comments:\\s*(\\d+)"); //group(1)==commentNum
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
						reader.readLine();
//						p.println(likes+"\t"+FeatureSelector.getWords(yakText));
						for (String yak: FeatureSelector.getWords(yakText)){
							int count= map.get(yak)!=null? map.get(yak): 0;
							map.put(yak, count+1);
//							if (likes<13){
//								map.put(yak, count-2);
//							} 
//								else if (likes <13){
//								map.put(yak,  count-1);
//							}
//							else if (likes<25){
//								map.put(yak, count+1);
//							} else if (likes >25){
//								map.put(yak,  count+2);
//							} 
//							else {
//								map.put(yak, count+2);
//							}
							
						}
						
					} else {
						reader.close();
						throw new RuntimeException("Number of lines of a yikyak isn't what was expected");
					}
					for (int i=0; i<map.size()/100; i++){
						int count = aboveNum.get(i)!=null? aboveNum.get(i): 0;
						if (likes>i){
							aboveNum.put(i, count+1);
						}
					}
				}
				reader.close();
				TreeMap<Double, String> sorted = new TreeMap<Double, String>(Collections.reverseOrder());
				for (String k: map.keySet()){
					Double thisDouble = map.get(k)+0.0;
					while (sorted.containsKey(thisDouble)){
						thisDouble = thisDouble+0.00000000001;
					}
					sorted.put(thisDouble, k);
				}
				for (int i = 0; i <100; i++){
					Entry<Double, String> next = sorted.pollFirstEntry();
					sorted.remove(next.getKey());
					System.out.println(next.getValue());
					
				}
//				System.out.println(aboveNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Temp("claremontFile.train");
	}
}
