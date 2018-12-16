package Model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 *  The city class, will hold all the cities required information
 */
public class City {
    private String cityName;
    private String countryName;
    private String population;
    private String currency;
    private boolean capital;
    private HashMap<Integer, String> locationsInDocuments;
    private HashMap<Integer, Integer> counterInDocuments = new HashMap<>();
    static String maxCity = "";
    static int maxAmount = 0;

    /**
     * A constructor which makes a city out of a json object
     * @param city - Json object with the city's information
     */
    City(JSONObject city) {
        countryName = city.get("name").toString();
        cityName = city.get("capital").toString();
        population = city.get("population").toString();
        capital = true;
        parsePopulation();
        currency = city.getJSONArray("currencies").getJSONObject(0).get("name").toString();
        locationsInDocuments = new HashMap<>();
    }

    /**
     * A constructor which makes a city with only its name
     * @param name - The city's name
     */
    City(String name) {
        cityName = name;
        capital = false;
        locationsInDocuments = new HashMap<>();
    }

    /**
     * Saves a location which at the city was found
     * @param documentId - The document id it was found at
     * @param location - The location at the given id
     */
    void addLocation(Integer documentId, int location) {
        if (locationsInDocuments.containsKey(documentId)) {
            locationsInDocuments.put(documentId, locationsInDocuments.get(documentId) + "," + location);
            counterInDocuments.put(documentId, counterInDocuments.get(documentId) + 1);
            if (counterInDocuments.get(documentId) > maxAmount) {
                maxAmount = counterInDocuments.get(documentId);
                maxCity = this.cityName;
            }
        } else {
            locationsInDocuments.put(documentId, "" + location);
            counterInDocuments.put(documentId, 1);
        }
    }

    /**
     * Parses the population value to the required standards
     */
    private void parsePopulation() {
        Character remember;
        if (population.length() > 9) {
            double newPopulation = (Double.parseDouble(population)) / 1000000000;
            population = "" + newPopulation;
            remember = 'B';
        } else if (population.length() > 6) {
            double newPopulation = (Double.parseDouble(population)) / 1000000;
            population = "" + newPopulation;
            remember = 'M';
        } else if (population.length() > 3) {
            double newPopulation = (Double.parseDouble(population)) / 1000;
            population = "" + newPopulation;
            remember = 'K';
        } else
            return;
        population += remember;
    }

    /**
     * Sets the info of a not capital city
     * @param data - The city's information
     */
    void setInfoNotCapitalCity(JSONObject data) {
        String info = data.toString();
        String temp = info.substring(info.indexOf("geobytescurrency\":\"") + 19);
        currency = temp.substring(0, temp.indexOf("\""));
        temp = info.substring(info.indexOf("geobytescountry\":\"") + 18);
        countryName = temp.substring(0, temp.indexOf("\""));
        temp = info.substring(info.indexOf("geobytespopulation\":\"") + 21);
        population = temp.substring(0, temp.indexOf("\""));
        parsePopulation();
    }

    /**
     * Tells if the city is capital
     * @return - If the city is capital
     */
    boolean isCapital() {
        return capital;
    }

    /**
     * Returns the city's name
     * @return - The city's name
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Returns the city's currency
     * @return - The city's currency
     */
    String getCurrency() {
        return currency;
    }

    /**
     * Returns all the location the city was found
     * @return - The locations
     */
    HashMap<Integer, String> getLocationsInDocuments() {
        return locationsInDocuments;
    }

    /**
     * Returns the city's country name
     * @return - The country name
     */
    String getCountryName() {
        return countryName;
    }

    /**
     * Sets the city's country name
     * @param countryName - The country name
     */
    void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Sets the city's population
     * @param population - The city's population
     */
    void setPopulation(String population) {
        this.population = population;
    }

    /**
     * Sets the city's currency
     * @param currency - The city's currency
     */
    void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Custom city's toString function which returns its country name, currency and population
     * @return - The city's country name, currency and population
     */
    public String toString() {
        return countryName + "," + currency + "," + population;
    }
}

