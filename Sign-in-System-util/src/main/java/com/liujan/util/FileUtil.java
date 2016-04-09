package com.liujan.util;

import java.io.File;

public class FileUtil {
	public static boolean deleteDir(File dir) {
		if (!dir.exists()) {
			return false;
		}
		if (dir.isDirectory()) {
			String[] files = dir.list();
			for (int i = 0; i < files.length; i++) {
				deleteDir(new File(dir, files[i]));
			}
		}
		return dir.delete();
	}
	public static boolean fileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
}
