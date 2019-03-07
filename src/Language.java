import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Language {
public static void main(String[] args) throws IOException {
/*
		
		File english = new File("english.txt");
		File italian = new File("italian.txt");
		FileReader englishFile = new FileReader(english);
		FileReader italianFile = new FileReader(italian);
		BufferedReader englishReader = new BufferedReader(englishFile);
		BufferedReader italianReader = new BufferedReader(italianFile);
		StringBuffer englishBuffer = new StringBuffer();
		StringBuffer italianBuffer = new StringBuffer();
		
		String line;
		

		while ((line = englishReader.readLine()) != null) {
			englishBuffer.append(line);
			englishBuffer.append("\n");
		}
		englishReader.close();
		while ((line = italianReader.readLine()) != null) {
			italianBuffer.append(line);
			italianBuffer.append("\n");
		}
		italianReader.close();
		System.out.println("Contents of file:");
		System.out.println(italianBuffer.toString());
		System.out.println(englishBuffer.toString());
		
		String[] englishWords = englishBuffer.toString().split("\n");
		String[] italianWords = italianBuffer.toString().split("\n");
		
		double[][] englishNums = new double[englishWords.length][15];
		double[][] italianNums = new double[italianWords.length][15];
		
		Parse parse = new Parse();
		
		for(int i = 0; i < englishWords.length; i++) {
			englishNums[i] = parse.parse(englishWords[i],15);
		}
		for(int i = 0; i < italianWords.length; i++) {
			italianNums[i] = parse.parse(italianWords[i],15);
		}
		double[][] combinedDictionary = new double[englishNums.length + italianNums.length][15];
		double[][] langResult = new double[englishNums.length + italianNums.length][output];
		for(int i = 0; i < combinedDictionary.length; i++) {
			if (i < englishNums.length) {
				combinedDictionary[i] = englishNums[i];
				langResult[i][0] = 1;
				langResult[i][1] = 0;
			}else {
				combinedDictionary[i] = italianNums[i-englishNums.length];
				langResult[i][0] = 0;
				langResult[i][1] = 1;
			}
		}
	}
	*/
}
}
