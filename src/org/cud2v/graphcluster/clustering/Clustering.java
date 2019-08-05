package org.cud2v.graphcluster.clustering;

import org.cud2v.graphcluster.block.Block;
import org.cud2v.graphcluster.cluster.Cluster;
import org.cud2v.graphcluster.graph.Edge;
import org.cud2v.graphcluster.graph.NameGraph;
import org.cud2v.graphcluster.graph.VertName;
import org.cud2v.graphcluster.util.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class Clustering {

	private List<Cluster> clusterList;
	private NameGraph grafo;
	private List<Cluster> lg;


	public Clustering(NameGraph grafo){
		this.grafo = grafo;
		this.lg = new ArrayList<Cluster>();
		this.clusterList = new LinkedList<Cluster>();
	}

	public List<Cluster> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<Cluster> clusterList) {
		this.clusterList = clusterList;
	}

	public static int fatorial(int valor){
		int retorno = 1;
		if(valor <= 1){
			retorno = 1;
		}else if(valor > 1){
			retorno = valor * fatorial(valor - 1);
		}

		return retorno;
	}

	/**
	 * Analise combinatoria de N, 2 a 2
	 * @param n
	 * @return retorno
	 */
	public static int combinatoria(int n){

		int retorno = 0;
		if(n==1 || n==2){
			retorno = 1;
		}else{
			retorno = ((n*(n-1))/2);
		}

		return retorno;
	}

	public static float clusterAvgWithoutVert(Cluster cluster, VertName vertice){		
		float retorno = 0;
		for (Map.Entry<Integer, Edge> aresta : cluster.getArestas().entrySet()){
			//		for(Edge aresta:org.cud2v.graphcluster.cluster.getArestas()){
			if(!aresta.getValue().contains(vertice))
				retorno += aresta.getValue().getWeight();
		}
		return (retorno/(cluster.getArestas().size()-1));
	}

	private static int max(int l, int r){
		if(l > r){
			return l;
		}else{
			return r;
		}
	}
	public static float clusterAvg(Cluster cluster){
		float retorno = 0;
		for (Map.Entry<Integer, Edge> aresta : cluster.getArestas().entrySet()){
			//		for(Edge aresta:org.cud2v.graphcluster.cluster.getArestas()){
			retorno += aresta.getValue().getWeight();
		}
		return (retorno/max(1,cluster.getArestas().size()));
	}

	public static float clusterPenalty(Cluster cluster){
		int nos_do_cluster = cluster.getVertices().size();
		int arestas_existentes = cluster.getArestas().size();
		int sefullconnectado = combinatoria(nos_do_cluster);
		System.out.println(sefullconnectado);
		float retorno = (sefullconnectado - arestas_existentes);

		for (Map.Entry<Integer, Edge> aresta : cluster.getArestas().entrySet()){
			//		for (Edge aresta:org.cud2v.graphcluster.cluster.getArestas()) {
			retorno += (1-aresta.getValue().getWeight());
		}

		return retorno;
	}

	public List<VertName> isVertWith(VertName v){
		List<VertName> retorno = new ArrayList<VertName>();
		for(Map.Entry<Integer, Edge> ed : this.grafo.getEdges(v).entrySet()){
			if(v.getId() == ed.getValue().getElemone().getId()){
				retorno.add(ed.getValue().getElemtwo());
			}else if(v.getId() == ed.getValue().getElemtwo().getId()){
				retorno.add(ed.getValue().getElemone());
			}
		}
		return retorno;
	}

	public boolean rNeighbor(Cluster clus1, Cluster clus2){
		for (Map.Entry<Integer, VertName> v : clus1.getVertices().entrySet()){
			for (Map.Entry<Integer, VertName> v2 : clus2.getVertices().entrySet()){
				//		for(VertName v:clus1.getVertices()){
				//			for(VertName v2:clus2.getVertices()){
				if(this.grafo.existEdgeValue(v.getValue(), v2.getValue())){
					return true;
				}
			}
		}
		return false;
	}
	public boolean hasNeighbor(Cluster clus){
		for (Cluster clus2 : this.clusterList){
			for(Map.Entry<Integer, VertName> v : clus.getVertices().entrySet()){
				for (Map.Entry<Integer, VertName> v2 : clus2.getVertices().entrySet()){
					if(this.grafo.existEdgeValue(v.getValue(), v2.getValue())){
						return true;
					}
				}
			}
		}
		return false;
	}

	public Map<Integer,Edge> arestasBtwList(Map<Integer, VertName> conexs){
		List<VertName> copia = new ArrayList<VertName>(conexs.values());
		Map<Integer,Edge> retorno = new HashMap<Integer, Edge>();

		while(copia.size() > 1){
			VertName v = copia.remove(0);
			for(VertName v2 : copia){
				Edge edg = this.grafo.getEdge(v, v2);
				if(edg != null){
					retorno.put(edg.getId(), edg);
				}
			}
		}

		return retorno;
	}

	public void updateClusAvg(Cluster clus, Cluster newClus){
		if(newClus.getArestas().size() > 0)
			newClus.setAvgPenalty(clusterAvg(newClus));
		else
			newClus.setAvgPenalty(0);

		if(clus.getArestas().size() > 0)
			clus.setAvgPenalty(clusterAvg(clus));
		else
			clus.setAvgPenalty(0);

	}

	public Map<Integer,VertName> getVerticesConectados(VertName v, Cluster c){
		Map<Integer,VertName> lista = new HashMap<Integer, VertName>();
		List<VertName> conexo = isVertWith(v);
		lista.put(v.getId(),v);

		while(!conexo.isEmpty()){
			VertName v2 = conexo.remove(0);
			VertName aux = lista.get(v2.getId());
			if(aux == null){
				boolean containVertice = c.getVertices().containsKey(v2.getId());
				if(containVertice){
					lista.put(v2.getId(),v2);
					List<VertName> con = isVertWith(v2);
					conexo.addAll(con);
				}
			}
		}

		return lista;
	}
	public void printClustering(){
		// Print out the final cluster assignment
		for(Cluster c : this.getLg()){
			System.out.println("Cluster " + c.getId_cluster());
			for(VertName v : c.getVertices().values()){
				System.out.println("\t"+v.getName());
			}
		}
	}
	public NameGraph getGrafo() {
		return grafo;
	}

	public void setGrafo(NameGraph grafo) {
		this.grafo = grafo;
	}

	public List<Cluster> getLg() {
		return lg;
	}

	public void setLg(List<Cluster> lg) {
		this.lg = lg;
	}

	/**
	 * A etapa de Split é como descrita:
	 * 1. For each node v of C, evaluate whether splitting v out generates a better org.cud2v.graphcluster.clustering.
	 * 2. Upon finding such a node v, create a new org.cud2v.graphcluster.cluster C' = {v} and conduct steps 3-4.
	 * 3. For each remaining node v' ∈ C evaluate whether moving  v1 to C' obtains a better org.cud2v.graphcluster.clustering.
	 *    If so, move v' to C' repeat Step 3.
	 * 4. Add C and C' to Qc if they are connected to other clusters.
	 * @param clus Cluster a ser dividido
	 * @return retorna true se tiver havido alteração em algum Cluster.
	 **/
	public boolean split(Cluster clus){

		boolean changed = false;
		boolean temVizinho = false;
		List<Cluster> copy = new ArrayList<Cluster>(this.getClusterList());

		List<Cluster> news = new ArrayList<Cluster>();

		HashMap<Float, VertName> verticeBymedia = new HashMap<Float, VertName>();
		List<Float> medias = new ArrayList<Float>();

		if(clus.getArestas().size() > 0){ //se possui uma aresta ou mais, o org.cud2v.graphcluster.cluster pode ser dividido

			VertName aRetirar = null;
			Map<Integer, VertName> conexs = null;//
			for (Map.Entry<Integer, VertName> vert : clus.getVertices().entrySet()){
				//calcula-se o valor da media de similaridade do org.cud2v.graphcluster.cluster sem o vertice que se deseja remover
				float valor = clusterAvgWithoutVert(clus,vert.getValue());
				/*
				 * se o valor da media do org.cud2v.graphcluster.cluster com o vertice for menor do que o valor sem o vertice
				 * é procedido os passos para o split.
				 */
				if(valor > clus.getAvgPenalty()){
					aRetirar = vert.getValue();
					break;
				}
				else{
					//pegamos o subgrafo conectado ao vertice
					conexs = getVerticesConectados(vert.getValue(), clus);
					break;
				}

			}

			if(aRetirar != null){//se temos vertice a ser retirado
				boolean tem = true;
				List<VertName> retirar = new LinkedList<VertName>();
				retirar.add(aRetirar);
				Cluster newClus = new Cluster(aRetirar); //cria-se um novo org.cud2v.graphcluster.cluster
				clus.remove(aRetirar);

				Map<Integer,Edge> edgstToNew = new HashMap<Integer, Edge>();

				//procura e insere vertices que fazem arestas com o vertice aRetirar no novo org.cud2v.graphcluster.cluster
				while(tem){

					VertName tirar = retirar.remove(0);
					List<VertName> vrts = this.isVertWith(tirar);
					for(VertName v : vrts){

						boolean containVertice = clus.getVertices().containsKey(v.getId());
						if(containVertice){

							Edge edg = this.grafo.getEdge(v, tirar);
							/*
							 * adicionamos os vertices que fazem aresta com
							 * o vertice da variavel aRetirar no novo org.cud2v.graphcluster.cluster
							 */
							newClus.add(v);
							retirar.add(v);
							/*
							 *  removemos os vertices adicionados no
							 *  novo org.cud2v.graphcluster.cluster do seu antigo org.cud2v.graphcluster.cluster.
							 *  Ao remover um vertice de um org.cud2v.graphcluster.cluster,
							 *  o metodo retira todas as arestas
							 *  que o vertice removido faz parte.
							 */
							clus.remove(v);
							edgstToNew.put(edg.getId(), edg);

						}
					}
					if(retirar.isEmpty()){
						tem = false;
					}
				}
				/*
				 * arestas que fazem parte do grafo e que o vertice da variavel aRetirar
				 * se encontra, são inseridos no novo org.cud2v.graphcluster.cluster
				 */
				newClus.insertNewEdges(edgstToNew);
				updateClusAvg(clus, newClus);
				temVizinho = this.hasNeighbor(newClus);
				/*
				 * se o novo org.cud2v.graphcluster.cluster formado possui vizinho, ele é colocado na lista
				 * que será iterada para sofrer novas operações. Caso contrário
				 * colocamos na lista de clusters que sera retornada no fim do algoritmo
				 * de agrupamento
				 */
				if(temVizinho){
					news.add(newClus);
				}else{
					this.lg.add(newClus);
				}
				changed = true;
			}

			else if(conexs != null){
				Map<Integer,Edge> arestas = arestasBtwList(conexs);
				Cluster newClus = new Cluster(conexs, arestas);
				clus.removeAll(conexs);

				updateClusAvg(clus, newClus);

				temVizinho = this.hasNeighbor(newClus);
				if(temVizinho){
					news.add(newClus);
				}else{
					this.lg.add(newClus);
				}
				changed = true;

			}

			if(!clus.getVertices().isEmpty()){
				news.add(clus);
			}
		}

		boolean add = copy.addAll(news);
		if(changed)
			this.setClusterList(copy);
		return changed;
	}

	/**
	 * Como definido no artigo do Srivastava:
	 * 1. For each neighbor org.cud2v.graphcluster.cluster C' of C, evaluate whether merging them generates a better org.cud2v.graphcluster.cluster.
	 * 2. Upon finding a better org.cud2v.graphcluster.clustering, (1) merge C with C', (2) add C U C' generates a better org.cud2v.graphcluster.clustering. to Qc,
	 * and (3) remove C' from Qc if C' ∈ Qc.
	 * @param clus Cluster a ser verificado com a lista quais são seus vizinhos
	 * @return retorna true se tiver havido alteração em algum Cluster.
	 **/
	public boolean merge(Cluster clus){
		boolean changed = false;
		// If the cluster is disconnected, we should not merge it. (Just skip immediately)
		if(this.clusterList.size() > 0){

			Map<Integer, VertName> verts;
			Map<Integer, Edge> eds;

			Map<Cluster,Cluster> pai2 = new HashMap<Cluster,Cluster>();
			Map<Float,Cluster> candidatos = new HashMap<Float,Cluster>();
			List<Float> medias = new ArrayList<Float>();

			for (int j = 0; (j < clusterList.size()) ; j++) {

				eds = new HashMap<Integer, Edge>(clus.getArestas());
				verts = new HashMap<Integer, VertName>(clus.getVertices());

				//inicializa o novo org.cud2v.graphcluster.cluster já com todos os vértices e arestas do org.cud2v.graphcluster.cluster 1
				Cluster newClus = new Cluster(verts, eds, clus.getIdKeys());
				Cluster clus2 = this.clusterList.get(j);

				boolean vizinhos = rNeighbor(clus, clus2);
				// se clusters são vizinhos
				if(vizinhos){
					//adiciona ao novo org.cud2v.graphcluster.cluster todos os vértices do org.cud2v.graphcluster.cluster 2
					newClus.InsertVertices(clus2.getVertices());
					Map<Integer,Edge> edgstToNew = new HashMap<Integer, Edge>();

					for (Entry<Integer, VertName> vert : clus2.getVertices().entrySet()){
						for(Entry<Integer, VertName> vertc : newClus.getVertices().entrySet()){
							//se existe aresta entre estes dois vertices, adicionar a lista
							if(this.grafo.existEdge(vertc.getValue(), vert.getValue())){
								Edge edg = this.grafo.getEdge(vertc.getValue(), vert.getValue());
								if(edgstToNew.get(edg.getId()) == null){
									edgstToNew.put(edg.getId(), edg);
								}
							}

						}
					}
					/*
					 * lista de novos vertices, que antes estava fora de ambos os clusters (intracluster)
					 * agora é inserida no novo org.cud2v.graphcluster.cluster, resultado do merge
					 */
					newClus.insertNewEdges(edgstToNew);
					if(newClus.getArestas().size()>0)
						newClus.setAvgPenalty(clusterAvg(newClus));
					else
						newClus.setAvgPenalty(0);

					if(!candidatos.containsKey(newClus.getAvgPenalty())){
						candidatos.put(newClus.getAvgPenalty(), newClus);
						medias.add(newClus.getAvgPenalty());
						pai2.put(newClus, clus2);
					}

				}
			}
			if(!medias.isEmpty()){
				float menor = Collections.max(medias);
				Cluster vencedor = candidatos.get(menor);
				Cluster clu2Rmv = pai2.get(vencedor);
				if((vencedor.getAvgPenalty() > clu2Rmv.getAvgPenalty() || clu2Rmv.getAvgPenalty() == 0)){
					//remover o org.cud2v.graphcluster.cluster c2
					//e adicionar o vencedor
					System.out.println("clusters antes de remover");
					for(int j = 0; j < this.clusterList.size();j++){
						System.out.print(this.clusterList.get(j).getId_cluster()+" ");
					}
					System.out.println();

					this.clusterList.remove(clu2Rmv);
					changed = true;
					System.out.println("removido org.cud2v.graphcluster.cluster de ID: "+clu2Rmv.getId_cluster());
					this.clusterList.add(vencedor);
					System.out.println("adicionando org.cud2v.graphcluster.cluster de ID: "+vencedor.getId_cluster());

					System.out.println("clusters apos adicionar " + vencedor.getId_cluster());
					for(int j = 0; j < this.clusterList.size();j++){
						System.out.print(this.clusterList.get(j).getId_cluster()+" ");
					}
					System.out.println("end");
				}
			}
		}
		return changed;
	}
	/**
	 * 1. For each neighbor org.cud2v.graphcluster.cluster C' of  C, do Steps 2-3.
	 * 2. For each node v' ∈ C that is connected to C' and for each v' ∈ C' connected to C,
	 *    evaluate whether moving v to the other org.cud2v.graphcluster.cluster generates a better org.cud2v.graphcluster.clustering.
	 *    Upon finding such a node v, move it to the other org.cud2v.graphcluster.cluster.
	 * 3. Repeat Step 2 until there is no more node to move. Then, (1) add the two new clusters to Qc,
	 *    and (2) dequeue C' if C'∈Qc.
	 * @param clus Cluster a ser verificado com a lista quais são seus vizinhos.
	 * @return retorna true se tiver havido alteração em algum Cluster.
	 */
	public boolean move(Cluster clus){
        System.out.println("move");
	boolean changed = false;

/*	if(clus.getId_cluster() == 3){
		System.out.println("esse da bronca");
	}*/

	if(this.clusterList.size() > 0){

		Map<Integer, VertName> verts;
		Map<Integer, Edge> eds;
		Map<Integer, VertName> verts2;
		Map<Integer, Edge> eds2;


		Map<Integer,Cluster> pais2 = new HashMap<Integer,Cluster>();
		Map<Float, List<Cluster>> filhos = new HashMap<Float, List<Cluster>>();

		Map<Float,Cluster> candidatos = new HashMap<Float,Cluster>();
		List<Float> medias = new ArrayList<Float>();

		boolean hasChangedc1 = false;

		for (int j = 0; (j < clusterList.size()) ; j++) {
			List<Cluster> sons = new ArrayList<Cluster>();

			eds = new HashMap<Integer, Edge>(clus.getArestas());
			verts = new HashMap<Integer, VertName>(clus.getVertices());
			Cluster newClus1 = new Cluster(verts, eds, clus.getIdKeys());

			Cluster clus2 = this.clusterList.get(j);
			eds2 = new HashMap<Integer, Edge>(this.clusterList.get(j).getArestas());
			verts2 = new HashMap<Integer, VertName>(this.clusterList.get(j).getVertices());
			Cluster newClus2 = new Cluster(verts2, eds2, clus2.getIdKeys());

			if(newClus1.getArestas().size()>0)
				newClus1.setAvgPenalty(clusterAvg(newClus1));
			else
				newClus1.setAvgPenalty(0);

			if(newClus2.getArestas().size()>0)
				newClus2.setAvgPenalty(clusterAvg(newClus2));
			else
				newClus2.setAvgPenalty(0);

			boolean vizinhos = rNeighbor(newClus1, newClus2);
			//			while(rNeighbor(newClus1, newClus2)){
			if(vizinhos){
				boolean atualizou = false;
				List<Edge> edges = getArestasBtwClusters(clus, clus2);
				for(Edge edg : edges){
					VertName v1 = edg.getElemone();
					VertName v2 = edg.getElemtwo();

					if(newClus1.contais(v1) && ((newClus2.getAvgPenalty() == 0) || (edg.getWeight() > newClus1.getAvgPenalty() || edg.getWeight() < newClus2.getAvgPenalty()))){//e ou v1 tem q ser maior que media de newClus1 ou tem q ser menor que media de newClus2
						newClus2.remove(v2);
						newClus1.InsertVertice(v2);
						newClus1.insertNewEdge(edg);
						atualizou = true;
						//adiciona edg ao newCluster2
						//add edg.getElemone() de clus2
						//remove edg.getElemone() de clus1

					}else if(newClus1.contais(v2) && (  (edg.getWeight() < newClus1.getAvgPenalty() || edg.getWeight() > newClus2.getAvgPenalty()))){//e ou v2 tem q ser maior que media de newClus1 ou tem q ser menor que media de newClus2
						newClus1.remove(v2);
						newClus2.InsertVertice(v2);
						newClus2.insertNewEdge(edg);
						atualizou = true;
						//adiciona edg ao newCluster2
						//add edg.getElemtwo() de clus2
						//remove edg.getElemtwo() de clus1

					}

					if(atualizou){
						if(newClus1.getArestas().size()>0)
							newClus1.setAvgPenalty(clusterAvg(newClus1));
						else
							newClus1.setAvgPenalty(0);
						if(newClus2.getArestas().size()>0)
							newClus2.setAvgPenalty(clusterAvg(newClus2));
						else{
							newClus2.setAvgPenalty(0);
						}
						//								atualiza a penalidade dos clusters newClus1, newClus2
					}

				}
				if(atualizou){
					float media = (newClus1.getAvgPenalty()+newClus2.getAvgPenalty())/2;
					medias.add(media);

					sons.add(newClus1);
					sons.add(newClus2);
					filhos.put(media, sons);
					pais2.put(newClus1.getId_cluster(), clus2);//o ID do newClus1, sempre!!!
					// adiciona no map pai2 ambos os clusters
				}
			}


		}
		//depois da papagaiada
		//aqueles que tem a menor média são de fato adicionados no this.clusterList e o seu pai dois sera removido junto com o clus1

		if(!medias.isEmpty()){
			System.out.println("clusters antes ");
			for(int j = 0; j < this.clusterList.size();j++){
				System.out.print(this.clusterList.get(j).getId_cluster()+" ");
			}
			System.out.println();

			//			for(float menor : medias){
			float menor = Collections.max(medias);
			List<Cluster> sons = filhos.get(menor);
			Cluster clus2 = pais2.get(sons.get(0).getId_cluster());

			this.clusterList.remove(clus2);
			if(sons.get(1).getVertices().size()>0){
				this.clusterList.add(sons.get(1));
			}
			if(sons.get(0).getVertices().size()>0){
				this.clusterList.add(sons.get(0));
			}
			changed = true;
			//					apos isso já pode adicionar os novos clusters a lista e deletar os antigos
			//			}
		}
	}
	if(changed){
		System.out.println("clusters depois ");
		for(int j = 0; j < this.clusterList.size();j++){
			System.out.print(this.clusterList.get(j).getId_cluster()+" ");
		}
		System.out.println();
	}
	/*
		Dado um org.cud2v.graphcluster.cluster C pertencente a Qc, consideramos movermos alguns dos nós para outro org.cud2v.graphcluster.cluster
		ou mover nós de outros clusters para C, poderíamos gerar um org.cud2v.graphcluster.cluster melhor. Novamente,
		consideramos nós se movendo entre dois clusters para que o algoritmo termine em tempo
		polinomial. O algoritmo MOVE é descrito:

			1. Para cada vizinho C’ do org.cud2v.graphcluster.cluster C, faça os passos 2-3.
			2. Para cada nó v pertencente a C que é conectado com C’ e
			para cada v pertencente a C’ conectado a C, avalie se movendo v
			para outro org.cud2v.graphcluster.cluster gera um org.cud2v.graphcluster.cluster melhor. Ao encontrar um nó v
			que satisfaça, mova-o para o outro org.cud2v.graphcluster.cluster.
			3. Repita 2 até que não haja mais nós para serem movidos.
			Então (1) adicione os dois novos clusters a fila Qc, e (2) tire
			da fila C’ se C’ estiver na fila.
	 */
	return changed;
	}

	public boolean isEdge(Edge edg, List<Edge> list) {
		for(Edge edge : list){
			if(edge.equals(edg)){
				return true;
			}
		}
		return false;
	}

	public List<Edge> getArestasBtwClusters(Cluster clus1, Cluster clus2){
		List<Edge> retorno = new ArrayList<Edge>();
		for (Map.Entry<Integer, VertName> v : clus1.getVertices().entrySet()){
			//		for(VertName v:clus1.getVertices()){
			for (Map.Entry<Integer, VertName> v2 : clus2.getVertices().entrySet()){
				//			for(VertName v2:clus2.getVertices()){
				if(this.grafo.existEdgeValue(v.getValue(), v2.getValue())){
					Edge ed = this.grafo.getEdge(v.getValue(), v2.getValue());
					if(!isEdge(ed,retorno)){
						retorno.add(ed);
					}
				}
			}
		}
		return retorno;
	}


/*
	public static void main(String[] args) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException, IOException {

		boolean newDatas;

		List<Float> medias = new ArrayList<Float>();
		List<String> lista = new ArrayList<String>();



		//List<List<List<String>>> dadosBDBySchemma = Util.getDataBD();
		//List<List<String>> dadosBD = dadosBDBySchemma.get(0);
		//dadosBD.addAll(dadosBDBySchemma.get(1));

		List<List<String>> dadosBD = Util.getDataFile(new String[]{"schema1.csv","schema2.csv"},";");
		Block bloco = new Block(lista);
		bloco.lista = bloco.genKey(dadosBD);
		//lista vai guardar key+'\t'+id+'\t'+keyCompare onde id eh idBD, key usa tuudo
		//da tupla e keyCompare utiliza apenas author,title,year,class

		NameGraph nome = bloco.getInitialGraph(10);

		Clustering org.cud2v.graphcluster.clustering = new Clustering(nome);

		Map<Integer,VertName> vertices = new HashMap<Integer, VertName>();
		vertices.putAll(org.cud2v.graphcluster.clustering.grafo.getVertices());
		Map<Integer,Edge> arestas = new HashMap<Integer, Edge>();
		arestas.putAll(org.cud2v.graphcluster.clustering.grafo.getEdges());
		//		int i = 1;
		Cluster clus = new Cluster(vertices, arestas, bloco.IdKey);
		org.cud2v.graphcluster.clustering.getClusterList().add(clus);
		clus.setAvgPenalty(org.cud2v.graphcluster.clustering.clusterAvg(clus));
		int iteration = 0;

		while(!org.cud2v.graphcluster.clustering.getClusterList().isEmpty()){
			boolean changed = false;

			Cluster clut = org.cud2v.graphcluster.clustering.getClusterList().remove(0);

			changed = org.cud2v.graphcluster.clustering.merge(clut);
			if(!changed)
				changed = org.cud2v.graphcluster.clustering.split(clut);
			if(!changed)
				changed = org.cud2v.graphcluster.clustering.move(clut);

			if(!changed){
				System.out.println("retirando " + clut.getId_cluster());
				org.cud2v.graphcluster.clustering.lg.add(clut);
			}
		}
		System.out.println(org.cud2v.graphcluster.clustering.getClusterList().size());


		*/
/*
		 * atualizando arquivo .xls com as entrandas e guardando em qual
		 * agrupamento ela se encontra
		 *//*

		Map<String, Integer> mapClus = new HashMap<String, Integer>();
		int idClus = 0;
		for(int i = 0; i < org.cud2v.graphcluster.clustering.lg.size(); i++){
			idClus = org.cud2v.graphcluster.clustering.lg.get(i).getId_cluster();
			for (Map.Entry<Integer, VertName> entry : org.cud2v.graphcluster.clustering.lg.get(i).getVertices().entrySet()){
				mapClus.put(entry.getValue().getIdBD(), idClus);
			}
		}

		Util.updateClusterXls(mapClus);
		Util.updateClusterXls2(mapClus);

		Util.writeGraph(org.cud2v.graphcluster.clustering);
		//apos escrever no arquivo, atualizar no banco
//		Util.updateDatabase(org.cud2v.graphcluster.clustering.grafo);
	}*/
}

