package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * Network Project
 *  Manage the Ping Command
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Ping {
	
	private Date start_time;
	
	private InetSocketAddress origin = null;
	
	private InetAddress ip = null;
	
	private OurMac mac = null;
	
	private static int count = 0;
	
	
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
	
	private static int findAdminPort (File fich, String name) throws IOException {
		LineNumberReader lnr = SyntaxAnalyz.find(fich, "host", name);
		int port = -1;
		String line;
		while (port==-1) {
			line = lnr.readLine();
			port = SyntaxAnalyz.readAdminPort(line);
		}
		return port;
	}


	public static void main(String[] args) {
		// appel : 	ping [-conf file] [-ip address] name mac
		// 				ping [-conf file] [-ip address] name ip
		File f = null;
		Ping p;
		String name;
		
		if (args.length <2)  {
		 	System.err.println("parametres incorects !");
		 	System.out.println("ping [-conf file] [-ip address] name mac");
		 	System.exit(-1)	;
		}
		
		for (int i=0; i<args.length-2; ++i) {
			if (args[i].equalsIgnoreCase("-conf")) {
				f = new File(args[++i]);
			}
		}
		if (f == null)
			f = Main.DEFAULT_CONF_FILE;

		name = args[args.length - 2];
		try {
				Ping pong = new Ping(findOrigin(f, name));
				if (pong == null ) {
					System.err.println("Hote <"+name+"> introuvable dans le fichier "+f.getAbsolutePath());
					System.exit(-1);
				}
			try { // distinguer IP/mac ?
				pong.ip = InetAddress.getByName(args[args.length - 1]);
				System.err.println("Ping pas implemente pour IP...  ");
				System.exit(-1);
			} catch (IOException ioe) {
				pong.mac = new OurMac(args[args.length - 1]);
				Socket ss = new Socket(InetAddress.getByName("localhost"), findAdminPort(f, name));
				BufferedWriter output = new BufferedWriter (new OutputStreamWriter(ss.getOutputStream()));
				output.write("ping "+pong.mac+"\nquit\n");
				output.flush();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
	//		System.err.println("Probleme d'E/S dans le ping...");
			System.exit(-1);
		}
		
	}
	
}
