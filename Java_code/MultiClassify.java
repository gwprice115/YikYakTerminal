import java.io.*;
import java.util.*;

/**
 * Runs through all possible combinations of unigrams, dayOfWeek, header? numCapLetters. 
 * For each combination, get featureFiles, train a model for claremont and all_schools, and for each model,
 * run time and rand testing sets. Output results in allSchoolResults.txt & claremontResults.txt.
 * @author sarah jundt, shannon lubetich, george price
 *
 */
public class MultiClassify {
	
	/**
	 * get featureFiles, train a model for claremont and all_schools, and for each model,
	 * run time and rand testing sets. Output results in results files.
	 * @param args- none
	 */
	public static void main(String[] args) {
		//unigrams, dayOfWeek, header?, numCapLetters
		int numFeatures = 4;
		PrintWriter writer = null;
		PrintWriter cwriter= null;
		String subsetString;
		try {
			BufferedReader reader;
			writer = new PrintWriter(new FileWriter("allSchoolResults.txt"));
			cwriter = new PrintWriter(new FileWriter("claremontResults.txt"));
			writer.println();
			cwriter.println();
			writer.close();
			cwriter.close();
			for(int i = 0; i < Math.pow(2,numFeatures); i*=2) {
				System.out.println(""+i);
				String[] subSet = new String[numFeatures];
				for(int b = 0; b < numFeatures; b++) {
					subSet[b] = "" + ((i >> b) & 1);
				}
				subsetString = Arrays.toString(subSet);
				FeatureSelector.main(subSet);
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("svm_learn -z c all_schools.features_train.txt ../regressionModels/all_schools.model");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line;
				while((line = reader.readLine()) != null) {
					System.out.print(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("svm_classify all_schools.rand.features_test.txt ../regressionModels/all_schools.model all_schools.rand.output");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					System.out.print(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py all_schools.rand.labels.txt all_schools.rand.output");
				pr.waitFor();
				writer = new PrintWriter(new FileWriter("allSchoolResults.txt", true));

				writer.println("features "+subsetString);
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("rand: ");
				while((line = reader.readLine()) != null) {
					writer.print(line+"\t");
				}
				writer.println();

				pr = rt.exec("svm_classify all_schools.time.features_test.txt ../regressionModels/all_schools.model all_schools.time.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py all_schools.time.labels.txt all_schools.time.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("time: ");
				while((line = reader.readLine()) != null) {
					writer.print(line+"\t");
				}
				writer.println();
				writer.close();
				pr = rt.exec("svm_learn -z c claremont.features_train.txt ../regressionModels/claremont.model");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					System.out.print(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("svm_classify claremont.rand.features_test.txt ../regressionModels/claremont.model claremont.rand.output");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				while((line = reader.readLine()) != null) {
					System.out.print(line+"\t");
				}
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py claremont.rand.labels.txt claremont.rand.output");
				pr.waitFor();
				cwriter = new PrintWriter(new FileWriter("claremontResults.txt", true));
				System.out.println(""+i);
				cwriter.println("features "+subsetString);
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				cwriter.print("rand: ");
				while((line = reader.readLine()) != null) {
					cwriter.print(line+"\t");
				}
				cwriter.println();

				pr = rt.exec("svm_classify claremont.time.features_test.txt ../regressionModels/claremont.model claremont.time.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py claremont.time.labels.txt claremont.time.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				cwriter.print("time: ");
				while((line = reader.readLine()) != null) {
					cwriter.print(line+"\t");
				}
				cwriter.println();
				cwriter.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("hi");
			writer.close();
			cwriter.close();
		}
	}
}