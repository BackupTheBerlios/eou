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
			BufferedWriter os = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			
			String str = "";
			os.write("Administration du switch <"+sw.getName()+">...\n\tEntrez votre commande : \n");
			os.flush();
			System.out.println(str);
			while ((str = is.readLine())!=null) {
				System.err.println("admin switch lis : "+str);
				os.write(str.toUpperCase()); //TODO faire traitement...
				os.write("\n");
				os.flush();
				analyse(str);
			}
		} catch (IOException e) {
			System.err.println("erreur d'entree sortie dans l'admin du switch");
		}
	}
	
	private void analyse(String args) {
		StringTokenizer st = new StringTokenizer(args);
		String cmd;
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
			if (cmd.equalsIgnoreCase("info"))
				info(st);
			// else //TODO gerer les autres cmd
			else System.out.println("Commande <"+cmd+"> de config de sitch non reconnue");
		}
	}
	
	private void info (StringTokenizer st) {
		sw.info(st);
	}
	
}
