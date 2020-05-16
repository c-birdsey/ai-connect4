import java.util.ArrayList;
import java.util.List;

/**
 * CS311 Artificial Intelligence 
 * Final Project - Spring 2020
 * 
 * File: Node.java
 * Authors: Calder Birdsey and Brandon Choe
 * Updated: 5/15/20
 * 
 * Description: Node class for AI Connect-4 minimax algorithm, evaluates board 
 * state utility via range of heuristics
 */

public class Node {
    // globals 
    public int MAX = 100000;
    public int MIN = -100000; 

    Board board; 
    int lastMove, depth; 
    boolean isLeaf, isMaxPlayer; 
    Node parent; 
    List <Node> children = new ArrayList<Node>(); 

    //default token settings for normal game where AI agent is "O"
    String agentType = "O"; 
    String opType = "X"; 

    int blockingMove = -1; 

    /**
     * method to evalute utility of the node based off the board arrangement 
     * @return double value utility score of the board 
     */
    public double evalUtility() {
        double utility = 0;      
        
        /************************* TESTING BOARD *****************************/

        // String[][] testBoard =  new String[6][7]; 
        // testBoard[0] = new String[] {"_", "_", "_", "_", "_", "_", "_"}; 
        // testBoard[1] = new String[] {"_", "_", "_", "_", "_", "_", "_"}; 
        // testBoard[2] = new String[] {"_", "_", "X", "O", "_", "_", "_"}; 
        // testBoard[3] = new String[] {"_", "_", "O", "O", "_", "_", "_"}; 
        // testBoard[4] = new String[] {"_", "O", "O", "X", "_", "_", "X"}; 
        // testBoard[5] = new String[] {"_", "X", "O", "O", "X", "X", "X"};    
        // board.boardArr = testBoard;

        /*********************************************************************/

        // if child board has a win, always take it 
        if(board.checkWin(agentType))
            return MAX; 

        if(board.checkWin(opType))
            return MIN; 

        // avoid board if it creates a win condition for opponent
        checkPreWinCondition(); 
        if(blockingMove >= 0) {
            return MIN;
        }

        // otherwise, score entire board layout for AI
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 7; j++) {
                if(board.boardArr[i][j].equals(agentType)) {
                    double vertScore = scoreVerticals(i, j, agentType); 
                    double horizScore = scoreHorizontals(i, j, agentType); 
                    double diagScore = scoreDiagonals(i, j, agentType); 
                    double neighborScore = scoreNeighborhoods(i, j, agentType); 
                    utility += (vertScore + horizScore + diagScore + neighborScore); 
                }
            }
        }

        return utility; 
    }

    /**
     * method to set leaf status of node based of winning conditions 
     */
    public void setLeaf() {
        if (board.checkWin(opType) || board.checkWin(agentType)) {
            isLeaf = true;  
        }
    }

    /**
     * method to check board for winning arangement for opponent called from Minimax.java 
     * before creating children - used to find blocking move
     */
    public void checkWinCondition() {
        // get location of last move 
        int col = board.lastHumanMove - 1; 
        int row = 0; 

        while(!board.boardArr[row][col].equals(opType) && row < 5) {
            row ++; 
        }

        // call score methods on X for last move location - this will set globals if block needed 
        scoreDiagonals(row, col, opType); 
        scoreHorizontals(row, col, opType); 
        scoreVerticals(row, col, opType); 
    }

    /**
     * method to check board for pre winning arangement for opponent (i.e. a move that 
     * gives opponent a winning move)
     */
    public void checkPreWinCondition() {
        // get location of last move 
        int col = lastMove - 1; 
        int row = 5; 

        while(!board.boardArr[row][col].equals("_") && row > 0) {
            row --; 
        }

        // call score methods on X for last move location - this will set globals if block needed 
        scoreDiagonals(row, col, opType); 
        scoreHorizontals(row, col, opType); 
        scoreVerticals(row, col, opType); 
    }

    /****************************** HEURISTICS *******************************/
    /*************************************************************************/

    /**
     * method to score surrounding 5x5 spaces of a given square  
     * @param row row location of square to examine
     * @param col col location of square to examine 
     * @param type piece type (X or O) to score for
     * @return double score for neighborhood of piece row, col
     */
    public double scoreNeighborhoods(int row, int col, String type) {
        double score = 10; 

        for (int i = -3; i < 4; i++) {
            // check col exists before referencing
            if (row + i > 5 || row + i < 0)
                continue; 

            for (int j = -3; j < 4; j ++) {
                // check row exists before referencing
                if(col + j > 6 || col + j < 0)
                    continue; 

                if(i == 0 && j == 0) {
                    continue; 
                } else {
                    if(Math.abs(i) <= 1 && Math.abs(j) <= 1) {
                        if(board.boardArr[row + i][col + j].equals(type)) {
                            score += 5; 
                        } else if(board.boardArr[row + i][col + j].equals("_")) { 
                            score += .5; 
                        } else {
                            score -= 5; 
                        }
                        continue; 
                    } else if (Math.abs(i) <= 2 && Math.abs(j) <= 2) {
                        if(board.boardArr[row + i][col + j].equals(type)) {
                            int exp = pieceConnections(i, j, row, col, type); 
                            score += Math.pow(3, exp); 
                        } else if(board.boardArr[row + i][col + j].equals("_")) {
                            score += .2; 
                        } else {
                            score -= 3;
                        }
                        continue; 
                    }
                }
            }
        }
        return score; 
    }
    
    /**
     * method to score all horizontals a given piece is a part of 
     * @param row row location of square to examine
     * @param col col location of square to examine 
     * @param type piece type (X or O) to score for
     * @return
     */
    public double scoreHorizontals(int row, int col, String type) {
        double score = 0; 
        int edgeLeft = col - 3; 
        if(edgeLeft < 0) 
            edgeLeft = 0; 
        int edgeRight = col + 3; 
        if(edgeRight > 6) {
            edgeRight = 6; 
        }

        for(int i = edgeLeft; i <= edgeRight - 3; i++) {
            int typeCount = 0; 
            int spaceCount = 0; 
            int[] blockLocation = new int[2]; 
            for(int j = 0; j < 4; j++) {
                if(board.boardArr[row][i + j].equals(type)) {
                    typeCount ++; 
                    score += 3 * typeCount; 
                } else if(board.boardArr[row][i + j].equals("_")) {
                    spaceCount ++; 
                    blockLocation[0] = row; 
                    blockLocation[1] = (i + j); 
                    score += .1; 
                } else
                    score -= 3; 
            }
            if(typeCount == 3 && spaceCount == 1 && type.equals(opType)) {
                handleWinCondition(blockLocation); 
            }
        }         
        
        return score; 
    }


    /**
     * method to score all verticals a given piece is a part of 
     * @param row row location of square to examine
     * @param col col location of square to examine 
     * @param type piece type (X or O) to score for
     * @return
     */
    public double scoreVerticals(int row, int col, String type) {
        double score = 0; 
        int edgeUpper = row - 3; 
        if(edgeUpper < 0) 
            edgeUpper = 0; 
        int edgeLower = row + 3; 
        if(edgeLower > 5) {
            edgeLower = 5; 
        }

        for(int i = edgeUpper; i <= edgeLower - 3; i++) {
            int typeCount = 0; 
            int spaceCount = 0; 
            int[] blockLocation = new int[2]; 
            for(int j = 0; j < 4; j++) {
                if(board.boardArr[i + j][col].equals(type)) {
                    typeCount ++; 
                    score += 3 * typeCount; 
                } else if(board.boardArr[i + j][col].equals("_")) {
                    spaceCount ++; 
                    blockLocation[0] = i + j; 
                    blockLocation[1] = col; 
                    score += .1; 
                } else
                    score -= 3; 
            }
            if(typeCount == 3 && spaceCount == 1 && type.equals(opType)) {
                handleWinCondition(blockLocation); 
            }
        }         
        
        return score; 
    }

    /**
     * method to score all diagonals a given piece is a part of 
     * @param row row location of square to examine
     * @param col col location of square to examine 
     * @param type piece type (X or O) to score for
     * @return
     */
    public double scoreDiagonals(int row, int col, String type) {
        double score = 0; 

        // get edges to look for diagonals 
        int edgeLower = (row + 3 > 5) ? 5 : row + 3; 
        int edgeUpper = (row - 3 < 0) ? 0 : row - 3; 
        int edgeRight = (col + 3 > 6) ? 6 : col + 3;     
        int edgeLeft =  (col - 3 < 0) ? 0 : col - 3;

        // get starting point for left to right diagonals 
        int upperLeftRow = row; 
        int upperLeftCol = col; 

        while(upperLeftRow > edgeUpper && upperLeftCol > edgeLeft) {
            upperLeftRow --; 
            upperLeftCol --; 
        }

        // evalute left to right diagonals 
        while(upperLeftRow <= (edgeLower - 3) && upperLeftCol <= (edgeRight - 3)) {
            int typeCount = 0; 
            int spaceCount = 0; 
            int[] blockLocation = new int[2]; 
            for(int k = 0; k < 4; k++) {
                if(board.boardArr[upperLeftRow + k][upperLeftCol + k].equals(type)) {
                    typeCount ++; 
                    score += (3 * typeCount); 
                } else if(board.boardArr[upperLeftRow + k][upperLeftCol + k].equals("_")) {
                    spaceCount ++; 
                    blockLocation[0] = upperLeftRow + k; 
                    blockLocation[1] = upperLeftCol + k; 
                    score += .1; 
                } else
                    score -= 3; 
            }
            upperLeftRow ++; 
            upperLeftCol ++; 
            if(typeCount == 3 && spaceCount == 1 && type.equals(opType)) {
                handleWinCondition(blockLocation); 
            }
        }

        // get starting point for left to right diagonals 
        int upperRightRow = row; 
        int upperRightCol = col; 

        while(upperRightRow > edgeUpper && upperRightCol < edgeRight) {
            upperRightRow --; 
            upperRightCol ++; 
        }
        
        // evalute right to left diagonals 
        while(upperRightRow <= (edgeLower - 3) && upperRightCol >= (edgeLeft + 3)) {
            int typeCount = 0; 
            int spaceCount = 0; 
            int[] blockLocation = new int[2]; 
            for(int k = 0; k < 4; k++) {
                if(board.boardArr[upperRightRow + k][upperRightCol - k].equals(type)) {
                    typeCount ++; 
                    score += (3 * typeCount); 
                } else if(board.boardArr[upperRightRow + k][upperRightCol - k].equals("_")) {
                    spaceCount ++; 
                    blockLocation[0] = upperLeftRow + k; 
                    blockLocation[1] = upperLeftCol + k; 
                    score += .1; 
                } else
                    score -= 3; 
            }
            upperRightRow ++; 
            upperRightCol --; 
            if(typeCount == 3 && spaceCount == 1 && type.equals(opType)) {
                handleWinCondition(blockLocation); 
            }
        }
        return score; 
    }

    /**
     * method to handle blocking situation - sets globals to indicate block necessary
     * @param blockLocation int arr of block board location [row, col]
     */
    public void handleWinCondition(int[] blockLocation) {
        blockingMove = blockLocation[1]; 
    }

    /**
     * method to identify triple piece connections - called by all scoring methods 
     * to get a multiplier when increasing score (i.e. triple and double connections
     * are scored higher than a solo unconnected piece)
     * @param i distance to check from row
     * @param j distance to check from col
     * @param row row location of square to examine
     * @param col col location of square to examine 
     * @param type piece type (X or O) to score for
     * @return integer multiplier for scoring 
     */
    public int pieceConnections(int i, int j, int row, int col, String type) {
        int mult = 1; 
        if(i == 0) {
            if(j < 0)
                mult = board.boardArr[row + i][col + j + 1].equals(type) ? 2 : 1; 
                // check i, j + 1  
            else
                // check i, j - 1
                mult = board.boardArr[row + i][col + j - 1].equals(type) ? 2 : 1; 
        } else if(j == 0) {
            if(i < 0)
                // check i + 1, j  
                mult = board.boardArr[row + i + 1][col + j].equals(type) ? 2 : 1; 
            else
                // check i - 1, j
                mult = board.boardArr[row + i - 1][col + j].equals(type) ? 2 : 1; 
        } else if(Math.abs(j) == Math.abs(i)) {
            if(j < 0) {
                if(i < 0) {
                    // check j +  1, i + 1
                    mult = board.boardArr[row + i + 1][col + j + 1].equals(type) ? 2 : 1; 
                } else {
                    // check j +  1, i - 1
                    mult = board.boardArr[row + i - 1][col + j + 1].equals(type) ? 2 : 1; 
                }
            } else {
                if(i < 0) {
                    // check j -  1, i + 1
                    mult = board.boardArr[row + i + 1][col + j - 1].equals(type) ? 2 : 1; 
                } else {
                    // check j -  1, i - 1
                    mult = board.boardArr[row + i - 1][col + j - 1].equals(type) ? 2 : 1; 
                }
            }
        }
        return mult; 
    }
}