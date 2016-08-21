/*
 * Textures.java: stores individual character images
 * 
 * Copyright (C) 2014 Jon Hopkins
 * 
 * This file is part of Umoria.
 * 
 * Umoria is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Umoria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Umoria.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonhopkins.moria.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Textures {
	public static final Map<String, BufferedImage> chars = init_chars();
	
	private static Map<String, BufferedImage> init_chars() {
		Map<String, BufferedImage> tmp = new HashMap<String, BufferedImage>();
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File("chars.png"));
		} catch (IOException e) {
			System.err.println("Could not load chars.png");
			return null;
		}
		
		for (int i = 0; i < 120; i++) {
			tmp.put((Character.valueOf((char)(i + 32))).toString(), img.getSubimage((i % 26) * 10, (int)(i / 26) * 19, 10, 19));
		}
		
		return tmp;
	}
	
}
