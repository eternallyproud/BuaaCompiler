package frontend.ir.llvm.value.type;

public class ArrayValueType extends ValueType {
    private final ValueType elementType;
    private final int elementNumber;

    public ArrayValueType(ValueType elementType, int elementNumber) {
        this.elementType = elementType;
        this.elementNumber = elementNumber;
    }

    public ValueType getElementType() {
        return elementType;
    }

    public int getElementNumber() {
        return elementNumber;
    }

    @Override
    public String toString() {
        return "[" + elementNumber + " x " + elementType + "]";
    }
}
