package main;

import config.config;
import java.util.Scanner;

public class Loansystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final config conf = new config();

    public static void viewUsers() {
        System.out.println("\n--- VIEW ALL USERS ---");
        String query = "SELECT u_id, u_name, u_email, u_type, u_status, u_contact, u_address FROM tbl_users";
        String[] headers = {"ID", "Name", "Email", "Type", "Status", "Contact", "Address"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status", "u_contact", "u_address"};
        conf.viewRecords(query, headers, columns);
    }

    public static void adminDashboard() {
        int adminChoice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. View All Users");
            System.out.println("2. Approve / Reject User Accounts");
            System.out.println("3. View All Loans");
            System.out.println("4. Approve / Reject Loan Requests");
            System.out.println("5. Record Loan Payments");
            System.out.println("6. View All Payment History");
            System.out.println("7. Approve / Reject Collateral");
            System.out.println("8. View Collaterals");
            System.out.println("9. Audit Borrower Portfolio (Users, Loans, Payments, Collaterals)");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");
            adminChoice = sc.nextInt();

            switch (adminChoice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    handleAccountApproval();
                    break;
                case 3:
                    viewAllLoans();
                    break;
                case 4:
                    handleLoanApproval();
                    break;
                case 5:
                    recordLoanPayment();
                    break;
                case 6:
                    viewAllPaymentHistory();
                    break;
                case 7:
                    viewAllCollaterals();
                    handleCollateralApproval();
                    break;
                case 8:
                    viewAllCollaterals();
                    break;
                case 9:
                    auditBorrowerPortfolio();
                    break;
                case 10:
                    System.out.println("Logging out from Admin Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (adminChoice != 10);
    }

    private static void handleLoanApproval() {
        String query = "SELECT l_id, u_id, l_amount, l_interest_rate, l_status FROM tbl_loans WHERE l_status = 'Pending'";
        String[] headers = {"Loan ID", "User ID", "Amount", "Interest Rate", "Status"};
        String[] columns = {"l_id", "u_id", "l_amount", "l_interest_rate", "l_status"};
        conf.viewRecords(query, headers, columns);

        System.out.print("Enter Loan ID to approve/reject: ");
        int loanId = sc.nextInt();
        System.out.print("Enter new status (Approved/Rejected): ");
        String status = sc.next();

        String sql;
        // The SQL is simplified since l_date_approved is removed
        sql = "UPDATE tbl_loans SET l_status = ? WHERE l_id = ?";

        conf.updateRecord(sql, status, loanId);
        System.out.println("Loan " + loanId + " updated to " + status + ".");
    }

    private static void viewAllLoans() {
        // l_date_approved removed from SELECT
        String query = "SELECT l_id, u_id, l_amount, l_interest_rate, l_status, l_date_applied FROM tbl_loans";
        String[] headers = {"Loan ID", "User ID", "Amount", "Interest Rate", "Status", "Date Applied"};
        String[] columns = {"l_id", "u_id", "l_amount", "l_interest_rate", "l_status", "l_date_applied"};
        conf.viewRecords(query, headers, columns);
    }

    private static void recordLoanPayment() {
        System.out.print("Enter Loan ID: ");
        int loanId = sc.nextInt();
        System.out.print("Enter Payment Amount: ");
        double amount = sc.nextDouble();

        String sql = "INSERT INTO tbl_payments (l_id, p_amount, p_date) VALUES (?, ?, date('now'))";
        conf.addRecord(sql, loanId, amount);
        System.out.println("Payment recorded successfully.");
    }

    private static void viewAllPaymentHistory() {
        String query = "SELECT p_id, l_id, p_amount, p_date FROM tbl_payments";
        String[] headers = {"Payment ID", "Loan ID", "Amount", "Date"};
        String[] columns = {"p_id", "l_id", "p_amount", "p_date"};
        conf.viewRecords(query, headers, columns);
    }

    private static void handleCollateralApproval() {
        System.out.print("Enter Collateral ID to approve/reject: ");
        int colId = sc.nextInt();
        System.out.print("Enter new status (Approved/Rejected): ");
        String status = sc.next();

        String query = "SELECT c_id, l_id, c_type, c_description, c_value, c_status FROM tbl_collaterals WHERE c_status = 'Pending'";
        String[] headers = {"Collateral ID", "Loan ID", "Type", "Description", "Value", "Status"};
        String[] columns = {"c_id", "l_id", "c_type", "c_description", "c_value", "c_status"};
        conf.viewRecords(query, headers, columns);

        String sql = "UPDATE tbl_collaterals SET c_status = ? WHERE c_id = ?";
        conf.updateRecord(sql, status, colId);

        System.out.println("Collateral " + colId + " updated to " + status + ".");
    }


    private static void viewAllCollaterals() {
        String query = "SELECT c_id, l_id, c_type, c_description, c_value, c_status, c_date_added FROM tbl_collaterals";
        String[] headers = {"Collateral ID", "Loan ID", "Type", "Description", "Value", "Status", "Date Added"};
        String[] columns = {"c_id", "l_id", "c_type", "c_description", "c_value", "c_status", "c_date_added"};
        conf.viewRecords(query, headers, columns);
    }

    private static void auditBorrowerPortfolio() {
        System.out.println("\n--- AUDIT BORROWER PORTFOLIO ---");

        String query = "SELECT u_id, u_name, u_email FROM tbl_users WHERE u_type = 'Borrower' AND u_status = 'Approved'";
        String[] headers = {"User ID", "Name", "Email"};
        String[] columns = {"u_id", "u_name", "u_email"};
        conf.viewRecords(query, headers, columns);

        System.out.print("Enter Borrower User ID to audit: ");
        int borrowerId = sc.nextInt();

        System.out.println("\n--- LOANS FOR USER ID " + borrowerId + " ---");
        viewMyLoans(borrowerId);

        System.out.println("\n--- PAYMENT HISTORY FOR USER ID " + borrowerId + " ---");
        viewPaymentHistory(borrowerId);

        System.out.println("\n--- COLLATERALS FOR USER ID " + borrowerId + " ---");
        viewMyCollaterals(borrowerId);
    }


    public static void borrowerDashboard(int userId) {
        int borrowerChoice;
        do {
            System.out.println("\n===== BORROWER DASHBOARD =====");
            System.out.println("1. Apply for a Loan");
            System.out.println("2. View My Loans");
            System.out.println("3. Make a Payment");
            System.out.println("4. View My Payment History");
            System.out.println("5. Add Collateral");
            System.out.println("6. VIew My Collateral");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            borrowerChoice = sc.nextInt();

            switch (borrowerChoice) {
                case 1:
                    applyForLoan(userId);
                    break;
                case 2:
                    viewMyLoans(userId);
                    break;
                case 3:
                    makePayment(userId);
                    break;
                case 4:
                    viewPaymentHistory(userId);
                    break;
                case 5:
                    addCollateral(userId);
                    break;
                case 6:
                    viewMyCollaterals(userId);
                    break;
                case 7:
                    System.out.println("Logging out from Borrower Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (borrowerChoice != 7);
    }

    private static void applyForLoan(int userId) {
        System.out.println("\n--- APPLY FOR A LOAN ---");
        sc.nextLine();

        System.out.print("Enter Loan Amount: ");
        double amount = sc.nextDouble();
        System.out.print("Enter Interest Rate: ");
        double interest = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Loan Purpose: ");
        String purpose = sc.nextLine();
        System.out.print("Enter Date Applied (MM-DD-YYYY): ");
        String dateApplied = sc.nextLine();

        // l_date_approved is no longer included in the INSERT statement
        String sql = "INSERT INTO tbl_loans (u_id, l_amount, l_interest_rate, l_purpose, l_status, l_date_applied) VALUES (?, ?, ?, ?, 'Pending', ?)";
        conf.addRecord(sql, userId, amount, interest, purpose, dateApplied);
        System.out.println("Loan application submitted successfully. Please wait for admin approval.");
    }

    private static void makePayment(int userId) {
        String query = "SELECT l_id, l_amount, l_interest_rate, l_status FROM tbl_loans WHERE u_id = ? AND l_status = 'Approved'";
        String[] headers = {"Loan ID", "Amount", "Interest Rate", "Status"};
        String[] columns = {"l_id", "l_amount", "l_interest_rate", "l_status"};
        conf.viewRecords(query, headers, columns, userId);

        System.out.print("Enter Loan ID to pay: ");
        int loanId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Payment Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Interest Paid: ");
        double interestPaid = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Remaining Balance: ");
        double remainingBalance = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Payment Method (e.g., Cash, GCash, Bank): ");
        String method = sc.nextLine();

        String sql = "INSERT INTO tbl_payments (l_id, p_amount, p_date, p_interest_paid, p_remaining_balance, p_method) VALUES (?, ?, date('now'), ?, ?, ?)";
        conf.addRecord(sql, loanId, amount, interestPaid, remainingBalance, method);
        System.out.println("\n✅ Payment of ₱" + amount + " recorded successfully.");
    }

    // --- VIEW LOANS ---
    private static void viewMyLoans(int userId) {
        // l_date_approved removed from SELECT
        String query = "SELECT l_id, l_amount, l_interest_rate, l_status, l_date_applied FROM tbl_loans WHERE u_id = ?";
        String[] headers = {"Loan ID", "Amount", "Interest Rate", "Status", "Date Applied"};
        String[] columns = {"l_id", "l_amount", "l_interest_rate", "l_status", "l_date_applied"};
        conf.viewRecords(query, headers, columns, userId);
    }

    private static void viewPaymentHistory(int userId) {
        String query = "SELECT p.p_date, p.p_amount, p.p_interest_paid, p.p_remaining_balance, p.p_method, l.l_id FROM tbl_payments p JOIN tbl_loans l ON p.l_id = l.l_id WHERE l.u_id = ? ORDER BY p.p_date DESC";
        String[] headers = {"Date", "Amount Paid", "Interest Paid", "Remaining Balance", "Method", "Loan ID"};
        String[] columns = {"p_date", "p_amount", "p_interest_paid", "p_remaining_balance", "p_method", "l_id"};
        conf.viewRecords(query, headers, columns, userId);
    }

    private static void addCollateral(int userId) {
        System.out.println("\n--- ADD COLLATERAL ---");

        viewMyLoans(userId);

        System.out.print("Enter Loan ID to attach collateral: ");
        int loanId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Collateral Type (e.g., Car, Land, Jewelry): ");
        String type = sc.nextLine();
        System.out.print("Enter Collateral Description: ");
        String description = sc.nextLine();
        System.out.print("Enter Collateral Estimated Value: ");
        double value = sc.nextDouble();

        // SQL updated to include c_status='Pending' and c_date_add=date('now')
        String sql = "INSERT INTO tbl_collaterals (l_id, c_type, c_description, c_value, c_status, c_date_added) VALUES (?, ?, ?, ?, 'Pending', date('now'))";
        conf.addRecord(sql, loanId, type, description, value);

        System.out.println("\n✅ Collateral added successfully and linked to Loan ID " + loanId + ". Awaiting admin approval.");
    }

    private static void viewMyCollaterals(int userId) {
        String query = "SELECT c_id, l_id, c_type, c_description, c_value, c_status, c_date_added " +
                        "FROM tbl_collaterals WHERE l_id IN (SELECT l_id FROM tbl_loans WHERE u_id = ?)";
        String[] headers = {"Collateral ID", "Loan ID", "Type", "Description", "Value", "Status", "Date Added"};
        String[] columns = {"c_id", "l_id", "c_type", "c_description", "c_value", "c_status", "c_date_added"};
        conf.viewRecords(query, headers, columns, userId);
    }


    public static void main(String[] args) {
        conf.connectDB();
        int choice;
        char cont;

        do {
            System.out.println("===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    System.out.println("Exiting Loan System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue? (Y/N): ");
            cont = sc.next().charAt(0);
        } while (cont == 'Y' || cont == 'y');
    }

    private static void handleLogin() {
        System.out.print("Enter email: ");
        String em = sc.next();
        System.out.print("Enter password: ");
        String pas = sc.next();

        String hashedPass = conf.hashPassword(pas);

        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
        java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, em, hashedPass);

        if (result.isEmpty()) {
            System.out.println("INVALID CREDENTIALS");
        } else {
            java.util.Map<String, Object> user = result.get(0);
            String stat = user.get("u_status").toString();
            String type = user.get("u_type").toString();
            int userId = ((Number) user.get("u_id")).intValue();

            if (stat.equals("Pending")) {
                System.out.println("Account pending approval.");
            } else if (stat.equals("Rejected")) {
                System.out.println("Account rejected.");
            } else if (type.equals("Admin")) {
                adminDashboard();
            } else {
                borrowerDashboard(userId);
            }
        }
    }

    private static void handleRegistration() {
        System.out.println("\n--- USER REGISTRATION ---");
        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Contact: ");
        String contact = sc.next();
        sc.nextLine();
        System.out.print("Enter Address: ");
        String address = sc.nextLine();

        System.out.print("Enter Type (1 - Admin, 2 - Borrower): ");
        int typeChoice = sc.nextInt();
        String type = (typeChoice == 1) ? "Admin" : "Borrower";

        System.out.print("Enter Password: ");
        String pass = sc.next();

        String hashedPass = conf.hashPassword(pass);

        String sql = "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass, u_contact, u_address) VALUES (?, ?, ?, 'Pending', ?, ?, ?)";
        conf.addRecord(sql, name, email, type, hashedPass, contact, address);
        System.out.println("Registration successful! Await admin approval.");

    }

    private static void handleAccountApproval() {
        System.out.println("\n--- APPROVE / REJECT USER ACCOUNT ---");
        String query = "SELECT u_id, u_name, u_email, u_status FROM tbl_users WHERE u_status = 'Pending'";
        String[] headers = {"ID", "Name", "Email", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_status"};
        conf.viewRecords(query, headers, columns);

        System.out.print("Enter User ID to approve/reject: ");
        int userId = sc.nextInt();
        System.out.print("Enter new status (Approved/Rejected): ");
        String status = sc.next();
        String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
        conf.updateRecord(sql, status, userId);
        System.out.println("User ID " + userId + " is now " + status + ".");
    }
}