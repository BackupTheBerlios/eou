package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 13 mars 2004 */

import java.io.*;
import java.net.*;

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
			
			String st;
			os.write("Administration du switch "+sw.getName()+"...\n\tEntrez votre texte : \n");
			os.flush();
			while ((st = is.readLine())!=null) {
				System.err.println("je lis : "+st);
				os.write(st.toUpperCase()); //TODO faire traitement...
				os.write("\n");
				os.flush();
			}
		} catch (IOException e) {
			System.err.println("erreur d'entree sortie dans l'admin du switch");
		}
		
	}
	
}
