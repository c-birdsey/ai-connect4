/**
 * CS311 Artificial Intelligence Final Project - Spring 2020
 * 
 * File: GreedyAI.java 
 * Authors: Calder Birdsey and Brandon Choe 
 * Updated: 5/15/20
 * 
 * Description: Greedy AI agent variation on the Minimax agent. Looks ahead to a
 * depth of 1 using similar heuristic functions to evaluate utility. 
 */

public class GreedyAI {

    public int depth = 1;
    public int NEGINFINITY = -100000; 
    public int POSINFINITY = 100000;
    Node root;

    /**
     * method to create AI agent for current move - checks base cases and then 
     * runs greedy choice algorithm to determine next best move. Greedy AI plays 
     * as the "X" piece (taking the place of human player and moving first)
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

        // set player tokens
        root.agentType = "X"; 
        root.opType = "O"; 

        // init col for best move 
        int col; 

        // init opponent win condition variables in root node
        root.checkWinCondition(); 
       
        // if first move, always go middle
        if(root.board.moveCount == 0) {
            col = 4; 
        // if second move, go to col 5 or 3 or middle again
        } else if(root.board.moveCount == 2 && !root.board.boardArr[5][3].equals("X")) {
            if(root.board.lastHumanMove == 3) {
                col = 5; 
            } else {
                col = 3; 
            }
        } else if(root.board.moveCount == 2 && root.board.boardArr[5][3].equals("X")) {
            col = 4; 
        } else if(root.blockingMove >= 0) { // check for opponent win conditions and block
            col = root.blockingMove + 1; 
        } else { // otherwise, call greedy choice
            col = greedyChoice(root, depth, true).lastMove;
        }

        // return move
        return col; 
    }

    /**
     * method to execute greedy selection for next move
     * @param node parent node board instance 
     * @param currDepth int depth of current node param in tree
     * @param max boolean value indicating maximizing or minimizing level
     * @return node of best next board instance given current board state
     */
    public Node greedyChoice(Node node, int currDepth, boolean max) {
        // create children 
        createChildren(node, max, currDepth - 1);

        Node bestChild = null; 
        double maxUtil = NEGINFINITY; 
        for (Node child : node.children) {
            // readjust default type settings 
            child.agentType = "X"; 
            child.opType = "O"; 
            double utility = 0; 
            if(child != null) {
                utility = child.evalUtility(); 
            }

            // incorporate element of "randomness" into moves 
            double rand = Math.random(); 
            if(utility >= (.65 * maxUtil) && rand < 0.5) {
                bestChild = child; 
                maxUtil = utility;
            } else if(utility > maxUtil) {
                bestChild = child; 
                maxUtil = utility;
            }
        }
        return bestChild; 
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
                temp.makeMove(i, isMax);

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