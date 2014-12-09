import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


public class ResultSorter {
	
	public static void main(String[] args) {
		PrintWriter writer = null;
		PrintWriter cwriter= null;
		BufferedReader reader = null;
		BufferedReader creader = null;
		ArrayList<String> files = new ArrayList<String>();
		files.add("allSchoolResults.txt");
		files.add("claremontResults.txt");
		try {
			for(String filename : files){
				writer = new PrintWriter(new FileWriter("sorted."+filename));
				reader = new BufferedReader(new FileReader(filename));
				String featureVector = reader.readLine();
				
			}
			
		
			
			
			
			
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
