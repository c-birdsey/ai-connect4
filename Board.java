/**
 * CS311 Artificial Intelligence 
 * Final Project - Spring 2020
 * 
 * File: Board.java
 * Authors: Calder Birdsey and Brandon Choe
 * Updated: 5/15/20
 * 
 * Description: Board class for AI Connect-4 to simulate command line gameplay
 */

public class Board implements Cloneable{
    
    boolean boardFull, playing;
    String winningPlayer; 
    String[][] boardArr =  new String[6][7]; 
    int moveCount, lastHumanMove; 
        
    /**
     * method to initialize new board instance with blank spaces
     */
    public void setBoard() {
        for (String[] row : boardArr) {
            for(int i = 0; i < row.length; i++) {
                row[i] = "_";  
            }
        }
        playing = true; 
        moveCount = 0; 
    }

    /**
     * method to execute move in board instance 
     * @param col int column to move into 
     * @param player boolean for player (true is human, false AI)
     */
	public void makeMove(int col, boolean player){
        for(int i = 5; i >= 0; i--) {
            if(col > 7) {
                col = 7; 
            }
            
            if(boardArr[i][col - 1].equals("_")) {
                if(player) {
                    boardArr[i][col - 1] = "X";
                    checkWin("X");
                } else {
                    boardArr[i][col - 1] = "O";
                    checkWin("O");
                } 
                break; 
            }
        }
        moveCount ++; 
        checkBoardFull(); 
    }

    /**
     * method to check for a win (four-in-a-row) in board instance 
     * @param player String piece to check ("X" or "O")
     * @return boolean if board contains win
     */
    public boolean checkWin(String player){
        boolean winner = false;

        // horizontal check 
        for (int j = 0; j<4 ; j++ ){
            for (int i = 0; i<6; i++){
                if (boardArr[i][j].equals(player) && boardArr[i][j+1].equals(player) && boardArr[i][j+2].equals(player) && boardArr[i][j+3].equals(player)){
                    winner = true;
                }           
            }
        }
        // vertical check
        for (int i = 0; i<3; i++ ){
            for (int j = 0; j<6; j++){
                if (boardArr[i][j].equals(player) && boardArr[i+1][j].equals(player) && boardArr[i+2][j].equals(player) && boardArr[i+3][j].equals(player)){
                    winner = true;
                }           
            }
        }

        // ascending diagonal check 
        for (int i=3; i<6; i++){
            for (int j=0; j<4; j++){
                if (boardArr[i][j].equals(player) && boardArr[i-1][j+1].equals(player) && boardArr[i-2][j+2].equals(player) && boardArr[i-3][j+3].equals(player))
                    winner = true;
            }
        }
        // descending diagonal check
        for (int i=5; i>2; i--){
            for (int j=3; j<7; j++){
                if (boardArr[i][j].equals(player) && boardArr[i-1][j-1].equals(player) && boardArr[i-2][j-2].equals(player) && boardArr[i-3][j-3].equals(player))
                    winner = true;
            }
        }

        if(winner) {
            playing = false; 
            winningPlayer = player; 
        }
        return winner; 
    }

    /**
     * method to check if potential move is valid
     * @param col int potential move column
     * @return boolean if move is valid or not
     */
    public boolean validMove(int col){
        if(col < 1 || col > 7) 
            return false; 
        else if(!boardArr[0][col - 1].equals("_")) 
            return false; 
        return true; 
    }

    /**
     * method to check if board is full (i.e. tie game)
     */
    public void checkBoardFull() {
        for(int i = 0;  i < 7; i++) {
            if(boardArr[0][i].equals("_"))
                return;
        }
        boardFull = true; 
        playing = false; 
    }

    /**
     * method to print board via command line
     */
    public void printBoard(){
        System.out.println(); 
        for (String[] row : boardArr) {
            for(int i = 0; i < row.length; i++) {
                System.out.print(row[i] + "\t"); 
            }
            System.out.print("\n\n"); 
        }
        System.out.println("__________________________________________________"); 
        System.out.println("1\t2\t3\t4\t5\t6\t7\t\n"); 
    }

    /**
     * method to create board copy - called my Minimax.java to create children 
     * instances 
     * @return Board copy of current board 
     */
    public Board copyBoard() {
        Board temp = new Board();
        temp.moveCount = moveCount; 
        temp.boardFull = boardFull; 
        temp.playing = playing; 
        temp.lastHumanMove = lastHumanMove; 
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 7; j++)
                temp.boardArr[i][j] = boardArr[i][j]; 
        }
        return temp; 
    }
}