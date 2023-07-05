
public class Decoder {
	
	private String befehl;
	private int k;
	private int wReg;
	private Ram ram;
	


	public Decoder() {
		befehl = "";
		ram = new Ram();
	}
	
	/**
	 *  Nimmt den übergebenen Befehl, teilt ihn in 2 Teile und übersetzt ihn bzw. führt in aus
	 * @param befehl der befehl der decoded werden soll
	 * @return ERROR CODE -1 = fehler (Befehl nicht erkannt) , 0 = gut
	 */
	public int decode(String befehl) {
		this.befehl = befehl;
		ram.addToRam(2, 1); //add 1 to pcl
		ram.addToRam(1,4); //add 1 to timer
		int befehl_ = Integer.parseInt(befehl, 16);
		int befehl1 = befehl_&0x3F00;
		int ad = befehl_&0x7FF; 
		k = befehl_&0xFF;
		int fReg = befehl_&0x007F;
		int bitAd = befehl_&0x380;
		int dBit = befehl_&0b010000000;
		bitAd = bitAd>>7;
		
		switch(befehl1) {
			case 0x2000 : 
			case 0x2100 : call(ad); break;
			case 0x3000 : movlw(k);  break;             
			case 0x3900 : andlw(k);  break;            
			case 0x3C00 : sublw(k);  break;            
			case 0x3A00 : xorlw(k);  break;             
			case 0x3800 : iorlw(k);  break;            
			case 0x3E00 : addlw(k);  break; 
			case 0x2800 : goto_(ad); break;
			case 0x3400 : retlw(k); break;
			case 0x0000 : uncertainCase(befehl_, fReg); break;
			case 0x0700 : addwf(dBit,fReg); break;
			case 0x0500 : andwf(dBit,fReg); break;
			case 0x0100 : clrf(dBit,fReg); break;
			case 0x0300 : decf(dBit,fReg); break;
			case 0x0A00 : incf(dBit,fReg); break;
			case 0x0F00 : incfsz(dBit,fReg); break;
			case 0x0400 : iorwf(dBit,fReg); break;
			case 0x0600 : xorwf(dBit,fReg); break;
			case 0x0200 : subwf(dBit,fReg); break;
			case 0x0800 : movf(dBit,fReg); break;
			case 0x0900 : comf(dBit,fReg); break;
			case 0x0E00 : swapf(dBit,fReg); break;
			case 0x0B00 : decfsz(dBit,fReg); break;
			case 0x0D00 : rlf(dBit,fReg); break;
			case 0x0C00 : rrf(dBit,fReg); break;
			case 0x1000 :
			case 0x1100 :
			case 0x1200 : 
			case 0x1300 : bcf(bitAd,fReg); break;
			case 0x1400 : 
			case 0x1500 : 
			case 0x1600 : 
			case 0x1700 : bsf(bitAd,fReg); break;
			case 0x1800 :
			case 0x1900 : 
			case 0x1A00 : 
			case 0x1B00 : btfsc(bitAd,fReg); break;
			case 0x1C00 : 
			case 0x1D00 : 
			case 0x1E00 : 
			case 0x1F00 : btfss(bitAd,fReg); break;
			default : return -1;
		}
		return ram.getPCL();
	}

	//------------
	//Befehle start
	//------------
	
	private void uncertainCase(int befehl, int fReg) {
		if(befehl == 0x0008) {
			return_();
		} else if(befehl == 0x0009) {
			retfie();
		} else {
			movwf(fReg);
		}
	}
	
	public void movlw(int k) {
		setW(k);
	}
	
	public void movf(int dBit, int fReg) {
		int temp = ram.getRamContent(fReg);
		if(temp == 0) {
			ram.setZero(true);
		} else {
			ram.setZero(false);
		}
		if(dBit == 0) {
			setW(temp);
		}else {
			ram.setRamContent(fReg, temp);
		}
	}
	
	public void andlw(int k) {
		setW(wReg&k);
	}
	
	public void sublw(int k) {
		setW(k-wReg);
	}
	
	public void subwf(int dBit,int fReg) {
		if(dBit == 0) {
			setW((ram.getRamContent(fReg)-wReg)&0xFF);
		} else {
			ram.setRamContent(fReg,(ram.getRamContent(fReg) - wReg)&0xFF);
		}
	}
	
	public void iorwf(int dBit, int fReg) {
		if(dBit == 0) {
			setW(ram.getRamContent(fReg)|wReg);
		} else {
			ram.setRamContent(fReg ,ram.getRamContent(fReg) | wReg); 
		}
	}
	
	public void xorwf(int dBit, int fReg) {
		if(dBit == 0) {
			setW(ram.getRamContent(fReg)^wReg);
		} else {
			ram.setRamContent(fReg , ram.getRamContent(fReg) ^ wReg);
		}
	}
	
	public void xorlw(int k) {
		setW(wReg^k);
	}
	
	public void iorlw(int k) {
		setW(wReg|k);
	}
	
	public void addlw(int k) {
		setW(wReg+k%256);
	}
	
	public void goto_(int k) {
		ram.setRamContent(2, k); 
		int pcl = k;
		int pclath = ram.getRamContent(10);
		pclath = pclath&0b11000;
		pclath <<= 8;
		ram.setRamContent(2, pcl+pclath);	
	}
	
	public int getProgrammzähler() {
		return ram.getRamContent(2);
	}
	
	public int call(int k) {
		ram.stack = ram.getRamContent(2);
		int pcl = k;
		int pclath = ram.getRamContent(10);
		pclath = pclath&0b11000;
		pclath <<= 8;
		ram.setRamContent(2, pcl+pclath);	
		return 0;
	}
	
	public void  return_() {
		ram.setRamContent(2,  ram.stack);
	}
	
	public void retlw(int k) {
		ram.setRamContent(2, ram.stack);
		setW(k);
	}
	
	public void movwf(int fReg) {
		ram.setRamContent(fReg,wReg);
	}
	
	public void addwf(int dBit, int fReg) {
		if(dBit == 0) {
			setW((ram.getRamContent(fReg)+wReg)%256);
		} else {
			if(fReg != 2) {
				ram.setRamContent(fReg, ram.getRamContent(fReg) + wReg);
			} else {
				int pcl = (ram.getRamContent(2)+wReg)%256;
				int pclath = ram.getRamContent(10);
				pclath <<= 8;
				ram.setRamContent(2, pcl+pclath);			
				}
		}
	}
	
	public void andwf(int dBit, int fReg) {
		if(dBit == 0) {
			setW(ram.getRamContent(fReg)&wReg);
		} else {
			ram.setRamContent(fReg ,ram.getRamContent(fReg)&wReg );
		}	
	}
	
	public void clrf(int dBit, int fReg) {
		if(dBit == 0) {
			setW(0);
		} else {
			ram.setRamContent(fReg,0);
		}	
		ram.setZero(true);
	}
	
	public void clrw() {
		setW(0);
		ram.setZero(true);
	}
	
	public void incf(int dBit, int fReg) {
		if(dBit == 0) {
			setW((ram.getRamContent(fReg)+1)&0xFF);
			
		} else {
			ram.setRamContent(fReg , (ram.getRamContent(fReg)+1)&0xFF);
		}
	}
	
	public void decf(int dBit, int fReg) {
		if(dBit == 0) {
			setW((ram.getRamContent(fReg)-1)&0xFF);
		} else {
			ram.setRamContent(fReg ,(ram.getRamContent(fReg)-1)&0xFF);
		}
	}
	
	public void comf(int dBit, int fReg) {
		if(dBit == 0) {
			setW(0xFF-ram.getRamContent(fReg));
		} else {
			ram.setRamContent(fReg, 0xFF-ram.getRamContent(fReg));
		}	
	}
	
	public void swapf(int dBit, int fReg) {
		int upper = ram.getRamContent(fReg)&0xF0;
		upper = upper>>4;
		int lower = ram.getRamContent(fReg)&0x0F;
		lower = lower<<4;
		if(dBit == 0) {
			setW(upper + lower);
		} else {
			ram.setRamContent(fReg, upper + lower);
		}	
	}
	
	public void decfsz(int dBit, int fReg) {
		int erg = ram.getRamContent(fReg) - 1;
		
		if(dBit == 0) {
			setW(erg);
		} else {
			ram.setRamContent(fReg, erg);
		}	

		if(erg == 0) {
			ram.addToRam(2, 1);
		}
	}
	
	public void incfsz(int dBit, int fReg) {
		int erg = ram.getRamContent(fReg) + 1;
		
		if(dBit == 0) {
			setW((ram.getRamContent(fReg) + 1)) ;
		} else {
			ram.setRamContent(fReg, ram.getRamContent(fReg) + 1);
		}	
		
		if(erg%256 == 0) {
			ram.addToRam(2, 1);
		}
	}
	
	public void rlf(int dBit, int fReg) {
		int temp = 0;
		int carryTemp = 0;
		if((ram.getRamContent(fReg)&0x80) == 0) {
			carryTemp = 0;
		} else {
			carryTemp = 1;
		}
		
		if(ram.getCarry()) {
			temp = ((ram.getRamContent(fReg)<<1)+1)%256;
		} else {
			temp = ((ram.getRamContent(fReg)<<1))%256;
		}
			
		if(dBit == 0) {
			setW(temp);
		} else {
			ram.setRamContent(fReg, temp); 
		}	
		
		ram.setCarry(carryTemp == 1);
	}
	
	public void rrf(int dBit, int fReg) {
		int temp = 0;
		int carryTemp = 0;
		
		if((ram.getRamContent(fReg)&0x01) == 0) {
			carryTemp = 0;
		} else {
			carryTemp = 128;
		}
		
		if(ram.getCarry()) {
			temp = ((ram.getRamContent(fReg)>>1)+128)%256;
		} else {
			temp = ((ram.getRamContent(fReg)>>1))%256;
		}
		
		if(dBit == 0) {
			setW(temp);
		} else {
			ram.setRamContent(fReg, temp); 
		}	
		ram.setCarry(carryTemp == 128);
	}
	
	public void bcf(int bBit, int fReg) {
		int maske = 0;
		for(int i=0; i<8; i++) {
			if(i != bBit) {
				maske += (int)Math.pow(2, i);
			}
		}
		ram.setRamContent(fReg, ram.getRamContent(fReg)&(maske));
	}
	
	public void bsf(int bBit, int fReg) {
		int maske = 0;
		for(int i=0; i<8; i++) {
			if(i == bBit) {
				maske += (int)Math.pow(2, i);
			}
		}
		ram.setRamContent(fReg, ram.getRamContent(fReg)|(maske));
	}
	
	public void btfsc(int bBit,int fReg) {
		int maske = 0;
		for(int i=0; i<8; i++) {
			if(i == bBit) {
				maske += (int)Math.pow(2, i);
			}
		}
		if((ram.getRamContent(fReg)&(maske)) == 0) {
			ram.addToRam(2, 1);
		}
	}
	
	public void btfss(int bBit,int fReg) {
		int maske = 0;
		for(int i=0; i<8; i++) {
			if(i == bBit) {
				maske += (int)Math.pow(2, i);
			}
		}
		if((ram.getRamContent(fReg)&(maske)) != 0) {
			ram.addToRam(2, 1);
		}
	}
	
	public void retfie() {
		ram.setGIE();
		ram.setRamContent(2, ram.stack);
	}
	//------------
	//Befehle ende 
	//------------
	
	public void setW(int w) {
		wReg = w%256;
	}
	public int getStack() {
		return ram.stack;
	}
	
	public int getW() {
		return wReg;
	}
	
	public int getF(int index) {
		return ram.getRamContent(index);
	}
	
	public int getF(int index, int bit) {
		int maske = (int) Math.pow(2, bit);
		if((ram.getRamContent(index)&maske) == maske) {
			return 1;
		} else return 0;
	}
	
	public void setF(int fReg, int content) {
		ram.setRamContent(fReg, content);
	}

	public void addToF(int fReg, int content) {
		ram.addToRam(fReg, content);
	}
	
	public String getBefehl() {
		return befehl;
	}
	
}
