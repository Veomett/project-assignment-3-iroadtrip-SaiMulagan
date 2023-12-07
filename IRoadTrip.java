import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
public class IRoadTrip {


    public IRoadTrip (String [] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Three file paths required: borders.txt, capdist.csv, state_name.tsv");
        }
        CountryDataLoader dataLoader = new CountryDataLoader();
        try {
            dataLoader.loadAllData(args[0], args[1], args[2]); // Load data from the files
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.exit(1); // Halts the implementation on failure
        }
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

    private boolean isValidCountry(String start) {
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}

