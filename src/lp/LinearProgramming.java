package lp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import play.NormalFormGame;
import scpsolver.constraints.*;
import scpsolver.lpsolver.*;
import scpsolver.problems.*;


public class LinearProgramming {
	static LinearProgram lp;
	static double[] x;
	public LinearProgramming() {
	}
	
	
	public static void setLP1() {
		double[] c = { 150.0, 175.0 }; // Definir funcao objetivo ( C )
        double[] b = {  77.0,  80.0,  9.0, 6.0 }; // definir vetor b
        double[][] A = { 	// Definir matriz A
                {  7.0, 11.0 },
                { 10.0,  8.0 },
                {  1.0,  0.0 },
                {  0.0,  1.0 },
        };
        double[] lb = {0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(false); // Problema de maximização
		for (int i = 0; i<b.length; i++)
			lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c"+i)); // Definir condição de menor ou igual
		lp.setLowerbound(lb);
	}

	public static void setLP2() {
		double[] c = { 3.0, 2.0, 7.0 }; // Definir funcao objetivo ( C )
		double[] b = {  10.0,  10.0,  0.0 }; // definir vetor b
		double[][] A = { 	// Definir matriz A
				{  -1.0, 1.0, 0.0 },
				{   2.0,-1.0, 1.0 },
		};
		double[] lb = {0.0, 0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true);
		lp.addConstraint(new LinearEqualsConstraint(A[0], b[0], "c"+0)); // Definir condição
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1)); // Definir condição

		lp.setLowerbound(lb);
	}

	public static void setLP3() {
		double[] c = { 1.0, 1.0}; // Definir funcao objetivo ( C )
		double[] b = {  8.0,  0.0}; // definir vetor b
		double[][] A = { 	// Definir matriz A
				{  0.0, 2.0 },
				{ 7.0,  2.0},
		};
		double[] lb = {0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true); // Problema de maximização
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[0], b[0], "c"+0)); // Definir condição
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1)); // Definir condição

		lp.setLowerbound(lb);
	}
	public static void setLP4() {
		double[] c = { 1.0, 1.0}; // Definir funcao objetivo ( C )
		double[] b = {  2.0,  2.0}; // definir vetor b
		double[][] A = { 	// Definir matriz A
				{  8.0, 0.0 },
				{ 0.0,  7.0},
		};
		double[] lb = {0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true); // Problema de maximização
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[0], b[0], "c"+0)); // Definir condição
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1)); // Definir condição

		lp.setLowerbound(lb);
	}
	public static void setLP5() {
		double[] c = { 1.0}; // Definir funcao objetivo ( C )
		double[] b = {  2.0,  2.0}; // definir vetor b
		double[][] A = { 	// Definir matriz A
				{  8.0, 0.0 },
				{ 0.0,  7.0},
		};
		double[] lb = {0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true); // Problema de maximização
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[0], b[0], "c"+0)); // Definir condição
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1)); // Definir condição

		lp.setLowerbound(lb);
	}
	// Mixed strat example
	public static void setLP6() {
		double[] c = {0, 0, 1.0}; // Definir funcao objetivo ( C )
		double[] b = {  2.0,  2.0}; // definir vetor b
		double[][] A = { 	// Definir matriz A
				{  8.0, 0.0 },
				{ 0.0,  7.0},
		};
		double[] lb = {0.0, 0.0}; // x e y >= 0
		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true); // Problema de maximização
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[0], b[0], "c"+0)); // Definir condição
		lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[1], b[1], "c"+1)); // Definir condição

		lp.setLowerbound(lb);
	}

	public static void setLP_Custom(int vars, int restr, double[] b, double[][] A) {
		System.out.println("vars: " + vars);
		System.out.println("restr: " + restr);
		System.out.println("b: " + Arrays.toString(b));
		System.out.println("A: " + Arrays.toString(A));
		System.out.println("SETTING CUSTOM LP");
		double[] c = new double[vars];
        Arrays.fill(c, 1.0);

		double[] lb = new double[vars];

		lp = new LinearProgram(c); // default meter isto
		lp.setMinProblem(true);


		for (int j = 0; j<restr; j++)
			lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[j], b[j], "c"+j)); // Definir condição

		lp.setLowerbound(lb);
	}

	public static void setLP_Minimax(int vars, int restr, double[] b, double[][] A){
		System.out.println("vars: " + vars);
		System.out.println("restr: " + restr);
		System.out.println("b: " + Arrays.toString(b));
		System.out.println("A: " + Arrays.toString(A));
		System.out.println("SETTING CUSTOM LP");
		double[] c = new double[vars];
		Arrays.fill(c, 0.0);
		c[vars - 1] = 1.0;


		double[] lb = new double[vars];

		lp = new LinearProgram(c);
		lp.setMinProblem(true);

		for (int j = 0; j<restr; j++){
			if(j == restr -1){
				lp.addConstraint(new LinearEqualsConstraint(A[j], b[j], "c"+j));
			} else{
				lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[j], b[j], "c"+j));
			}
		}


		System.out.println(Arrays.toString(lp.getC()));
		lp.setLowerbound(lb);
	}

	public static boolean solveLP() {
		LinearProgramSolver solver  = SolverFactory.newDefault();
		x = solver.solve(lp);
		if (x==null) return false;
		return true;
	}
	
	public static double showSolution() {
		if (x==null) {
			System.out.println("*********** NO SOLUTION FOUND ***********");
			return -1;
		}
		else {
			System.out.println("*********** SOLUTION ***********");
			for (int i = 0; i<x.length; i++) System.out.println("x["+i+"] = "+x[i]);
			double ans = lp.evaluate(x);
			System.out.println("f(x) = "+ ans);
			return ans;
		}
	}
	public static double getSolution() {
		if (x==null) {
			System.out.println("*********** NO SOLUTION FOUND ***********");
			return -1;
		}
		else {
			double ans = lp.evaluate(x);
			return ans;
		}
	}


	
	public static void showLP() {
		System.out.println("*********** LINEAR PROGRAMMING PROBLEM ***********");
		String fs;
		if (lp.isMinProblem()) System.out.print("  minimize: "); 
		else System.out.print("  maximize: "); 
		double[] cf = lp.getC();
		for (int i = 0; i<cf.length; i++) if (cf[i] != 0) {
			fs = String.format(Locale.US,"%+7.1f", cf[i]);
			System.out.print(fs + "*x["+i+"]"); 
		}
		System.out.println("");
		System.out.print("subject to: "); 
		ArrayList<Constraint> lcstr = lp.getConstraints();
		double aij;
		double[] ci = null;
		String str = null;
		for (int i = 0; i<lcstr.size(); i++) {
			if (lcstr.get(i) instanceof LinearSmallerThanEqualsConstraint) {
				str = " <= ";			
				ci = ((LinearSmallerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearBiggerThanEqualsConstraint) {
				str = " >= ";
				ci = ((LinearBiggerThanEqualsConstraint) lcstr.get(i)).getC();
			}
			if (lcstr.get(i) instanceof LinearEqualsConstraint) {
				str = " == ";
				ci = ((LinearEqualsConstraint) lcstr.get(i)).getC();
			}
			str = str + String.format(Locale.US,"%6.1f", lcstr.get(i).getRHS());
			if (i != 0) System.out.print("            ");
			for(int j=0;j<lp.getDimension();j++) {
				aij = ci[j];
				if (aij != 0) {
					fs = String.format(Locale.US,"%+7.1f", aij);
					System.out.print(fs + "*x["+j+"]"); 
				}
				else System.out.print("            "); 
			}
			System.out.println(str);	
		}
	}
	
	
	
	public static void main(String[] args) {

		int vars = 4;
		int restrs = 5;
		double[] b = {0.0, 0.0, 0.0, 0.0, 1.0};
		double[][] A = {
				{4.0, 67.0, 90.0, -1.0},
				{64.0, 15.0, 86.0, -1.0},
				{-40.0, -4.0,94.0,-1.0},
				{96.0,25.0,-13.0,-1.0},
				{1.0, 1.0, 1.0, 0.0}
		};
		setLP_Minimax(vars, restrs, b, A);
		showLP();  // Default, nao mexer em nada
		solveLP(); // Default, nao mexer em nada
		showSolution(); // Default, nao mexer em nada
		
	}

}
