package sample.Model;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Indexer {
    private HashMap<String, String[]> dictionary;
    private HashMap<Character,String> filesNames;
    private Iterator it;

    public Indexer() {
        dictionary = new HashMap<>();
        filesNames = new HashMap<>();
        try {
            //createPostingFiles();
        } catch (Exception e) {
            System.out.println("failed at createPostingFiles");
        }
        initFileNames();

    }

//    File currentDirectory = new File(path);
//        currentFile = new File(path + directory);
//        System.out.println("Current file " + directory);
//        allFiles = currentFile.list()[0];
//        try {
//            lines = Files.readAllLines(Paths.get(path + directory + "/" + allFiles), StandardCharsets.ISO_8859_1);
//        }
//        catch (Exception e){
//            System.out.println("Cannot open file: "+path);
//        }
//    }

    public void addToDictionary(Term term, String path){
        path = "C:\\Users\\ספיר רצון\\Desktop\\test\\";
        if(dictionary.containsKey(term.getValue())){
//            String fileName = "";
//            if (term.getInDocuments().containsKey(term.getValue()))
//                fileName = term.getInDocuments().get(Integer.toString(term.getValue()));
//            File currentDirectory = new File(path);
        }
        else{
            String filename = path+ "numbers.txt";
            if(filesNames.containsKey(term.getValue().charAt(0))){
                filename = path+filesNames.get(term.getValue().charAt(0)) + ".txt";
            }

            try{
                File posting = new File(filename);
                RandomAccessFile writer = new RandomAccessFile(posting, "rw");
                writer.writeBytes("<"+term.getValue()+":"+term.getInDocuments().size()+ ";");
                //                posting.createNewFile();
//                FileOutputStream out = new FileOutputStream(posting, true);
//                out.write(("<"+term.getValue()+":"+term.getInDocuments().size()+ ";").getBytes());
//                FileWriter fw = new FileWriter(filename, true);
//                BufferedWriter bw = new BufferedWriter(fw);
//                PrintWriter out = new PrintWriter(bw);
//                out.print("<"+term.getValue()+":"+term.getInDocuments().size()+ ";");
                Iterator it = term.getInDocuments().entrySet().iterator();
                int previous = 0;
                StringBuilder line = new StringBuilder();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    line.append((int)pair.getValue() - previous + ",");
                    it.remove();
                }
                line.deleteCharAt(line.toString().length() - 1).append("\n");
                writer.writeBytes((line.toString()));
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }

    }

    private void createPostingFiles() throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("a.txt"), "utf-8"))) {
            writer.write("a");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("bc.txt"), "utf-8"))) {
            writer.write("bc");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("defg.txt"), "utf-8"))) {
            writer.write("defg");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("hi.txt"), "utf-8"))) {
            writer.write("hi");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("jklmn.txt"), "utf-8"))) {
            writer.write("jklmn");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("op.txt"), "utf-8"))) {
            writer.write("op");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("qrs.txt"), "utf-8"))) {
            writer.write("qrs");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("t.txt"), "utf-8"))) {
            writer.write("t");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("uvwxyz.txt"), "utf-8"))) {
            writer.write("uvwxyz");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("numbers.txt"), "utf-8"))) {
            writer.write("numbers");
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("docs.txt"), "utf-8"))) {
            writer.write("docs");
        }
    }

    private void initFileNames() {
        filesNames.put('a',"a");
        filesNames.put('b',"bc");
        filesNames.put('c',"bc");
        filesNames.put('d',"defg");
        filesNames.put('e',"defg");
        filesNames.put('f',"defg");
        filesNames.put('g',"defg");
        filesNames.put('h',"hi");
        filesNames.put('i',"hi");
        filesNames.put('j',"jklmn");
        filesNames.put('k',"jklmn");
        filesNames.put('l',"jklmn");
        filesNames.put('m',"jklmn");
        filesNames.put('n',"jklmn");
        filesNames.put('o',"op");
        filesNames.put('p',"op");
        filesNames.put('q',"qrs");
        filesNames.put('r',"qrs");
        filesNames.put('s',"qrs");
        filesNames.put('t',"t");
        filesNames.put('u',"uvwxyz");
        filesNames.put('v',"uvwxyz");
        filesNames.put('w',"uvwxyz");
        filesNames.put('x',"uvwxyz");
        filesNames.put('y',"uvwxyz");
        filesNames.put('z',"uvwxyz");

    }

    public void addAllTerms(HashMap<String, Term> allTerms) {
        Iterator it = allTerms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            addToDictionary((Term) pair.getValue(),"");
            it.remove();
        }
    }
}
