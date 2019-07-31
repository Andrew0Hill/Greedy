package org.cud2v.graphcluster.block;

import org.cud2v.graphcluster.cluster.Cluster;
import org.cud2v.graphcluster.clustering.Clustering;
import org.cud2v.graphcluster.graph.Edge;
import org.cud2v.graphcluster.graph.NameGraph;
import org.cud2v.graphcluster.graph.VertName;
import org.cud2v.graphcluster.similarity.Similarity;

import java.util.*;

public class Block{

	public static List<List<String>> lista;
	private static List<String> simlist;
	//tem o id automatico do VertName <tab> ID da tabela <tab> chave gerada
	public static List<String> IdKey = new ArrayList<String>();
	//mapkey � o map que guarda a quantidade de keys que tiveram merge;
	static Map<String, Integer> mapkey;
	//mapvalue � o map que guarda key e value
	static Map<String, Object> mapvalue;

	//FAZER UM MAP CHAVE - TUPLA
	static Map<String, List<String>> keytuple;


	/**
	 * Gera a key para cada linha de entrada (no momento removendo espa�os em branco que separam primeiro nome e sobrenomes) Ex: Aracelly Maria Guerra Azevedo se torna aracellymariaguerraazevedo.
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
/*	public static List<String> genKey(List<List<String>> lista){

		List<String> list = new ArrayList<String>();
		mapkey = new HashMap<String, Integer>();
		mapvalue = new HashMap<String, Object>();
		keytuple = new HashMap<String, List<String>>();
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

			key = Normalizer.normalize(key, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
			keyCompare = Normalizer.normalize(keyCompare, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
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
	}*/

	// Generates a "key" which is used to compare the org.cud2v.graphcluster.similarity of the records.
/*	public static List<String> genKeyObs(List<List<String>> list){
		keytuple = new HashMap<String, String[]>();

		ArrayList<String> keys = new ArrayList<>();
		StringBuilder bl = new StringBuilder();
        for (List<String> row : list){

        	String id = row.get(0);
        	String cmp_key = row.get(1);
			if (cmp_key.equals("")){
				cmp_key = "placeholder";
			}
			bl.append(cmp_key);
			bl.append("\t");
			bl.append(id);
			bl.append("\t");
			bl.append(cmp_key);

			// Add our string to the list we're returning.
        	keys.add(bl.toString());
        	// Reset the StringBuilder at the end of the interation.
        	bl.setLength(0);
        	keytuple.put(cmp_key,toArray(row));
		};
        return keys;
    }*/

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

	public static double similarity(List<String> left, List<String> right){
		// Fields [2,5] contain the values we want to compare, use the org.cud2v.graphcluster.similarity metric from the paper.
		int num_match = 0;
		for(int col = 2; col <= 6; ++col) {
			if (left.get(col).toLowerCase().equals(right.get(col).toLowerCase())) {
				num_match += 1;
			}
		}
		double score = 0.0;
		switch(num_match){
			case 3:
				score = 0.8;
				break;
			case 4:
				score = 0.9;
				break;
			case 5:
				score = 1.0;
				break;
			default:
				score = 0.0;
				break;
		}
		return score;
	}

	public static NameGraph getInitialGraphObs(){
		Map<Integer,Edge> edges = new HashMap<Integer,Edge>();
		Map<Integer,VertName> vertices = new HashMap<Integer,VertName>();
		simlist = new ArrayList<String>();

		NameGraph graph = new NameGraph();
		VertName elem1;
		VertName elem2;
		// Iterate the rows
		for(int i = 0; i < lista.size(); ++i) {
			// Hardcode the location of the ID string.
			String elem1_name = lista.get(i).get(1);
			elem1 = new VertName(elem1_name);
			elem1.insertIdBD(elem1_name);
			// Similarities should be symmetric so only need to calculate half of the
			// pairs. We also don't need to calculate self-similarities.
			for (int j = i+1; j < lista.size(); ++i) {

			}
		}
		return graph;
	}

	/*
	Gets the connected component
	 */

	public static NameGraph getConnectedComponentClusters(Clustering current_clust, List<List<String>> increment){
		NameGraph orig_graph = current_clust.getGrafo();
		Map<Integer,VertName> existing_verts = orig_graph.getVertices();
		Map<Integer,Edge> existing_edges = orig_graph.getEdges();

		// Data structures to hold the new vertices and edges.
		Map<Integer,VertName> new_vertices = new HashMap<>();
		Map<Integer,Edge> new_edges = new HashMap<>();

		// Holds the set of connected clusters to run the clustering on, which includes the
		// new singleton clusters as well as any directly connected clusters.
		HashSet<Cluster> connected_clusts = new HashSet<>();

		//ArrayList<VertName> new_verts = new ArrayList<>();
		// Parse all incremental vertices from string.
		for(List<String> row : increment){
			String name = row.get(1);
			VertName v = new VertName(name);
			v.insertIdBD(name);
			v.setData(row);
			new_vertices.put(v.getId(),v);
		}
		// Iterate each of the new vertices.
		// We need to calculate the similarities between both the existing vertices, and the new set.
		ArrayList<VertName> new_vert_list = new ArrayList<>(new_vertices.values());
		for(int i = 0; i < new_vert_list.size(); ++i){
			// Get reference to the current vertex
			VertName cur_v = new_vert_list.get(i);
			// Make a singleton cluster for the new vertex.
			Cluster cur_clust = new Cluster(cur_v);
			connected_clusts.add(cur_clust);
			// Iterate all of the existing clusters to find the connected component clusters for this vertex.
			for(Cluster clust : current_clust.getLg()){
				for(VertName clust_vert : clust.getVertices().values()) {
					double siml = similarity(cur_v.getData(), clust_vert.getData());
					// If similarity is above threshold, make a new edge.
					if (siml > 0.75) {
						// Make a new edge in the graph.
						Edge<Object> new_edge = new Edge<>(clust_vert, cur_v, siml);
						new_edges.put(new_edge.getId(), new_edge);
						cur_clust.getArestas().put(new_edge.getId(), new_edge);
						connected_clusts.add(clust);
					}
				}
			}
			// Iterate all of the other new vertices being added in this increment to calculate the similarities
			for(int j = i+1; j < new_vert_list.size(); ++j){
				VertName nb_v = new_vert_list.get(j);
				double siml = similarity(nb_v.getData(),cur_v.getData());
				if(siml > 0.75 && !existEdge(new_edges,nb_v,cur_v)){
					Edge<Object> new_edge = new Edge<>(nb_v,cur_v,siml);
					new_edges.put(new_edge.getId(),new_edge);
					cur_clust.getArestas().put(new_edge.getId(),new_edge);
				}
			}
		}
/*		// Iterate the new vertices and for each one, compute the similarity between that vertex and all vertices in
		// the existing graph.
		for(List<String> row : increment){
			// Get the ID for this row, and make a new vertex for it.
			String name = row.get(1);
			VertName elem = new VertName(name);
			elem.insertIdBD(name);
			elem.setData(row);
			new_vertices.put(elem.getId(),elem);

			// Create a new singleton Cluster to hold the new vertex.
			Cluster new_clust = new Cluster(elem);
			// Add this cluster to the clustering list.
			connected_clusts.add(new_clust);
			//current_clust.getClusterList().add(new_clust);

			// Iterate the existing clustering to calculate the similarity between the new node and each of the
			// elements of the existing clusters.
			for(Cluster clust : current_clust.getLg()){
				for(VertName vert : clust.getRecords().values()){
					double siml = similarity(elem.getData(),vert.getData());
					// If similarity is above threshold, make a new edge.
					if(siml > 0.75){
						// Make a new edge in the graph.
						Edge<Object> new_edge = new Edge<>(elem,vert,siml);
						new_edges.put(new_edge.getId(),new_edge);
						new_clust.getArestas().put(new_edge.getId(),new_edge);
						connected_clusts.add(clust);
						//current_clust.getLg().remove(clust);
						//current_clust.getClusterList().add(clust);
					}
				}
			}

			// Iterate each cluster to compute the similarity between the new elements
			for(int i = 0; i < )
			new_clust.setAvgPenalty(current_clust.clusterAvg(new_clust));
		}*/

		for(Cluster c : connected_clusts){
			current_clust.getLg().remove(c);
			current_clust.getClusterList().add(c);
		}

		existing_verts.putAll(new_vertices);
		existing_edges.putAll(new_edges);

		NameGraph updated_graph = new NameGraph(existing_edges,existing_verts);
		// Update the graph.
		current_clust.setGrafo(updated_graph);

		while (!current_clust.getClusterList().isEmpty()) {
			boolean changed = false;

			Cluster clut = current_clust.getClusterList().remove(0);

			changed = current_clust.merge(clut);
			if (!changed)
				changed = current_clust.split(clut);
			if (!changed)
				changed = current_clust.move(clut);

			if (!changed) {
				System.out.println("Removed " + clut.getId_cluster());
				current_clust.getLg().add(clut);
			}
		}

		return updated_graph;
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
		double valo;
		String add;
		int aux;
		int last = 0;


		for(int i = 0; i<lista.size();i++){
			add = "";
			aux = window;
			String nome = lista.get(i).get(1);
			String idBD = lista.get(i).get(1);
			List<List<String>> list = lista;

			elem1 = new VertName(nome);
			elem1.insertIdBD(idBD);
			elem1.setData(lista.get(i));
			//elem1.setTupla(keytuple.get(nome));

			VertName alrExist = existVert(vertices, elem1);

			if(alrExist == null){
				vertices.put(elem1.getId(), elem1);
				//System.out.println("adicionado em 1 - "+ elem1.getIdBD() + " " + elem1.getId());
			}else{
				elem1 = alrExist;
			}

			last = 0;
			for(int j = i+1; j < lista.size() && aux > 0;j++){

				if(j > i && aux > 0){
					aresta = null;
					valo = 0;
/*					if(elem1.getId() == 11){
						System.out.println("lina 440 Block bug");
					}*/

					String nome2 = lista.get(j).get(1);
					String idBD2 = lista.get(j).get(1);

					elem2 = new VertName(nome2);
					elem2.insertIdBD(idBD2);
					elem2.setData(lista.get(j));
					//elem2.setTupla(keytuple.get(nome2));

					//					SmithWaterman sw = new SmithWaterman(elem1.getData(), elem2.getData());
					//					valo = sw.getSimSW();

					//valo = Similarity.getFedit(elem1.getData(), elem2.getData());
					try {
						//valo = (float) (dice_dist.process(elem1.getData(), elem2.getData())/100);
						valo = similarity(elem1.getData(),elem2.getData());
					}catch(Exception e){e.printStackTrace();}


					alrExist = existVert(vertices, elem2);
					if(alrExist == null){
						//System.out.println("adicionado em 2 - "+ elem2.getIdBD() + " " + elem2.getId());
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

/*
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
				System.out.print(vertice.getData()+" ");
			}
			System.out.println("de peso " + aresta.getWeight());
		}

		System.out.println(nome.getVertices().size() + " vertices");
		//		System.out.println(bloco.lista.size());
		for (Entry<Integer, VertName> entry : vertices.entrySet()){
			System.out.println(entry.getValue().getData() + '\t' + entry.getValue().getIdBD());
		}
		
	}
*/

}
