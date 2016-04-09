package com.liujan.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class NetUtil {
	private static String os = "";
	private static String arpCMD = "";
	
	static {
		os = System.getProperty("os.name");
		if (os.indexOf("Windows") >= 0 || os.indexOf("windows") >= 0) {
			arpCMD = "arp -a ";
		}
		else {
			arpCMD = "arp -n ";
		}
	}
	
	public static String getMacAddress(String ip) {
		String macAdd = "";
		
		try {
			String string;
			Process process = Runtime.getRuntime().exec(arpCMD + ip);
			InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
			LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader);
			for (int i = 0; i < 100; i++) {
				string = lineNumberReader.readLine();
				if (string != null) {
					if (string.indexOf(ip) > 1) {
						if (string.indexOf(":") >= 0)
							macAdd = string.substring(string.indexOf(":") - 2, string.indexOf(":") + 15);
						else if (string.indexOf("-") >= 0)
							macAdd = string.substring(string.indexOf("-") - 2, string.indexOf("-") + 15);
						return macAdd;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}
		
		return macAdd;
	}
}
