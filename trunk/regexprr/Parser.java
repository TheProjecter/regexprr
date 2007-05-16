class Tree
{
	public Tree()
	{
	 
	}
}
 
public class Parser
{
	private ChomskyRules grammar;
	private Tree parseTree;
	
	public Parser(String pattern)
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

 
	private Tree CYK(String pattern)
	{
		int n = pattern.length();
		int varCount = grammar.GetVarCount();

		boolean[][][] D = new boolean[varCount][n][n];
		int[][][] L = new int[varCount][n][n];
		int[][][] M = new int[varCount][n][n];
		int[][][] R = new int[varCount][n][n];

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

		boolean validExpression = D[grammar.GetStartVarIndex()][0][n - 1];
		
		if (validExpression)
		{
			System.out.println("Yes, it is a regular expression");
		} else
		{
			System.out.println("Not a regular expression");
		}
		
		return new Tree();
	}
}
