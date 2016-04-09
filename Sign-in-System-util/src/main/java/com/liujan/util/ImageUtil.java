package com.liujan.util;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("restriction")
public class ImageUtil {
	public static String compressImage(String inputFile, String outputFile) {
		try {
			// 获得源文件
			File file = new File(inputFile);
			//System.out.println(inputDir + inputFileName);
			if (!file.exists()) {
				//throw new Exception("文件不存在");
			}
			Image img = ImageIO.read(file);
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			
			// 判断图片格式是否正确
			if (width == -1) {
				System.out.println(" can't read,retry!" + "<BR>");
				return "no";
			} else {
				int newWidth = width;
				int newHeight = height;
				// 判断是否是等比缩放
				if (width > 1100) {
					double rate = 1000.0 / width;
					newWidth = (int) (width * rate);
					newHeight = (int) (height * rate);
				}
				
				BufferedImage tag = new BufferedImage((int) newWidth,
						(int) newHeight, BufferedImage.TYPE_INT_RGB);

				/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
				tag.getGraphics().drawImage(
						img.getScaledInstance(newWidth, newHeight,
								Image.SCALE_SMOOTH), 0, 0, null);
				File outFile = new File(outputFile);
				ImageIO.write(tag,"jpg", outFile);
//				FileOutputStream out = new FileOutputStream(outputFile);
//				// JPEGImageEncoder可适用于其他图片类型的转换
//				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//				encoder.encode(tag);
//				out.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "ok";
	}
}
