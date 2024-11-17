package frontend.symbol;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import frontend.token.Token;
import utils.InOut;

import java.util.ArrayList;
import java.util.TreeMap;

public class SymbolTable {
    public static final SymbolTable SYMBOL_TABLE = new SymbolTable();
    private final TreeMap<Integer, ArrayList<Symbol>> symbolTable;
    private final ArrayList<Integer> scopeStack;
    private int currentScope;
    private int maxScope;
    private int loopDepth;

    private SymbolTable() {
        symbolTable = new TreeMap<>();
        scopeStack = new ArrayList<>();
        maxScope = 0;
        loopDepth = 0;
    }

    private void addSymbol(Symbol symbol) {
        symbolTable.get(currentScope).add(symbol);
    }

    private Symbol getSymbolInScope(String name, int scope) {
        for (Symbol symbol : symbolTable.get(scope)) {
            if (symbol.getName().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    private Symbol getSymbolInScopeStack(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Symbol symbol = getSymbolInScope(name, scopeStack.get(i));
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    private boolean existSymbolInScope(String name, int scope) {
        return getSymbolInScope(name, scope) != null;
    }

    private boolean existSymbolInScopeStack(String name) {
        return getSymbolInScopeStack(name) != null;
    }

    public void addScope() {
        maxScope++;
        symbolTable.put(maxScope, new ArrayList<>());
        currentScope = maxScope;
        scopeStack.add(currentScope);
    }

    public void removeScope() {
        scopeStack.remove(scopeStack.size() - 1);
        currentScope = !scopeStack.isEmpty() ? scopeStack.get(scopeStack.size() - 1) : 0;
    }

    public void addLoopDepth() {
        loopDepth++;
    }

    public void reduceLoopDepth() {
        loopDepth--;
    }

    //add NumericalSymbol to symbolTable
    public void tackle(Token token, DataType type) {
        String name = token.getContent();
        if (existSymbolInScope(name, currentScope)) {
            handleRedeclaredIdentError(token);
        } else {
            NumericalSymbol symbol = null;
            switch (type) {
                case INT -> symbol = new IntSymbol(name);
                case CHAR -> symbol = new CharSymbol(name);
                case CONST_INT -> symbol = new ConstIntSymbol(name);
                case CONST_CHAR -> symbol = new ConstCharSymbol(name);
                case INT_ARRAY -> symbol = new IntArraySymbol(name);
                case CHAR_ARRAY -> symbol = new CharArraySymbol(name);
                case CONST_INT_ARRAY -> symbol = new ConstIntArraySymbol(name);
                case CONST_CHAR_ARRAY -> symbol = new ConstCharArraySymbol(name);
                default -> handleUndeclaredIdentError(token);
            }
            addSymbol(symbol);
        }
    }

    public void tackle(Token token) {
        switch (token.getType()) {
            //use Symbol
            case IDENFR -> {
                if (!existSymbolInScopeStack(token.getContent())) {
                    handleUndeclaredIdentError(token);
                }
            }
            //illegal continue or break
            case BREAKTK, CONTINUETK -> {
                if (loopDepth <= 0) {
                    handleRedundantBreakContinueError(token);
                }
            }
            //mismatch return type
            case RETURNTK -> handleMismatchedReturnTypeError(token);

            //missing return
            case RBRACE -> handleMissingReturnError(token);

        }
    }

    //add FuncSymbol to symbolTable
    public void tackle(Token token, DataType type, ArrayList<NumericalSymbol> parameters) {
        String name = token.getContent();
        if (existSymbolInScope(name, currentScope)) {
            handleRedeclaredIdentError(token);
        } else {
            FuncSymbol symbol = null;
            switch (type) {
                case INT_FUNC -> symbol = new IntFuncSymbol(name, parameters);
                case CHAR_FUNC -> symbol = new CharFuncSymbol(name, parameters);
                case VOID_FUNC -> symbol = new VoidFuncSymbol(name, parameters);
                default -> handleUnexpectedError(token);
            }
            addSymbol(symbol);
        }
    }

    //add FuncFParam to symbolTable
    public void tackle(ArrayList<Token> parameterTokens, ArrayList<NumericalSymbol> parameterSymbols) {
        for (int i = 0; i < parameterTokens.size(); i++) {
            Token token = parameterTokens.get(i);
            if (existSymbolInScope(token.getContent(), currentScope)) {
                handleRedeclaredIdentError(token);
            } else {
                addSymbol(parameterSymbols.get(i));
            }
        }
    }

    //call Function
    public void tackle(Token token, ArrayList<DataType> parameterDataTypes) {
        tackle(token);
        Symbol symbol = getSymbolInScopeStack(token.getContent());
        if (symbol instanceof FuncSymbol funcSymbol) {
            if (parameterDataTypes.size() != (funcSymbol.getParameters().size())) {
                handleMismatchedFunctionParameterNumberError(token);
            } else {
                for (int i = 0; i < parameterDataTypes.size(); i++)
                    if (!parameterDataTypes.get(i).equals(funcSymbol.getParameters().get(i).getType())) {
                        switch (funcSymbol.getParameters().get(i).getType()) {
                            case INT_ARRAY, CHAR_ARRAY -> {
                                handleMismatchedFunctionParameterTypeError(token);
                                return;
                            }
                            case INT, CHAR -> {
                                if (parameterDataTypes.get(i) == DataType.INT_ARRAY || parameterDataTypes.get(i) == DataType.CHAR_ARRAY) {
                                    handleMismatchedFunctionParameterTypeError(token);
                                    return;
                                }
                            }
                        }
                    }
            }
        } else {
            handleUnexpectedError(token);
        }
    }

    //assign to Symbol
    public void tackle(Token token, boolean reduce) {
        DataType lValDataType = getNumericalDataType(token, reduce);
        if (lValDataType == DataType.UNEXPECTED) {
            handleUnexpectedError(token);
        } else if (!lValDataType.canBeAssignedTo()) {
            handleUnmodifiableLvalueError(token);
        }
    }

    public DataType getFunctionReturnDataType(Token token) {
        Symbol symbol = getSymbolInScopeStack(token.getContent());
        if (symbol instanceof FuncSymbol funcSymbol) {
            return funcSymbol.getReturnType();
        } else {
            return DataType.UNEXPECTED;
        }
    }

    public DataType getNumericalDataType(Token token, boolean reduce) {
        Symbol symbol = getSymbolInScopeStack(token.getContent());
        if (symbol instanceof NumericalSymbol numericalSymbol) {
            return reduce ? numericalSymbol.getType().getReducedDataType() : numericalSymbol.getType();
        } else {
            return DataType.UNEXPECTED;
        }
    }

    //Error: b
    private void handleRedeclaredIdentError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.REDECLARED_IDENT_ERROR));
    }

    //Error: c
    private void handleUndeclaredIdentError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.UNDECLARED_IDENT_ERROR));
    }

    //Error: d
    private void handleMismatchedFunctionParameterNumberError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.MISMATCHED_FUNCTION_PARAMETER_NUMBER_ERROR));
    }

    //Error: e
    private void handleMismatchedFunctionParameterTypeError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.MISMATCHED_FUNCTION_PARAMETER_TYPE_ERROR));
    }

    //Error: f
    private void handleMismatchedReturnTypeError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.MISMATCHED_RETURN_TYPE_ERROR));
    }

    //Error: g
    private void handleMissingReturnError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.MISSING_RETURN_ERROR));
    }

    //Error: h
    private void handleUnmodifiableLvalueError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.UNMODIFIABLE_LVALUE));
    }

    //Error: m
    private void handleRedundantBreakContinueError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.REDUNDANT_BREAK_CONTINUE_ERROR));
    }

    //Error: u
    private void handleUnexpectedError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(token.getLine(), ErrorType.UNEXPECTED_ERROR));
    }

    public void writeSymbolTable() {
        InOut.writeSemanticResult(toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= symbolTable.size(); i++) {
            for (Symbol symbol : symbolTable.get(i)) {
                sb.append(i).append(" ").append(symbol).append('\n');
            }
        }
        return sb.toString();
    }
}
