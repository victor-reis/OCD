package ep;

import static ep.Leitura.converter;
import static ep.Leitura.setLenght;
import static ep.Operands.execute;
import static ep.Operands.printValue;

public class Main {
    public static void main(String[] args) {
            String entradaTexto = "1010";
            String entradaTexto2 = "10";
            int tamanho = 5;
            setLenght(tamanho);
            boolean[] a = converter(entradaTexto);
            boolean[] b = converter(entradaTexto2);

            printValue(execute(a,b,"*"));

    }
}
