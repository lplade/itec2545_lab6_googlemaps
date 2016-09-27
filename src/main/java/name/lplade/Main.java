package name.lplade;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

import static com.google.maps.GeocodingApi.geocode;

public class Main {

    static Scanner stringScanner = new Scanner(System.in);
    static Scanner numberScanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception{
        String key = null;
        //Read key from file
        try (BufferedReader reader = new BufferedReader(new FileReader("API_KEY"))){
            key = reader.readLine();
            //System.out.println(key); //just checking...
        } catch (Exception ioe) {
            System.out.println("No key file found, or could not read key. Please verify elevation_key is present");
            System.exit(-1); //quit program, need to fix before continuing
        }


        GeoApiContext context = new GeoApiContext().setApiKey(key);

/*        LatLng mctcLatLng = new LatLng(44.97074, -93.283356);

        ElevationResult[] results = ElevationApi.getByPoints(context, mctcLatLng).await();

        if (results.length >=1) {
            //Get first ElevationResult object
            ElevationResult mctcElevation = results[0];
            System.out.println("The elevation of MCTC above sea level is " + mctcElevation.elevation + " meters");
            //Let's do some rounding
            System.out.printf("The elevation of MCTC above sea level is %.2f meters.\n", mctcElevation.elevation);

        }*/

        System.out.println("Enter a place name:");
        String placeToSearchFor = stringScanner.nextLine();

        GeocodingResult[] results = GeocodingApi.geocode(context, placeToSearchFor).await();

        if (results.length >= 1) {
            System.out.println("I found the following locations:");

            int resultsToDisplay;
            if (results.length > 5) {
                resultsToDisplay = 5;
            } else {
                resultsToDisplay = results.length;
            }

            for (int i=0; i < resultsToDisplay; i++) {
                System.out.printf("  %d: %s\n", i, results[i].formattedAddress);
            }

            System.out.println("Which number do you want?");
            int choice = numberScanner.nextInt(); //TODO validate valid number
            LatLng resultLatLng = results[choice].geometry.location;
            System.out.println("Latitude, longitude for that is "+ resultLatLng);

            ElevationResult[] elResults = ElevationApi.getByPoints(context, resultLatLng).await();

            if (results.length >=1) {
                //Get first ElevationResult object
                ElevationResult resultElevation = elResults[0];
                //Let's do some rounding
                System.out.printf("The elevation above sea level is %.2f meters.\n", resultElevation.elevation);

            }

        } else {

            System.out.println("Sorry, no results for that place");
        }

        stringScanner.close();
        numberScanner.close();

    }
}
