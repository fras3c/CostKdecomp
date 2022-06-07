package util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.carrotsearch.hppc.IntArrayList;

/**
 * @author Francesco creato il 16/mag/2014 12:35:49
 */
public class Mapping {

	private Map<Integer, String> atomsNameMap;
	private Map<Integer, String> varsNameMap;
	private Map<Integer, Integer> atomsSize;
	
	/**
	 * serve per la questione delle keys
	 */
	private Map<Integer, Map<Integer, Double>> atomVarDegree;
	
	
	private Map<Integer, Double> varDistinctValues;
	

	/**
	 * 
	 */
	public Mapping() {

		atomsNameMap = new LinkedHashMap<Integer, String>();
		varsNameMap = new LinkedHashMap<Integer, String>();
		atomsSize = new LinkedHashMap<>();
		atomVarDegree = new HashMap<>();
		varDistinctValues = new HashMap<>();
	}
	
	public Map<Integer, Integer> getAtomsSize() {
		return atomsSize;
	}

	public void setAtomVars(Map<Integer, Integer> atomsSize) {
		this.atomsSize = atomsSize;
	}

	public Map<Integer, String> getAtomMap() {
		return atomsNameMap;
	}

	public void setAtomsMap(Map<Integer, String> atomsName) {
		this.atomsNameMap = atomsName;
	}

	public String getAtomNameById(int id) {
		return atomsNameMap.get(id);
	}
	public String getVarNameById(int id) {
		return varsNameMap.get(id);
	}

	public void setVarsMap(Map<Integer, String> varsName) {
		this.varsNameMap = varsName;
	}

	/**
	 * @return
	 */
	public Map<Integer, String> getVarsMap() {
		// TODO Auto-generated method stub
		return varsNameMap;
	}
	
	public int getAtomID(String atom) {
		
		for(Map.Entry<Integer, String> name : atomsNameMap.entrySet())
			if(name.getValue().equals(atom))
				return name.getKey();
		return -1;
	}
	public int getVarID(String var) {
		
		for(Map.Entry<Integer, String> variable : varsNameMap.entrySet())
			if(variable.getValue().equals(var))
				return variable.getKey();
		return -1;
	}
	public int getVarAtom(String atom) {
		
		for(Map.Entry<Integer, String> a : atomsNameMap.entrySet())
			if(a.getValue().equals(atom))
				return a.getKey();
		return -1;
	}
	
	public String toString() {
		
		String result = "";
		
		for(Map.Entry<Integer, String> variable : varsNameMap.entrySet())
			result += variable.getKey() + " -> " + variable.getValue()+"\n";
		
		return result;
	}

	public Map<Integer, Map<Integer, Double>> getAtomVarDegree() {
		return atomVarDegree;
	}

	/**
	 * @param i
	 * @param j
	 */
	public double getDegreeAtomVar(int atom, int var) {
		// TODO Auto-generated method stub
//		System.out.println("atom " + atom + " var " + var);
		return atomVarDegree.get(atom).get(var);
	}
	
	public double getVarDistinctValues(int var) {
		return varDistinctValues.get(var);
	}

	public Map<Integer, Double> getVarDistinctValues() {
		return varDistinctValues;
	}

	
}
