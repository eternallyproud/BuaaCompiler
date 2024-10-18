package frontend.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private final ArrayList<NumericalSymbol> parameters;

    public FuncSymbol(String name, DataType type, ArrayList<NumericalSymbol> parameters) {
        super(name, type);
        this.parameters = parameters;
    }

    public DataType getReturnType() {
        switch (this.getType()) {
            case INT_FUNC -> {
                return DataType.INT;
            }
            case CHAR_FUNC -> {
                return DataType.CHAR;
            }
            case VOID_FUNC -> {
                return DataType.VOID;
            }
            default -> {
                return DataType.UNEXPECTED;
            }
        }
    }

    public ArrayList<NumericalSymbol> getParameters() {
        return parameters;
    }
}
