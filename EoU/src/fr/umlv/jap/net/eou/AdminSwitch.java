package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 13 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class AdminSwitch implements Runnable {

	private Socket sock;
	
//	private String name;
	private Switch sw;
	
	private BufferedWriter output;
	
	/** default constructor */
	public AdminSwitch(Switch sw, Socket s) {
		super();
		this.sock = s;
		this.sw = sw;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// la connection est etablie on recuper les flots;
		try {
			BufferedReader is = new BufferedReader (new InputStreamReader(sock.getInputStream()));
//			BufferedWriter os = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			output = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			
			String str = "";
			output.write("Administration du switch <"+sw.getName()+">...\n\tEntrez votre commande : \n");
			output.flush();
			System.out.println(str);
			while ((str = is.readLine())!=null) {
				System.err.println("admin switch lis : "+str);
//				output.write(str.toUpperCase()); //TODO faire traitement...
//				output.write("\n");
//				output.flush();
				analyse(str);
			}
		} catch (IOException e) {
			System.err.println("erreur d'entree sortie dans l'admin du switch");
		}
	}
	
	private void analyse(String args) throws IOException {
		StringTokenizer st = new StringTokenizer(args);
		String cmd;
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
//			System.out.println(cmd);
			if (cmd.equalsIgnoreCase("info"))
				adminInfo(st);
			else if (cmd.equalsIgnoreCase("priority")) {
				adminPriority(st);
			}
			else if (cmd.equalsIgnoreCase("port")) 
				adminPort(st);
			else if (cmd.equalsIgnoreCase("quit")) 
				sock.close();
			
			
			// else //TODO gerer les autres cmd
//			else System.out.println("Commande <"+cmd+"> de config de sitch non reconnue");
			else {
				output.write("Commande <"+cmd+"> de config de switch non reconnue\n");
				output.flush();
			}
		}
	}
	
	private void adminInfo (StringTokenizer st) throws IOException {
		sw.info(st, output);
	}
	
	private void adminPriority(StringTokenizer st) throws IOException {
		if (st.hasMoreTokens()) {
			sw.setPriority(Integer.parseInt(st.nextToken()));
			output.write("Nouvelle priorite du switch <"+sw.getName()+"> : "+ new Integer(sw.getPriority())+"\n");
			output.flush();
		}
		else 
//		System.out.println("Priorite du switch <"+sw.getName()+"> : "+ new Integer(sw.getPriority()));
		output.write("Priorite du switch <"+sw.getName()+"> : "+ new Integer(sw.getPriority()));
		output.flush();
	}
	
	private void adminPort(StringTokenizer st) {
		String str = "";
		try {
			if (st.hasMoreTokens()) {
				str = st.nextToken();
				int i = Integer.parseInt(str);
				if (st.hasMoreTokens()) {
					str = st.nextToken();
					if (str.equalsIgnoreCase("down")) {
						sw.setPort(i, null);
					}
					else /*if (str.startsWith("["))*/ {
						sw.setPort(i, new OurSocket(str));
					}
				}
				else 
					System.err.println("arguments insuffisants dans la config du port");
			}
			else 
				System.err.println("arguments insuffisants dans la config du port");
	
		} catch (NumberFormatException nfe) {
			System.err.println("valeur <"+Integer.parseInt(str)+"> incorrecte, entier attendu");
		}
	}
	
}
