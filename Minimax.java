/**
 * CS311 Artificial Intelligence Final Project - Spring 2020
 * 
 * File: AiPlayer.java 
 * Authors: Calder Birdsey and Brandon Choe 
 * Updated: 4/26/20
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
     */
    public void initAgent(Board state) {
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
            col = minimax(root, depth, true).lastMove;
        }

        // make move in current board state
        state.makeMove(col, false);
    }

    /**
     * method to execute the minimax algorithm 
     * @param node parent node board instance 
     * @param currDepth int depth of current node param in tree
     * @param max boolean value indicating maximizing or minimizing level
     * @return node of best next board instance given current board state
     */
    public Node minimax(Node node, int currDepth, boolean max) {
        // check base cases
        if (currDepth == 0 || node.isLeaf) {
            // System.out.println("\n\nLEAF: \t" + node.evalUtility() + "\t" + node.lastMove); 
            return node;
        }

        // create children 
        createChildren(node, max, currDepth - 1);

        if (max) {
            Node bestChild = null; 
            double maxUtil = NEGINFINITY; 
            for (Node child : node.children) {
                double utility = minimax(child, currDepth - 1, false).evalUtility(); 
                if(utility > maxUtil) {
                    bestChild = child; 
                    maxUtil = utility;
                }
            }
            return bestChild; 
        } else {
            Node bestChild = null; 
            double minUtil = POSINFINITY; 
            for (Node child : node.children) {
                double utility = minimax(child, currDepth - 1, true).evalUtility(); 
                if(utility < minUtil) {
                    minUtil = utility; 
                    bestChild = child; 
                }
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