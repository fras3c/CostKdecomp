# CostKdecomp
An optimal cost-based algorithm (cost-k-decomp) for optimal hypertree decompositions inspired by https://dl.acm.org/doi/10.1145/1055558.1055587

Please note that I've developed a simple python script (please check my github repos) which wraps the main algorithm to allow for an easier usage. 
By default this script computes and optimal hypertree decomposition using a structural cost function, that is, it exploits only information about the hypergraph representation.

However, for those situations where we have further information at hand on the given problem, for instance consider concrete contexts like database query answering where we can use statistics such as the size of relations, the selectivity of attributes, indexes, etc., we have built a custom cost (or weighting) function which is able to exploit such statistics in the computation of the Hypertree Decomposition which will improve the runtimes of join algorithms. This cost function is called GLSFunction. 


For details about cost functions please have a look at Section 4.5 of my PhD thesis https://scholar.google.it/citations?view_op=view_citation&hl=it&user=icnAjqQAAAAJ&citation_for_view=icnAjqQAAAAJ:Y0pCki6q_DkC


