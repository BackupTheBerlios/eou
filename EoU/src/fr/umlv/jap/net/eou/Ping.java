package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Ping {

	Date start_time;
	
	/** Default constructor */
	public Ping() {
		super();
		
	}
	
	private InetAddress findOriginIp (File fich, String name) throws IOException {
		LineNumberReader lnr = SyntaxAnalyz.find(fich, "host", name);
		InetAddress ia = null;
		String line;
		while (ia==null) {
			line = lnr.readLine();
			ia = SyntaxAnalyz.readIp(line);
		}
		return ia;
	}
	
	
	private void adminPing(StringTokenizer st) throws IOException {
		if (st.hasMoreTokens()) {
			System.out.println("on ping ?");
			int num_port = Integer.parseInt(st.nextToken());
			OurMac dest_mac = new OurMac(st.nextToken());
			OurMac origin_mac = new OurMac("depart");
			Trame msg =
			new Trame(
					dest_mac,
					origin_mac,
					Trame.TYPE_PING,
					Trame.OPCODE_REQUEST,
					"<<pong>>");
			//	sw.getPort(num_port).write(msg.getBytes());
			
		} else
			System.out.println("Ping sans args ?");
	}
	

	
	public static void main(String[] args) {
		File f = null;
		Ping p;
		String name;
		InetAddress ip;
		OurMac mac;
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
		
		for (int i=0; i<args.length-2; ++i) {
			if (args[i].equalsIgnoreCase("-conf")) {
				f = new File(args[++i]);
			}
			else if (args[i].equalsIgnoreCase("-ip")) {
	//			f = new File(args[++i]);
			}
		}
		if (f == null)
			f=Main.DEFAULT_CONF_FILE;
		
		
	}
	
	
}
