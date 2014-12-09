import java.io.*;

public class MultiClassify {
	
	public static void main(String[] args) {
		//unigrams, dayOfWeek, postTime, header?, length(words), length(chars), uniqueWords, numCapLetters
		int numFeatures = 8;
		
		try {
			PrintWriter writer = new PrintWriter(new FileWriter("results.txt"));
			BufferedReader reader;
			for(int i = 0; i < Math.pow(2,numFeatures); i++) {
				writer.print("features ");
				String[] subSet = new String[numFeatures];
				for(int b = 0; b < numFeatures; b++) {
					subSet[b] = "" + ((i >> b) & 1);
					writer.print(subSet[b]+",");
				}
				writer.println();
				// FeatureSelector.main(subSet);
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("svm_learn -z c all_schools.features_train.txt ../regressionModels/all_schools.model");
				
				writer.println("All Schools:");
				pr = rt.exec("svm_classify all_schools.rand.features_test.txt ../regressionModels/all_schools.model all_schools.rand.output");
				pr = rt.exec("../accuracy.py all_schools.rand.labels.txt all_schools.rand.output");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("rand: ");
				while((line = reader.readLine()) != null) {
					writer.print(line);
				}
				writer.println();

				pr = rt.exec("svm_classify all_schools.time.features_test.txt ../regressionModels/all_schools.model all_schools.time.output");
				pr = rt.exec("../accuracy.py all_schools.time.labels.txt all_schools.time.output");
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("time: ");
				while((line = reader.readLine()) != null) {
					writer.print(line);
				}
			}
			writer.close();
		}
		catch(IOException e) {
			System.out.println("you fucked up");
		}
	}
}