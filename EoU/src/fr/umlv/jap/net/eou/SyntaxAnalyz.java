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
 * Analyse Some string - syntax
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class SyntaxAnalyz {
	
	
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
	
	
	/**
	 * Tests wether the line is a Commentary or not
	 * @param line the line to analise
	 * @return true if the line is a commentary
	 */
	public static boolean isComment(String line) {
		return line.startsWith("#");
	}
	
	
	/**
	 * Find an element in a file
	 * @param fich the configuration file 
	 * @param elt the type element to look for (switch/host)
	 * @param name the name of the element
	 * @return
	 */
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
	
	
	/**
	 * Returns the value of the administration pour of the element
	 * @param line the line in with the information is located
	 * @return the value of the TCP port
	 */
	public static int readAdminPort (String line) {
		String token = null;
		try {
			if (line.startsWith("admin-port:")) {
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens())
					token = st.nextToken();
				return Integer.parseInt(token);
			}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		}
		return 0;
	}
	
	
	/**
	 * Returns the priority of the element
	 * @param line the line in with the information is located
	 * @return the priority
	 */
	public static int readPriority (String line) {
		String token = null;
		try {
			if (line.startsWith("priority:")) {
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens())
					token = st.nextToken();
				return Integer.parseInt(token);
			}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		}
		return 0;
	}
	
	
	/**
	 * Returns the mac-address of the element 
	 * @param line the line in with the information is located
	 * @return the mac-address
	 */
	public static OurMac readMac(String line) {
		String token = null;
		if (line.startsWith("MAC-address:")) {
			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens())
				token = st.nextToken();
			return new OurMac(token);
		}
		return null;
	}
	

	/**
	 * Returns the Ip address of the element (for an host)
	 * @param line the line in with the information is located
	 * @return  Ip address
	 */
	public static InetAddress readIp(String line) {
		String token = null;
		try {
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
	

	/**
	 * Returns the value of the Link (for an host)
	 * @param line the line in with the information is located
	 * @return the link
	 */
	public static InetSocketAddress readLink(String line) {
		String token = null;
		try {
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
	
	
	/**
	 * Parses a string "<code>192.168.0.10:1234</code>" into an InetSocketAdress
	 * @param sock the String representation
	 * @return the InetSocketAddress
	 * @throws UnknownHostException
	 */
	public static InetSocketAddress parseISA(String sock) throws UnknownHostException {
		StringTokenizer st = new StringTokenizer(sock, ":");
		//TODO ajouter verif ...
		int port /*= DEFAULT_PORT*/;
		InetAddress ip = InetAddress.getByName(st.nextToken());
		try {
			port = Integer.parseInt(st.nextToken());
			return new InetSocketAddress(ip, port);
		} catch (NumberFormatException nfe) {
			System.err.println("Probleme de format... entier attendu");
			return null;
		}
	}

	
}
