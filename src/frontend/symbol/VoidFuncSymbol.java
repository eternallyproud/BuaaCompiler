package frontend.symbol;

import java.util.ArrayList;

public class VoidFuncSymbol extends FuncSymbol {
    public VoidFuncSymbol(String name, ArrayList<NumericalSymbol> parameters) {
        super(name, DataType.VOID_FUNC, parameters);
    }
}
