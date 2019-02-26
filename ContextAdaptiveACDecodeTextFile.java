package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import ac.ArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class ContextAdaptiveACDecodeTextFile {

	public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
		String input_file_name = "contextAdapt.dat";
		String output_file_name = "reUncompressedContext.dat";

		FileInputStream fis = new FileInputStream(input_file_name);

		InputStreamBitSource bit_source = new InputStreamBitSource(fis);

		Integer[] symbols = new Integer[256];

		for (int i = 0; i < 256; i++) {
			symbols[i] = i;
		}
		Integer[] symbols2 = new Integer[511];
		for (int i = -255; i < 256; i++) {
			symbols2[i + 255] = i;
		}

		// Create 256 models. Model chosen depends on value of symbol prior to
		// symbol being encoded.

		FreqCountIntegerSymbolModel[] models = new FreqCountIntegerSymbolModel[256];
		FreqCountIntegerSymbolModel[] models2 = new FreqCountIntegerSymbolModel[511];

		for (int i = 0; i < 256; i++) {
			// Create new model with default count of 1 for all symbols
			models[i] = new FreqCountIntegerSymbolModel(symbols);
		}
		for (int i = -255; i < 256; i++) {
			// Create new model with default count of 1 for all symbols
			models2[i + 255] = new FreqCountIntegerSymbolModel(symbols2);
		}

		// Read in number of symbols encoded

		// int num_symbols = bit_source.next(32);

		// Read in range bit width and setup the decoder

		// int range_bit_width = bit_source.next(8);
		int num_symbols = (int) new File(input_file_name).length();
		int range_bit_width = 40;
		ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder<Integer>(range_bit_width);

		// Decode and produce output.

		System.out.println("Uncompressing file: " + input_file_name);
		System.out.println("Output file: " + output_file_name);
		System.out.println("Range Register Bit Width: " + range_bit_width);
		System.out.println("Number of encoded symbols: " + num_symbols);

		FileOutputStream fos = new FileOutputStream(output_file_name);

		// Use model 0 as initial model.
		FreqCountIntegerSymbolModel model = models[0];
		FreqCountIntegerSymbolModel model2 = models2[0];

		int count = 0;
		int array[] = new int[1228800];
		for (int i = 0; i < 300; i++) {
			for (int j = 0; j < 64; j++) {
				for (int p = 0; p < 64; p++) {
					if(j == 0 && p == 0) {
						int sym = decoder.decode(model, bit_source);
						array[count] = sym;
						count++;
						fos.write(sym);
						model.addToCount(sym);
						model = models[sym];
					} else {
						int sym = decoder.decode(model2, bit_source);
						int val = array[count-1];
						int finalValue = sym + val;
						array[count] = finalValue;
						count++;
						fos.write(finalValue);
						model2.addToCount(sym+255);
						model2 = models2[sym+255];
					}
				}
			}
		}

		System.out.println("Done.");
		fos.flush();
		fos.close();
		fis.close();
	}
