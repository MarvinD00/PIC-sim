
public class Parser {
	
	private String parsedString;
	
	/**
	 * Konstruktor für die Klasse
	 */
	public Parser() {
		this.parsedString = "";
	}
	
	/**
	 * Der übergebene String wird auf 25 Zeichen gekürzt
	 * 
	 * @param stringToParse der String der zu parsen ist
	 */
	public Parser(String stringToParse) {
		parsedString = stringToParse.substring(0, 25);
	}
	
	//get methode für parsedString
	public String getParsedString() {
		return parsedString;
	}
	
	//set methode für stringToParse
	public void setStringToParse(String stringToParse) {
	}
		
}
