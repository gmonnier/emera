package testextractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Programm {


	public static void reduceFile() {
		File input = new File("C:\\Gilles\\DiversProjects\\Data\\2ndAnalysis\\geckoL150123_S1_L001_R1_001.fastq");
		File output = new File("C:\\Gilles\\DiversProjects\\Data\\2ndAnalysis\\geckoL150123_S1_L001_R1_001.fastq_Partial");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(input));
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));

			String line;
			int counter = 0;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
				counter++;
				if(counter == (1 << 20)) {
					break;
				}
			}
			reader.close();
			writer.close();
		} catch (Throwable e) {
			System.err.println("error");
		} 
	}
}
