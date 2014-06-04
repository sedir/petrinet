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

public class Testes {
	private static final String nl = "\n";
	private Petrinet petrinet1;
	private Petrinet petrinet2;

	@Before
	public void setup(){
		try {
			petrinet1 = new MarkupProcessor(this.getNet1()).getPetrinet();
			petrinet2 = new MarkupProcessor(this.getNet2()).getPetrinet();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	//	@Test
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

	//	@Test
	//	public void test_pseudo_inversa() {
	public static void main(String[] args) {
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
	@Test
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
//	@Test
	public void testnet_conservability() throws NumberFormatException, IOException {

		//Petrinet pn = new MarkupProcessor(this.getNetNoConservative()).getPetrinet();
		Petrinet pn = new MarkupProcessor(this.getNetFig416()).getPetrinet();
		//Petrinet pn = new MarkupProcessor(this.getNetStrictlyConservative()).getPetrinet();

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

		System.out.println(result);

		DenseMatrix64F r = new DenseMatrix64F(A.numCols,1); 
		SingularOps.nullVector(svd, true, r);
		System.out.println(r);

		System.out.println(A);
	}
	@Test
	public void testnet1_valida_disparos() {
		// Valida estado inicial
		assertArrayEquals(new double [] {2.0, 0.0, 0.0, 0.0}, petrinet1.stateVector(),0);
		Transition t = petrinet1.getTransition("t1");
		t.fire();
		assertArrayEquals(new double [] {1.0, 1.0, 1.0, 0.0}, petrinet1.stateVector(),0);		
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 2.0, 0.0}, petrinet1.stateVector(),0);
		assertEquals(t.canFire(), false);

		t = petrinet1.getTransition("t2");
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 1.0, 1.0}, petrinet1.stateVector(),0);
		t.fire();
		assertArrayEquals(new double [] {0.0, 2.0, 0.0, 2.0}, petrinet1.stateVector(),0);		

	}
	@Test
	public void testnet1_ha_estado_bloqueante() {
		assertEquals(true, petrinet1.hasDeadlock());
		//		petrinet1.getTransitions();
	}
	@Test
	public void testnet2_ha_estado_bloqueante() {
		assertEquals(true, petrinet2.hasDeadlock());
	}
	@Test
	public void testenet1_ha_estado_limitado() {
		assertEquals(true, petrinet1.hasBounded());
	}
	@Test
	public void testenet1_estado_alcancavel() {
		assertEquals(true,petrinet1.hasPlaceReachable(new double [] {0.0, 2.0, 0.0, 2.0}));
		assertEquals(false,petrinet1.hasPlaceReachable(new double [] {0.0, 2.0, 0.0, 1.0}));
	}	
	@Test
	public void testenet2_estado_alcancavel() {
		assertEquals(true, petrinet1.hasBounded());
		petrinet1.hasPlaceReachable(new double [] {1.0, 1.0, 1.0, 0.0});
	}	
	@Test
	public void testenet2_ha_estado_nao_limitado() {
		assertEquals(false, petrinet2.hasBounded());
	}
	@Test
	public void testnet1_valida_transicoes_ativas() {
		Transition t = petrinet1.getTransition("t1");
		arrayEquals(new boolean [] {true, false, false}, petrinet1.getEnabledTransitions());
		t.fire();
		arrayEquals(new boolean [] {true, true, false}, petrinet1.getEnabledTransitions());	
		t.fire();
		arrayEquals(new boolean [] {false, true, false}, petrinet1.getEnabledTransitions());	
		assertEquals(t.canFire(), false);

		t = petrinet1.getTransition("t2");
		t.fire();
		arrayEquals(new boolean [] {false, true, false}, petrinet1.getEnabledTransitions());	
		t.fire();
		arrayEquals(new boolean [] {false, false, false}, petrinet1.getEnabledTransitions());			

	}


	private void arrayEquals(boolean [] expecteds, boolean [] actuals) {
		for (int i = 0; i < actuals.length; i++) {
			if (expecteds[i] != actuals[i])
				assertEquals(true, false);
		}
		assertEquals(true, true);
	}	
	private String getNet1() {
		StringBuilder sb = new StringBuilder("net Minha rede ");
		sb.append(nl);
		sb.append("p p1 2").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3").append(nl);
		sb.append("p p4").append(nl);
		sb.append(nl);
		sb.append("t t1").append(nl);
		sb.append("t t2").append(nl);
		sb.append("t t3").append(nl);
		sb.append(nl);
		sb.append("a p1 t1").append(nl);
		sb.append("a p1 t3").append(nl);
		sb.append("a t1 p2").append(nl);
		sb.append("a t1 p3").append(nl);
		sb.append("a p2 t2").append(nl);
		sb.append("a t2 p2").append(nl);
		sb.append("a p3 t2").append(nl);
		sb.append("a p3 t3").append(nl);
		sb.append("a t2 p4").append(nl);
		sb.append("a p4 t3	").append(nl);	
		return sb.toString();
	}
	private String getNet2() {

		StringBuilder sb = new StringBuilder("Exemplo Motivação");
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
		sb.append("a t1 p3").append(nl);
		sb.append("a t1 p2").append(nl);
		sb.append("a p3 t3").append(nl);
		sb.append("a t3 p3").append(nl);
		sb.append("a p2 t3").append(nl);
		sb.append("a p2 t2").append(nl);
		sb.append("a t2 p1").append(nl);
		sb.append("a t3 p4").append(nl);
		return sb.toString();
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

}
