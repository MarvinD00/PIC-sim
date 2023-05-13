import java.io.IOException;

public class ProgrammSpeicher {
	
	private String[] befehle;
	private String[] lines;
	
	/**
	 * Konstruktor für die klasse
	 */
	public ProgrammSpeicher() {
		this.befehle = new String[500];
		this.lines = new String[500];
		
		for(int i=0; i<500; i++) {
			befehle[i] = "";
		}
		for(int i=0; i<500; i++) {
			lines[i] = "";
		}
	}
	
	/**
	 * Die Methode wandelt den String der vom reader aus dem LST file gelesen wird in einen integer um
	 * 
	 * @param str der string aus dem LST file
	 * @return integer value der hex zahl die in dem string steht
	 */
	/*
	 * private int string(String str) { String hexString = str.substring(5, 9);
	 * System.out.println(hexString); return Integer.parseInt(hexString, 16); }
	 */
	
	/**
	 * Die Zeilen in denen Befehle stehen werden vom reader gelesen und hier in den befehlsarray gespeichert
	 * @param filePath pfad zum LST file
	 * @return error code
	 */
	
	public int befehleLaden(String filePath){
		try {
			Reader fileReader = new Reader(filePath);
			lines = fileReader.getContent();
			int j = 0;
			for(int i=0; i<lines.length; i++) {
				if(lines[i] == null) {
					break;
				} else if (lines[i].charAt(0) != ' ') {
					befehle[j] = lines[i].substring(5,9);
					j++;
				}
			}
			return 0; // NO ERROR
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fehler beim einlesen");
		}
		return -1; //ERROR
	}
	
	public String getBefehl(int index) {
		return befehle[index];
	}
	
	public String getLine(int index) {
		return lines[index];
	}
	//test
	public String[] getBefehle() {
		return befehle;
	}
}
