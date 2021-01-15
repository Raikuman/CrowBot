package com.reliquary.crow.resources.RandomClasses;

	import java.awt.Color;
	import java.util.Random;

public class RandomColor {
	private final static Random random = new Random();

	public static Color getRandomColor() {
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();

		return new Color(r, g, b);
	}
}