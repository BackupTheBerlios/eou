package fr.umlv.jap.net.eou;

import java.io.IOException;
import java.net.*;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Sniff {

	private InetSocketAddress sock;
	
	/** Default constructor */
	public Sniff(String str) {
		super();
		try {
			this.sock = SyntaxAnalyz.parseISA(str);
			new Thread() {
				public void run() {	
					while (!Main.stop)
						survey(); //TODO	
				}
			}.start();
		} catch (UnknownHostException e) {
		System.err.println("Hote inconnu pour le sniff");
		}
	}
	
		protected void survey() /*throws IOException*/ {
			//TODO une boucle ?
			byte[] buf = new byte[1024];
			try {
//				DatagramSocket dgs = new DatagramSocket(); // num de port choisi par java
				MulticastSocket ms = new MulticastSocket(); // num de port choisi par java
				DatagramPacket dgp = new DatagramPacket(buf, 0, sock);
	//			dgs.send(dgp);
				dgp.setLength(1024);
				ms.receive(dgp);
				System.out.println("sniff lis : "+new String(dgp.getData(), 0, dgp.getLength()));
				//TODO preciser affichage + vers le term...
			} catch (IOException ioe) {
				System.err.println("Probleme d'E/S pour le sniff");
			}
			
		}
	
	public String toString() {
		return ("renifle : <"+sock+">");
	}
	
	public static void main(String[] args) {
		if (args.length>0) {
			Sniff s = new Sniff(args[0]);
			System.out.println(s);
		}
		else {
			System.err.println("pas assez d'arguments");
		}
	}
	
	
}
