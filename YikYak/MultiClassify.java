import java.io.*;
import java.util.*;

public class MultiClassify extends Thread{
	//unigrams, dayOfWeek, header?, length(words), length(chars), uniqueWords, numCapLetters
	public static final int NUM_FEATURES = 7;
	private int i;
	public MultiClassify(int i){
		this.i = i;
	}
	public void run() {
		System.out.println(""+i);
		String[] subSet = new String[NUM_FEATURES];
		for(int b = 0; b < NUM_FEATURES; b++) {
			subSet[b] = "" + ((i >> b) & 1);
		}
		String subsetString = Arrays.toString(subSet);
		FeatureSelector.main(subSet); //java
		new ClaremontThread(subsetString).start();
		new AllThread(subsetString).run();
	}
	public static void main(String[] args) {
		//Math.pow(2,numFeatures)
		for(int i = 0; i < 1; i++) {
			new MultiClassify(i).start();
		}
	}

	private class ClaremontThread extends Thread{

		String subsetString;
		public ClaremontThread(String subsetString) {
			this.subsetString = subsetString;
		}
		public void run() {
			PrintWriter cwriter=null;
			BufferedReader reader;
			Runtime rt = Runtime.getRuntime();
			String line;
			//print features!!
			try{
				cwriter = new PrintWriter(new FileWriter("claremontResults.txt"));
				cwriter.println("features "+subsetString);
				System.out.println("trying to learn on"+subsetString+"claremont.features_train.txt");
				Process	pr = rt.exec("svm_learn -z c "+subsetString+"claremont.features_train.txt ../regressionModels/"+subsetString+"claremont.model");
				pr.waitFor();
				System.out.println("java done claremont"+subsetString);
				pr = rt.exec("svm_classify "+subsetString+"claremont.rand.features_test.txt ../regressionModels/"+subsetString+"claremont.model "+subsetString+"claremont.rand.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py "+subsetString+"claremont.rand.labels.txt "+subsetString+"claremont.rand.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				cwriter.print("rand: ");
				while((line = reader.readLine()) != null) {
					cwriter.print(line+"\t");
				}
				cwriter.println();

				pr = rt.exec("svm_classify "+subsetString+"claremont.time.features_test.txt ../regressionModels/"+subsetString+"claremont.model "+subsetString+"claremont.time.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py "+subsetString+"claremont.time.labels.txt "+subsetString+"claremont.time.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				cwriter.print("time: ");
				while((line = reader.readLine()) != null) {
					cwriter.print(line+"\t");
				}
				cwriter.println();
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				cwriter.close();
				System.out.println(subsetString+"done");
			}
		}
	}
	private class AllThread extends Thread{
		String subsetString, line;
		public AllThread(String subsetString) {
			this.subsetString = subsetString;
		}
		public void run() {
			BufferedReader reader;
			PrintWriter writer=null;
			Runtime rt = Runtime.getRuntime();
			try{
				//print features!!
				writer = new PrintWriter(new FileWriter("allSchoolResults.txt"));
				writer.println("features "+subsetString);
				System.out.println("trying to learn on"+subsetString+"all_schools.features_train.txt");
				Process pr = rt.exec("svm_learn -z c "+subsetString+"all_schools.features_train.txt ../regressionModels/"+subsetString+"all_schools.model");
				pr.waitFor();

				pr = rt.exec("svm_classify "+subsetString+"all_schools.rand.features_test.txt ../regressionModels/"+subsetString+"all_schools.model "+subsetString+"all_schools.rand.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py "+subsetString+"all_schools.rand.labels.txt "+subsetString+"all_schools.rand.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("rand: ");
				while((line = reader.readLine()) != null) {
					writer.print(line+"\t");
				}
				writer.println();

				pr = rt.exec("svm_classify "+subsetString+"all_schools.time.features_test.txt ../regressionModels/"+subsetString+"all_schools.model "+subsetString+"all_schools.time.output");
				pr.waitFor();
				pr = rt.exec("python ../accuracy.py "+subsetString+"all_schools.time.labels.txt "+subsetString+"all_schools.time.output");
				pr.waitFor();
				reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				writer.print("time: ");
				while((line = reader.readLine()) != null) {
					writer.print(line+"\t");
				}
				writer.println();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				writer.close();
				System.out.println(subsetString+"done");
			}
		}
	}
}