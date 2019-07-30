package org.cud2v.graphcluster.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameGraph {

	private Map<Integer,Edge> edges;
	private Map<Integer,VertName> vertexes;

	//colocar hashMap em Vertice e talvez em arestas tbm
	public NameGraph(){
		this.edges = new HashMap<Integer,Edge>();
		this.vertexes = new HashMap<Integer,VertName>();
	}

	public NameGraph(Map<Integer,Edge> edgs, Map<Integer,VertName> vrtxs){
		this.edges = edgs;
		this.vertexes = vrtxs;
	}

	public Map getEdges() {
		return this.edges;
	}

	public Map<Integer,Edge> getEdges(VertName v) {
		Map<Integer,Edge> retorno = new HashMap<Integer,Edge>();
		for(Map.Entry<Integer, Edge> edg: edges.entrySet()){
			if(edg.getValue().contains(v)){
				retorno.put(edg.getKey(), edg.getValue());
			}
		}

		return retorno;
	}
	public Edge getEdge(VertName v, VertName v2) {
		
		for(Map.Entry<Integer, Edge> aresta: edges.entrySet()){
			if(aresta.getValue().contains(v) && aresta.getValue().contains(v2)){
				return aresta.getValue();
			}
		}
		
		return null;
		
	}

	public Map getVertices() {
		return vertexes;
	}

	public List getVertices(Edge edg) {
		for(Map.Entry<Integer, Edge> edge: edges.entrySet()){
			if(edge.getValue().equals(edg)){
				return edg.getElements();
			}
		}
		return null;
	}

	public boolean isVertex(VertName v) {
		for(Map.Entry<Integer, VertName> vn: this.vertexes.entrySet()){
			if(vn.getValue().getName().equals(v.getName())){
				return true;
			}
		}
		return false;
	}
	public boolean isEdge(Edge edg) {
		for(Map.Entry<Integer, Edge> edge: edges.entrySet()){
			if(edge.getValue().equals(edg)){
				return true;
			}
		}
		return false;
	}
	public boolean existEdge(VertName vrt1, VertName vrt2){
		for(Map.Entry<Integer, Edge> aresta: edges.entrySet()){
			if(aresta.getValue().contains(vrt1) && aresta.getValue().contains(vrt2)){
				return true;
			}
		}
		return false;
	}
	
	public boolean existEdgeValue(VertName vrt1, VertName vrt2){
		for(Map.Entry<Integer, Edge> aresta: edges.entrySet()){
			if(aresta.getValue().contains(vrt1) && aresta.getValue().contains(vrt2)){
				return true;
			}
		}
		return false;
	}

}
