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
        dataLoader.printBordersMap();
    }

    public int getDistance(String country1, String country2) {
        country1 = country1.trim().toLowerCase();
        country2 = country2.trim().toLowerCase();

        HashMap<String, Integer> distances = dataLoader.getCapitalDistances().get(country1);
        if (distances != null && distances.containsKey(country2)) {
            int distance = distances.get(country2);
            //System.out.println("Distance between " + country1 + " and " + country2 + ": " + distance);
            return distance;
        } else {
            //System.out.println("No direct distance found between " + country1 + " and " + country2);
            return -1;
        }
    }

    public List<String> findPath(String country1, String country2) {
        country1 = country1.trim().toLowerCase();
        country2 = country2.trim().toLowerCase();

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        for (String country : validCountries) {
            distances.put(country, Integer.MAX_VALUE);
            predecessors.put(country, null);
        }

        distances.put(country1, 0);
        queue.add(new Node(country1, 0));
        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (visited.contains(current.countryName)) continue; // Skip already visited countries
            visited.add(current.countryName);

            List<CountryDataLoader.Border> neighbors = dataLoader.getBordersMap().get(current.countryName);
            if (neighbors == null || neighbors.isEmpty()) {
                System.out.println("No neighbors found or all neighbors already visited for " + current.countryName);
                continue;
            }

            for (CountryDataLoader.Border border : neighbors) {
                String neighbor = border.getBorderCountry().toLowerCase();
                if (visited.contains(neighbor)) continue;}

            for (CountryDataLoader.Border border : neighbors) {
                String neighbor = border.getBorderCountry().toLowerCase(); // Convert to lowercase
                System.out.println("Checking neighbor: " + neighbor + " of " + current.countryName);
                if (!validCountries.contains(neighbor)) {
                    System.out.println("Neighbor " + neighbor + " not in valid countries list");
                    continue;
                }

                int newDist = current.distance + border.getDistance();
                System.out.println("Neighbor: " + neighbor + ", Current Distance: " + distances.get(neighbor) + ", New Distance: " + newDist);

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current.countryName);
                    queue.add(new Node(neighbor, newDist));
                    System.out.println("Updated distance for " + neighbor + ". New predecessor: " + current.countryName);
                }
            }
        }

        return reconstructPath(predecessors, distances, country1, country2);
    }

    private List<String> reconstructPath(Map<String, String> predecessors, Map<String, Integer> distances, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        String at = end;

        while (at != null && !at.equals(start)) {
            path.addFirst(at);
            at = predecessors.get(at);
        }

        if (at != null && at.equals(start)) {
            path.addFirst(start);
            // Build path string with distances
            List<String> fullPath = new ArrayList<>();
            String prev = null;
            for (String country : path) {
                if (prev != null) {
                    int distance = distances.get(country) - distances.get(prev);
                    fullPath.add(prev + " --> " + country + " (" + distance + " km.)");
                }
                prev = country;
            }
            return fullPath;
        }

        return Collections.emptyList(); // No path found
    }

    private static class Node {
        String countryName;
        int distance;

        Node(String countryName, int distance) {
            this.countryName = countryName;
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
