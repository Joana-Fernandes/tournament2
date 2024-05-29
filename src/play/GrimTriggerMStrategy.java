package play;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.exception.InvalidStrategyException;

import java.util.Iterator;

public class GrimTriggerMStrategy extends Strategy {

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
                GameNode oppLastMoveAsP2 = null;
                System.out.println("Game size: "+ myStrategy.getMaximumNumberOfIterations());
                System.out.println("Game prob to go on: "+ myStrategy.probabilityForNextIteration());
                if(myStrategy.getFinalP1Node() != -1) {
                    System.out.println("Sou Player 1");
                    oppLastMoveAsP2 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
                    System.out.println("oppLastMoveAsP2: " + oppLastMoveAsP2); // MINHA JOGADA PASSADO
                    if(oppLastMoveAsP2 != null) {
                        try {
                            myLastMoveAsP1 = oppLastMoveAsP2.getAncestor();
                            System.out.println("myLastMoveAsP1: " + myLastMoveAsP1); // JOGADA PASSADA DO OPP
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P1: " + showLabel(myLastMoveAsP1.getLabel()) + "|" + showLabel(oppLastMoveAsP2.getLabel()));
                        System.out.println(" -> (Me) " + myLastMoveAsP1.getPayoffP1() + " : (Opp) "+ oppLastMoveAsP2.getPayoffP2());
                    }
                }
                GameNode oppLastMoveAsP1 = null;
                GameNode myLastMoveAsP2 = null;
                if(myStrategy.getFinalP2Node() != -1) {
                    System.out.println("Sou Player 2");
                    myLastMoveAsP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
                    System.out.println("myLastMoveAsP2: " + myLastMoveAsP2);
                    if(myLastMoveAsP2 != null) {
                        try {
                            oppLastMoveAsP1 = myLastMoveAsP2.getAncestor();
                            System.out.println("oppLastMoveAsP1: " + oppLastMoveAsP1);
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P2: " + showLabel(oppLastMoveAsP1.getLabel()) + "|" + showLabel(myLastMoveAsP2.getLabel()));
                        System.out.println(" -> (Opp) " + oppLastMoveAsP1.getPayoffP1() + " : (Me) "+ myLastMoveAsP2.getPayoffP2());
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
                double[] strategyP1 = setGrimmTriggerStratM(1,labelsP1,myStrategy, myLastMoveAsP1, oppLastMoveAsP2); // Aqui faz as strats 1
                double[] strategyP2 = setGrimmTriggerStratM(2,labelsP2,myStrategy, myLastMoveAsP2,oppLastMoveAsP1); // Aqui faz as strats 2
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

    public void showUtility(int P, int[][] M) {
        int nLin = M.length;
        int nCol = M[0].length;
        System.out.println("Utility Player " + P + ":");
        for (int i = 0; i<nLin; i++) {
            for (int j = 0; j<nCol; j++) System.out.print("| " + M[i][j] + " ");
            System.out.println("|");
        }
    }

    public double[] setMixedStrategy(int P, String[] labels, PlayStrategy myStrategy, double[][] u) {
        int n = labels.length;
        double[] strategy = new double[n];
        double p = 0;
        if (P==1) {
            //Mixed strat for P1
            if((u[0][0] - u[0][1] + u[1][1] - u[1][0]) != 0)
                p = (u[1][1] - u[1][0])/(u[0][0] - u[0][1] + u[1][1] - u[1][0]);
            else
                p = 0.5;
            strategy[0] = p;
            strategy[1] = 1 - strategy[0];
        }
        else {
            if((u[0][0] - u[0][1] + u[1][1] - u[1][0]) != 0)
                p = (u[1][1] - u[0][1])/(u[0][0] - u[0][1] + u[1][1] - u[1][0]);
            else
                p = 0.5;
            strategy[0] = p;
            strategy[1] = 1 - strategy[0];
        }

        for (int i = 0; i<n; i++) {
            System.out.println("Adding " + labels[i] + ":" + strategy[i]);
            myStrategy.put(labels[i], strategy[i]); // Aqui adiciona a estrategia mais concretamente
        }
        return strategy;
    }

    public double[] setStrategy(int P, String[] labels, PlayStrategy myStrategy) {
        int n = labels.length;
        double[] strategy = new double[n];
        for (int i = 0; i<n; i++)  strategy[i] = 0;
        if (P==1) { // if playing as player 1 then choose first action
            strategy[0] = 1;
            strategy[1] = 0;
        }
        else { 		// if playing as player 2 then choose first or second action randomly
            strategy[0] = 1;
            strategy[1] = 0;
        }
        for (int i = 0; i<n; i++) {
            System.out.println("Adding " + labels[i] + ":" + strategy[i]);
            myStrategy.put(labels[i], strategy[i]); // Aqui adiciona a estrategia mais concretamente
        }
        return strategy;
    }

    public double[] setGrimmTriggerStratM(int P, String[] labels, PlayStrategy myStrategy, GameNode myLastMove, GameNode oppLastMove) {
        int n = labels.length;
        double[] strategy = new double[n];

        if(myLastMove != null && (showLabel(myLastMove.getLabel()).equals("Defect") || showLabel(oppLastMove.getLabel()).equals("Defect")) || myStrategy.getMaximumNumberOfIterations() == 1 || myStrategy.probabilityForNextIteration() <= 1f/3f) {
            for(int i = 0; i<n; i++){
                String label = showLabel(labels[i]);
                if(label.equals("Defect")){
                    strategy[i] = 1;
                }
            }
        }else{
            for(int i = 0; i<n; i++){
                String label = showLabel(labels[i]);
                if(label.equals("Cooperate")){
                    strategy[i] = 1;
                }
            }
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

}
