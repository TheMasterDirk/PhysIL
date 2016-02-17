import java.util.ArrayList;
import java.util.Arrays;  
import java.util.Scanner;
import java.io.File;

public class Calculate
{
    private Figure basic;
    private String solveFor;
    private ArrayList<Equation> equations;
    private Equation currEq;

    /**
     * @param base The figure to calculate on
     * @param solve The variable to solve for
     */
    public Calculate(Figure base, String solve)
    {
        basic = base;
        equations = getFormulasFromFile();
        setUnknownVariable(solve.trim());
    }

    public Calculate(Figure base)
    {
        basic = base;
        equations = getFormulasFromFile();
        setUnknownVariable("mass");
    }

    /**
     * Reads all formulas fromthe file
     * 
     * @return A list of Equations
     */
    public ArrayList<Equation> getFormulasFromFile()
    {
        ArrayList<Equation> vvv = new ArrayList<Equation>();
        try
        {
            Scanner forms = new Scanner(new File("formulas3.txt"));
            while(forms.hasNext())
            {
                String currForm  = forms.nextLine();
                vvv.add(new Equation(new ArrayList<String>(Arrays.asList(currForm.split(",")))));
            }
        }
        catch(Exception e) {e.printStackTrace();}
        return vvv;
    }

    public ArrayList<Equation> getAllFormulas()
    {
        return equations;
    }

    public Equation getCurrEq()
    {
        return currEq;
    }

    public void setUnknownVariable(String solveFor)
    {
        this.solveFor = solveFor.trim();
        int uk = 99999;
        Equation blank = currEq;
        for(Equation e: equations)
        {
            if(e.countUnknowns(basic) < uk && e.hasVariable(solveFor))
                blank = e;
        }
        currEq = blank;
    }

    public Equation getEquationToUse()
    {
        for(Equation e: equations)
        {
            if(e.hasVariable(solveFor))
            {
                e.switchBase(solveFor);
                boolean use = true;
                for(String parts:e.getRequiredVars())
                {
                    if(basic.getVar(parts) == null || parts.equals(solveFor))
                        use = false;
                    else if(!basic.getVar(parts).isKnown())
                        use = false;
                }
                if(use) return e;
            }
        }
        return null;
    }

    public boolean solve()
    {
        Equation solutionGuide = getEquationToUse();
        if(solutionGuide == null)
            return false;

        //gettting equation to use ***IF MORE THAN 1 EQUATION HAS SOLVEFOR, THIS METHOD WILL TAKE FIRST EQUATION***
        currEq = solutionGuide;
        currEq.switchBase(solveFor);
        //determining vectors or not
        String result = "";
        Double angle = basic.getVar(solveFor).getAngle();
        if(currEq.isVector(basic))
        {
            ArrayList<String> directions = solutionGuide.getCompForms();
            ArrayList<String> ans = new ArrayList<String>(2);
            for(int index = 0; index < directions.size(); index++)
            {
                String modified = variableReplaceBefore(directions.get(index));
                ans.add(index,findValueOfEquation(modified));
            }
            Double tmpResult = 0.0;
            for(String comp:ans)
            {
                if(Variable.canBeDouble(comp))
                    tmpResult += Double.parseDouble(comp) * Double.parseDouble(comp);
                else
                    return false;
            }
            result = Math.sqrt(tmpResult)+"";
            angle = Math.toDegrees(Math.atan2(Double.parseDouble(ans.get(0)),Double.parseDouble(ans.get(1))));
        }
        else
        {
            result = findValueOfEquation(variableReplaceBefore(solutionGuide.getEq()));
        }
        
        if(Variable.canBeDouble(result))
        {
            basic.getVar(solveFor).setVandA(result,angle);
            basic.getVar(solveFor).setSolved(true);
        }
        return true;
    }

    public String findValueOfEquation(String eq)
    {
        String partToMessWith = eq;
        boolean go = true;
        while(partToMessWith.lastIndexOf("[") >= 0 && go)
        {
            String original = partToMessWith;
            String fx = partToMessWith.substring(partToMessWith.lastIndexOf("[")-3,partToMessWith.lastIndexOf("["));
            partToMessWith = partToMessWith.substring(0,partToMessWith.lastIndexOf("[")-3) + partToMessWith.substring(partToMessWith.lastIndexOf("["));

            int index2 = partToMessWith.lastIndexOf("[");
            String s2 = partToMessWith.substring(index2+1,partToMessWith.substring(index2).indexOf("]")+index2);
            String ns2 = partToMessWith.substring(0,partToMessWith.lastIndexOf("["+s2+"]"))+partToMessWith.substring(partToMessWith.lastIndexOf("["+s2+"]")+s2.length()+2);
            //Here, we decide whether or not we need to sovle before running the fucntion, or we need the actual variable name
            if(fx.equals("dot") || fx.equals("crs") || fx.equals("ang")|| fx.equals("der")) // <-- Inner function non-numerical
                partToMessWith = ns2.substring(0,index2) + solveFunc(fx,s2)+ns2.substring(index2);
            else
                partToMessWith = ns2.substring(0,index2) + solveFunc(fx,findValueOfEquation(s2))+ns2.substring(index2);
            if(partToMessWith.equals(original))
                go = false;
        }
        go = true;

        while(partToMessWith.lastIndexOf("(") >=0 && go)
        {
            String original  = partToMessWith;
            int indx = partToMessWith.lastIndexOf("(");
            String solve = partToMessWith.substring(indx+1,partToMessWith.substring(indx).indexOf(")")+indx);
            //Got to replace replace
            //String nosolve = partToMessWith.replace("("+solve+")","");
            String nosolve = partToMessWith.substring(0,indx)+partToMessWith.substring(indx+solve.length()+2);
            String valueToGet = getFinalVarValue(solve,"");
            partToMessWith = nosolve.substring(0,indx)+getValue(valueToGet)+nosolve.substring(indx);
            if(partToMessWith.equals(original))
                go = true;
        }

        return getValue(getFinalVarValue(partToMessWith,""));
    }

    /**
     * This parses functions such as trigonometrics(in degrees), 
     * various roots, and others.
     * 
     * @param fx The function in question
     * @param value The parameter of fx
     * @return The function's value
     */
    public String solveFunc(String fx, String value)
    {
        try{
            //Value is non numeric cases
            if(fx.equals("ang"))
                return ""+basic.getVar(value).getAngle();
            else if(fx.equals("dot"))
            {
                String[] products = value.split("\\*");
                return "("+value+"*cos[ang["+products[0]+"]-ang["+products[1]+"]])";
            }
            else if(fx.equals("crs"))
            {
                String[] products = value.split("\\*");
                return "("+value+"*sin[ang["+products[0]+"]-ang["+products[1]+"]])";
            }
            else if(fx.equals("der"))
            {
                Equation e = new Equation("null="+variableReplaceBefore(value));
                Derivative ddx = new Derivative(e, "time");
                return ddx.getDerivative();
            }

            double val = Double.parseDouble(getValue(value));
            if(fx.equals("sqt"))
                return ""+Math.sqrt(val);
            else if(fx.equals("cos"))
                return ""+Math.cos(Math.toRadians(val));
            else if(fx.equals("sin"))
                return ""+Math.sin(Math.toRadians(val));
            else if(fx.equals("tan"))
                return ""+Math.tan(Math.toRadians(val)); 
            else if(fx.equals("sec"))
                return ""+(1/Math.cos(Math.toRadians(val)));
            else if(fx.equals("cot"))
                return ""+(1/Math.tan(Math.toRadians(val)));
            else if(fx.equals("csc"))
                return ""+(1/Math.sin(Math.toRadians(val)));
            else if(fx.equals("log"))
                return ""+Math.log(val);
            else if(fx.substring(0,2).equals("lg"))
                return ""+Math.log(val)/Math.log(Double.parseDouble(fx.substring(2)));
        }catch(Exception e){e.printStackTrace(); return fx+"["+value+"]";}
        return fx+"["+value+"]";
    }

    public String getValue(String eq)
    {
        // Return eq if double 
        try{
            Double.parseDouble(eq);
            return eq;
        }catch(Exception e){}

        // Operation Division
        // Fixes -- or +-

        for(int i = 0; i < eq.length()-1; i++)
        {
            if(eq.charAt(i) == '-' && eq.charAt(i+1) == '+')
                eq = eq.substring(0,i+1)+eq.substring(i+2);
            else if(eq.charAt(i) == '-' && eq.charAt(i+1) == '-')
                eq = eq.substring(0,i)+"+"+eq.substring(i+2);
            else if(eq.charAt(i) == '+' && eq.charAt(i+1) == '-')
                eq = eq.substring(0,i)+eq.substring(i+1);
        }

        //Case for extra stuff that prohibits solving (should be no more of these at this point)
        if(eq.indexOf("[") > -1)
        {
            String eq1 = "";
            String funct = eq.substring(eq.indexOf("[")-3, eq.indexOf("["));
            eq = findValueOfEquation(eq);
        }

        if(eq.indexOf("(") > -1)
            return eq;

        ArrayList<String> tmpArray = Equation.splitEquation(eq);
        for(int i=1; i< tmpArray.size()-1;i++)
        {
            Object result;
            if(tmpArray.get(i).equals("*"))
            {
                try{
                    result = Double.parseDouble(tmpArray.get(i-1)) * Double.parseDouble(tmpArray.get(i+1));
                }catch(Exception e){ result = tmpArray.get(i-1) + "*" + tmpArray.get(i+1);}
                tmpArray.set(i+1, result.toString());
                tmpArray.remove(i-1); tmpArray.remove(i-1);
                i-=2;
            }
            else if(tmpArray.get(i).equals("/"))
            {
                try{
                    result = Double.parseDouble(tmpArray.get(i-1)) / Double.parseDouble(tmpArray.get(i+1));
                }catch(Exception e){result = tmpArray.get(i-1) + "/" +   tmpArray.get(i+1); }
                tmpArray.set(i+1, result.toString());
                tmpArray.remove(i-1);
                tmpArray.remove(i-1);
                i-=2;
            }
        }

        //System.out.println(tmpArray.toString());

        for(int i=1; i < tmpArray.size()-1; i++)
        {
            Object result;
            if(tmpArray.get(i).equals("+"))
            {
                try{
                    result = Double.parseDouble(tmpArray.get(i-1)) + Double.parseDouble(tmpArray.get(i+1));
                }catch(Exception e){result = tmpArray.get(i-1) + "+" + tmpArray.get(i+1); }
                tmpArray.set(i+1, result.toString());   
            }
            else if(tmpArray.get(i).equals("-"))
            {
                try{
                    result = Double.parseDouble(tmpArray.get(i-1)) - Double.parseDouble(tmpArray.get(i+1));
                }catch(Exception e){ result = tmpArray.get(i-1) + "-" +   tmpArray.get(i+1);}
                tmpArray.set(i+1, result.toString());   
            }
        }
        return tmpArray.get(tmpArray.size()-1);
    }

    public String getFinalVarValue(String eq, String origin)
    {
        ArrayList<String> partsOfEq = Equation.splitEquation(eq);

        String depth = "";

        for(String k: partsOfEq)
        {
            Variable v = basic.getVar(k);
            if(origin.equals(""))
                depth = k;
            else
                depth = origin;
            if(v == null || v.canBeDouble(k) || v.getValue().toString().contains(k))
            {/*Dont do anything*/}
            else if(v.canBeDouble(v.getValue().toString()))
            {
                partsOfEq.set(partsOfEq.indexOf(k), Double.parseDouble(v.getValue().toString())+"");
            }
            else
            {
                //Possible Recursion for when a varaible is equal to another variable
                if(!v.getValue().toString().contains(depth))
                {
                    String blanket = v.getValue().toString();
                    partsOfEq.set(partsOfEq.indexOf(k),getFinalVarValue(blanket,depth));
                }
                else
                {
                    if(partsOfEq.size() == 1)
                        return depth;
                    else
                    {
                        String combined = "";
                        for(String part: partsOfEq)
                            combined+=part;
                        return combined; 
                    }
                }
            }
        }
        String combined = "";
        for(String part: partsOfEq)
            combined+=part;
        return combined;
    }

    /**
     * Takes all the varaibles on the figure, and combines them all 
     * into one variable to be used for calculations
     */
    public void combineVariables()
    {
        ArrayList<Variable> allVars = basic.getVars();
        for(int indexOfBase = 0; indexOfBase < allVars.size(); indexOfBase++)
        {
            Variable base = allVars.get(indexOfBase);
            for(int indexOfCheck  = 0; indexOfCheck < allVars.size(); indexOfCheck++)
            {
                Variable check = allVars.get(indexOfCheck);
                // IF VARIABLE DEFINED AS STRING i.e. "time"
                //Why does trim need to be called?
                if(((base.isConstant() ||base.isSolved()) && (check.isConstant()||check.isSolved())) && !(base.getAngle() == null || check.getAngle() == null))
                {
                    //To eliminate the ammount of time spent in recusriveCall/getFinalVarValue
                    String baseV = getFinalVarValue((base.getValue()+"").trim(),"");
                    String checkV = getFinalVarValue((check.getValue()+"").trim(),"");
                    if(base.canBeDouble(baseV) && check.canBeDouble(checkV) && (base.getName()+"").trim().equals((check.getName()+"").trim()) && !(indexOfBase == indexOfCheck))
                    { 
                        base.setValue(baseV);
                        check.setValue(checkV);

                        double finalAnswer = 0.0;
                        Double finalAngle = 0.0;
                        //If the variables have the same angle, they can just be added together. (Even if they are scalars)
                        if(base.getAngle() == check.getAngle())
                        {
                            finalAngle = base.getAngle();
                            finalAnswer = Double.parseDouble(base.getValue()+"") + Double.parseDouble(check.getValue()+""); //Combine into first variable
                        }
                        else
                        {
                            double x = Double.parseDouble(base.getValue().toString()) * Math.cos(Math.toRadians(base.getAngle())) + Double.parseDouble(check.getValue().toString()) * Math.cos(Math.toRadians(check.getAngle()));
                            double y = Double.parseDouble(base.getValue().toString()) * Math.sin(Math.toRadians(base.getAngle())) + Double.parseDouble(check.getValue().toString()) * Math.sin(Math.toRadians(check.getAngle()));
                            finalAnswer = Math.sqrt((x*x) + (y*y));
                            finalAngle = Math.atan2(y,x);
                        }
                        //Combine into second var
                        
                        //Making a new variable that has no ties to check
                        /*if(base.getAllComponents().size() == 0)
                        {
                            Variable whyCantIClone = new Variable(base.getName(), base.getVariableName(),base.getValue(),base.isConstant(),base.isEnvironmental());
                            whyCantIClone.setSolved(base.isSolved());
                            whyCantIClone.setUnits(base.getUnits());
                            whyCantIClone.setAngle(base.getAngle());
                            base.addOldVariable(whyCantIClone);
                        }*/
                        base.addOldVariable(check);
                        //Remove first instance
                        basic.getVars().remove(check);

                        //NEW METHOD FIX REMOVING INCORRECT 
                        ArrayList<Variable> all = basic.getAllSameVars(base.getName()+"");
                        for(Variable opt: all)
                        {
                            if(opt == base)
                            {
                                opt.setVandA(finalAnswer, Math.toDegrees(finalAngle));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public String variableReplaceBefore(String eq)
    {
        String newEq = eq;
        Equation usedOnlyForReq = new Equation(newEq);
        for(String req: usedOnlyForReq.getRequiredVars())
        {
            if(!Variable.canBeDouble(req) && !(!basic.getVar(req).isKnown() || Variable.canBeDouble(basic.getVar(req).getValue()+"")))
            {
                newEq = newEq.replace(req, basic.getVar(req).getValue()+"");
            }
        }
        return newEq;
    }
}