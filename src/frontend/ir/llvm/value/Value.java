package frontend.ir.llvm.value;

import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected final ValueType valueType;
    protected final String name;
    protected final ArrayList<Use> userList;
    protected final ArrayList<Use> usedList;

    public Value(ValueType valueType, String name) {
        this.valueType = valueType;
        this.name = name;
        this.userList = new ArrayList<>();
        this.usedList = new ArrayList<>();
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getName() {
        return name;
    }

    public Value convertTo(ValueType expectedValueType) {
        if ((expectedValueType instanceof ScalarValueType) && (!this.valueType.equals(expectedValueType))) {
            ConversionOperation conversionOperation = new ConversionOperation(expectedValueType, IRBuilder.IR_BUILDER.getLocalVarName(), this);
            IRBuilder.IR_BUILDER.addInstruction(conversionOperation);
            return conversionOperation;
        }
        return this;
    }

    public void buildAssembly(){
    }
}
