package frontend.ir.llvm.value;

import frontend.ir.llvm.value.type.ValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class User extends Value {

    public User(ValueType type, String name) {
        super(type, name);
    }

    public void addUsed(Value used) {
        addUsedValue(used);
        used.addUser(this);
    }

    private void addUsedValue(Value usedValue) {
        this.usedList.add(new Use(this, usedValue));
    }

    public void removeUsedValue(Value usedValue) {
        for (Use use : usedList) {
            if (use.getUsed().equals(usedValue)) {
                usedList.remove(use);
                break;
            }
        }
    }

    protected void updateUsed(Value oldUsed, Value newUsed) {
        //add newUsed to this usedList
        for (int i = 0; i < usedList.size(); i++) {
            if (usedList.get(i).getUsed() == oldUsed) {
                usedList.set(i, new Use(this, newUsed));
                break;
            }
        }

        //remove this from oldUsed userList
        oldUsed.removeUser(this);

        //add this to newUsed userList
        newUsed.addUser(this);
    }

    public void setUsed(int index, Value newUsed) {
        Value oldUsed = getUsedValue(index);

        updateUsed(oldUsed, newUsed);
    }

    public Value getUsedValue(int index) {
        return usedList.size() > index ? usedList.get(index).getUsed() : null;
    }

    public ArrayList<Value> getUsedValueList() {
        return usedList.stream().map(Use::getUsed).collect(Collectors.toCollection(ArrayList::new));
    }
}
