package fr.umlv.jap.net.eou;

/*
 * Cree le Mar 8, 2004
 * 
 * Maitrise info 2003/2004 - module reseau (java)
 */

import java.io.*;

/**
 * Network Project
 * The main class... used for the first tests
 * Nowadays, it only job is to keep common static values...
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class Main {


	/** the input */
	private static BufferedReader input;
	/** the prompt string */
	private static final String prompt = "net > ";
	/** if the listening should stop */
	public static boolean stop = false;
	/** the Syntaxe analyse */
	private static SyntaxAnalyz sa;
	/** the default configuration file */
	public static final File DEFAULT_CONF_FILE = new File("../network.conf");
//	public static final File DEFAULT_CONF_FILE = new File("network.conf");


	/** Displays the prompt. */
	private static void showPrompt() {
		System.out.print(prompt); // on veut ne pas aller a la ligne !
	}

	/**
	 * Returns the input string.
	 * 
	 * @return the input string.
	 */
	private static String getString() {
		try { 
			return input.readLine();
		} catch (IOException e) {
			System.err.println("erreur de lecture du flot");
			return null;
		}
	}

	/** The function listening to the keyboard. */
	private static void listening() {
		while (!stop) {
			showPrompt();
			String s = getString();
			if (s.equalsIgnoreCase("quit") || s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("sortie") || s.equalsIgnoreCase("q")) {
				System.out.println("tcho");
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("Erreur de fermeture du flot");
				}
				stop();
			} else
				sa.analyse(s);
		}
	}

	/**
	 * Tells the function to stop.
	 */
	private static void stop() {
		stop = true;
	}

	
	public static void main(String[] args) {
		listening();
//		Switch s1 = new Switch("switch1", DEFAULT_CONF_FILE);
//		Switch s2 = new Switch("switch2", DEFAULT_CONF_FILE);
//		byte[] buf = new byte[1024];
//		s1.getPort(3).write(buf);
	}
}
