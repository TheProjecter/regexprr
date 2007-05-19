class SyntaxException extends Exception
{
	public SyntaxException(String msg)
	{
		super(msg);
	}
}
 
public class Parser
{
	private ChomskyRules grammar;
	private BinaryTree parseBinaryTree;
	private boolean[][][] D;
	private int[][][] L;
	private int[][][] M;
	private int[][][] R;
	
	public Parser(String pattern) throws SyntaxException
	{
		InitGrammar();
		parseBinaryTree = CYK(pattern);
	}
	
	public BinaryTree GetParseBinaryTree()
	{
		return parseBinaryTree;
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

 
	private BinaryTree CYK(String pattern) throws SyntaxException
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
			throw new SyntaxException("Not a regular expression");
		}
		
		BinaryTree tree = new BinaryTree();
		
		BuildParseBinaryTree(tree.GetRootBinaryNode(), startVar, 0, n-1, pattern);
		
		//System.out.println(tree.ToDot());
		
		ConvertToExpressionBinaryTree(tree.GetRootBinaryNode());
		
		//System.out.println(tree.ToDot());
		
		return tree;
	}
	
	private void ConvertToExpressionBinaryTree(BinaryNode n)
	{
		if (n == null)
		{
			return;
		}

		BinaryNode operator = n.GetRight();
		
		if (operator != null)
		{
			BinaryNode left = n.GetLeft();
			BinaryNode right = n.GetRight().GetRight();
			
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
				
				ConvertToExpressionBinaryTree(left);
				ConvertToExpressionBinaryTree(right);
				
				if (left != null && left.GetLabel() == '(')
					n.SetLeft(null);
				if (right != null && right.GetLabel() == ')')
					n.SetRight(null);
			}
		}
		else
		{
			BinaryNode terminal = n;
			
			while(terminal.GetLeft() != null)
			{
				terminal = terminal.GetLeft();
			}
			n.SetLabel(terminal.GetLabel());
			n.SetLeft(null);
		}
	}
	
	private void BuildParseBinaryTree(BinaryNode m, int A, int i, int j, String pattern)
	{
		m.SetLabel(grammar.VarIndexToChar(A));
		
		if (i == j)
		{
			BinaryNode n = new BinaryNode();
			n.SetLabel(pattern.charAt(i));
			
			m.SetLeft(n);
		}
		else
		{
			BinaryNode n1 = new BinaryNode();
			BinaryNode n2 = new BinaryNode();
			
			m.SetLeft(n1);
			m.SetRight(n2);
			BuildParseBinaryTree(n1, L[A][i][j], i, M[A][i][j], pattern);
			BuildParseBinaryTree(n2, R[A][i][j], M[A][i][j]+1, j, pattern);
		}
	}
}
