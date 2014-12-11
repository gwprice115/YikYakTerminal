import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Collections;

public class ResultSorter {

	//SMALL_DOUBLE is used as a minimum-impact addition to keys in a TreeMap to overcome the unique-key constraint
	private static final double SMALL_DOUBLE = 0.0000000000000001;
	
	public static void main(String[] args) {
		BufferedReader reader = null;
		BufferedReader creader = null;
		ArrayList<String> files = new ArrayList<String>();
		files.add("allSchoolResults.txt");
		files.add("claremontResults.txt");
		try {
			for(String filename : files){
				PrintWriter randWriter = new PrintWriter(new FileWriter("randSorted."+filename));
				PrintWriter timeWriter = new PrintWriter(new FileWriter("timeSorted."+filename));
				reader = new BufferedReader(new FileReader(filename));
				TreeMap<Double, String> randSorted = new TreeMap<Double, String>(Collections.reverseOrder());
				TreeMap<Double, String> timeSorted = new TreeMap<Double, String>(Collections.reverseOrder());
				String featureVector = reader.readLine();
				String randData = reader.readLine();
				String timeData = reader.readLine();
				while(featureVector != null && randData != null && timeData != null) {
					System.out.println(featureVector);
					System.out.println(randData);
					System.out.println(timeData);
					double randAcc = Double.parseDouble(randData.split("accuracy: ")[1]);
					while(randSorted.containsKey(randAcc)) {
						randAcc += SMALL_DOUBLE;
					}
					randSorted.put(randAcc,featureVector + "\n" + randData + "\n" + timeData + "\n");
					
					double timeAcc = Double.parseDouble(timeData.split("accuracy: ")[1]);
					while(timeSorted.containsKey(timeAcc)) {
						timeAcc += SMALL_DOUBLE;
					}
					timeSorted.put(timeAcc,featureVector + "\n" + randData + "\n" + timeData + "\n");

					featureVector = reader.readLine();
					randData = reader.readLine();
					timeData = reader.readLine();
				}
				for(Double acc : randSorted.keySet()) {
					String lines = randSorted.get(acc);
					randWriter.print(lines);
				}
				for(Double acc : timeSorted.keySet()) {
					String lines = timeSorted.get(acc);
					timeWriter.print(lines);
				}
				randWriter.close();
				timeWriter.close();
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}


// features 0,0,0,0,0,0,0,
// rand: correct 643	total 2144	accuracy: 0.299906716418	
// time: correct 653	total 2075	accuracy: 0.314698795181	
// features 1,0,0,0,0,0,0,
// rand: correct 1576	total 2144	accuracy: 0.735074626866	
// time: correct 1475	total 2075	accuracy: 0.710843373494