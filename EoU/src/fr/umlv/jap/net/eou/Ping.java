package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.File;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Ping {

	/** Default constructor */
	public Ping() {
		super();
		
	}

	public static void main(String[] args) {
		File f;
		Ping p;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
	//		f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
	//		p = new Ping(args[2], f);
	//		System.out.println(p);
			// structure cree
			// lancer le server d'emulation...
			
		}
		else {
			if (args.length>0) {
				System.out.println("-> default");
				f = new File("network.conf");
	//			p = new Ping(args[0], f);
			}
			else 
				System.err.println("pas assez d'arguments ");
			
		}
			
	}
}
