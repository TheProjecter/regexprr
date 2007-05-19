public class Main
{
	public Main()
	{
	}

	public static void main(String[] args)
	{
		
		/*
		This class should compile with your java files and execution of
		the main method should write "OK" to standard output.
		*/
		boolean correct = true;
		
		try 
		{
			boolean test = Matcher.matches("a.a","a");
		} 
		catch (Exception e)
		{
			correct = false;
		}
		
		try 
		{
			boolean test = Matcher.matches("aa","a.a");
			correct = false;
		}
		catch (SyntaxException se)
		{
		} 
		catch (Exception e)
		{
			correct = false;
		}
		
		try
		{
			boolean test = Matcher.matches("$","");
			if (!test)
				correct = false;
		} 
		catch (Exception e)
		{
			correct = false;
		}
		
		if (correct)
			System.out.println("OK");
		else
			System.out.println("Not OK");
	}
}
