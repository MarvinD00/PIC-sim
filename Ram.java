
public class Ram {
	private int[] ram;
	public int stack;
	
	public Ram() {
		ram = new int[1024];
		for(int i=0; i<=1023; i++) {
			ram[i] = 0;
		}
		setRamContent(133,31);
		setRamContent(134,255);
	}
	
	//helping functions to set and get zero and carry bits from 03h register
	public void setCarry(boolean carry) {
		if(carry) {
			ram[3] = ram[3] | 0b01;
		} else {
			ram[3] = ram[3] & 0xFE;
		}
	}
	
	public boolean getCarry() {
		if((ram[3]&0b01) == 1 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getDigitCarry() {
		return (ram[3]&0b10) == 1;
	}
	public void setZero(boolean carry) {
		if(carry) {
			ram[3] = ram[3] | 0b0100;
		} else {
			ram[3] = ram[3] & 0xFB;
		}
	}
	
	public boolean getZero() {
		if((ram[3]&0b0100) == 1 ) {
			return true;
		} else {
			return false;
		}
	}
	
	//function for the decoder to access RAM Content
	public int getRamContent(int fReg) {
		if(fReg != 0) {
			return ram[fReg];
		} else {
			int FSRad = ram[4];
			return ram[FSRad];
		}
	}
	
	//helping functions to get PC and PCLATH
	public int getPCLATH() {
		return ram[10];
	}
	
	public int getPCL() {
		return ram[2];
	}
	
	//set content of RAM
	public void setRamContent(int fReg, int content) {
		if(ram[3] == 32 && noSFR(fReg)) {
			fReg += 128;
		}
		if(fReg == 0) {
			int FSRad = ram[4];
			ram[FSRad] = content%256;
		} else if(fReg == 2) {
			ram[fReg] = content;
		} else{
			ram[fReg] = content%256;
		}
		
		if(content>255) {
			System.out.println("Fehler bei setRamContent > 255");
		}
		if(checkForInterrupt(fReg, content)) {
			interrupt();
		}
	}
	
	private boolean noSFR(int fReg) {
		return fReg != 0 && fReg != 2 && fReg != 3 && fReg != 4 && fReg != 10 && fReg != 11;
	}

	//function to check for interrupts
	public boolean checkForInterrupt(int fReg, int content) {
		if(fReg == 6 && getGIE()) {
			if(RB0(content)&&RB0isInput() && getINTE()) { //RB0
				setINTF();
				return true;
			}
			if(RB4(content)&&RB4isInput()&&getRBIE()) { //RB4
				setRBIF();
				return true;
			}
			if(RB5(content)&&RB5isInput()&&getRBIE()) { //RB5
				setRBIF();
				return true;
			}
			if(RB6(content)&&RB6isInput()&&getRBIE()) { //RB6
				setRBIF();
				return true;
			}
			if(RB7(content)&&RB7isInput()&&getRBIE()) { //RB7
				setRBIF();
				return true;
			}
		}
		return false;
	}

	//--------------------------
	//PORTB & TRISB (RB0 +RB4-7)
	//---------------------------
	
	public boolean RB0(int content) {
		return (content&0b00000001) == 1;
	}
	
	public boolean RB4(int content) {
		return (content&0b00010000) == 16;
	}
	
	public boolean RB5(int content) {
		return (content&0b00100000) == 32;
	}
	
	public boolean RB6(int content) {
		return (content&0b01000000) == 64;
	}
	
	public boolean RB7(int content) {
		return (content&0b10000000) == 128;
	}
	
	public boolean RA0isInput() {
		return (getRamContent(133)&0b00000001) == 1;
	}
	
	public boolean RA1isInput() {
		return (getRamContent(133)&0b00000010) == 2;
	}
	
	public boolean RA2isInput() {
		return (getRamContent(133)&0b00000100) == 4;
	}
	
	public boolean RA3isInput() {
		return (getRamContent(133)&0b00001000) == 8;
	}
	
	public boolean RA4isInput() {
		return (getRamContent(133)&0b00000001) == 16;
	}
	
	public boolean RB0isInput() {
		return (getRamContent(134)&0b00000001) == 1;
	}
	
	public boolean RB1isInput() {
		return (getRamContent(134)&0b00000010) == 2;
	}
	
	public boolean RB2isInput() {
		return (getRamContent(134)&0b00000100) == 4;
	}
	
	public boolean RB3isInput() {
		return (getRamContent(134)&0b00001000) == 8;
	}
	
	public boolean RB4isInput() {
		return (getRamContent(134)&0b00010000) == 16;
	}
	
	public boolean RB5isInput() {
		return (getRamContent(134)&0b00100000) == 32;
	}
	
	public boolean RB6isInput() {
		return (getRamContent(134)&0b01000000) == 64;
	}
	
	public boolean RB7isInput() {
		return (getRamContent(134)&0b10000000) == 128;
	}
	
	//----------------
	//intcon register
	//----------------
	
	public void setRBIF() {
		ram[11] += 1;
	}
	
	public boolean getRBIF() {
		return (ram[11]&0b0000001) == 0b0000001;
	}
	
	public void setINTF() {
		ram[11] += 2;
	}
	
	public boolean getINTF() {
		return (ram[11]&0b0000010) == 0b0000010;
	}
	
	public void setTOIF() {
		ram[11] += 4;
	}
	
	public boolean getTOIF() {
		return (ram[11]&0b0000100) == 0b0000100;
	}
	
	public void setRBIE() {
		ram[11] += 8;
	}
	
	public boolean getRBIE() {
		return (ram[11]&0b0001000) == 0b0001000;
	}
	
	public void setINTE() {
		ram[11] += 16;
	}
	
	public boolean getINTE() {
		return (ram[11]&0b0010000) == 0b0010000;
	}
	
	public void setTOIE() {
		ram[11] += 32;
	}
	
	public boolean getTOIE() {
		return (ram[11]&0b0100000) == 0b0100000;
	}
	
	public void setGIE() {
		ram[11] += 128;
	}
	
	public boolean getGIE() {
		return (ram[11]&0b10000000) == 0b10000000;
	}
	
	//Function to simplify adding to RAM
	public void addToRam(int fReg,int num) {

		if(fReg == 5 || fReg == 6) {
			if(checkIfPortIsInput(fReg, num)) {
				setRamContent(fReg, (ram[fReg]+num)%256);
			}
		} else {
			setRamContent(fReg, (ram[fReg]+num)%256);
		}
		if(fReg == 1) {
			if (ram[fReg]+num >= 256 && getGIE() && getTOIE()) {
				ram[fReg] = 0;
				setTOIF();
				interrupt();
			}
		} 
	}
	
	public boolean checkIfPortIsInput(int fReg, int num) {
		
		num = Math.abs(num);
		
		if(num == 1 && fReg == 5) {
			return RA0isInput();
		} else if (num == 2 && fReg == 5) {
			return RA1isInput();
		} else if (num == 4 && fReg == 5) {
			return RA2isInput();
		} else if (num == 8 && fReg == 5) {
			return RA3isInput();
		} else if (num == 16 && fReg == 5) {
			return RA4isInput();
		} else if (num == 1 && fReg == 6) {
			return RB0isInput();
		} else if (num == 2 && fReg == 6) {			
			return RB1isInput();
		} else if (num == 4 && fReg == 6) {			
			return RB2isInput();
		} else if (num == 8 && fReg == 6) {			
			return RB3isInput();
		} else if (num == 16 && fReg == 6) {			
			return RB4isInput();
		} else if (num == 32 && fReg == 6) {			
			return RB5isInput();
		} else if (num == 64 && fReg == 6) {			
			return RB6isInput();
		} else if (num == 128 && fReg == 6) {		
			return RB7isInput();
		} 
		return false;
	}
	
	public void interrupt() {
		stack = ram[2];
		ram[2] = 4;
		ram[11] -= 128;
	}

}
