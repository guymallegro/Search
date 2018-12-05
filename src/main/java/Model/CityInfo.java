package Model;

import org.json.JSONObject;

import java.util.HashMap;

public class CityInfo {
    private String cityName;
    private String countryName;
    private String population;
    private String currency;

    private HashMap<Integer, String> locationsInDocuments;

    public CityInfo(JSONObject data) {
        countryName = data.get("name").toString();
        cityName = data.get("capital").toString();
        population = data.get("population").toString();
        currency = data.getJSONArray("currencies").getJSONObject(0).get("name").toString();
        locationsInDocuments = new HashMap<>();
    }

    public CityInfo(String name) {
        cityName = name;
        locationsInDocuments = new HashMap<>();
    }

    public void addLocation(Integer documentId, int location) {
        if (locationsInDocuments.containsKey(documentId)) {
            locationsInDocuments.put(documentId, locationsInDocuments.get(documentId) + "," + location);
        } else
            locationsInDocuments.put(documentId, "" + location);
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getPopulation() {
        return this.population;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String toString() {
        return countryName + "," + currency + "," + population;
    }

    public HashMap<Integer, String> getLocationsInDocuments() {
        return locationsInDocuments;
    }

}
