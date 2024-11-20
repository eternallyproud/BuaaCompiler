package frontend.ir.llvm.value;

import frontend.ir.llvm.value.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class User extends Value {

    public User(ValueType type, String name) {
        super(type, name);
    }

    public void addUsed(Value used) {
        this.usedList.add(new Use(this, used));
    }

    public Value getUsed(int index) {
        return usedList.size() > index ? usedList.get(index).getUsed() : null;
    }

    public ArrayList<Value> getUsedList(){
        return usedList.stream().map(Use::getUsed).collect(Collectors.toCollection(ArrayList::new));
    }
}
