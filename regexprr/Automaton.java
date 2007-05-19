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
	private boolean accept;
	
	public AutomatonNode()
	{
		edges = new ArrayList();
		epsilonEdges = new ArrayList();
	}
	
	public void SetAccept()
	{
		accept = true;
	}
	
	public void UnsetAccept()
	{
		accept = false;
	}
	
	public boolean IsAccepted()
	{
		return accept;
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
}

public class Automaton
{
	private AutomatonNode start;
	private ArrayList nodes;
	private ArrayList acceptNodes;
	private ArrayList alphabet;

	private Automaton()
	{
		Init();
	}
	
	public Automaton(char operation, Automaton a)
	{
		Init();
		
		switch(operation)
		{
			case '*':
				Kleene(a);
			break;
			
			case '<':
				Complement(a);
			break;

		}
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
	
	public void Init()
	{
		nodes = new ArrayList();
		acceptNodes = new ArrayList();
		alphabet = new ArrayList();
		
		InitAlphabet();
	}
	
	
	public Automaton(boolean t)
	{
		Init();
		
		/*
		AutomatonNode q0 = NewAutomatonNode();
		AutomatonNode q1 = NewAutomatonNode();
		AutomatonNode q2 = NewAutomatonNode();
		
		start = q0;
		q0.AddEdge('b', q1);
		q0.AddEdge('$', q2);
		
		q1.AddEdge('a', q1);
		q1.AddEdge('a', q2);
		q1.AddEdge('b', q2);
		
		q2.AddEdge('a', q0);
		
		AddToAlphabet('a');
		AddToAlphabet('b');
		
		AddAcceptNode(q0);
		*/
		
		AutomatonNode q0 = NewAutomatonNode();
		AutomatonNode q1 = NewAutomatonNode();
		AutomatonNode q2 = NewAutomatonNode();
		AutomatonNode q3 = NewAutomatonNode();
		
		start = q0;
		q0.AddEdge('a', q1);
		q0.AddEdge('c', q3);
		
		q1.AddEdge('a', q1);
		q1.AddEdge('c', q2);
		
		q2.AddEdge('a', q2);
		q2.AddEdge('c', q2);

		q3.AddEdge('a', q2);
		q3.AddEdge('c', q2);

		AddAcceptNode(q2);
	}
	
	
	public AutomatonNode NewAutomatonNode()
	{
		AutomatonNode n = new AutomatonNode();
		
		nodes.add(n);
		
		return n;
	}
	
	// Calculate which nodes you can directly reach via epsilon edges
	private AutomatonNodeSet EpsilonDirectReach(AutomatonNode n)
	{
		AutomatonNodeSet set = new AutomatonNodeSet();
		
		// Basically, just add all the end nodes of the epsilon edges
		for(int i=0; i<n.GetEpsilonEdgeCount(); i++)
		{
			AutomatonEdge e = n.GetEpsilonEdge(i);
			
			set.AddNode(e.GetEndNode());
		}
		
		return set;
	}
	
	// Remove all epsilon edges from the automaton
	public void RemoveEpsilonEdges()
	{
		for(int i=0; i<GetNodeCount(); i++)
		{
			GetNode(i).ClearEpsilonEdges();
		}
	}
	
	// Calculate all nodes you can reach via one or more epsilon edges, for all nodes of the automaton
	private AutomatonNodeSet[] GetEpsilonReachableSet()
	{
		// Every node will have a set describing which nodes you can reach via epsilon edges
		AutomatonNodeSet[] sets = new AutomatonNodeSet[GetNodeCount()];
		
		// And so, for each node
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);

			// Get the direct reachable nodes via epsilon edges
			sets[i] = EpsilonDirectReach(n);
			AutomatonNodeSet set = sets[i];
			
			// And add in the direct reachable nodes via epsilon edges, of the newly added nodes
			// Eventually this will stop because the set's node count is not increased
			for(int j=0; j<set.GetNodeCount(); j++)
			{
				AutomatonNodeSet newset = EpsilonDirectReach(set.GetNode(j));
				
				// Take the union (don't add nodes we already have)
				set.Union(newset);
			}
		}
		
		return sets;
	}
	
	// By removing all the epsilon edges, we need to fix up all the possible paths that were reachable by epsilon edges
	public void FixupEpsilons(AutomatonNodeSet[] sets)
	{
		// For each node
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode n = GetNode(i);
			
			// And for each (non-epsilon) edge of that node
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				AutomatonEdge e = n.GetEdge(j);
				
				// Get the end node of that edge, and check which nodes are reachable via one or more epsilon edges
				AutomatonNodeSet set = sets[e.GetEndNode().GetId()];
				for(int k=0; k<set.GetNodeCount(); k++)
				{
					// And then add edges to that new end node (which was reachable via one or more epsilon edges)
					n.AddEdge(e.GetTerminal(), set.GetNode(k));
				}
			}
		}		
	}
	
	// Check whether t contains one of the accept nodes
	public boolean ContainsAcceptNode(AutomatonNodeSet t)
	{
		// Now we need to check whether this node was an accept node
		for(int j=0; j<acceptNodes.size(); j++)
		{
			AutomatonNode acceptNode = (AutomatonNode)acceptNodes.get(j);
			
			if (t.Contains(acceptNode))
			{
				return true;
			}
		}
		
		return false;
	}
	
	// Given a node (which is a set), calculate all direct nodes which are reachable via every symbol of the alphabet,
	// and recursively do the same for each directly added node
	// All the added nodes are kept in the powerSet
	private void DFARecur(ArrayList powerSet, AutomatonNodeSet set)
	{
		// All nodes ({a set}) have (alphabet size) edges to other nodes 
		AutomatonNodeSet[] nextSet  = new AutomatonNodeSet[GetAlphabetSize()];
		
		// So initialize these sets to the empty set for now
		for(int i=0; i<nextSet.length; i++)
		{
			nextSet[i] = new AutomatonNodeSet();
		}


		// Calculate the reachable nodes (a nextSet), for each symbol k
		// This process is easy because all epsilon edges have already been removed and patched up
		for(int i=0; i<set.GetNodeCount(); i++)
		{
			// So for each node of the set, calculate which nodes it can reach
			AutomatonNode n = set.GetNode(i);
			
			for(int j=0; j<n.GetEdgeCount(); j++)
			{
				// Do so by checking each edge
				AutomatonEdge e = n.GetEdge(j);
				
				for(int k=0; k<GetAlphabetSize(); k++)
				{
					char c = GetAlphabetChar(k);
					
					// Add the end node to the corresponding nextSet
					if (e.GetTerminal() == c)
						nextSet[k].AddNode(e.GetEndNode());
				}
			}
		}
		
		// The powerNode is a single AutomatonNode representing a whole {set of nodes} 
		AutomatonNode parentPowerNode = set.GetPowerNode();
		
		// So we need to add an edge from the parentPowerNode to each of its child powernodes
		for(int k=0; k<GetAlphabetSize(); k++)
		{
			// We need to check if the nextSet already exists in the powerSet 
			boolean found = false;
			for(int i=0; i<powerSet.size(); i++)
			{
				AutomatonNodeSet t = (AutomatonNodeSet)powerSet.get(i);
				
				// Does it exist already?
				if (nextSet[k].Equals(t))
				{
					// Yes, so simply copy the reference to its real powernode
					found = true;
					nextSet[k].SetPowerNode(t.GetPowerNode());
					break;
				}
			}
			
			// Not found?
			if (!found)
			{
				// Create a new node, and add the set to the powerSet
				nextSet[k].SetPowerNode(new AutomatonNode());
				powerSet.add(nextSet[k]);
				
				// Does the nextSet contain an accept node?
				if (ContainsAcceptNode(nextSet[k]))
				{
					// Then make the powerNode an accept node
					nextSet[k].GetPowerNode().SetAccept();
				}
				
				// Recur on the newly added node, this will eventually stop when all reachable nodes have been added 
				DFARecur(powerSet, nextSet[k]);
			}
			
			// Now simply add an edge from the parent to the child
			// and this is done for each symbol of the alphabet
			AutomatonNode childPowerNode = nextSet[k].GetPowerNode(); 
			
			parentPowerNode.AddEdge(GetAlphabetChar(k), childPowerNode);
		}
		
	}
	
	public void ToDFA()
	{
		// Give each node a unique id, by their index (very important)
		IdentifyNodes();
		
		// Calculate which nodes are reachable via one or more epsilon edge
		AutomatonNodeSet[] sets = GetEpsilonReachableSet();
		
		// Remove them
		RemoveEpsilonEdges();
		
		// Fix them up
		FixupEpsilons(sets);
		
		// Get the nodes reachable via one or more epsilon-edges by the start node
		AutomatonNodeSet startSet = sets[start.GetId()];
		// add the start node itself to it
		startSet.AddNode(start);
		
		
		// Init the powerSet
		ArrayList powerSet = new ArrayList();
		
		// Init the DFA start node
		AutomatonNode dfaStart = new AutomatonNode();
		startSet.SetPowerNode(dfaStart);
		
		// If the startSet contains an accept node
		if (ContainsAcceptNode(startSet))
		{
			// make the powernode an accept node
			dfaStart.SetAccept();
		}
		
		// Add the startSet to the powerSet
		powerSet.add(startSet);
		
		// Now find all nodes reachable by the start node
		// When it ends, it will have created a DFA
		DFARecur(powerSet, startSet);
		
		// Now we want to copy all the DFA's powerNodes to this automaton
		
		// First clear all nodes and acceptNodes of this automaton
		nodes.clear();
		acceptNodes.clear();
		
		// Start adding nodes
		for(int i=0; i<powerSet.size(); i++)
		{
			AutomatonNodeSet t = (AutomatonNodeSet)powerSet.get(i);
			
			AutomatonNode node = t.GetPowerNode();
			
			// Add powernode to this automaton
			nodes.add(node);
			
			// Acceptnode?
			if (node.IsAccepted())
			{
				acceptNodes.add(node);
			}
		}
		
		start = dfaStart;
		
		// All done now
	}
	
	public boolean Run(String s)
	{
		ToDFA();
		
		AutomatonNode r = start;
		
		for(int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			
			r = r.GetNodeViaTerminal(c);
			
			if (r == null)
				return false;
		}
		
		return r.IsAccepted();
	}
	
	private int GetAlphabetSize()
	{
		return alphabet.size();
	}
	
	private char GetAlphabetChar(int i)
	{
		return ((Character)alphabet.get(i)).charValue();
	}
	
	private void InitAlphabet()
	{
		alphabet.add(new Character('a'));
		alphabet.add(new Character('b'));
		alphabet.add(new Character('c'));
		alphabet.add(new Character('d'));
	}

	
	private void Kleene(Automaton a)
	{
		start = NewAutomatonNode();

		AddAcceptNode(start);
		
		AddAllNodes(a);
		
		start.AddEdge('$', a.GetStartNode());
		
		for(int i=0; i<a.GetAcceptNodeCount(); i++)
		{
			AutomatonNode n = a.GetAcceptNode(i);
			
			n.AddEdge('$', a.GetStartNode());
		}
	}
	
	private void Complement(Automaton a)
	{
		a.ToDFA();
		
		AddAllNodes(a);
		
		acceptNodes.clear();
		
		start = a.start;
		
		for(int i=0; i<GetNodeCount(); i++)
		{
			AutomatonNode node = GetNode(i);
			
			if (node.IsAccepted())
			{
				node.UnsetAccept();
			}
			else
			{
				AddAcceptNode(node);
			}
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
			AutomatonNode n = a.GetNode(i);
			
			nodes.add(n);
			
			if (n.IsAccepted())
			{
				acceptNodes.add(n);
			}
		}
	}
	
	
	private void Union(Automaton a1, Automaton a2)
	{
		start = NewAutomatonNode();
		
		AddAllNodes(a1);
		AddAllNodes(a2);
		
		
		start.AddEdge('$', a1.GetStartNode());
		start.AddEdge('$', a2.GetStartNode());
	}
	
	private void Concat(Automaton a1, Automaton a2)
	{
		AddAllNodes(a1);
		
		for(int i=0; i<GetAcceptNodeCount(); i++)
		{
			AutomatonNode n = GetAcceptNode(i);
			
			n.UnsetAccept();
		}
		acceptNodes.clear();
		
		AddAllNodes(a2);
		
		start = a1.GetStartNode();
		

		// Connect all accept nodes from a1 to the start of a2
		for(int i=0; i<a1.GetAcceptNodeCount(); i++)
		{
			AutomatonNode n = a1.GetAcceptNode(i);
			
			n.AddEdge('$', a2.GetStartNode());
		}
	}
	
	private void Intersect(Automaton a1, Automaton a2)
	{
		Automaton c1 = new Automaton('<', a1);
		Automaton c2 = new Automaton('<', a2);
		Automaton u = new Automaton('|', c1, c2);
		
		// A ^ B = (Ac U Bc)c
		Complement(u);
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
	}
	
	private void InitEmptyStringBase()
	{
		start = NewAutomatonNode();
		AddAcceptNode(start);
	}
	
	private void AddAcceptNode(AutomatonNode n)
	{
		n.SetAccept();
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
		return node.IsAccepted();
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
