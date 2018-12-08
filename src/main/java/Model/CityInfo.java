package Model;

import org.json.JSONObject;

import java.util.HashMap;

public class CityInfo {
    private String cityName;
    private String countryName;
    private String population;
    private String currency;
    private boolean capital;

    private HashMap<Integer, String> locationsInDocuments;
    public HashMap<Integer, Integer> counterInDocuments = new HashMap<>();
    public static String maxCity = "";
    public static int maxAmount = 0;

    CityInfo(JSONObject data) {
        countryName = data.get("name").toString();
        cityName = data.get("capital").toString();
        population = data.get("population").toString();
        capital = true;
        parsePopulation();
        currency = data.getJSONArray("currencies").getJSONObject(0).get("name").toString();
        locationsInDocuments = new HashMap<>();
    }

    CityInfo(String name) {
        cityName = name;
        capital = false;
        locationsInDocuments = new HashMap<>();
    }

    public String getCountryName() {
        return countryName;
    }

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

    String getCityName() {
        return cityName;
    }

    public String getPopulation() {
        return population;
    }

    String getCurrency() {
        return currency;
    }

    public String toString() {
        return countryName + "," + currency + "," + population;
    }

    HashMap<Integer, String> getLocationsInDocuments() {
        return locationsInDocuments;
    }

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

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    void setPopulation(String population) {
        this.population = population;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    void setInfoNotCapitalCity(JSONObject data) {
        String info = data.toString();
        String temp = info.substring(info.indexOf("geobytescurrency\":\"") + 19);
        currency = temp.substring(0, temp.indexOf("\""));
        temp = info.substring(info.indexOf("geobytescountry\":\"") + 18);
        countryName = temp.substring(0, temp.indexOf("\""));
        temp = info.substring(info.indexOf("geobytespopulation\":\"") + 21);
        population = temp.substring(0, temp.indexOf("\""));
//        startIndex = info.indexOf("geobytescountry\":\"");
//        endIndex = info.indexOf("\",\"geobytesregion");
//        countryName = info.substring(startIndex + 18, endIndex);
//        startIndex = info.indexOf("geobytespopulation\":\"");
//        endIndex = info.indexOf("\",\"geobytesforwarderfor");
//        population = info.substring(startIndex + 21, endIndex);
        parsePopulation();
    }

    public boolean isCapital() {
        return capital;
    }
}
