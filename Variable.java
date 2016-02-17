import java.util.ArrayList;
public class Variable
{
    private Object name, variableName, value, originValue;
    private Double angle, originAngle;
    private String units;
    private ArrayList<Variable> oldVariables = new ArrayList<Variable>();
    private boolean isConstant, isEnvironmental, isVisibleVector = false, isSolved;

    public Variable(Object n, Object variableSymbol, Object vaar, boolean isConstant, boolean isEnvironmental)
    {
        name = n.toString().split(" -")[0];
        try{units = n.toString().split("-")[1];}catch(Exception e){units = "";}
        variableName = variableSymbol;
        value = vaar;
        angle = null;
        this.isConstant = isConstant;
        if(isConstant)
            isSolved = true;
        this.isEnvironmental = isEnvironmental;
        originValue = vaar;
    }

    /**
     * This adds the variables of the same type which are deleted and added to this original
     * 
     * @param v The added component that was deleted
     */
    public void addOldVariable(Variable v)
    {  
        if(isKnown() && oldVariables.size() == 0)
            oldVariables.add(copy());

        if(v.getAngle() == null)
            v.setAngle(getAngle());

        oldVariables.add(v);
    }

    public void setAllOldVariables(ArrayList<Variable> v)
    {
        oldVariables = v;
    }

    public ArrayList<Variable> getAllComponents()
    {
        if(isKnown() && oldVariables.size() == 0)
            oldVariables.add(copy());
        return oldVariables;
    }

    public Object getName()
    {
        return name;
    }

    public boolean isEnvironmental()
    {
        return isEnvironmental;
    }

    public boolean isConstant()
    {
        return isConstant;
    }

    public boolean isSolved()
    {
        return isSolved;
    }

    public boolean equals(Variable v)
    {
        return (v.getAngle().equals(getAngle()) && v.getValue().equals(getValue()) && v.getVariableName().equals(getVariableName()) && v.getName().equals(getName()) && v.isConstant()==isConstant() && v.isEnvironmental()==isEnvironmental());
    }

    public Variable copy()
    {
        Variable whyCantIClone = new Variable(getName(), getVariableName(),getValue(),isConstant(),isEnvironmental());
        whyCantIClone.setSolved(isSolved());
        whyCantIClone.setUnits(getUnits());
        whyCantIClone.setAngle(getAngle());
        return whyCantIClone;
    }

    public void setSolved(boolean s)
    {
        isSolved = s;
    }

    public void setConstant(boolean c)
    {
        isConstant = c;
    }
    
    public void setEnvironmental(boolean c)
    {
        isEnvironmental = c;
    }

    public void setAngle(Double a)
    {
        if(angle == null) originAngle = a;
        angle = a;
    }

    public Double getOriginalAngle()
    {
        return originAngle;
    }

    public Double getAngle()
    {
        return angle;
    }

    public Object getVariableName()
    {
        return variableName;
    }

    public Object getOriginalValue()
    {
        return originValue;
    }

    public Object getValue()
    {
        return value;
    }

    public String getUnits()
    {
        return units;
    }

    public void setVandA(Object o, Double a)
    {
        setValue(o);
        if(angle == null) originAngle = a;
        angle = a;
    }

    public void setValue(Object o)
    {
        if(o.toString().indexOf(name.toString()) > -1)
            value = value;
        else
        {
            value = o;
        }
    }

    public void resetValue()
    {
        value = name;
    }

    public void setName(String n)
    {
        name = n;
    }

    public void setVariableName(String n)
    {
        variableName = n;
    }

    public void setUnits(String u)
    {
        units = u;
    }

    public static boolean canBeDouble(String v)
    {
        try{
            Double.parseDouble(v);
        }catch(Exception e){return false;}
        return true;
    }

    public boolean isKnown()
    {
        return (!(value.toString().equals(name.toString().trim())));
    }

    /**
     * Allows for dynamic changing of variable by replacing one of its old components with a newer version,
     * and then calculates the new value of it. Does so solely based off of a value and an angle
     * 
     * @param varToReplac - the variable that's being replaced
     * @param angleToChange - the angle of the old variable that will be replaced
     * @param newValue - the new value of the varaible
     * @param newAnlge - the new value of the angle
     */
    public boolean dynamicallyChange(Object valueToChange, Double angleToChange, Object newValue, Double newAngle)
    {
        boolean goOn = true;
        boolean changedAnything = false;

        if(oldVariables.size() == 0)
            oldVariables.add(copy());
        //Replacing the old Variable
        for(Variable old: oldVariables)
        {
            if(anglesMatch(old.getAngle(), angleToChange) &&valuesMatch(old.getValue(), valueToChange))
            {
                old.setVandA(newValue,newAngle);
                changedAnything = true;
            }

            if(!canBeDouble(old.getValue()+""))
                goOn =false;
        }

        //Allows to change variables that are set to time constants
        if(!goOn || !changedAnything)
            return false;

        //Recalculating the new values
        Double yValues = 0.0;
        Double xValues = 0.0;
        for(Variable all: oldVariables)
        {
            yValues += (Double.parseDouble(all.getValue()+"") * Math.sin(Math.toRadians(all.getAngle())));
            xValues += (Double.parseDouble(all.getValue()+"") * Math.cos(Math.toRadians(all.getAngle())));
        }
        //Final Math Stuffs
        value = Math.sqrt(yValues*yValues + xValues*xValues);
        angle = Math.toDegrees(Math.atan2(yValues,xValues));
        return true;
    }

    /**
     * This methods will check two angles and see if they are the same, up to two decimal places if they aren't exact (e.g. -90 and 270)
     * 
     * @param a - One of the angles
     * @param b - The other angle
     */
    public static boolean anglesMatch(Double a, Double b)
    {
        if(a == null && b == null) return true;
        else if(a == null || b == null) return false;
        a = (double)Math.round(a * 100.0) / 100.0;
        b = (double)Math.round(b * 100.0) / 100.0;
        if(a<0) a+=360.0;
        if(b<0)b+=360.0;

        if(a.equals(b)) return true;
        else if(Math.tan(a) == Math.tan(b))return true;
        return false;
    }
    
    public static boolean valuesMatch(Object a, Object b)
    {    
        if(a == null && b == null) return true;
        else if(a == null || b == null) return false;
        else if(a.equals(b)) return true;
        if(!(canBeDouble(a+"") && canBeDouble(b+""))) return false;
        
        //Rounding to 3 decinmal places
        double a2 = Double.parseDouble(a+"");
        double b2 = Double.parseDouble(b+"");
        a2 = (double)Math.round(a2 * 1000.0) / 1000.0;
        b2 = (double)Math.round(b2 * 1000.0) / 1000.0;
        
        if(a2 == b2) return true;
        return false;
    }
}
