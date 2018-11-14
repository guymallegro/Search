package sample.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Model {

    private ArrayList<Document> documents;
    private HashSet<DocumentTerms> documentsTerms;
    private Parse parse;
    private ReadFile fileReader;
    private DocumentTerms currentDocumentTerms;
    private Document currentDocument;
    private String text;
    private boolean insideText;

    public Model() {
        documents = new ArrayList<>();
        documentsTerms = new HashSet<>();
        parse = new Parse(this);
        fileReader = new ReadFile(this);
    }

    public void readFiles(String filesPath, String stopWordsPath) {
        long tStart = System.currentTimeMillis();
        parse.setStopWords(fileReader.readStopWords(stopWordsPath));
        fileReader.readFile(filesPath);
        System.out.println("--------------------------------------");
        System.out.println("-----------------Complete-------------");
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Time it took: "+elapsedSeconds +" seconds");
    }

    public void processFile(List<String> data) {
        createDocuments(data);
        for (Document document : documents) {
            currentDocumentTerms = new DocumentTerms(document.getId());
            parse.setCurrentDocumentTerms(currentDocumentTerms);
            parse.parseDocument(document);
            documentsTerms.add(currentDocumentTerms);
            currentDocumentTerms.print();
        }
        documents.clear();
        documentsTerms.clear();
    }

    private void createDocuments(List<String> data) {
        currentDocument = null;
        text = "";
        insideText = false;
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

    private String removeTag(String line) {
        return line.replaceAll("\\<.*?\\>", "");
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }
}