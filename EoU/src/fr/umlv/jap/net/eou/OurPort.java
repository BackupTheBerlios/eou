package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

import java.io.IOException;
import java.net.*;

/**
 * Network Project
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class OurPort {

	protected InetSocketAddress isa;
	// Switch -> domain d'origine
	// Host   -> Link
	
	protected Switch sw = null;
	
	protected Host ho = null;
	
	protected MulticastSocket ms;

	protected DatagramSocket ds;
	
	protected Thread read;
	
	protected Thread write;
	
	protected boolean isHost;
	
	
	/**
	 * Constructor for OurPort.
	 */
	public OurPort(InetSocketAddress isa) throws IOException {
		super();
		System.out.println("## ISA : "+isa);
		this.isa = isa;
		ms = new MulticastSocket(isa);
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener());
		read.start();
//		write = new Thread(new Talker());
//		write.start();
	}
	
		/**
	 * Constructor for OurPort.
	 */
	public OurPort(InetSocketAddress isa, Switch s) throws IOException {
//		new OurPort(isa);
		sw = s;
		System.out.println("## ISA : "+isa);
		this.isa = isa;
		ms = new MulticastSocket(isa);
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener());
		read.start();
	}
	
		/**
	 * Constructor for OurPort.
	 */
	public OurPort(InetSocketAddress isa, Host h) throws IOException {
	//	new OurPort(isa);
		ho = h;
		System.out.println("## ISA : "+isa);
		this.isa = isa;
		ms = new MulticastSocket(isa);
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener());
		read.start();
	}
	
	
	public InetSocketAddress getIsa() {
		return isa;
	}
	
	protected boolean isSwitchPort() {
		return (sw!=null);
	}
	
	public void write(byte[] buf) {
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, isa);
			ds = new DatagramSocket();
			ds.send(dp);
			ds.close();
			System.out.println("on veut ecrire dans : "+isa);
			System.out.println("parti : "+new String(dp.getData()));
			//TODO traitement ?
		} catch (IOException e) {
			System.err.println("Probleme d'E/S dans listener switch");
		}
	}

	
	public void traitementHost(Trame msg) {
		System.out.println("reception <"+msg+"> recue par host "+ho);
	}
	

	public void traitementSwitch(Trame msg) {
		System.out.println("reception <"+msg+"> recue par switch "+sw);
	}
	

	public String toString() {
		return ("OurPort : "+isa.toString());	
	}


	/** a threading class to listen to the net */
	class Listener implements Runnable {

		public Listener() {
			super();
		}

		public void run() {
			while (!Main.stop) {
				byte[] buf = new byte[1024];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ms.joinGroup(isa.getAddress());
					ms.receive(dp);
					System.out.println("reception : "+new String(dp.getData()));
					Trame receipt = new Trame(new String(dp.getData()));
					if (!isSwitchPort())
						traitementHost(receipt); 
					else
						traitementSwitch(receipt);
					//TODO traitement ?
				} catch (IOException e) {
					System.err.println("Probleme d'E/S dans listener switch");
				}
			}//loop
		}//run
	}//listener

	//inutile ?
	/** a threading class to listen to the net */
	class Talker implements Runnable {

		public Talker() {
			super();
		}

		public void run() {
			while (!Main.stop) {
				byte[] buf = new byte[1024];
				try {
						DatagramPacket dp = new DatagramPacket(buf, buf.length, isa);
						ds = new DatagramSocket();
						ds.send(dp);
						ds.close();
					//TODO traitement ?
				} catch (IOException e) {
					System.err.println("Probleme d'E/S dans talker switch");
				}
			}//loop
		}//run
	}//talker

}
