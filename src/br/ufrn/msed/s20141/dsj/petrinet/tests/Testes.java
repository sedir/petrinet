package br.ufrn.msed.s20141.dsj.petrinet.tests;

import static org.junit.Assert.*;

import java.io.IOException;

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
	
	@Test
	public void test_pseudo_inversa() {
		double [][] matrix = {{1.000000, 0.000000, 0.000000, 0.000000, 2.000000}, 
						 {0.000000, 0.000000, 3.000000, 0.000000, 0.000000}, 
						 {0.000000, 0.000000, 0.000000, 0.000000, 0.000000}, 
						 {0.000000, 4.000000, 0.000000, 0.000000, 0.000000}
						};
		SimpleMatrix A = new SimpleMatrix(matrix);
		SimpleSVD svd = A.svd();


		SimpleMatrix U = svd.getU();
		SimpleMatrix W = svd.getW();
		SimpleMatrix V = svd.getV();
		
		SimpleMatrix VT = V.transpose();
		//[U] * [W] * [V-Transpose] = A
		SimpleMatrix U_W_VT = U.mult(W).mult(VT);
		
		SimpleMatrix UT = U.transpose();
		SimpleMatrix Wpseudo = W.pseudoInverse();
		
		
		//Pseudo-Inverse = [V] * [W+] * [U-Transpose]
		SimpleMatrix V_Wpseudo_UT = V.mult(Wpseudo).mult(UT);
        
        System.out.println("*****************Original Matrix****************");
        System.out.println(A);
        System.out.println();
        System.out.println("*******************After SVD********************");
        System.out.println("Matrix U");
        System.out.println(U);
        System.out.println("Matrix V:");
        System.out.println(V);
        System.out.println("Matrix W");
        System.out.println(W);
        System.out.println("*************************************************");
        System.out.println("Matrix U-Transpose:");
        System.out.println(UT);
        System.out.println("Matrix W+:");
        System.out.println(Wpseudo);        
        System.out.println("***********This is the Pseudo-Inverse**********");
        System.out.println("Product of [V] * [W+] * [U-Transpose]");
        System.out.println(V_Wpseudo_UT);     
        

        
	}
	
	public void testnet_firing_couter_vector() throws NumberFormatException, IOException {

		Petrinet pn = new MarkupProcessor(this.getNetFig416()).getPetrinet();
		
		DenseMatrix64F A = new DenseMatrix64F(pn.incidenceMatrix());
		
		DenseMatrix64F x0 = new DenseMatrix64F(new double [][] {{1.0, 0.0, 0.0, 0.0}} );
		DenseMatrix64F x = new DenseMatrix64F(new double [][] {{0.0, 0.0, 0.0, 1.0}} );
		
		DenseMatrix64F b = new DenseMatrix64F(A.numCols, 1);
		
		CommonOps.sub(x, x0, b); // c = x - x0
		System.out.println("-------------------------");
		System.out.println(b);
		System.out.println("-------------------------");
		
		DenseMatrix64F vx = new DenseMatrix64F(A.numRows, 1); //b
		
		LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.leastSquares(A.numRows, A.numCols);
		
		

		boolean solve = false;

		if( !solver.setA(A) ) {
			//é dita singular quando não admite uma inversa.
			//Se uma matriz A\, é singular, então o problema Ax=b\, ou não possui solução ou possui infinitas soluções.
			System.out.println("Matriz singular");
		}else if( solver.quality() <= 1e-8 ) {
			System.out.println("aproximadamente matriz singular");
		} else {
			solver.solve(b,vx);	
			solve = true;
		}
		
		//O sistema linear não foi resolvido.
		if (!solve) {
			//SolvePseudoInverseSvd solver2 = new SolvePseudoInverseSvd(A.numRows, A.numCols)
			//x=inv(A^T * A) * A^T * b
		}

		//aplicação da SVD serve para aplicar a pseudo-inversa.
		//decomposição em valores singulares
		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);
		//[U,S,V] = svd(A)	 DecompositionFactory.svd(A.numRows,A.numCols,true,true,false)
		//S = svd(A)	 DecompositionFactory.svd(A.numRows,A.numCols,false,false,true)

        if( !svd.decompose(A) )
            throw new RuntimeException("SVD failed");

        DenseMatrix64F U = svd.getU(null,false);
        DenseMatrix64F W = svd.getW(null);
        DenseMatrix64F V = svd.getV(null,false);
        
        
 
        
        DenseMatrix64F result = new DenseMatrix64F(A.numRows,A.numCols); 
        //solução: x = pseudoinverse(A)*b   //matlab x = pinv(A)*b
        
        
        SingularOps.nullSpace(svd, result, 0.00001);
        
        System.out.println(result);
        
        DenseMatrix64F r = new DenseMatrix64F(A.numCols,1); 
        SingularOps.nullVector(svd, true, r);
        System.out.println(r);
        
        System.out.println(A);
	}	

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
