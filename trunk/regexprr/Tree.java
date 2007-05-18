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

public class Tree
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