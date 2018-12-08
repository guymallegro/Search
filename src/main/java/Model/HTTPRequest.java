package Model;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * A class which creates an HTTP request and returns the response as an JSON object
 */
class HTTPRequest {
    /**
     * Created an GET HTTP request and returns the response as an JSON object
     * @param url - The URL to send the request
     * @return - The response as an JSON object
     */
    JSONObject post(String url) {
        String json = "";
        try {
            URL address = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) address.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            json = "{\"result\":";
            Scanner scan = new Scanner(address.openStream());
            while (scan.hasNext())
                json += scan.nextLine();
            scan.close();
            json += "}";
        } catch (Exception e) {
            System.out.println("Failed to send the following request : " + url);
        }
        return new JSONObject(json);
    }
}
