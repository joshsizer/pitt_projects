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

    private boolean wasNotNumber;
    private boolean shouldNegateNumber;

    // precedence for operators
    private static final int EXPONENTIATION = 4;
    private static final int MULTIPLICATION = 3;
    private static final int DIVISION = 3;
    private static final int ADDITION = 2;
    private static final int SUBTRACTION = 2;

    private static final char SIN = 's';
    private static final char COS = 'c';
    private static final char TAN = 't';
    private static final char MAX = 'x';
    private static final char MIN = 'n';
    private static final char CEIL = 'l';
    private static final char FLOOR = 'f';

    /**
     * Initializes the evaluator to read an infix expression from an input
     * stream.
     *
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
        wasNotNumber = true;
        shouldNegateNumber = false;
    }

    /**
     * Parses and evaluates the expression read from the provided input stream,
     * then returns the resulting value
     *
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
                    handleOperand((double) tokenizer.nval);
                    wasNotNumber = false;
                    break;
                case '-':
                case '+':
                case '*':
                case '/':
                case '^':
                    // If the token is any of the above characters, process it
                    // is an operator
                    handleOperator((char) tokenizer.ttype);
                    wasNotNumber = true;
                    break;
                case '(':
                case '{':
                    // If the token is open bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
                    handleOpenBracket((char) tokenizer.ttype);
                    wasNotNumber = true;
                    break;
                case ')':
                case '}':
                    // If the token is close bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
                    handleCloseBracket((char) tokenizer.ttype);
                    wasNotNumber = false;
                    break;
                case ',':
                    handleComma();
                    wasNotNumber = true;
                    break;
                case StreamTokenizer.TT_WORD:
                    String word = tokenizer.sval.toLowerCase();
                    switch (word) {
                        case "sin":
                        case "cos":
                        case "tan":
                        case "max":
                        case "min":
                        case "ceil":
                        case "floor":
                            handleFunction(word);
                            wasNotNumber = true;
                            break;
                        case "pi":
                            handleOperand(Math.PI);
                            break;
                        default:
                            // If the token is a "word", throw an expression error
                            throw new InvalidExpressionException("Unrecognized symbol: " +
                                    tokenizer.sval);

                    }
                    break;
                default:
                    // If the token is any other type or value, throw an
                    // expression error
                    throw new InvalidExpressionException("Unrecognized " +
                            "symbol: " +
                            String.valueOf((char) tokenizer.ttype));
            }

            // Read the next token, again converting any potential IO exception
            try {
                tokenizer.nextToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Almost done now, but we may have to process remaining operators in
        // the operators stack
        handleRemainingOperators();

        if (operandStack.isEmpty()) {
            throw new InvalidExpressionException("Not enough operands");
        }

        double result = operandStack.pop();

        if (!operatorStack.isEmpty()) {
            throw new InvalidExpressionException("Too many operators");
        } else if (!operandStack.isEmpty()) {
            throw new InvalidExpressionException("Too many operands");
        }

        // Return the result of the evaluation
        // TODO: Fix this return statement
        return result;
    }

    void handleComma() {
        while (operatorStack.peek() != '(') {
            operandStack.push(popAndCalculate());
        }
    }
    /**
     * This method is called when the evaluator encounters an operand. It
     * manipulates operatorStack and/or operandStack to process the operand
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     *
     * @param operand the operand token that was encountered
     */
    void handleOperand(double operand) {
        if (shouldNegateNumber) {
            operand *= -1.0;
            shouldNegateNumber = false;
        }
        operandStack.push(operand);
    }

    /**
     * This method is called when the evaluator encounters an operator. It
     * manipulates operatorStack and/or operandStack to process the operator
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     *
     * @param operator the operator token that was encountered
     */
    void handleOperator(char operator) {
        if (wasNotNumber && operator == '-') {
            shouldNegateNumber = !shouldNegateNumber;
            return;
        }
        while (!isFunction(operator) && !operatorStack.isEmpty() && !isStrictlyHigherPrecedence(operator,
                operatorStack.peek()) && !isOpenBracket(operator)) {

            operandStack.push(popAndCalculate());
            String thiss = "hello";
            int[] i = new int[thiss.length()];

        }
        operatorStack.push(operator);
    }

    /**
     * This method is called when the evaluator encounters an open bracket. It
     * manipulates operatorStack and/or operandStack to process the open bracket
     * according to the Infix-to-Postfix and Postfix-evaluation algorithms.
     *
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
     *
     * @param closeBracket the close bracket token that was encountered
     */
    void handleCloseBracket(char closeBracket) {
        if (operatorStack.isEmpty())
            throw new InvalidExpressionException("Too many closed brackets");

        while ((closeBracket == ')' && operatorStack.peek() != '(')
                || (closeBracket == '}' && operatorStack.peek() != '{')) {

            if ( (closeBracket == ')' && operatorStack.peek() == '{') ||
                    (closeBracket == '}' && operatorStack.peek() == '(') ) {
                throw new InvalidExpressionException("Mismatched brackets");
            }

            operandStack.push(popAndCalculate());
        }
        operatorStack.pop();
        if (!operatorStack.isEmpty() && isFunction(operatorStack.peek())) {
            operandStack.push(popAndCalculate());
        }
    }

    void handleFunction(String func) {
        char operator = ' ';
        switch (func) {
            case "sin": operator = SIN; break;
            case "cos": operator = COS; break;
            case "tan": operator = TAN; break;
            case "max": operator = MAX; break;
            case "min": operator = MIN; break;
            case "ceil": operator = CEIL; break;
            case "floor": operator = FLOOR; break;
            default: break;
        }
        handleOperator(operator);
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

    private boolean isFunction(char c) {
        return c == SIN || c == COS || c == TAN || c == MAX || c == MIN
                || c == CEIL || c == FLOOR;
    }

    private boolean isOpenBracket(char operator) {
        return operator == '(' || operator == '{';
    }

    private boolean isCloseBracket(char operator) {
        return operator == ')' || operator == '}';
    }

    /**
     * Returns the precedence as an integer of each operator
     *
     * @param c the operator which presedence you require
     * @return the precedence of a specific operator
     */
    private int getPrecedence(char c) {
        switch (c) {
            case '^':
                return EXPONENTIATION;
            case '*':
                return MULTIPLICATION;
            case '/':
                return DIVISION;
            case '+':
                return ADDITION;
            case '-':
                return SUBTRACTION;
            default:
                return -1;
        }
    }

    /**
     * Returns true if the first operator is scrictly higher precedence than
     * the second operator (not equal to, only greater)
     *
     * @param first  the operator
     * @param second the operator to compare against
     * @return true if the first operator's precedence is greater than the
     * seconds
     */
    private boolean isStrictlyHigherPrecedence(char first, char second) {
        return getPrecedence(first) > getPrecedence(second);
    }

    private double calculate(double one, char c) {
        switch (c) {
            case SIN: return Math.sin(one);
            case COS: return Math.cos(one);
            case TAN: return Math.tan(one);
            case FLOOR: return Math.floor(one);
            case CEIL: return Math.ceil(one);
            default:
                throw new InvalidExpressionException("Operation failed");
        }
    }

    private double calculate(double one, double two, char c) {
        switch (c) {
            case '^':
                return Math.pow(one, two);
            case '*':
                return one * two;
            case '/':
                if (two == 0) throw new InvalidExpressionException(
                        "Cannot divide by 0!");
                return one / two;
            case '+':
                return one + two;
            case '-':
                return one - two;
            case MAX:
                return Math.max(one, two);
            case MIN:
                return Math.min(one, two);
            default:
                throw new InvalidExpressionException("Operation of \""
                        + one + " " + c + " " + two + " failed");
        }
    }

    private double popAndCalculate() {
        char operator;
        double operandTwo;
        double operandOne;

        try {
            operator = operatorStack.pop();
        } catch (EmptyStackException e) {
            throw new InvalidExpressionException("Not enough operators");
        }

        try {
            operandTwo = operandStack.pop();
        } catch (EmptyStackException e) {
            throw new InvalidExpressionException("Too many operators");
        }

        if (isFunction(operator) && operator != MAX && operator != MIN) {
            return calculate(operandTwo, operator);
        }

        try {
            operandOne = operandStack.pop();
        } catch (EmptyStackException e) {
            throw new InvalidExpressionException("Too many operators");
        }

        return calculate(operandOne, operandTwo,
                operator);
    }


    /**
     * Creates an InfixExpressionEvaluator object to read from System.in, then
     * evaluates its input and prints the result.
     *
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

