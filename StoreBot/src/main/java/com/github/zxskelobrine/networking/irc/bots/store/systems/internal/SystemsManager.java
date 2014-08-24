package com.github.zxskelobrine.networking.irc.bots.store.systems.internal;

public class SystemsManager {

    private static boolean idiotSystem = true;
    private static boolean riotSystem = true;
    private static boolean lyricSystem = true;
    private static boolean norwaySystem = true;
    private static boolean mailSystem = true;
    private static boolean karmaSystem = true;
    private static boolean insultsSystem = true;
    private static boolean slapSystem = true;
    private static boolean partySystem = true;

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

    public static void enableKarmaSystem() {
	karmaSystem = true;
    }

    public static void enableInultsSystem() {
	insultsSystem = true;
    }

    public static void enableSlapSystem() {
	slapSystem = true;
    }

    public static void enablePartySystem() {
	partySystem = true;
    }

    // ----------------------\\

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

    public static void disableKarmaSystem() {
	karmaSystem = false;
    }

    public static void disableInsultsSystem() {
	insultsSystem = false;
    }

    public static void disableSlapSystem() {
	slapSystem = false;
    }

    public static void disablePartySystem() {
	partySystem = false;
    }

    // ----------------------\\

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

    public static boolean isKarmaSystemEnabled() {
	return karmaSystem;
    }

    public static boolean isInsultsSystemEnabled() {
	return insultsSystem;
    }

    public static boolean isSlapSystemEnabled() {
	return slapSystem;
    }

    public static boolean isPartySystemEnabled() {
	return partySystem;
    }

    // -------------------------------------

    public static void setIdiotSystem(boolean value) {
	idiotSystem = value;
    }

    public static void setRiotSystem(boolean value) {
	riotSystem = value;
    }

    public static void setLyricSystem(boolean value) {
	lyricSystem = value;
    }

    public static void setNorwaySystem(boolean value) {
	norwaySystem = value;
    }

    public static void setMailSystem(boolean value) {
	mailSystem = value;
    }

    public static void setKarmaSystem(boolean value) {
	karmaSystem = value;
    }

    public static void setInsultsSystem(boolean value) {
	insultsSystem = value;
    }

    public static void setSlapSystem(boolean value) {
	slapSystem = value;
    }

    public static void setPartySystem(boolean value) {
	partySystem = value;
    }

}
