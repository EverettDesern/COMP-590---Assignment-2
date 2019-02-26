package ac;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class DifferentialCoding {

	public ArrayList<int[][]> list = new ArrayList<int[][]>();
	public ArrayList<int[][]> newList = new ArrayList<int[][]>();
	public int[] symbolCounts = new int[256];
	public int[] symbolCounts2 = new int[511];

	public DifferentialCoding(FileInputStream source) throws InsufficientBitsLeftException, IOException {

		for (int i = 0; i < 300; i++) { // seconds of video * frames/second
			int[][] frames = new int[64][64];
			int[][] newFrames = new int[64][64];
			for (int p = 0; p < 64; p++) { // 64 pixels per row
				for (int q = 0; q < 64; q++) { // 64 pixels per column
					int value = source.read();
					if (p == 0 && q == 0) {
						frames[p][q] = value;
						newFrames[p][q] = value;
						symbolCounts[value]++;
					} else {
						if (q != 0) {
							int value2 = frames[p][q - 1];
							int finalValue = value - value2;
							newFrames[p][q] = finalValue;
							symbolCounts2[finalValue+255]++;
						} else {
							if (p != 0) {
								int value2 = frames[p - 1][63];
								int finalValue = value - value2;
								newFrames[p][q] = finalValue;
								symbolCounts2[finalValue+255]++;
							}
						}
						frames[p][q] = value;
					}
				}

			}
			list.add(frames);
			newList.add(newFrames);
		}

	}
