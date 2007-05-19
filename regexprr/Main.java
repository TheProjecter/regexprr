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

	public static Automaton Transform(BinaryNode node)
	{
		char label = node.GetLabel();
		
		switch(label)
		{
			case '*':
			case '<':
				return new Automaton(label, Transform(node.GetLeft()));
			case '.':
			case '|':
			case '^':
				return new Automaton(label, Transform(node.GetLeft()), Transform(node.GetRight()));
			case '@':
			case '$':
			default:
				return new Automaton(label);
		}
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
			//Parser p = new Parser("a<.(b.b).(b.b).((b.(b)))");
			//Parser p = new Parser("(a*.b)*|c");
			Parser p = new Parser("a*.b.c.d|c*");
			//Parser p = new Parser("(a*.b*)^(a.a.b)");
			//Parser p = new Parser("a.b");
			//Parser p = new Parser("(a*.b*)");
			//Parser p = new Parser("a.a.b");
			
			BinaryTree tree = p.GetParseBinaryTree();
			BinaryNode root = tree.GetRootBinaryNode();
			
			Automaton NFA = Transform(root);
			NFA.ConvertToDFA();
			//NFA.ToDot();
			
		} catch(ParseException e)
		{
			System.out.println("Error: "+e.getMessage());
		}
		
		//Automaton aa = new Automaton('@');
		//aa.ToDot();
		
	}

}
