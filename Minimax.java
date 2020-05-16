/**
 * CS311 Artificial Intelligence Final Project - Spring 2020
 * 
 * File: Minimax.java 
 * Authors: Calder Birdsey and Brandon Choe 
 * Updated: 5/15/20
 * 
 * Description: Code for AI agent to make move in Connect-4. Uses minmax tree
 * with alpha-beta pruning. Called from driver file AIC4.java
 */

public class Minimax {

    public int depth = 5;
    public int NEGINFINITY = -100000; 
    public int POSINFINITY = 100000;
    Node root;

    /**
     * method to create AI agent for current move - checks base cases and then 
     * runs minimax algorithm to determine next best move
     * @param state current board state of connect-4 game 
     * @return int col, the column of the most recent move
     */
    public int initAgent(Board state) {
        // initialize root node as current board state
        root = new Node();
        root.board = state;
        root.isMaxPlayer = true;
        root.parent = null;
        root.isLeaf = false;

        // init col for best move 
        int col; 

        // init opponent win condition variables in root node
        root.checkWinCondition(); 
       
        // if AI first move, always go middle
        if(root.board.moveCount == 1) {
            col = 4; 
        // if second move, go to col 5 or 3 or middle again
        } else if(root.board.moveCount == 3 && !root.board.boardArr[5][3].equals("O")) {
            if(root.board.lastHumanMove == 3) {
                col = 5; 
            } else {
                col = 3; 
            }
        } else if(root.board.moveCount == 3 && root.board.boardArr[5][3].equals("O")) {
            col = 4; 
        } else if(root.blockingMove >= 0) { // check for opponent win conditions and block
            col = root.blockingMove + 1; 
        } else { // otherwise, call minimax
            col = minimax(root, depth, NEGINFINITY, POSINFINITY, true).lastMove; 
        }

        // make move in current board state
        state.makeMove(col, false);
        return col; 
    }

    /**
     * method to execute the minimax algorithm 
     * @param node parent node board instance 
     * @param currDepth int depth of current node param in tree
     * @param alpha int alpha value
     * @param beta int beta value
     * @param max boolean value indicating maximizing or minimizing level
     * @return node of best next board instance given current board state
     */
    public Node minimax(Node node, int currDepth, double alpha, double beta, boolean max) {
        // check base cases
        if (currDepth == 0 || node.isLeaf) {
            return node;
        }

        // create children 
        createChildren(node, max, currDepth - 1);
        if(node.children.size() == 0) {
            return node; 
        }

        if(max) {
            Node bestChild = null; 
            double maxUtil = NEGINFINITY; 
            for (Node child : node.children) {
                double utility = 0; 
                Node temp = minimax(child, currDepth - 1, alpha, beta, false);
                if(temp != null) {
                    utility = temp.evalUtility(); 
                }
                if(utility > maxUtil) {
                    bestChild = child; 
                    maxUtil = utility;
                }

                // set new alpha val
                if(maxUtil > alpha) 
                    alpha = maxUtil; 
                
                // prune if condition met
                if(alpha >= beta) 
                    break; 
            }
            return bestChild; 
        } else {
            Node bestChild = null; 
            double minUtil = POSINFINITY; 
            for (Node child : node.children) {
                double utility = 0; 
                Node temp = minimax(child, currDepth - 1, alpha, beta, true);
                if(temp != null) {
                    utility = temp.evalUtility(); 
                }
                if(utility < minUtil) {
                    bestChild = child; 
                    minUtil = utility;
                }

                // set new beta val
                if(minUtil < beta) 
                    beta = minUtil; 
                
                // prune if condition met
                if(alpha >= beta) 
                    break; 
            }
            return bestChild; 
        }
    }

    /**
     * method to create all children boards based off viable moves, given a parent 
     * root board 
     * @param parent board instance to act as root
     * @param isMax boolean value if max or min level of minimax tree
     * @param currDepth tree depth of the parent node
     */
    public void createChildren(Node parent, boolean isMax, int currDepth) {
        for (int i = 1; i < 8; i++) {
            if (parent.board.validMove(i)) {
                // create new child board
                Board temp = parent.board.copyBoard(); 
                temp.makeMove(i, !isMax);

                // create child node 
                Node child = new Node(); 
                child.board = temp; 
                child.parent = parent; 
                child.lastMove = i; 

                // set leaf status based on depth or winning board 
                if(currDepth == 0) {
                    child.isLeaf = true; 
                } else {
                    child.setLeaf();
                }

                // add child node to parent
                parent.children.add(child);
            }
        }
    }
}