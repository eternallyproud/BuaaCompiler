package frontend.ir.value.type;

public class ValueType {
    public ValueType getArrayElementValueType(){
        return ((ArrayValueType)this).getElementType();
    }

    public ValueType getPointerReferenceValueType(){
        return ((PointerValueType)this).getReferenceType();
    }
}
