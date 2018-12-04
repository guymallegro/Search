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
        parsePopulation();
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
        return "population " + population + ", currency " + currency;
    }

    public HashMap<Integer, String> getLocationsInDocuments() {
        return locationsInDocuments;
    }

    private void parsePopulation() {
        System.out.println("Before : " + population);
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
        String[] tempArr = population.split("\\.");
        if (tempArr.length > 1 && tempArr[1].length() > 2) {
            if (Integer.parseInt("" + tempArr[1].charAt(2)) > 5) {
                tempArr[1] = tempArr[1].substring(0, 2);
                tempArr[1] = "" + (Integer.parseInt(tempArr[1]) + 1);

            } else {
                tempArr[1] = tempArr[1].substring(0, 2);
            }
            population=tempArr[0]+"."+tempArr[1];
        }
        population += remember;
        System.out.println("After : " + population);
    }
}
