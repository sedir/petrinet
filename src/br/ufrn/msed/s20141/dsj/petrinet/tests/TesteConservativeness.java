package br.ufrn.msed.s20141.dsj.petrinet.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.vecmath.SingularMatrixException;

import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.alg.dense.mult.MatrixDimensionException;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.LinearSolver;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.junit.Before;
import org.junit.Test;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;
import br.ufrn.msed.s20141.dsj.petrinet.util.MarkupProcessor;

public class TesteConservativeness {
	private static final String nl = "\n";
	private Petrinet petrinet1;
	private Petrinet petrinet2;

	@Test
	public void test_invariants() throws NumberFormatException, IOException {
		Petrinet pn = new MarkupProcessor(this.getNetFig21()).getPetrinet();
		System.out.println(this.getNetFig21());
		System.out.println("Matriz incidência");
		pn.printIncidenceMatrix();
		System.out.println("Estado inicial");
		pn.printStateVector();
		
		System.out.println(pn);
	}
	public void test_solution_system_linear() {
		//		double [][] matrix = {{1.000000, 0.000000, 0.000000, 0.000000, 2.000000}, 
		//				 {0.000000, 0.000000, 3.000000, 0.000000, 0.000000}, 
		//				 {0.000000, 0.000000, 0.000000, 0.000000, 0.000000}, 
		//				 {0.000000, 4.000000, 0.000000, 0.000000, 0.000000}
		//				};
		double [][] matrix = {{2.0, 3.0},
				{3.0, 1.0},
				{4.0, 0.0}
		};

		DenseMatrix64F A = new DenseMatrix64F(matrix);
		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);
		if( !svd.decompose(A) ) {
			System.out.println("SVD failed");
			throw new RuntimeException("SVD failed");
		}

		DenseMatrix64F U = svd.getU(null,false);
		DenseMatrix64F W = svd.getW(null);
		DenseMatrix64F V = svd.getV(null,false); 

		DenseMatrix64F result = new DenseMatrix64F(A.numRows, A.numCols); 

		SingularOps.nullSpace(svd, result, 0.00001);
		System.out.println("*****************Original Matrix****************");
		System.out.println(A);
		System.out.println("**************SingularOps.nullSpace *****************");
		System.out.println("Matriz result");
		System.out.println(result);



	}

	public DenseMatrix64F matrix_pseudoinverse(double [][] matrix) {
		DenseMatrix64F A = new DenseMatrix64F(matrix);
		DenseMatrix64F Apseudo = new DenseMatrix64F(A.numCols, A.numRows);

		try {
			CommonOps.pinv(A, Apseudo);
		}catch (MatrixDimensionException e) {
			throw new MatrixDimensionException();
		}
		return Apseudo;   		
	}

	public void test_pseudo_inversa() {
		//		double [][] matrix = {{1.000000, 0.000000, 0.000000, 0.000000, 2.000000}, 
		//				{0.000000, 0.000000, 3.000000, 0.000000, 0.000000}, 
		//				{0.000000, 0.000000, 0.000000, 0.000000, 0.000000}, 
		//				{0.000000, 4.000000, 0.000000, 0.000000, 0.000000}
		//		};
		double [][] matrix = {{2.0, 3.0},
				{3.0, 1.0},
				{4.0, 0.0}
		};		

		DenseMatrix64F A = new DenseMatrix64F(matrix);
		DenseMatrix64F Apseudo = new DenseMatrix64F(A.numCols, A.numRows);

		try {
			CommonOps.pinv(A, Apseudo);
			System.out.println(Apseudo);
		}catch (MatrixDimensionException e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}

		System.out.println("*****************Original Matrix****************");
		System.out.println(A);
		System.out.println("***********This is the Pseudo-Inverse**********");
		System.out.println("Product of [V] * [W+] * [U-Transpose]");
		System.out.println(Apseudo);     


		DenseMatrix64F x = new DenseMatrix64F(A.numCols,1);
		DenseMatrix64F b = new DenseMatrix64F(A.numRows,1);		


		//		CommonOps.mult(Apseudo,b,x);
		//		System.out.println("Matrix b:");
		//		System.out.println(b);
		//		System.out.println("Matrix x:");
		//		System.out.println(x);

		//		/ compute b = (X^T*X)^-1 * X^T*Y 
		//		LinearSolver<DenseMatrix64F> solver=new SolvePseudoInverseSvd(A.numRows, A.numCols);
		LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.pseudoInverse(true);
		if( solver.modifiesA())
			A = A.copy();

		if( !solver.setA(A) ) {
			throw new IllegalArgumentException("Singular matrix");
		}		

		try {
			solver.solve(b,x);
			System.out.println("Matrix b:");
			System.out.println(b);
			System.out.println("Matrix x:");
			System.out.println(x);

		}catch (MatrixDimensionException e) {
			throw new IllegalArgumentException("MatrixDimensionException");
		}	

	}
	public static double[] mult(double[][] A, double[] v)
	{
		int m = A.length;
		int n = A[0].length;

		double[] resultado = new double[m];

		for(int i = 0; i< m; i++)
		{
			double aux = 0;
			for(int j = 0; j< n; j++)
			{   
				aux = aux + A[i][j]*v[j];
			}
			if(aux > 0)
			{
				resultado[i] = aux;
			}
			else
			{
				resultado[i] = 0;
			}
		}
		return resultado;
	}

	public void testnet_firing_couter_vector() throws NumberFormatException, IOException {
		Petrinet pn = new MarkupProcessor(this.getNetFig416()).getPetrinet();
		double[] resultado = pn.getFiringCounterVector(new double [] {1.0, 0.0, 0.0, 0.0}, new double [] {0.0, 0.0, 0.0, 1.0});
		//		assertArrayEquals(new double [] {1.0, 1.0, 0.0}, resultado,0);
		//		resultado = pn.getFiringCounterVector(new double [] {1.0, 0.0, 0.0, 0.0}, new double [] {0.0, 1.0, 0.0, 0.0});

		for(int i= 0; i<resultado.length;i++)
		{
			System.out.println(resultado[i]);
		}
	}

	public void testnet_conservability() throws NumberFormatException, IOException {

		//Petrinet pn = new MarkupProcessor(this.getNetNoConservative()).getPetrinet();
		//Petrinet pn = new MarkupProcessor(this.getNetFig416()).getPetrinet();
		Petrinet pn = new MarkupProcessor(this.getNetStrictlyConservative()).getPetrinet();

		DenseMatrix64F A = new DenseMatrix64F(pn.incidenceMatrix());

		//aplicação da SVD serve para aplicar a pseudo-inversa.
		//decomposição em valores singulares
		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);

		if( !svd.decompose(A) )
			throw new RuntimeException("SVD failed");

		DenseMatrix64F U = svd.getU(null,false);
		DenseMatrix64F W = svd.getW(null);
		DenseMatrix64F V = svd.getV(null,false);

		DenseMatrix64F result = new DenseMatrix64F(A.numRows,A.numCols); 

		SingularOps.nullSpace(svd, result, 0.00001);

		System.out.println("***************** Incidence matrix ****************");
		System.out.println(A);
		System.out.println("***************** Result ****************");
		System.out.println(result);

		DenseMatrix64F r = new DenseMatrix64F(A.numCols,1); 
		//		SingularOps.nullVector(svd, true, r);
		//		System.out.println(r);


	}
	private void arrayEquals(boolean [] expecteds, boolean [] actuals) {
		for (int i = 0; i < actuals.length; i++) {
			if (expecteds[i] != actuals[i])
				assertEquals(true, false);
		}
		assertEquals(true, true);
	}	
	private String getNetFig416() {
		//Pág. 272

		StringBuilder sb = new StringBuilder("Fig. 4.16 - Pag. 272");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3").append(nl);
		sb.append("p p4").append(nl);
		sb.append(nl);
		sb.append("t t1").append(nl);
		sb.append("t t2").append(nl);
		sb.append("t t3").append(nl);
		sb.append(nl);
		sb.append("a p1 t1").append(nl);
		sb.append("a t1 p2").append(nl);
		sb.append("a p2 t2").append(nl);
		sb.append("a t2 p3").append(nl);
		sb.append("a p3 t1").append(nl);
		sb.append("a t2 p4").append(nl);
		sb.append("a p4 t3").append(nl);
		sb.append("a t3 p1").append(nl);
		return sb.toString();
	}
	private String getNetConservative() {
		//Fig. 9. - Petri net conservative with rcspect to w = [1, 1, 2, 1, 1]
		//			Árvore de acessibilidade
		//			[1, 0, 1, 0]
		//				 |t3
		//		    [1, 0, 0, 1]
		//		    	 |t2
		//		    [1, w, 1, 0]	
		//		     |t1      |t3
		//	  [1, w, 0, 0]   [1, w, 0, 1]
		//			               |t2
		//			         [1, w, 1, 0]


		StringBuilder sb = new StringBuilder("Fig9");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2 1").append(nl);
		sb.append("p p3").append(nl);
		sb.append("p p4").append(nl);
		sb.append("p p5").append(nl);
		sb.append(nl);
		sb.append("t t1").append(nl);
		sb.append("t t2").append(nl);
		sb.append(nl);
		sb.append("a p1 t1").append(nl);
		sb.append("a p2 t1").append(nl);
		sb.append("a t1 p3").append(nl);
		sb.append("a p3 t2").append(nl);
		sb.append("a t2 p4").append(nl);
		sb.append("a t2 p5").append(nl);
		return sb.toString();
	}
	private String getNetStrictlyConservative() {
		//Fig. 10. -Petri net that is strictly conservative w = [1, 1, 1]


		StringBuilder sb = new StringBuilder("Fig10");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3").append(nl);
		sb.append(nl);
		sb.append("t t1").append(nl);
		sb.append("t t2").append(nl);
		sb.append("t t3").append(nl);
		sb.append(nl);
		sb.append("a p1 t1").append(nl);
		sb.append("a t1 p2").append(nl);
		sb.append("a p2 t2").append(nl);
		sb.append("a t2 p3").append(nl);
		sb.append("a p3 t3").append(nl);
		sb.append("a t3 p1").append(nl);
		return sb.toString();
	}
	private String getNetNoConservative() {
		//Fig. 8. Petri net that is unbounded


		StringBuilder sb = new StringBuilder("Fig8");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3").append(nl);
		sb.append("p p4").append(nl);
		sb.append(nl);
		sb.append("t t1").append(nl);
		sb.append("t t2").append(nl);
		sb.append("t t3").append(nl);
		sb.append(nl);
		sb.append("a p1 t1").append(nl);
		sb.append("a t1 p2").append(nl);
		sb.append("a p2 t2").append(nl);
		sb.append("a t2 p3").append(nl);
		sb.append("a p3 t3").append(nl);
		sb.append("a t3 p1").append(nl);
		sb.append("a t1 p4").append(nl);
		sb.append("a p4 t2").append(nl);
		sb.append("a t2 p4").append(nl);
		return sb.toString();
	}
	private String getNetInvariante() {
		//Figura 2.13: Invariantes
		//Livro REDES DE PETRI de JANETTE CARDOSO
		//
		StringBuilder sb = new StringBuilder("Fig8");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3 3").append(nl);
		sb.append("p p4").append(nl);
		sb.append("p p5 1").append(nl);
		sb.append(nl);
		sb.append("t a").append(nl);
		sb.append("t b").append(nl);
		sb.append("t c").append(nl);
		sb.append("t d").append(nl);
		sb.append(nl);
		sb.append("a p1 a").append(nl);
		sb.append("a a p2").append(nl);
		sb.append("a p2 b").append(nl);
		sb.append("a b p1").append(nl);
		sb.append("a b p3").append(nl);
		sb.append("a p3 a").append(nl);
		sb.append("a p3 c").append(nl);
		sb.append("a c p4").append(nl);
		sb.append("a d p3").append(nl);
		sb.append("a d p5").append(nl);
		sb.append("a p5 c").append(nl);
		return sb.toString();
	}
	private String getNetFig21() {
		//Figura 2.1: 
		//Livro REDES DE PETRI de JANETTE CARDOSO
		//
		StringBuilder sb = new StringBuilder("Fig8");
		sb.append(nl);
		sb.append("p p1").append(nl);
		sb.append("p p2 3").append(nl);
		sb.append("p p3").append(nl);
		sb.append(nl);
		sb.append("t a").append(nl);
		sb.append("t b").append(nl);
		sb.append("t c").append(nl);
		sb.append("t d").append(nl);
		sb.append(nl);
		sb.append("a a p1").append(nl);
		sb.append("a p1 b").append(nl);
		sb.append("a b p2").append(nl);
		sb.append("a p2 a").append(nl);
		sb.append("a p2 c 3").append(nl);
		sb.append("a c p3").append(nl);
		sb.append("a p3 d").append(nl);
		sb.append("a d p2 3").append(nl);
		return sb.toString();
	}
}
