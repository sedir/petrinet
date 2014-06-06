package br.ufrn.msed.s20141.dsj.petrinet.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import Jama.Matrix;
import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;
import br.ufrn.msed.s20141.dsj.petrinet.util.FourierMotzkin;
import br.ufrn.msed.s20141.dsj.petrinet.util.FourierMotzkinElimination;
import br.ufrn.msed.s20141.dsj.petrinet.util.MarkupProcessor;

public class TesteConservativeness {
	private static final String nl = "\n";
	private Petrinet petrinet1;
	private Petrinet petrinet2;

		@Test
	public void testFourierMotzkinAlgorithm() throws NumberFormatException, IOException {

		Petrinet pn = new MarkupProcessor(this.getNetFig213()).getPetrinet();
		//		System.out.println(pn);
		pn.printIncidenceMatrix();
		Matrix C = new Matrix(pn.incidenceMatrix());
		C = C.transpose();
		FourierMotzkinElimination farkas = new FourierMotzkinElimination(C.getArrayCopy());
		double i[] = farkas.getInvariants();
		System.out.println("Array Invariantes");
		for (double d : i) {
			System.out.print(d+", ");
		}

		System.out.println(farkas);

	}
//	@Test
	public void testFourierMotzkinAlgorithm2() throws NumberFormatException, IOException {
		//		double[][] C = { { -1, 1, 0, 0 },
		//		{ 1, -1, 0, 0 },
		//		{ -1, 1, -3, -3},
		//		{0, 0, 1, -1},
		//		{0, 0, -1, 1}
		//};
		//		double[][] C = { { -1, 1,  1, -1 },
		//				{ 1, -1, -1, 1 },
		//				{ 0, 0, 1, 0},
		//				{1, 0, 0, -1},
		//				{-1, 0, 0, 1}
		//		};
		//		double[][] C = { { -1, 1,  0, 0 },
		//				{ 1, -1, 0, 0 },
		//				{ 0, 0, 1, -1},
		//				{0, 0, -1, 1},
		//				{0, 1, -1, 0}
		//		};
		double[][] C = { { -1, 1,  0 },
				{ 1, -1, 0},
				{ 0, 1, -1},
				{0, -1, 1}
		};
		FourierMotzkinElimination farkas = new FourierMotzkinElimination(C);

		double i[] = farkas.getInvariants();
		System.out.println("Array Invariantes");
		for (double d : i) {
			System.out.print(d+", ");
		}
		System.out.println(farkas);

	}


	@Test
	public void test_proximo_estado() throws NumberFormatException, IOException {
		Petrinet pn = new MarkupProcessor(this.getNetFig21()).getPetrinet();
		double nextState[] =  pn.getNextState(new double[] {0.0, 3.0, 0.0},pn.getTransition("a"));
		assertArrayEquals(new double [] {1.0, 2.0, 0.0}, nextState,0);

		nextState =  pn.getNextState(new double[] {0.0, 3.0, 0.0},pn.getTransition("c"));
		assertArrayEquals(new double [] {0.0, 0.0, 1.0}, nextState,0);

		nextState =  pn.getNextState(new double[] {0.0, 3.0, 0.0},new double[] {2.0, 1.0, 0.0, 0.0});
		assertArrayEquals(new double [] {1.0, 2.0, 0.0}, nextState,0);

		pn = new MarkupProcessor(this.getNetFig24()).getPetrinet();
		//a sequência abcd não pode ser disparada
		nextState =  pn.getNextState(new double[] {1.0, 0.0, 0.0, 0.0},new double[] {1.0, 1.0, 1.0, 1.0});
		assertArrayEquals(null, nextState,0);
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
	private String getNetFig213() {
		//Figura 2.13: Invariantes
		//Livro REDES DE PETRI de JANETTE CARDOSO
		//
		StringBuilder sb = new StringBuilder("Fig 2.13");
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
		sb.append("a p3 c 3").append(nl);
		sb.append("a c p4").append(nl);
		sb.append("a d p3 3").append(nl);
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
	private String getNetFig24() {
		//Figura 2.4: 
		//Livro REDES DE PETRI de JANETTE CARDOSO
		//
		StringBuilder sb = new StringBuilder("Fig8");
		sb.append(nl);
		sb.append("p p1 1").append(nl);
		sb.append("p p2").append(nl);
		sb.append("p p3").append(nl);
		sb.append("p p4").append(nl);
		sb.append(nl);
		sb.append("t a").append(nl);
		sb.append("t b").append(nl);
		sb.append("t c").append(nl);
		sb.append("t d").append(nl);
		sb.append(nl);
		sb.append("a p1 a").append(nl);
		sb.append("a a p2").append(nl);
		sb.append("a p2 b").append(nl);
		sb.append("a b p3").append(nl);
		sb.append("a p3 c").append(nl);
		sb.append("a c p4").append(nl);
		sb.append("a p4 d").append(nl);
		sb.append("a d p1").append(nl);
		sb.append("a p1 b").append(nl);
		sb.append("a c p1").append(nl);
		return sb.toString();
	}
}
