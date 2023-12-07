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

    public List<String> findPath(String country1, String country2) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String country : validCountries) {
            distances.put(country, Integer.MAX_VALUE);
            predecessors.put(country, null);
        }

        distances.put(country1, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        queue.add(new Node(country1, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (!visited.add(current.countryName)) {
                continue;
            }

            if (current.countryName.equals(country2)) {
                break; // Destination reached
            }

            for (CountryDataLoader.Border border : dataLoader.getBordersMap().getOrDefault(current.countryName, Collections.emptyList())) {
                String neighbor = border.getBorderCountry();
                int newDist = current.distance + border.getDistance();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current.countryName);
                    queue.add(new Node(neighbor, newDist));
                }
            }
        }

        return reconstructPath(predecessors, country1, country2);
    }

    private List<String> reconstructPath(Map<String, String> predecessors, String start, String end) {
        LinkedList<String> path = new LinkedList<>();
        for (String at = end; at != null; at = predecessors.get(at)) {
            path.addFirst(at);
        }
        if (path.getFirst().equals(start)) {
            return path;
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
