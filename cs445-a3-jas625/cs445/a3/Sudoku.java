package cs445.a3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

/**
 * I have chosen to represent cells that were originally on the board using
 * negative numbers. The valid number range is as follows:
 * [-9, -1] : prefilled, immutable numbers
 * 0        : empty cells
 * [1. 9]   : program added values that can be modified to fit solutions
 */
public class Sudoku {

    public static final String boardsPath = new File("").getAbsolutePath() +
            "/boards";

    private static class Test {
        int[][] board;
        boolean shouldEvalute;
        String fileName;
    }

    /**
     * Checks if the board is filled with numbers (no 0s in any spaces)
     *
     * @param board
     * @return
     */
    static boolean isFullSolution(int[][] board) {
        for (int[] row : board) {
            for (int x : row) {
                if (x == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if we should reject a partial solution. We should reject a
     * partial solution in one of 3 cases:
     *
     * 1) There are two of the same numbers in the same row
     * 2) There are two of the same numbers in the same column
     * 3) There are two of the same numbers in a 3x3 section
     *
     * @param board The partial solution to accept of reject
     * @return True if this board should be rejected, false if it should be
     * accepted
     */
    static boolean reject(int[][] board) {
        // case #1, two of the same numbers in the same row
        for (int x = 0; x < 9; x++) {
            // use an array to keep track of which number has already appeared
            boolean[] tracking = new boolean[9];
            for (int y = 0; y < 9; y++) {
                int cellVal = Math.abs(board[x][y]);

                if (cellVal == 0) {
                    continue;
                } else if (tracking[cellVal-1]) {
                    return true;
                } else {
                    tracking[cellVal-1] = true;
                }
            }
        }

        // case #2, two of the same numbers in the same column
        for (int y = 0; y < 9; y++) {
            // use an array to keep track of which number has already appeared
            boolean[] tracking = new boolean[9];
            for (int x = 0; x < 9; x++) {
                int cellVal = Math.abs(board[x][y]);

                if (cellVal == 0) {
                    continue;
                } else if (tracking[cellVal-1]) {
                    return true;
                } else {
                    tracking[cellVal-1] = true;
                }
            }
        }

        // case #3, two of the same numbers in the same 3x3
        int x = 0;
        int y = 0;
        for (int i = 0; i < 9; i++) {
            boolean[] tracking = new boolean[9];
            for (int j = 0; j < 9; j++) {
                int cellVal = Math.abs(board[x][y]);

                if (cellVal == 0) {
                    // don't worry about it because it's empty
                } else if (tracking[cellVal-1]) {
                    return true;
                } else {
                    tracking[cellVal-1] = true;
                }

                // this logic seems a bit funny to me: I feel like it could
                // be done in a better fashion/with the variable i and j, but
                // this works so I'll use it
                if ((x + 1) % 3 == 0) {
                    x = x - 2;
                    y++;
                } else {
                    x++;
                }
            }
            // same here
            if ((i + 1) % 3 == 0) {
                x = 0;
            } else {
                x = x + 3;
                y = y - 3;
            }
        }

        return false;
    }

    /**
     * This method finds the next 0 in the board and replaces it with a 1. We
     * traverse the board in a row by row fashion.
     *
     * @param board The partial board to extend
     * @return The extended board
     */
    static int[][] extend(int[][] board) {
        // new array to return
        int[][] newBoard = new int[9][9];
        boolean foundExtend = false;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                newBoard[x][y] = board[x][y];
                if (newBoard[x][y] == 0 && !foundExtend) {
                    newBoard[x][y] = 1;
                    foundExtend = true;
                }
            }
        }
        if (foundExtend) {
            return newBoard;
        } else {
            return null;
        }
    }

    /**
     * This method finds the first program modified value who's proceding
     * program modifiable value is 0, and increases it
     *
     * @param board The board to modify
     * @return The modified board
     */
    static int[][] next(int[][] board) {
        int modifyIndexX = -1;
        int modifyIndexY = -1;

        boolean modified = false;
        int[][] newBoard = new int[9][9];

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                newBoard[x][y] = board[x][y];
                if (modifyIndexX == -1 && newBoard[x][y] == 0) {
                    return null;
                } else if (!modified && newBoard[x][y] > 0) {
                    modifyIndexX = x;
                    modifyIndexY = y;
                } else if (newBoard[x][y] == 0) {
                    modified = true;
                }
            }
        }

        if (modifyIndexX == -1 || newBoard[modifyIndexX][modifyIndexY] + 1 > 9) {
            return null;
        } else {
            newBoard[modifyIndexX][modifyIndexY]++;
            return newBoard;
        }
    }

    static void testIsFullSolution() {
        Test[] tests = getTests("full_solution");
        if (tests == null) {
            System.out.println("Could not find any fullSolution() tests!");
        }

        for (Test t : tests) {
            printBoard(t.board);
            System.out.println("The function fullSolution() on this board " +
                    "should return ===== " + t.shouldEvalute + " =====");
            boolean result = isFullSolution(t.board);
            System.out.print("This board actually evaluates ----- " + result);
            if (t.shouldEvalute == result) {
                System.out.println(" -----, test OK");
            } else {
                System.out.println("----- !!! TEST FAILED !!!");
            }
        }
    }

    static void testReject() {
        Test[] tests = getTests("reject");
        if (tests == null) {
            System.out.println("Could not find any reject() tests!");
        }

        for (Test t : tests) {
            printBoard(t.board);
            System.out.println("The function reject() on this board " +
                    "should return ===== " + t.shouldEvalute + " =====");
            boolean result = reject(t.board);
            System.out.print("This board actually evaluates ----- " + result);
            if (t.shouldEvalute == result) {
                System.out.println(" -----, test OK");
            } else {
                System.out.println("----- !!! TEST FAILED !!!");
            }
        }
    }

    static void testExtend() {
        int[][] test1 = {{0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0}};

        System.out.println("Testing extend() on this board:");
        printBoard(test1);
        System.out.println("The method extend() on this board should place a " +
                "'1' in the top left:");
        int[][] test1Extend = extend(test1);
        printBoard(test1Extend);
        if (test1Extend[0][0] != 1) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

        int[][] test2 = {{-3, -4, -1, -8, -9, -2, -5, -6, -8},
                        {-1, 0, -2, -3, -4, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, -4, 0, 0, 0},
                        {0, 0, 0, 0, -2, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, -1, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, -9, -8, -7, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0}};

        System.out.println("Testing extend() on this board:");
        printBoard(test2);
        System.out.println("The method extend() on this board should place a " +
                "'1' at the coordinate (1, 1):");
        int[][] test2Extend = extend(test2);
        printBoard(test2Extend);
        if (test2Extend[1][1] != 1) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

        int[][] test3 = {{-3, -4, -1, -8, -9, -2, -5, -6, -8},
                        {-1, -2, -2, -3, -4, -2, -2, -2, -2},
                        {-5, -6, -7, -8, -9, -4, -1, -2, -3},
                        {-3, -4, -5, -5, -2, -3, -1, -7, -9},
                        {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                        {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                        {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                        {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                        {-1, -2, -3, -4, -5, -6, -8, -9, -7}};

        System.out.println("Testing extend() on this board:");
        printBoard(test3);
        System.out.println("The method extend() on this board should return " +
                "----- NULL -----");
        int[][] test3Extend = extend(test3);
        if (test3Extend != null) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

    }

    static void testNext() {
        int[][] test1 = {{0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}};

        System.out.println("Testing next() on this board:");
        printBoard(test1);
        System.out.println("The method next() on this board should return " +
                "----- NULL -----");
        int[][] test1Extend = next(test1);
        if (test1Extend != null) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

        int[][] test2 = {{-3, -4, -1, -8, -9, -2, -5, -6, -8},
                {-1, 4, -2, -3, -4, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, -4, 0, 0, 0},
                {0, 0, 0, 0, -2, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, -1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, -9, -8, -7, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}};

        System.out.println("Testing next() on this board:");
        printBoard(test2);
        System.out.println("The method next() on this board should place a " +
                "'5' at the coordinate (1, 1):");
        int[][] test2Extend = next(test2);
        printBoard(test2Extend);
        if (test2Extend[1][1] != 5) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

        int[][] test3 = {{-3, -4, -1, -8, -9, -2, -5, -6, -8},
                {-1, -2, -2, -3, -4, -2, -2, -2, -2},
                {-5, -6, -7, -8, -9, -4, -1, -2, -3},
                {-3, -4, -5, -5, -2, -3, -1, -7, -9},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7}};

        System.out.println("Testing next() on this board:");
        printBoard(test3);
        System.out.println("The method next() on this board should return " +
                "----- NULL -----");
        int[][] test3Extend = next(test3);
        if (test3Extend != null) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }

        int[][] test4 = {{-3, -4, -1, -8, -9, -2, -5, -6, -8},
                {-1, -2, -2, -3, -4, -2, -2, -2, -2},
                {-5, -6, -7, -8, -9, -4, -1, -2, -3},
                {-3, -4, -5, -5, -2, -3, -1, -7, -9},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, -7},
                {-1, -2, -3, -4, -5, -6, -8, -9, 7}};

        System.out.println("Testing next() on this board:");
        printBoard(test4);
        System.out.println("The method next() on this board should place a " +
                "'8' at the coordinate (8, 8):");
        int[][] test4Extend = next(test4);
        printBoard(test4Extend);
        if (test4Extend[8][8] != 8) {
            System.out.println("----- !!!!TEST FAILED!!!! -----");
        } else {
            System.out.println("----- test OK -----");
        }
    }

    static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("No assignment");
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 6) {
                System.out.println("----+-----+----");
            }
            for (int j = 0; j < 9; j++) {
                int value = Math.abs(board[i][j]);

                if (j == 2 || j == 5) {
                    System.out.print(value + " | ");
                } else {
                    System.out.print(value);
                }
            }
            System.out.print("\n");
        }
    }

    static int[][] readBoard(String filename) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
        int[][] board = new int[9][9];
        int val = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    val =
                            -1 * Integer.parseInt(Character.toString(lines.get(i).charAt(j)));
                } catch (Exception e) {
                    val = 0;
                }
                board[i][j] = val;
            }
        }
        return board;
    }

    /**
     * Returns an array of test boards for the particular function
     *
     * @param testFuntion the name of the test function as it appears in the
     *                    files. Test files should have the name
     *                    t_test_funtion_t where the first t indicates a test
     *                    file, test_function would indicate files meant for
     *                    the testFunction(), and the last t can be either a
     *                    t or f, indicating the expected return value of the
     *                    function.
     * @return
     */
    private static Test[] getTests(String testFuntion) {
        File boardsFolder = new File(boardsPath);
        String expectedFileName = "t_" + testFuntion;
        ArrayList<Test> tests = new ArrayList<>();

        if (boardsFolder.listFiles() == null) {
            return null;
        }

        for (File board : boardsFolder.listFiles()) {
            String fileName = board.getName();
            if (fileName.substring(0, fileName.length() - 7).equals(expectedFileName)) {
                Test t = new Test();
                t.fileName = fileName;
                t.board = readBoard(board.getAbsolutePath());
                t.shouldEvalute =
                        fileName.charAt(fileName.length() - 4) == 't';
                tests.add(t);
            }
        }

        return tests.toArray(new Test[0]);
    }

    static int[][] solve(int[][] board) {
        if (reject(board)) return null;
        if (isFullSolution(board)) return board;
        int[][] attempt = extend(board);
        while (attempt != null) {
            int[][] solution = solve(attempt);
            if (solution != null) return solution;
            attempt = next(attempt);
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please enter -t for test methods or the " +
                    "absolute file path to the sudoku file to solve");
        } else if (args[0].equals("-t")) {
            System.out.println("Testing mode:\n");
            testIsFullSolution();
            testReject();
            testExtend();
            testNext();
        } else {
            int[][] board = readBoard(args[0]);
            if (board == null) {
                System.out.println("Could not find file!");
                return;
            }
            System.out.println("Input board:");
            printBoard(board);
            long start = System.nanoTime();
            int[][] solution = solve(board);
            double total = (System.nanoTime() - start) / 1e9;
            System.out.println("solve() took " + total + "(s) to run.");
            if (solution == null) {
                System.out.println("No solution to this board!");
            } else {
                System.out.println("Solution:\n");
                printBoard(solution);
            }
        }
    }
}

