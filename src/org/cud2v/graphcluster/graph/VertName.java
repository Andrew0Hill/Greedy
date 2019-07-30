package org.cud2v.graphcluster.graph;

import org.apache.commons.graph.Vertex;

import java.util.List;

public class VertName implements Vertex{
	private static int ID = 0;//contador de vertices
	private String name;
	private int id;
	private String idBD;
	private List<String> data;
	//private String[] tupla;

	public VertName(String name){
		this.id = ID++; //o mesmo id da tabela!!
		this.name = name;
		this.data = null;
		this.idBD = "";
		//this.tupla = null;
	}

	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public void insertData(List<String> data){
		if(this.data == null){
			this.data = data;
		}
	}
	public void insertIdBD(String id){
		if(this.idBD.isEmpty()){
			this.idBD = id;
		}
	}
	public void setData(List<String> data){
		this.data = data;
	}
	public List<String> getData(){
		return this.data;
	}

	public String getIdBD(){
		return this.idBD;
	}

/*	public String getTupla() {
		String retorno = "";
		for(int i = 2; i < tupla.length; i++){
			if(this.tupla[i].isEmpty() || this.tupla[i]==null){
				retorno += " ";
			}else{	
				retorno += this.tupla[i] + " ";
			}
		}
		return retorno;
	}

	public void setTupla(String[] strings) {
		this.tupla = strings;
	}*/
	

}
