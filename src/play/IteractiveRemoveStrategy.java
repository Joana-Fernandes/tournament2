package play;

import java.util.Iterator;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.exception.InvalidStrategyException;

import static lp.LinearProgramming.*;

public class IteractiveRemoveStrategy  extends Strategy {

    @Override
    public void execute() throws InterruptedException {
        while(!this.isTreeKnown()) {
            System.err.println("Waiting for game tree to become available.");
            Thread.sleep(1000);
        }
        while(true) {
            PlayStrategy myStrategy = this.getStrategyRequest();
            if(myStrategy == null) //Game was terminated by an outside event
                break;
            boolean playComplete = false;

            while(! playComplete ) {
                System.out.println("*******************************************************");
                GameNode myLastMoveAsP1 = null;
                GameNode oppLastMoveAsP1 = null;

                if(myStrategy.getFinalP1Node() != -1) {
                    System.out.println("Sou Player 1");
                    myLastMoveAsP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
                    System.out.println("myLastMoveAsP1: " + myLastMoveAsP1); // MINHA JOGADA PASSADO
                    if(myLastMoveAsP1 != null) {
                        try {
                            oppLastMoveAsP1 = myLastMoveAsP1.getAncestor();
                            System.out.println("oppLastMoveAsP1: " + oppLastMoveAsP1); // JOGADA PASSADA DO OPP
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P1: " + showLabel(oppLastMoveAsP1.getLabel()) + "|" + showLabel(myLastMoveAsP1.getLabel()));
                        System.out.println(" -> (Me) " + myLastMoveAsP1.getPayoffP1() + " : (Opp) "+ myLastMoveAsP1.getPayoffP2());
                    }
                }
                GameNode oppLastMoveAsP2 = null;
                GameNode myLastMoveAsP2 = null;
                if(myStrategy.getFinalP2Node() != -1) {
                    System.out.println("Sou Player 2");
                    oppLastMoveAsP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
                    System.out.println("oppLastMoveAsP2: " + oppLastMoveAsP2);
                    if(oppLastMoveAsP2 != null) {
                        try {
                            myLastMoveAsP2 = oppLastMoveAsP2.getAncestor();
                            System.out.println("myLastMoveAsP2: " + myLastMoveAsP2);
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P2: " + showLabel(oppLastMoveAsP2.getLabel()) + "|" + showLabel(oppLastMoveAsP2.getLabel()));
                        System.out.println(" -> (Opp) " + oppLastMoveAsP2.getPayoffP1() + " : (Me) "+ oppLastMoveAsP2.getPayoffP2());
                    }
                }
                // Normal Form Games only!
                System.err.println("PLEASE SHOW");
                GameNode rootNode = tree.getRootNode();
                int n1 = rootNode.numberOfChildren();
                int n2 = rootNode.getChildren().next().numberOfChildren();
                String[] labelsP1 = new String[n1];
                String[] labelsP2 = new String[n2];
                int[][] U1 = new int[n1][n2];
                int[][] U2 = new int[n1][n2];
                Iterator<GameNode> childrenNodes1 = rootNode.getChildren();
                GameNode childNode1;
                GameNode childNode2;
                int i = 0;
                int j = 0;
                while(childrenNodes1.hasNext()) {
                    childNode1 = childrenNodes1.next();
                    labelsP1[i] = childNode1.getLabel();
                    j = 0;
                    Iterator<GameNode> childrenNodes2 = childNode1.getChildren();
                    while(childrenNodes2.hasNext()) {
                        childNode2 = childrenNodes2.next();
                        if (i==0) labelsP2[j] = childNode2.getLabel();
                        U1[i][j] = childNode2.getPayoffP1();
                        U2[i][j] = childNode2.getPayoffP2();
                        j++;
                    }
                    i++;
                }
                showActions(1,labelsP1);
                showActions(2,labelsP2);
                NormalFormGame game = new NormalFormGame(U1,U2,labelsP1,labelsP2);
                game.showGame();
                iterativeRemove(game, labelsP1, labelsP2);
                System.err.println("Game after iterate remove");
                game.showGame();
                double[] strategyP1 = setMixedStrategy2x2(1,labelsP1,myStrategy, game); // Aqui faz as strats 1
                double[] strategyP2 = setMixedStrategy2x2(2,labelsP2,myStrategy, game); // Aqui faz as strats 2
                showStrategy(1,strategyP1,labelsP1); // Aqui mostra as strats 1
                showStrategy(2,strategyP2,labelsP2); // Aqui mostra as strats 2

                try{
                    this.provideStrategy(myStrategy);
                    playComplete = true;
                } catch (InvalidStrategyException e) {
                    System.err.println("Invalid strategy: " + e.getMessage());;
                    e.printStackTrace(System.err);
                }
            }
        }

    }

    public String showLabel(String label) {
        return label.substring(label.lastIndexOf(':')+1);
    }

    public void showActions(int P, String[] labels) {
        System.out.println("Actions Player " + P + ":");
        for (int i = 0; i<labels.length; i++) System.out.println("   " + showLabel(labels[i]));
    }

    public void showUtility(int P, double[][] M) {
        int nLin = M.length;
        int nCol = M[0].length;
        System.out.println("Utility Player " + P + ":");
        for (int i = 0; i<nLin; i++) {
            for (int j = 0; j<nCol; j++) System.out.print("| " + (int) M[i][j] + " ");
            System.out.println("|");
        }
    }
    public void iterativeRemove(NormalFormGame game, String[] labelsP1, String[] labelsP2) {
        boolean repeat = true;
        while(repeat) {
            repeat = false;
            for (int i = 0; i < game.nRow; i++) {
                if(!game.pRow[i])
                    continue;
                System.out.println("Verifying P1 row: " + showLabel(labelsP1[i]));
                game.fixNegativesP1();
                if (game.pRow[i] && isItDominatedP1(i, game)) {
                    game.pRow[i] = false;
                    repeat = true;
                    game.undoNegativesP1();
                    game.showGame();
                }
                game.undoNegativesP1();
            }

            for (int i = 0; i < game.nCol; i++) {
                if(!game.pCol[i])
                    continue;
                System.out.println("Verifying P2 col: " + showLabel(labelsP2[i]));
                game.fixNegativesP2();
                if (game.pCol[i] && isItDominatedP2(i, game)) {
                    game.pCol[i] = false;
                    repeat = true;
                    game.undoNegativesP2();
                    game.showGame();
                }
                game.undoNegativesP2();
            }
        }

    }

    public boolean isItDominatedP1(int p1OptionIdx, NormalFormGame game){
        double[] p1Option = game.getP1OptionUtilities(p1OptionIdx);
        double[][] p1OtherOptions = game.getP1OtherOptionsUtilities(p1OptionIdx);
        if(p1OtherOptions.length == 0) return false;

        setLP_Custom(p1OtherOptions.length, p1OtherOptions[0].length, p1Option, transpose(p1OtherOptions));
        showLP();
        solveLP();
        double ans = showSolution();
        if(ans >= 0 && ans < 1) {
            System.out.println("is Dominated");
            return true;
        }
        System.out.println("is not Dominated");

        return false;
    }
    public boolean isItDominatedP2(int p2OptionIdx, NormalFormGame game){
        double[] p2Option = game.getP2OptionUtilities(p2OptionIdx);
        double[][] p2OtherOptions = game.getP2OtherOptionsUtilities(p2OptionIdx);
        if(p2OtherOptions.length == 0) return false;

        setLP_Custom(p2OtherOptions.length, p2OtherOptions[0].length, p2Option, transpose(p2OtherOptions));
        showLP();
        solveLP();
        double ans = showSolution();
        if(ans >= 0 && ans < 1) {
            System.out.println("is Dominated");
            return true;
        }
        System.out.println("is not Dominated");
        return false;
    }
    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    public double[] setMixedStrategy2x2(int P, String[] labels, PlayStrategy myStrategy, NormalFormGame game) {
        double[][] u = P == 1 ? game.getLeftMatrixP2() : game.getLeftMatrixP1();
        showUtility(P == 1 ? 2 : 1, u);
        int n = labels.length;
        double[] strategy = new double[n];
        double p = 0;
        if (P==1) {
            //Mixed strat for P1
            if((u[0][0] - u[0][1] + u[1][1] - u[1][0]) != 0)
                p = (u[1][1] - u[1][0])/(u[0][0] - u[0][1] + u[1][1] - u[1][0]);
            else
                p = 0.5;
            int[] idxs = game.getActiveIdxsP1();
            if(idxs.length == 1)
                strategy[idxs[0]] = 1;
            else {
                strategy[idxs[0]] = Math.round(p * 100.0) / 100.0;;
                strategy[idxs[1]] = Math.round((1 - strategy[idxs[0]]) * 100.0) / 100.0;
            }
        }
        else {
            if((u[0][0] - u[0][1] + u[1][1] - u[1][0]) != 0)
                p = (u[1][1] - u[0][1])/(u[0][0] - u[0][1] + u[1][1] - u[1][0]);
            else
                p = 0.5;
            int[] idxs = game.getActiveIdxsP2();
            if(idxs.length == 1)
                strategy[idxs[0]] = 1;
            else {
                strategy[idxs[0]] = Math.round(p * 100.0) / 100.0;
                strategy[idxs[1]] = Math.round((1 - strategy[idxs[0]]) * 100.0) / 100.0;
            }
        }

        for (int i = 0; i<n; i++) {
            System.out.println("Adding " + labels[i] + ":" + strategy[i]);
            myStrategy.put(labels[i], strategy[i]); // Aqui adiciona a estrategia mais concretamente
        }
        return strategy;
    }

    // Choose random just to not be empty
    public double[] setStrategy(int P, String[] labels, PlayStrategy myStrategy) {
        int n = labels.length;
        double[] strategy = new double[n];
        for (int i = 0; i<n; i++)  strategy[i] = 0;
        if (P==1) { // if playing as player 1 then choose first action
            strategy[0] = 1;
        }
        else { 		// if playing as player 2 then choose first or second action randomly
            strategy[0] = 1;
        }
        for (int i = 0; i<n; i++) {
            System.out.println("Adding " + labels[i] + ":" + strategy[i]);
            myStrategy.put(labels[i], strategy[i]); // Aqui adiciona a estrategia mais concretamente
        }
        return strategy;
    }

    public void showStrategy(int P, double[] strategy, String[] labels) {
        System.out.println("Strategy Player " + P + ":");
        for (int i = 0; i<labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
    }

    //Maxmin and minmax attempts
    public void minimaxStrategy(double[][] utilities, NormalFormGame game){
        double[][] minimaxMatrix = makeMatrix(utilities, game);
        double[] b = new double[minimaxMatrix.length];
        for (int i = 0; i < b.length - 1; i++) {
            if(i != b.length - 1){
                b[i] = 0;
            } else {
                b[i] = 1;
            }
        }
        setLP_Minimax(minimaxMatrix.length, minimaxMatrix[0].length, b, transpose(minimaxMatrix));
        showLP();
        solveLP();
    }

    public double[][] makeMatrix(double[][] utilities, NormalFormGame game) {
        double[][] matrix = new double[game.nRow + 1][game.nCol + 1];
        for (int i = 0; i < game.nRow + 1; i++) {
            for (int j = 0; j < game.nCol + 1; j++) {
                if(i < game.nRow && j < game.nCol){
                    double tmp = utilities[i][j];
                    matrix[i][j] = tmp - tmp*2; // just making the symmetric value
                }
                if(i == game.nRow && j != game.nCol){
                    matrix[i][j] = 1;
                }
                if(j == game.nCol && i != game.nRow){
                    matrix[i][j] = -1;
                }
                if(i == game.nRow && j == game.nCol){
                    matrix[i][j] = 0;
                }
            }
        }
        return matrix;
    }



}
