import java.io.*;
import java.util.*;

// --- Meal class to handle food orders ---
class Meal implements Serializable {
    int itemId;
    int quantity;
    float totalPrice;

    Meal(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
        switch (itemId) {
            case 1 -> totalPrice = quantity * 45;
            case 2 -> totalPrice = quantity * 55;
            case 3 -> totalPrice = quantity * 65;
            case 4 -> totalPrice = quantity * 25;
            default -> totalPrice = 0;
        }
    }
}

// --- Single Room with one guest ---
class SingleOccupancyRoom implements Serializable {
    String guestName;
    String contactNumber;
    String gender;
    ArrayList<Meal> mealOrders = new ArrayList<>();

    SingleOccupancyRoom(String name, String contact, String gender) {
        this.guestName = name;
        this.contactNumber = contact;
        this.gender = gender;
    }
}

// --- Double Room with two guests ---
class DoubleOccupancyRoom extends SingleOccupancyRoom {
    String guestName2;
    String contactNumber2;
    String gender2;

    DoubleOccupancyRoom(String name1, String contact1, String gender1,
                        String name2, String contact2, String gender2) {
        super(name1, contact1, gender1);
        this.guestName2 = name2;
        this.contactNumber2 = contact2;
        this.gender2 = gender2;
    }
}

// --- Custom Exception when room is not available ---
class RoomUnavailableException extends Exception {
    public String toString() {
        return "Sorry! Room is already booked.";
    }
}

// --- Registry to hold all room types ---
class RoomRegistry implements Serializable {
    DoubleOccupancyRoom[] executiveDoubles = new DoubleOccupancyRoom[10];
    DoubleOccupancyRoom[] deluxeDoubles = new DoubleOccupancyRoom[20];
    SingleOccupancyRoom[] executiveSingles = new SingleOccupancyRoom[10];
    SingleOccupancyRoom[] deluxeSingles = new SingleOccupancyRoom[20];
}

// --- Hotel Manager with operations ---
class HotelManager {
    static RoomRegistry registry = new RoomRegistry();
    static Scanner scanner = new Scanner(System.in);

    static void saveData() throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("roomdata.ser"))) {
            oos.writeObject(registry);
        }
    }

    static void loadData() throws Exception {
        File file = new File("roomdata.ser");
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            registry = (RoomRegistry) ois.readObject();
        }
    }

    static void showFeatures(int type) {
        System.out.println("\n--- Room Features ---");
        switch (type) {
            case 1 -> System.out.println("Executive Double Room: AC, King Bed, ₹4000/day");
            case 2 -> System.out.println("Deluxe Double Room: Non-AC, Queen Bed, ₹3000/day");
            case 3 -> System.out.println("Executive Single Room: AC, Single Bed, ₹2000/day");
            case 4 -> System.out.println("Deluxe Single Room: Non-AC, Single Bed, ₹1500/day");
        }
    }

    static void showAvailability(int type) {
        System.out.println("\n--- Room Availability ---");
        Object[] rooms = switch (type) {
            case 1 -> registry.executiveDoubles;
            case 2 -> registry.deluxeDoubles;
            case 3 -> registry.executiveSingles;
            case 4 -> registry.deluxeSingles;
            default -> null;
        };

        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null)
                System.out.println("Room " + (i + 1) + " is Available");
            else
                System.out.println("Room " + (i + 1) + " is Occupied");
        }
    }

    static void bookRoom(int type) throws RoomUnavailableException {
        System.out.print("Enter room number to book (1 to " +
                (switch (type) {
                    case 1 -> 10;
                    case 2 -> 20;
                    case 3 -> 10;
                    case 4 -> 20;
                    default -> 0;
                }) + "): ");
        int roomNo = scanner.nextInt() - 1;

        Object[] rooms = switch (type) {
            case 1 -> registry.executiveDoubles;
            case 2 -> registry.deluxeDoubles;
            case 3 -> registry.executiveSingles;
            case 4 -> registry.deluxeSingles;
            default -> null;
        };

        if (rooms[roomNo] != null) throw new RoomUnavailableException();

        System.out.print("Enter guest name: ");
        String name = scanner.next();
        System.out.print("Enter contact number: ");
        String contact = scanner.next();
        System.out.print("Enter gender: ");
        String gender = scanner.next();

        if (type == 1 || type == 2) {
            System.out.print("Enter second guest name: ");
            String name2 = scanner.next();
            System.out.print("Enter second contact number: ");
            String contact2 = scanner.next();
            System.out.print("Enter gender: ");
            String gender2 = scanner.next();
            rooms[roomNo] = new DoubleOccupancyRoom(name, contact, gender, name2, contact2, gender2);
        } else {
            rooms[roomNo] = new SingleOccupancyRoom(name, contact, gender);
        }

        System.out.println("Room booked successfully.");
    }

    static void orderMeal(int type) {
        System.out.print("Enter room number: ");
        int roomNo = scanner.nextInt() - 1;

        SingleOccupancyRoom room = switch (type) {
            case 1 -> registry.executiveDoubles[roomNo];
            case 2 -> registry.deluxeDoubles[roomNo];
            case 3 -> registry.executiveSingles[roomNo];
            case 4 -> registry.deluxeSingles[roomNo];
            default -> null;
        };

        if (room == null) {
            System.out.println("Room is not booked.");
            return;
        }

        System.out.println("1. Pizza - ₹45");
        System.out.println("2. Burger - ₹55");
        System.out.println("3. Pasta - ₹65");
        System.out.println("4. Juice - ₹25");

        System.out.print("Enter item number: ");
        int item = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int qty = scanner.nextInt();

        room.mealOrders.add(new Meal(item, qty));
        System.out.println("Meal ordered successfully.");
    }

    static void generateBill(int type) {
        System.out.print("Enter room number: ");
        int roomNo = scanner.nextInt() - 1;

        SingleOccupancyRoom room = switch (type) {
            case 1 -> registry.executiveDoubles[roomNo];
            case 2 -> registry.deluxeDoubles[roomNo];
            case 3 -> registry.executiveSingles[roomNo];
            case 4 -> registry.deluxeSingles[roomNo];
            default -> null;
        };

        if (room == null) {
            System.out.println("Room is not booked.");
            return;
        }

        int roomCost = switch (type) {
            case 1 -> 4000;
            case 2 -> 3000;
            case 3 -> 2000;
            case 4 -> 1500;
            default -> 0;
        };

        float mealTotal = 0;
        for (Meal m : room.mealOrders) mealTotal += m.totalPrice;

        System.out.println("Room Rent: ₹" + roomCost);
        System.out.println("Meal Charges: ₹" + mealTotal);
        System.out.println("Total Bill: ₹" + (roomCost + mealTotal));
    }

    static void cancelBooking(int type) {
        System.out.print("Enter room number to cancel: ");
        int roomNo = scanner.nextInt() - 1;

        switch (type) {
            case 1 -> registry.executiveDoubles[roomNo] = null;
            case 2 -> registry.deluxeDoubles[roomNo] = null;
            case 3 -> registry.executiveSingles[roomNo] = null;
            case 4 -> registry.deluxeSingles[roomNo] = null;
        }

        System.out.println("Booking cancelled.");
    }
}

// --- Main Program Entry Point ---
public class Main {
    public static void main(String[] args) throws Exception {
        HotelManager.loadData();
        Scanner input = new Scanner(System.in);
        int choice;

        do {
            System.out.println("""
                    \n===== HOTEL MANAGEMENT MENU =====
                    1. Book a Room
                    2. Check Room Features
                    3. Check Room Availability
                    4. Order Meal
                    5. Generate Bill
                    6. Cancel Booking
                    7. Exit""");

            choice = input.nextInt();

            if (choice >= 1 && choice <= 6) {
                System.out.println("""
                        Select Room Type:
                        1. Executive Double
                        2. Deluxe Double
                        3. Executive Single
                        4. Deluxe Single""");
                int roomType = input.nextInt();

                try {
                    switch (choice) {
                        case 1 -> HotelManager.bookRoom(roomType);
                        case 2 -> HotelManager.showFeatures(roomType);
                        case 3 -> HotelManager.showAvailability(roomType);
                        case 4 -> HotelManager.orderMeal(roomType);
                        case 5 -> HotelManager.generateBill(roomType);
                        case 6 -> HotelManager.cancelBooking(roomType);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } while (choice != 7);

        HotelManager.saveData();
        System.out.println("Thank you for using the system!");
    }
}
