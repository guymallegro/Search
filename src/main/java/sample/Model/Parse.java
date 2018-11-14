package sample.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Parse {
    private boolean doStemming = true; //@TODO Needs to be set by UI
    private Stemmer stemmer;
    private HashSet<String> stopWords;
    private Model model;
    private DocumentTerms currentDocumentTerms;
    private HashMap<String, String> numbers;
    private HashMap<String, String> percents;
    private HashMap<String, String> money;
    private HashMap<String, String> date;
    private HashMap<String, Term> allTerms;
    private StringBuilder result;
    private Term newTerm;
    private String phrase;
    private ArrayList<String> tokens;
    private String toAdd;
    private boolean split;
    private String[] tempStrings;

    Parse(Model model) {
        this.model = model;
        stemmer = new Stemmer();
        numbers = new HashMap<>();
        percents = new HashMap<>();
        date = new HashMap<>();
        money = new HashMap<>();
        allTerms = new HashMap<>();
        initRules();
    }

    void parseDocument(Document document) {
        if (document.getContent() != null) {
            tokens = splitDocument(document.getContent());
            for (int i = 0; i < tokens.size(); i++) {
                if (!isStopWord(tokens.get(i))) {
                    tokens.set(i, cleanString(tokens.get(i)));
                    if (doStemming) {
                        try {
                            stemmer.setTerm(tokens.get(i));
                            stemmer.stem();
                            tokens.set(i, stemmer.getTerm());
                        } catch (Exception e) {
                            System.out.println("Stemmer error for word :" + tokens.get(i));
                        }
                    }
                }
            }
            phrase = "";
            for (int i = 0; i < tokens.size(); i++) {
                if (date.containsKey(phrase)) {
                    if (tokens.get(i).matches("[0-9]+")) {
                        if (Long.parseLong(tokens.get(i)) > 31)
                            phrase = tokens.get(i) + "-" + date.get(phrase);
                        else
                            phrase = date.get(phrase) + "-" + String.format("%02d", Long.parseLong(tokens.get(i)));
                        i++;
                    }
                } else if (date.containsKey(tokens.get(i)) && phrase.matches("[0-9]+")) {
                    phrase = date.get(tokens.get(i)) + "-" + String.format("%02d", Long.parseLong(phrase));
                } else if (i + 1 < tokens.size() && !tokens.get(i + 1).equals("Dollars") && !(tokens.get(i).charAt(0) == '$') && !tokens.get(i + 1).equals("Dollar")) {
                    tokens.set(i, parseNumbers(tokens.get(i)));
                }

                if (tokens.size() > i) {
                    if (numbers.containsKey(tokens.get(i))) {
                        phrase += numbers.get(tokens.get(i));
                    } else if (percents.containsKey(tokens.get(i))) {
                        phrase += percents.get(tokens.get(i));
                    } else if (tokens.get(i).matches("[0-9]+[/][0-9]+")) {
                        phrase += " " + tokens.get(i);
                    } else if (tokens.get(i).equals("Dollars") || tokens.get(i).equals("Dollar")) {
                        phrase += " Dollars";
                    } else if (tokens.get(i).charAt(0) == '$') {
                          tokens.set(i,tokens.get(i).replaceAll(",", ""));
                        if (Long.parseLong(tokens.get(i).substring(1)) < 1000000) {
                            addTerm(phrase);
                            phrase = tokens.get(i).substring(1) + " Dollars";
                        } else {
                            tokens.set(i, parseNumbers(tokens.get(i)));
                            double number= Double.parseDouble(tokens.get(i))/ Double.parseDouble(money.get(tokens.get(i)));
                        }
                    } else {
                        if (!phrase.equals("")) {
                            addTerm(phrase);
                        }
                        phrase = tokens.get(i);
                    }
                }
            }
            addTerm(phrase);
        }
    }

    private String parseNumbers(String token) {
        if (token.length() > 3 && token.matches("[0-9,\\.,\\,]+")) {
            split = false;
            toAdd = "";
            token = token.replaceAll(",", "");
            if (token.contains(".")) {
                split = true;
                tempStrings = token.split("\\.");
                token = tempStrings[0];
                toAdd = tempStrings[1];
                if (Long.parseLong(token) < 1000)
                    return token + "." + toAdd;
            }
            result = new StringBuilder(token);
            switch (result.length()) {
                case 4:
                case 7:
                case 10:
                    result.insert(1, ".");
                    break;
                case 5:
                case 8:
                case 11:
                    result.insert(2, ".");
                    break;
                case 6:
                case 9:
                case 12:
                    result.insert(3, ".");
                    break;
            }
            if (split)
                result.insert(result.length(), toAdd);
            if (result.length() - toAdd.length() < 8)
                result.insert(result.length(), "K");
            else if (result.length() - toAdd.length() < 11)
                result.insert(result.length(), "M");
            else
                result.insert(result.length(), "T");
            removeRedundantZeros(result);
            return result.toString();
        }
        return token;
    }

    private void addTerm(String term) {
        System.out.println(term);
        if (term.length() > 0) {
            if (!allTerms.containsKey(term)) {
                newTerm = new Term(term);
                allTerms.put(term, newTerm);
                currentDocumentTerms.addTermToText(newTerm);
            } else {
                allTerms.get(term).increaseAmount();
                currentDocumentTerms.addTermToText(allTerms.get(term));
            }
        }
    }

    private boolean isStopWord(String word) {
        if (stopWords.contains(word))
            return true;
        return false;
    }

    private String cleanString(String str) {
        char current = str.charAt(0);
        while (!(Character.isLetter(current) || Character.isDigit(current) || current == '$')) {
            if (str.length() > 1) {
                str = str.substring(1);
                current = str.charAt(0);
            } else
                return str;
        }
        current = str.charAt(str.length() - 1);
        while (!(Character.isLetter(current) || Character.isDigit(current)) || current == '%') {
            if (str.length() > 1) {
                str = str.substring(0, str.length() - 1);
                current = str.charAt(str.length() - 1);
            } else return str;
        }
        return str;
    }

    void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    void setCurrentDocumentTerms(DocumentTerms documentTerms) {
        currentDocumentTerms = documentTerms;
    }

    private void initRules() {
        numbers.put("Thousand", "K");
        numbers.put("Million", "M");
        numbers.put("Billion", "B");
        numbers.put("Trillion", "T");
        percents.put("percent", "%");
        percents.put("percentag", "%");
        percents.put("percentage", "%");
        date.put("January", "01");
        date.put("Januari", "01");
        date.put("JANUARY", "01");
        date.put("February", "02");
        date.put("Februari", "02");
        date.put("FEBRUARY", "02");
        date.put("March", "03");
        date.put("MARCH", "03");
        date.put("April", "04");
        date.put("APRIL", "04");
        date.put("Mai", "05");
        date.put("May", "05");
        date.put("MAY", "05");
        date.put("June", "06");
        date.put("JUNE", "06");
        date.put("July", "07");
        date.put("Juli", "07");
        date.put("JULY", "07");
        date.put("August", "08");
        date.put("AUGUST", "08");
        date.put("September", "09");
        date.put("Septemb", "09");
        date.put("SEPTEMBER", "09");
        date.put("October", "10");
        date.put("OCTOBER", "10");
        date.put("November", "11");
        date.put("Novemb", "11");
        date.put("NOVEMBER", "11");
        date.put("December", "12");
        date.put("Decemb", "12");
        date.put("DECEMBER", "12");
        money.put("million", "1");
        money.put("m", "1");
        money.put("billion", "0.001");
        money.put("bn", "0.001");
        money.put("trillion", "0.000001");
        money.put("T", "0.000001");
    }

    private void removeRedundantZeros(StringBuilder string) {
        for (int i = string.length() - 2; i > 0; i--) {
            if (string.charAt(i) == '0') {
                string.delete(i, i + 1);
            } else
                break;
        }
    }

    private ArrayList<String> splitDocument(String content) {
        ArrayList<String> ans = new ArrayList<>();
        int i = 0;
        while (i < content.length()) {
            String token = "";
            while (content.length() > i && content.charAt(i) != ' ') {
                token += content.charAt(i);
                i++;
            }
            if (!token.equals(""))
                ans.add(token);
            i++;
        }
        return ans;
    }


}