/**
 * ImageCompression.java: description
 * <p>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jonhopkins.moria.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;

public class ImageCompression {
	private void compressImage() {
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File("chars.png"));
		} catch (IOException e) {
			System.err.println("Could not load chars.png");
			return;
		}
		
		if (img == null) {
			return;
		}
		
		int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		
		if (pixels.length == 0) {
			return;
		}
		
		Vector<int[]> colorData = new Vector<int[]>();
		Vector<Pattern> patternData = new Vector<Pattern>();
		int[] colors = new int[512];
		int width = img.getWidth();
		int height = img.getHeight();
		int len = pixels.length;
		int colorCount = 1; // number of colors in palette
		int patternCount = 1; // length of current string of same color
		int currentColor = pixels[0];
		//colorData.add(currentColor);
		colors[0] = currentColor;
		
		for (int i = 1; i < len; i++) {
			if (pixels[i] != currentColor) {
				// end of string of same color
				// add it to the list and reset patternCount
				patternData.add(new Pattern(currentColor, patternCount));
				patternCount = 1;
				
				// start new string of same color
				currentColor = pixels[i];
				
				// add the new color to the list if necessary
				if (!colorData.contains(currentColor)) {
					//colorData.add(currentColor);
					colors[colorCount] = currentColor;
					colorCount++;
					if (colorCount >= 512) {
						colorData.add(colors);
						colors = new int[512];
						colorCount = 0;
					}
				}
			} else {
				patternCount++;
			}
		}
		
		//if (b_ptr >= bytes.length) {
		//	bytes = Arrays.copyOf(bytes, bytes.length * 2);
		//}
		//xor_byte ^= c;
		//bytes[b_ptr] = xor_byte;
		//b_ptr++;
	}
	
	private class Pattern {
		public int color;
		public int length;
		
		public Pattern(int c, int l) {
			this.color = c;
			this.length = l;
		}
	}
}