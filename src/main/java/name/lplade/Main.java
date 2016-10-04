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
import java.util.InputMismatchException;
import java.util.Scanner;

import static com.google.maps.GeocodingApi.geocode;

public class Main {

    static Scanner stringScanner = new Scanner(System.in);
    static Scanner numberScanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        String key = null;
        //Read key from file
        try (BufferedReader reader = new BufferedReader(new FileReader("API_KEY"))) {
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

        //initialize array which is populated inside while loop
        GeocodingResult[] results;

        //main input loop
        while (true) {

            System.out.println("Enter a place name: ");
            String placeToSearchFor = stringScanner.nextLine();

            results = GeocodingApi.geocode(context, placeToSearchFor).await();

            if (results.length >= 1) {
                System.out.println("I found the following locations:");

                int resultsToDisplay;
                if (results.length > 5) {
                    resultsToDisplay = 5;
                } else {
                    resultsToDisplay = results.length;
                }

                for (int i = 0; i < resultsToDisplay; i++) {
                    System.out.printf("  %d: %s\n", i, results[i].formattedAddress);
                }

                //TODO some kind of smart handling where any input not 1-5 is assumed to be a new query
                System.out.print("Is one of these the location you were looking for? (y/n) ");
                String goodList = stringScanner.nextLine();
                if (goodList.equals("y")) { //TODO better Y/N input handling
                    break; //get out of the while loop and continue
                }
                System.out.println("Try entering more informationy");

            } else {
                //No hits
                System.out.println("Sorry, no results for that place");
            }
        }

        System.out.println("Which number do you want?");
        int choice = intInput();
        LatLng resultLatLng = results[choice].geometry.location;
        System.out.println("Latitude, longitude for that is " + resultLatLng); //TODO split and format to 2 decimals

        ElevationResult[] elResults = ElevationApi.getByPoints(context, resultLatLng).await();

        if (results.length >= 1) {
            //Get first ElevationResult object
            ElevationResult resultElevation = elResults[0];
            //Let's do some rounding
            System.out.printf("The elevation above sea level is %.2f meters.\n", resultElevation.elevation);

        }

        stringScanner.close();
        numberScanner.close();

    }

    private static int intInput() {
        //from lab 3 Validation.java
        while (true) {
            //Try to read what the user typed as an int.
            try {
                // If the input can be read as a int, that int will be returned
                // This ends the loop, and this method, and control returns to the calling method.
                return numberScanner.nextInt();

            } // if the input can't be read as an int, then an error will be raised.
            // For example, if the user enters 'ten' or 1.4 or 123456543454343434, these are not ints, so will cause an error.
            // That error can be 'caught' by this code, and we can print an error message.
            // Since we are inside a while loop, then the loop can repeat and ask the user for input again.
            catch (InputMismatchException ime) {
                System.out.println("Error - please enter an integer number");
                numberScanner.next();   //Clear any other characters from the Scanner
            }
        }
    }
}
