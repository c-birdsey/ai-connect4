import java.util.Scanner;

/**
 * CS311 Artificial Intelligence Final Project - Spring 2020
 * 
 * File: AIC4.java 
 * Authors: Calder Birdsey and Brandon Choe 
 * Updated: 4/26/20
 * 
 * Description: Main driver file for Connect-4 with AI opponent. Implements
 * gameplay via the command line and allows human user to compete against AI
 * agent.
 */
 
public class AIC4 {
    
    // init game to begin with human turn
    public static boolean humanTurn = true;

    /**
     * main function to run cli connect-4 game - reads from command line for 
     * human turns and creates instances of AI agent when AIs turn to move. 
     */
    public static void main(String[] args) {
        Board board = new Board(); 
        Minimax agent = new Minimax(); 
        board.setBoard(); 
        System.out.println("\nWelcome to command-line Connect-4 against an AI player. Press (q) at anytime to quit. Human (X) gets to make the first move:");
        board.printBoard(); 
        try (Scanner scan = new Scanner(System.in)) {
            while(board.playing) {
                int col; 
                String s; 
                if(humanTurn) {
                    System.out.println("Which column would you like to drop your piece (1-7)?");
                    s =  scan.next(); 
                    if(s.equals("q")) {
                        System.out.println("Exiting game..."); 
                        return; 
                    }
                    col = Integer.parseInt(s);
                    while(!board.validMove(col)) {
                        System.out.println("That is not a valid move. Please choose again: ");
                        col = scan.nextInt();
                    }
                    board.lastHumanMove = col; 
                    board.makeMove(col, true);
                } else {
                    System.out.println("AI making move...");
                    agent.initAgent(board); 
                }
                humanTurn = !humanTurn;
                board.printBoard();
            } 
        }
        if(board.boardFull) {
            System.out.println("The game is a tie!"); 
        } else {
            //print winner
            System.out.println(board.winningPlayer + " is the winner!"); 

            // print message about AI success 
            if(board.winningPlayer.equals("O")) 
                System.out.println("You were beat by our AI agent."); 
            else 
                System.out.println("Congratulations, you bested our AI agent!"); 
        }
        System.out.println("This game lasted for " + board.moveCount + " moves."); 
    }
}