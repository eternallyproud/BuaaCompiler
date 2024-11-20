package frontend.semantic.symbol;

import java.util.Objects;

public class Symbol {
    private final String name;
    private final DataType type;

    public Symbol(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(name, symbol.name);
    }

    @Override
    public String toString() {
        return name + " " + type;
    }
}
