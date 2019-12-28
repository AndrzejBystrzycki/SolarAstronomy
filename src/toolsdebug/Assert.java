package toolsdebug;
//Chapter 5: Hiding the Implementation 253
public class Assert {
	private static void perr(String msg) {
		System.err.println(msg);
	}
	public final static void msg(String msg){
		System.out.println(msg);
	}
	public final static void is_true(boolean exp) {
		if(!exp) perr("Assertion failed");
	}
	public final static void is_false(boolean exp){
		if(exp) perr("Assertion failed");
	}
	public final static void
	is_true(boolean exp, String msg) {
		if(!exp) perr("Assertion failed: " + msg);
	}
	public final static void
	is_false(boolean exp, String msg) {
		if(exp) perr("Assertion failed: " + msg);
	}
} ///:~
