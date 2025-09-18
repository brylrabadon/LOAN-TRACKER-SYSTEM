package main;

import config.config;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String resp;
        do {
            System.out.println("\n=== LOAN TRACKER MENU ===");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. EXIT");

            System.out.print("Enter Action: ");
            int action = sc.nextInt();
            Main app = new Main();

            switch(action){
                case 1:
                    app.addUser();
                break;
                case 2:
                    app.viewUsers();
                break;
                case 3:
                    app.viewUsers();
                    app.updateUser();
                break;
                case 4:
                    app.viewUsers();
                    app.deleteUser();
                    app.viewUsers();
                break;
                case 5:
                    System.out.println("Thank You!");
                break;
                default:
                    System.out.println("Invalid Option!");
                break;
            }

            if (action == 5) break;

            System.out.print("Continue? ");
            resp = sc.next();
        } while(resp.equalsIgnoreCase("yes"));
    }

    public void addUser() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Name: "); 
        String name = sc.nextLine();
        System.out.print("Email: "); 
        String email = sc.nextLine();
        System.out.print("Password: "); 
        String pass = sc.nextLine();
        System.out.print("Address: "); 
        String address = sc.nextLine();
        System.out.print("Contact: "); 
        String contact = sc.nextLine();
        System.out.print("Role (Admin/Customer): "); 
        String role = sc.nextLine();

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_pass, u_address, u_contact, u_role) VALUES (?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, pass, address, contact, role);
    }

    public void viewUsers() {
        String qry = "SELECT * FROM tbl_users";
        String[] hdrs = {"ID", "Name", "Email", "Password", "Address", "Contact", "Role"};
        String[] clms = {"u_id", "u_name", "u_email", "u_pass", "u_address", "u_contact", "u_role"};

        config conf = new config();
        conf.viewRecords(qry, hdrs, clms);
    }

    public void updateUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter User ID to Update: ");
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("New Name: "); 
        String nname = sc.nextLine();
        System.out.print("New Email: "); 
        String nemail = sc.nextLine();
        System.out.print("New Password: "); 
        String npass = sc.nextLine();
        System.out.print("New Address: "); 
        String naddress = sc.nextLine();
        System.out.print("New Contact: "); 
        String ncontact = sc.nextLine();
        System.out.print("New Role: "); 
        String nrole = sc.nextLine();

        String qry = "UPDATE tbl_users SET u_name=?, u_email=?, u_pass=?, u_address=?, u_contact=?, u_role=? WHERE u_id=?";
        config conf = new config();
        conf.updateRecord(qry, nname, nemail, npass, naddress, ncontact, nrole, id);
    }

    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();

        String qry = "DELETE FROM tbl_users WHERE u_id=?";
        config conf = new config();
        conf.deleteRecord(qry, id);
    }
}