import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * This class connect with the Stop Points API provided by TFL.
 * For each station it stores the name, the id, the latitude, the longitude.
 * An array list of Station Points stores all this information to be accessed by other classes.
 *
 * @author Rym Rajhi
 */

public class TFLStations {
    private ArrayList<StationPoint> allStationsDetails= new ArrayList<>();
    
    public void populate() {
        try {
            String appId = "Stop Points";
            String appKey = "baf07fc7454446c3940edd5397c646a3";

            String urlString = "https://api.tfl.gov.uk/StopPoint/Mode/tube?app_id=" + appId + "&app_key=" + appKey;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // Reading response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();

            // Parse JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray stations = jsonResponse.getJSONArray("stopPoints");

            // Loop through stations
            for (int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);
                String name = station.getString("commonName");
                String id = station.getString("naptanId");
                double lat = station.getDouble("lat");
                double lon = station.getDouble("lon");
                StationPoint stationDetails = new StationPoint(name,id,lat,lon);
                allStationsDetails.add(stationDetails);

                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList getAllStatiosnDetails()
    {   populate();
        return allStationsDetails;
    }
}