
public class BinFloat
{
	BinInt mantissa;
	BinInt exponent;
    boolean sign;
    
    static int mantissa_size = 23;
    static int exponent_size = 8;
    static int excess        = 127;
    
    BinFloat(int sign, int exp, int significand)
    {
        //binario normal unsigned
        mantissa = new BinInt(significand, mantissa_size);
        
        //excesso de 128, i.e pertence a [-127, 128]
        this.exponent = new BinInt(exp + excess, exponent_size);

        if(sign > 0)
            this.sign = true;
        else
            this.sign = false;
    }
    
    BinFloat(boolean sig,  BinInt exp, BinInt significand)
    {
        mantissa  = significand;
        exponent  = exp;
        sign = sig;
    }
    
    //Transforma em string
    String ToString()
    {   
        boolean[] significand = this.mantissa.bits;
        String s = "";
        
        String signi = "";
        
        for(int i = 0; i < significand.length; i++)
        {
            if(significand[i])
                signi = "1" + signi;
            else
                signi = "0" + signi;
        }
                   
        String expo = "";
        
        for(int i = 0; i < exponent.length; i++)
        {
            if(exponent.bits[i])
                expo = "1" + expo;
            else
                expo = "0" + expo;
        }

        s = expo + signi;
        
        if(sign)
            s = "1" + s;
        else
            s = "0" + s;
        
        return s;
    }
    
    //Transforma em string na forma cientifica
    String ToBinScientific()
    {
        boolean[] significand = this.mantissa.bits;  
        
        String s = "";

        String signi = "";
        
        for(int i = 0; i < significand.length; i++)
        {
            if(significand[i])
                signi = "1" + signi;
            else
                signi = "0" + signi;
        }
        
        String expo = "";
        
        for(int i = 0; i < exponent.length; i++)
        {
            if(exponent.bits[i])
                expo = "1" + expo;
            else
                expo = "0" + expo;
        }

        if(mantissa.ToDec() == 0 && exponent.ToDec() == 0)
        	s = "0." + signi + "e" + expo;
        else
        	s = "1." + signi + "e" + expo;

        if(sign)
            s = "-" + s;
        else
            s = "+" + s;        
            
        return s;
    }
    
    //Se o numero tem valor zero na representacao
    boolean isZero()
    {
    	if(isMantissaZero() && isExponentZero())
    		return true;
            
    	return false;
    }
    
    //Se o valor da mantissa e zero
    boolean isMantissaZero()
    {
        for(int i = 0; i < mantissa.length; i++)
        {
            if(mantissa.bits[i])
                return false;
        }
            
        return true;
    }
    
    //Se o valor do expoente na MEMORIA e zero
    boolean isExponentZero()
    {
        for(int i = 0; i < exponent.length; i++)
        {
            if(exponent.bits[i])
                return false;
        }
            
        return true;
    }
}