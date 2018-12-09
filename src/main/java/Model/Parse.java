package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The parsing class
 */
class Parse {
    private Model model;
    private Stemmer stemmer;
    private HashSet<String> stopWords;
    private boolean doStemming = true;
    private HashMap<String, String> numbers;
    private HashMap<String, String> percents;
    private HashMap<String, String> money;
    private HashMap<String, String> date;
    private HashMap<String, String> numberNames;
    private HashMap<String, Term> allTerms;
    private HashSet dollars;
    private String[] tests;
    private ArrayList<String> tokens;
    private int currentTest = 0;
    private Document currentDocument;
    private boolean toTest = false;

    /**
     * The parse default constructor
     *
     * @param model - The model
     */
    Parse(Model model) {
        this.model = model;
        tokens = new ArrayList<>();
        stemmer = new Stemmer();
        numbers = new HashMap<>();
        percents = new HashMap<>();
        numberNames = new HashMap<>();
        allTerms = new HashMap<>();
        dollars = new HashSet();
        date = new HashMap<>();
        money = new HashMap<>();
        initRules();
        initTests();
    }

    /**
     * Parses a given document into terms using smaller functions.
     *
     * @param document - The given document to parse.
     */
    void parseDocument(Document document) {
        currentDocument = document;
        if (document.getContent() != null) {
            splitDocument(document.getContent());
            currentDocument.setLength(tokens.size());
            for (int i = 0; i < tokens.size(); i++) {
                checkNumberName(i);
                if(tokens.get(i).length()>0 && tokens.get(i).charAt(0)=='<') continue;
                if (tokens.get(i).length() > 0 && (!isStopWord(tokens.get(i)) || (tokens.get(i).equals("between") && (i < tokens.size() - 1 && tokens.get(i + 1).length() > 0 && Character.isDigit(tokens.get(i + 1).charAt(0)))))) {
                    checkCity(i);
                    if (doStemming) {
                        stemmer.setTerm(tokens.get(i));
                        stemmer.stem();
                        tokens.set(i, stemmer.getTerm());
                    }
                    if (checkIfContainsIllegalSymbols(tokens.get(i))) {
                        continue;
                    } else if (checkRange(i)) {
                    } else if (checkFraction(i)) {
                    } else if (Character.isDigit(tokens.get(i).charAt(0)) || tokens.get(i).charAt(0) == '$') {
                        if (checkIfContainsLetters(tokens.get(i))) {
                        } else if (checkLittleMoney(i)) {
                        } else if (checkPercent(i)) {
                        } else if (checkDate(i)) {
                        } else if (checkMoney(i)) {
                        } else if (checkNumber(i)) {
                        }
                    } else if (date.containsKey(tokens.get(i))) {
                        if (i + 1 < tokens.size()) {
                            if (i < tokens.size() - 1 && tokens.get(i + 1).matches("[0-9]+")) {
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
                    removeRedundantZero(i);
                    addTerm(tokens.get(i), i);
                }
            }
        }
    }

    /**
     * Checks if the token at position i is a city, and add it if it is
     *
     * @param i - The position
     */
    private void checkCity(int i) {
        if (model.getCitiesDictionary().containsKey(tokens.get(i)))
            model.getCitiesDictionary().get(tokens.get(i)).addLocation(currentDocument.getIndexId(), i);
    }

    /**
     * Checks if the token at position i is a range term, and parse it if it its
     *
     * @param i - The position
     * @return - If it is a range term
     */
    private boolean checkRange(int i) {
        if (tokens.get(i).contains("--"))
            tokens.set(i, tokens.get(i).substring(0, tokens.get(i).indexOf("--")) + tokens.get(i).substring(tokens.get(i).indexOf("--") + 1));
        String[] range = tokens.get(i).split("-");
        if (range.length > 1) {
            StringBuilder ans = new StringBuilder();
            for (int j = 0; j < range.length; j++) {
                tokens.set(i, range[j]);
                if (range[j].matches("[0-9]+"))
                    checkNumber(i);
                else
                    parseByLetters(i);
                addTerm(tokens.get(i), i);
                ans.append(tokens.get(i)).append("-");
            }
            ans.deleteCharAt(ans.length() - 1);
            tokens.set(i, ans.toString());
            return true;
        }
        return false;
    }

    /**
     * Checks if the token at position i is a fraction, and parse it if it its
     *
     * @param i - The position
     * @return - If it is a fraction
     */
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

    /**
     * Checks if the token at position i is little money, and parse it if it its
     *
     * @param i - The position
     * @return - If it is little money
     */
    private boolean checkLittleMoney(int i) {
        if (i + 1 < tokens.size() && dollars.contains(tokens.get(i + 1))) {
            if (tokens.get(i).indexOf(",") != tokens.get(i).lastIndexOf(",") || tokens.get(i).charAt(0) == '/')
                return false;
            String num = tokens.get(i).replaceAll(",", "");
            if (num.indexOf(".") > 6)
                return false;
            if (tokens.get(i).charAt(0) == '$')
                tokens.set(i, tokens.get(i).substring(1) + " Dollars");
            else
                tokens.set(i, tokens.get(i) + " Dollars");
            tokens.set(i + 1, "");
            return true;
        } else if (tokens.get(i).charAt(0) == '$') {
            if (tokens.get(i).indexOf(",") != tokens.get(i).lastIndexOf(","))
                return false;
            String num = tokens.get(i).replaceAll(",", "");
            if (num.indexOf(".") > 6 || (i + 1 < tokens.size() && money.containsKey(tokens.get(i + 1))))
                return false;
            tokens.set(i, tokens.get(i).substring(1) + " Dollars");
            return true;
        }
        return false;
    }

    /**
     * Checks if the token at position i is percent, and parse it if it its
     *
     * @param i - The position
     * @return - If it is percent
     */
    private boolean checkPercent(int i) {
        if (i + 1 < tokens.size()) {
            if (percents.containsKey(tokens.get(i + 1))) {
                tokens.set(i, tokens.get(i) + "%");
                tokens.set(i + 1, "");
                return true;
            }
        }
        return tokens.get(i).contains("%");
    }

    /**
     * Checks if the token at position i is a date, and parse it if it its
     *
     * @param i - The position
     * @return - If it is a date
     */
    private boolean checkDate(int i) {
        if (checkIfContainsCommas(tokens.get(i)))
            return false;
        if (i + 1 < tokens.size()) {
            if (date.containsKey(tokens.get(i + 1))) {
                tokens.set(i, date.get(tokens.get(i + 1)) + "-" + String.format("%02d", Integer.parseInt(tokens.get(i))));
                if (i + 2 < tokens.size()) {
                    if (tokens.get(i + 2).matches("[0-9][0-9][0-9][0-9]")) {
                        addTerm(tokens.get(i + 2) + "-" + date.get(tokens.get(i + 1)), i);
                        tokens.set(i + 2, "");
                    }
                }
                tokens.set(i + 1, "");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the token at position i is money, and parse it if it its
     *
     * @param i - The position
     * @return - If it is money
     */
    private boolean checkMoney(int i) {
        if (i < tokens.size() - 3) {
            if (tokens.get(i + 2).equals("U.S") && (dollars.contains(tokens.get(i + 3)))) {
                if (tokens.get(i).charAt(0) == '$')
                    tokens.set(i, tokens.get(i).substring(1));
                if (money.containsKey(tokens.get(i + 1))) {
                    double num = Double.parseDouble(money.get(tokens.get(i + 1)));
                    tokens.set(i, tokens.get(i).replaceAll(",", ""));
                    tokens.set(i, parseNumber(Double.valueOf(tokens.get(i)) / num) + " M Dollars");
                    tokens.set(i + 1, "");
                    tokens.set(i + 2, "");
                    tokens.set(i + 3, "");
                    return true;
                }
            }
        }
        if (i < tokens.size() - 2 && dollars.contains(tokens.get(i + 2))) {
            if (money.containsKey(tokens.get(i + 1))) {
                if (tokens.get(i).charAt(0) == '$')
                    tokens.set(i, tokens.get(i).substring(1));
                double num = Double.parseDouble(money.get(tokens.get(i + 1)));
                tokens.set(i, tokens.get(i).replaceAll(",", ""));
                if (tokens.get(i).length() == 0)
                    return true;
                tokens.set(i, parseNumber(Double.valueOf(tokens.get(i)) / num) + " M Dollars");
                tokens.set(i + 1, "");
                tokens.set(i + 2, "");
                return true;
            }
        }
        if (i < tokens.size() - 1 && dollars.contains(tokens.get(i + 1))) {
            if (tokens.get(i).charAt(0) == '$')
                tokens.set(i, tokens.get(i).substring(1));
            String num = tokens.get(i).replaceAll(",", "");
            num = parseNumber(Double.valueOf(num) / 1000000.0);
            tokens.set(i, num + " M Dollars");
            tokens.set(i + 1, "");
            return true;
        }
        if (tokens.get(i).charAt(0) == '$' && tokens.get(i).length() > 1) {
            tokens.set(i, tokens.get(i).substring(1).replaceAll(",", ""));
            if (i < tokens.size() - 1 && money.containsKey(tokens.get(i + 1))) {
                double num = Double.parseDouble(money.get(tokens.get(i + 1)));

                tokens.set(i, parseNumber(Double.valueOf(tokens.get(i)) / num) + " M Dollars");
                tokens.set(i + 1, "");
                return true;
            } else {
                String num = tokens.get(i).replaceAll(",", "");
                num = parseNumber(Double.valueOf(num) / 1000000.0);
                tokens.set(i, num + " M Dollars");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the token at position i is a number, and parse it if it its
     *
     * @param i - The position
     * @return - If it is a number
     */
    private boolean checkNumber(int i) {
        if (tokens.get(i).indexOf('.') != tokens.get(i).lastIndexOf('.') || tokens.get(i).contains("/"))
            return true;
        tokens.set(i, tokens.get(i).replaceAll(",", ""));
        if (tokens.get(i).length() == 0)
            return true;
        double num = 0;
        try {
            num = Double.parseDouble(tokens.get(i));
        } catch (Exception e) {
            System.out.println("Illegal word " + tokens.get(i) + " current doc: " + currentDocument.getIndexId());
        }
        if (i + 1 < tokens.size() && numbers.containsKey(tokens.get(i + 1))) {
            if (tokens.get(i + 1).equals("Trillion"))
                num *= 100;
            if (num < 1000) {
                tokens.set(i, parseNumber(num) + numbers.get(tokens.get(i + 1)));
                tokens.set(i + 1, "");
                return true;
            }
        }
        if (num < 1000) {
            tokens.set(i, parseNumber(num));
            return true;
        }
        if (num >= 1000) {
            if (num < 1000000) {
                tokens.set(i, parseNumber(num / 1000) + "K");
            } else if (num >= 1000000 && num < 1000000000) {
                tokens.set(i, parseNumber(num / 1000000) + "M");
            } else {
                tokens.set(i, parseNumber(num / 1000000000) + "B");
            }
            return true;
        }
        return false;
    }

    /**
     * This function checks if the token at position i starts with capital letters or not. It checks its previous
     * appearances and only if all its appearances are with capital letters it will be saved with capital letters.
     *
     * @param i - The postiion
     */
    private void parseByLetters(int i) {
        if (tokens.get(i).length() < 1) return;
        String upper = tokens.get(i).toUpperCase();
        String lowerCase = tokens.get(i).toLowerCase();
        if (Character.isUpperCase(tokens.get(i).charAt(0))) {
            if (allTerms.containsKey(lowerCase)) {
                tokens.set(i, lowerCase);
                return;
            } else if (model.getTermsDictionary().containsKey(lowerCase)) {
                if (allTerms.containsKey(upper)) {
                    Term value = allTerms.remove(upper);
                    value.setValue(lowerCase);
                    allTerms.put(lowerCase, value);
                    tokens.set(i, lowerCase);
                    return;
                }
            }
            tokens.set(i, upper);
        } else if (Character.isLowerCase(tokens.get(i).charAt(0))) {
            if (allTerms.containsKey(lowerCase))
                return;
            else if (allTerms.containsKey(upper)) {
                Term value = allTerms.remove(upper);
                value.setValue(lowerCase);
                allTerms.put(lowerCase, value);
            }
            tokens.set(i, lowerCase);
        }
    }

    /**
     * Cleans a string spaces and illegal symbols
     *
     * @param str - The string to clean
     * @return - The clean string
     */
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

    /**
     * Checks if a given word is a stop word
     *
     * @param word - The word to check
     * @return - If it is a stop word
     */
    private boolean isStopWord(String word) {
        if (stopWords.contains(word) || stopWords.contains(Character.toUpperCase(word.charAt(0)) + word.substring(1)))
            return true;
        return false;
    }

    /**
     * This function adds a term to the dictionary
     *
     * @param term - The term to add
     * @param i    -The position the term was found at
     */
    private void addTerm(String term, int i) {
        if (term.length() > 1 || (term.length() == 1 && Character.isDigit(term.charAt(0)))) {
            String upper = term.toUpperCase();
            String lowerCase = term.toLowerCase();
            if (allTerms.containsKey(term)) {
                allTerms.get(term).increaseAmount();
                allTerms.get(term).addInDocument(currentDocument.getIndexId(), ((double) i) / tokens.size());
                currentDocument.addTermToText(allTerms.get(term));
            } else {
                if (Character.isUpperCase(term.charAt(0))) {
                    Term newTerm;
                    if (model.getTermsDictionary().containsKey(lowerCase))
                        newTerm = new Term(lowerCase);
                    else
                        newTerm = new Term(upper);
                    allTerms.put(term, newTerm);
                    newTerm.addInDocument(currentDocument.getIndexId(), ((double) i) / tokens.size());
                    currentDocument.addTermToText(newTerm);
                } else {
                    Term newTerm = new Term(lowerCase);
                    allTerms.put(term, newTerm);
                    newTerm.addInDocument(currentDocument.getIndexId(), ((double) i) / tokens.size());
                    currentDocument.addTermToText(newTerm);
                }
            }
//            if (term.equals("NEW-TEST")) {
//                toTest = true;
//                return;
//            }
//            if (toTest) {
//                if (term.equals(tests[currentTest])) {
//                    System.out.println("Successful test : " + term);
//                } else {
//                    System.out.println("FAILED TEST!!! Got : " + term + " , Expected " + tests[currentTest]);
//                }
//                toTest = false;
//                currentTest++;
//            }
        }
    }

    /**
     * Splits a string into terms and puts it into the tokens list
     *
     * @param content - The content to split
     */
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

    /**
     * Sets the stop words into the given stop words
     *
     * @param stopWords - The given stop words
     */
    void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * Checks if a given string contains letters
     *
     * @param s - The given string
     * @return - If it contains letters
     */
    private boolean checkIfContainsLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            int ascii = (int) s.charAt(i);
            if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given string contains illegal symbols
     *
     * @param s - The given string
     * @return - If it contains illegal symbols
     */
    private boolean checkIfContainsIllegalSymbols(String s) {
        if (s.equals("$"))
            return true;
        if (s.indexOf(".") != s.lastIndexOf("."))
            return true;
        int index = s.indexOf('$');
        if (index != -1 && index != 0 && index != s.length() - 1)
            return true;
        int size = s.length();
        for (int i = 0; i < size; i++) {
            int ascii = (int) s.charAt(i);
            if (ascii <= 35 || (ascii >= 38 && ascii <= 43) || ascii == 47 || (ascii >= 58 && ascii <= 64) || (ascii >= 91 && ascii <= 96) || ascii >= 123)
                return true;
        }
        return false;
    }

    /**
     * Checks if a given string contains a comma
     *
     * @param s - The given string
     * @return - If it contains a comma
     */
    private boolean checkIfContainsCommas(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.' || s.charAt(i) == ',')
                return true;
        }
        return false;
    }

    /**
     * Checks if the tokens are a number and if they are parse them into a digits number.
     *
     * @param i - The position to start checking for
     */
    private void checkNumberName(int i) {
        int checking = i;
        int finalResult = 0;
        int result = 0;
        if (numberNames.containsKey(tokens.get(i))) {
            while (checking < tokens.size() && numberNames.containsKey(tokens.get(checking))) {
                String str = tokens.get(checking);
                tokens.set(checking, "");
                if (str.equals("zero")) {
                    result += 0;
                } else if (str.equals("one")) {
                    result += 1;
                } else if (str.equals("two")) {
                    result += 2;
                } else if (str.equals("three")) {
                    result += 3;
                } else if (str.equals("four")) {
                    result += 4;
                } else if (str.equals("five")) {
                    result += 5;
                } else if (str.equals("six")) {
                    result += 6;
                } else if (str.equals("seven")) {
                    result += 7;
                } else if (str.equals("eight")) {
                    result += 8;
                } else if (str.equals("nine")) {
                    result += 9;
                } else if (str.equals("ten")) {
                    result += 10;
                } else if (str.equals("eleven")) {
                    result += 11;
                } else if (str.equals("twelve")) {
                    result += 12;
                } else if (str.equals("thirteen")) {
                    result += 13;
                } else if (str.equals("fourteen")) {
                    result += 14;
                } else if (str.equals("fifteen")) {
                    result += 15;
                } else if (str.equals("sixteen")) {
                    result += 16;
                } else if (str.equals("seventeen")) {
                    result += 17;
                } else if (str.equals("eighteen")) {
                    result += 18;
                } else if (str.equals("nineteen")) {
                    result += 19;
                } else if (str.equals("twenty")) {
                    result += 20;
                } else if (str.equals("thirty")) {
                    result += 30;
                } else if (str.equals("forty")) {
                    result += 40;
                } else if (str.equals("fifty")) {
                    result += 50;
                } else if (str.equals("sixty")) {
                    result += 60;
                } else if (str.equals("seventy")) {
                    result += 70;
                } else if (str.equals("eighty")) {
                    result += 80;
                } else if (str.equals("ninety")) {
                    result += 90;
                } else {
                    tokens.set(checking, str);
                }
                checking++;
            }
            finalResult += result;
            tokens.set(i, Integer.toString(finalResult));
        }
    }

    /**
     * Prases a number
     *
     * @param num - The number to parse
     * @return - The parsed number
     */
    private String parseNumber(double num) {
        String ans = Double.toString(num);
        while (ans.length() > 1) {
            if (ans.charAt(ans.length() - 1) == '.')
                return ans.substring(0, ans.length() - 1);
            if (ans.charAt(ans.length() - 1) != '0')
                return ans;
            else
                ans = ans.substring(0, ans.length() - 1);
        }
        return ans;
    }

    /**
     * Removes redundant zeros from a token at a given position
     *
     * @param i - The given position
     */
    private void removeRedundantZero(int i) {
        if (tokens.get(i).length() > 2 && tokens.get(i).charAt(0) == '0' && tokens.get(i).charAt(1) == '.')
            tokens.set(i, tokens.get(i).substring(1));
    }

    /**
     * Tells if stemming is used
     *
     * @param selected - If it is used
     */
    void setStemming(boolean selected) {
        doStemming = selected;
    }

    /**
     * Returns all the terms that were found
     *
     * @return - All the found terms
     */
    HashMap<String, Term> getAllTerms() {
        return allTerms;
    }

    /**
     * Initializes the rules
     */
    private void initRules() {
        numbers.put("Thousand", "K");
        numbers.put("thousand", "K");
        numbers.put("Million", "M");
        numbers.put("million", "M");
        numbers.put("Billion", "B");
        numbers.put("billion", "B");
        numbers.put("Trillion", "B");
        numbers.put("trillion", "B");
        percents.put("percent", "%");
        percents.put("percentag", "%");
        percents.put("percentage", "%");
        date.put("January", "01");
        date.put("Jan", "01");
        date.put("Januari", "01");
        date.put("JANUARY", "01");
        date.put("JAN", "01");
        date.put("February", "02");
        date.put("Feb", "02");
        date.put("Februari", "02");
        date.put("FEBRUARY", "02");
        date.put("FEB", "02");
        date.put("March", "03");
        date.put("Mar", "03");
        date.put("MARCH", "03");
        date.put("MAR", "03");
        date.put("April", "04");
        date.put("Apr", "04");
        date.put("APRIL", "04");
        date.put("APR", "04");
        date.put("Mai", "05");
        date.put("May", "05");
        date.put("MAY", "05");
        date.put("June", "06");
        date.put("Jun", "06");
        date.put("JUNE", "06");
        date.put("JUN", "06");
        date.put("July", "07");
        date.put("Jul", "07");
        date.put("Juli", "07");
        date.put("JULY", "07");
        date.put("JUL", "07");
        date.put("August", "08");
        date.put("Aug", "08");
        date.put("AUGUST", "08");
        date.put("AUG", "08");
        date.put("September", "09");
        date.put("Sep", "09");
        date.put("Septemb", "09");
        date.put("SEPTEMBER", "09");
        date.put("SEP", "09");
        date.put("October", "10");
        date.put("Oct", "10");
        date.put("OCTOBER", "10");
        date.put("OCT", "10");
        date.put("November", "11");
        date.put("Nov", "11");
        date.put("Novemb", "11");
        date.put("NOVEMBER", "11");
        date.put("NOV", "11");
        date.put("December", "12");
        date.put("Dec", "12");
        date.put("Decemb", "12");
        date.put("DECEMBER", "12");
        date.put("DEC", "12");
        money.put("million", "1");
        money.put("m", "1");
        money.put("M", "1");
        money.put("billion", "0.001");
        money.put("B", "0.001");
        money.put("bn", "0.001");
        money.put("trillion", "0.000001");
        money.put("T", "0.000001");

        numberNames.put("zero", "0");
        numberNames.put("one", "1");
        numberNames.put("two", "2");
        numberNames.put("three", "3");
        numberNames.put("four", "4");
        numberNames.put("five", "5");
        numberNames.put("six", "6");
        numberNames.put("seven", "7");
        numberNames.put("eight", "8");
        numberNames.put("nine", "9");
        numberNames.put("ten", "10");
        numberNames.put("eleven", "11");
        numberNames.put("twelve", "12");
        numberNames.put("thirteen", "13");
        numberNames.put("fourteen", "14");
        numberNames.put("fifteen", "15");
        numberNames.put("sixteen", "16");
        numberNames.put("seventeen", "17");
        numberNames.put("eighteen", "18");
        numberNames.put("nineteen", "19");
        numberNames.put("twenty", "20");
        numberNames.put("thirty", "30");
        numberNames.put("forty", "40");
        numberNames.put("fifty", "50");
        numberNames.put("sixty", "60");
        numberNames.put("seventy", "70");
        numberNames.put("eighty", "80");
        numberNames.put("ninety", "90");
        numberNames.put("hundred", "100");
        numberNames.put("thousand", "1000");
        numberNames.put("million", "1000000");
        numberNames.put("billion", "1000000000");
        numberNames.put("and", "");

        dollars.add("Dollar");
        dollars.add("Dollars");
        dollars.add("dollar");
        dollars.add("dollars");
    }

    private void initTests() {
        tests = new String[50];
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
        tests[29] = "Value-added";
        tests[30] = "step-by-step";
        tests[31] = "10-part";
        tests[32] = "6-7";
        tests[33] = "18-24";
        tests[34] = "1";
        tests[35] = "2";
        tests[36] = "3";
        tests[37] = "4";
        tests[38] = "5";
        tests[39] = "6";
        tests[40] = "7";
        tests[41] = "8";
        tests[42] = "9";
        tests[43] = "0";
        tests[44] = "134";
        tests[45] = "26.000157M";
        tests[46] = "5 Dollars";
        tests[47] = "1.000003 M Dollars";
        tests[48] = ".15";
    }
}