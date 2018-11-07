package sample.Model;

import java.util.ArrayList;
import java.util.List;

public class Model {


    private ArrayList<Document> documents;
    private Parse parse;
    private ReadFile fileReader;


    public Model() {
        documents = new ArrayList<>();
        parse = new Parse(this);
        fileReader = new ReadFile(this);
    }

    public void readFiles(String path) {
        try {
            fileReader.readFile(path);
            parse.setStopWords(fileReader.readStopWords());

        } catch (Exception exception) {
            System.out.println("Exception");
            System.out.println(exception.getMessage());
        }
    }

    public void processFile(List<String> data) {
        parse.processFile(data);
        parse.processText();
        System.out.println(data.size());

    }

    public void addDocument(Document document) {
        documents.add(document);
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

}
