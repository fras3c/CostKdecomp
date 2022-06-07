package userfunctions;

import com.carrotsearch.hppc.IntDoubleHashMap;

import functions.Value;

/**
 * @author Francesco
 * creato il 06/ago/2014 18:08:40
 */
public class CostDV extends Value {

	private double cumulatedCost;
	private double bagCost;
	
	private IntDoubleHashMap selectivities;
	
	public CostDV(double cumulatedCost, double bagCost, IntDoubleHashMap selectivities) {
		// TODO Auto-generated constructor stub
		this.cumulatedCost = cumulatedCost;
		this.bagCost = bagCost;
		this.selectivities = selectivities;
	}
	
	/* (non-Javadoc)
	 * @see function.Value#toReal()
	 */
	@Override
	public double toReal() {
		// TODO Auto-generated method stub
		return cumulatedCost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cumulatedCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CostDV other = (CostDV) obj;
		if (Double.doubleToLongBits(cumulatedCost) != Double.doubleToLongBits(other.cumulatedCost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Estimated cost: " + cumulatedCost + "\nEstimated rows: " + bagCost;
	}

	public double getEstimatedRows() {
		return bagCost;
	}

	public void setEstimatedRows(double estimatedRows) {
		this.bagCost = estimatedRows;
	}

	public IntDoubleHashMap getDegrees() {
		return selectivities;
	}

	public void setSelectivities(IntDoubleHashMap selectivities) {
		this.selectivities = selectivities;
	}

	
	
}
