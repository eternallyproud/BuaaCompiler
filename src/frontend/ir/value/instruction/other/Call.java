package frontend.ir.value.instruction.other;

import frontend.ir.value.Value;
import frontend.ir.value.global.Function;
import frontend.ir.value.type.ScalarValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

//<result> = call <ty> <function>(<function args>)
public class Call extends OtherOperation {
    public Call(String name, Function function, ArrayList<Value> parameters) {
        super(function.getValueType(), name);
        addUsed(function);
        for (Value parameter : parameters) {
            addUsed(parameter);
        }
    }

    @Override
    public String toString() {
        String prefix = getUsed(0).getValueType() == ScalarValueType.VOID ? "" : name + " = ";
        ArrayList<String> parameters = getUsedList().subList(1, getUsedList().size()).stream()
                .map(parameter -> parameter.getValueType() + " " + parameter.getName()).collect(Collectors.toCollection(ArrayList::new));
        return super.toString() + prefix + "call " + getUsed(0).getValueType() + " "
                + getUsed(0).getName() + "(" + String.join(", ", parameters) + ")";
    }
}
