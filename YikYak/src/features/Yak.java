package features;

import java.util.HashMap;

public class Yak {
	public double postTime;
	public School school;
	public int label;
	public int commentNum;
	public String yak;
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
	
	public boolean hasHeader(){
		return header!=null;
	}
	
//	public HashMap<String, Integer> getUnigrams(){
//		
//	}
//	public HashMap<String, Integer> getBigrams(){
//		
//	}

}
