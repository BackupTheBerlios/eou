package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Network Project
 * 
 * Emulates a bridge
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Switch {

	// pas d'IP ???
//	private String ip;
	
	private int admin_port;
	
//	private String mac_address;
	private OurMac mac_address;
	
	private String name;
	
	private int priority;
	
	private ArrayList ports;
	
	private static final int DEFAULT_ADMIN_PORT = 8000;
	
	
	
	/** Build a new Switch */
	public Switch(String switch_name, String mac_add, int adminPort) {
		super();
		this.name = switch_name;
		this.mac_address = new OurMac(mac_add);
		this.admin_port = adminPort;
	}
	
	/** Build a new Switch */
	public Switch(String name, File fich) {
		super();
		LineNumberReader lnr;
		String line;
		try {
			lnr = new LineNumberReader(new FileReader(fich));
			line = lnr.readLine();
			while (SyntaxAnalyz.isComment(line) || line.startsWith("["))
				line = lnr.readLine();
			admin_port = readAdminPort(line);
			line = lnr.readLine();
			while (SyntaxAnalyz.isComment(line))
				line = lnr.readLine();
			mac_address = readMac(line);
			line = lnr.readLine();
				while (SyntaxAnalyz.isComment(line))
					line = lnr.readLine();
			do {
			addPort(line);
			line = lnr.readLine();
			while (SyntaxAnalyz.isComment(line))
				line = lnr.readLine();
			} while (line != null);
			
		} catch (FileNotFoundException e) {
			System.err.println ("Fichier <"+fich.getAbsolutePath()+"> introuvable");
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant la lecture de <"+fich.getAbsolutePath()+"> ");
		}
	}


	/**
	 * Analyse the command line.
	 * 
	 * @param line line of string to analyse.
	 */
	protected boolean analyse(String line) {
		if (SyntaxAnalyz.isComment(line)) {
			StringTokenizer args = new StringTokenizer(line);
			
			if (args.hasMoreElements()) {
				String token = args.nextToken();
				System.out.println ("ACTION");
				System.out.println(token);
				while (args.hasMoreTokens()) 
					System.out.println(args.nextToken());
				return true;
			} 
			else {
			System.out.println("ligne vide ?");
				return false;
			}
		}
		System.out.println ("com");
		return true; //commentaires
	}

	private int readAdminPort (String line) {
		StringTokenizer st = new StringTokenizer(line);
		String tok = null;
		while (st.hasMoreTokens())
			tok = st.nextToken();
		return Integer.parseInt(tok);
	}

	private OurMac readMac(String line) {
		StringTokenizer st = new StringTokenizer(line);
		String tok = null;
		while (st.hasMoreTokens())
			tok = st.nextToken();
		return new OurMac(tok);
	}
	
	private void addPort(String line) {
		StringTokenizer st = new StringTokenizer(line);
		if (st.hasMoreTokens()) {
			String tok = st.nextToken();
			String tmp = tok.substring("port-".length(), tok.length()-1);
			int i = Integer.parseInt(tmp);
			System.err.println (i);
			if (st.hasMoreTokens()) {
				tok = st.nextToken();
				System.err.println (tok);
				OurSocket os = new OurSocket(tok);
				ports.add(i, os);
			}
		}
	}
	
	public void setPort (int num_port, OurSocket value) {
		ports.set(num_port, value);
	}
	
	public OurSocket getPort (int num_port) {
		return (OurSocket)ports.get(num_port);
	}
	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port).toString()+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
		Iterator i = ports.iterator();
		while (i.hasNext()) {
			sb.append((OurSocket)i.next());
		}
		return sb.toString();
	}
	
	// TESTS ...
	public static void main(String[] args) {
		int i;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			File f = new File(args[1]);
			System.out.println(new Switch(args[2], f));
			// structure cree
			// lancer le server d'emulation...
			
		}
		else {
			System.err.println("pas assez d'args");
		}
			
	}
}
