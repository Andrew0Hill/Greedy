package tcc.com.block;

import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tcc.com.graph.Edge;
import tcc.com.graph.NameGraph;
import tcc.com.graph.VertName;
import tcc.com.similarity.Similarity;
import tcc.com.similarity.SmithWaterman;
import tcc.com.util.Util;

public class Block{

	public static List<?> lista;
	private static List<String> simlist; 
	//tem o id automatico do VertName <tab> ID da tabela <tab> chave gerada
	public static List<String> IdKey = new ArrayList<String>();
	//mapkey é o map que guarda a quantidade de keys que tiveram merge;
	static Map<String,Integer> mapkey;
	//mapvalue é o map que guarda key e value
	static Map<String, Object> mapvalue;

	//FAZER UM MAP CHAVE - TUPLA
	static Map<String, String[]> keytuple;	

	public Block (List<?> lista){
		this.lista = lista;		
	}

	/**
	 * Gera a key para cada linha de entrada (no momento removendo espaços em branco que separam primeiro nome e sobrenomes) Ex: Aracelly Maria Guerra Azevedo se torna aracellymariaguerraazevedo.
	 * retorna a lista de keys ordenada, sem acentos e com caracteres em lower case.
	 * **/
	/*
	public static List<String> genKey(List<?> lista){

		List<String> list = new ArrayList<String>();
		mapkey = new HashMap<String,Integer>();
		mapvalue = new HashMap<String, Object>();

		for(Object obj : lista){
			String key = obj.toString().replace(" ", "");			
			key = Normalizer.normalize(key,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
			//			System.out.println(key);
			if(mapkey.containsKey(key)){
				mapkey.put(key, mapkey.get(key) + 1);
				mapvalue.put(key, mapvalue.get(key) +","+ obj.toString());
				//				list.add(key);
			}else{
				mapkey.put(key, 1);
				mapvalue.put(key, obj.toString());
			}
			list.add(key);
		}
		Collections.sort(list);
		return list;	
	}
	 */
	//	2author;3volume;4title;5institution;6venue;7address;8publisher;9year;10pages;11editor;12note;13month;14class
	public static List<String> genKey(List<List<String>> lista){

		List<String> list = new ArrayList<String>();
		mapkey = new HashMap<String,Integer>();
		mapvalue = new HashMap<String, Object>();
		keytuple = new HashMap<String, String[]>();
		String key = "";
		String id = "";
		String keyCompare = "";

		for(List<String> obj : lista){
			id="";
			key="";
			keyCompare = "";
			for(int j = 0; j < obj.size(); j++){
				String add = "";
				if(j==1){
					id = obj.get(j);
				}
				if(j==2){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("and", "").replace("(", "")
								.replace(")", "").replace("`","").replace("\\", "").replace("//", "").replace(":", "")
								.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("<", "")
								.replace(">", "").replace("/", "");
						String[] substr = add.toString().split(" ");
						for(int i = 0; i<substr.length; i++){
							if(substr[i].length() > 3){
								key = key.concat(substr[i].substring(0, 4));
								keyCompare = keyCompare.concat(substr[i].substring(0, 3));
							}
							else{
								if(!substr[i].equals("the") && !substr[i].equals("and") 
										&& !substr[i].equals("an") && !substr[i].equals("or")){
									keyCompare = keyCompare.concat(substr[i]);
									key = key.concat(substr[i]);
								}
							}
						}					
					}
				}else if (j==4){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("and", "").replace("(", "")
								.replace(")", "").replace("`","").replace("\\", "").replace("//", "").replace(":", "")
								.replace("[", "").replace("]", "").replace("!", "").replace("?", "").replace("<", "")
								.replace(">", "").replace("/", "");
						String[] substr = add.toString().split(" ");
						for(int i = 0; i<substr.length; i++){
							if(substr[i].length() > 3){
								key = key.concat(substr[i].substring(0, 2));
								keyCompare = keyCompare.concat(substr[i].substring(0, 3));
							}else if(substr[i].length() > 1){
								key = key.concat(substr[i].substring(0, 2));
								
								if(!substr[i].equals("the") && !substr[i].equals("and") 
										&& !substr[i].equals("an") && !substr[i].equals("or")){
									keyCompare = keyCompare.concat(substr[i].substring(0, 2));
								}
							}
						}					
					}
				}
				else if(j==5 || j==6 || j==7 || j==8 || j==11 || j==12){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("and", "").replace("(", "")
								.replace(")", "").replace("`","").replace("\\", "").replace("//", "").replace(":", "")
								.replace("[", "").replace("]", "").replace("!", "").replace("?", "");
						String[] substr = add.toString().split(" ");
						for(int i = 0; i < substr.length; i++){
							if(i == 0){
								if(substr[i].length() >= 3){
									key = key.concat(substr[i].substring(0, 1));
								}else{
									key = key.concat(substr[i]);
								}
							}
						}					
					}
				}
				else if(j==3){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("-"," ").replace("\\", "").replace("(", "")
								.replace(")", "").replace("//", "").replace(":", "").replace("[", "").replace("]", "")
								.replace("!", "").replace("?", "").replace("-", "");
						String[] substr = add.toString().split(" ");
						for(int i = 0; i<substr.length; i++){
							if(substr[i].length() >= 2 && i > 0){
								key = key.concat(substr[i].substring(0, 2));
							}
							//							else{key = key.concat(substr[i]);}
						}					
					}
				}
				else if(j==9){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("(", "")
								.replace(")", "").replace("-", "").replace(" ", "").replace("\\", "")
								.replace("//", "").replace(":", "").replace("[", "").replace("]", "")
								.replace("!", "").replace("?", "").replace("\t", "");
						if(add.length() > 4){
							key = key.concat(add.substring(2, 4));
//							keyCompare = keyCompare.concat(add.substring(2, 4));
						}else if(add.length() > 1){
							key = key.concat(add);
//							keyCompare = keyCompare.concat(add);
						}
					}
				}
				else if(j==13){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace(".", "").replace(",", "").replace("-","").replace("(", "")
								.replace(")", "").replace("\\", "").replace("//", "").replace(":", "").replace("[", "")
								.replace("]", "").replace("!", "").replace("?", "").replace("-", "").replace(" ", "").replace(" ", "");
						if(add.length()>3){
							key = key.concat(add.substring(0, 3));
						}else{key = key.concat(add);}
					}
				}
				else if( j==10 || j ==14){
					if(!obj.get(j).isEmpty()){
						add = obj.get(j).replace("\t", "").replace(".", "").replace(",", "").replace("(", "")
								.replace(")", "").replace("-", "").replace("\\", "")
								.replace("//", "").replace(":", "").replace("[", "").replace("]", "")
								.replace("!", "").replace("?", "").replace("<", "")
								.replace(">", "").replace("/", "").replace(" ", "");

						key=key.concat(add);
						if(j == 14){
							String[] arr = add.split(" ");
							if(arr.length > 1)
								keyCompare = keyCompare.concat(arr[0]);
							else
								keyCompare = keyCompare.concat(add);
						}
					}
				}
			}	

			key = Normalizer.normalize(key,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
			keyCompare = Normalizer.normalize(keyCompare,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
			String[] value = toArray(obj);
			System.out.println(value[1]);
			keytuple.put(key, value);
			//			System.out.println(key);
			//			if(mapkey.containsKey(key)){
			//				mapkey.put(key, mapkey.get(key) + 1);
			//				mapvalue.put(key, mapvalue.get(key) +","+ obj.toString());
			//
			//			}else{
			mapkey.put(key, 1);
			mapvalue.put(key, obj.toString());
			list.add(key+'\t'+id+'\t'+keyCompare);	
			//			}

			IdKey.add(id+'\t'+key);

		}

		Collections.sort(list);
		return list;	
	}

	public static String[] toArray(List<String> obj){
		String[] retorno = new String[obj.size()];
		for(int i = 0; i < obj.size(); i++){
			retorno[i] = obj.get(i);
		}
		return retorno;
	}

	public static boolean contains(Edge edg, VertName v){
		if(v.getId() == edg.getElemone().getId() || v.getId() == edg.getElemtwo().getId() ){
			return true;
		}
		return false;
	}
	public static boolean existEdge(Map<Integer,Edge> edgs, VertName vrt1, VertName vrt2){
		for(Map.Entry<Integer, Edge> aresta: edgs.entrySet()){
			if(contains(aresta.getValue(), vrt1) && contains(aresta.getValue(), vrt2)){
				return true;
			}
		}
		return false;
	}

	public static VertName existVert(Map<Integer,VertName> vrts, VertName vrt1){
		for(Map.Entry<Integer, VertName> v: vrts.entrySet()){
			if(v.getValue().getIdBD().equals(vrt1.getIdBD())){
				return v.getValue();
			}
		}
		return null;
	}


	//simlist contem valores de similaridade dos blocos que sao sobrepostos
	public static NameGraph getInitialGraph(int window){
		Map<Integer,Edge> arestas = new HashMap<Integer,Edge>();
		Map<Integer,VertName> vertices = new HashMap<Integer,VertName>();
		simlist = new ArrayList<String>();
		NameGraph retorno;
		VertName elem1;
		VertName elem2;
		Edge aresta;
		float valo;
		String add;
		int aux;
		int last = 0;


		for(int i = 0; i<lista.size();i++){
			add = "";
			aux = window;
			String nome = lista.get(i).toString().split("\t")[0];
			String idBD = lista.get(i).toString().split("\t")[1];
			String keyCompare = lista.get(i).toString().split("\t")[2];
			List<String> list = (List<String>) lista;

			elem1 = new VertName(nome);
			elem1.insertIdBD(idBD);
			elem1.setKey(keyCompare);
			elem1.setTupla(keytuple.get(nome));

			VertName alrExist = existVert(vertices, elem1);

			if(alrExist == null){
				vertices.put(elem1.getId(), elem1);
				System.out.println("adicionado em 1 - "+ elem1.getIdBD() + " " + elem1.getId());
			}else{
				elem1 = alrExist;
			}

			last = 0;
			for(int j = i+1; j < lista.size() && aux > 0;j++){

				if(j > i && aux > 0){
					aresta = null;
					valo = 0;
					if(elem1.getId() == 11){
						System.out.println("lina 440 Block bug");
					}

					String nome2 = lista.get(j).toString().split("\t")[0];
					String idBD2 = lista.get(j).toString().split("\t")[1];
					String key2 = lista.get(j).toString().split("\t")[2];

					elem2 = new VertName(nome2);
					elem2.insertIdBD(idBD2);
					elem2.setKey(key2);
					elem2.setTupla(keytuple.get(nome2));

					//					SmithWaterman sw = new SmithWaterman(elem1.getKey(), elem2.getKey());
					//					valo = sw.getSimSW();

					valo = Similarity.getFedit(elem1.getKey(), elem2.getKey());

					alrExist = existVert(vertices, elem2);
					if(alrExist == null){
						System.out.println("adicionado em 2 - "+ elem2.getIdBD() + " " + elem2.getId());				
						vertices.put(elem2.getId(), elem2);
					}else{
						elem2 = alrExist;
					}

					if(valo > 0.75){
						if(!existEdge(arestas, elem1, elem2)){
							aresta = new Edge<Object>(elem1,elem2,valo);
							arestas.put(aresta.getId(),aresta);
							last++;
						}
						if(!add.isEmpty()){add=add.concat(";");}
						add = add.concat(""+valo);
					}					
					aux--;
				}
			}
			simlist.add(add);
		}
		retorno = new NameGraph(arestas, vertices);	
		return retorno;
	}

	public static void getSimonList(){
		float valo;
		String add;
		//		int aux;
		simlist = new ArrayList<String>();

		for(int i = 0; i<lista.size();i++){
			add = "";


			for(int j = 0; j<lista.size();j++){

				if(j > i){
					if(!add.isEmpty()){add=add.concat(",");}
					valo = Similarity.getFedit(lista.get(i).toString(), lista.get(j).toString());
					add = add.concat(""+valo);


					//					if((i+1 == lista.size()) && (lista.size()%2==1) ){
					//						add = Similarity.getFedit(lista.get(i).toString(), lista.get(j).toString());
					//					}else{add = Similarity.getFedit(lista.get(i).toString(), lista.get(j).toString());}
				}

			}
			simlist.add(add);
			//			simlist.add(add);
		}
	}

	public static void getSimonList(int window){
		float valo;
		String add;
		int aux;
		simlist = new ArrayList<String>();

		for(int i = 0; i<lista.size();i++){
			add = "";
			aux = window;

			for(int j = 0; j < lista.size() && aux > 0;j++){

				if(j > i && aux > 0){
					if(!add.isEmpty()){add=add.concat(",");}
					valo = Similarity.getFedit(lista.get(i).toString(), lista.get(j).toString());
					add = add.concat(""+valo);
					aux--;

				}

			}
			simlist.add(add);
			//			simlist.add(add);
		}
	}

	public static String getKeybyIdBD(String key){
		String retorno = "";
		for(String linha:IdKey){
			String[] id_key = linha.split("\\t");
			if(key.equals(id_key[1])){
				if(!retorno.isEmpty())
					retorno = retorno.concat(",");
				retorno = retorno.concat(id_key[0]);
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		List<List<List<String>>> dadosBDBySchemma = Util.getDataBD();
		List<List<String>> dadosBD = dadosBDBySchemma.get(0);
		dadosBD.addAll(dadosBDBySchemma.get(1));

		Block bloco = new Block(lista);
		bloco.lista = genKey(dadosBD);

		NameGraph nome = getInitialGraph(10);
		Map<Integer,VertName> vertices = nome.getVertices();
		Map<Integer,Edge> arestas = nome.getEdges();
		int i = 1;

		//codigo pra mostrar os vertices
		for (Map.Entry<Integer, Edge> entry : arestas.entrySet()){
			Edge aresta = entry.getValue();
			List<VertName> vertexes = aresta.getElements();
			System.out.print("aresta "+ i++ +": ");
			for (VertName vertice:vertexes) {
				System.out.print(vertice.getKey()+" ");
			}
			System.out.println("de peso " + aresta.getWeight());
		}

		System.out.println(nome.getVertices().size() + " vertices");
		//		System.out.println(bloco.lista.size());
		for (Entry<Integer, VertName> entry : vertices.entrySet()){
			System.out.println(entry.getValue().getKey() + '\t' + entry.getValue().getIdBD()); 
		}
		
	}

}
