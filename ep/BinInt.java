
public class BinInt
{
    int length;
    boolean[] bits;
    
    //Construtor padrao
    BinInt(int decimal, int num_bits)
    {
        boolean negative = (decimal < 0);
        if(negative)
        {
            decimal *= -1;
            bits = DecToBin(decimal, num_bits);
            bits = Alu.TwoComplement(this).bits;  
        }
        else
        {
            bits   = DecToBin(decimal, num_bits);
        }
        length = bits.length;
    }
    
    BinInt(boolean[] bits)
    {
        this.bits = bits;
        length = bits.length;
    }
    
    //Transforma um binario em inteiro decimal e com n bits
    static boolean[] DecToBin(int dec, int num_bits)
    {
        boolean[] bin = new boolean[num_bits];
        int   bit  = 0b1;
        
        for(int i = 0; i < num_bits; i++)
        {
            if((dec & bit) > 0)
                bin[i] = true;
            else
                bin[i] = false;
                
            bit <<= 1;
        }
        
        return bin;
    }
    
    //Mesmo que acima
    static int BinToDec(boolean[] bin)
    {
        int dec = 0;
                
        for(int i = 0; i < bin.length; i++)
        {
            if(bin[i])
                dec |= (1<< i); 
        }
        
        return dec;
    }
    
    //Retorna o valor decimal do binario
    int ToDec()
    {
        if(!bits[bits.length - 1])
            return BinToDec(bits.clone());
            
        return -BinToDec(Alu.TwoComplement(this).bits);
    }
    
    //Retorna uma representacao de string do binario
    String ToString()
    {
        String s = "";
        for(int i = 0; i < bits.length; i++)
        {
            if(bits[i])
                s = 1 + s;
            else
                s = 0 + s;
        }
            
        return s;
    }
}