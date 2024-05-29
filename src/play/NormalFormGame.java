package play;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class NormalFormGame {
	List<String> rowActions; 	// actions of player 1
	List<String> colActions; 	// actions of player 2
	int nRow;					// number of actions of player 1
	int nCol;					// number of actions of player 2
	boolean[] pRow;				// if pRow[i]==false than action i of player 1 is not considered 
	boolean[] pCol;				// if pCol[j]==false than action j of player 2 is not considered 
	double[][] u1;				// utility matrix of player 1 
	double[][] u2;				// utility matrix of player 2
	double minP1;
	double minP2;
	public NormalFormGame() {
	}
	
	public NormalFormGame(int[][] M1, int[][] M2, String[] labelsP1, String[] labelsP2) {
		/*
		 * Constructor of a NormalFormGame with data obtained from the API 
		 */
		nRow = labelsP1.length;
		rowActions = new ArrayList<String>();
		pRow = new boolean[nRow];
		for (int i = 0; i<nRow; i++) {
			rowActions.add(labelsP1[i].substring(labelsP1[i].lastIndexOf(':')+1));
			pRow[i] = true;
		}
		nCol = labelsP2.length;
		colActions = new ArrayList<String>();	
		pCol = new boolean[nCol];
		for (int j = 0; j<nCol; j++) {
			colActions.add(labelsP2[j].substring(labelsP2[j].lastIndexOf(':')+1));
			pCol[j] = true;
		}
		u1 = new double[nRow][nCol];
		u2 = new double[nRow][nCol];		
		for (int i = 0; i<nRow; i++) {
			for (int j = 0; j<nCol; j++) {
				u1[i][j] = M1[i][j];
				u2[i][j] = M2[i][j];
			}
		}
	}

	public void fixNegativesP1(){
		minP1 = 0;
		for (int i = 0; i < nRow; i++) {
			for (int j = 0; j < nCol; j++) {
				if(pRow[i] && pCol[j])
					minP1 = Math.min(minP1, u1[i][j]);
			}
		}
		System.out.println("Min1: " + minP1);
		if(minP1 < 0) {
			for (int i = 0; i < nRow; i++) {
				for (int j = 0; j < nCol; j++) {
					if(pRow[i] && pCol[j])
						u1[i][j] -= minP1; // Min é negativo, entao tou a "adicionar"
				}
			}
		}
	}
	public void fixNegativesP2(){
		minP2 = 0;
		for (int i = 0; i < nRow; i++) {
			for (int j = 0; j < nCol; j++) {
				if(pRow[i] && pCol[j])
					minP2 = Math.min(minP2, u2[i][j]);
			}
		}
		System.out.println("Min2: " + minP2);
		if(minP2 < 0) {
			for (int i = 0; i < nRow; i++) {
				for (int j = 0; j < nCol; j++) {
					if(pRow[i] && pCol[j])
						u2[i][j] -= minP2; // Min é negativo, entao tou a "adicionar"
				}
			}
		}
	}

	public void undoNegativesP1(){
		if(minP1 < 0) {
			for (int i = 0; i < nRow; i++) {
				for (int j = 0; j < nCol; j++) {
					if(pRow[i] && pCol[j])
						u1[i][j] += minP1; // Min é negativo, entao tou a "subtrair"
				}
			}
		}
		minP1 = 0;
	}

	public void undoNegativesP2(){
		if(minP2 < 0) {
			for (int i = 0; i < nRow; i++) {
				for (int j = 0; j < nCol; j++) {
					if(pRow[i] && pCol[j])
						u2[i][j] += minP2; // Min é negativo, entao tou a "subtrair"
				}
			}
		}
		minP2 = 0;
	}
	public int howManyActiveRows(){
		int sum = 0;
		for (int i = 0; i < nRow; i++) {
			if(pRow[i])
				sum += 1;
		}
		return sum;
	}
	public int howManyActiveCols(){
		int sum = 0;
		for (int i = 0; i < nCol; i++) {
			if(pCol[i])
				sum += 1;
		}
		return sum;
	}


	public double[][] getP1OtherOptionsUtilities(int r){
		//double[][] otherUtilities = new double[leng-1][];
		// Make an Array list of double[]
		List<double[]> otherUtilities = new ArrayList<>();
		int i = 0;
		for (double[] row : u1) {
			if (i != r && pRow[i]) {
				List<Double> rowUtilities = new ArrayList<>();
				for (int k = 0; k < nCol; k++) {
					if(pCol[k])
						rowUtilities.add(row[k]);
				}
				otherUtilities.add(rowUtilities.stream().mapToDouble(Double::doubleValue).toArray());
			}
			i++;
		}

		// Determine the size of the resulting double[][]
		int rows = otherUtilities.size();

		// Create the double[][] array
		double[][] result = new double[rows][];

		// Convert List<double[]> to double[][]
		for (int k = 0; k < rows; k++) {
			result[k] = otherUtilities.get(k);
		}
		return result;
	}

	public double[] getP1OptionUtilities(int r){
		List<Double> ans = new ArrayList<>();
		for (int i = 0; i < nCol; i++) {
			if(pCol[i])
				ans.add(u1[r][i]);
		}
		return ans.stream().mapToDouble(Double::doubleValue).toArray();
	}
	public double[] getP2OptionUtilities(int c){
		List<Double> ans = new ArrayList<>();
		for (int i = 0; i < nRow; i++) {
			if(pRow[i])
				ans.add(u2[i][c]);
		}
		return ans.stream().mapToDouble(Double::doubleValue).toArray();
	}

	public double[][] getP2OtherOptionsUtilities(int c){

		//double[][] otherUtilities = new double[u2.length][leng-1];
		List<List<Double>> otherUtilities = new ArrayList<>();

		boolean isFirst = true;
		for (int i = 0; i < u2.length; i++) { // Linhas
			int j = 0;
			int counterList = 0;
			if(!pRow[i])
				continue;
			for (int k = 0; k < u2[0].length; k++) { // Colunas
				double value = u2[i][k];
				if (j != c && pCol[j]) {
					if(isFirst) {
						List<Double> tmp = new ArrayList<>();
						tmp.add(value);
						otherUtilities.add(tmp);
					}else{
						otherUtilities.get(counterList).add(value);
						counterList++;
					}
				}
				j++;
			}
			isFirst = false; // pronto cheguei ao fim da minha primeira criação de colunas (listas), agora nas proximas é so ir adicionando
		}
		// Convert List<List<Double>> to double[][]
		double[][] result = new double[otherUtilities.size()][];
		for (int i = 0; i < otherUtilities.size(); i++) {
			List<Double> row = otherUtilities.get(i);
			result[i] = new double[row.size()];
			for (int j = 0; j < row.size(); j++) {
				result[i][j] = row.get(j);
			}
		}

		return result;
	}

	public double[][] getLeftMatrixP1(){
		int nr = howManyActiveRows();
		int nc = howManyActiveCols();
		double[][] result = new double[2][2];
		int ii = 0;
		int jj = 0;
		for (int i = 0; i < nRow; i++) {
			boolean putted = false;
			jj = 0;
			for (int j = 0; j < nCol; j++) {
				if(pRow[i] && pCol[j]) {
					result[ii][jj] = u1[i][j];
					jj++;
					putted = true;
				}
			}
			if(putted)
				ii++;
		}
		return result;
	}
	public int[] getActiveIdxsP1(){
		int nr = howManyActiveRows();
		int[] result = new int[nr];
		int ii = 0;
		for (int i = 0; i < nRow; i++) {
			if(pRow[i]){
				result[ii] = i;
				ii++;
			}
		}
		return result;
	}
	public int[] getActiveIdxsP2(){
		int nc = howManyActiveCols();
		int[] result = new int[nc];
		int ii = 0;
		for (int i = 0; i < nCol; i++) {
			if(pCol[i]){
				result[ii] = i;
				ii++;
			}
		}
		return result;
	}
	public double[][] getLeftMatrixP2(){
		int nr = howManyActiveRows();
		int nc = howManyActiveCols();
		double[][] result = new double[2][2];
		int ii = 0;
		int jj = 0;
		for (int i = 0; i < nRow; i++) {
			boolean putted = false;
			jj = 0;
			for (int j = 0; j < nCol; j++) {
				if(pRow[i] && pCol[j]) {
					result[ii][jj] = u2[i][j];
					jj++;
					putted = true;
				}
			}
			if(putted)
				ii++;
		}
		return result;
	}

	public void showGame() {
		/*
		 * Prints the game in matrix form. The names of the actions are shortened to the first letter 
		 */
		System.out.print("****");
		for (int j = 0; j<nCol; j++)  if (pCol[j]) 
			System.out.print("***********");
		System.out.println();
		System.out.print("  ");
		for (int j = 0; j<nCol; j++)  if (pCol[j]) {
				if (!colActions.isEmpty()) {
					System.out.print("      ");
					System.out.print(colActions.get(j).substring(0,1));
					System.out.print("    ");
				}
				else {
					System.out.print("\t");
					System.out.print("Col " +j);
				}
		}
		System.out.println();
		for (int i = 0; i<nRow; i++) if (pRow[i]) {
			if (rowActions.size()>0) System.out.print(rowActions.get(i).substring(0,1)+ ": ");
			else System.out.print("Row " +i+ ": ");
			for (int j = 0; j<nCol; j++)  if (pCol[j]) {
				String fs = String.format("| %3.0f,%3.0f", u1[i][j], u2[i][j]);
				System.out.print(fs+"  ");
			}
			System.out.println("|");
		}
		System.out.print("****");
		for (int j = 0; j<nCol; j++)  if (pCol[j]) 
			System.out.print("***********");
		System.out.println();
	}
	
}
