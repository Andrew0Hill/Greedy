package tcc.com.graph;

import org.apache.commons.graph.Vertex;

public class VertName implements Vertex{
	private static int ID = 0;//contador de vertices
	private String name;
	private int id;
	private String idBD;
	private String key;
	private String[] tupla;

	public VertName(String name){
		this.id = ID++; //o mesmo id da tabela!!
		this.name = name;
		this.key = "";
		this.idBD = "";
		this.tupla = null;
	}

	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public void insertKey(String key){
		if(this.key.isEmpty()){
			this.key = key;
		}
	}
	public void insertIdBD(String id){
		if(this.idBD.isEmpty()){
			this.idBD = id;
		}
	}
	public void setKey(String key){
		this.key = key;
	}
	public String getKey(){
		return this.key;
	}
	public String getIdBD(){
		return this.idBD;
	}

	public String getTupla() {
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
	}
	

}
