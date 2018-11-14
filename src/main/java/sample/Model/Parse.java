package sample.Model;

import com.sun.deploy.util.StringUtils;
import javafx.util.converter.DateTimeStringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

class Parse {
    private Stemmer stemmer;
    private boolean doStemming = true; //@TODO Needs to be set by UI
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
    private String[] tokens;
    private String toAdd;
    private boolean split;
    private String[] tempStrings;

    Parse(Model model) {
        this.model = model;
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
            ArrayList <String> tokens = splitDocument (document.getContent());
            for (int i = 0; i < tokens.size(); i++) {
                if (!tokens.get(i).equals("") && !isStopWord(tokens.get(i))) {
                    tokens.set(i, cleanString(tokens.get(i)));
                    if (doStemming) {
                        stemmer.setTerm(tokens.get(i));
                        stemmer.stem();
                        tokens.set(i, stemmer.getTerm());
                    }
                    if (Character.isDigit(tokens.get(i).charAt(0)) || tokens.get(i).charAt(0) == '$') {
                        if (!(Character.isDigit(tokens.get(i).charAt(tokens.get(i).length()-1)))){
                            addTerm(tokens.get(i));
                        }
                        else {
                            StringBuilder[] ans = new StringBuilder[2];
                            ans[0] = new StringBuilder(tokens.get(i));
                            //ans[1]= new StringBuilder("no");
                            if (i != tokens.size() - 1) { // don't forget to add if not !!
                                ans[1] = new StringBuilder(tokens.get(i + 1));
                                if (checkRange(ans)) {
                                    tokens.set(i, ans[0].toString());
                                    if (ans[1].toString().equals(""))
                                        tokens.remove(i + 1);
                                    addTerm(ans[0].toString());
                                } else if (checkFraction(ans)) {
                                    tokens.set(i, ans[0].toString());
                                    if (ans[1].toString().equals(""))
                                        tokens.remove(i + 1);
                                    addTerm(ans[0].toString());
//                                else
//                                    tokens.set(i + 1, ans[1].toString());
                                } else if (checkPercent(ans)) {
                                    tokens.set(i, ans[0].toString());
                                    if (ans[1].toString().equals(""))
                                        tokens.remove(i + 1);
                                    addTerm(ans[0].toString());
//                                else
//                                    tokens.set(i + 1, ans[1].toString());
                                } else if (checkDate(tokens, i, ans)) {
                                    tokens.set(i, ans[0].toString());
                                    if (ans[1].toString().equals(""))
                                        tokens.remove(i + 1);
                                    addTerm(ans[0].toString());
//                                else
//                                    tokens.set(i + 1, ans[1].toString());
                                } else if (moneyCases(tokens, i, ans) && checkMoney(ans)) {
                                    addTerm(ans[0].toString());
//                                else
//                                    tokens.set(i + 1, ans[1].toString());
                                } else if (checkNumber(ans)) {
                                    tokens.set(i, ans[0].toString());
                                    if (ans[1].toString().equals(""))
                                        tokens.remove(i + 1);
                                    addTerm(ans[0].toString());
//                                else
//                                    tokens.set(i + 1, ans[1].toString());
                                }
                            }
                        }
                    }
                    else if (tokens.get(i).contains("-")) {
                        addTerm(tokens.get(i));
                    }
                    else if (date.containsKey(tokens.get(i))){
                        if (i < tokens.size() - 1 && Character.isDigit(tokens.get(i+1).charAt(0))){
                            StringBuilder [] ans = new StringBuilder[]{new StringBuilder(tokens.get(i)), new StringBuilder(tokens.get(i+1))};
                            if (checkDate(tokens, i, ans)){
                                if (ans[1].toString().equals(""))
                                    tokens.remove(i + 1);
                                addTerm(ans[0].toString());
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
                        parseByLetters(tokens.get(i));
                    }
                }
            }
        }
    }

    private boolean moneyCases(ArrayList<String> tokensArray, int i, StringBuilder [] tokens) {
        if (i < tokensArray.size() - 3){
            if (tokensArray.get(i + 2).equals("U.S.") && (tokensArray.get(i + 3).equals("dollars")))
                tokensArray.remove(i + 3); // check if the change update !!!
        }
        if (i < tokensArray.size() - 2){
            if (tokensArray.get(i + 2).equals("Dollars") || tokensArray.get(i + 2).equals("dollars")) {
                tokensArray.remove(i + 2); // check if the change update !!!
                tokens[0] = new StringBuilder('$' + tokens[0].toString());
            }
        }
        if (tokens[1].toString().equals("Dollars")){
            tokens[0] = new StringBuilder("$" + tokens[0].toString());
            tokens[0] = new StringBuilder();
        }
        else if (tokens[0].toString().charAt(0) == '$' && money.containsKey(tokens[1].toString())){
            String first = cleanString(tokens[0].toString().substring(1));
            double number = Double.parseDouble(first) / Double.parseDouble(money.get(tokens[1].toString()));
            if (number % (double) 1 == 0)
                tokens[0] = new StringBuilder('$' + Integer.toString((int) number));
            else
                tokens[0] = new StringBuilder('$' + Double.toString(number));
            tokens[1] = new StringBuilder();
            return true;
        }
        return false;
    }

    private boolean checkFraction(StringBuilder[] ans) {
        if (ans[0].toString().contains("/")){
            return true;
        }
        else if (ans[1].toString().contains("/") &&
                (Character.isDigit(ans[1].toString().charAt(ans[1].toString().indexOf("/") + 1)))){
            ans [0] = new StringBuilder(ans[0].toString() + " " + ans[1].toString());
            ans [1] = new StringBuilder();
            return true;
        }
        return false;
    }

    private ArrayList<String> splitDocument(String content) {
        ArrayList <String> ans = new ArrayList<>();
        int i = 0;
        while (i < content.length()) {
            String token = "";
            while (content.charAt(i) != ' ') {
                token += content.charAt(i);
                i++;
            }
            if (!token.equals(""))
                ans.add(token);
            i++;
        }
        return ans;
    }

    private boolean checkDate(ArrayList<String> tokensArray, int i, StringBuilder [] tokens) {
        if (date.containsKey(tokens[1].toString())){
            tokens [0] = new StringBuilder(date.get(tokens[1].toString()) + "-" + String.format("%02d", Integer.parseInt(tokens[0].toString())));
            if (i < tokensArray.size() - 2){
                if (i < tokensArray.size() - 1 && Character.isDigit(tokensArray.get(i + 2).charAt(0)))
                    return true;
            }
            tokens [1] = new StringBuilder();
            return true;
        }
        else if (date.containsKey(tokens[0].toString())){
            if (tokens[1].toString().length() == 4)
                tokens [0] = new StringBuilder(tokens[1].toString() + "-" + date.get(tokens[0].toString()));
            else
                tokens [0] = new StringBuilder(date.get(tokens[0].toString()) + "-" + String.format("%02d", Integer.parseInt(tokens[1].toString())));
            tokens [1] = new StringBuilder();
            return true;
        }
//            if (tokens[0].toString().length() == 1){
//                tokens[0] = new StringBuilder(date.get(tokens[1].toString()) + "-0" + tokens[0].toString());
//                tokens[1] = new StringBuilder();
//                return true;
//            }
//            else if (tokens[0].toString().length() == 2){
//                tokens[0] = new StringBuilder(date.get(tokens[1].toString()) + tokens[0].toString());
//                tokens[1] = new StringBuilder();
//                return true;
//            }
        return false;
    }

    private boolean checkMoney (StringBuilder [] tokens) {
        String first = tokens[0].toString();
        if (first.contains("-") || first.contains("/") || first.length() < 4) {
            if (tokens[1].toString().equals("Dollars")) {
                tokens[0] = new StringBuilder(tokens[0] + " Dollars");
                tokens[1] = new StringBuilder();
                return true;
            } else if (first.charAt(0) == '$') {
                first = first.substring(1);
                tokens[0] = new StringBuilder(first.substring(1) + " Dollars");
//                double num = new BigDecimal(Double.parseDouble(first)).doubleValue();
//                if (num < 1000000)
//                    tokens[0] = new StringBuilder(first);
//                checkNumber(tokens);
//                tokens[0] = new StringBuilder(tokens[0] + " Dollars");
                return true;
            }
        }
        else {
            if (tokens[1].toString().equals("Dollars")) {
                first = "$" + first;
                tokens[1] = new StringBuilder();
            }
            if (first.charAt(0) == '$') {
                first = cleanString(first.substring(1));
                if (money.containsKey(tokens[1].toString())) {
                    double number = Double.parseDouble(first) / Double.parseDouble(money.get(tokens[1].toString()));
                    if (number % (double) 1 == 0) {
                        tokens[0] = new StringBuilder(Integer.toString((int) number) + " M Dollars");
                        tokens[1] = new StringBuilder();
                    }
                } else {
                    tokens[0] = new StringBuilder(first);
                    checkNumber(tokens);
                    String number = tokens[0].toString();
                    tokens[0] = new StringBuilder(number.substring(0, number.length() - 1) + " M Dollars");
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkPercent(StringBuilder [] tokens) {
        String first = tokens[0].toString();
        first = cleanString(first);
        tokens[1] = new StringBuilder(cleanString(tokens[1].toString()));
        if (tokens[1].toString().equals("percent") || tokens[1].toString().equals("percentage")){
            tokens[0] = new StringBuilder(tokens[0] + "%");
            tokens[1] = new StringBuilder();
            return true;
        }
        else if (first.charAt(first.length() - 1) == '%') {
//            first = first.substring(0, first.length() - 1);
//            tokens[0] = new StringBuilder (first);
//            checkNumber(tokens);
//            tokens[0] = new StringBuilder(tokens[0] + "%");
            return true;
        }
        return false;
    }

    private boolean checkNumber(StringBuilder [] tokens) {
        tokens [0] = new StringBuilder(tokens[0].toString().replaceAll(",", ""));
        tokens [0] = new StringBuilder(cleanString(tokens[0].toString()));
        tokens [1] = new StringBuilder(cleanString(tokens[1].toString()));
        double num = new BigDecimal(Double.parseDouble(tokens[0].toString())).doubleValue();
        if (numbers.containsKey(tokens[1].toString())){
            if (tokens[1].toString().equals("Trillion"))
                num *= 100;
            if (num < 1000){
                if (num % (double) 1 == 0) {
                    tokens[0] = new StringBuilder(Integer.toString((int)num) + numbers.get(tokens[1].toString()));
                    tokens[1] = new StringBuilder();
                    return true;
                }
                tokens[0] = new StringBuilder (num + numbers.get(tokens[1].toString()));
                tokens[1] = new StringBuilder();
                return true;
            }
            tokens[1] = new StringBuilder();
        }
        if (num >= 1000) {
            if (num < 1000000) {
                if (num / 1000 % (double) 1 == 0) {
                    tokens[0] = new StringBuilder(Integer.toString((int) num / 1000) + "K");
                    return true;
                }
                tokens[0] = new StringBuilder (Double.toString(num / 1000) + "K");
                return true;
            } else if (num >= 1000000 && num < 1000000000) {
                if (num / 1000000 % (double) 1 == 0) {
                    tokens[0] = new StringBuilder(Integer.toString((int) num / 1000000) + "M");
                    return true;
                }
                tokens[0] = new StringBuilder(Double.toString(num / 1000000) + "M");
                return true;
            } else {
                if (num / 1000000000 % (double) 1 == 0) {
                    tokens[0] = new StringBuilder(Integer.toString((int) num / 1000000000) + "B");
                    return true;
                }
                tokens[0] = new StringBuilder(Double.toString(num / 1000000000) + "B");
                return true;
            }
        }
        return false;
    }


//    private StringBuilder [] checkNumber(StringBuilder firstToken, StringBuilder secondToken) {
//        String [] ans = new String [2];
//        ans [0] = firstToken.toString().replaceAll(",", "");
//        ans[1] = cleanString(secondToken.toString());
//        if (numbers.containsKey(ans[1]))
//            ans[1] = "";
//        BigDecimal bigDecimal = new BigDecimal(Double.parseDouble(ans[0]));
//        double num = bigDecimal.doubleValue();
//        if (num >= 1000) {
//            if (num < 1000000) {
//                if (num / 1000 % (double) 1 == 0) {
//                    ans[0] = Integer.toString((int) num / 1000) + "K";
//                    return ans;
//                }
//                ans [0] = Double.toString(num / 1000) + "K";
//                return ans;
//            } else if (num >= 1000000 && num < 1000000000) {
//                if (num / 1000000 % (double) 1 == 0) {
//                    ans[0] = Integer.toString((int) num / 1000000) + "M";
//                    return ans;
//                }
//                ans[0] = Double.toString(num / 1000000) + "M";
//                return ans;
//            } else {
//                if (num / 1000000000 % (double) 1 == 0) {
//                    ans[0] = Integer.toString((int) num / 1000000000) + "B";
//                    return ans;
//                }
//                ans[0] = Double.toString(num / 1000000000) + "B";
//                return ans;
//            }
//        }
//        return ans;
//    }

    private boolean checkRange(StringBuilder [] token) {
        String [] range = token[0].toString().split("-");
        if (range.length > 1) {
            StringBuilder [] temp = new StringBuilder[2];
            temp [1] = token [1];
            String ans = "";
            temp[0] = new StringBuilder(range[0]);
            checkNumber(temp);
            ans = temp[0].toString();
            temp[0] = new StringBuilder(range[1]);
            if (Character.isDigit(range[1].charAt(0)))
                checkNumber(temp);
            ans += "-" + temp[0].toString();
            token[0] = new StringBuilder(ans);
            return true;
        }
        return false;
    }

    private void parseByRules(String [] tokens) {
        String phrase = "";
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = parseNumbers(tokens[i]);
            if (numbers.containsKey(tokens[i])) {
                phrase += numbers.get(tokens[i]);
            }
            else if(percents.containsKey(tokens[i])){
                phrase += percents.get(tokens[i]);
            }
            else {
                addTerm(phrase);
                phrase = tokens[i];
            }
        }
        addTerm(phrase);
    }


    private String parseNumbers2(String token) {
        String ans = token.replaceAll(",", "");
        BigDecimal bigDecimal = new BigDecimal(Double.parseDouble(ans));
        double num = bigDecimal.doubleValue();
        if (num >= 1000) {
            if (num >= 1000 && num < 1000000){
                if (num/1000 % (double) 1 == 0)
                    return Integer.toString((int)num / 1000) + "K";
                return Double.toString(num/1000) + "K";
            }
            else if (num >= 1000000 && num < 1000000000) {
                if (num / 1000000 % (double) 1 == 0)
                    return Integer.toString((int)num / 1000000) + "M";
                return Double.toString(num / 1000000) + "M";
            }
            else {
                if (num / 1000000000 % (double) 1 == 0)
                    return Integer.toString((int)num / 1000000000) + "B";
                return Double.toString(num / 1000000000) + "B";
            }
        }
        return ans;
    }

    private String parseNumbers(String token) {
        token = token.replaceAll(",", "");
        if (token.length() > 3 && token.matches("[0-9]+")) { // this condition makes problems!!!
            Boolean split = false;
            String toAdd = "";
            token = token.replaceAll(",", "");
            if (token.contains(".")) {
                split = true;
                String[] tempStrings = token.split("\\.");
                token = tempStrings[0];
                toAdd = tempStrings[1];
                if(Integer.parseInt(token) < 1000)
                    return token+"."+toAdd;
            }
            StringBuilder ans = new StringBuilder(token);
            switch (ans.length()) {
                case 4:
                case 7:
                case 10:
                    ans.insert(1, ".");
                    break;
                case 5:
                case 8:
                case 11:
                    ans.insert(2, ".");
                    break;
                case 6:
                case 9:
                case 12:
                    ans.insert(3, ".");
                    break;
            }
            if (split)
                ans.insert(ans.length(), toAdd);
            if (ans.length() - toAdd.length() < 8)
                ans.insert(ans.length(), "K");
            else if (ans.length() - toAdd.length() < 11)
                ans.insert(ans.length(), "M");
            else
                ans.insert(ans.length(), "T");
            return ans.toString();
        }
        return token;
    }

    /*
    This function refers to small or capital letters of terms.
    For each term, if the first letter of the term always appears as capital letter,
    the term will be save just with capital letters, else, the term will be save just with small letters.
     */
    private void parseByLetters (String token) {
        if (token.length() == 0)
            return;
        if (Character.isUpperCase(token.charAt(0))) {
            if (allTerms.containsKey(token.toLowerCase())) {
                removeCapitalLetter (token);
                addTerm(token.toLowerCase());
                return;
            }
            else {
                addTerm(token.toUpperCase());
                return;
            }
        }
        else {
            removeCapitalLetter(token);
        }
        addTerm(token.toLowerCase());
    }


    private void removeCapitalLetter(String token) {
        if (allTerms.containsKey(token.toUpperCase())) {
            int amount = allTerms.get(token.toUpperCase()).getAmount();
            Term newTerm = new Term(token.toLowerCase());
            newTerm.setAmount(amount);
            allTerms.put(token.toLowerCase(), newTerm);
            allTerms.remove(token.toUpperCase());
        }
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
        System.out.println(term + ", Amount: (" + allTerms.get(term).getAmount() + ")");
    }

    private boolean isStopWord(String word) {
        if (stopWords.contains(word))
            return true;
        return false;
    }

    private String cleanString(String str) {
//        if (str.length() == 0)
//            return "";
        //char current = str.charAt(0);
        char current = str.charAt(str.length() - 1);
        while (current == '.' || current == ',') {
            str = str.substring(0, str.length() - 1);
            current = str.charAt(str.length() - 1);
        }
//        while (!(Character.isDigit(current) || Character.isUpperCase(current) ||
//                Character.isLowerCase(current) || str.length() == 1)) {
//            str = str.substring(1);
//        }
//        current = str.charAt(str.length() - 1);
//        while (!(Character.isDigit(current) || Character.isUpperCase(current) ||
//                Character.isLowerCase(current) || str.length() == 1 || current == '%')) {
//            str = str.substring(0, str.length() - 1);
//        }
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
        numbers.put("M", "1");
        numbers.put("B", "0.001");
        numbers.put("Trillion", "B");;
        money.put("million", "1");
        money.put("m", "1");
        money.put("billion", "0.001");
        money.put("bn", "0.001");
        money.put("trillion", "0.000001");
        money.put("T", "0.000001");

        date.put ("January", "01");
        date.put ("JANUARY", "01");
        date.put ("February", "02");
        date.put ("FEBRUARY", "02");
        date.put ("March", "03");
        date.put ("MARCH", "03");
        date.put ("April", "04");
        date.put ("APRIL", "04");
        date.put ("May", "05");
        date.put ("MAY", "05");
        date.put ("June", "06");
        date.put ("JUNE", "06");
        date.put ("July", "07");
        date.put ("JULY", "07");
        date.put ("August", "08");
        date.put ("AUGUST", "08");
        date.put ("September", "09");
        date.put ("SEPTEMBER", "09");
        date.put ("October", "10");
        date.put ("OCTOBER", "10");
        date.put ("November", "11");
        date.put ("NOVEMBER", "11");
        date.put ("December", "12");
        date.put ("DECEMBER", "12");



    }

    private void removeRedundantZeros(StringBuilder string){
        for(int i=string.length()-2;i>0;i--){
            if(string.charAt(i) =='0'){
                string.delete(i,i+1);
            }
            else
                break;
        }
    }
}