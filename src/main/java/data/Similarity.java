package data;

import java.util.List;

public class Similarity {
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
}
