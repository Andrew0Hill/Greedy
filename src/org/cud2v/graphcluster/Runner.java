package org.cud2v.graphcluster;

import org.cud2v.graphcluster.block.Block;
import org.cud2v.graphcluster.cluster.Cluster;
import org.cud2v.graphcluster.clustering.Clustering;
import org.cud2v.graphcluster.graph.Edge;
import org.cud2v.graphcluster.graph.NameGraph;
import org.cud2v.graphcluster.graph.VertName;
import org.cud2v.graphcluster.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {
    public static void main(String[] args) throws Exception {

        // Read in the file here.
        String separator = ",";
        List<List<String>> data;
        data = Util.getDataFile(new String[]{"starbucks_initial.csv"}, separator).get(0);

        Block block = new Block();
        block.lista = data;
        //block.lista = block.genKeyObs(this.data)


        NameGraph nome = block.getInitialGraph(10);

        Map<Integer, VertName> vertices = new HashMap<Integer, VertName>();

        Clustering clustering = new Clustering(nome);

        vertices.putAll(clustering.getGrafo().getVertices());
        Map<Integer, Edge> arestas = new HashMap<Integer, Edge>();
        arestas.putAll(clustering.getGrafo().getEdges());
        //		int i = 1;
        Cluster clus = new Cluster(vertices, arestas, block.IdKey);
        clustering.getClusterList().add(clus);
        clus.setAvgPenalty(clustering.clusterAvg(clus));

        while (!clustering.getClusterList().isEmpty()) {
            boolean changed = false;

            Cluster clut = clustering.getClusterList().remove(0);

            changed = clustering.merge(clut);
            if (!changed)
                changed = clustering.split(clut);
            if (!changed)
                changed = clustering.move(clut);

            if (!changed) {
                System.out.println("Removed " + clut.getId_cluster());
                clustering.getLg().add(clut);
            }
        }
        System.out.println("Initial Clustering Finished");

        // Now we add the incremental clustering data.
        String[] inc_files = {"starbucks/sbc_inc_1.csv", "starbucks/sbc_inc_2.csv", "starbucks/sbc_inc_3.csv", "starbucks/sbc_inc_4.csv"};
        List<List<List<String>>> increment_data = Util.getDataFile(inc_files, ",");

        for (List<List<String>> increment : increment_data) {

            System.out.println("Increment has " + increment.size() + " rows.");

            // Run incremental update clustering on this increment.

            // 1. Get connected component for update.
            // First calculate similarity between all existing nodes and the new nodes.
            NameGraph orig_graph = clustering.getGrafo();
            Block.getConnectedComponentClusters(clustering,increment);
            // 2. Get clusters that participate in the connected components

            // 3. Run clustering on each cluster, along with the singleton clusters from the new data.
        }
        System.out.println("Done");
    }
}