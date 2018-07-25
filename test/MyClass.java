package test;

import java.io.Serializable;


class MyClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	
	int i;
	float f;
	String s;


	MyClass(int i, float f, String s)
	{
		this.i = i;
		this.f = f;
		this.s = s;
	}


	@Override
	public String toString()
	{
		return "i: " + i + ", f: " + f + ", s: " + s;
	}
}