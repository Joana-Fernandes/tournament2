package play;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import play.exception.InvalidStrategyException;

public class NewStrategy  extends Strategy {
	boolean trigger = false;

	@Override
	public void execute() throws InterruptedException {
		GameNode finalP1 = null;
		GameNode finalP2 = null;

		while (!this.isTreeKnown()) {
			System.err.println("Waiting for game tree to become available.");
			Thread.sleep(1000);
		}
		while (true) {
			PlayStrategy myStrategy = this.getStrategyRequest();
			if (myStrategy == null) //Game was terminated by an outside event
				break;
			boolean playComplete = false;

			while (!playComplete) {
				System.out.println("*******************************************************");
				if (myStrategy.getFinalP1Node() != -1) {
					finalP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
					GameNode fatherP1 = null;
					if (finalP1 != null) {
						try {
							fatherP1 = finalP1.getAncestor();
						} catch (GameNodeDoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.print("Last round as P1: " + showLabel(fatherP1.getLabel()) + "|" + showLabel(finalP1.getLabel()));
						System.out.println(" -> (Me) " + finalP1.getPayoffP1() + " : (Opp) " + finalP1.getPayoffP2());
					}
				}
				if (myStrategy.getFinalP2Node() != -1) {
					finalP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
					GameNode fatherP2 = null;
					if (finalP2 != null) {
						try {
							fatherP2 = finalP2.getAncestor();
						} catch (GameNodeDoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.print("Last round as P2: " + showLabel(fatherP2.getLabel()) + "|" + showLabel(finalP2.getLabel()));
						System.out.println(" -> (Opp) " + finalP2.getPayoffP1() + " : (Me) " + finalP2.getPayoffP2());
					}
				}
				// Normal Form Games only!
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
				while (childrenNodes1.hasNext()) {
					childNode1 = childrenNodes1.next();
					labelsP1[i] = childNode1.getLabel();
					j = 0;
					Iterator<GameNode> childrenNodes2 = childNode1.getChildren();
					while (childrenNodes2.hasNext()) {
						childNode2 = childrenNodes2.next();
						if (i == 0) labelsP2[j] = childNode2.getLabel();
						U1[i][j] = childNode2.getPayoffP1();
						U2[i][j] = childNode2.getPayoffP2();
						j++;
					}
					i++;
				}
				showActions(1, labelsP1);
				showActions(2, labelsP2);
				showUtility(1, U1);
				showUtility(2, U2);
				NormalFormGame game = new NormalFormGame(U1, U2, labelsP1, labelsP2);
				game.showGame();
				double[] strategyP1 = new double[0];
				try {
					strategyP1 = setStrategy(1, labelsP1, myStrategy, finalP1, finalP2);
				} catch (GameNodeDoesNotExistException e) {
					throw new RuntimeException(e);
				}
				double[] strategyP2 = new double[0];
				try {
					strategyP2 = setStrategy(2, labelsP2, myStrategy, finalP1, finalP2);
				} catch (GameNodeDoesNotExistException e) {
					throw new RuntimeException(e);
				}
				showStrategy(1, strategyP1, labelsP1);
				showStrategy(2, strategyP2, labelsP2);
				try {
					this.provideStrategy(myStrategy);
					playComplete = true;
				} catch (InvalidStrategyException e) {
					System.err.println("Invalid strategy: " + e.getMessage());
					;
					e.printStackTrace(System.err);
				}
			}
		}

	}


	public String showLabel(String label) {
		return label.substring(label.lastIndexOf(':') + 1);
	}

	public void showActions(int P, String[] labels) {
		System.out.println("Actions Player " + P + ":");
		for (int i = 0; i < labels.length; i++) System.out.println("   " + showLabel(labels[i]));
	}

	public void showUtility(int P, int[][] M) {
		int nLin = M.length;
		int nCol = M[0].length;
		System.out.println("Utility Player " + P + ":");
		for (int i = 0; i < nLin; i++) {
			for (int j = 0; j < nCol; j++) System.out.print("| " + M[i][j] + " ");
			System.out.println("|");
		}
	}

	public double[] setStrategy(int P, String[] labels, PlayStrategy myStrategy, GameNode finalP1, GameNode finalP2) throws GameNodeDoesNotExistException {
		int n = labels.length;
		double[] strategy = new double[n];
		List<GameNode> listP1 = getReversePath(finalP1);
		List<GameNode> listP2 = getReversePath(finalP2);
		String lastOpponentMove = getLastOpponentMove(listP1, listP2, myStrategy);

		for (int i = 0; i < n; i++) {
			strategy[i] = 0;
		}

		if (lastOpponentMove == null) {
			strategy[0] = 1;
		} else {
			for (int i = 0; i < labels.length; i++) {
				if(labels[i].equals(lastOpponentMove)){
					strategy[i] = 1.0;
					break;
				}
			}
		}
		for (int i = 0; i < n; i++) {
			myStrategy.put(labels[i], strategy[i]);
		}
		return strategy;
	}

	public void showStrategy(int P, double[] strategy, String[] labels) {
		System.out.println("Strategy Player " + P + ":");
		for (int i = 0; i<labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
	}


	private List<GameNode> getReversePath(GameNode current) {
		try {
			GameNode n = current.getAncestor();
			List<GameNode> l =  getReversePath(n);
			l.add(current);
			return l;
		} catch (GameNodeDoesNotExistException e) {
			List<GameNode> l = new ArrayList<GameNode>();
			l.add(current);
			return l;
		}
	}

	private String getLastOpponentMove(List<GameNode> listP1,
									   List<GameNode> listP2,
									   PlayStrategy myStrategy) throws GameNodeDoesNotExistException {

		String lastOpponentMove = null;

		for (GameNode n : listP1) {
			if (n.isNature() || n.isRoot()) continue;
			if (n.getAncestor().isPlayer2()) {
				lastOpponentMove = n.getLabel();
			}
		}

		for (GameNode n : listP2) {
			if (n.isNature() || n.isRoot()) continue;
			if (n.getAncestor().isPlayer1()) {
				lastOpponentMove = n.getLabel();
			}
		}

		return lastOpponentMove;
	}

	private void grimTrigger(double[] strategy, String lastOpponentMove, boolean trigger){

	}
}
