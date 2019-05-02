
public class Alu
{
    //flag que determina se houver ou nao overflow de operacoes de INT
    //no float sao jogadas execoes
    //incocistencia na forma mas whatever...
    static boolean overflow  = false;
    
    //se o resultado da operacao int eh negativo
    static boolean sign      = false;
    
    //Durante a divisao de float foi percebida a necessidade do resto,
    //mas foi preferido fazer esta gambiarra do que atualizar todas as utilizacoes do metodo
    //DivInt
    static BinInt div_remainder = null;
    
    //Soma dois bits e o carry in passado
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
    
    //Soma inteiros: A + B
    static BinInt SumInt(BinInt intA, BinInt intB, boolean cin)
    {
        boolean   signA  = intA.bits[intA.length - 1];
        boolean   signB  = intB.bits[intB.length - 1];
        boolean[] sum    = new boolean[intA.length];
        boolean[] sumbit = new boolean[2];
        sumbit[0] = false;
        sumbit[1] = cin;
        
        //Soma os n bits de cada inteiro
        for(int i = 0; i < intA.length; i++)
        {
            sumbit = SumBit(intA.bits[i], intB.bits[i], sumbit[1]);
            sum[i] = sumbit[0];
        }
    
        //testa e seta overflow ou nao
        if((signA == signB) && (sum[sum.length -1] != signA))
            overflow = true;
        else
            overflow = false;
        
        //testa e seta se o resultado eh negativo ou nao
        if(sum[sum.length -1])
            sign = true;
        else
            sign = false;
        
        return new BinInt(sum);
    } 
    
    //Complemento de dois d eum inteiro
    static BinInt TwoComplement(BinInt binA)
    {
        BinInt bin = new BinInt(binA.bits.clone());
        
        for(int i = 0; i < bin.length; i++)
            bin.bits[i] = !bin.bits[i];
            
        bin = SumInt(bin, new BinInt(0b1, bin.length), false);    
        return bin;
    }   
    
    //Subtrai inteiros: A - B
    static BinInt SubInt(BinInt intA, BinInt intB, boolean cin)
    {
        return SumInt(intA, TwoComplement(intB), cin);
    }
    
    //Divivde inteiros: A / B
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
        
        //testa e seta overflow ou nao
        if(result.bits[result.bits.length - 1] != final_sign)
            overflow = true;
        else
            overflow = false;
            
        //testa e seta se o resultado eh negativo ou nao
        if(result.bits[result.bits.length -1])
            sign = true;
        else
            sign = false;
        
        //seta a gambiarra do resto que foi esquecida ate eu lembrar que naverdade era muito util no float
        div_remainder = remainder;

        return result;
    }
    
    //Shift right no array
    //sticky = quando ha necessidade de shift right sticky, i.g:
    //100, shiftR(100) = 110; 010, shiftR(010) = 001
    //no normal o bit adicionada a esquerda eh sempre zero
    //no sticky o bit mais a esquerda se repete
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
    
    //Shift left no array
    static boolean[] shiftL(boolean[] valor)
    {
        boolean[] shift = new boolean[valor.length];
        
        for(int i = valor.length - 1; i > 0; i--)
            shift[i] = valor[i-1];
        
        shift[shift.length - 1] = false;
        
        return shift;
    }
            
    //Remove o ultimo bit naverdade
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
        
        //preenche os bits das variaveis de acordo com as especificacoes do algoritmo
        for(int pos = 0; pos < intA.length; pos++)
            add.bits[intA.length + 1 + pos] = intA.bits[pos];
        for(int pos = 0; pos < compl.length; pos++)
            sub.bits[intA.length + 1 + pos] = compl.bits[pos];
        for(int pos = 0; pos < intA.length; pos++)
            product.bits[product.length - intA.length - 1 - pos] = intB.bits[intB.length - 1 - pos];
        
        product.bits[0] = false;
        
        //Ou adiciona ou subtrai de acordo com os bits menos significativos, no final da shiftR no resultado, repete
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
        
        //Testa overflow de multiplicacao e seta ou nao
        if(product.bits[product.bits.length - 1] != final_sign)
            overflow = true;
        else
            overflow = false;
            
        //testa e seta se o resultado eh negativo ou nao        
        if(product.bits[product.bits.length -1])
            sign = true;
        else
            sign = false;
    
        return RemoveBitZero(product);
    }
    
    //Incrementa ou decrementa o expoente dependendo da necessidade
    //Arredonda o valor e retorna a forma padrao
    static BinFloat RoundFloat(BinFloat floatA, boolean add)
    {
        //add = se for multiplicacao
        int offset = 0;
        if(add)
            offset += BinFloat.mantissa_size;
        
        //Testa os casos de rounding de float na multiplicacao
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
        
        //copia a mantissa nova em uma nova mantissa com tamanho padrao
        for(int i = 0; i < BinFloat.mantissa_size; i++)
        {
            mantissa.bits[i] = floatA.mantissa.bits[i + offset];
        }
        
        floatA.mantissa = mantissa;

    	return floatA;
    }
    
    //Faz a normalizacao e rounding(nessa caso trunca) de fato, sem macetes
    static BinFloat RoundFloat3(BinFloat floatA)
    {
        int leftmost = 0;
        
        //achar bit mais significativo que seja 1
        for(int i = 0; i < floatA.mantissa.length; i++)
        {
            if(floatA.mantissa.bits[i])
                leftmost = i;
        }
        
        leftmost = floatA.mantissa.length - 2 - leftmost;
        
        //ajusta o expoente
        while(leftmost > 0)
        {
            floatA.mantissa.bits = shiftL(floatA.mantissa.bits);
            leftmost--;
        }
        
        BinInt mantissa = new BinInt(0, BinFloat.mantissa_size);
        
        //copia a mantissa nova em uma nova mantissa com tamanho padrao
        for(int i = 0; i < BinFloat.mantissa_size; i++)
        {
            mantissa.bits[i] = floatA.mantissa.bits[i];
        }
        
        floatA.mantissa = mantissa;

        return floatA;
    }
    
    //Excecao quando ha soma: intA + intB (para multiplicao)
    static void FloatException(BinInt intA, BinInt intB, BinInt intR) throws Exception
    {
        FloatException3(intA, intB, intR);
            
        if((!intA.bits[intA.bits.length - 1] && !intB.bits[intB.bits.length - 1]) && intR.bits[intR.bits.length - 1])    
            throw new Exception("Underflow dectected");
    }
    
    //Excecao quando ha sub: intA - intB (para divisao)
    static void FloatException2(BinInt intA, BinInt intB, BinInt intR) throws Exception
    {
        if((intA.bits[intA.bits.length - 1] && !intB.bits[intB.bits.length - 1]) && !intR.bits[intR.bits.length - 1])
            throw new Exception("Overflow detected");
            
        if((!intA.bits[intA.bits.length - 1] && intB.bits[intB.bits.length - 1]) && intR.bits[intR.bits.length - 1])    
            throw new Exception("Underflow dectected");
    }
    
    //Testa excecao na soma A + B com A e B > 0
    static void FloatException3(BinInt intA, BinInt intB, BinInt intR) throws Exception
    {
        if(((intA.bits[intA.bits.length - 1] || intB.bits[intB.bits.length - 1])) && !intR.bits[intR.bits.length - 1])
            throw new Exception("Overflow detected");
    }
    
    //Adiciona extra bits a esquerda para facilitar as contas usando operacoes inteiras que eram para complemento de dois
    static BinInt PrepareMantissa(BinInt mantissa, int extra)
    {
        boolean[] bits = new boolean[mantissa.bits.length + extra];
        for(int i = 0; i < mantissa.bits.length; i++)
            bits[i] = mantissa.bits[i];
        
        bits[bits.length - extra] = true;
        //Report.log("prepared: " + (new BinInt(bits)).ToString());
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
        signi = MultInt(PrepareMantissa(floatA.mantissa, 2), PrepareMantissa(floatB.mantissa, 2));
        ////Report.log(signi.ToString());
        
    	return RoundFloat(new BinFloat(sign, exp, signi), true);
    }
    
    static BinFloat DivFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
        boolean sign  = (floatA.sign ^ floatB.sign); 
    	BinInt  exp   = null;
    	BinInt  signi = null;
        BinInt  res   = null;
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
        signi = new BinInt(0 , BinFloat.mantissa_size + 2);
        
        BinInt floatBP = PrepareMantissa(floatB.mantissa, BinFloat.mantissa_size);
        
        res = DivInt(PrepareMantissa(floatA.mantissa, BinFloat.mantissa_size), floatBP);        
        signi.bits[signi.bits.length - 2] = res.bits[0];
        if(div_remainder.bits[div_remainder.bits.length - BinFloat.mantissa_size - 1])
            div_remainder = TwoComplement(div_remainder);
        
        div_remainder.bits = shiftL(div_remainder.bits);
        
        //Report.log("rem     : " + div_remainder.ToString());
        //Report.log("res     : " + res.ToString());
        //Report.log("signi   : " + signi.ToString());
        //detect if subnormal here
        
        for(int i = 0; i < 3; i++)
        {
            res = DivInt(div_remainder, floatBP);
            signi.bits[signi.bits.length - 3 - i] = res.bits[0];
            if(div_remainder.bits[div_remainder.bits.length - BinFloat.mantissa_size + i])
                div_remainder = TwoComplement(div_remainder);
                
            div_remainder.bits = shiftL(div_remainder.bits);
            //Report.log("rem     : " + div_remainder.ToString());
            //Report.log("res     : " + res.ToString());
            //Report.log("signi   : " + signi.ToString());
        }
        
        //Report.log(signi.ToString());    
        
    	return RoundFloat(new BinFloat(sign, exp, signi), false);
    }
    
    //Compara A e B
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
    
    //Subtrai floats: A - B
    static BinFloat SubFloat(BinFloat floatA, BinFloat floatB) throws Exception
    {
        return SumFloat(floatA, floatB, true);
    }
    
    //Soma floats: A + B, onde subtract inverte o sinal do segundo
    static BinFloat SumFloat(BinFloat floatA, BinFloat floatB, boolean subtract) throws Exception
    {
        if(floatA.isZero())
            return floatB;
            
        if(floatB.isZero())
            return floatA;
            
        BinFloat smaller    = null;
        BinFloat greater    = null;
        BinInt   signi      = null;
        BinInt   exp        = null;
        boolean  final_sign = false;
        
        
        //Decide qual e maior e qual e menor
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
        
        //Ajusta mantissas alinhando-as
        while(AGreaterB(greater.exponent, smaller.exponent) > 1)
        {
            smaller.mantissa.bits = shiftR(smaller.mantissa.bits, false);
            if(smaller.isMantissaZero())
                return greater;
                
            smaller.exponent = SumInt(smaller.exponent, new BinInt(1, smaller.exponent.length), false);
        }

        ////Report.log("Shift: " + smaller.ToBinScientific()); 
        
        BinInt gmantissa = PrepareMantissa(greater.mantissa, 2);
        BinInt gexp      = greater.exponent;
        BinInt smantissa = PrepareMantissa(smaller.mantissa, 2);
        BinInt sexp      = smaller.exponent;
        
        if(subtract)
            smaller.sign = !smaller.sign;
        
        //Na hora de somar, se houver sinais iguais
        if(smaller.sign == greater.sign)
        {
            final_sign = smaller.sign;
            signi = SumInt(gmantissa, smantissa, false);
            // testa overflow no expoente
            if(signi.bits[signi.bits.length - 1])
            {
                signi.bits = shiftR(signi.bits, false);
                exp = SumInt(sexp, new BinInt(1, BinFloat.exponent_size), false);
                //Testa overflow no expoente
                FloatException3(sexp, new BinInt(1, BinFloat.exponent_size), exp);
            }
        }
        else
        {
            signi = SubInt(gmantissa, smantissa, false);
            exp = sexp;
            if(signi.bits[signi.bits.length - 1])
            {
                final_sign = true;
                signi = TwoComplement(signi);
            }
        }
        
        ////Report.log("result = " + signi.ToString());
         
        return RoundFloat3(new BinFloat(final_sign, exp, signi));
    }
}