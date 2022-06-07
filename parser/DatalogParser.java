package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import struct.HyperEdge;
import struct.HyperGraph;
import util.Mapping;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ObjectIntHashMap;


/**
 * @author Francesco
 * creato il 19/mag/2016 17:53:00
 */
public class DatalogParser {
	
	public int id = 0;
	private int idVar = 0;

	private HyperGraph constraintHyperGraph;
	private HyperGraph resourceHyperGraph;
	private Mapping mapping;
	
	private static IntArrayList artificialVars;
	
	private int nov;
	
	private int noa;
	
	private double parserTime;
	
	private boolean completeDecomp;
	
	/**
	 * 
	 */
	public DatalogParser() {
		// TODO Auto-generated constructor stub
		constraintHyperGraph = new HyperGraph();
		resourceHyperGraph = new HyperGraph();
		mapping = new Mapping();
		completeDecomp = false;
		
		if(completeDecomp)
			artificialVars = new IntArrayList();
	}
	
	public DatalogParser(boolean completeDecomposition) {
		// TODO Auto-generated constructor stub
		constraintHyperGraph = new HyperGraph();
		resourceHyperGraph = new HyperGraph();
		mapping = new Mapping();
		this.completeDecomp = completeDecomposition;
		
		if(completeDecomp)
			artificialVars = new IntArrayList();
	}
	
	public DatalogParser(String datalogExpression, boolean completeDecomp) {
		// TODO Auto-generated constructor stub
		long tStart = System.nanoTime();
		constraintHyperGraph = new HyperGraph();
		resourceHyperGraph = new HyperGraph();
		mapping = new Mapping();
		
		this.completeDecomp = completeDecomp;
		
		if(completeDecomp)
			artificialVars = new IntArrayList();
		
		parse(datalogExpression);
		
		long tEnd = System.nanoTime();
		
		parserTime = (tEnd - tStart) / 1e9;
		
	}
	public DatalogParser(String datalogExpression, String atomsSizes, String varDomains, boolean completeDecomp) {
		// TODO Auto-generated constructor stub
		long tStart = System.nanoTime();
		constraintHyperGraph = new HyperGraph();
		resourceHyperGraph = new HyperGraph();
		mapping = new Mapping();
		
		this.completeDecomp = completeDecomp;
		
		if(completeDecomp)
			artificialVars = new IntArrayList();
		
        parse(datalogExpression);
        
        fetchAtomSizes(atomsSizes);
        
        fetchDistinctValues(varDomains);
        
        long tEnd = System.nanoTime();
        
        parserTime = (tEnd - tStart) / 1e9;
        
	}
	
	public double getTime() {
		return parserTime;
	}
	
	public int nov() {
		return nov;
	}

	public int noa() {
		return noa;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {  // non funziona generazionekvertex
		// TODO Auto-generated method stub
		DatalogParser parser = new DatalogParser(false);
		
		parser.parse("/Users/flupia/Documents/workspace/GreedyTP_MOD2/test.txt");
		
		parser.fetchAtomSizes("testSizes.txt");
		
		parser.fetchDistinctValues("testDistinctValues.txt");
		
		
		
//		parser.fetchVarAtomSelectivities("testDistinctValues.txt");
		
		HyperGraph hg = parser.getConstraintHyperGraph();
		
		Mapping m = parser.getMapping();
		
		System.out.println("constraints size " + hg.getHyperEdges().size());
		
 	}
	
	public void parseDocument(String constraintHGPath) {
		parse(constraintHGPath);
	}
	
	public void fetchAtomSizes(String sizesPath) {
		
		Path path = FileSystems.getDefault().getPath(sizesPath);
		
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				
				Pattern p = Pattern.compile("[^\\w]");
				
					String[] split = p.split(line);
					String atom = split[0];
					
					int x = mapping.getAtomID(atom);
					
					int y = Integer.parseInt(split[split.length - 1]);
					mapping.getAtomsSize().put(x, y);
					
			}

					
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
  }	
	
	
	/**
	 * 
	 * Questo metodo ci permette di essere molto più precisi nelle stima. 
	 * Infatti non consideriamo l'intero dominio della variabile ma la proiezione del suo dominio
	 * rispetto l'atomo che stiamo considerando.
	 */
	
	public void fetchVarAtomSelectivities(String degreesPath) {
		// TODO Auto-generated method stub
		
		Path path = FileSystems.getDefault().getPath(degreesPath);
		
//		int linesCount = 0;
		// serve per le keys... vediamo dopo
//		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
//			String line = null;
//			
//			while ((line = reader.readLine()) != null) {
//				linesCount++;
//				String[] split = line.split(";");
//				double value = Double.parseDouble(split[split.length - 1]);
//				if(value == 1) {
//					String constr = split[1].split("\\(")[0];
//					String var = split[2];
//					keys.put(constr, mapping.getVarID(var));
//				}
//			}
//		} catch (IOException x) {
//			System.err.format("IOException: %s%n", x);
//		}
//		System.out.println(linesCount);
//		degrees  = new ObjectIntHashMap<String>(linesCount);
//	    path = FileSystems.getDefault().getPath("instance.txtdeg.csv");
		
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {			
				String[] split = line.split(",");
				double value = Double.parseDouble(split[2]);
		
				String atom = split[0];
				int atomID = mapping.getAtomID(atom);
				
				String var = split[1];
				int varID = mapping.getVarID(var);
				
				if(!mapping.getAtomVarDegree().containsKey(atomID))
					mapping.getAtomVarDegree().put(atomID, new HashMap<>());
			
					mapping.getAtomVarDegree().get(atomID).put(varID, value);
				
//				String key = atom+";"+mapping.getVarID(var);
//				degrees.put(key, value);
				
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		
		if(completeDecomp)
		for(int atomId = 0; atomId < artificialVars.size(); atomId++) {
			mapping.getAtomVarDegree().get(atomId).put(artificialVars.get(atomId), 1.0);
		}
	}
	
	
	/**
	 * Questo metodo lo usiamo quando non conosciamo in modo dettagliato 
	 * i valori distinti che assume la variabile in quell'atomo
	 */
	
	public void fetchDistinctValues(String degreesPath) {
		// TODO Auto-generated method stub
		
		Path path = FileSystems.getDefault().getPath(degreesPath);
		
//		int linesCount = 0;
		// serve per le keys... vediamo dopo
//		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
//			String line = null;
//			
//			while ((line = reader.readLine()) != null) {
//				linesCount++;
//				String[] split = line.split(";");
//				double value = Double.parseDouble(split[split.length - 1]);
//				if(value == 1) {
//					String constr = split[1].split("\\(")[0];
//					String var = split[2];
//					keys.put(constr, mapping.getVarID(var));
//				}
//			}
//		} catch (IOException x) {
//			System.err.format("IOException: %s%n", x);
//		}
//		System.out.println(linesCount);
//		degrees  = new ObjectIntHashMap<String>(linesCount);
//	    path = FileSystems.getDefault().getPath("instance.txtdeg.csv");
		
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {			
				String[] split = line.split(",");
				double value = Double.parseDouble(split[1]);
	
				String var = split[0];
				
				int varID = mapping.getVarID(var);
				
				mapping.getVarDistinctValues().put(varID, value);
				
//				String key = atom+";"+mapping.getVarID(var);
//				degrees.put(key, value);				
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		
		if(completeDecomp)
			for(int atomId = 0; atomId < artificialVars.size(); atomId++) {
				mapping.getVarDistinctValues().put(artificialVars.get(atomId), 1.0);
			}
		
//		for(int i = 0; i < constraintHyperGraph.getHyperEdges().size(); i++) {
//			
//			int atomID =  constraintHyperGraph.getHyperEdges().get(i).getId();
//			
//			if(!mapping.getAtomVarDegree().containsKey(atomID))
//				mapping.getAtomVarDegree().put(atomID, new HashMap<>());
//			
//			for(int j = 0; j < constraintHyperGraph.getHyperEdges().get(i).getVariables().size(); j++){
//			
//				int varID = constraintHyperGraph.getHyperEdges().get(i).getVariables().get(j);
//		
//				mapping.getAtomVarDegree().get(atomID).put(varID, mapping.getVarDistinctValues().get(varID));
//			}
//		}
	}
	
	public void parse(String constraintHGPath) {
		
		Path path = FileSystems.getDefault().getPath(constraintHGPath);
		
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				
				Pattern p = Pattern.compile("[^\\w]");
				
					String[] vars = p.split(line);
					String atom = vars[0];
					
					int x = mapping.getAtomID(atom);
					
					if (x == -1) {
						mapping.getAtomMap().put(id, atom);
						x = id;
						id++;
						noa++;
					}
					
					IntArrayList nodes = new IntArrayList();
					HyperEdge he = new HyperEdge(x, nodes);
					he.setName("{"+atom+"}");
//					constraintHyperGraph.getVars().add(idVar);
//					nodes.add(idVar);
//					mapping.getVarsMap().put(idVar, atom);
//					artificialVars.add(idVar);
//					idVar++;
	
					for(int i = 1 ; i < vars.length; i++)  {
//					for (String var : vars) {
						String var = vars[i];
						if (!var.equals(" ") && !var.equals("")) {
														
							//if(var.equals("w"))
//								System.out.println("stop");
							x = mapping.getVarID(var);
							if (x == -1) {
								mapping.getVarsMap().put(idVar, var);
								x = idVar;
								constraintHyperGraph.getVars().add(x);
								idVar++;
								nov++;
							}
							nodes.add(x); 
						}
					}
					
					he.getVariables().trimToSize();
					Arrays.parallelSort(he.getVariables().buffer);
					constraintHyperGraph.getHyperEdges().add(he);

			}
			
			if(completeDecomp) {
				artificialVars.trimToSize();
				nov += artificialVars.size();
			}
			
				// if(true)
				// return;

					int m = constraintHyperGraph.getVars().size();
				
					constraintHyperGraph.createAdjacencyList(m);
				
					for(int j = 0; j < constraintHyperGraph.getHyperEdges().size(); j++) {
						IntArrayList nodes = constraintHyperGraph.getHyperEdges().get(j).getVariables();
						int[] vars = nodes.toArray();
						for(int i = 0; i < vars.length; i++) {
							for(int k = i + 1; k < vars.length; k++) {
								constraintHyperGraph.getAdjacencyList()[vars[i]].add(vars[k]);
								constraintHyperGraph.getAdjacencyList()[vars[k]].add(vars[i]);
							}
						}
					}
					
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		constraintHyperGraph.getHyperEdges().trimToSize();
	}

	public HyperGraph getConstraintHyperGraph() {

		return constraintHyperGraph;
	}

	public HyperGraph getResourceHyperGraph() {

		return resourceHyperGraph;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public static IntArrayList getArtificialVars() {
		return artificialVars;
	}
}
