package test;

import java.io.*;


public class Tester
{

	public static void main(String[] args)
	{
		MyClass class1 = new MyClass(1, 1.1f, "one");
		MyClass class2 = new MyClass(2, 2.2f, "two");

		try
		{
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
			objectOutStream.writeObject(class1);
			objectOutStream.writeObject(class2);
			objectOutStream.flush();
			byte[] serialization = byteOutStream.toByteArray();

			ByteArrayInputStream byteInStream = new ByteArrayInputStream(serialization);
			ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);
			System.out.println(objectInStream.readObject());
			System.out.println(objectInStream.readObject());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
