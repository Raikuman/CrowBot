package com.reliquary.crow.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextLineLoader {

	/*
readFileToArray
Reads a file by line and adds it to an array
 */
	public static List<String> readFileToArray(File file) {

		// Write config file to ArrayList
		List<String> configArray = new ArrayList<>();

		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null)
				configArray.add(line);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return configArray;
	}

}
