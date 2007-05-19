public class Matcher
{
	private static Automaton Transform(BinaryNode node)
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
	
	public static boolean matches(String regexp, String str) throws Exception
	{
		Parser p = new Parser(regexp);
		
		BinaryTree tree = p.GetParseBinaryTree();
		BinaryNode root = tree.GetRootBinaryNode();
		
		Automaton NFA = Transform(root);
		
		return NFA.Run(str);
	}
}
