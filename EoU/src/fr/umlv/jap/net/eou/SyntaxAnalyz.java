package fr.umlv.jap.net.eou;

/*
 * Cree le Mar 8, 2004
 * 
 * Maitrise info 2003/2004 - module reseau (java)
 */

import java.util.StringTokenizer;

/**
 * Network Project 
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class SyntaxAnalyz {
	

	/** Name of the commands */
/*	private static final String[] funcName =
		{
			"swith",
			"ping" };

	private static final Command[] functions =
		{
			new Switch(),
			new Ping() };
*/
	
	/**
	 * Conctructs a new SyntaxAnalyse.
	 */
	public SyntaxAnalyz() {
		super();
	}

	/**
	 * Analyse the command line.
	 * 
	 * @param line line of string to analyse.
	 */
	public void analyse(String line) {
		if (isComment(line)) {
			StringTokenizer args = new StringTokenizer(line);
			
			if (args.hasMoreElements()) {
				String token = args.nextToken();
					System.out.println ("ACTION");
					System.out.println(token);
					while (args.hasMoreTokens()) 
						System.out.println(args.nextToken());
			} 
			else {
				System.out.println("ligne vide ?");
			}
		}
		else 
			System.out.println ("-< commentaire >-");
	}
	

	public static boolean isComment(String line) {
		return line.startsWith("#");
	}
	

}
