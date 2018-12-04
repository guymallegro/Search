 package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class CityChecker {
    HashMap<String, CityInfo> cityDictionary;

    public CityChecker(String WebServiceURL, HashMap<String, CityInfo> cityDictionary) {
        this.cityDictionary = cityDictionary;
        HTTPRequest request = new HTTPRequest();
        JSONObject jsonDetails = request.post(WebServiceURL);
        JSONArray result = jsonDetails.getJSONArray("result");

        for (Object obj : result) {
            JSONObject data = (JSONObject) obj;
            CityInfo city = new CityInfo(data);
            cityDictionary.put(city.getCityName(), city);
        }
    }

    public CityInfo getCityInfo(String cityName) {
        if (cityDictionary.containsKey(cityName))
            return cityDictionary.get(cityName);
        else
            return new CityInfo(cityName);
    }
}