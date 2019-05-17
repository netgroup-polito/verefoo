package it.polito.verigraph.extra;

public class PortInterval {
	private int start;
	private int end;
	boolean single;
	public PortInterval(int start, int end) {
		this.start = start;
		this.end = end;
		single = start==end;
	}
	public PortInterval(String range) {
		if(range.equals("*")){
			this.start = 0;
			this.end = 65535;
		}
		else if(range.contains("-")){
			String strStart = range.substring(0, range.indexOf("-"));
			String strEnd = range.substring(range.indexOf("-")+1);
			this.start = Integer.parseInt(strStart); 
			this.end = Integer.parseInt(strEnd);
			
		}else{
			this.start = Integer.parseInt(range);
			this.end = Integer.parseInt(range);
		}
		single = start==end;;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	@Override
	public String toString() {
		if(start == 0 && end == 65535)
			return "*";
		if(start == end)
			return String.valueOf(start);
		return start + "-" + end;
	}
	public boolean overlapsWith(PortInterval other){
		if(this.single && other.single){
			return this.getStart() == other.getStart() || this.getStart()+1 == other.getStart();
		}
		if(this.single){
			return this.start >= other.getStart()-1 && this.getEnd() <= other.getEnd()+1;
		}
		if(other.single){
			return other.start >= this.getStart()-1 && other.getEnd() <= this.getEnd()+1;
		}
		return other.getStart() <= this.getEnd()+1;
	}
	
	

}
