package Model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CityChecker {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String url = "https://restcountries.eu/rest/v2/capital/";
    private static final String filters = "?fields=name;currencies;population";

    public HashMap<String, String> findCityInformation(String city) {
        HashMap<String, String> info = new HashMap<>();
        try {
            URL obj = new URL(url + city + filters);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                int startTagIndex = response.toString().indexOf("\",\"name\":\"");
                int endTagIndex = response.toString().indexOf("\",\"symbol");
                info.put("currency", response.toString().substring(startTagIndex + 10, endTagIndex));
                startTagIndex = response.toString().indexOf("}],\"name\":\"");
                endTagIndex = response.toString().indexOf("\",\"po");
                info.put("country", response.toString().substring(startTagIndex + 11, endTagIndex));
                startTagIndex = response.toString().indexOf("tion\":");
                endTagIndex = response.toString().indexOf("0}]");
                info.put("population", response.toString().substring(startTagIndex + 6, endTagIndex + 1));
            } else {
                System.out.println("The request have failed.");
            }
        } catch (Exception e) {
            System.out.println("Filed to get city information for :" + city);
        }
        return info;
    }
}
