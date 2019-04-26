package ep;

public class Alu
{
    static boolean overflow  = false;
    static boolean underflow = false;
    static boolean sign      = false;
    
    static boolean[] SumBit(boolean bitA, boolean bitB, boolean cin)
    {
        int aux = 0;
        boolean[] output = new boolean[2];
        
        if(bitA)
            aux++;
            
        if(bitB)
            aux++;
            
        if(cin)
            aux++;
            
        switch (aux){
            case 0:
                output[1] = false;
                output[0] = false;
                break;
            case 1:
                output[1] = false;
                output[0] = true;
                break;
            case 2:
                output[1] = true;
                output[0] = false;
                break;
            case 3:
                output[1] = true;
                output[0] = true;
                break;
        }
        
        return output;
    }
    
    static BinInt SumInt(BinInt intA, BinInt intB, boolean cin)
    {
        boolean   signA  = intA.bits[intA.length - 1];
        boolean   signB  = intB.bits[intB.length - 1];
        boolean[] sum    = new boolean[intA.length];
        boolean[] sumbit = new boolean[2];
        sumbit[0] = false;
        sumbit[1] = cin;
        
        for(int i = 0; i < intA.length; i++)
        {
            sumbit = SumBit(intA.bits[i], intB.bits[i], sumbit[1]);
            sum[i] = sumbit[0];
        }
        
        if((signA == signB) && (sum[sum.length -1] != signA))
            overflow = true;
        else
            overflow = false;
        
        if(sum[sum.length -1])
            sign = true;
        else
            sign = false;
        
        return new BinInt(sum);
    } 
    
    static BinInt TwoComplement(BinInt binA)
    {
        BinInt bin = new BinInt(binA.bits.clone());
        
        for(int i = 0; i < bin.length; i++)
            bin.bits[i] = !bin.bits[i];
            
        bin = SumInt(bin, new BinInt(0b1, bin.length), false);    
        return bin;
    }   
    
    static BinInt SubInt(BinInt intA, BinInt intB, boolean cin)
    {
        return SumInt(intA, TwoComplement(intB), cin);
    }
    
    static BinInt DivInt(BinInt intA, BinInt intB) throws Exception
    {
        if(intB.ToDec() == 0)
        {
            throw new Exception("Divisao por zero");
        }        
        
        BinInt  remainder   = new BinInt(intA.bits.clone());
        BinInt  auxB        = new BinInt(intB.bits.clone());
        
        //XOR dos sinais de intA e de intB, configurara o sinal final
        boolean final_sign  = (intA.bits[intA.bits.length - 1] ^ intB.bits[intB.bits.length - 1]); 
        
        //se negativo, vira positivo
        if(remainder.bits[remainder.bits.length - 1])
            remainder = TwoComplement(remainder);
        
        //se negativo, vira positivo
        if(auxB.bits[auxB.bits.length - 1])
            auxB = TwoComplement(auxB);
        
        int quotient = 0;
        sign = false;
        
        //subtrai ate ficar menor que zero
        while(!sign)
        {
            remainder = SubInt(remainder, auxB, false);
            quotient++;
        }
        
        BinInt result;
        
        //retorna com sinal negativo        
        if (final_sign)
            result = TwoComplement(new BinInt(quotient - 1, intA.length));
        else
            result = new BinInt(quotient - 1, intA.length);
        
        if(result.bits[result.bits.length - 1] != final_sign)
            overflow = true;
        else
            overflow = false;
        
        return result;
    }
    
    static boolean[] shiftR(boolean[] valor) {
        
        boolean[] shift = new boolean[valor.length];
        boolean sticky = valor[valor.length - 1];
        
        for(int i = 1; i < valor.length; i++)
        {
            shift[i-1] = valor[i];
        }
        
        shift[shift.length - 1] = sticky;
        
        return shift;       
    }
    
    static boolean[] shiftL(boolean[] valor)
    {
        boolean[] shift = new boolean[valor.length];
        
        for(int i = valor.length - 1; i > 0; i--)
            shift[i] = valor[i-1];
        
        shift[shift.length - 1] = false;
        
        return shift;
    }
            
    static BinInt RemoveBitZero(BinInt intA)
    {
        boolean[] bin = new boolean[intA.length - 1];
        for(int i = 0; i < bin.length; i++)
            bin[i] = intA.bits[i + 1];
            
        return new BinInt(bin);
    }
    
    static BinInt MultInt(BinInt intA, BinInt intB) 
    {

        BinInt product = new BinInt(0, intA.length*2 + 1);
        BinInt add     = new BinInt(0, intA.length*2 + 1);
        BinInt sub     = new BinInt(0, intA.length*2 + 1);
        BinInt compl   = TwoComplement(intA);
        
        //XOR dos sinais de intA e de intB, configurara o sinal final
        boolean final_sign  = (intA.bits[intA.bits.length - 1] ^ intB.bits[intB.bits.length - 1]); 
        
        for(int pos = 0; pos < intA.length; pos++)
            add.bits[intA.length + 1 + pos] = intA.bits[pos];
        for(int pos = 0; pos < compl.length; pos++)
            sub.bits[intA.length + 1 + pos] = compl.bits[pos];
        for(int pos = 0; pos < intA.length; pos++)
            product.bits[product.length - intA.length - 1 - pos] = intB.bits[intB.length - 1 - pos];
        
        product.bits[0] = false;
        
        for(int pos = 0; pos < intA.length; pos++)
        {
            if(!product.bits[1] && product.bits[0])
            {
                product = SumInt(product, add, false);      
            }    
            else if(product.bits[1] && !product.bits[0])
            { 
                product = SumInt(product, sub, false);   
            }
            
            product.bits = shiftR(product.bits);
        }
        
        if(product.bits[product.bits.length - 1] != final_sign)
            overflow = true;
        else
            overflow = false;
        
        return RemoveBitZero(product);
    }
    
    static BinFloat NormalizeFloat(BinFloat floatA)
    {
    	//TODO
    	return floatA;
    }
    
    static BinFloat RoundFloat(BinFloat floatA)
    {
    	//TODO
    	return floatA;
    }
    
    static BinFloat MultFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
    	boolean sign  = (floatA.sign ^ floatB.sign); 
    	BinInt  exp   = null;
    	BinInt  signi = null;
    	
    	if(floatA.isZero() || floatB.isZero())
    		return new BinFloat(0, 0, 0);
    	
    	exp = SumInt(floatA.exponent, floatB.exponent, false);
    	
    	if(overflow)
    		throw new Exception("Overflow no expoente");
    	
    	exp = SubInt(floatA.exponent, new BinInt(BinFloat.excess, BinFloat.exponent_size), false);
    	if(overflow)
    		throw new Exception("Underflow no expoente");
    	
    	return RoundFloat(NormalizeFloat(new BinFloat(sign, exp, signi)));
    }
    
    static BinFloat DivFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
    	boolean sign  = (floatA.sign ^ floatB.sign);
    	BinInt  exp   = null;
    	BinInt  signi = null;
    	
    	if(floatA.isZero())
    		return new BinFloat(0, 0, 0);
    	
    	if(floatB.isZero())
            throw new Exception("Divisao por zero");
    	
    	exp = SubInt(floatA.exponent, floatB.exponent, false);
    	if(overflow)
    		throw new Exception("Underflow no expoente");
    	
    	exp = SumInt(floatA.exponent, new BinInt(BinFloat.excess, BinFloat.exponent_size), false);
    	if(overflow)
    		throw new Exception("Overflow no expoente");
    	
    	return RoundFloat(NormalizeFloat(new BinFloat(sign, exp, signi)));
    }
    
    //NEEDS TESTING
    static int AGreaterB(BinInt intA, BinInt intB)
    {
    	//Iguais
    	int bigger = 1;
    	
    	for(int i = 0; i < intA.length; i++)
    	{
    		//Maior
    		if(intA.bits[i] && !intA.bits[i])
    			bigger = 2;
    		
    		//Menor
    		if(!intA.bits[i] && intA.bits[i])
    			bigger = 0;
    	}
    	
    	return bigger;
    }
    
    static BinFloat SumFloat(BinFloat floatA, BinFloat floatB, boolean subtract) throws Exception
    {
    	boolean sign  = false;
    	BinInt  exp   = null;
    	BinInt  signi = null;
    	
    	
    	if(floatA.isZero())
    		return floatB;
    	
    	if(floatB.isZero())
    		return floatA;
    	
    	BinFloat bigger_exp;
    	BinFloat smaller_exp;
    		
    	//if(floatA.exponent.ToDec() >= floatB.exponent.ToDec() || floatA.exponent.ToDec() == -1)
    	if(AGreaterB(floatA.exponent, floatB.exponent) > 0)
    	{
    		bigger_exp  = floatA;
    		smaller_exp = floatB;
    	}
    	else
    	{
    		bigger_exp  = floatB;
    		smaller_exp = floatA;
    	}
    	
    	1000
    	
    	while(AGreaterB(floatA.exponent, floatB.exponent) != 0)
    	{
    		smaller_exp.exponent = SumInt(smaller_exp.exponent, new BinInt(1, BinFloat.exponent_size), false);
    		smaller_exp.mantissa.bits = shiftR(smaller_exp.mantissa.bits);
    	}
    	  	
    	return RoundFloat(NormalizeFloat(new BinFloat(sign, exp, signi)));
    }
}