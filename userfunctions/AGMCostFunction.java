package userfunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import struct.HyperGraph;
import util.Mapping;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;

import functions.DomainValue;
import functions.HDWeightingFunction;

/**
 * @author Francesco creato il 29/lug/2014 18:20:28
 */

public class AGMCostFunction implements HDWeightingFunction<CostDV> {

	private HyperGraph hg;
	private Mapping g;
	
	public AGMCostFunction() {
		
	}
	
	public AGMCostFunction(HyperGraph hg, Mapping g) {
		// TODO Auto-generated constructor stub
		this.hg = hg;
		this.g = g;
	}
	
	@Override
	public DomainValue<CostDV> gh(IntArrayList chi, IntArrayList lambda, DomainValue<CostDV>[] values) {
		// TODO Auto-generated method stub
		
		DomainValue<CostDV> dv = new DomainValue<>();
		
		double cost = 0;
		
		double[] weights = new double[lambda.size()];
		
		for(int i = 0; i < lambda.size(); i++) {
			weights[i] = g.getAtomsSize().get(lambda.get(i)); 
//			cost +=  weights[i];
		}
		
		ObjectArrayList<ObjectArrayList<AtomVarDegree>> joinVarsNumDist = new ObjectArrayList<>();
		
		IntDoubleHashMap num_dist = new IntDoubleHashMap();
		
		double agmBound = estimateJoinCost(chi, lambda, weights, joinVarsNumDist);
		
		if(agmBound < 0) { // overflow
			
		 System.err.println("AGMBOUND - OVERFLOW");
			cost = Double.MAX_VALUE;
			agmBound = Double.MAX_VALUE;
			dv.setT(new CostDV(cost, agmBound, num_dist));
			return dv;
		}

		ObjectArrayList<AtomVarDegree> lista = new ObjectArrayList<>();
		
		for(int i = 0; i < joinVarsNumDist.size(); i++) {
			
			if(joinVarsNumDist.get(i).size() > 0) { 
				
				double min = 1/joinVarsNumDist.get(i).get(0).getDegree();
				
				int atomId = joinVarsNumDist.get(i).get(0).getAtomID();
				int varID = joinVarsNumDist.get(i).get(0).getVarID();
				
				for(int j = 1; j < joinVarsNumDist.get(i).size(); j++) {
					double temp = 1/joinVarsNumDist.get(i).get(j).getDegree();
					if(temp < min) {
						min = temp;
						atomId = joinVarsNumDist.get(i).get(j).getAtomID();
						varID = joinVarsNumDist.get(i).get(j).getVarID();
					}
			    }
				
				lista.add(new AtomVarDegree(atomId, varID, 1/min));
				
				if(joinVarsNumDist.get(i).size() > 1)
					agmBound *= min;
			    }
		}
		
		if(agmBound > 15000000) { // 
			cost = Double.MAX_VALUE;
			agmBound = Double.MAX_VALUE;
			dv.setT(new CostDV(cost, agmBound, num_dist));
			return dv;
		}
		
		for(int i = 0; i < lista.size(); i++) {
			num_dist.addTo(lista.get(i).getVarID(), updateNumDist(lista.get(i), agmBound));
		}
	
		if (values == null) { // questa risorsa non ha figli
//			cost+= agmBound;
			cost = agmBound;	
			dv.setT(new CostDV(cost, agmBound, num_dist));
			return dv;
		}
		
		cost+=agmBound;
		
		double minCosto = agmBound;
		
		sort(values); // figli ordinati rispetto alla loro cardinalità
		
		for(DomainValue<CostDV> d : values) { // il costo della risorsa dovrebbe essere la somma dei costi dei sotto alberi + il suo agm bound + il costo della semijoin 
			
			//cost+= d.getT().getEstimatedRows() + minCosto + d.toReal(); // d.toReal() = costo del sottoalbero
			cost += d.toReal();
			
			// cè qualcosa che non va... le selettività del figlio non è detto che siano più grandi rispetto al padre
			
			double t = getSelectivities(num_dist, d.getT().getDegrees());
					
			double temp = d.getT().getEstimatedRows() * minCosto * t;
			
			if(temp < minCosto) {
				minCosto = temp;
			}			
		}
			
		dv.setT(new CostDV(cost, minCosto, num_dist));
		
		return dv;
	}
	
	public Mapping getG() {
		return g;
	}

	public void setG(Mapping g) {
		this.g = g;
	}

	/**
	 * @param numDist
	 * @param numDist2
	 * @return
	 */
	private double getSelectivities(IntDoubleHashMap numDist, IntDoubleHashMap numDist2) {
		// TODO Auto-generated method stub
		
		double selectivity = 1;
		Iterator<IntDoubleCursor> it = numDist.iterator();
		
		while(it.hasNext()) {
			IntDoubleCursor x = it.next();
			Iterator<IntDoubleCursor> it2 = numDist2.iterator();
			int varX = x.key;
			while(it2.hasNext()) {
				IntDoubleCursor y = it2.next();
				int varY = y.key;
				if(varX == varY) {
					if(y.value > x.value)
						numDist.put(varX, y.value);
					selectivity *= 1/y.value;
				}
					
			}
		}
		return selectivity;
	}

	/**
	 * @param values
	 */
	private void sort(DomainValue<CostDV>[] values) {
		// TODO Auto-generated method stub
	
			for (int j = 1; j < values.length; j++) {
			
				DomainValue temp = values[j];
			
				int i = j - 1;
				for (; (i >= 0) && ( values[i].getT().getEstimatedRows() > ((CostDV)temp.getT()).getEstimatedRows()); i--) {
					values[i + 1] = values[i];
				
				}
				values[i + 1] = temp;
			
			}	
	}

	/**
	 * @param min
	 * @return
	 */
	private double updateNumDist(AtomVarDegree avd, double agm) {
		// TODO Auto-generated method stub
		// Update number of distinct values w.r.t. current view sizes
		return avd.getDegree() *  agm / g.getAtomsSize().get(avd.getAtomID());
	}

	public HyperGraph getHg() {
		return hg;
	}

	public void setHg(HyperGraph hg) {
		this.hg = hg;
	}


	public double estimateJoinCost(IntArrayList chi, IntArrayList lambda, double[] weights, ObjectArrayList<ObjectArrayList<AtomVarDegree>> selectivities) {
		
		//TODO manca l'aggiunta delle keys semplici...
		
		int size = lambda.size();
		
		double[] costs = new double[size];
		
		for(int i = 0; i < costs.length; i++) 
		   costs[i] = Math.log(weights[i])/Math.log(2);
		
		Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		
		for(int i = 0; i < chi.size(); i++) {
			
			ObjectArrayList<AtomVarDegree> SelChiI = new ObjectArrayList<>(size);
			
			selectivities.add(SelChiI);
			
			double[] constraint = new double[size];
			
			for(int j = 0; j < size; j++) {
				for(int k = 0; k < hg.getHyperEdges().size(); k++) {
					if(hg.getHyperEdges().get(k).getId() == lambda.get(j)) {
						if(hg.getHyperEdges().get(k).getVariables().contains(chi.get(i))) {
							constraint[j] = 1;
							SelChiI.add(new AtomVarDegree(lambda.get(j), chi.get(i), g.getDegreeAtomVar(lambda.get(j), chi.get(i))));
						}
						break;
					}
				}
			}
			constraints.add(new LinearConstraint(constraint, Relationship.GEQ, 1));
		}
		
		for(int j = 0; j < size; j++) {
			double[] constraint = new double[size];
			constraint[j] = 1;
			constraints.add(new LinearConstraint(constraint, Relationship.GEQ, 0));
		}
				
		LinearObjectiveFunction f = new LinearObjectiveFunction(costs, 0);

	    SimplexSolver solver = new SimplexSolver();
	    PointValuePair optSolution = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),GoalType.MINIMIZE, new NonNegativeConstraint(true)); 
	  
	    return Math.round(Math.pow(2, optSolution.getValue()));
//	    return Math.round((Math.pow(2, optSolution.getValue())) + Math.round(Math.pow(2, optSolution.getValue())/Math.pow(2, chi.size()))/2);
	    
	  }
	
	private class AtomVarDegree {
		
		private int atomID;
		private double degree;
		private int varID;
		
		public AtomVarDegree(int atomID, int varID, double degree) {
			this.atomID = atomID;
			this.degree = degree;
			this.varID = varID;
		}

		public int getAtomID() {
			return atomID;
		}

		public int getVarID() {
			return varID;
		}

		public void setAtomID(int atomID) {
			this.atomID = atomID;
		}

		public double getDegree() {
			return degree;
		}

		public void setDegree(double degree) {
			this.degree = degree;
		}
			
	}

}
