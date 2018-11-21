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
    private HashMap<String, String> percents;
    private HashMap<String, String> money;
    private HashMap<String, String> date;
    private HashMap<String, Term> allTerms;
    private String[] tests;
    private ArrayList<String> tokens;
    private int currentTest = 0;
    private boolean toTest = false;

    Parse() {
        tokens = new ArrayList<>();
        stemmer = new Stemmer();
        numbers = new HashMap<>();
        percents = new HashMap<>();
        allTerms = new HashMap<>();
        date = new HashMap<>();
        money = new HashMap<>();
        initRules();
        initTests();
    }

    void parseDocument(Document document) {
        if (document.getContent() != null) {
            splitDocument(document.getContent());
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).length() > 0 && (!isStopWord(tokens.get(i)) || (tokens.get(i).equals("between") && (i < tokens.size() - 1 && tokens.get(i + 1).length() > 0 && Character.isDigit(tokens.get(i + 1).charAt(0)))))) {
                    if (doStemming) {
                        stemmer.setTerm(tokens.get(i));
                        stemmer.stem();
                        tokens.set(i, stemmer.getTerm());
                    }
                    if (checkIfContainsIllegalSymbols(tokens.get(i))) {
                    }
                    else if (checkRange(i)) {
                    }else if (checkFraction(i)) {
                    } else if (checkLittleMoney(i)) {
                    } else if (Character.isDigit(tokens.get(i).charAt(0)) || tokens.get(i).charAt(0) == '$') {
                        if (checkIfContainsLetters(tokens.get(i))) {
                        } else if (checkPercent(i)) {
                        } else if (checkDate(i)) {
                        } else if (checkMoney(i)) {
                        } else if (checkNumber(i)) {
                        }
                    } else if (date.containsKey(tokens.get(i))) {
                        if (i + 1 < tokens.size()) {
                            if (i < tokens.size()-1 && tokens.get(i + 1).matches("[0-9]+")) {
                                if (tokens.get(i + 1).length() == 4)
                                    tokens.set(i, tokens.get(i + 1) + "-" + date.get(tokens.get(i)));
                                else if (tokens.get(i + 1).length() == 0) {
                                } else
                                    tokens.set(i, date.get(tokens.get(i)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i + 1))));
                                tokens.set(i + 1, "");
                            }
                        }
                    } else if (tokens.get(i).equals("Between") || tokens.get(i).equals("between")) {
                        if (i < tokens.size() - 3) {
                            if (Character.isDigit(tokens.get(i + 1).charAt(0)) &&
                                    tokens.get(i + 2).equals("and") &&
                                    Character.isDigit(tokens.get(i + 3).charAt(0))) {
                                tokens.set(i, tokens.get(i + 1) + "-" + tokens.get(i + 3));
                                tokens.set(i + 3, "");
                                tokens.set(i + 2, "");
                                tokens.set(i + 1, "");
                            }
                        }
                    } else {
                        parseByLetters(i);
                    }
                    addTerm(tokens.get(i));
                }
            }
        }
    }

    private boolean checkRange(int i) {
        return tokens.get(i).contains("-");
    }

    private boolean checkFraction(int i) {
        if (tokens.get(i).contains("/")) {
            checkLittleMoney(i);
            return true;
        } else if (i + 1 < tokens.size() && tokens.get(i + 1).contains("/")) {
            if (tokens.get(i).matches("[0-9]+") && tokens.get(i + 1).matches("[0-9]+[/][0-9]+")) {
                tokens.set(i + 1, tokens.get(i) + " " + tokens.get(i + 1));
                checkLittleMoney(i + 1);
                tokens.set(i, tokens.get(i + 1));
                tokens.set(i + 1, "");
                return true;
            }
        }
        return false;
    }

    private boolean checkLittleMoney(int i) {
        if (i + 1 < tokens.size() && tokens.get(i + 1).contains("Dollar")) {
            if ((tokens.get(i).length() > 6) || (tokens.get(i).contains(",") && tokens.get(i).length() > 7))
                return false;
            tokens.set(i, tokens.get(i).replaceAll("m", " M"));
            tokens.set(i, tokens.get(i).replaceAll("bn", "000 M"));
            tokens.set(i, tokens.get(i) + " Dollars");
            tokens.set(i + 1, "");
            return true;

        } else if (tokens.get(i).charAt(0) == '$') {
            if (tokens.get(i).contains(",")) {
                if (tokens.get(i).length() > 8)
                    return false;
            } else {
                if (tokens.get(i).length() > 7)
                    return false;
            }
            if (i < tokens.size() - 1) {
                if (tokens.get(i + 1).contains("million")) {
                    tokens.set(i, tokens.get(i).substring(1) + " M" + " Dollars");
                    return true;
                } else if (tokens.get(i + 1).contains("billion")) {
                    tokens.set(i, tokens.get(i).substring(1) + "000 M" + " Dollars");
                    return true;
                }
            }
            tokens.set(i, tokens.get(i).substring(1) + " Dollars");
            return true;
        }
        return false;
    }

    private boolean checkPercent(int i) {
        if (i + 1 < tokens.size()) {
            if (percents.containsKey(tokens.get(i + 1))) {
                tokens.set(i, tokens.get(i) + "%");
                tokens.set(i + 1, "");
                return true;
            }
        }
        return tokens.get(i).charAt(tokens.get(i).length() - 1) == '%';
    }

    private boolean checkDate(int i) {
        if (checkIfContainsCommas(tokens.get(i)))
            return false;
        if (i + 1 < tokens.size()) {
            if (date.containsKey(tokens.get(i + 1))) {
                tokens.set(i, date.get(tokens.get(i + 1)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i))));
                if (i + 2 < tokens.size()) {
                    //tokens.set(i + 2, cleanString(tokens.get(i + 2)));
                    if (tokens.get(i + 2).matches("[0-9][0-9][0-9][0-9]")) {
                        addTerm(tokens.get(i + 2) + "-" + date.get(tokens.get(i + 1)));
                        tokens.set(i + 2, "");
                    }
                }
                tokens.set(i + 1, "");
                return true;
            }
        }
        return false;
    }

    private boolean checkMoney(int i) {
        if (i < tokens.size() - 3) {
            if (tokens.get(i + 2).equals("U.S") && ((tokens.get(i + 3).equals("dollars")) || (tokens.get(i + 3).equals("dollar")))) {
                tokens.set(i, '$' + tokens.get(i));
                tokens.set(i + 2, "");
                tokens.set(i + 3, "");
            }
        }
        if (i < tokens.size() - 2) {
            if (money.containsKey(tokens.get(i + 1)) && (tokens.get(i + 2).equals("Dollars") || tokens.get(i + 2).equals("dollars") ||
                    tokens.get(i + 2).equals("Dollar") || tokens.get(i + 2).equals("dollar"))) {
                tokens.set(i + 2, "");
                tokens.set(i, '$' + tokens.get(i));
            }
        }
        if (i < tokens.size() - 1) {
            if (tokens.get(i + 1).contains("Dollar")) {
                tokens.set(i, '$' + tokens.get(i));
                tokens.set(i + 1, "");
            }
        }
        if (tokens.get(i).charAt(0) == '$') {
            if (i < tokens.size() - 1 && money.containsKey(tokens.get(i + 1))) {
                tokens.set(i, tokens.get(i).replaceAll(",", ""));
                String first = tokens.get(i).substring(1);
                double num=0;
                try {
                    num = new BigDecimal(Double.parseDouble(first)).doubleValue();
                }catch (Exception e){
                    System.out.println("Illegal word");
                }
                double number = num / Double.parseDouble(money.get(tokens.get(i + 1)));
                if (number % (double) 1 == 0)
                    tokens.set(i, Integer.toString((int) number) + " M Dollars");
                else
                    tokens.set(i, Double.toString(number) + " M Dollars");
                tokens.set(i + 1, "");
                return true;
            } else {
                tokens.set(i, tokens.get(i).substring(1));
                if(checkNumber(i)) {
                    tokens.set(i, tokens.get(i).substring(0, tokens.get(i).length() - 1) + " M Dollars");
                }
                else{
                    tokens.set(i, tokens.get(i).substring(0, tokens.get(i).length() - 1) + " Dollars");
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkNumber(int i) {
        if (checkIfIllegalNumber(tokens.get(i)))
            return true;
        tokens.set(i, tokens.get(i).replaceAll(",", ""));
        double num=0;
        try {
            num = new BigDecimal(Double.parseDouble(tokens.get(i))).doubleValue();
        }catch (Exception e){
            System.out.println("Illegal word");
        }
        if (i + 1 < tokens.size() && numbers.containsKey(tokens.get(i + 1))) {
            if (tokens.get(i + 1).equals("Trillion"))
                num *= 100;
            if (num < 1000) {
                if (num % (double) 1 == 0) {
                    tokens.set(i, Integer.toString((int) num) + numbers.get(tokens.get(i + 1)));
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
    private void parseByLetters(int i) {
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
            if (str.length() == 1) {
                return str;
            } else {
                str = str.substring(1);
                current = str.charAt(0);
            }
        }
        current = str.charAt(str.length() - 1);
        while (!(Character.isLetter(current) || Character.isDigit(current) || current == '%')) {
            if (str.length() == 1) {
                return str;
            } else {
                str = str.substring(0, str.length() - 1);
                current = str.charAt(str.length() - 1);
            }
        }
        return str;
    }

    private boolean isStopWord(String word) {
        return stopWords.contains(word);
    }

    private void addTerm(String term) {
        if (term.length() > 0) {
            if (term.equals("NEW-TEST")) {
                toTest = true;
                return;
            }
            if (toTest) {
                if (term.equals(tests[currentTest])) {
                    System.out.println("Successful test : " + term);
                } else {
                    System.out.println("FAILED TEST!!! Got : " + term + " , Expected " + tests[currentTest]);
                }
                toTest = false;
                currentTest++;
            }
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

    private void splitDocument(String content) {
        tokens = new ArrayList<>();
        int i = 0;
        while (i < content.length()) {
            String token = "";
            while (i < content.length() && content.charAt(i) != ' ') {
                token += content.charAt(i);
                i++;
            }
            if (!token.equals("")) {
                tokens.add(cleanString(token));
            }
            i++;
        }
    }

    void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    void setCurrentDocumentTerms(DocumentTerms documentTerms) {
        currentDocumentTerms = documentTerms;
    }

    private boolean checkIfContainsLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            int ascii = (int) s.charAt(i);
            if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfContainsIllegalSymbols(String s) {
        for (int i = 0; i < s.length(); i++) {
            int ascii = (int) s.charAt(i);
            if (ascii <= 35 || (ascii >= 38 && ascii <= 43) || ascii == 47 || (ascii >= 58 && ascii <= 64) || (ascii >= 91 && ascii <= 96) || ascii >= 123)

                return true;
        }
        return false;
    }

    private boolean checkIfContainsCommas(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.' || s.charAt(i) == ',')
                return true;
        }
        return false;
    }

    private boolean checkIfIllegalNumber(String s) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.')
                counter++;
            if (counter > 1 || s.charAt(i) == '$')
                return true;
        }
        return false;
    }

    private void initRules() {
        numbers.put("Thousand", "K");
        numbers.put("Million", "M");
        numbers.put("million", "M");
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
    }

    private void initTests() {
        tests = new String[40];
        tests[0] = "10.123K";
        tests[1] = "123K";
        tests[2] = "1.01056K";
        tests[3] = "10.123M";
        tests[4] = "55M";
        tests[5] = "10.123B";
        tests[6] = "55B";
        tests[7] = "700B";
        tests[8] = "204";
        tests[9] = "35.66";
        tests[10] = "35 3/4";
        tests[11] = "6%";
        tests[12] = "10.6%";
        tests[13] = "10.6%";
        tests[14] = "1.7320 Dollars";
        tests[15] = "22 3/4 Dollars";
        tests[16] = "450,000 Dollars";
        tests[17] = "1 M Dollars";
        tests[18] = "450 M Dollars";
        tests[19] = "100 M Dollars";
        tests[20] = "20.6 M Dollars";
        tests[21] = "100000 M Dollars";
        tests[22] = "100000 M Dollars";
        tests[23] = "100000 M Dollars";
        tests[24] = "320 M Dollars";
        tests[25] = "1000000 M Dollars";
        tests[26] = "05-14";
        tests[27] = "06-04";
        tests[28] = "1994-05";
        tests[29] = "Value-ad";
        tests[30] = "step-by-step";
        tests[31] = "10-part";
        tests[32] = "6-7";
        tests[33] = "18-24";
        tests[34] = "478.79 Dollars";
    }
}