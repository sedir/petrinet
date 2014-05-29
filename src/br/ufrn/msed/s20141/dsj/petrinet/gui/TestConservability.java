package br.ufrn.msed.s20141.dsj.petrinet.gui;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

import br.ufrn.msed.s20141.dsj.petrinet.models.Petrinet;
import br.ufrn.msed.s20141.dsj.petrinet.models.Place;
import br.ufrn.msed.s20141.dsj.petrinet.models.Transition;

public class TestConservability {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// The coefficient matrix 'a' is dense 1D matrix 

		//
		//		// A right hand side vector, which is simple dense vector
		//		Vector b = new BasicVector(new double[] {
		//		   0, 0, 0, 0, 0
		//		});
		//
		//		// We will use Least Squares method,
		//		// which is based on QR decomposition and can be used with overdetermined systems
		//		LinearSystemSolver solver = a.withSolver(LinearAlgebra.LEAST_SQUARES);
		//		// The 'x' vector will be sparse
		//		Vector x = solver.solve(b, LinearAlgebra.DENSE_FACTORY);
		//		System.out.println(x);
		//		
		//		
		//		

//		DenseMatrix64F A = new DenseMatrix64F(new double[][] {
//				{  1.0,  0.0,  0.0, 0.0 },
//				{  0.0,  1.0,  0.0, 0.0 },
//				{  0.0,  2.0,  0.0, 0.0 },
//				{  1.0,  1.0,  0.0, 0.0 },
//				{  1.0, -1.0,  0.0, 0.0 }
//		});
//
//		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);
//
//		if( !svd.decompose(A) )
//			throw new RuntimeException("Decomposition failed");
//
//		DenseMatrix64F U = svd.getU(null,false);
//		DenseMatrix64F W = svd.getW(null);
//		DenseMatrix64F V = svd.getV(null,false);
//
//		DenseMatrix64F result = new DenseMatrix64F(A.numRows,A.numCols); 
//
//		SingularOps.nullSpace(svd, result, 0.00001);
//
//		System.out.println(result);
//
//		System.out.println(A);
		
		
		
		
		Petrinet pn = new Petrinet("Minha rede");
		Place p1 = pn.place("p1",1);
		Place p2 = pn.place("p2");
		Place p3 = pn.place("p3");
		Place p4 = pn.place("p4");
		
		Transition t1 = pn.transition("t1");
		Transition t2 = pn.transition("t2");
		Transition t3 = pn.transition("t3");

		pn.arc(p1, t1);
		pn.arc(t1, p2);
		pn.arc(t1, p3);
		
		pn.arc(t2, p1);
		pn.arc(p2, t2);
		
		pn.arc(p2, t3);
		pn.arc(t3, p4);
		
		DenseMatrix64F A = new DenseMatrix64F(pn.incidenceMatrix());
		
		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(A.numRows,A.numCols,true,true,false);

        if( !svd.decompose(A) )
            throw new RuntimeException("Decomposition failed");

        DenseMatrix64F U = svd.getU(null,false);
        DenseMatrix64F W = svd.getW(null);
        DenseMatrix64F V = svd.getV(null,false);
        
        DenseMatrix64F result = new DenseMatrix64F(A.numRows,A.numCols); 
        		
        SingularOps.nullSpace(svd, result, 0.00001);
        
        System.out.println(result);
        
        System.out.println(A);
				
	}

}
