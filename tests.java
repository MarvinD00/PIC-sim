import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class tests {
	
	@Test
	void testParser() {
		Parser parser = new Parser("123456789012345678901234567890");
		assertEquals("1234567890123456789012345", parser.getParsedString());
	}
	
	@Test
	void testReader() throws IOException {
		Reader fileReader = new Reader("H:\\RA Praktikum\\readertest.txt");
		String[] testArray = {"00001","00002","00003","00004","00005"};
		String[] fileReaderArray = fileReader.getContent();
		String[] newFileReaderArray = new String[5];
		for(int i=0; i<5; i++) {
			newFileReaderArray[i] = fileReaderArray[i].substring(20);
		}
		assertArrayEquals(testArray, newFileReaderArray);
	}
	
	@Test
	void testProgrammSpeicher() {
		ProgrammSpeicher speicher = new ProgrammSpeicher();
		speicher.befehleLaden("H:\\RA Praktikum\\speichertest.txt");
		String[] newSpeicherArray = new String[7];
		for(int i=0; i<7; i++) {
			newSpeicherArray[i] = speicher.getBefehle()[i];
		}
		String[] testArray = {"3011","3930","380D","3C3D","3A20","3E25","2806"};
		assertArrayEquals(testArray,newSpeicherArray);
	}
	
	@Test
	void testDecoder() {
		long wExpected;
		long wActual;
		Decoder decoder = new Decoder();
		decoder.decode("3011");
		wExpected = 17;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); //befehl 1
		decoder.decode("3930");
		wExpected = 16;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); // befehl 2
		decoder.decode("380D");
		wExpected = 29;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); //befehl 3
		decoder.decode("3C3D");
		wExpected = 32;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); //befehl 4
		decoder.decode("3A20");
		wExpected = 0;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); //befehl 5
		decoder.decode("3E25");
		wExpected = 37;
		wActual = decoder.getW();
		assertEquals(wExpected,wActual); //befehl 6
		
	}

}
