package sqlconnection;

import java.sql.*;
import java.util.Scanner;

public class dbms {

    // Database connection information
    private static final String URL = "jdbc:mysql://localhost:3306/java_proj";
    private static final String USER = "root";
    private static final String PASSWORD = "passMysql@2005"; // Replace with your MySQL password

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connection successful!");

            while (true) {
                System.out.println("\n--- Intelligent Parking System ---");
                System.out.println("1. Check Parking Spot Availability");
                System.out.println("2. Add New Parking Spot");
                System.out.println("3. Reserve a Parking Spot");
                System.out.println("4. Free a Parking Spot");
                System.out.println("5. View All Parking Spots");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // consume newline

                switch (choice) {
                    case 1:
                        checkParkingAvailability(conn, scanner);
                        break;
                    case 2:
                        addParkingSpot(conn, scanner);
                        break;
                    case 3:
                        reserveParkingSpot(conn, scanner);
                        break;
                    case 4:
                        freeParkingSpot(conn, scanner);
                        break;
                    case 5:
                        viewAllParkingSpots(conn);
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }

    // Feature 1: Check if a parking spot is available
    private static void checkParkingAvailability(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the parking spot location (e.g., A1, B2): ");
        String location = scanner.nextLine();
        String query = "SELECT availability FROM ParkingSpots WHERE location = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, location);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String availability = rs.getString("availability");
                System.out.println("Parking spot " + location + " is " + (availability.equalsIgnoreCase("available") ? "available." : "occupied."));
            } else {
                System.out.println("Parking spot " + location + " does not exist.");
            }
        }
    }

    // Feature 2: Add a new parking spot
    private static void addParkingSpot(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter parking spot location (e.g., A4, B5): ");
        String location = scanner.nextLine();
        System.out.print("Enter spot size (small, medium, large): ");
        String size = scanner.nextLine().toLowerCase();
        String query = "INSERT INTO ParkingSpots (location, size, availability) VALUES (?, ?, 'available')";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, location);
            pstmt.setString(2, size);
            pstmt.executeUpdate();
            System.out.println("Parking spot " + location + " added successfully as " + size + " and available.");
        } catch (SQLException e) {
            System.out.println("Error: Could not add parking spot.");
            e.printStackTrace();
        }
    }

    // Feature 3: Reserve a parking spot
    private static void reserveParkingSpot(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the parking spot location to reserve (e.g., A1, B2): ");
        String location = scanner.nextLine();
        String query = "UPDATE ParkingSpots SET availability = 'occupied' WHERE location = ? AND availability = 'available'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, location);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Parking spot " + location + " has been reserved.");
            } else {
                System.out.println("Parking spot " + location + " is either occupied or does not exist.");
            }
        }
    }

    // Feature 4: Free a parking spot
    private static void freeParkingSpot(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the parking spot location to free (e.g., A1, B2): ");
        String location = scanner.nextLine();
        String query = "UPDATE ParkingSpots SET availability = 'available' WHERE location = ? AND availability = 'occupied'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, location);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Parking spot " + location + " has been freed and is now available.");
            } else {
                System.out.println("Parking spot " + location + " is either already available or does not exist.");
            }
        }
    }

    // Feature 5: View all parking spots and their availability
    private static void viewAllParkingSpots(Connection conn) throws SQLException {
        String query = "SELECT * FROM ParkingSpots";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- Parking Spots ---");
            System.out.printf("%-10s %-10s %-15s\n", "Location", "Size", "Availability");
            System.out.println("---------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s %-10s %-15s\n",
                        rs.getString("location"),
                        rs.getString("size"),
                        rs.getString("availability"));
            }
        }
    }
}
