package fr.umlv.jap.net.eou;

/*
 * Cree le Mar 8, 2004
 * 
 * Maitrise info 2003/2004 - module reseau (java)
 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Network Project 
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class SyntaxAnalyz {
	
	private static int DEFAULT_PORT = 1234;

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
	
	public static LineNumberReader find(File fich, String elt, String name) {
		LineNumberReader lnr;
		String line;
		try {
			lnr = new LineNumberReader(new FileReader(fich));
			while ((line = lnr.readLine())!=null) {
				if (line.startsWith("["+elt) && line.endsWith(name+"]"))
					return lnr;
			}
		} catch (FileNotFoundException e) {
			System.err.println ("Fichier <"+fich.getAbsolutePath()+"> introuvable");
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture de <"+fich.getAbsolutePath()+"> ");
		}
		return null;
	}
	
	public static int readAdminPort (String line) {
//		String line;
		String token = null;
		try {
//			while ((line = lnr.readLine())!=null)
			if (line.startsWith("admin-port:")) {
				StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens())
				token = st.nextToken();
			return Integer.parseInt(token);
			}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		}/* catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture du port d'administration");
		}*/
		return 0;
	}
	
	public static int readPriority (String line) {
//		String line;
		String token = null;
		try {
//			while ((line = lnr.readLine())!=null)
			if (line.startsWith("priority:")) {
				StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens())
				token = st.nextToken();
			return Integer.parseInt(token);
			}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		}/* catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture du port d'administration");
		}*/
		return 0;
	}
	
	public static OurMac readMac(String line) {
//		String line;
		String token = null;
//		try {
//			while ((line = lnr.readLine())!=null)
				if (line.startsWith("MAC-address:")) {
					
					StringTokenizer st = new StringTokenizer(line);
					while (st.hasMoreTokens())
						token = st.nextToken();
					return new OurMac(token);
					
				}
//		} catch (IOException e) {
//			System.err.println ("Erreur d'entree sortie pendant la lecture de la mac-adresse");
//		}
		return null;
	}
	

	
	public static InetAddress readIp(String line) {
//		String line;
		String token = null;
		try {
//			while ((line = lnr.readLine())!=null)
				if (line.startsWith("IP-address:")) {
					
					StringTokenizer st = new StringTokenizer(line);
					while (st.hasMoreTokens())
						token = st.nextToken();
					return InetAddress.getByName(token);
					
				}
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture de l\'adresse IP");
		}
		return null;
	}
	

	
	public static InetSocketAddress readLink(String line) {
//		String line;
		String token = null;
		try {
//			while ((line = lnr.readLine())!=null)
				if (line.startsWith("link:")) {
					
					StringTokenizer st = new StringTokenizer(line);
					while (st.hasMoreTokens())
						token = st.nextToken();
					return SyntaxAnalyz.parseISA(token);
					
				}
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture de l\'adresse IP");
		}
		return null;
	}
	
	
	public static InetSocketAddress parseISA(String sock) throws UnknownHostException {
		//	super();
		StringTokenizer st = new StringTokenizer(sock, ":");
		//TODO ajouter verif ...
		//	this.ip = new OurIp(st.nextToken());
		int port = DEFAULT_PORT;
		InetAddress ip = InetAddress.getByName(st.nextToken());
		try {
			port = Integer.parseInt(st.nextToken());
		} catch (NumberFormatException nfe) {
			System.err.println("Probleme de format... entier attendu");
		}
		return new InetSocketAddress(ip, port);
	}

	
}
