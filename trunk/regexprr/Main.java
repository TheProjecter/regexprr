/*
 * Main.java
 *
 * Created on 13 mei 2007, 21:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


/**
 *
 * @author neimod
 */
public class Main
{

	/** Creates a new instance of Main */
	public Main()
	{
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		//Parser p = new Parser("((a*).b)|c");
		//Parser p = new Parser("(((b)|(c)))");
		//Parser p = new Parser("(a*)");
		try
		{
			Parser p = new Parser("a*.(b.b).(b.b).((b.(b)))");
		} catch(ParseException e)
		{
			System.out.println("Error: "+e.getMessage());
		}
	}

}
