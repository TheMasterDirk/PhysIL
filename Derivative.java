import java.util.ArrayList;

public class Derivative
{
    String equation;
    char dx;    
    ArrayList<String> variables = new ArrayList<String>();

    public Derivative(Equation eq, String dx)
    {
        equation = eq.getEqNoEquals();
        this.dx = 'x';
        variables = eq.getRequiredVars();
        for(String v : variables)
        {
            
            if(v.equals(dx)) this.dx = (char)(variables.indexOf(v)+74);
            equation = equation.replace(v, (char)(variables.indexOf(v)+74)+"");
        }
    }
    
    public String getEquation()
    {
        return equation;
    }

    public char getTerm()
    {
        return dx;
    }

    public void setEquation(String eq)
    {
        equation = eq;
    }

    public void setTerm(char dx)
    {
        this.dx = dx;
    }
    
    public String getDerivative()
    {
        String derek = getDerivative(equation, dx);
        String tmp = "";
        for(Character c : derek.toCharArray())
        {
            if(c > 73 && c < 100)
            {
                tmp += variables.get(c-74);
            }
            else
                tmp += c;
        }
        return tmp;
    }

    /**
     * Gets derivative of any function by splitting it up and relaying parts to different methods
     * @param function The original function to be passed in
     * @return the derivative of function
     */
    public String getDerivative(String function, char dx)
    {
        // Step 1 - Replace all "-" with "+-1*" 
        function = function.replace("+-1*", "-");
        function = function.replace("+(-1)*", "-");
        function = function.replace("-", "+-1*");
        String f1 = "";

        // Step 2 - Split by "+" outside of parenthesis
        ArrayList<String> subequations = new ArrayList<String>();
        int parenthesisDepth = 0;
        String tmp = "";
        for(Character c : function.toCharArray())
        {
            if(c == '(' || c == '[') parenthesisDepth++;
            else if(c == ')' || c == ']') parenthesisDepth--;

            if(c == '+' && parenthesisDepth == 0){ subequations.add(tmp); tmp = ""; }
            else tmp += c;
        }
        subequations.add(tmp);

        // Step 3 - Group everything into parenthesis
        for(String currentPart : subequations)
        {
            int bracketDepth = 0;
            String pNp = "";
            char nextC=' ';
            if(currentPart.length() == 1) subequations.set(subequations.indexOf(currentPart), "("+currentPart+")");
            else
            {
                for(int i = 1; i < currentPart.length()-1; i++)
                {
                    char c = currentPart.charAt(i);
                    char prevC = currentPart.charAt(i-1);
                    nextC = currentPart.charAt(i+1);
                    if(pNp.equals("")) pNp += prevC;
                    if(i == 1 && prevC == '[') bracketDepth++;
                    if(i == currentPart.length()-1 && nextC == ']') bracketDepth--;
                    if(c == '[') bracketDepth++;
                    else if(c == ']') bracketDepth--;
                    if((nextC != ')' && prevC != '(') && bracketDepth == 0 && (c == '*' || c == '/' || c == '+')) pNp += ")" + c + "(";
                    else
                        pNp += c;
                }
                pNp += nextC;
                if(!pNp.substring(0,1).equals("(") || !pNp.substring(pNp.length()-1).equals(")"))pNp = "("+pNp+")";
                subequations.set(subequations.indexOf(currentPart), pNp);
            }
        }

        // Removes extra ()S
        for(String sub : subequations)
        {
            parenthesisDepth = 0;
            int index = subequations.indexOf(sub);
            for(Character c : sub.toCharArray())
            {
                if(c == '(') parenthesisDepth++;
                else if(c == ')') parenthesisDepth--;
                if(parenthesisDepth < 0)
                {
                    subequations.set(index, sub.replace(")*(", "*"));
                }
            }
        }
        
        // Step 4 - Group off subequations based on whether there is a "/" before the opening parenthesis.
        for(String currentPart : subequations)
        {
            ArrayList<String> nums = new ArrayList<String>();
            ArrayList<String> dens = new ArrayList<String>();
            parenthesisDepth = 0;
            boolean isNumerator = true;
            tmp = "";
            for(Character c : currentPart.toCharArray())
            {
                if(c == '(' || c == '[') parenthesisDepth++;
                else if(c == ')' || c == ']') parenthesisDepth--;
                if(parenthesisDepth == 0 && isNumerator && !tmp.equals("")) 
                {
                    if(tmp.substring(0,1).equals("*") || tmp.substring(0,1).equals("/")) tmp = tmp.substring(1);
                    nums.add(tmp+c); tmp = "";
                }
                else if(parenthesisDepth == 0 && !isNumerator && !tmp.equals("")) 
                {
                    if(tmp.substring(0,1).equals("*") || tmp.substring(0,1).equals("/")) tmp = tmp.substring(1);
                    dens.add(tmp+c); tmp = "";
                }
                else tmp += c; 
                if(c == '/' && parenthesisDepth == 0) isNumerator = false;
                else if(c == '*' && parenthesisDepth == 0) isNumerator = true;
            }

            // Remove extra ()s
            for(String n : nums)
            {
                if(n.substring(0,1).equals("(") && n.substring(n.length()-1).equals(")")){
                    n = n.substring(1,n.length()-1);
                    nums.set(nums.indexOf("("+n+")"), n);
                }
            }
            for(String n : dens)
            {
                if(n.substring(0,1).equals("(") && n.substring(n.length()-1).equals(")")){
                    n = n.substring(1,n.length()-1);
                    dens.set(dens.indexOf("("+n+")"), n);
                }
            }

            // Step 5a - If there are any subequation denominators
            if(dens.size() > 0)
                f1 += quotientRule(join(nums, "*"), join(dens, "*"), dx);
            else
            {
                if(nums.size() == 1 && !nums.get(0).contains("[")) f1 += powerRule(nums.get(0), dx);
                else if(nums.size() == 1)
                {
                    f1 += deriveFunction(nums.get(0).substring(0,3), nums.get(0).substring(4,nums.get(0).lastIndexOf("]")), dx);
                }
                else
                    f1 += productRule(nums, dx);
            }
        }
        return f1;
    }

    public int getAppearances(String str, char chr)
    {
        int counter = 0;
        for(Character c : str.toCharArray()) 
        {
            if(c == chr) 
            {
                counter++;
            } 
        }
        return counter;
    }

    /**
     * Small utility method to join ArrayLists together with a delimiter
     */
    public String join(ArrayList<String> arr, String s)
    {
        String tmp = "";
        for(String str : arr)
        {
            tmp += str+s;
        }
        try{
            return tmp.substring(0,tmp.lastIndexOf(s));
        }catch(Exception e){return "";}
    }

    /**
     * Derives the equation parts using the power rules.
     * @param fx the polynomial equation
     * @param dx the variable to derive in terms of
     */
    public String powerRule(String fx, char dx)
    {
        String f1 = "";
        double power = 0, constant = 0, constant1 = 1;
        String constantString = "";
        int pc = 0;
        for(Character c : fx.toCharArray())
        {
            constantString = constant+"";
            if(constantString.contains("E")) constantString = constantString.substring(0,constantString.indexOf("E")+1);
            if(c == dx) power++;
            else if(c == '.'){pc = constantString.length()-1;}
            else if(Character.isDigit(c)){constant*=10; constant+=c-48;}
            else if(c == '*' && constant != 0){if(pc != 0)constant /= Math.pow(10,constantString.length()-pc-1); constant1 *= constant; constant = 0;}
            else if(c == '+' || c == '-') 
            {
                constant /= Math.pow(10,constantString.length()-pc-1);
                if(constant != 0)constant1 *= constant;
                f1+=(constant1*power)+"*";
                for(int i = 0; i < power-1; i++) f1+=dx+"*";
                f1 = f1.substring(0,f1.length()-1);
                power = 0; 
                constant1 = 1;
                constant = 0;
                pc = 0;
                f1+=c;  
            }
        }

        constant /= Math.pow(10,constantString.length()-pc-1);
        if(constant != 0)constant1 *= constant;
        f1+=(constant1*power)+"*";
        for(int i = 0; i < power-1; i++) f1+=dx+"*";
        f1 = f1.substring(0,f1.length()-1);

        if(f1.equals("")) return "0";
        if(f1.substring(f1.length()-1).equals("+") || f1.substring(f1.length()-1).equals("-")) f1 = f1.substring(0,f1.length()-1);
        return f1;
    }

    public String quotientRule(String hi, String lo, char dx)
    {
        return "("+lo+"*"+getDerivative(hi, dx)+"-"+hi+"*"+getDerivative(lo, dx)+")/("+lo+"*"+lo+")";
    }

    public String productRule(ArrayList<String> functions, char dx)
    {
        String tmp = "";
        ArrayList<String> tmpArr = new ArrayList<String>();
        ArrayList<String> outerArr = new ArrayList<String>();
        for(int i = 0; i < functions.size(); i++)
        {
            String fx = functions.get(i);
            tmpArr = new ArrayList<String>();
            tmpArr.add(getDerivative(fx, dx));
            for(int j = 0; j < functions.size(); j++)
            {
                String gx = functions.get(j);
                if(i!=j) tmpArr.add(gx);
            }
            outerArr.add(join(tmpArr, "*"));
        }
        return "("+join(outerArr, "+")+")";        
    }

    /**
     * Derives equation parts in the form fx[gx] -> fx'[gx]*gx' given that fx is a function of gx
     * @param fx the function in the form: "cos" or "lg3"
     * @param gx the inner function
     */
    public String deriveFunction(String fx, String gx, char dx)
    {
        String f1 = "";
        if(fx.equals("cos")) 
            f1 = "-1*sin["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("sin"))
            f1 = "cos["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("tan"))
            f1 = "sec["+gx+"]*sec["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("sec"))
            f1 = "sec["+gx+"]*tan["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("csc"))
            f1 = "-1*csc["+gx+"]*cot["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("cot"))
            f1 = "-1*csc["+gx+"]*csc["+gx+"]*"+getDerivative(gx, dx);
        else if(fx.equals("asn"))
            f1 = getDerivative(gx, dx)+"/sqt[1-"+gx+"*"+gx+"]";
        else if(fx.equals("acs"))
            f1 = "-1*"+getDerivative(gx, dx)+"/sqt[1-"+gx+"*"+gx+"]";
        else if(fx.equals("atn"))
            f1 = getDerivative(gx, dx)+"/(1+"+gx+"*"+gx+")";
        else if(fx.equals("log"))
            f1 = getDerivative(gx, dx)+"/"+gx;
        else if(fx.substring(0,2).equals("lg"))
            f1 = getDerivative(gx, dx)+"/"+gx+"/log["+fx.substring(2)+"]";
        else if(fx.equals("sqt"))
            f1 = getDerivative(gx, dx)+"/(2*"+fx+"["+gx+"])";
        else
            f1 = "";
        return f1;
    }
}