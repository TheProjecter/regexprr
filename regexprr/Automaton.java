import java.util.*;

class AutomatonNodeSet
{
	private ArrayList nodes;
	private AutomatonNode powerNode;
	private int id;
	
	public AutomatonNodeSet()
	{
		nodes = new ArrayList();
		powerNode = null;
	}
	
	public void SetId(int i)
	{
		id = i;
	}
	
	public int GetId()
	{
		return id;
	}

	public String ToString()
	{
		String s = "{";
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			if (i != 0)
				s += ", ";
			
			s += GetNode(i).GetId();
		}
		
		s += "}";
		
		return s;
	}
	
	public int GetNodeCount()
	{
		return nodes.size();
	}
	
	public AutomatonNode GetNode(int i)
	{
		return (AutomatonNode)nodes.get(i);
	}
	
	public boolean Contains(AutomatonNode node)
	{
		for(int i=0; i<GetNodeCount(); i++)
		{
			if (GetNode(i) == node)
				return true;
		}
		
		return false;
	}
	
	public boolean Equals(AutomatonNodeSet b)
	{
		if (b.GetNodeCount() != GetNodeCount())
			return false;
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			 if (!b.Contains(GetNode(i)))
				 return false;
		}
		
		return true;
	}
	
	public boolean Union(AutomatonNodeSet set)
	{
		boolean addedSomething = false;
		
		for(int i=0; i<set.GetNodeCount(); i++)
		{
			AutomatonNode n = set.GetNode(i);
			
			if (AddNode(n))
			{
				addedSomething = true;
			}
		}
		
		return addedSomething;
	}
	
	public boolean AddNode(AutomatonNode node)
	{
		if (!Contains(node))
		{
			nodes.add(node);
			
			return true;
		}
		
		return false;
	}
	
	public void SetPowerNode(AutomatonNode n)
	{
		powerNode = n;
	}
	
	public AutomatonNode GetPowerNode()
	{
		return powerNode;
	}
}

class AutomatonEdge
{
	private AutomatonNode node;
	private char terminal;
	
	public AutomatonEdge(char term, AutomatonNode n)
	{
		terminal = term;
		node = n;
	}
	
	public char GetTerminal()
	{
		return terminal;
	}
	
	public AutomatonNode GetEndNode()
	{
		return node;
	}
}

class AutomatonNode
{
	private ArrayList edges;
	private ArrayList epsilonEdges;
	private int id;
	
	public AutomatonNode()
	{
		edges = new ArrayList();
		epsilonEdges = new ArrayList();
	}
	
	public void ClearEpsilonEdges()
	{
		epsilonEdges.clear();
	}
	
	public int GetEdgeCount()
	{
		return edges.size();
	}

	public int GetEpsilonEdgeCount()
	{
		return epsilonEdges.size();
	}

	
	public AutomatonEdge GetEdge(int i)
	{
		return (AutomatonEdge)edges.get(i);
	}
	
	public AutomatonEdge GetEpsilonEdge(int i)
	{
		return (AutomatonEdge)epsilonEdges.get(i);
	}
	
	private boolean ContainsEdge(char terminal, AutomatonNode n)
	{
		for(int i=0; i<GetEdgeCount(); i++)
		{
			AutomatonEdge e = GetEdge(i);
			
			if (e.GetTerminal() == terminal && e.GetEndNode() == n)
				return true;
		}
		
		return false;
	}
	
	private boolean ContainsEpsilonEdge(AutomatonNode n)
	{
		for(int i=0; i<GetEpsilonEdgeCount(); i++)
		{
			AutomatonEdge e = GetEpsilonEdge(i);
			
			if (e.GetEndNode() == n)
				return true;
		}
		
		return false;
	}
	
	public void AddEdge(char terminal, AutomatonNode n)
	{
		if (terminal == '$')
		{
			if (!ContainsEpsilonEdge(n))
			{
				epsilonEdges.add(new AutomatonEdge(terminal, n));
			}
		}
		else
		{
			if (!ContainsEdge(terminal, n))
			{
				edges.add(new AutomatonEdge(terminal, n));
			}
		}
	}
	
	public void SetId(int i)
	{
		id = i;
	}
	
	public int GetId()
	{
		return id;
	}
/*	
	public AutomatonNode GetNodeViaTerminal(char terminal)
	{
		for(int i=0; i<GetEdgeCount(); i++)
		{
			AutomatonEdge e = GetEdge(i);
			
			if (e.GetTerminal() == terminal)
				return e.GetEndNode();
		}
		
		return null;
	}
*/	
}

public class Automaton
{
	private AutomatonNode start;
	private ArrayList nodes;
	private ArrayList acceptNodes;
	private ArrayList alphabet;
	
	
	public void Init()
	{
		nodes = new ArrayList();
		acceptNodes = new ArrayList();
		alphabet = new ArrayList();
	}
	
	public AutomatonNode NewAutomatonNode()
	{
		AutomatonNode n = new AutomatonNode();
		
		nodes.add(n);
		
		return n;
	}
	
	private AutomatonNodeSet EpsilonDirectReach(AutomatonNode n)
	{
		AutomatonNodeSet set = new AutomatonNodeSet();
		
		for(int i=0; i<n.GetEpsilonEdgeCount(); i++)
		{
			AutomatonEdge e = n.GetEpsilonEdge(i);
			
			set.AddNode(e.GetEndNode());
		}
		
		return set;
	}
	
	/*private ArrayList EpsilonWideReach(AutomatonNode n)
	{
		
	}*/
	
	public void RemoveEpsilonEdges()
	{
		for(int i=0; i<GetNodeCount(); i++)
		{
			GetNode(i).ClearEpsilonEdges();
		}
	}
	
	private AutomatonNodeSet[] GetEpsilonReachableSet()
	{
		AutomatonNodeSet[] sets = new AutomatonNodeSet[GetNodeCount()];
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);

			sets[i] = EpsilonDirectReach(n);
			AutomatonNodeSet set = sets[i];
			
			for(int j=0; j<set.GetNodeCount(); j++)
			{
				AutomatonNodeSet newset = EpsilonDirectReach(set.GetNode(j));
				
				set.Union(newset);
			}
		}
		
		return sets;
	}
	
	public void FixupEpsilons(AutomatonNodeSet[] sets)
	{
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);
				
				//e.GetTerminal()
				AutomatonNodeSet set = sets[e.GetEndNode().GetId()];
				//System.out.print("Reachable from node "+e.GetEndNode().GetId()+": ");
				for(int k=0; k<set.GetNodeCount(); k++)
				{
					//System.out.println
					//System.out.print(""+set.GetNode(k).GetId()+",");
					n.AddEdge(e.GetTerminal(), set.GetNode(k));
				}
				//System.out.println();
			}
		}		
	}
	
	private void Test(ArrayList powerSet, AutomatonNodeSet set)
	{
		AutomatonNodeSet[] nextSet  = new AutomatonNodeSet[GetAlphabetSize()];
		
		for(int i=0; i<nextSet.length; i++)
		{
			nextSet[i] = new AutomatonNodeSet();
		}


		for(int i=0; i<set.GetNodeCount(); i++)
		{
			AutomatonNode n = set.GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);
				
				for(int k=0; k<GetAlphabetSize(); k++)
				{
					char c = GetAlphabetChar(k);
					
					if (e.GetTerminal() == c)
						nextSet[k].AddNode(e.GetEndNode());
				}
			}
		}
		
		AutomatonNode parentPowerNode = set.GetPowerNode();
		for(int k=0; k<GetAlphabetSize(); k++)
		{
			boolean found = false;
			for(int i=0; i<powerSet.size(); i++)
			{
				AutomatonNodeSet t = (AutomatonNodeSet)powerSet.get(i);
				
				if (nextSet[k].Equals(t))
				{
					found = true;
					nextSet[k].SetPowerNode(t.GetPowerNode());
					break;
				}
			}
			
			if (!found)
			{
				nextSet[k].SetPowerNode(new AutomatonNode());
				powerSet.add(nextSet[k]);
				
				
				Test(powerSet, nextSet[k]);
			}
			
			AutomatonNode childPowerNode = nextSet[k].GetPowerNode(); 
			
			parentPowerNode.AddEdge(GetAlphabetChar(k), childPowerNode);
		}
		
	}
	
	private Automaton()
	{
		Init();
	}
	
	public void ConvertToDFA()
	{
		IdentifyNodes();
		
		AutomatonNodeSet[] sets = GetEpsilonReachableSet();
		
		RemoveEpsilonEdges();
		
		FixupEpsilons(sets);
		
		AutomatonNodeSet startSet = sets[start.GetId()];
		startSet.AddNode(start);
		
		System.out.println(startSet.ToString());
		
		ArrayList l = new ArrayList();
		
		//Automaton DFA = new Automaton();
		//DFA.start = new AutomatonNode();
		AutomatonNode dfaStart = new AutomatonNode();
		startSet.SetPowerNode(dfaStart);
		l.add(startSet);
		Test(l, startSet);
		
		Automaton dfa = new Automaton();
		
		for(int i=0; i<l.size(); i++)
		{
			AutomatonNodeSet t = (AutomatonNodeSet)l.get(i);
			
			AutomatonNode node = t.GetPowerNode();
			
			for(int j=0; j<acceptNodes.size(); j++)
			{
				AutomatonNode acceptNode = (AutomatonNode)acceptNodes.get(j);
				
				if (t.Contains(acceptNode))
				{
					dfa.acceptNodes.add(node);
				}
			}
			
			dfa.nodes.add(node);
		}
		
		dfa.start = dfaStart;
		
		
		
		dfa.ToDot();
		/*
		AutomatonNodeSet[] setVia  = new AutomatonNodeSet[GetAlphabetSize()];
		
		for(int i=0; i<setVia.length; i++)
		{
			setVia[i] = new AutomatonNodeSet();
		}
		
		for(int i=0; i<startSet.GetNodeCount(); i++)
		{
			AutomatonNode n = startSet.GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);
				
				for(int k=0; k<GetAlphabetSize(); k++)
				{
					char c = GetAlphabetChar(k);
					if (e.GetTerminal() == c)
						setVia[k].AddNode(e.GetEndNode());
				}
			}
		}
		
		for(int k=0; k<GetAlphabetSize(); k++)
		{
			char c = GetAlphabetChar(k);
			
			System.out.println("Next via "+c+": "+setVia[k].ToString());
		}
*/
		/*
		AutomatonNodeSet set = sets[start.GetId()];
		System.out.print("Reachable from node "+start.GetId()+": ");
		for(int k=0; k<set.GetNodeCount(); k++)
		{
			//System.out.println
			System.out.print(""+set.GetNode(k).GetId()+",");
			start.AddEdge(e.GetTerminal(), set.GetNode(k));
		}
		System.out.println();
		*/
		
		/*
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNodeSet set = sets[i];
			
			System.out.print("Reachable from node "+GetNode(i).GetId()+": ");
			for(int j=0; j<set.GetNodeCount(); j++)
			{
				System.out.print(""+set.GetNode(j).GetId()+",");
			}
			System.out.println();
		}
		*/
		/*
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNodeSet set = sets[i];
			
			for(int j=0; j<set.GetNodeCount(); j++)
			{
				int id = set.GetNode(j).GetId();
				
				if (id > i && !set.Union( sets[id] ))
					break;
			}
		}*/
		
		
		/*
		// Remove epsilon-edges
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);
				
				AutomatonNode next = e.GetEndNode();

				for(int k=0; k<next.GetEdgeCount(); k++)
				{
					AutomatonEdge enext = next.GetEdge(k);
					
					if (enext.GetTerminal() == '$')
					{
						//n.AddEdge(e.GetTerminal(), next);
					}
				}
			}
		}
		*/
	}
	
	public Automaton(char operation, Automaton a)
	{
		Init();
		
		switch(operation)
		{
			case '*':
				Kleene(a);
			break;
		}
	}
	
	private int GetAlphabetSize()
	{
		return alphabet.size();
	}
	
	private char GetAlphabetChar(int i)
	{
		return ((Character)alphabet.get(i)).charValue();
	}
	
	private void AddToAlphabet(char c)
	{
		boolean found = false;
		
		for(int j=0; j<GetAlphabetSize(); j++)
		{
			char k = GetAlphabetChar(j);
			
			if (k == c)
			{
				found = true;
				break;
			}
		}
		
		if (!found)
			alphabet.add(new Character(c));
	}
	
	private void UnionAlphabet(Automaton a)
	{
		for(int i=0; i<a.GetAlphabetSize(); i++)
		{
			char c = a.GetAlphabetChar(i);
			
			AddToAlphabet(c);
		}
	}
	
	private void IntersectAlphabet(Automaton a1, Automaton a2)
	{
		for(int i=0; i<a1.GetAlphabetSize(); i++)
		{
			char c = a1.GetAlphabetChar(i);
			
			for(int j=0; j<a2.GetAlphabetSize(); j++)
			{
				char k = a2.GetAlphabetChar(j);
				
				if (c == k)
				{
					AddToAlphabet(c);
				}
			}
		}
	}
	
	private void Kleene(Automaton a)
	{
		start = NewAutomatonNode();

		AddAcceptNode(start);
		
		AddAllNodes(a);
		AddAllAcceptNodes(a);
		
		start.AddEdge('$', a.GetStartNode());
		
		for(int i=0; i<a.GetAcceptNodeCount(); i++)
		{
			AutomatonNode n = a.GetAcceptNode(i);
			
			n.AddEdge('$', a.GetStartNode());
		}
		
		//AddToAlphabet('$');
		UnionAlphabet(a);
	}
	
	
	
	public Automaton(char operation, Automaton a1, Automaton a2)
	{
		Init();
		
		switch(operation)
		{
			case  '|':
				Union(a1, a2);
			break;
			
			case '.':
				Concat(a1, a2);
			break;
			
			case '^':
				Intersect(a1, a2);
			break;
		}
	}
	
	private AutomatonNode GetStartNode()
	{
		return start;
	}
	
	private void AddAllNodes(Automaton a)
	{
		for(int i=0; i<a.GetNodeCount(); i++)
		{
			nodes.add( a.GetNode(i) );
		}
	}
	
	private void AddAllAcceptNodes(Automaton a)
	{
		for(int i=0; i<a.GetAcceptNodeCount(); i++)
		{
			AddAcceptNode(a.GetAcceptNode(i));
		}
	}
	
	private void Union(Automaton a1, Automaton a2)
	{
		start = NewAutomatonNode();
		
		AddAllNodes(a1);
		AddAllNodes(a2);
		
		
		start.AddEdge('$', a1.GetStartNode());
		start.AddEdge('$', a2.GetStartNode());
		
		AddAllAcceptNodes(a1);
		AddAllAcceptNodes(a2);
		
		//AddToAlphabet('$');
		UnionAlphabet(a1);
		UnionAlphabet(a2);
	}
	
	private void RemoveNode(AutomatonNode node)
	{
		nodes.remove(node);
	}
	
	private void Concat(Automaton a1, Automaton a2)
	{
		
		AddAllNodes(a1);
		AddAllNodes(a2);
		
		start = a1.GetStartNode();
		

		// Connect all accept nodes from a1 to the start of a2
		for(int i=0; i<a1.GetAcceptNodeCount(); i++)
		{
			AutomatonNode n = a1.GetAcceptNode(i);
			
			n.AddEdge('$', a2.GetStartNode());
		}
		
		
		AddAllAcceptNodes(a2);
		
		//AddToAlphabet('$');
		UnionAlphabet(a1);
		UnionAlphabet(a2);
	}
	
	private void Intersect(Automaton a1, Automaton a2)
	{
		/*
		a1.IdentifyNodes();
		a2.IdentifyNodes();
		
		//start = NewAutomatonNode();
		int a2count = a2.GetNodeCount();
		int a1count = a1.GetNodeCount();
		

		System.out.println("A1 =");
		a1.ToDot();
		System.out.println("A2 =");
		a2.ToDot();
		
		AutomatonNode[] intNodes = new AutomatonNode[a1count * a2count];

		
		//start = NewAutomatonNode();
		//index[0] = start;
		IntersectAlphabet(a1, a2);
		
	
		for(int i=0; i<a1count; i++)
		{
			for(int j=0; j<a2count; j++)
			{
				//AutomatonNode n = new AutomatonNode();
				
				AutomatonNode n1 = a1.GetNode(i);
				AutomatonNode n2 = a2.GetNode(j);
				
				int index = n1.GetId()*a2count + n2.GetId();

				//intNodes[index] = n;

				for(int k=0; k<GetAlphabetSize(); k++)
				{
					char c = GetAlphabetChar(k);

					AutomatonNode n1next = n1.GetNodeViaTerminal(c);
					AutomatonNode n2next = n2.GetNodeViaTerminal(c);
					
					if (n1next != null && n2next != null)
					{
						System.out.println("("+n1.GetId()+","+n2.GetId()+")->("+n1next.GetId()+","+n2next.GetId()+")");

						int nextindex = n1next.GetId()*a2count + n2next.GetId();
						
						if (intNodes[nextindex] == null)
						{
							intNodes[nextindex] = new AutomatonNode();
						}
						
						if (intNodes[index] == null)
						{
							intNodes[index] = new AutomatonNode();
						}
						
						intNodes[index].AddEdge(c, intNodes[nextindex]);
					}
				}
			}
		}
		
		for(int i=0; i<intNodes.length; i++)
		{
			if (intNodes[i] != null)
			{
				nodes.add(intNodes[i]);
			}
		}
		
		int index = a1.GetStartNode().GetId()*a2count + a2.GetStartNode().GetId();
		
		start = intNodes[index];
		
		System.out.println("Start = ("+a1.GetStartNode().GetId()+","+a2.GetStartNode().GetId()+")");
		*/
	}
	
	public Automaton(char terminal)
	{
		Init();
		
		
		if (terminal == '@')
		{
			// do nothing, already have an empty set automaton
			InitEmptySetBase();
		}
		else if (terminal == '$')
		{
			// set the empty string automaton
			InitEmptyStringBase();
		}
		else
		{
			// set terminal automaton
			InitTerminalBase(terminal);
		}
	}

	private void InitEmptySetBase()
	{
		start = NewAutomatonNode();
	}
	
	private void InitTerminalBase(char terminal)
	{
		start = NewAutomatonNode();
		AutomatonNode n = NewAutomatonNode();
		
		start.AddEdge(terminal, n);
		
		AddAcceptNode(n);
		
		AddToAlphabet(terminal);
	}
	
	private void InitEmptyStringBase()
	{
		start = NewAutomatonNode();
		AddAcceptNode(start);
	}
	
	private void AddAcceptNode(AutomatonNode n)
	{
		acceptNodes.add(n);
	}
	
	public String ToDot()
	{
		IdentifyNodes();
		String s = "digraph g {\n\n";
		s += IdentifiersToDot();
		s += "\n";
		s += NodesToDot();
		s += "}";
		
		
		System.out.println(s);
		
		return s;
	}
	
	private int GetNodeCount()
	{
		return nodes.size();
	}
	
	private AutomatonNode GetNode(int i)
	{
		return (AutomatonNode)nodes.get(i);
	}
	
	private int GetAcceptNodeCount()
	{
		return acceptNodes.size();
	}
	
	private AutomatonNode GetAcceptNode(int i)
	{
		return (AutomatonNode)acceptNodes.get(i);
	}
	
	// Give each node a unique id
	private void IdentifyNodes()
	{
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			n.SetId(i);
		}
	}
	
	private boolean IsAcceptNode(AutomatonNode node)
	{
		for(int i=0; i<GetAcceptNodeCount(); i++)
		{
			if (GetAcceptNode(i) == node)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private String IdentifiersToDot()
	{
		String s = "";
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			s += "q"+n.GetId()+" [label=\"q"+n.GetId()+"\", shape=\"ellipse\"";
			
			if (IsAcceptNode(n))
			{
				s += ", peripheries = \"2\"";
			}
			
			s += "]\n";
		}
		
		return s;
	}
	
	private String NodesToDot()
	{
		String s = "";
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);

				s += "q"+n.GetId()+" -> q"+e.GetEndNode().GetId()+" [label=\""+e.GetTerminal()+"\"]\n";
			}
			
			for(int j=0; j<n.GetEpsilonEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEpsilonEdge(j);

				s += "q"+n.GetId()+" -> q"+e.GetEndNode().GetId()+" [fontname=\"Symbol\", label=\"e\"]\n";
			}
		}
		
		return s;
	}
}
