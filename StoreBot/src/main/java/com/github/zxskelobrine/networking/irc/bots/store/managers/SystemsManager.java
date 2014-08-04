package com.github.zxskelobrine.networking.irc.bots.store.managers;

public class SystemsManager {

	private static boolean idiotSystem = true;
	private static boolean riotSystem = true;
	private static boolean lyricSystem = true;
	private static boolean norwaySystem = true;
	private static boolean mailSystem = true;

	public static void enableIdiotSystem() {
		idiotSystem = true;
	}

	public static void enableRiotSystem() {
		riotSystem = true;
	}

	public static void enableLyricSystem() {
		lyricSystem = true;
	}

	public static void enableNorwaySystem() {
		norwaySystem = true;
	}

	public static void enableMailSystem() {
		mailSystem = true;
	}

	//----------------------\\

	public static void disableIdiotSystem() {
		idiotSystem = false;
	}

	public static void disableRiotSystem() {
		riotSystem = false;
	}

	public static void disableLyricSystem() {
		lyricSystem = false;
	}

	public static void disableNorwaySystem() {
		norwaySystem = false;
	}

	public static void disableMailSystem() {
		mailSystem = false;
	}

	//----------------------\\

	public static boolean isIdiotSystemEnabled() {
		return idiotSystem;
	}

	public static boolean isRiotSystemEnabled() {
		return riotSystem;
	}

	public static boolean isLyricSystemEnabled() {
		return lyricSystem;
	}

	public static boolean isNorwaySystemEnabled() {
		return norwaySystem;
	}

	public static boolean isMailSystemEnabled() {
		return mailSystem;
	}

}
