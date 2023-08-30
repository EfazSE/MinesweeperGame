import java.util.Random;
import java.util.Scanner;

public class Minesweeper {

    private static long startTime;
    private static long endTime;

    private static final int BEGINNER = 0;
    private static final int INTERMEDIATE = 1;
    private static final int ADVANCED = 2;
    private static final int MAXSIDE = 25;
    private static final int MAXMINES = 99;
    private static final int MOVESIZE = 526; // (25 * 25 - 99)

    private static int SIDE;
    private static int MINES;

    private static boolean isValid(int row, int col) {
        return (row >= 0) && (row < SIDE) && (col >= 0) && (col < SIDE);
    }

    private static boolean isMine(int row, int col, char[][] board) {
        return (board[row][col] == '*');
    }

    private static void makeMove(int[] move) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your move, (row, column) -> ");
        move[0] = scanner.nextInt();
        move[1] = scanner.nextInt();
    }

    private static void printBoard(char[][] myBoard) {
        System.out.print("    ");
        for (int i = 0; i < SIDE; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n");
        for (int i = 0; i < SIDE; i++) {
            System.out.print(i + "   ");
            for (int j = 0; j < SIDE; j++) {
                System.out.print(myBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int countAdjacentMines(int row, int col, int[][] mines, char[][] realBoard) {
        int count = 0;
        int[] rowArr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colArr = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int newRow = row + rowArr[i];
            int newCol = col + colArr[i];
            if (isValid(newRow, newCol) && isMine(newRow, newCol, realBoard)) {
                count++;
            }
        }
        return count;
    }

    private static void initialise(char[][] realBoard, char[][] myBoard) {
        Random random = new Random();
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                myBoard[i][j] = realBoard[i][j] = '-';
            }
        }
    }

    private static void cheatMinesweeper(char[][] realBoard) {
        System.out.println("The mines positions are:");
        printBoard(realBoard);
    }

    private static void replaceMine(int row, int col, char[][] realBoard, int[][] mines) {
        Random random = new Random();
        for (int i = 0; i < MINES; i++) {
            if (realBoard[mines[i][0]][mines[i][1]] != '*') {
                realBoard[mines[i][0]][mines[i][1]] = '*';
                realBoard[row][col] = '-';
                mines[i][0] = row;
                mines[i][1] = col;
                return;
            }
        }
    }

    private static boolean playMinesweeperUtil(char[][] myBoard, char[][] realBoard, int[][] mines, int row, int col, int[] movesLeft) {
        if (!isValid(row, col) || myBoard[row][col] != '-') {
            return false;
        }

        if (isMine(row, col, realBoard)) {
            myBoard[row][col] = '*';
            for (int i = 0; i < MINES; i++) {
                myBoard[mines[i][0]][mines[i][1]] = '*';
            }
            printBoard(myBoard);
            System.out.println("You lost!");
            return true;
        }

        int count = countAdjacentMines(row, col, mines, realBoard);
        movesLeft[0]--;
        myBoard[row][col] = (char) (count + '0');

        if (count == 0) {
            int[] rowArr = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] colArr = {-1, 0, 1, -1, 1, -1, 0, 1};
            for (int i = 0; i < 8; i++) {
                int newRow = row + rowArr[i];
                int newCol = col + colArr[i];
                if (isValid(newRow, newCol) && !isMine(newRow, newCol, realBoard)) {
                    playMinesweeperUtil(myBoard, realBoard, mines, newRow, newCol, movesLeft);
                }
            }
        }

        return false;
    }

    private static void placeMines(int[][] mines, char[][] realBoard) {
        Random random = new Random();
        for (int i = 0; i < MINES; i++) {
            int randomRow = random.nextInt(SIDE);
            int randomCol = random.nextInt(SIDE);
            if (realBoard[randomRow][randomCol] != '*') {
                mines[i][0] = randomRow;
                mines[i][1] = randomCol;
                realBoard[randomRow][randomCol] = '*';
            } else {
                i--;
            }
        }
    }

    // Modify the playMinesweeper method to ensure the first move is safe
    private static void playMinesweeper() {
        startTime = System.nanoTime();
        boolean gameOver = false;
        char[][] realBoard = new char[MAXSIDE][MAXSIDE];
        char[][] myBoard = new char[MAXSIDE][MAXSIDE];
        int[][] mines = new int[MAXMINES][2];
        int movesLeft = SIDE * SIDE - MINES;
        int[] movesLeftArr = { movesLeft };
        int[] move = new int[2];
        initialise(realBoard, myBoard);

        // Ensure the first move is safe
        boolean isFirstMove = true;
        int firstMoveRow = -1;
        int firstMoveCol = -1;

        while (!gameOver) {
            System.out.println("Current Status of Board : ");
            printBoard(myBoard);
            makeMove(move);
            if (move[0] == -1 || move[1] == -1) {
                System.out.println("Exiting...");
                break;
            }

            if (isFirstMove) {
                firstMoveRow = move[0];
                firstMoveCol = move[1];
                isFirstMove = false;
            }

            if (movesLeft == SIDE * SIDE - MINES) {
                // Place mines after the first move
                placeMines(mines, realBoard);

                // If the first move is a mine, replace it with a safe cell
                if (isMine(firstMoveRow, firstMoveCol, realBoard)) {
                    replaceMine(firstMoveRow, firstMoveCol, realBoard, mines);
                }
            }

            gameOver = playMinesweeperUtil(myBoard, realBoard, mines, move[0], move[1], movesLeftArr);

            if (!gameOver && movesLeftArr[0] == 0) {
                System.out.println("You won!");
                gameOver = true;
            }
        }

        endTime = System.nanoTime(); // Stop the timer with nanoseconds resolution

        long elapsedTimeNano = endTime - startTime; // Calculate elapsed time in nanoseconds
        double seconds = (double) elapsedTimeNano / 1e9; // Convert nanoseconds to seconds

        System.out.println("Congratulations! You completed the game in " + seconds + " seconds.");
    }


    public static void main(String[] args) {
        int level;
        Scanner scanner = new Scanner(System.in);
        boolean playAgain = true;

        while(playAgain) {
            System.out.println("Enter the difficulty level");
            System.out.println("Press 0 for BEGINNER (9 * 9 Cells and 10 Mines)");
            System.out.println("Press 1 for INTERMEDIATE (16 * 16 Cells and 40 Mines)");
            System.out.println("Press 2 for ADVANCED (24 * 24 Cells and 99 Mines)");
            level = scanner.nextInt();

            if (level == BEGINNER) {
                SIDE = 9;
                MINES = 10;
            }

            if (level == INTERMEDIATE) {
                SIDE = 16;
                MINES = 40;
            }

            if (level == ADVANCED) {
                SIDE = 24;
                MINES = 99;
            }

            playMinesweeper();
            // Prompt the user to play again or exit
            System.out.print("Do you want to play again? (play/exit): ");
            String playAgainInput = scanner.next();
            if (!playAgainInput.equalsIgnoreCase("play")) {
                playAgain = false;
            }
        }
    }
}