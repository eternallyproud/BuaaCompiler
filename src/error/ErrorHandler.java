package error;

import utils.InOut;
import utils.Tools;

import java.util.ArrayList;
import java.util.Comparator;

public class ErrorHandler {
    public static final ErrorHandler ERROR_HANDLER = new ErrorHandler();
    private final ArrayList<Error> errorList;

    private ErrorHandler() {
        errorList = new ArrayList<>();
    }

    public void addError(Error error) {
        System.out.println("第 " + error.getLine() + " 行出现错误: " + error.getErrorType().name());
        errorList.add(error);
    }

    public void writeErrors() {
        sortErrors();
        if (!errorList.isEmpty()) {
            InOut.writeError(Tools.arrayListToString(errorList));
        }
    }

    private void sortErrors() {
        errorList.sort(Comparator.comparingInt(Error::getLine));
    }
}
