import java.util.*;

class ChomskyVarList
{
    ArrayList l;
    
    public ChomskyVarList()
    {
    	l = new ArrayList();
    }
    
    public int GetSize()
    {
        return l.size();
    }
    
    public void AddVar(char varName)
    {
        l.add(new Character(varName));        
    }
    
    public char GetVar(int index)
    {
    	Character ch = (Character)l.get(index);
        return ch.charValue();
    }
    
    public int GetVarIndex(char varName)
    {
        int varIndex = -1;
        
        for(int i=0; i<l.size(); i++)
        {
            if (GetVar(i) == varName)
            {
                varIndex = i;
                
                break;
            }
        }
        
        return varIndex;
    }
}

class ChomskyTerminal
{
    private int varIndex;
    private char terminal;
    
    public ChomskyTerminal(int nVarIndex, char nTerminal)
    {   
        varIndex = nVarIndex;
        terminal = nTerminal;
    }
    
    public char GetTerminal()
    {
        return terminal;
    }
    
    public int GetVarIndex()
    {
        return varIndex;
    }
}

class ChomskyTwoVar
{
    private int varHeadIndex, varLeftIndex, varRightIndex;
    
    // (head)->(left)(right)
    
    public ChomskyTwoVar(int nVarHeadIndex, int nVarLeftIndex, int nVarRightIndex)
    {
        varHeadIndex = nVarHeadIndex;
        varLeftIndex = nVarLeftIndex;
        varRightIndex = nVarRightIndex;
    }

    public int GetVarHeadIndex()
    {
        return varHeadIndex;
    }
    
    public int GetVarLeftIndex()
    {
        return varLeftIndex;
    }
    
    public int GetVarRightIndex()
    {
        return varRightIndex;
    }
}


public class ChomskyRules 
{
    private ChomskyVarList varList;	// list of all variables
    private ArrayList terminalList; // list of all terminal rules A->a
    private ArrayList twoVarList; 	// list of all twovar rules A->BC
    int startIndex;	// start index
    

    public ChomskyRules() 
    {
        varList = new ChomskyVarList();
        terminalList = new ArrayList();
        twoVarList = new ArrayList();    
        startIndex = -1;
    }
    
    public int GetStartVarIndex()
    {
    	return startIndex;
    }
    
    public int GetVarCount()
    {
        return varList.GetSize();
    }
    
    public int GetTerminalRuleCount()
    {
        return terminalList.size();
    }
    
    public ChomskyTerminal GetTerminalRule(int terminalRuleIndex)
    {
        return (ChomskyTerminal)terminalList.get(terminalRuleIndex);
    }
    
    public int GetTwoVarRuleCount()
    {
        return twoVarList.size();
    }
    
    public ChomskyTwoVar GetTwoVarRule(int twoVarRuleIndex)
    {
        return (ChomskyTwoVar)twoVarList.get(twoVarRuleIndex);
    }
    
    // Add new variable symbol
    public void AddVar(char varName)
    {
        varList.AddVar(varName);
    }
    
    // Check if a string contains "->"
    private boolean HasArrow(String rule)
    {
        if (rule.length() > 3 && rule.charAt(1) == '-' && rule.charAt(2) == '>')
            return true;
        else
            return false;
    }
    
    // Check if the grammar has a rule (varindex)->terminal
    public boolean IsRule(int varIndex, char terminal)
    {
    	int terminalCount = terminalList.size();
    	
        for(int i=0; i<terminalCount; i++)
        {
        	ChomskyTerminal t = GetTerminalRule(i);

        	if (t.GetTerminal() == terminal && t.GetVarIndex() == varIndex)
            {
                return true;
            }
        }
        
        return false;
    }
    
    // Add a new rule
    public void AddRule(String rule)
    {
        // We only allow 2 kind of basic rules:
        // A->BC
        // A->a
        
        // A rule can be made up out of multiple basic rules like so:
        // A->BC/a/FG/...

    	// Cut up the string by /
        StringTokenizer st = new StringTokenizer(rule,"/");
        
        if (!st.hasMoreTokens())
        {
            // error
            System.out.println("error: unexpected end of string");
            return;
        }
        
        // A rule A->BC/c is split up into a head rule A->BC, and a subrule c
        String headRule = st.nextToken();
        
        if (!HasArrow(headRule))
        {
            // error
            System.out.println("error: rule has no arrow. "+headRule);
            return;
        }
        
        char varHeadName = rule.charAt(0);
        int varHeadIndex = varList.GetVarIndex(varHeadName);
        if (varHeadIndex == -1)
        {
            // error 
            System.out.println("error: head variable "+varHeadName+" unknown");
            return;
        }
        
        // First rule added, that makes this the start rule
        if (-1 == startIndex)
        {
        	startIndex = varHeadIndex;
        }

        // Check whether we are dealing with terminal rule or twovar rule
        boolean terminalRule = false;
        if (headRule.length() == 4)
            terminalRule = true;
        else if (headRule.length() != 5)
        {
            // error
            System.out.println("error: "+headRule);
            return;
        }

        if (terminalRule)
        {
        	// Get terminal symbol
            char terminal = headRule.charAt(3);

            terminalList.add(new ChomskyTerminal(varHeadIndex, terminal));
        }
        else
        {
        	// Get left and right symbols
            char varLeftName = headRule.charAt(3);
            char varRightName = headRule.charAt(4);
            
            int varLeftIndex = varList.GetVarIndex(varLeftName);
            int varRightIndex = varList.GetVarIndex(varRightName);

            if (varLeftIndex == -1 || varRightIndex == -1)
            {
                // error
                System.out.println("error: undefined variables");
                return;
            }
            else
            {
                twoVarList.add(new ChomskyTwoVar(varHeadIndex, varLeftIndex, varRightIndex));
            }
        }   
        
        // Now keep adding subrules
        while (st.hasMoreTokens()) 
        {
            String subrule = st.nextToken();
            
            // Check whether this is a terminal rule or twovar rule
            terminalRule = false;
            if (subrule.length() == 1)
                terminalRule = true;
            else if (subrule.length() != 2)
            {
                // error
                System.out.println("error: "+subrule);
                return;
            }

            if (terminalRule)
            {
                char terminal = subrule.charAt(0);

                terminalList.add(new ChomskyTerminal(varHeadIndex, terminal));
            }
            else
            {
                char varLeftName = subrule.charAt(0);
                char varRightName = subrule.charAt(1);

                int varLeftIndex = varList.GetVarIndex(varLeftName);
                int varRightIndex = varList.GetVarIndex(varRightName);

                if (varLeftIndex == -1 || varRightIndex == -1)
                {
                    // error
                    System.out.println("error: undefined variables");
                    return;
                }
                else
                {
                    twoVarList.add(new ChomskyTwoVar(varHeadIndex, varLeftIndex, varRightIndex));
                }
            }   
        }
    }
    
    // Help functions to turn the grammar into strings
    public String TerminalRuleToString(ChomskyTerminal t)
    {
		char varName = varList.GetVar(t.GetVarIndex());
		
		return ""+varName+"->"+t.GetTerminal();   	
    }
    public char VarIndexToChar(int varIndex)
    {
    	return varList.GetVar(varIndex);
    }
    
    public int VarCharToIndex(char var)
    {
    	for(int i=0; i<varList.GetSize(); i++)
    	{
    		if (varList.GetVar(i) == var)
    			return i;
    	}
    	
    	return -1;
    }
    public String TwoVarRuleToString(ChomskyTwoVar t)
    {
	    char varHeadName = varList.GetVar(t.GetVarHeadIndex());
	    char varLeftName = varList.GetVar(t.GetVarLeftIndex());
	    char varRightName = varList.GetVar(t.GetVarRightIndex());
	    
	    return ""+varHeadName+"->"+varLeftName+varRightName;
    }
    
    public void PrintTerminalRules()
    {
    	int terminalCount = terminalList.size();
    	
        for(int i=0; i<terminalCount; i++)
        {
            ChomskyTerminal t = GetTerminalRule(i);
            
            char varName = varList.GetVar(t.GetVarIndex());
            
            System.out.println(""+varName+"->"+t.GetTerminal());
        }
    }
    
    public void PrintTwoVarRules()
    {
    	int twoVarCount = twoVarList.size();
    	
        for(int i=0; i<twoVarCount; i++)
        {
            ChomskyTwoVar t = GetTwoVarRule(i);
            
            char varHeadName = varList.GetVar(t.GetVarHeadIndex());
            char varLeftName = varList.GetVar(t.GetVarLeftIndex());
            char varRightName = varList.GetVar(t.GetVarRightIndex());
            
            System.out.println(""+varHeadName+"->"+varLeftName+varRightName);
        }
    }
    
    public void PrintRules()
    {
        PrintTerminalRules();
        PrintTwoVarRules();
    }
}