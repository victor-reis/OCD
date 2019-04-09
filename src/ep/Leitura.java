package ep;

import java.util.Scanner;

public class Leitura {
    public static int TAMANHO;
    public static int POS_BIT_MENOS_SIGNIFICATIVO;
    static final int POS_BIT_COMPLEMENTO = 0;

    static public String leEntrada(){
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }

    static public boolean[] converter(String entradaTexto){
        boolean[] entradaBinario = new boolean[TAMANHO];
        for(int pos = 0; pos < entradaTexto.length(); pos++)
               entradaBinario[POS_BIT_MENOS_SIGNIFICATIVO - pos] = entradaTexto.charAt(-pos + entradaTexto.length() - 1) == '1';
        return entradaBinario;
    }

    static public void setLenght(int tamanho) {
        TAMANHO = tamanho;
        POS_BIT_MENOS_SIGNIFICATIVO = TAMANHO - 1;
        }
}
