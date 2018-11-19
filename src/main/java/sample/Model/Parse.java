package sample.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Parse {
    private Stemmer stemmer;
    private boolean doStemming = true; //@TODO Needs to be set by UI
    private HashSet<String> stopWords;
    private DocumentTerms currentDocumentTerms;
    private HashMap<String, String> numbers;
    private HashMap<String,String> percents;
    private HashMap<String, String> money;
    private HashMap<String, String> date;
    private HashMap<String, Term> allTerms;
    ArrayList<String> tokens;

    Parse(Model model) {
        tokens = new ArrayList<>();
        stemmer = new Stemmer();
        numbers = new HashMap<>();
        percents = new HashMap<>();
        allTerms = new HashMap<>();
        date = new HashMap<>();
        money = new HashMap<>();
        initRules();
    }

    void parseDocument(Document document) {
        if (document.getContent() != null) {
            tokens = splitDocument (document.getContent());
            for (int i = 0; i < tokens.size(); i++) {
                if (!tokens.get(i).equals("") && !isStopWord(tokens.get(i))) {
                    tokens.set(i, cleanString(tokens.get(i)));
                    if (doStemming) {
                        stemmer.setTerm(tokens.get(i));
                        stemmer.stem();
                        tokens.set(i, stemmer.getTerm());
                    }
                    if (checkRange(i)){}
                    else if (checkFraction(i)) {
                    }
                    else if (checkLittleMoney(i)){
                    }
                    else if (Character.isDigit(tokens.get(i).charAt(0)) || tokens.get(i).charAt(0) == '$') {
                        if (!(tokens.get(i).matches("[0-9]+"))){
                        } else if (checkPercent(i)) {
                        } else if (checkDate(i)) {
                        } else if (checkMoney(i)) {
                        } else if (checkNumber(i)) { }
                    }
                    else if (tokens.get(i).contains("-")) {
                    }
                    else if (date.containsKey(tokens.get(i))){
                        if (i + 1 < tokens.size())  {
                            tokens.set(i + 1,cleanString(tokens.get(i + 1)));
                            if (tokens.get(i + 1).matches("[0-9]+")) {
                                if (tokens.get(i + 1).length() == 4)
                                    tokens.set(i, tokens.get(i + 1) + "-" + date.get(tokens.get(i)));
                                else if (tokens.get(i + 1).length() == 0) {
                                } else
                                    tokens.set(i, date.get(tokens.get(i)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i + 1))));
                                tokens.set(i + 1, "");
                            }
                        }
                    }
                    else if (tokens.get(i).equals("Between")){
                        if (i < tokens.size() - 3) {
                            if (Character.isDigit(tokens.get(i + 1).charAt(0)) &&
                                    tokens.get(i + 2).equals("and") &&
                                    Character.isDigit(tokens.get(i + 3).charAt(0))) {
                                tokens.set(i + 1, tokens.get(i + 1) + "-" + tokens.get(i + 3)); // check if the change update !!!
                                tokens.remove(i + 3);
                                tokens.remove(i + 2);
                                tokens.remove(i);
                            }
                        }
                    }
                    else{
                        parseByLetters(i);
                    }
                    addTerm(tokens.get(i));
                }
            }
        }
    }

    private boolean checkRange(int i) {
        String [] range = tokens.get(i).split("-");
        if (range.length > 1) {
            String ans = range[0];
            if (range[0].matches("[0-9]+")) {
                tokens.set(i, range[0]);
                checkNumber(i);
                ans = tokens.get(i);
            }
            if (range[1].matches("[0-9]+")) {
                tokens.set(i, range[1]);
                checkNumber(i);
                ans += "-" + tokens.get(i);
            }
            else
                ans += "-" + range[1];
            tokens.set(i, ans);
            return true;
        }
        return false;
    }

    private boolean checkFraction(int i) {
        if (tokens.get(i).contains("/")){
            checkLittleMoney(i);
            return true;
        }
        else if (i + 1 < tokens.size() && tokens.get(i+1).contains("/")){
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if (tokens.get(i).matches("[0-9]+") && tokens.get(i + 1).matches("[0-9]+[/][0-9]+")){
                tokens.set(i + 1, tokens.get(i) + " " + tokens.get(i + 1));
                checkLittleMoney(i + 1);
                tokens.set(i, tokens.get(i + 1));
                tokens.set(i + 1, "");
                return true;
            }
        }
        return false;
    }

    private boolean checkLittleMoney (int i){
        if (i + 1 < tokens.size() && tokens.get(i + 1).contains("Dollar")){
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if ((tokens.get(i).length() > 6) || (tokens.get(i).contains(",") && tokens.get(i).length() > 7))
                return false;
            tokens.set(i, tokens.get(i) + " Dollars");
            tokens.set(i + 1, "");
            return true;

        }
        else if (tokens.get(i).charAt(0) == '$'){
            if ((tokens.get(i).length() > 7) || (tokens.get(i).contains(",") && tokens.get(i).length() > 8))
                return false;
            tokens.set(i, tokens.get(i).substring(1) + " Dollars");
            return true;
        }
        return false;
    }

    private boolean checkPercent(int i) {
        if (i + 1 < tokens.size()){
            tokens.set(i+1, cleanString(tokens.get(i + 1)));
            if (percents.containsKey(tokens.get(i+1))) {
                tokens.set(i, tokens.get(i) + "%");
                tokens.set(i + 1, "");
                return true;
            }
        }
        if (tokens.get(i).charAt(tokens.get(i).length() - 1) == '%') {
            return true;
        }
        return false;
    }

    private boolean checkDate(int i) {
        if (i + 1 < tokens.size()){
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if (date.containsKey(tokens.get(i + 1))) {
                tokens.set(i, date.get(tokens.get(i + 1)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i))));
                if (i + 2 < tokens.size()) {
                    tokens.set(i + 2, cleanString(tokens.get(i + 2)));
                    if (tokens.get(i + 2).matches("[0-9][0-9][0-9][0-9]")) {
                        addTerm(tokens.get(i + 2) + "-" + date.get(tokens.get(i + 1)));
                        tokens.set(i + 2, "");
                    }
                }
                tokens.set(i + 1, "");
                return true;
            }
        }
//        if (date.containsKey(tokens.get(i))){
//            if (tokens.get(i+1).length() == 4)
//                //addTerm(tokens.get(i+1) + "-" + date.get(tokens.get(i)));
//                tokens.set(i, tokens.get(i+1) + "-" + date.get(tokens.get(i)));
//            else if (tokens.get(i+1).length() == 0){
//                return false;
//            }
//            else
//                //addTerm(date.get(tokens.get(i)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i+1))));
//                tokens.set(i, date.get(tokens.get(i)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i+1))));
//            tokens.set(i + 1, "");
//            return true;
//        }
        return false;
    }

    private boolean checkMoney(int i) {
        if (i < tokens.size() - 3){
            tokens.set(i + 3, cleanString(tokens.get(i + 3)));
            tokens.set(i + 2, cleanString(tokens.get(i + 2)));
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if (tokens.get(i + 2).equals("U.S.") && ((tokens.get(i + 3).equals("dollars")) || (tokens.get(i + 3).equals("dollar")))){
                tokens.set(i, '$' + tokens.get(i));
                tokens.set(i + 2, "");
                tokens.set(i + 3, "");
            }
        }
        if (i < tokens.size() - 2){
            tokens.set(i + 2, cleanString(tokens.get(i + 2)));
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if (money.containsKey(tokens.get(i + 1)) && (tokens.get(i + 2).equals("Dollars") || tokens.get(i + 2).equals("dollars") ||
                    tokens.get(i + 2).equals("Dollar") || tokens.get(i + 2).equals("dollar"))) {
                tokens.set(i + 2, "");
                tokens.set(i,'$' + tokens.get(i));
            }
        }
        if (i < tokens.size() - 1){
            //tokens.set(i + 1, cleanString(tokens.get(i + 1)));
            if (tokens.get(i+1).contains("Dollar")) {
                tokens.set(i + 1, cleanString(tokens.get(i + 1)));
                tokens.set(i, '$' + tokens.get(i));
                tokens.set(i + 1, "");
            }
        }
        if (tokens.get(i).charAt(0) == '$'){
            if (money.containsKey(tokens.get(i+1))) {
                String first = tokens.get(i).substring(1);
                double number = Double.parseDouble(first) / Double.parseDouble(money.get(tokens.get(i + 1)));
                if (number % (double) 1 == 0)
                    tokens.set(i, Integer.toString((int) number) + " M Dollars");
                else
                    tokens.set(i, Double.toString(number) + " M Dollars");
                tokens.set(i + 1, "");
                return true;
            }
            else{
                tokens.set(i, tokens.get(i).substring(1));
                checkNumber(i);
                tokens.set(i, tokens.get(i).substring(0, tokens.get(i).length() - 1) + " M Dollars");
                return true;
            }
        }
        return false;
    }

    private boolean checkNumber(int i) {
        tokens.set(i, tokens.get(i).replaceAll(",", ""));
        tokens.set(i, cleanString(tokens.get(i)));
        if (i + 1 < tokens.size())
            tokens.set(i + 1, cleanString(tokens.get(i + 1)));
        double num = new BigDecimal(Double.parseDouble(tokens.get(i))).doubleValue();
        if (i + 1 < tokens.size() && numbers.containsKey(tokens.get(i + 1))){
            if (tokens.get(i + 1).equals("Trillion"))
                num *= 100;
            if (num < 1000){
                if (num % (double) 1 == 0) { // maybe 0.0?
                    tokens.set(i, Integer.toString((int)num) + numbers.get(tokens.get(i + 1)));
                    tokens.set(i + 1, "");
                    return true;
                }
                tokens.set(i, num + numbers.get(tokens.get(i + 1)));
                tokens.set(i + 1, "");
                return true;
            }
            tokens.set(i + 1, "");
        }
        if (num >= 1000) {
            if (num < 1000000) {
                if (num / 1000 % (double) 1 == 0) {
                    tokens.set(i, Integer.toString((int) num / 1000) + "K");
                    return true;
                }
                tokens.set(i, Double.toString(num / 1000) + "K");
                return true;
            } else if (num >= 1000000 && num < 1000000000) {
                if (num / 1000000 % (double) 1 == 0) {
                    tokens.set(i, Integer.toString((int) num / 1000000) + "M");
                    return true;
                }
                tokens.set(i, Double.toString(num / 1000000) + "M");
                return true;
            } else {
                if (num / 1000000000 % (double) 1 == 0) {
                    tokens.set(i, Integer.toString((int) num / 1000000000) + "B");
                    return true;
                }
                tokens.set(i, Double.toString(num / 1000000000) + "B");
                return true;
            }
        }
        return false;
    }

    /*
    This function refers to small or capital letters of terms.
    For each term, if the first letter of the term always appears as capital letter,
    the term will be save just with capital letters, else, the term will be save just with small letters.
    */
    private void parseByLetters (int i) {
        String upper = tokens.get(i).toUpperCase();
        String lowerCase = tokens.get(i).toLowerCase();
        if (allTerms.containsKey(lowerCase)) {
            tokens.set(i, lowerCase);
            return;
        }
        if (Character.isUpperCase(tokens.get(i).charAt(0))) {
            tokens.set(i, upper);
        } else {
            if (allTerms.containsKey(upper)) {
                int amount = allTerms.get(upper).getAmount();
                allTerms.remove(upper);
                Term newTerm = new Term(lowerCase);
                newTerm.setAmount(amount);
                allTerms.put(newTerm.getValue(), newTerm);
                tokens.set(i, newTerm.getValue());
            } else
                tokens.set(i, lowerCase);
        }
    }

    private String cleanString(String str) {
        if (str.length() == 0)
            return "";
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

    private boolean isStopWord(String word) {
        if (stopWords.contains(word))
            return true;
        return false;
    }

    private void addTerm(String term) {
        if (term.length() > 0) {
            if (!allTerms.containsKey(term)) {
                Term newTerm = new Term(term);
                allTerms.put(term, newTerm);
                currentDocumentTerms.addTermToText(newTerm);
            } else {
                allTerms.get(term).increaseAmount();
                currentDocumentTerms.addTermToText(allTerms.get(term));
            }
        }
        //System.out.println(term + "    Amount: (" + allTerms.get(term).getAmount() + ")");
    }

    private ArrayList<String> splitDocument(String content) {
        ArrayList <String> ans = new ArrayList<>();
        int i = 0;
        while (i < content.length()) {
            String token = "";
            while (i < content.length() && content.charAt(i) != ' ') {
                token += content.charAt(i);
                i++;
            }
            if (!token.equals(""))
                ans.add(token);
            i++;
        }
        return ans;
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
        numbers.put("Trillion", "B");

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
        money.put("M", "1");
        money.put("billion", "0.001");
        money.put("B", "0.001");
        money.put("bn", "0.001");
        money.put("trillion", "0.000001");
        money.put("T", "0.000001");
        //        numbers.put("Thousand", "K");
//        numbers.put("Million", "M");
//        numbers.put("Billion", "B");
//        numbers.put("M", "1");
//        numbers.put("B", "0.001");
//        numbers.put("Trillion", "B");
//
//        money.put("million", "1");
//        money.put("m", "1");
//        money.put("billion", "0.001");
//        money.put("bn", "0.001");
//        money.put("trillion", "0.000001");
//        money.put("T", "0.000001");
//
//        date.put ("January", "01");
//        date.put ("JANUARY", "01");
//        date.put ("February", "02");
//        date.put ("FEBRUARY", "02");
//        date.put ("March", "03");
//        date.put ("MARCH", "03");
//        date.put ("April", "04");
//        date.put ("APRIL", "04");
//        date.put ("May", "05");
//        date.put ("MAY", "05");
//        date.put ("June", "06");
//        date.put ("JUNE", "06");
//        date.put ("July", "07");
//        date.put ("JULY", "07");
//        date.put ("August", "08");
//        date.put ("AUGUST", "08");
//        date.put ("September", "09");
//        date.put ("SEPTEMBER", "09");
//        date.put ("October", "10");
//        date.put ("OCTOBER", "10");
//        date.put ("November", "11");
//        date.put ("NOVEMBER", "11");
//        date.put ("December", "12");
//        date.put ("DECEMBER", "12");

    }
}