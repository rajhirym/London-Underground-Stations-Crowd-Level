import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * This class connects with the Crowding API provided by tfl.
 * For each station, thanks to a naptan Code (which is a code given to each station) it gives the crowd level at that time in a percentage.
 * The percentage is interpreted to be more understandable for the user.
 *
 * @author Rym Rajhi
 */

public class TfLCrowdingAPI {
    public String getCrowdingInfo(String naptanCode) {
        try {
            String apiKey = "7ba89eeefa134e0681549c25190b066d";   // Replace with your actual API key
            
            String urlString = "https://api.tfl.gov.uk/crowding/" + naptanCode + "/Live?app_key=" + apiKey;
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get response code
            int responseCode = connection.getResponseCode();
            
            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }

            // Close connections
            in.close();
            connection.disconnect();

            // Print the result
            String[] contentArray= content.toString().split(",");
            char lastChar = contentArray[1].charAt(contentArray[1].length() - 1);
            double crowdLevel = Integer.parseInt(String.valueOf(lastChar));
            
                if(contentArray[0].substring(17, 22).equals("false"))
                    return "TFL is not providing data at the moment";
            
                else{
                return  """
                %s %s
                %s
                %s
                """.formatted(contentArray[1], crowdingInterpreter(crowdLevel), contentArray[2], contentArray[3].substring(0, 15));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    /**
     * This method interprets the crowd level given by the API and returns a string
     */
    private String crowdingInterpreter(double crowdLevel)
    {   
        if (crowdLevel < 0.3) {
            return "Very quiet";
        } else if (crowdLevel < 0.7) {
            return " Moderately busy";
        } else if (crowdLevel < 1.1) {
            return "Typical crowd level";
        } else {
            return "Very crowded";
        }
    }
}