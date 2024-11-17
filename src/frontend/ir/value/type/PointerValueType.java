package frontend.ir.value.type;

public class PointerValueType extends ValueType{
    private final ValueType referenceType;

    public PointerValueType(ValueType referenceType) {
        this.referenceType = referenceType;
    }

    public ValueType getReferenceType() {
        return referenceType;
    }

    @Override
    public String toString() {
        return referenceType + "*";
    }
}
