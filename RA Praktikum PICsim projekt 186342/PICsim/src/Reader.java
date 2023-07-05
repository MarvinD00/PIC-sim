import java.io.*;


public class Reader {
	
	private File file;
	private String[] fileContent;
	private String[] fileContentUnparsed;
	
	/**
	 *  Konstruktor für die Klasse Reader, liest zeilenweise das file ein und speichert die zeilen als strings
	 *  im Array fileContent
	 *  
	 * @param filePath Pfad zur Datei die eingelesen werden soll
	 * @throws IOException 
	 */
	public Reader(String filePath) throws IOException {

		file = new File(filePath);
		fileContent = new String[499];
		fileContentUnparsed = new String[499];
		int i = 0;
		if(file.isFile())
		{
			// Initial setup
			FileInputStream inputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);			
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	
			while(bufferedReader.ready())
			{
				String readString = bufferedReader.readLine();
				Parser parser = new Parser(readString);
				fileContent[i] = parser.getParsedString();
				fileContentUnparsed[i] = readString;
				i++;
			}
			bufferedReader.close();
		}
		
	}
	
	//get Methode für fileContent
	public String[] getContent() {
		return fileContent;
	}
	
	public String getLine(int i) {
		return fileContentUnparsed[i];
	}
}
