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
	
//	protected Thread write;
	
	protected boolean isHost;
	
	
	/**
	 * Constructor for OurPort.
	 */
	public OurPort(InetSocketAddress isa) throws IOException {
		super();
		System.out.println("## ISA : "+isa);
		this.isa = isa;
//		ms = new MulticastSocket(isa);
		ms = new MulticastSocket(isa.getPort());
		ms.joinGroup(isa.getAddress());
		System.out.println("ourPort cree une ms : "+ms.getInetAddress());
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener(this));
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
		System.out.println("## this.ISA : "+this.isa);
		System.out.println("## this.ISA port: "+this.isa.getPort());
	//	ms = new MulticastSocket(isa);
		ms = new MulticastSocket(isa.getPort());
		System.out.println("ourPort sur switch cree une ms sur port: "+ms.getPort());
		ms.joinGroup(isa.getAddress());
		System.out.println("ourPort sur switch cree une ms adress : "+ms.getInetAddress());
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener(this));
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
		System.out.println("## this.ISA : "+this.isa);
//		ms = new MulticastSocket(isa);
		ms = new MulticastSocket(isa.getPort());
		System.out.println("ourPort sur host cree une ms : "+ms.getInetAddress());
		ms.joinGroup(isa.getAddress());
//		ds = new DatagramSocket(); // il s'adapte tout seul...
		read = new Thread(new Listener(this));
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

	public void send(Trame msg) {
		try {
			ds = new DatagramSocket();
			byte[] buf = msg.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, isa);
			ds.send(dp);
			ds.close();
		} catch (IOException e) {
			System.err.println("erreur d'E/S dans l envoi d une trame");
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
	//				System.out.println("ecoute sur "+ms.getInetAddress()+" port : "+ms.getPort());
					ms.receive(dp);
					Trame receipt = new Trame(new String(dp.getData()));
					System.out.println("reception (port) : "+receipt.getTrame());
					if (!isSwitchPort()) {
							ho.treatTrame(receipt);
					} 
					else {
						System.out.println("chui le switch, je propage, ?a vient de "+this);
						//traitementSwitch(receipt);
						sw.propagate(receipt, parent);
			//			System.out.println("traitement pour switch "+sw.getName());
					//TODO traitement ?
					}
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

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		read.destroy();
		super.finalize();
	}

}
