package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Sniff {

	private OurSocket sock;
	
	/** Default constructor */
	public Sniff(String str) {
		super();
		this.sock = new OurSocket(str);
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
