class Node
{
	private Node left, right;
	private char label;
	private int id;
	
	public Node()
	{
		left = null;
		right = null;
	}
	
	public void SetLeft(Node node)
	{
		left = node;
	}
	
	public void SetRight(Node node)
	{
		right = node;
	}
	
	public Node GetLeft()
	{
		return left;
	}
	
	public Node GetRight()
	{
		return right;
	}
	
	public void SetLabel(char l)
	{
		label = l;
	}
	
	public char GetLabel()
	{
		return label;
	}
	
	public void SetId(int i)
	{
		id = i;
	}
	
	public int GetId()
	{
		return id;
	}
}

class Tree
{
	private Node root;
	
	public Tree()
	{
		root = new Node();
	}
	
	public Node GetRootNode()
	{
		return root;
	}
	
	public void AddLeftNode(Node parent, Node child)
	{
		if (parent == null)
		{
			parent = root;
		}
		
		parent.SetLeft(child);
	}
	
	public void AddRightNode(Node parent, Node child)
	{
		if (parent == null)
		{
			parent = root;
		}
		
		parent.SetRight(child);
	}
	
	// Write out tree in dot form
	public String ToDot()
	{
		IdentifyNodesRecur(root, 0);
		
		String s = "digraph g {\n\n";
		
		s += IdentifiersToDot(root, "");
		s += "\n";
		
		s += NodesToDot(root, root.GetLeft(), ""); 
		s += NodesToDot(root, root.GetRight(), "");
		
		s += "}";
		
		
		return s;
	}
	
	// Give each node a unique id
	private int IdentifyNodesRecur(Node n, int lastId)
	{
		if (n != null)
		{
			n.SetId(lastId++);
			
			lastId = IdentifyNodesRecur(n.GetLeft(), lastId);
			lastId = IdentifyNodesRecur(n.GetRight(), lastId);
		}
		
		return lastId;
	}
	
	// Write out nodes nX = [label="Z"]
	private String IdentifiersToDot(Node n, String s)
	{
		if (n != null)
		{
			s += "n"+n.GetId()+" [label=\""+n.GetLabel()+"\"];\n";
			
			s = IdentifiersToDot(n.GetLeft(), s);
			s = IdentifiersToDot(n.GetRight(), s);
		}
		
		return s;
	}
	
	// Write out nX->nY nodes
	private String NodesToDot(Node parent, Node child, String s)
	{
		if (child != null)
		{
			s += "n"+parent.GetId()+"->n"+child.GetId()+";\n";
			
			s = NodesToDot(child, child.GetLeft(), s);
			s = NodesToDot(child, child.GetRight(), s);
		}
		
		return s;
	}
}

class ParseException extends Exception
{
	public ParseException(String msg)
	{
		super(msg);
	}
}
 
public class Parser
{
	private ChomskyRules grammar;
	private Tree parseTree;
	private boolean[][][] D;
	private int[][][] L;
	private int[][][] M;
	private int[][][] R;
	
	public Parser(String pattern) throws ParseException
	{
		InitGrammar();
		parseTree = CYK(pattern);
	}
	
	public Tree GetParseTree()
	{
		return parseTree;
	}
	 
	private void InitGrammar()
	{
		grammar = new ChomskyRules();
		
		// Add all variable symbols we will be using
		grammar.AddVar('A');
		grammar.AddVar('B');
		grammar.AddVar('C');
		grammar.AddVar('D');
		//grammar.AddVar('E'); -- not used
		grammar.AddVar('F');
		grammar.AddVar('G');
		grammar.AddVar('H');
		grammar.AddVar('I');
		grammar.AddVar('J');
		grammar.AddVar('K');
		grammar.AddVar('L');
		grammar.AddVar('R');
		grammar.AddVar('S');
		grammar.AddVar('T');
		grammar.AddVar('U');
		grammar.AddVar('V');
		
		// Add rules
		grammar.AddRule("R->SA/TB/UC/VF/VG/HD/a/b/c/d/$/@");
		grammar.AddRule("S->SA/TB/UC/VF/VG/HD/a/b/c/d/$/@");
		grammar.AddRule("T->TB/UC/VF/VG/HD/a/b/c/d/$/@");
		grammar.AddRule("U->UC/VF/VG/HD/a/b/c/d/$/@");
		grammar.AddRule("V->VF/VG/HD/a/b/c/d/$/@");
		grammar.AddRule("A->IT");
		grammar.AddRule("B->JU");
		grammar.AddRule("C->KV");
		grammar.AddRule("D->SL");
		grammar.AddRule("F->*");
		grammar.AddRule("G-><");
		grammar.AddRule("H->(");
		grammar.AddRule("I->|");
		grammar.AddRule("J->^");
		grammar.AddRule("K->.");
		grammar.AddRule("L->)");
	
	}

 
	private Tree CYK(String pattern) throws ParseException
	{
		int n = pattern.length();
		int varCount = grammar.GetVarCount();

		D = new boolean[varCount][n][n];
		L = new int[varCount][n][n];
		M = new int[varCount][n][n];
		R = new int[varCount][n][n];

		for (int l = 0; l < varCount; l++)
		{
			for (int i = 0; i < n; i++)
			{
				for (int j = 0; j < n; j++)
				{
					D[l][i][j] = false;
				}
			}

			for (int i = 0; i < n; i++)
			{
				if (grammar.IsRule(l, pattern.charAt(i)))
				{
					D[l][i][i] = true;
				}
			}
		}

		for (int j = 1; j < n; j++)
		{
			for (int i = j - 1; i >= 0; i--)
			{
				for (int l = 0; l < grammar.GetTwoVarRuleCount(); l++)
				{
					ChomskyTwoVar rule = grammar.GetTwoVarRule(l);

					int A = rule.GetVarHeadIndex();
					int B = rule.GetVarLeftIndex();
					int C = rule.GetVarRightIndex();

					for (int k = i; k <= j - 1; k++)
					{
						if (D[B][i][k] && D[C][k + 1][j])
						{
							//System.out.println("Can reach "+grammar.TwoVarRuleToString(rule)+" via "+grammar.VarIndexToChar(B)+"("+i+"->"+k+") and "+grammar.VarIndexToChar(C)+"("+(k+1)+"->"+j+")");
							D[A][i][j] = true;
							L[A][i][j] = B;
							M[A][i][j] = k;
							R[A][i][j] = C;
						}

					}
				}
			}
		}
		
		int startVar = grammar.GetStartVarIndex();

		boolean validExpression = n>0 && D[startVar][0][n - 1];
		
		if (!validExpression)
		{
			throw new ParseException("Not a regular expression");
		}
		
		Tree tree = new Tree();
		
		BuildParseTree(tree.GetRootNode(), startVar, 0, n-1, pattern);
		
		System.out.println(tree.ToDot());
		
		ConvertToExpressionTree(tree.GetRootNode());
		
		System.out.println(tree.ToDot());
		
		return tree;
	}
	
	private void ConvertToExpressionTree(Node n)
	{
		if (n == null)
		{
			return;
		}

		Node operator = n.GetRight();
		
		if (operator != null)
		{
			Node left = n.GetLeft();
			Node right = n.GetRight().GetRight();
			
			operator = n.GetRight().GetLeft();

			while(operator != null && operator.GetLeft() != null && operator.GetRight() != null)
			{
					left = operator.GetLeft();
					right = operator.GetRight().GetRight();
					
					operator = operator.GetRight().GetLeft();
			}
			
			while(operator != null && operator.GetLeft() != null)
			{
				operator = operator.GetLeft();
			}
			
			// if operator is null then we did not get a valid parse tree
			if (operator != null)
			{
				n.SetLabel(operator.GetLabel());		
				n.SetLeft(left);
				n.SetRight(right);
				
				ConvertToExpressionTree(left);
				ConvertToExpressionTree(right);
				
				if (left != null && left.GetLabel() == '(')
					n.SetLeft(null);
				if (right != null && right.GetLabel() == ')')
					n.SetRight(null);
			}
		}
		else
		{
			Node terminal = n;
			
			while(terminal.GetLeft() != null)
			{
				terminal = terminal.GetLeft();
			}
			n.SetLabel(terminal.GetLabel());
			n.SetLeft(null);
		}
	}
	
	private void BuildParseTree(Node m, int A, int i, int j, String pattern)
	{
		m.SetLabel(grammar.VarIndexToChar(A));
		
		if (i == j)
		{
			Node n = new Node();
			n.SetLabel(pattern.charAt(i));
			
			m.SetLeft(n);
		}
		else
		{
			Node n1 = new Node();
			Node n2 = new Node();
			
			m.SetLeft(n1);
			m.SetRight(n2);
			BuildParseTree(n1, L[A][i][j], i, M[A][i][j], pattern);
			BuildParseTree(n2, R[A][i][j], M[A][i][j]+1, j, pattern);
		}
	}
}
