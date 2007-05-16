/*
 * ChomskyRules.java
 *
 * Created on 13 mei 2007, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//package regexp;

import java.util.*;

class ChomskyVarList
{
    ArrayList<Character> l;
    
    public ChomskyVarList()
    {
        l = new ArrayList<Character>();
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
        return l.get(index).charValue();
    }
    
    public int GetVarIndex(char varName)
    {
        int varIndex = -1;
        
        for(int i=0; i<l.size(); i++)
        {
            if (l.get(i).charValue() == varName)
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

/**
 *
 * @author neimod
 */
public class ChomskyRules 
{
    private ChomskyVarList varList;
    private ArrayList<ChomskyTerminal> terminalList;
    private ArrayList<ChomskyTwoVar> twoVarList;
    int startIndex;
    
    /** Creates a new instance of ChomskyRules */
    public ChomskyRules() 
    {
        varList = new ChomskyVarList();
        terminalList = new ArrayList<ChomskyTerminal>();
        twoVarList = new ArrayList<ChomskyTwoVar>();    
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
        return terminalList.get(terminalRuleIndex);
    }
    
    public int GetTwoVarRuleCount()
    {
        return twoVarList.size();
    }
    
    public ChomskyTwoVar GetTwoVarRule(int twoVarRuleIndex)
    {
        return twoVarList.get(twoVarRuleIndex);
    }
    
    public void AddVar(char varName)
    {
        varList.AddVar(varName);
    }
    
    private boolean HasArrow(String rule)
    {
        if (rule.length() > 3 && rule.charAt(1) == '-' && rule.charAt(2) == '>')
            return true;
        else
            return false;
    }
    
    public boolean IsRule(int varIndex, char terminal)
    {
        for(int i=0; i<terminalList.size(); i++)
        {
            //System.out.println(""+varList.GetVar(varIndex)+"->"+terminal+" == "+varList.GetVar(terminalList.get(i).GetVarIndex())+"->"+terminalList.get(i).GetTerminal()+"? ");
            if (terminalList.get(i).GetTerminal() == terminal && terminalList.get(i).GetVarIndex() == varIndex)
            {
                //System.out.println("Yes.");
                return true;
            }
        }
        
        return false;
    }
    
    public void AddRule(String rule)
    {
        // We only allow 2 kind of basic rules:
        // A->BC
        // A->a
        
        // A rule can be made up out of multiple basic rules like so:
        // A->BC/a/FG/...

        StringTokenizer st = new StringTokenizer(rule,"/");
        
        if (!st.hasMoreTokens())
        {
            // error
            System.out.println("error: unexpected end of string");
            return;
        }
        
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
        
        if (-1 == startIndex)
        {
        	startIndex = varHeadIndex;
        }

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
            char terminal = headRule.charAt(3);

            terminalList.add(new ChomskyTerminal(varHeadIndex, terminal));
        }
        else
        {
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
        
        while (st.hasMoreTokens()) 
        {
            String subrule = st.nextToken();
            
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
        for(int i=0; i<terminalList.size(); i++)
        {
            ChomskyTerminal t = terminalList.get(i);
            
            char varName = varList.GetVar(t.GetVarIndex());
            
            System.out.println(""+varName+"->"+t.GetTerminal());
        }
    }
    
    public void PrintTwoVarRules()
    {
        for(int i=0; i<twoVarList.size(); i++)
        {
            ChomskyTwoVar t = twoVarList.get(i);
            
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