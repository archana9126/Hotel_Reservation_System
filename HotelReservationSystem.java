import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "yourpassword";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner in = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4.Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");
                int choice = in.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(connection, in);
                        break;
                    case 2:
                        viewreservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, in);
                        break;
                    case 4:
                        updateReservation(connection,in);
                        break;
                    case 5:
                    deleteReservation(connection,in);
                        break;
                    case 0:
                        Exit();
                        in.close();
                        return;
                    default:
                        System.out.println("Invalid Choice, Try Again.");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
          throw new RuntimeException(e);
        }
    }

     private static void reserveRoom(Connection connection, Scanner in){
         System.out.print("Enter Guest Name: ");
         String guestname = in.next();
         in.nextLine();
         System.out.print("Enter Room Number: ");
         int roomno = in.nextInt();
         System.out.print("Enter Contact Number: ");
         String contactNo = in.next();

         String sql = "INSERT INTO reservation (guest_name, room_name, contact_no)" +
        "VALUES (' "+guestname+ " ',' "+roomno+ " ',' "+contactNo+ " ')";

         try(Statement stat = connection.createStatement()){
             int rs = stat.executeUpdate(sql);

             if(rs>0){
                 System.out.println("Reservation Successful!!");
             }else{
                 System.out.println("Reservation Failed");
             }
         }catch (SQLException e){
             System.out.println(e.getMessage());
         }
    }

    private static void viewreservation(Connection connection) throws SQLException{
        String sql = "SELECT reservation_id, guest_name, room_name, contact_no, reservation_date FROM reservation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_name");
                String contactNumber = resultSet.getString("contact_no");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    private static void getRoomNumber(Connection connection, Scanner in){
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = in.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = in.next();

            String sql = "SELECT room_name FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_name");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

     private static  void updateReservation(Connection connection, Scanner in){
         try {
             System.out.print("Enter reservation ID to update: ");
             int reservationId = in.nextInt();
             in.nextLine(); // Consume the newline character

             if (!reservationExits(connection, reservationId)) {
                 System.out.println("Reservation not found for the given ID.");
                 return;
             }

             System.out.print("Enter new guest name: ");
             String newGuestName = in.nextLine();
             System.out.print("Enter new room number: ");
             int newRoomNumber = in.nextInt();
             System.out.print("Enter new contact number: ");
             String newContactNumber = in.next();

             String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                     "room_name = " + newRoomNumber + ", " +
                     "contact_no = '" + newContactNumber + "' " +
                     "WHERE reservation_id = " + reservationId;

             try (Statement statement = connection.createStatement()) {
                 int affectedRows = statement.executeUpdate(sql);

                 if (affectedRows > 0) {
                     System.out.println("Reservation updated successfully!");
                 } else {
                     System.out.println("Reservation update failed.");
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
     private static void deleteReservation(Connection connection, Scanner in){
        try{
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = in.nextInt();

            if(!reservationExits(connection, reservationId)){
                System.out.print("Reservation not found for the given ID");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try(Statement stat = connection.createStatement()){
                int rs = stat.executeUpdate(sql);

                if(rs>0){
                    System.out.println("Reservation deleted successfully");
                }else{
                    System.out.println("Reservation deletion Failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
     }
      private static boolean reservationExits(Connection connection, int reservationId){
        try{
            String sql ="SELECT reservation_id FROM reservation where reservation_id = " + reservationId;

            try(Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery(sql)){
              return rs.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
          }
      }

      private static void Exit() throws InterruptedException{
          System.out.print("Existing System");
          int i =5;
          while(i!=0){
              System.out.print(".");
              Thread.sleep(450);
              i--;
          }
          System.out.println();
          System.out.println("Thank you for Using Hotel Reservation System");
      }

}
