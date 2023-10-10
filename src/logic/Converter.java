package logic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import logic.MarchingSquares.VecPathArea;

public class Converter {
	
	public static void converter(BufferedImage source) {
		
		int w = source.getWidth(); // 32n
		int h = source.getHeight(); // 32n
		
		HashMap<Integer, boolean[][]> colors = new HashMap<Integer, boolean[][]>();

		Integer[][] colorsMap = new Integer[w*2+2][h*2+2];
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int rgb = source.getRGB(x, y);
//				colorsMap[x*2][y*2] = rgb;

				for (int cy = -1; cy < 2; cy++) {
					for (int cx = 0; cx < 2; cx++) {
						if(x+cx >= w || y+cy >= h) continue;
						if(x+cx < 0 || y+cy < 0) continue;
						if(rgb == source.getRGB(x+cx, y+cy)) {
							colorsMap[x*2 + cx + 2][y*2 + cy + 2] = rgb;
						}
					}
				}
				
				boolean[][] map = colors.get(rgb);
				if(map == null) {
					map = new boolean[w+1][h+1];
					colors.put(rgb, map);
				}
				map[x+1][y+1] = true;
			}
		}

		StringBuilder svg = new StringBuilder();
		/// width="1320px" height="1320px" 
		svg.append(
				"""
				<svg id="svg" viewBox="-1 -1 @ @" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
				""".replaceFirst("@", (w*2+4)+"").replaceFirst("@", (h*2+4) + ""));
		
		if(colors.entrySet().size() > 10) {
			System.err.println("To many colors, skipping");
			return;
		}
		
		ArrayList<VecPathArea> paths = new ArrayList<>();
		
		colors.entrySet().forEach(e -> {
			MarchingSquares.color = new Color(e.getKey());
//			System.out.println()
//			;
			paths.addAll(new MarchingSquares(e.getValue()).colorsMap(colorsMap).create(e.getKey()).getSvgPaths(e.getKey()));
//			toSvgGroup(svg);
		});
		
		
		paths.sort(new Comparator<VecPathArea>() {

			@Override
			public int compare(VecPathArea p1, VecPathArea p2) {
				return p2.boundsArea() - p1.boundsArea();
			}
		});
		
		for (VecPathArea p : paths) {
			svg.append(p.svg());
		}
		svg.append("</svg>");
		
//		System.out.println(svg.toString());
		
		
		try {
			File f = new File("svg");
			f.mkdirs();
//			System.out.println(f.getAbsolutePath());
			Files.write(Paths.get(f.getAbsolutePath() + "/" + MarchingSquares.save + ".svg"), svg.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
//		colors.entrySet().forEach(e -> {
//			for (int y = 0; y < h; y++) {
//				for (int x = 0; x < w; x++) {
//					System.out.print(e.getValue()[x][y] ? "\u2593" : " ");
//					System.out.print(e.getValue()[x][y] ? "\u2593" : " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		});
		
		
		
	}
}