
public class BinInt
{
    int length;
    boolean[] bits;
    
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
    
    int ToDec()
    {
        if(!bits[bits.length - 1])
            return BinToDec(bits.clone());
            
        return -BinToDec(Alu.TwoComplement(this).bits);
    }
    
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