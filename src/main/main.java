package main;

import config.config;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        config conf = new config();
        conf.connectDB();

        System.out.print("Enter Name: ");
        String name = sc.next();

        System.out.print("Enter E-mail: ");
        String email = sc.next();

        System.out.print("Enter Password: ");
        String pass = sc.next();

        System.out.print("Enter Address: ");
        String address = sc.next();

        System.out.print("Enter Contact: ");
        String contact = sc.next(); 

        String sql = "INSERT INTO tbl_users (a_name, a_email, a_pass, a_address, a_contact) VALUES (?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, pass, address, contact);
    }
}
