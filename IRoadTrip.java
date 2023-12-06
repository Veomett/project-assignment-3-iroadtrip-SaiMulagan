import java.util.List;
import java.util.Scanner;

public class IRoadTrip {


    public IRoadTrip (String [] args) {
        // Replace with your code
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
        System.out.println("Enter start country: ");
        String start = scanner.nextLine();
        System.out.println("Enter destination country: ");
        String destination = scanner.nextLine();

        List<String> path = findPath(start, destination);
        int distance = getDistance(start, destination);

        System.out.println("Path: " + path);
        System.out.println("Total distance: " + distance + " km");
    }



    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}

