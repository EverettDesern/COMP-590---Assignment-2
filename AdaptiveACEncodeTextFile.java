package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import ac.ArithmeticEncoder;
import ac.DifferentialCoding;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;
import io.OutputStreamBitSink;

public class AdaptiveACEncodeTextFile {

	public static void main(String[] args) throws IOException, InsufficientBitsLeftException {
		
		String input_file_name = "/Users/edesern/Downloads/out.dat";
		String output_file_name = "adapt.dat";

		int range_bit_width = 40;

		System.out.println("Encoding text file: " + input_file_name);
		System.out.println("Output file: " + output_file_name);
		System.out.println("Range Register Bit Width: " + range_bit_width);

		int num_symbols = (int) new File(input_file_name).length();

		Integer[] symbols = new Integer[256];
		for (int i = 0; i < 256; i++) {
			symbols[i] = i;
		}


		FreqCountIntegerSymbolModel model = new FreqCountIntegerSymbolModel(symbols);

		Integer[] symbols2 = new Integer[511];
		for (int i = -255; i < 256; i++) {
			symbols2[i+255] = i;
		}

		FreqCountIntegerSymbolModel model2 = new FreqCountIntegerSymbolModel(symbols2);

		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(range_bit_width);

		FileOutputStream fos = new FileOutputStream(output_file_name);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);


		FileInputStream fis = new FileInputStream(input_file_name);

		DifferentialCoding differential = new DifferentialCoding(fis);

		for(int i = 0; i < 300; i++) {
			for(int j = 0; j < 64; j++) {
				for(int p = 0; p < 64; p++) {
					if(j == 0 && p == 0) {
						int next_symbol = differential.newList.get(i)[0][0];
						encoder.encode(next_symbol, model, bit_sink);
						model.addToCount(next_symbol);
					} else {
						int next_symbol = differential.newList.get(i)[j][p];
						encoder.encode(next_symbol, model2, bit_sink);
						model2.addToCount(next_symbol+255);
					}
				}
			}
		}
		fis.close();

		encoder.emitMiddle(bit_sink);
		bit_sink.padToWord();
		fos.close();

		System.out.println("Done");
	}
}
