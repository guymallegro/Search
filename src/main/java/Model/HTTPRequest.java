package Model;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HTTPRequest {
    public JSONObject post(String url) {
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
