 package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CityChecker {
    HashMap<String, CityInfo> cityDictionary;
    ArrayList<String> countires;

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
        else {
            HTTPRequest request = new HTTPRequest();
            JSONObject jsonDetails = request.post("http://getcitydetails.geobytes.com/GetCityDetails?fqcn="+cityName);
            CityInfo info = new CityInfo(cityName);
            info.setInfoNotCapitalCity(jsonDetails);
            return info;
        }
    }

    public int amountOfCountries(){
        return 0;
    }
}