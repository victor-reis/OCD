
public class Alu
{
    static boolean overflow  = false;
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
    
    static boolean[] shiftR(boolean[] valor, boolean sticky) {
        
        boolean[] shift = new boolean[valor.length];
        
        if(sticky)
            sticky = valor[valor.length - 1];
        
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
            
            product.bits = shiftR(product.bits, true);
        }
        
        if(product.bits[product.bits.length - 1] != final_sign)
            overflow = true;
        else
            overflow = false;
        
        return RemoveBitZero(product);
    }
    
    //Incrementa ou decrementa o expoente dependendo da necessidade
    //Arredonda o valor e retorna a forma padrao
    static BinFloat RoundFloat(BinFloat floatA, boolean add)
    {
        int offset = 0;
        if(add)
            offset += BinFloat.mantissa_size;
        
        if(floatA.mantissa.bits[floatA.mantissa.bits.length -3] && add)
        {
            offset += 1;
            floatA.exponent = SumInt(floatA.exponent, new BinInt(1, floatA.exponent_size), false);
        }
        else if(!floatA.mantissa.bits[floatA.mantissa.bits.length -3] && floatA.mantissa.bits[floatA.mantissa.bits.length -3] && !add)
        {            
            floatA.exponent = SubInt(floatA.exponent, new BinInt(1, floatA.exponent_size), false);
        }
        
        BinInt mantissa = new BinInt(0, BinFloat.mantissa_size);
        
        for(int i = 0; i < BinFloat.mantissa_size; i++)
        {
            mantissa.bits[i] = floatA.mantissa.bits[i + offset];
        }
        
        floatA.mantissa = mantissa;

    	return floatA;
    }
    
    //Excecao quando ha soma: intA + intB
    static void FloatException(BinInt intA, BinInt intB, BinInt intR) throws Exception
    {
        if((intA.bits[intA.bits.length - 1] && intB.bits[intB.bits.length - 1]) && !intR.bits[intR.bits.length - 1])
            throw new Exception("Overflow detected");
            
        if((!intA.bits[intA.bits.length - 1] && !intB.bits[intB.bits.length - 1]) && intR.bits[intR.bits.length - 1])    
            throw new Exception("Underflow dectected");
    }
    
    //Excecao quando ha sub: intA - intB
    static void FloatException2(BinInt intA, BinInt intB, BinInt intR) throws Exception
    {
        if((intA.bits[intA.bits.length - 1] && !intB.bits[intB.bits.length - 1]) && !intR.bits[intR.bits.length - 1])
            throw new Exception("Overflow detected");
            
        if((!intA.bits[intA.bits.length - 1] && intB.bits[intB.bits.length - 1]) && intR.bits[intR.bits.length - 1])    
            throw new Exception("Underflow dectected");
    }
    
    //Adiciona dois bits a esquerda para facilitar as contas usando operacoes inteiras que eram para complemento de dois
    static BinInt PrepareMantissa(BinInt mantissa)
    {
        boolean[] bits = new boolean[mantissa.bits.length + 2];
        for(int i = 0; i < mantissa.bits.length; i++)
            bits[i] = mantissa.bits[i];
        
        bits[bits.length - 2] = true;
        Report.Log("prepared: " + (new BinInt(bits)).ToString());
        return new BinInt(bits);
    }
    
    static BinFloat MultFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
    	boolean sign  = (floatA.sign ^ floatB.sign); 
    	BinInt  exp   = null;
    	BinInt  signi = null;
    	BinInt  exc   = new BinInt(BinFloat.excess, BinFloat.exponent_size);
        
    	if(floatA.isZero() || floatB.isZero())
    		return new BinFloat(0, 0, 0);
    	
        //Retira os excessos de cada valor
        floatA.exponent = SubInt(floatA.exponent, exc, false);
        floatB.exponent = SubInt(floatB.exponent, exc, false);
        
        exp = SumInt(floatA.exponent, floatB.exponent, false);    
        exp = SumInt(exp, exc, false);   
         
        //Concerta os valores ao adicionar o excesso
        floatA.exponent = SumInt(floatA.exponent, exc, false);
        floatB.exponent = SumInt(floatB.exponent, exc, false);
        
        //Testa se houver overflow ou underflow no expoente
        FloatException(floatA.exponent, floatB.exponent, exp);
        signi = MultInt(PrepareMantissa(floatA.mantissa), PrepareMantissa(floatB.mantissa));
        //Report.Log(signi.ToString());
        
    	return RoundFloat(new BinFloat(sign, exp, signi), true);
    }
    
    static BinFloat DivFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
        boolean sign  = (floatA.sign ^ floatB.sign); 
    	BinInt  exp   = null;
    	BinInt  signi = null;
    	BinInt  exc   = new BinInt(BinFloat.excess, BinFloat.exponent_size);
        
    	if(floatA.isZero())
    		return new BinFloat(0, 0, 0);
    	
        if(floatB.isZero())
            throw new Exception("Divisao por zero!");
        
        //Retira os excessos de cada valor
        floatA.exponent = SubInt(floatA.exponent, exc, false);
        floatB.exponent = SubInt(floatB.exponent, exc, false);
        
        exp = SubInt(floatA.exponent, floatB.exponent, false);    
        exp = SumInt(exp, exc, false);   
         
        //Concerta os valores ao adicionar o excesso
        floatA.exponent = SumInt(floatA.exponent, exc, false);
        floatB.exponent = SumInt(floatB.exponent, exc, false);
        
        //Testa se houver overflow ou underflow no expoente
        FloatException2(floatA.exponent, floatB.exponent, exp);
        signi = DivInt(PrepareMantissa(floatA.mantissa), PrepareMantissa(floatB.mantissa));        
        Report.Log(signi.ToString());    
        
    	return RoundFloat(new BinFloat(sign, exp, signi), false);
    }
    
    //NEEDS TESTING
    static int AGreaterB(BinInt intA, BinInt intB)
    {
    	//Iguais
    	int bigger = 1;
    	
    	for(int i = 0; i < intA.length; i++)
    	{
    		//Maior
    		if(intA.bits[i] && !intB.bits[i])
    			bigger = 2;
    		
    		//Menor
    		if(!intA.bits[i] && intB.bits[i])
    			bigger = 0;
    	}
    	
    	return bigger;
    }
    
    static BinFloat SumFloat(BinFloat floatA, BinFloat floatB, boolean subtract) throws Exception
    {
        if(floatA.isZero())
            return floatB;
            
        if(floatB.isZero())
            return floatA;
            
        BinFloat smaller = null;
        BinFloat greater = null;
        BinInt signi;
        
        if(AGreaterB(floatA.exponent, floatB.exponent) > 0)
        {
            greater = floatA;
            smaller = floatB;
        }
        else
        {
            greater = floatB;
            smaller = floatA;
        }
            
        while(AGreaterB(greater.exponent, smaller.exponent) > 1)
        {
            smaller.mantissa.bits = shiftR(smaller.mantissa.bits, false);
            if(smaller.isMantissaZero())
                return greater;
                
            smaller.exponent = SumInt(smaller.exponent, new BinInt(1, smaller.exponent.length), false);
        }
        
        Report.Log("Shift: " + smaller.ToBinScientific()); 
        
        boolean final_sign;
        
        if(subtract)
            smaller.sign = !smaller.sign;
        
        if(smaller.sign == greater.sign)
        {
            signi = SumInt(bigger.mantissa, smaller.mantissa, false);
            //TESTAR OVERFLOW COM EXCEPTION
        }
        else
        {
            signi = SubInt(bigger.mantissa, smaller.mantissa, false);
        }
            
        return floatA;
    }
}