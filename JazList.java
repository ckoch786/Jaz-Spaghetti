package jaz;


/***
 * 
 * @author ckoch
 *
 * @param <h> Head of tuple 
 * @param <r> rest of tuple 
 */
public class JazList<h,r> {
	private h head;
	private r rest;

	public JazList(h head, r rest){
		this.head = head;
		this.rest = rest;
	}
	
	public h head() {
		return this.head;
	}
	
	public r rest() {
		return this.rest;
	}
	
	public void head(h head) {
		this.head = head;
	}
	
	public void rest(r rest) {
		this.rest = rest;
	}
	
	public String toString(){
		return "("+head + rest +")";
	}

}
