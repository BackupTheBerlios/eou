package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Ping {

	private Date start_time;
	
	private InetSocketAddress origin = null;
	
	private InetAddress ip = null;
	
	private OurMac mac = null;
	
	
	/** Default constructor */
	public Ping(InetSocketAddress src) {
		super();
		origin = src;	
	}
	
	public Ping(String str) {
		super();
		try {
			ip = InetAddress.getByName(str);
		} catch (IOException ioe) {
				mac = new OurMac(str);
		}	
	}
	
	private static InetSocketAddress findOrigin (File fich, String name) throws IOException {
		LineNumberReader lnr = SyntaxAnalyz.find(fich, "host", name);
		InetSocketAddress isa = null;
		String line;
		while (isa==null) {
			line = lnr.readLine();
			isa = SyntaxAnalyz.readLink(line);
		}
		return isa;
	}
	
	
	private void doPing(OurMac dest_mac, OurMac src_mac) throws IOException {
			System.out.println("on ping ?");
			Trame msg = new Trame(	dest_mac,
															src_mac,
															Trame.TYPE_PING,
															Trame.OPCODE_REQUEST,
															"<<pong>>");
			//	sw.getPort(num_port).write(msg.getBytes());
			byte[] buf = msg.getBytes();
			System.out.println("avant dgs origin : "+origin);
			DatagramPacket dp = new DatagramPacket(buf, buf.length, this.origin);
	//		DatagramSocket ds = new DatagramSocket(this.origin);
			DatagramSocket ds = new DatagramSocket();
			ds.send(dp);
			System.out.println("apres dgs");
			this.start_time = new Date();
			listenPingReception(msg);
	}
	
	private void listenPingReception(Trame sent) {
		MulticastSocket ms;
		try {
			System.out.println("inetsocket adresse d'origine : "+origin);
			ms = new MulticastSocket(origin);
			System.out.println("ms d'origine : "+ms);
			// socket de reception
			byte[] buf = new byte[1024];
	//		DatagramPacket dp = new DatagramPacket();
	System.out.println("hey avt?");
			DatagramPacket dp = new DatagramPacket(buf, buf.length, ms.getInetAddress(), ms.getPort());
			System.out.println("hey ap?");

			boolean isReceived = false;
			while (!isReceived && !Main.stop) {
			System.out.println("boucle !! attend "+sent.getTrame()+" sur "+ms+"\n ad : "+ms.getInetAddress()+" port "+ms.getPort());
				ms.receive(dp);
				System.out.println("reception ? "+dp);
				// verif C pouyr moi + answer ...
				Trame rcp = new Trame(new String(dp.getData()));
				if (rcp.isPingAnswer(sent))
					isReceived=true;
			}
			System.out.println(
				"Duree ecoulee : "
					+ new Long(new Date().getTime() - start_time.getTime())
						.toString()
					+ " ms");

		} catch (IOException e) {
			System.err.println("Erreur d'E/S pendant l\'attente de la reponse au ping");
		}
	}

	
	public static void main(String[] args) {
		// appel : ping [-conf file] [-ip address] name mac
		// 				ping [-conf file] [-ip address] name ip
		File f = null;
		Ping p;
		String name;
//		InetAddress ip = null;
//		OurMac mac = null;

//		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
	//		f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
	//		p = new Ping(args[2], f);
	//		System.out.println(p);
			// structure cree
			// lancer le server d'emulation...
			
//		}
//		else {
//			if (args.length>0) {
//				System.out.println("-> default");
//				f = Main.DEFAULT_CONF_FILE;
//	//			p = new Ping(args[0], f);
//			}
//			else 
//				System.err.println("pas assez d'arguments ");	
//		}
		
		if (args.length <2)  {
		 	System.err.println("parametres incorects !");
		 	System.out.println("ping [-conf file] [-ip address] name mac");
		 	System.exit(-1)	;
		}
		
		for (int i=0; i<args.length-2; ++i) {
			if (args[i].equalsIgnoreCase("-conf")) {
				f = new File(args[++i]);
			}
	//		else if (args[i].equalsIgnoreCase("-ip")) {
	//			f = new File(args[++i]);
	//		}
		}
		if (f == null)
			f = Main.DEFAULT_CONF_FILE;

		name = args[args.length - 2];
		try {
				System.out.println("hey ? " + f.getAbsolutePath());
				Ping pong = new Ping(findOrigin(f, name));
				if (pong == null ) {
					System.err.println("Hote <"+name+"> introuvable dans le fichier "+f.getAbsolutePath());
					System.exit(-1);
				}
			try { // distinguer IP/mac ?
				pong.ip = InetAddress.getByName(args[args.length - 1]);
				System.out.println("on a pris ip  "+pong.ip);
			} catch (IOException ioe) {
				pong.mac = new OurMac(args[args.length - 1]);
				System.out.println("on a pris mac  "+pong.mac);
			} //TODO verif ce qu'on a..
		 pong.doPing(new OurMac("dep"), new OurMac("arr"));
		} catch (IOException e) {
			System.err.println("Probleme d'E/S dans le ping...");
			System.exit(-1);
		}
	}
	
}
