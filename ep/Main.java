/*
 * EP desenvolvido pelos alunos Lucas Moura de Carvalho e Victor Reis.
 * https://en.wikipedia.org/wiki/Booth%27s_multiplication_algorithm
 * javac *.java
 * java Main
 * jar cfe EP.jar Main Main.class 
 */
 
import java.util.Scanner;

public class Main
{
    Scanner scanner;
    
    Main()
    {
        scanner = new Scanner(System.in);
    }
    
    public String read()
    {
        return scanner.next();
    }
    
    public int readInt()
    {
        return scanner.nextInt();
    }
    
    public char readChar()
    {
        return scanner.next().charAt(0);
    }
    
    //Transforma um array de chars em um array de booleans 1 ou 0
    public static boolean[] StringToBin(String intA) throws Exception
    {
        boolean[] bin = new boolean[intA.length()];
        for(int i = 0; i < bin.length; i++)
        {
            if(intA.charAt(i) == '1')
            {
                bin[bin.length - 1 - i] = true;            
            }
            else if(intA.charAt(i) == '0')
            {
                bin[bin.length - 1 - i] = false;
            }
            else
                throw new Exception("Caracter invalido para binario, so podem 1 ou 0, nao " + intA.charAt(i) + ".");
        }
        
        return bin;
    }
    
    public static void main(String [] args) throws Exception
    {

        Main main = new Main();
        
        Report.Log("Usara float ou int? int para inteiros e float para float.");
        String type = main.read();
        String inta = null;
        String intb = null;
        BinInt intA = null;
        BinInt intB = null;
        BinInt intR = null;
        int  int_size    = 0;
        char op          = ' ';
        
        char   sign      = ' ';
        String exponent  = null;
        String mantissa  = null;
        
        BinFloat floatA = null;
        BinFloat floatB = null;
        BinFloat floatR = null;
        
        if(type.equals("int"))
        {
            Report.Log("Entre o tamanho dos valores int em bits.");
            int_size = main.readInt();
            
            Report.Log("Entre o inteiro A em binario. (Com zeros a esquerda tambem inclusos)");
            inta = main.read();
            
            Report.Log("Entre o inteiro B em binario. (Com zeros a esquerda tambem inclusos)");
            intb = main.read();
            
            boolean[] a = StringToBin(inta);
            boolean[] b = StringToBin(intb);
            
            if(a.length != int_size || b.length != int_size)
                throw new Exception("Algum inteiro nao tinha o tamanha especificado");
            
            intA = new BinInt(a);
            intB = new BinInt(b);
            
            Report.Log("Qual operacao? Digite:\n + para soma. \n - para subtracao. \n * para multiplicacao. \n / para divisao.");
            op = main.readChar();
            switch (op)
            {
                case '+':
                    intR = Alu.SumInt(intA, intB, false);
                break;
                case '-':
                    intR = Alu.SubInt(intA, intB, false);
                break;
                case '*':
                    intR = Alu.MultInt(intA, intB);
                break;
                case '/':
                    intR = Alu.DivInt(intA, intB);
                break;
                default:
                    throw new Exception("Operacao invalida. So podem +, -, * ou /, nao " + op + ".");                
            }
            
            if(Alu.overflow)
                Report.Log("OVERFLOW DETECTADO!");
                
            Report.Log(" Resultado: " + intA.ToString() + " " + op + " " + intB.ToString() + " = " + intR.ToString());
        }
        else if(type.equals("float"))
        {   
            for(int i = 0; i < 2; i++)
            {
                Report.Log("Para o binario " + i + ", entre o sinal do float. + para positivo e - para negativo");
                sign = main.readChar();
                int float_sign;
                
                if(sign == '+')
                    float_sign = 1;
                else if(sign == '-')
                    float_sign = 0;
                else
                    throw new Exception("Sinal invalido. So podem + ou -, nao " + sign + ".");
                    
                Report.Log("Entre o valor do expoente " + i + " em binario. (9 bits incluindo zeros a esquerda) (o bit mais a esquerda e o bit de sinal: 0 = positivo 1 = negativo, para facilitar) (entre -127 e 128)");
                exponent = main.read();
                
                int exp;
                
                if(exponent.charAt(0) == '0')
                {   
                    exponent = "0" + exponent;
                    exp = new BinInt(StringToBin(exponent)).ToDec();
                }
                else
                {
                    exponent = "0" + exponent.substring(0, exponent.length() -1);
                    Report.Log(exponent);
                    exp = (new BinInt(new BinInt(StringToBin(exponent)).ToDec()*(-1), 8)).ToDec();
                }
                
                if(exp < -127 || exp > 128)
                    throw new Exception(exp + " esta fora do range [-127, 128].");
                    
                Report.Log("Entre o valor da mantissa " + i + " em binario, considerando que o primeiro bit ja esta implicito como 1. (23 bits).");
                mantissa = "0" + main.read();
                boolean[] mant = StringToBin(mantissa);
                
                if(i == 0)
                    floatA = new BinFloat(float_sign, new BinInt(exp, 8).ToDec(),  new BinInt(mant).ToDec());
                else
                    floatB = new BinFloat(float_sign, new BinInt(exp, 8).ToDec(),  new BinInt(mant).ToDec());
            }
            
            Report.Log("Qual operacao? Digite:\n + para soma. \n - para subtracao. \n * para multiplicacao. \n / para divisao.");
            op = main.readChar();
            
            try
            {
                switch (op)
                {
                    case '+':
                        floatR = Alu.SumFloat(floatA, floatA, false);
                    break;
                    case '-':
                        floatR = Alu.SubFloat(floatA, floatA);
                    break;
                    case '*':
                        floatR = Alu.MultFloat(floatA, floatA);
                    break;
                    case '/':
                        floatR = Alu.DivFloat(floatA, floatA);
                    break;
                    default:
                        throw new Exception("Operacao invalida. So podem +, -, * ou /, nao " + op + ".");                
                }
                
                Report.Log(" Resultado: " + floatA.ToString() + "\n" + op + "\n" + floatB.ToString() + " = " + floatR.ToString());
                Report.Log(" Resultado: " + floatA.ToBinScientific() + " " + op + " " + floatB.ToBinScientific() + " = " + floatR.ToBinScientific());
            }
            catch(Exception ex)
            {
                Report.Log(ex.getMessage());
            }
        }
        
        
        //Report.TestSum();
        //Report.TestSub();
        //Report.TestDiv();
        //Report.TestMult();        	
        //Report.TestFloatMult();
        //Report.TestFloatDiv(); ERR
        //Report.TestFloatSum();
    }
}