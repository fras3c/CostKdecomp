package userfunctions;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleHashMap;

import functions.Value;

/**
 * @author Francesco
 * creato il 06/ago/2014 18:08:40
 */
public class StructuralDV extends Value{

	private double cost;
	private double width;
	
	private IntArrayList chi;
	
	
	public StructuralDV(double cost, double width) {
		// TODO Auto-generated constructor stub
		this.cost = cost;
		this.width = width;
	}
	
	/* (non-Javadoc)
	 * @see function.Value#toReal()
	 */
	@Override
	public double toReal() {
		// TODO Auto-generated method stub
		return cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
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
		StructuralDV other = (StructuralDV) obj;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cost: " + cost + "\nNode width: " + width;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public IntArrayList getChi() {
		return chi;
	}

	public void setChi(IntArrayList chi) {
		this.chi = chi;
	}
	
}
