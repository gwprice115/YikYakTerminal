import YikYak.*;

public class MultiClassify {
	
	public static void main(String[] args) {
		//unigrams, dayOfWeek, postTime, header?, length(words), length(chars), uniqueWords, numCapLetters
		int numFeatures = 8;
		for(int i = 0; i < Math.pow(2,numFeatures); i++) {
			int[] subSet = new int[numFeatures];
			for(int b = 0; b < numFeatures; b++) {
				subSet[b] = (i >> b) & 1;
				System.out.print(subSet[b]+",");
				main(args);
			}
			System.out.println();
		}
	}







}