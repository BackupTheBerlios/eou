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
	
	private Thread job;
	
	/** Default constructor */
	public Sniff(String str) {
		super();
		try {
			this.sock = SyntaxAnalyz.parseISA(str);
			(job = new Thread() {
				public void run() {	
					while (!Main.stop)
						survey(); 
				}
			}).start();
		} catch (UnknownHostException e) {
			System.err.println("Hote inconnu pour le sniff");
		}
	}
	
		protected void survey() {
			byte[] buf = new byte[1024];
			try {
				MulticastSocket ms = new MulticastSocket(sock.getPort());
				ms.joinGroup(sock.getAddress()); 
				DatagramPacket dgp = new DatagramPacket(buf, 0);
				dgp.setLength(1024);
				ms.receive(dgp);
				System.out.println(new Trame(new String(dgp.getData(), 0, dgp.getLength())));
			} catch (IOException ioe) {
//				System.err.println("Probleme d'E/S pour le sniff");
			}
		}
		
		
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		job.destroy();
		super.finalize();
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
