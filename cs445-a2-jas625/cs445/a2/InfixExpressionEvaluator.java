package cs445.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.EmptyStackException;

/**
 * This class uses two stacks to evaluate an infix arithmetic expression from an
 * InputStream. It should not create a full postfix expression along the way; it
 * should convert and evaluate in a pipelined fashion, in a single pass.
 */
public class InfixExpressionEvaluator {
    // Tokenizer to break up our input into tokens
    StreamTokenizer tokenizer;

    // Stacks for operators (for converting to postfix) and operands (for
    // evaluating)
    StackInterface<Character> operatorStack;
    StackInterface<Double> operandStack;

    // precedence for operators
    private static final int EXPONENTIATION = 4;
    private static final int MULTIPLICATION = 3;
    private static final int DIVISION = 3;
    private static final int ADDITION = 2;
    private static final int SUBTRACTION = 2;

    /**
     * Initializes the evaluator to read an infix expression from an input
     * stream.
     * @param input the input stream from which to read the expression
     */
    public InfixExpressionEvaluator(InputStream input) {
        // Initialize the tokenizer to read from the given InputStream
        tokenizer = new StreamTokenizer(new BufferedReader(
                        new InputStreamReader(input)));

        // StreamTokenizer likes to consider - and / to have special meaning.
        // Tell it that these are regular characters, so that they can be parsed
        // as operators
        tokenizer.ordinaryChar('-');
        tokenizer.ordinaryChar('/');

        // Allow the tokenizer to recognize end-of-line, which marks the end of
        // the expression
        tokenizer.eolIsSignificant(true);

        // Initialize the stacks
        operatorStack = new ArrayStack<Character>();
        operandStack = new ArrayStack<Double>();
    }

    /**
     * Parses and evaluates the expression read from the provided input stream,
     * then returns the resulting value
     * @return the value of the infix expression that was parsed
     */
    public Double evaluate() throws InvalidExpressionException {
        // Get the first token. If an IO exception occurs, replace it with a
        // runtime exception, causing an immediate crash.
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Continue processing tokens until we find end-of-line
        while (tokenizer.ttype != StreamTokenizer.TT_EOL) {
            // Consider possible token types
            switch (tokenizer.ttype) {
                case StreamTokenizer.TT_NUMBER:
                    // If the token is a number, process it as a double-valued
                    // operand
                    handleOperand((double)tokenizer.nval);
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '^':
                    // If the token is any of the above characters, process it
                    // is an operator
                    handleOperator((char)tokenizer.ttype);
                    break;
                case '(':
                case '{':
                    // If the token is open bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
                    handleOpenBracket((char)tokenizer.ttype);
                    break;
                case ')':
                case '}':
                    // If the token is close bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
                    handleCloseBracket((char)tokenizer.ttype);
                    break;
                case StreamTokenizer.TT_WORD:
                    // If the token is a "word", throw an expression error
                    throw new InvalidExpressionException("Unrecognized symbol: " +
                                    tokenizer.sval);
                default:
                    // If the token is any other type or value, throw an
                    // expression error
                    throw new InvalidExpressionException("Unrecognized symbol: " +
                                    String.valueOf((char)tokenizer.ttype));
            }

            // Read the next token, again converting any potential IO exception
            try {
                tokenizer.nextToken();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Almost done now, but we may have to process remaining operators in
        // the operators stack
        handleRemainingOperators();

        double result;

        if (operandStack.isEmpty()) {
            result = 0;
        } else {
            result = operandStack.pop();
        }

        if (!operatorStack.isEmpty()) {
            throw new InvalidExpressionException("Too many operators");
        } else if (!operandStack.isEmpty()) {
            throw new InvalidExpressionException("Too many operands");
        }

        // Return the result of the evaluation
        // TODO: Fix this return statement
        return result;
    }

    /**
     * This method is called when the evaluator encounters an operand. It
     * manipulates operatorStack and/or operandStack to process the operand
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     * @param operand the operand token that was encountered
     */
    void handleOperand(double operand) {
        operandStack.push(operand);
    }

    /**
     * This method is called when the evaluator encounters an operator. It
     * manipulates operatorStack and/or operandStack to process the operator
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     * @param operator the operator token that was encountered
     */
    void handleOperator(char operator) {
        while (!operatorStack.isEmpty() && !isStrictlyHigherPrecedence(operator,
                    operatorStack.peek()) && !isOpenBracket(operator)) {

            operandStack.push(popAndCalculate());

        }
        operatorStack.push(operator);
    }

    /**
     * This method is called when the evaluator encounters an open bracket. It
     * manipulates operatorStack and/or operandStack to process the open bracket
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     * @param openBracket the open bracket token that was encountered
     */
    void handleOpenBracket(char openBracket) {
        operatorStack.push(openBracket);
    }

    /**
     * This method is called when the evaluator encounters a close bracket. It
     * manipulates operatorStack and/or operandStack to process the close
     * bracket according to the Infix-to-Postfix and Postfix-evaluation
     * algorithms.
     * @param closeBracket the close bracket token that was encountered
     */
    void handleCloseBracket(char closeBracket) {
        if (operatorStack.isEmpty()) {
            throw new InvalidExpressionException("Too many closed brackets");
        }
        while ((closeBracket == ')' && operatorStack.peek() != '(')
                || (closeBracket == '}' && operatorStack.peek() != '{')) {
            boolean throwException =
                    (closeBracket == ')' && operatorStack.peek() == '{') ||
                            (closeBracket == '}' && operatorStack.peek() == '(');

            if (throwException) {
                throw new InvalidExpressionException("Mismatched brackets");
            }

            operandStack.push(popAndCalculate());
        }
        operatorStack.pop();
    }

    /**
     * This method is called when the evaluator encounters the end of an
     * expression. It manipulates operatorStack and/or operandStack to process
     * the operators that remain on the stack, according to the Infix-to-Postfix
     * and Postfix-evaluation algorithms.
     */
    void handleRemainingOperators() {
        while (!operatorStack.isEmpty()) {
            if (isOpenBracket(operatorStack.peek()) || isCloseBracket(operatorStack.peek())) {
                throw new InvalidExpressionException("Mismatched brackets");
            }
            operandStack.push(popAndCalculate());
        }
    }

    private boolean isOpenBracket(char operator) {
        return operator == '(' || operator =='{';
    }

    private boolean isCloseBracket(char operator) {
        return operator == ')' || operator =='}';
    }

    /**
     * Returns the precedence as an integer of each operator
     *
     * @param c the operator which presedence you require
     * @return the precedence of a specific operator
     */
    private int getPrecedence(char c) {
        switch (c) {
            case '^': return EXPONENTIATION;
            case '*': return MULTIPLICATION;
            case '/': return DIVISION;
            case '+': return ADDITION;
            case '-': return SUBTRACTION;
            default : return -1;
        }
    }

    /**
     * Returns true if the first operator is scrictly higher precedence than
     * the second operator (not equal to, only greater)
     *
     * @param first the operator
     * @param second the operator to compare against
     * @return true if the first operator's precedence is greater than the
     * seconds
     */
    private boolean isStrictlyHigherPrecedence(char first, char second) {
        return getPrecedence(first) > getPrecedence(second);
    }

    private double calculate(double one, double two, char c) {
        switch (c) {
            case '^': return Math.pow(one, two);
            case '*': return one * two;
            case '/': return one / two;
            case '+': return one + two;
            case '-': return one - two;
            default: throw new InvalidExpressionException("Operation of \""
                    + one + " " + c + " " + two + " failed");
        }
    }

    private double popAndCalculate() {
        char operator;
        double operandTwo;
        double operandOne;

        try {
            operandTwo = operandStack.pop();
            operandOne = operandStack.pop();
        } catch (EmptyStackException e) {
            String msg;
            if (operatorStack.isEmpty()) {
                msg = "Too many operands";
            } else {
                msg = "Too many operators";
            }
            throw new InvalidExpressionException(msg);
        }

        try {
            operator = operatorStack.pop();
        } catch (EmptyStackException e) {
            throw new InvalidExpressionException("Not enough operators");
        }

        return calculate(operandOne, operandTwo,
                operator);
    }


    /**
     * Creates an InfixExpressionEvaluator object to read from System.in, then
     * evaluates its input and prints the result.
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Infix expression:");
        InfixExpressionEvaluator evaluator =
                        new InfixExpressionEvaluator(System.in);
        Double value = null;

        try {
            value = evaluator.evaluate();
        } catch (InvalidExpressionException e) {
            System.out.println("Invalid expression: " + e.getMessage());
        }
        if (value != null) {
            System.out.println(value);
        }
    }

}

