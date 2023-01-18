package it.polito.verefoo.graph;

/**
 * This class models the IPv4 address representation following four fields
 * approach.
 * Each one
 * of these four fields represent a byte of the IPv4 address and can have a
 * value
 * between 0 and 255 or can be represented
 * by
 * a wild card (“*” represents concisely the full range [0, 255]).
 *
 */
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
	
	/**
	 * This constructor simply populates the IPv4 address of "this" object including
	 * the negation status.
	 * 
	 * @param firstByte  firstByte of the IPv4 address having value in [0-255]
	 * @param secondByte secondByte of the IPv4 address having value in [0-255]
	 * @param thirdByte  thirdByte of the IPv4 address having value in [0-255]
	 * @param fourthByte fourthByte of the IPv4 address having value in [0-255]
	 * @param neg        Negation status of the IPv4 address that is equivalent to
	 *                   "!"
	 */
	public IPAddress(String fistByte, String secondByte, String thirdByte, String fourthByte, boolean neg) {
		super();
		this.firstByte = fistByte;
		this.secondByte = secondByte;
		this.thirdByte = thirdByte;
		this.fourthByte = fourthByte;
		this.neg = neg;
	}
	

	/**
	 * This constructor simply populates the IPv4 address of "this" object including
	 * the negation status.
	 * 
	 * @param ip  Contains IPv4 address in string format
	 * @param neg Negation status of the IPv4 address that is equivalent to
	 *            "!"
	 */
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
	/**
	 * Getter method for the first byte of IPV4 address
	 * @return the first byte
	 */
	public String getFirstByte() {
		return firstByte;
	}
	/**
	 * Setter method for the first byte of the IPV4 address
	 * @param fistByte It is the first byte of the IPV4 address
	 */
	public void setFirstByte(String fistByte) {
		this.firstByte = fistByte;
	}
	/**
	 * Getter method for the second byte of IPV4 address
	 * @return the second byte
	 */
	public String getSecondByte() {
		return secondByte;
	}
	/**
	 * Setter method for the second byte of the IPV4 address
	 * @param secondByte Is the second byte of the IPV4 address
	 */
	public void setSecondByte(String secondByte) {
		this.secondByte = secondByte;
	}
	/**
	 * Getter method for the third byte of IPV4 address
	 * @return the third byte
	 */
	public String getThirdByte() {
		return thirdByte;
	}
	/**
	 * Setter method for the third byte of the IPV4 address
	 * @param thirdByte Is the third byte of the IPV4 address
	 */
	public void setThirdByte(String thirdByte) {
		this.thirdByte = thirdByte;
	}
	/**
	 * Getter method for the fourth byte of IPV4 address
	 * @return the fourth byte
	 */
	public String getFourthByte() {
		return fourthByte;
	}
	/**
	 * Setter method for the fourth byte of the IPV4 address
	 * @param fourthByte Is the fourth byte of the IPV4 address
	 */
	public void setFourthByte(String fourthByte) {
		this.fourthByte = fourthByte;
	}
	/**
	 * Checks if the IPv4 negation status is true
	 * @return True if negation exist, False otherwise.
	 */
	public boolean isNeg() {
		return neg;
	}
	/**
	 * Setter method for the negation status of IPv4
	 * @param neg Is the negation status
	 */
	public void setNeg(boolean neg) {
		this.neg = neg;
	}

	/**
	 * Checks if "this" is included in the parameter 'ip' or equal to it. 'ip'
	 * should use wildcards to represent a range of
	 * IPv4 addresses and neg is not considered here.
	 * 
	 * @param ip Contains IPv4 address
	 * @return True if "this" is included or equal to 'ip', False otherwise.
	 */
	public boolean isIncludedIn(IPAddress ip) {
		if((firstByte.equals(ip.getFirstByte()) || ip.getFirstByte().equals("-1")) 
				&& (secondByte.equals(ip.getSecondByte()) || ip.getSecondByte().equals("-1"))
				&& (thirdByte.equals(ip.getThirdByte()) || ip.getThirdByte().equals("-1"))
				&& (fourthByte.equals(ip.getFourthByte()) || ip.getFourthByte().equals("-1")))
			return true;
		return false;
	}
	
	/**
	 * Checks if all the bytes within the IPAddress ("this") are equal to the
	 * wild card.
	 * 
	 * @return True if all the bytes within the IPAddress are equal to the wildcard,
	 *         False otherwise.
	 */	
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
	
	/**
	 * Checks if "this" is equal to 'ip' without considering the neg attribute
	 * 
	 * @param ip Contains IPv4 address
	 * @return True if the two IPAddresses have the same values for all bytes, False
	 *         otherwise.
	 */
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
