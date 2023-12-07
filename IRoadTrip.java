import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class IRoadTrip {
    private Set<String> validCountries;
    private CountryDataLoader dataLoader;

    public IRoadTrip(String[] args) {
        dataLoader = new CountryDataLoader();
        String bordersFilePath = "/Users/sai/Documents/Project3/borders.txt";
        String capDistFilePath = "/Users/sai/Documents/Project3/capdist.csv";
        String stateNameFilePath = "/Users/sai/Documents/Project3/state_name.tsv";

        try {
            dataLoader.loadAllData(bordersFilePath, capDistFilePath, stateNameFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        initializeValidCountries();
    }

    public int getDistance(String country1, String country2) {
        country1 = country1.trim().toLowerCase();
        country2 = country2.trim().toLowerCase();

        HashMap<String, Integer> distances = dataLoader.getCapitalDistances().get(country1);
        if (distances != null && distances.containsKey(country2)) {
            return distances.get(country2);
        }
        return -1;
    }

    public List<String> findPath(String startCountry, String endCountry) {
        startCountry = startCountry.trim().toLowerCase();
        endCountry = endCountry.trim().toLowerCase();

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // Initialize distances and predecessors
        for (String country : validCountries) {
            distances.put(country, Integer.MAX_VALUE);
            predecessors.put(country, null);
        }
        distances.put(startCountry, 0);
        queue.add(new Node(startCountry, 0));

        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.country.equals(endCountry)) {
                break; // Reached destination
            }

            int currentDistance = distances.get(current.country);
            for (CountryDataLoader.Border border : dataLoader.getBordersMap().getOrDefault(current.country, new ArrayList<>())) {
                String neighbor = border.getBorderCountry();
                int newDistance = currentDistance + border.getDistance();

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, current.country);
                    queue.add(new Node(neighbor, newDistance));
                }
            }
        }

        // Reconstruct the path
        List<String> path = new ArrayList<>();
        for (String at = endCountry; at != null; at = predecessors.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return path.size() > 1 ? path : null; // Return null if no path found
    }

    private static class Node {
        String country;
        int distance;

        Node(String country, int distance) {
            this.country = country;
            this.distance = distance;
        }
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
        validCountries.addAll(dataLoader.getBordersMap().keySet());
        validCountries.addAll(dataLoader.getCountryCodes().keySet());
        validCountries = validCountries.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }
}
