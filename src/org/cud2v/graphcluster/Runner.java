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
        data = Util.getDataFile(new String[]{"starbucks_initial.csv"}, separator);

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
        List<List<String>> increment_data = Util.getDataFile(new String[]{"starbucks_increment.csv"}, ",");

        int inc_idc = 0;

        while (inc_idc < increment_data.size()) {
            List<List<String>> increment_list = new ArrayList<>();
            String cur_increment = increment_data.get(inc_idc).get(0);
            String inc_plc = cur_increment;
            System.out.println("Processing Increment: " + inc_plc);
            // Iterate over the rows that are the same increment as this row.
            while (cur_increment.equals(inc_plc) && inc_idc < increment_data.size()) {
                // Add this row to the incremental subset
                increment_list.add(increment_data.get(inc_idc));
                // Increment the index by one.
                inc_idc++;
                // Get the increment string for the next row.
                if (inc_idc < increment_data.size())
                    inc_plc = increment_data.get(inc_idc).get(0);

            }
            System.out.println("Increment has " + increment_list.size() + " rows.");

            // Run incremental update clustering on this increment.

            // 1. Get connected component for update.

            // 2. Get clusters that participate in the connected components

            // 3. Run clustering on each cluster, along with the singleton clusters from the new data.
        }
    }
}