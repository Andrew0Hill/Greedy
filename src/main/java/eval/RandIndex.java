package eval;

import cluster.Cluster;
import data.RecordVertex;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class RandIndex {


    public static void write_labels_to_file(HashMap<RecordVertex, Cluster<RecordVertex, DefaultWeightedEdge>> cls_1,
                                            HashMap<RecordVertex,Cluster<RecordVertex,DefaultWeightedEdge>> cls_2) throws Exception{
        if(cls_1.keySet().size() != cls_2.keySet().size()){
            throw new Exception("Sizes are different. Cannot compare.");
        }
        ArrayList<RecordVertex> vlist_1 = new ArrayList<>(cls_1.keySet());
        ArrayList<RecordVertex> vlist_2 = new ArrayList<>(cls_2.keySet());

        Collections.sort(vlist_1);
        Collections.sort(vlist_2);

        List<Integer> labels_1 = vlist_1.stream().mapToInt(rv -> cls_1.get(rv).getId()).boxed().collect(Collectors.toList());
        List<Integer> labels_2 = vlist_1.stream().mapToInt(rv -> cls_2.get(rv).getId()).boxed().collect(Collectors.toList());

        BufferedWriter bf = new BufferedWriter(new FileWriter("labels.csv"));
        bf.write("left_id,left_label,right_id,right_label\n");
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < labels_1.size(); ++i){
            builder.append(vlist_1.get(i).getName());
            builder.append(",");
            builder.append(labels_1.get(i));
            builder.append(",");
            builder.append(vlist_2.get(i).getName());
            builder.append(",");
            builder.append(labels_2.get(i));
            builder.append("\n");
            bf.write(builder.toString());
            builder.setLength(0);
        }
        bf.close();
    }

}
