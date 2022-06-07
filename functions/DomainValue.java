package functions;

import java.util.ArrayList;

import com.carrotsearch.hppc.IntArrayList;

import solver.ResourceNode;

/**
 * @author Francesco creato il 25/lug/2014 12:48:30
 */

public class DomainValue<T extends Value> {
	
	private T t;

	private ResourceNode domainValueResourceOwner;
	
	private ArrayList<DomainValue<T>> childrenDVs;

	
	public DomainValue() {
		childrenDVs = new ArrayList<>();
	}
	
	
	public ResourceNode getResOwner() {
		return domainValueResourceOwner;
	}

	public void setResOwner(ResourceNode resOwner) {
		this.domainValueResourceOwner = resOwner;
	}


//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}

	public ArrayList<DomainValue<T>> getChildrenDomainValues() {
		return childrenDVs;
	}

	public void setChildrenDomainValues(ArrayList<DomainValue<T>> childrenDVS) {
		this.childrenDVs = childrenDVS;
	}

	
	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public double toReal() {
		return t.toReal();
	}

	@Override
	public String toString() {
		if(t == null)
			return "error. null";
		return t.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t == null) ? 0 : t.hashCode());
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
		DomainValue<?> other = (DomainValue<?>) obj; // rimuove warning, ma resta unsafe
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}
	
	
	
}
