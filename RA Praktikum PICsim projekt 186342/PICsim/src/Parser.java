
public class Parser {
	
	private String parsedString;
	
	/**
	 * Konstruktor f�r die Klasse
	 */
	public Parser() {
		this.parsedString = "";
	}
	
	/**
	 * Der �bergebene String wird auf 25 Zeichen gek�rzt
	 * 
	 * @param stringToParse der String der zu parsen ist
	 */
	public Parser(String stringToParse) {
		parsedString = stringToParse.substring(0, 25);
	}
	
	//get methode f�r parsedString
	public String getParsedString() {
		return parsedString;
	}
	
	//set methode f�r stringToParse
	public void setStringToParse(String stringToParse) {
	}
		
}
