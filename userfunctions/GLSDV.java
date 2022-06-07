package userfunctions;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleHashMap;

import functions.Value;

/**
 * @author Francesco
 * creato il 06/ago/2014 18:08:40
 */
public class GLSDV extends Value{

	private double estimatedCost;
	private double estimatedRows;
	
	private IntArrayList chi;
	
	
	public GLSDV(double estimatedCost, double estimatedRow) {
		// TODO Auto-generated constructor stub
		this.estimatedCost = estimatedCost;
		this.estimatedRows = estimatedRow;
	}
	
	/* (non-Javadoc)
	 * @see function.Value#toReal()
	 */
	@Override
	public double toReal() {
		// TODO Auto-generated method stub
		return estimatedCost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(estimatedCost);
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
		GLSDV other = (GLSDV) obj;
		if (Double.doubleToLongBits(estimatedCost) != Double.doubleToLongBits(other.estimatedCost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Estimated cost: " + estimatedCost + "\nEstimated rows: " + estimatedRows;
	}

	public double getEstimatedRows() {
		return estimatedRows;
	}

	public void setEstimatedRows(double estimatedRows) {
		this.estimatedRows = estimatedRows;
	}

	public IntArrayList getChi() {
		return chi;
	}

	public void setChi(IntArrayList chi) {
		this.chi = chi;
	}
	
}
