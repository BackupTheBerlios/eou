package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

import java.io.IOException;
import java.net.*;

/**
 * Network Project
 *  Manage Our Ports
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class OurPort {
	/** inet socket address of the port */
	protected InetSocketAddress isa;
	/** the parent switch if this is a Switch port */
	protected Switch sw = null;// Switch -> domain d'origine
	/** the parent host if this is a Host port */	
	protected Host ho = null;// Host   -> Link
	/** the listening Multcast Socket */
	protected MulticastSocket ms;
	/** the writing Datagram Socket */
	protected DatagramSocket ds;
	/** the listening thread */
	protected Thread read;
	


	/** Constructor for a switch dependant OurPort. */
	public OurPort(InetSocketAddress isa, Switch s) throws IOException {
		sw = s;
		this.isa = isa;
		ms = new MulticastSocket(isa.getPort());
		ms.joinGroup(isa.getAddress());
		read = new Thread(new Listener(this));
		read.start();
	}
	
	/** Constructor for an host dependant for OurPort. */
	public OurPort(InetSocketAddress isa, Host h) throws IOException {
		ho = h;
		this.isa = isa;
		ms = new MulticastSocket(isa.getPort());
		ms.joinGroup(isa.getAddress());
		read = new Thread(new Listener(this));
		read.start();
	}
	
	
	/** Retrurns the InetSocketAdress */
	public InetSocketAddress getIsa() {
		return isa;
	}
	
	/** Tests wether the the port is switch dependant */
	protected boolean isSwitchPort() {
		return (sw!=null);
	}
	
	/**
	 * Write informations on the port
	 * @param buf the informations to write
	 */
	public void write(byte[] buf) {
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, isa);
			ds = new DatagramSocket();
			ds.send(dp);
			ds.close();
		} catch (IOException e) {
	//		System.err.println("Probleme d'E/S dans listener switch");
		}
	}



	/** Returns a string representation of that port */
	public String toString() {
		return (isa.toString());	
	}

	/** 
	 * Send a tram throught this port
	 * @param msg the trame to send
	 */
	public void send(Trame msg) {
		try {
			ds = new DatagramSocket();
			byte[] buf = msg.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, isa);
			ds.send(dp);
			ds.close();
		} catch (IOException e) {
	//		System.err.println("erreur d'E/S dans l envoi d une trame");
		} 
	}


	/** a threading class to listen to the net */
	class Listener implements Runnable {
		OurPort parent;
		public Listener(OurPort parent) {
			super();
			this.parent = parent;
		}

		public void run() {
			while (!Main.stop) {
				byte[] buf = new byte[1024];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ms.joinGroup(isa.getAddress());
					ms.receive(dp);
					Trame receipt = new Trame(new String(dp.getData()));
					if (!isSwitchPort()) {
							ho.treatTrame(receipt);
					} 
					else {
						sw.propagate(receipt, parent);
					}
				} catch (IOException e) {
		//			System.err.println("Probleme d'E/S dans listener switch");
				}
			}//loop
		}//run
	}//listener


	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		read.destroy();
		super.finalize();
	} // pour eviter les threads dormante ? la fin du prog

}
