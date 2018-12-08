package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * The class which is responsible for getting the information about the cities
 */
class CityChecker {
    Model model;
    private int capital=0;

    /**
     * The CityChecker constructor
     * @param model - The model
     * @param WebServiceURL - The url which will be used for the HTTP request to the API
     * @param cityDictionary - The cities dictionary which will store the information
     */
    CityChecker(Model model, String WebServiceURL, HashMap<String, City> cityDictionary) {
        this.model = model;
        HTTPRequest request = new HTTPRequest();
        JSONObject jsonDetails = request.post(WebServiceURL);
        JSONArray result = jsonDetails.getJSONArray("result");

        for (Object obj : result) {
            JSONObject data = (JSONObject) obj;
            City city = new City(data);
            capital++;
            cityDictionary.put(city.getCityName(), city);
        }
    }

    /**
     * Returns the info of a city
     * @param cityName - The city name
     * @return - The given city info
     */
    City getCityInfo(String cityName) {
        HTTPRequest request = new HTTPRequest();
        JSONObject jsonDetails = request.post("http://getcitydetails.geobytes.com/GetCityDetails?fqcn="+cityName);
        City info = new City(cityName);
        info.setInfoNotCapitalCity(jsonDetails);
        return info;
    }
}