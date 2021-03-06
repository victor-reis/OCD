package ep;

public class Report
{    
    static void Log(Object obj)
    {
        System.out.println(obj);
    }
    
    static void TestSum()
    {        
        //Inteiro de teste A
        BinInt intA;
        //Inteiro de teste B
        BinInt intB;
        //Inteiro de resultado de somaDivision by zero detected
        BinInt intR;
        //1 em 8 bits
        BinInt one  = new BinInt(0b00000001, 8);
        
        Log("Testando complemento de dois. C(IntA(i)) = -i");
        
        for(int i = 0; i < 128; i++)
        {
            intA = Alu.TwoComplement(new BinInt(i, 8));

            if(intA.ToDec() != -i)
            {
                Log(" - " + intA.ToDec() + " != " + (-i));
            }
            else
            {
                //Log(" + " + intA.ToDec() + " == " + (-i));
            }
        }
        
        Log("Testando complemento de dois. C(IntA(-i)) = i");
        //C(-128) = -128
        for(int i = -128; i < 0; i++)
        {
            intA = Alu.TwoComplement(new BinInt(i, 8));

            if(intA.ToDec() != -i)
            {
                Log(" - " + intA.ToDec() + " != " + (-i));
            }
            else
            {
                //Log(" + " + intA.ToDec() + " == " + (-i));
            }
        }
        
        Log("Testando range de soma de inteiros. IntA + i : i = [-127, 127]");
        
        //127
        int numA = 0b01111111;
        //-128 em 8 bits-128
        intA = Alu.TwoComplement(new BinInt(0b10000000, 8));
        
        //Incrementando -127 ate 127 e comparando com i.
        for(int i = -numA - 1; i < numA + 1; i++)
        {
            if(intA.ToDec() != i)
            {
                Log(" - " + intA.ToDec() + " != " + i);
            }
            else
            {
                //Log(" + " + intA.ToDec() + " == " + i);
            }
            
            intA = Alu.SumInt(intA, one, false);
        }
        
        Log("Testando range de soma de inteiros. IntA + IntB, com todas as combinacoes de IntA e IntB : IntA, IntB = [-128, 127].");
        
        for(int i = -numA - 1; i < numA + 1; i++)
        {
            intA = new BinInt(i, 8);
            
            for(int j = -numA - 1; j < numA + 1; j++)
            {
                intB = new BinInt(j, 8);
                intR = Alu.SumInt(intA, intB, false);
                
                if(((i + j) > 127) || ((i + j) < -128)) 
                {
                    if(Alu.overflow)
                    {
                        //Log(" + Overflow, Alu detectou. " + intR.ToDec() + " | " + (i + j) + " | " + i + " + " + j);
                    }
                    else
                    {
                        Log(" - Overflow, mas a Alu nao detectou. " + intR.ToDec() + " | " + (i + j) + " | " + i + " + " + j);
                    }
                }                
                else if(intR.ToDec() != i + j)
                {
                    Log(" - " + intR.ToDec() + " != " + (i + j));
                }
                else
                {
                    //Log(" + " + intR.ToDec() + " == " + (i + j));
                }
            }
        }
    }
    
    //Qualquer num - valor minimo gera overflow nao pego
    //Problema que: C(valor min) = valor min
    static void TestSub()
    {
        //Inteiro de teste A
        BinInt intA;
        //Inteiro de teste B
        BinInt intB; 
        //Inteiro de resultado de subtracao
        BinInt intR;
        //127
        int numA = 0b01111111;
        
        Log("Testando range de soma de inteiros. IntA - IntB, com todas as combinacoes de IntA e IntB : IntA, IntB = [-128, 127].");
        
        for(int i = -numA - 1; i < numA + 1; i++)
        {
            intA = new BinInt(i, 8);
            
            for(int j = -numA - 1; j < numA + 1; j++)
            {
                intB = new BinInt(j, 8);
                intR = Alu.SubInt(intA, intB, false);
                
                if(((i - j) > 127) || ((i - j) < -128))
                {
                    if(Alu.overflow)
                    {
                        //Log(" + Overflow, Alu detectou. " + intR.ToDec() + " | " + (i - j) + " | " + i + " - " + j);
                    }
                    else
                    {
                        Log(" - Overflow, mas a Alu nao detectou. " + intR.ToDec() + " | " + (i - j) + " | " + i + " - " + j);
                    }
                }                
                else if(intR.ToDec() != i - j)
                {
                    Log(" - " + intR.ToDec() + " != " + (i - j));
                }
                else
                {
                    //Log(" + " + intR.ToDec() + " == " + (i - j));
                }
            }
        }
    }
    
    static void TestDiv()
    {
        //Inteiro de teste A
        BinInt intA;
        //Inteiro de teste B
        BinInt intB;  
        //Inteiro de resultado de divisao
        BinInt intR;
        //127
        int numA = 0b01111111;
        
        Log("Testando range de soma de inteiros. IntA / IntB, com todas as combinacoes de IntA e IntB : IntA, IntB = [-128, 127].");
        
        for(int i = -numA - 1; i < numA + 1; i++)
        {
            intA = new BinInt(i, 8);
            
            for(int j = -numA - 1; j < numA + 1; j++)
            {
                intB = new BinInt(j, 8);
                try
                {
                    intR = Alu.DivInt(intA, intB);
                }
                catch(Exception ex)
                {
                    //Log(" Divisao por zero detectada: " + i + " / " + j);
                    continue;
                }
                
                if(((i / j) > 127) || ((i / j) < -128)) 
                {
                    if(Alu.overflow)
                    {
                        //Log(" + Overflow, Alu detectou. " + intR.ToDec() + " | " + (i / j) + " | " + i + "/" + j);
                    }
                    else
                    {
                        Log(" - Overflow, mas a Alu nao detectou. " + intR.ToDec() + " | " + (i / j) + " | " + i + "/" + j);
                    }
                }                
                else if((intR.ToDec() != (i / j)))
                {
                    Log(" - " + intR.ToDec() + " != " + (i / j) + " | " + i + "/" + j);
                }
                else
                {
                    //Log(" + " + intR.ToDec() + " == " + (i / j) + " | " + i + "/" + j);
                }
            }
        }
    }
    
    //-128 * qualquer num da o valor errado (wikipedia tem a solucao)
    static void TestMult()
    {
        //Inteiro de teste A 
        BinInt intA;
        //Inteiro de teste B
        BinInt intB;  
        //Inteiro de resultado de divisao
        BinInt intR;
        //127
        int numA = 0b01111111;
        
        /*
        intA = new BinInt(7, 4);
        intB = new BinInt(-8, 4);
        intR = Alu.MultInt(intA, intB);
        Log(intR.ToString() + " | " + intR.ToDec());
        */
        
        Log("Testando range de soma de inteiros. IntA * IntB, com todas as combinacoes de IntA e IntB : IntA, IntB = [-128, 127].");
        
        for(int i = -numA - 1; i < numA + 1; i++)
        {
            intA = new BinInt(i, 8);
            
            for(int j = -numA - 1; j < numA + 1; j++)
            {
                intB = new BinInt(j, 8);
                intR = Alu.MultInt(intA, intB);

                if(((i * j) > 128*128-1) || ((i * j) < -(128*128))) 
                {
                    if(Alu.overflow)
                    {
                        Log(" + Overflow, Alu detectou. " + intR.ToDec() + " * " + (i * j));
                    }
                    else
                    {
                        Log(" - Overflow, mas a Alu nao detectou. " + intR.ToDec() + " * " + (i * j));
                    }
                }                
                else if((intR.ToDec() != (i * j)))
                {
                    Log(" - " + intR.ToDec() + " != " + (i * j) + " | " + i + "*" + j);
                }
                else
                {
                    //Log(" + " + intR.ToDec() + " == " + (i * j) + " | " + i + "*" + j);
                }
            }
        }
    }
    
    static void TestFloatMult()
    {
    	
    	//ZERO
        //BinFloat floatA = new BinFloat(0, -127, 0);      	
    	//MAX exp
        //BinFloat floatA = new BinFloat(0, 128, 0);
        //MIN exp
    	//BinFloat floatA = new BinFloat(1, -126, 1);
        
    	/*
    	BinFloat floatA = new BinFloat(1, 1, 0);
        Log(floatA.ToString());
        Log(floatA.ToBinScientific());
    	
    	float f = -2;
    	Log(Integer.toBinaryString(Float.floatToRawIntBits(f)));
    	*/
    	
    	/*
    	boolean[] a = {false, false, false, false, false, false, true};
    	boolean[] b = {true, false, false, false, false, false, false};
    	BinInt x = new BinInt(a);
    	BinInt y = new BinInt(b);
    	BinInt z = Alu.SumInt(x, y, false);
    	Log(z.ToString());
    	*/
    }
}