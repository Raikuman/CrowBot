package com.reliquary.crow.resources.other;

import java.awt.Color;
import java.util.Random;

/**
 * This class handles getting a random color
 *
 * @version 1.0.1
 * @since 2021-19-11
 */
public class RandomColor {

	private final static Random random = new Random();

	/**
	 * This method randomly generates a color object with rgb values
	 * @return Returns a color object
	 */
	public static Color getRandomColor() {
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();

		return new Color(r, g, b);
	}
}