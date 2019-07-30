package org.cud2v.graphcluster.cluster;


import org.cud2v.graphcluster.graph.Edge;
import org.cud2v.graphcluster.graph.VertName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Cluster {

	private static int ID = 0;
	private Map<Integer,VertName> records;
	private Map<Integer,Edge> arestas;
	int id_cluster;
	List<String> idKeys;
	String idsBd;
	float avgPenalty;

	//idKeys � id<tab>chave
	public Cluster(Map<Integer,VertName> records, Map<Integer,Edge> arestas, List<String> idKeys){
		this.records = records;
		this.arestas = arestas;
		this.idKeys = idKeys;
		this.id_cluster = ID++;
		this.avgPenalty = 0;
	}
	public Cluster(VertName record){
		id_cluster = ID++;
		this.records = new HashMap<Integer,VertName>();
		this.records.put(record.getId(),record);
		this.arestas = new HashMap<Integer,Edge>();
		this.avgPenalty = 0;
	}
	public Cluster(Map<Integer,VertName> records, Map<Integer,Edge> arestas){
		this.records = records;
		this.arestas = arestas;
		this.id_cluster = ID++;
		this.avgPenalty = 0;
	}

	public Map<Integer, VertName> getRecords() {
		return records;
	}
	public Edge[] toArray(){
		Edge[] retorno = new Edge[this.arestas.size()];
		//TEM QUE ITERAR
		int i=0;
		for(Map.Entry<Integer, Edge> aresta: this.arestas.entrySet()){
			retorno[i++] = aresta.getValue();
		}

		return retorno;
	}
	public boolean removeAll(Map<Integer, VertName> verts){
		boolean retorno = false;
		boolean aux = true;
		for(Map.Entry<Integer, VertName> v : verts.entrySet()){
			retorno = this.remove(v.getValue());
			if(!retorno){
				aux = false;
			}
		}
		return aux && retorno;
	}

	public boolean remove(VertName record){
		Edge[] arestasBackup = toArray();
		VertName removeu1 = this.records.remove(record.getId());

		Edge removeu2 = null;
		boolean retorno = false;

		if(removeu1 != null){
			for(int i = 0; i < arestasBackup.length ; i++){
				Edge ar = arestasBackup[i];
				if(ar.contains(record)){
					removeu2 = this.arestas.remove(ar.getId());
				}
			}
		}

		if(removeu1!=null && removeu2!= null){ retorno = true; }


		return retorno;

	}
	public boolean add(VertName record){
		boolean retorno = false;
		if(!this.records.containsValue(record)){
			this.records.put(record.getId(),record);

			//tem q calcular a similaridade pra ver quais vao ser as arestas


		}

		return false;
	}
	public boolean insertNewEdges(Map<Integer,Edge> arestasToInsert){//ACHO QUE TA ERRADO, ACHO QUE AQUI DEVE SER APENAS SE H� VERTICE QUE TEM
		//ARESTAS COM ELE, SE TIVER, ADICIONAR AQUI!!
		float valor = 0;
		boolean retorno = false;
		boolean inserir;
		for (Entry<Integer, Edge> aresta : arestasToInsert.entrySet()){
			inserir = true;
			Edge ed = arestas.get(aresta.getKey());

			if(ed != null){
				inserir = false;
			}

			if(inserir){
				this.arestas.put(aresta.getValue().getId(),aresta.getValue());
				retorno = true;
			}
		}
		return retorno;
	}

	public boolean insertNewEdge(Edge arestaToInsert){//ACHO QUE TA ERRADO, ACHO QUE AQUI DEVE SER APENAS SE H� VERTICE QUE TEM
		//ARESTAS COM ELE, SE TIVER, ADICIONAR AQUI!!
		float valor = 0;
		boolean retorno = false;
		boolean inserir;

		inserir = true;
		Edge ed = this.arestas.get(arestaToInsert.getId());

		if(ed != null){
			inserir = false;
		}

		if(inserir){
			this.arestas.put(arestaToInsert.getId(),arestaToInsert);
			retorno = true;
		}

		return retorno;
	}

	public void InsertVertices(Map<Integer,VertName> vertices){
		for (Map.Entry<Integer, VertName> v : vertices.entrySet()){
			//		for(VertName v:vertices){
			if(!contais(v.getValue())){
				this.records.put(v.getValue().getId(),v.getValue());
			}
		}
	}

	public void InsertVertice(VertName vertice){
		if(!contais(vertice)){
			this.records.put(vertice.getId(),vertice);
		}

	}

	public boolean contais(VertName v){
		for (Map.Entry<Integer, Edge> edg : arestas.entrySet()){
			//		for(Edge edg:arestas
			if(edg.getValue().contains(v)){
				return true;
			}
		}
		return false;

	}
	public Map<Integer,VertName> getVertices(){
		return this.records;
	}
	public Map<Integer,Edge> getArestas(){
		return this.arestas;
	}
	public void addidsBd(String id){
		idsBd+=id;
	}
	public float getAvgPenalty() {
		return avgPenalty;
	}
	public void setAvgPenalty(float avgPenalty) {
		this.avgPenalty = avgPenalty;
	}
	public List<String> getIdKeys() {
		return idKeys;
	}
	public void setIdKeys(List<String> idKeys) {
		this.idKeys = idKeys;
	}
	public int getId_cluster() {
		return this.id_cluster;
	}
	public void setId_cluster(int id_cluster) {
		this.id_cluster = id_cluster;
	}


}
