package p2;

import p1.A;

public class B {
	public void mB1() {}
	
	public void mB2() {}

	public void mA1(A a, float j, int foo, String bar) {
		mB1();
		System.out.println(bar + j + a);
		String z= a.fString + a.fBool;
		a.fInt++;
	}
}