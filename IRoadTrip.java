import java.util.List;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.text.ParseException;

public class IRoadTrip {
    private Set<String> validCountries; // Set containing all valid country names
    private CountryDataLoader dataLoader;

    public IRoadTrip (String [] args) {
        dataLoader = new CountryDataLoader();
        String bordersFilePath = "/Users/sai/Documents/Project3/borders.txt";
        String capDistFilePath = "/Users/sai/Documents/Project3/capdist.csv";
        String stateNameFilePath = "/Users/sai/Documents/Project3/state_name.tsv";

        CountryDataLoader dataLoader = new CountryDataLoader();
        try {
            dataLoader.loadAllData(bordersFilePath, capDistFilePath, stateNameFilePath); // Load data from the files
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); // Halts the implementation on failure
        }
        initializeValidCountries();
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter the name of the first country (type EXIT to quit): ");
            String start = scanner.nextLine();
            if ("EXIT".equalsIgnoreCase(start)) break;

            if (!isValidCountry(start)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            System.out.println("Enter the name of the second country (type EXIT to quit): ");
            String destination = scanner.nextLine();
            if ("EXIT".equalsIgnoreCase(destination)) break;

            if (!isValidCountry(destination)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            List<String> path = findPath(start, destination);
            if (path == null || path.isEmpty()) {
                System.out.println("No valid path found.");
            } else {
                System.out.println("Route from " + start + " to " + destination + ":");
                for (String step : path) {
                    System.out.println("* " + step);
                }
            }
        }
    }

    public boolean isValidCountry(String country) {
        return validCountries.contains(country.trim().toLowerCase());
    }
    private void initializeValidCountries() {
        validCountries = new HashSet<>();

        // Assuming dataLoader has methods to get borders and country codes
        Map<String, List<CountryDataLoader.Border>> bordersMap = dataLoader.getBordersMap();
        Map<String, String> countryCodes = dataLoader.getCountryCodes();

        // Add countries from bordersMap
        validCountries.addAll(bordersMap.keySet());

        // Add countries from countryCodes (if different from bordersMap)
        validCountries.addAll(countryCodes.keySet());

        // Convert all entries to lowercase for case-insensitive matching
        validCountries = validCountries.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}

