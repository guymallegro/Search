package sample.Model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Parse {
    HashMap<String, String> termsTable;


    HashSet<String> stopWords;
    Model model;

    public Parse(Model model) {
        termsTable = new HashMap<>();
        this.model = model;
    }

    public void processFile(List<String> data) {
        Document currentDocument = null;
        String text = "";
        boolean insideText = false;
        for (String line : data) {
            if (!line.equals(""))
                if (line.equals("</TEXT>")) {
                    currentDocument.setContent(text);
                    insideText = false;
                } else if (insideText) {
                    text += line;
                } else if (line.equals("<DOC>")) {
                    currentDocument = new Document();
                } else if (line.equals("</DOC>")) {
                    model.addDocument(currentDocument);
                    text = "";
                } else if (line.equals("<TEXT>")) {
                    insideText = true;
                } else if (line.contains("<TI>")) {
                    currentDocument.setTitle(removeTag(line));
                } else if (line.contains("<DATE")) {
                    currentDocument.setDate(removeTag(line));
                } else if (line.contains("<DOCNO>")) {
                    currentDocument.setId(removeTag(line));
                }
        }
    }

    public void processText() {
        try {
            String resultText = "";
            for (Document document : model.getDocuments()) {
                if (document.getContent() != null) {
                    String[] tokens = document.getContent().split(" ");
                    for (String token : tokens) {
                        if (!isStopWord(token)){

                        }

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("process");
        }

    }

    private boolean isStopWord(String word) {
        if (stopWords.contains(word))
            return true;
        return false;
    }

    public void parsing(Document document) {
        String[] tokens = document.getContent().split(" ");
        for (String token : tokens) {


        }

    }

    private String removeTag(String line) {
        return line.replaceAll("\\<.*?\\>", "");
    }

    public void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }
}