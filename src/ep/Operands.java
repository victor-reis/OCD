package ep;

import static ep.Leitura.POS_BIT_COMPLEMENTO;
import static ep.Leitura.POS_BIT_MENOS_SIGNIFICATIVO;

public class Operands {

    private static boolean[] soma(boolean[] valor1, boolean[] valor2){
        boolean resultado[] = new boolean[valor1.length + 1];
        boolean carryBit = false;
        int aux;
        for(int pos = valor1.length-1; pos >= 0 ; pos--){
            aux = 0;
            if(valor1[pos])aux++;
            if(valor2[pos])aux++;
            if(carryBit)aux++;

            switch (aux){
                case 0:
                    carryBit = false;
                    resultado[pos+1] = false;
                    break;
                case 1:
                    carryBit = false;
                    resultado[pos+1] = true;
                    break;
                case 2:
                    carryBit = true;
                    resultado[pos+1] = false;
                    break;
                case 3:
                    carryBit = true;
                    resultado[pos+1] = true;
                    break;
                default:
                        throw new IllegalArgumentException();
            }
        }

        return removeIndiceZero(resultado);
    }

    private static boolean[] subtracao(boolean[] valor1, boolean[] valor2){
        boolean[] result = soma(valor1,complementoDeDois(valor2));
        return result;
    }

    private static boolean[] divisao(boolean[] valor1, boolean[] valor2){
        final boolean[] um = new boolean[valor1.length];
        boolean[] quociente = new boolean[valor1.length];
        boolean[] resto = valor1.clone();

        um[POS_BIT_MENOS_SIGNIFICATIVO] = true;

        while(!subtracao(resto,valor2)[POS_BIT_COMPLEMENTO]){
            resto = subtracao(resto,valor2);
            quociente = soma(quociente,um);
        }
        System.out.print("quociente: ");
        printValue(quociente);
        System.out.print("resto: ");
        printValue(resto);
        return quociente;
    }

    public static boolean[] multiplicacao(boolean[] valor1, boolean[] valor2) {
        boolean[] produto = new boolean[valor1.length * 2 + 1];
        final boolean[] add = new boolean[valor1.length * 2 + 1];
        final boolean[] sub = new boolean[valor1.length * 2 + 1];
        final boolean[] complementoValor1 = complementoDeDois(valor1);


        for(int pos = 0; pos < valor1.length;pos++)
            add[pos] = valor1[pos];
        for(int pos = 0; pos < complementoValor1.length;pos++)
            sub[pos] = complementoValor1[pos];
        for(int pos = 0; pos < complementoValor1.length;pos++)
            produto[valor2.length + pos] = valor2[pos];

        printValue(add);
        printValue(sub);
        printValue(produto);

        for(int pos = 0; pos < valor1.length;pos++){
            if (!produto[produto.length - 2] &&
                    produto[produto.length - 1]) produto = soma(produto, add);

            else if (produto[produto.length - 2] &&
                    !produto[produto.length - 1])  produto = soma(produto, sub);

            produto = shiftR(produto);
        }
        return removeIndiceMax(produto);
    }

    static boolean[] complementoDeDois (final boolean[] valor){
        boolean[] um = new boolean[valor.length];
        boolean[] resultado;
        boolean[] entrada = valor.clone();
        um[POS_BIT_MENOS_SIGNIFICATIVO] = true;

        for(int pos = 0; pos < valor.length; pos++)
            entrada[pos] = !entrada[pos];

        resultado = soma(entrada,um);

        return resultado;
    }

    private static Boolean isZero(final boolean[] valor){
        boolean[] zero = new boolean[valor.length];
        boolean isZero = true;
        for(int pos = 0; pos < valor.length; pos++)
            if(valor[pos] != zero[pos]) {
                isZero = false;
                break;
            }
            return isZero;
    }

    static boolean[] removeIndiceZero(boolean[] valor){
        boolean[] retificado = new boolean[valor.length - 1];
        for(int pos = 1; pos <= retificado.length; pos++)
            retificado[retificado.length - pos] = valor[valor.length - pos];
        return retificado;
    }

    static boolean[] removeIndiceMax(boolean[] valor){
        boolean[] retificado = new boolean[valor.length - 1];
        for(int pos = 0; pos < retificado.length; pos++)
            retificado[pos] = valor[pos];
        return retificado;
    }

    static void printValue(boolean[] valor){
        for(boolean bit : valor){
            if(bit) System.out.print("1");
            if(!bit) System.out.print("0");
        }
        System.out.println();
    }

    public static boolean[] shiftR(boolean[] valor) {
        boolean[] shift = new boolean[valor.length];
        for (int i = valor.length - 1; i >= 1; i--) {
            shift[i] = valor[i - 1];
        }
        shift[0]=valor[0];
        return shift;
    }

    public static int[] shiftLMantissa(int[] bin) {
        for (int i = 9; i < 31; i++) {
            bin[i] = bin[i + 1];
        }
        bin[31] = 0;
        return bin;
    }

    public static boolean[] execute(boolean[] valor1, boolean[] valor2, String operacao){
        boolean[] result;
        switch (operacao){
            case "+":
                result = soma(valor1,valor2);
                break;
            case "-":
                result = subtracao(valor1,valor2);
                break;
            case "*":
                result = multiplicacao(valor1,valor2);
                break;
            case "/":
                if(isZero(valor2)) throw new IllegalArgumentException();
                result = divisao(valor1, valor2);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return result;
    }
}

