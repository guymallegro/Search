package sample.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private ArrayList<Document> documents;

    public Model() {
        documents = new ArrayList<>();
    }

    public void readFiles(String path) {
        try {
            ReadFile.readFile(path);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
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
                    documents.add(currentDocument);
                    text = "";
                    currentDocument.print();
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
        System.out.println(documents.size());
    }

    private String removeTag(String line) {
        return line.replaceAll("\\<.*?\\>", "");
    }
}
