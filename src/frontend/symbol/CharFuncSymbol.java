package frontend.symbol;

import java.util.ArrayList;

public class CharFuncSymbol extends FuncSymbol {
    public CharFuncSymbol(String name, ArrayList<NumericalSymbol> parameters) {
        super(name, DataType.CHAR_FUNC, parameters);
    }
}
