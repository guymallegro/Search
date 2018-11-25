package sample.Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Indexer {
    private HashMap<String, String[]> dictionary;
    private HashMap<Character,String> filesNames;
    private int currentPostFile;

    public Indexer() {
        currentPostFile = 0;
        dictionary = new HashMap<>();
        filesNames = new HashMap<>();
    }

        public void addAllTerms(HashMap<String, Term> allTerms, String path){
        path = "C:\\Users\\ספיר רצון\\Desktop\\test\\";
        Object [] sortedterms = allTerms.keySet().toArray();

        Arrays.sort(sortedterms);
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<String>();
        for (int i = 0; i < sortedterms.length; i++){
            line.append("<" + sortedterms[i]+":" + allTerms.get(sortedterms[i]).getInDocuments().size()+ ";");
            Iterator it = allTerms.get(sortedterms[i]).getInDocuments().entrySet().iterator();
            int previous = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                line.append((int)pair.getValue() - previous + ",");
                it.remove();
            }
            line.deleteCharAt(line.toString().length() - 1);
            lines.add(line.toString());
            line.setLength(0);
        }
        path += "post" + currentPostFile + ".txt";
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception e){
            System.out.println("cannot write");
        }
        currentPostFile++;
    }
}
