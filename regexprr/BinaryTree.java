class BinaryNode
{
	private BinaryNode left, right;
	private char label;
	private int id;
	
	public BinaryNode()
	{
		left = null;
		right = null;
	}
	
	public void SetLeft(BinaryNode node)
	{
		left = node;
	}
	
	public void SetRight(BinaryNode node)
	{
		right = node;
	}
	
	public BinaryNode GetLeft()
	{
		return left;
	}
	
	public BinaryNode GetRight()
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

public class BinaryTree
{
	private BinaryNode root;
	
	public BinaryTree()
	{
		root = new BinaryNode();
	}
	
	public BinaryNode GetRootBinaryNode()
	{
		return root;
	}
	
	public void AddLeftBinaryNode(BinaryNode parent, BinaryNode child)
	{
		if (parent == null)
		{
			parent = root;
		}
		
		parent.SetLeft(child);
	}
	
	public void AddRightBinaryNode(BinaryNode parent, BinaryNode child)
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
		IdentifyBinaryNodesRecur(root, 0);
		
		String s = "digraph g {\n\n";
		
		s += IdentifiersToDot(root, "");
		s += "\n";
		
		s += BinaryNodesToDot(root, root.GetLeft(), ""); 
		s += BinaryNodesToDot(root, root.GetRight(), "");
		
		s += "}";
		
		
		return s;
	}
	
	// Give each node a unique id
	private int IdentifyBinaryNodesRecur(BinaryNode n, int lastId)
	{
		if (n != null)
		{
			n.SetId(lastId++);
			
			lastId = IdentifyBinaryNodesRecur(n.GetLeft(), lastId);
			lastId = IdentifyBinaryNodesRecur(n.GetRight(), lastId);
		}
		
		return lastId;
	}
	
	// Write out nodes nX = [label="Z"]
	private String IdentifiersToDot(BinaryNode n, String s)
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
	private String BinaryNodesToDot(BinaryNode parent, BinaryNode child, String s)
	{
		if (child != null)
		{
			s += "n"+parent.GetId()+"->n"+child.GetId()+";\n";
			
			s = BinaryNodesToDot(child, child.GetLeft(), s);
			s = BinaryNodesToDot(child, child.GetRight(), s);
		}
		
		return s;
	}
}