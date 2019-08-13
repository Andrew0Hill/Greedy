package util;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataReader {

    public static HashMap<Integer,List<String>> formatBackingData(List<List<String>> data_file){
        HashMap<Integer,List<String>> bd_map = new HashMap<>();
        for(List<String> row : data_file){
            bd_map.put(Integer.parseInt(row.get(17)),row);
        }
        return bd_map;
    }

    public static List<List<String>> getEdgeListFile(String filename, String sep, boolean skipHeader) throws FileNotFoundException, IOException {

        List<List<String>> edgelist_file = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // Skip the header
        if (skipHeader)
            reader.readLine();
        String line = reader.readLine();
        while(line != null) {
            List<String> values_row = Arrays.asList(line.split(sep));
            //assert values_row.size() == 9;
            edgelist_file.add(values_row);
            line = reader.readLine();
        }
        return edgelist_file;
    }
    public static List<List<List<String>>> getDataFile(String[] filenames, String sep, boolean skipHeader) throws FileNotFoundException, IOException {
        List<List<List<String>>> data_file_list = new ArrayList<>();

        for (String filename : filenames) {
            List<List<String>> data_file = new ArrayList<List<String>>();
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            // Skip the header of each file.
            if(skipHeader)
                reader.readLine();
            // Start reading here.
            String line = reader.readLine();
            while (line != null) {
                List<String> values_row = Arrays.asList(line.split(sep));

                for (int i = 0; i < values_row.size(); i++) {
                    values_row.set(i, values_row.get(i).replace("\"", ""));
                }

                data_file.add(values_row);
                line = reader.readLine();
            }
            reader.close();
            data_file_list.add(data_file);
        }
        return data_file_list;

    }
}
