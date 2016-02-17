import java.util.ArrayList;
public class Equation
{
    private String baseEq;
    private ArrayList<String> eqParts;
    private ArrayList<String> eqForms;
    private ArrayList<String> compForm = new ArrayList<String>(0);;

    public Equation(String form)
    {
        baseEq = form;
        eqParts = splitEquation(form);
        eqForms = new ArrayList<String>();
        eqForms.add(form);
    }

    public Equation(ArrayList<String> forms)
    {
        baseEq = forms.get(0);
        eqParts = splitEquation(baseEq);
        eqForms = forms;
    }

    public String getEq()
    {
        return baseEq;
    }
    
    public String getEqNoEquals()
    {
        return baseEq.split("=")[1];
    }

    public ArrayList<String> getEqParts()
    {
        return eqParts;
    }

    public ArrayList<String> getCompForms()
    {
        return compForm;
    }

    /**
     * This method determines whether or not he base equation needs to do component method or not
     * 
     * @param Figure f A figure is need so that it can be determined, based off figure's variables, whether component method shall be use
     * @return if component method needs to be used
     */
    public boolean isVector(Figure f)
    {
        if(eqParts.contains("dot") || eqParts.contains("crs"))
            return false;
        for(String var: getRequiredVars())
        {
            if(f.getVar(var) != null && f.getVar(var).getAngle() != null)
            {
                updateComponentForms(f);
                return true;
            }
        }
        return false;
    }

    /**
     * Switches the baseEq so that other methods can use differnt versions of the same equation
     * 
     * @param String nameOfVar the name of the variable (in the eqation) used to determine what form an equation will be in
     */
    public void switchBase(String nameOfVar)
    {
        for(String eq: eqForms)
        {
            if(eq.indexOf(nameOfVar) < eq.indexOf("="))
            {
                baseEq = eq;
                eqParts = splitEquation(baseEq);
            }
        }
    }

    /**
     * Gives a list of variable names need to solve an equation. If the equation has any constants listed as NUMBERS, 
     * they will not be returned. (i.e. "Force=Mass-3" will return ["Force","Mass"])
     * 
     * @return An arraylist of strings that correspond to the names of variables needed
     */
    public ArrayList<String> getRequiredVars()
    {
        ArrayList<String> varParts = new ArrayList<String>();
        for(int pos = 1; pos < eqParts.size(); pos++)
        {
            if(eqParts.get(pos).equals("["))
            {
                varParts.remove(varParts.size()-1);
            }
            else if(!(eqParts.get(pos).length() == 1 && !Character.isLetter(eqParts.get(pos).charAt(0))))
            {
                varParts.add(eqParts.get(pos));
            }
        }
        return varParts;
    }

    /**
     * Allows the user to check if this equation can be used to solve for a particular variable
     * 
     * @param name - the name of the variable the user wants to check against
     * @return returns whether the equation can be used to solve for the variable the user wants
     */
    public boolean hasVariable(String name)
    {
        for(String partOf: eqParts)
        {
            if(name.equals(partOf))
                return true;
        }
        return false;
    }

    /**
     * Splits the string passed in by variables and symbols
     * 
     * @return A String arraylist of the parts of the equation (symbols & parentheses included)
     */
    public static ArrayList<String> splitEquation(String eq2)
    {
        String tmpVal = "";
        ArrayList<String> tmpArray = new ArrayList<String>();
        for(int i = 0; i < eq2.length(); i++)
        {
            //case for really small numbers (e.g., 1*E-7)
            if(i < eq2.length()-1 && eq2.charAt(i) == 'E' && eq2.charAt(i+1) =='-')
            {
                tmpVal += eq2.charAt(i)+"";
                tmpVal += eq2.charAt(i+1)+"";
                i++;
            }
            else if(Character.isLetterOrDigit(eq2.charAt(i)) || eq2.charAt(i) == '.' || eq2.charAt(i) == ' ')
                tmpVal += eq2.charAt(i)+"";
            //case for negative numbers
            else if(eq2.charAt(i) == '-' && tmpVal.equals(""))
                tmpVal += eq2.charAt(i);
            else if(!tmpVal.equals(""))
            {
                tmpArray.add(tmpVal);
                tmpVal = "";
                tmpArray.add(eq2.charAt(i)+"");
            }
            else
                tmpArray.add(eq2.charAt(i)+"");
        }
        if(!tmpVal.equals(""))
            tmpArray.add(tmpVal);
        return tmpArray;
    }

    /**
     * 
     * Splits the current form of the equation by variables
     * 
     * @return A String arraylist of the parts of the current equation (symbols & parentheses included)
     */
    public ArrayList<String> splitEquation()
    {
        return splitEquation(baseEq);
    }

    public int countUnknowns(Figure f)
    {
        int unknown = 0;
        for(String v: getRequiredVars())
        {
            //If a variable isnt current in a figure, (e.g. a term with a the "intial" or "final" tag on it), add 1 to unknowns
            if(f!= null && f.getVar(v)==null)
                unknown++;
            else if(f!=null&&!Variable.canBeDouble(f.getVar(v).getValue().toString()))
                unknown++;
        }
        return unknown;
    }

    public void updateComponentForms(Figure f)
    {
        ArrayList<String> parts = new ArrayList<String>(2);
        String[] dire = {"sin","cos"};
        for(String d: dire)
        {
            String tmp = "";
            String angle = "90.0";
            if(d.equals("cos"))
                angle = "0.0";
            ArrayList<String> req = getRequiredVars();
            for(String part: splitEquation())
            {
                if(req.contains(part) && f.getVar(part).getAngle()!=null)
                {
                    tmp = tmp+"("+part+")*"+d+"["+f.getVar(part).getAngle()+"]";
                }
                else if(req.contains(part))
                    tmp = tmp+"("+part+")*"+d+"["+angle+"]";
                else
                    tmp = tmp+part;

            }
            parts.add(tmp);
        }
        compForm = parts;
    }
}