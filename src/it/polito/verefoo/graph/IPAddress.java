package it.polito.verefoo.graph;

public class IPAddress {
	String firstByte;
	String secondByte;
	String thirdByte;
	String fourthByte;
	boolean neg;
	
	public IPAddress(IPAddress toCopy) {
		super();
		this.firstByte = toCopy.getFirstByte();
		this.secondByte = toCopy.getSecondByte();
		this.thirdByte = toCopy.getThirdByte();
		this.fourthByte = toCopy.getFourthByte();
		this.neg = toCopy.isNeg();
	}
	
	public IPAddress(IPAddress toCopy, boolean neg) {
		super();
		this.firstByte = toCopy.getFirstByte();
		this.secondByte = toCopy.getSecondByte();
		this.thirdByte = toCopy.getThirdByte();
		this.fourthByte = toCopy.getFourthByte();
		this.neg = neg;
	}
	
	
	public IPAddress(String fistByte, String secondByte, String thirdByte, String fourthByte, boolean neg) {
		super();
		this.firstByte = fistByte;
		this.secondByte = secondByte;
		this.thirdByte = thirdByte;
		this.fourthByte = fourthByte;
		this.neg = neg;
	}
	
	public IPAddress(String ip, boolean neg) {
		if(ip.equals("*")) {
			this.firstByte = "-1";
			this.secondByte = "-1";
			this.thirdByte = "-1";
			this.fourthByte = "-1";
		} else {
			String fields[] = ip.split("\\.");
			this.firstByte = fields[0];
			this.secondByte = fields[1];
			this.thirdByte = fields[2];
			this.fourthByte = fields[3];
		}
		this.neg = neg;	
	}

	public String getFirstByte() {
		return firstByte;
	}
	public void setFirstByte(String fistByte) {
		this.firstByte = fistByte;
	}
	public String getSecondByte() {
		return secondByte;
	}
	public void setSecondByte(String secondByte) {
		this.secondByte = secondByte;
	}
	public String getThirdByte() {
		return thirdByte;
	}
	public void setThirdByte(String thirdByte) {
		this.thirdByte = thirdByte;
	}
	public String getFourthByte() {
		return fourthByte;
	}
	public void setFourthByte(String fourthByte) {
		this.fourthByte = fourthByte;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}

	/* function that checks if "this" is included in ip (NOTE: ip should use wildcards)
	   NOTE: here not considering if neg or not */
	public boolean isIncludedIn(IPAddress ip) {
		if((firstByte.equals(ip.getFirstByte()) || ip.getFirstByte().equals("-1")) 
				&& (secondByte.equals(ip.getSecondByte()) || ip.getSecondByte().equals("-1"))
				&& (thirdByte.equals(ip.getThirdByte()) || ip.getThirdByte().equals("-1"))
				&& (fourthByte.equals(ip.getFourthByte()) || ip.getFourthByte().equals("-1")))
			return true;
		return false;
	}
	
	
	public boolean equalsStar() {
		if(firstByte.equals("-1") && secondByte.equals("-1") && thirdByte.equals("-1") && fourthByte.equals("-1"))
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		if(firstByte.equals("-1") && secondByte.equals("-1") && thirdByte.equals("-1") && fourthByte.equals("-1"))
			return "*";
		return firstByte+"."+secondByte+"."+thirdByte+"."+fourthByte;
	}

	@Override
	public boolean equals(Object obj) {
		IPAddress ip = (IPAddress) obj;
		if(firstByte.equals(ip.getFirstByte()) && secondByte.equals(ip.getSecondByte()) &&
				thirdByte.equals(ip.getThirdByte()) && fourthByte.equals(ip.getFourthByte())
				&& neg == ip.isNeg())
			return true;
		return false;
	}
	
	//fuction that checks if "this" is equal to ip, but without considering the neg attribute
	public boolean equalFileds(IPAddress ip) {
		if(firstByte.equals(ip.getFirstByte()) && secondByte.equals(ip.getSecondByte()) &&
				thirdByte.equals(ip.getThirdByte()) && fourthByte.equals(ip.getFourthByte()))
			return true;
		return false;
	}
	
	//This method is used to understand if the IPAddress has wildcards. It returns:
		//1 if it has wildcards in byte 1,2,3,4 es *.*.*.*
		//2 if it has wildcards in byte 2,3,4 es 10.*.*.*
		//3 if it has wildcards in byte 3,4 es 10.0.*.*
		//4 if it has wildcards in byte 4 es 10.0.0.*
		//5 if it has no wildcards
		public int hasWildcardsInByte() {
			if(firstByte.equals("-1"))
				return 1;
			else if(secondByte.equals("-1"))
				return 2;
			else if(thirdByte.equals("-1"))
				return 3;
			else if(fourthByte.equals("-1"))
				return 4;
			else return 5;	
		}

		public int getByteNumber(int byteNumber) {
			if(byteNumber == 1)
				return Integer.valueOf(firstByte);
			else if(byteNumber == 2)
				return Integer.valueOf(secondByte);
			else if(byteNumber == 3)
				return Integer.valueOf(thirdByte);
			else
				return Integer.valueOf(fourthByte);
		}
		
		public void setByteNumberWithValue(int byteNumber, int value) {
			if(byteNumber == 1)
				firstByte = String.valueOf(value);
			else if(byteNumber == 2)
				secondByte = String.valueOf(value);
			else if(byteNumber == 3)
				thirdByte = String.valueOf(value);
			else
				fourthByte = String.valueOf(value);
		}

}
