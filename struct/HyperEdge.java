package struct;

import java.util.LinkedHashSet;
import java.util.Set;

import com.carrotsearch.hppc.IntArrayList;

/**
 * @author Francesco creato il 06/mag/2014 15:10:46
 */

public class HyperEdge {

	private int id;
	private String name;
	private IntArrayList variables;
	private int noa = 1; // default
	private int usatePerAttaccare;

	public HyperEdge(int id, String name) {
		this.id = id;
		this.name = name;
		this.variables = new IntArrayList();
		
	}
	public HyperEdge(int id, String name, IntArrayList variables) {
		this.id = id;
		this.name = name;
		this.variables = variables;
		
	}

	/**
	 * @param id2
	 * @param name2
	 * @param asList
	 */
	public HyperEdge(int id,  IntArrayList variables) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = "";
		this.variables = variables;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IntArrayList getVariables() {
		return variables;
	}

	public void setVariables(IntArrayList listIDNodes) {
		this.variables = listIDNodes;
	}

	@Override
	public String toString() {
		String result = "HyperEdge ["+name+"] -> "; 
		for(int i = 0; i < variables.size(); i++)
			result+= variables.get(i) + ", ";
		return result;
	}
	
	public IntArrayList intersect1(IntArrayList variables) {
		
		IntArrayList intersection = new IntArrayList();
		int i = 0;
		int j = 0;
		while( i < variables.size() && j < this.variables.size()) {
			
			if(variables.get(i) < this.variables.get(j))
				i++;
			else if(variables.get(i) > this.variables.get(j))
				j++;
			else {
				intersection.add(this.variables.get(j));
				i++;
				j++;
			}
		}
		
//		for (int i = 0; i < variables.size(); i++) {
//			int v = variables.get(i);
//			if (this.variables.contains(v)){
//				intersection.add(v);
//				usatePerAttaccare++;
//			}
//		}
		return intersection;
	}
	public int intersect2(IntArrayList variables) {
		
		int counter = 0;
		int i = 0;
		int j = 0;
		while( i < variables.size() && j < this.variables.size()) {
			
			if(variables.get(i) < this.variables.get(j))
				i++;
			else if(variables.get(i) > this.variables.get(j))
				j++;
			else {
				counter++;
				i++;
				j++;
			}
		}
//		usatePerAttaccare = counter;
		return counter;
	}
	
//	public IntArrayList intersect(IntArrayList variables) {
//
//		IntArrayList intersection = new IntArrayList();
//
//		for (int i = 0; i < variables.size(); i++) {
//			int v = variables.get(i);
//			if (this.variables.contains(v)){
//				intersection.add(v);
//				usatePerAttaccare++;
//			}
//		}
//		return intersection;
//	}
	
	public boolean contains(IntArrayList variables) {
		
		int risultato = intersect2(variables);
		if(risultato == variables.size())
			return true;
		return false;
		
	}
//	public boolean contains(IntArrayList variables) {
//
//		for (int i = 0; i < variables.size(); i++) {
//			int v = variables.get(i);
//			if (!this.variables.contains(v))
//				return false;
//		}
//		return true;
//
//	}
	
	
	public int getNoa() {
		return noa ;
	}

	public void setNoa(int noa) {
		this.noa = noa;
	}
	
	public int getUsatePerAttaccare() {
		return usatePerAttaccare;
	}
	
	public void resetUsatePerAttaccare() {
		 usatePerAttaccare = 0;
	}
	public void setUsatePerAttaccare(int x) {
		usatePerAttaccare = x;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((variables == null) ? 0 : variables.hashCode());
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
		HyperEdge other = (HyperEdge) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (variables == null) {
			if (other.variables != null)
				return false;
		} else if (!variables.equals(other.variables))
			return false;
		return true;
	}
	
	
}
