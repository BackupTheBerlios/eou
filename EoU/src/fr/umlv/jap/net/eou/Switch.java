package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.util.*;

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
	
//	private int priority;
	
//	private ArrayList ports;
	private Hashtable ports;
//	private LinkedHashMap ports;
	
//	private static final int DEFAULT_ADMIN_PORT = 8000;
	
	private static final int MAX_PORT = 9999;
	
	
	/** Build a new Switch */
	public Switch(String switch_name, String mac_add, int adminPort) {
		super();
		this.name = switch_name;
		this.mac_address = new OurMac(mac_add);
		this.admin_port = adminPort;
	//	ports = new ArrayList(MAX_PORT);
		ports = new Hashtable();
	}
	
	/** Build a new Switch */
	public Switch(String name, File fich) {
		super();
		LineNumberReader lnr;
		String line;
		if ((lnr = SyntaxAnalyz.find(fich, "switch", name))!=null) {
//			lnr.mark(1);
			this.name = name;
			// faire une copie de sauvegarde de "lnr" pour assurer si l'ordre n'est pas le bon ?
			this.admin_port = SyntaxAnalyz.readAdminPort(lnr);
			// verifier si on n'a pas 0 ?
			//TODO recup priorite
			this.mac_address = SyntaxAnalyz.readMac(lnr);
			// verif null ?
//			ports = new ArrayList(MAX_PORT);
			ports = new Hashtable();
			addPorts(lnr);
			
		}
		else
			System.err.println ("Switch <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");

	}

	
	
	private  void addPorts(LineNumberReader lnr) {
		
		String line;
		String token = null;
		int i;
		
		try {
			while ((line = lnr.readLine())!=null)
				if (line.startsWith("port-")) {
					//		i = Integer.parseInt(line.substring("port-".length(), line.indexOf(":")));

					StringTokenizer st = new StringTokenizer(line);
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						String tmp = token.substring("port-".length(), token.length()-1);
						i = Integer.parseInt(tmp);
	//					System.err.println (i);
						if (st.hasMoreTokens()) {
							token = st.nextToken();
	//						System.err.println (token);
							OurSocket os = new OurSocket(token);
							//			ports.add(i, os);
	//						System.out.println("port : "+new Integer(i)+"\t socket : "+os);
							setPort(i, os);
						}
					}
				}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant l\'ajout d\'un port ");
		}
	}
	
	

	/**
	 * Analyse the command line.
	 * 
	 * @param line line of string to analyse.
	 * @deprecated
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
	
	public void setPort (int num_port, OurSocket value) {
//		ports.set(num_port, value);
//		ports.add(num_port, value);
		ports.put(new Integer(num_port), value);
	}
	
	public OurSocket getPort (int num_port) {
//		return (OurSocket)ports.get(num_port);
		return (OurSocket)ports.get(new Integer(num_port));
	}
	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port).toString()+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
//		Iterator i = ports.iterator();
		Iterator i = ports.values().iterator();

		while (i.hasNext()) {
			sb.append((OurSocket)i.next()+"\n");
		} 
		
		return sb.toString();
	}
	
	// TESTS ...
	public static void main(String[] args) {
		int i;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			File f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
			Switch s = new Switch(args[2], f);
			System.out.println(s);
			// structure cree
			// lancer le server d'emulation...
			
		}
		else {
			System.err.println("pas assez d'arguments");
		}
			
	}
}
