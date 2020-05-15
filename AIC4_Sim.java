import java.util.ArrayList;

/**
 * CS311 Artificial Intelligence Final Project - Spring 2020
 * 
 * File: AIC4_Sim.java 
 * Authors: Calder Birdsey and Brandon Choe 
 * Updated: 4/26/20
 * 
 * Description: 
 */
 
public class AIC4_Sim {
    
    // init game to begin with human turn
    public static boolean humanTurn = true; 
    public static int simCount = 1000; 
    public static int ties = 0; 
    public static int greedyWin = 0; 
    public static int smartWin = 0; 
    public static ArrayList<Integer> moveCounts = new ArrayList<Integer>();  

    /**
     * main function to run cli connect-4 game - reads from command line for 
     * human turns and creates instances of AI agent when AIs turn to move. 
     */
    public static void main(String[] args) {
        System.out.println("\nAI Connect-4 Statistical Simulator: Simulating " + simCount + " games between greedy AI and advanced minimax AI...");
        int gameCount = 0; 
        while(gameCount < simCount) {
            int turn = 0; 
            Board board = new Board(); 
            Minimax smartAgent = new Minimax(); 
            GreedyAI greedyAgent = new GreedyAI(); 
            board.setBoard();  
            board.lastHumanMove = 1;

            //simulate game
            while(board.playing) {
                if(turn == 0) {
                    // simualte "human" player 
                    int col = greedyAgent.initAgent(board); 
                    board.lastHumanMove = col; 
                    board.makeMove(col, true);
                    turn = 1; 
                } else {
                    smartAgent.initAgent(board); 
                    turn = 0; 
                }
            }

            // store move count 
            moveCounts.add(board.moveCount); 

            // store winner
            if(board.boardFull) {
                ties ++; 
            } else {
                if(board.winningPlayer.equals("O"))
                    smartWin ++; 
                else 
                    greedyWin++; 
            }
            System.out.print(".");
            gameCount ++; 
        }

        System.out.println("\nSimulation complete...");
        System.out.println("Performance statistics:");
        System.out.println("Advanced AI won " + smartWin + " out of " + simCount + " games.");
        System.out.println("Greedy AI won " + greedyWin + " out of " + simCount + " games.");
        System.out.println(ties + " tie games occured");
    }
}