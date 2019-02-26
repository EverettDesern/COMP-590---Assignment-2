package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import ac.ArithmeticDecoder;
import ac.DifferentialCoding;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class StaticACDecodeTextFile {

	public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
		String input_file_name = "static.dat";
		String output_file_name = "reUncompressedStatic.dat";

		FileInputStream fis = new FileInputStream(input_file_name);

		InputStreamBitSource bit_source = new InputStreamBitSource(fis);

		
		int[] symbol_counts = new int[256];
		int[] symbol_counts2 = new int[511];
		Integer[] symbols = new Integer[256];
		Integer[] symbols2 = new Integer[511];
		
		for (int i=0; i<256; i++) {
			symbol_counts[i] = bit_source.next(32);
			symbols[i] = i;
		}
		
		for (int i=-255; i<256; i++) {
			symbol_counts2[i+255] = bit_source.next(32);
			symbols2[i+255] = i;
		}

		FreqCountIntegerSymbolModel model = 
				new FreqCountIntegerSymbolModel(symbols, symbol_counts);
		FreqCountIntegerSymbolModel model2 = 
				new FreqCountIntegerSymbolModel(symbols2, symbol_counts2);

		int num_symbols = (int) new File(input_file_name).length();
		int range_bit_width = 40;
		ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder<Integer>(range_bit_width);

		
		System.out.println("Uncompressing file: " + input_file_name);
		System.out.println("Output file: " + output_file_name);
		System.out.println("Range Register Bit Width: " + range_bit_width);
		System.out.println("Number of symbols: " + num_symbols);
		
		FileOutputStream fos = new FileOutputStream(output_file_name);
		int count = 0;
		int array[] = new int[1228800];
		for(int i = 0; i < 300; i++) {
			for(int j = 0; j < 64; j++) {
				for(int p = 0; p < 64; p++) {
					if(j == 0 && p == 0) {
						int sym = decoder.decode(model, bit_source);
						array[count] = sym;
						count++;
						fos.write(sym);
					} else {
						int sym = decoder.decode(model2, bit_source);
						int val = array[count-1];
						int finalValue = sym + val;
						array[count] = finalValue;
						count++;
						fos.write(finalValue);
					}
				}
			}
		}

		System.out.println("Done.");
		fos.flush();
		fos.close();
		fis.close();
	}
}
