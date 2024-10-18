package frontend.symbol;

import java.util.ArrayList;

public class IntFuncSymbol extends FuncSymbol {
    public IntFuncSymbol(String name, ArrayList<NumericalSymbol> parameters) {
        super(name, DataType.INT_FUNC, parameters);
    }
}
