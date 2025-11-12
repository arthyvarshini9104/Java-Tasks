package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    selectUser(scanner);
                    break;
                case 2:
                    createUser(scanner);
                    break;
                case 3:
                    createGroup(scanner);
                    break;
                case 4:
                    addUserToGroup(scanner);
                    break;
                case 5:
                    addExpense(scanner);
                    break;
                case 6:
                    viewMyGroups();
                    break;
                case 7:
                    viewGroupBalances(scanner);
                    break;
                case 8:
                    settleUp(scanner);
                    break;
                case 9:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    static void printMenu() {
        System.out.println("Expense Sharing App:");
        System.out.println("1. Select User");
        System.out.println("2. Create User");
        System.out.println("3. Create Group");
        System.out.println("4. Add User to Group");
        System.out.println("5. Add Expense");
        System.out.println("6. View My Groups");
        System.out.println("7. View Group Balances");
        System.out.println("8. Settle Up");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }

    static int activeUserId = -1;

    static void selectUser(Scanner sc) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, username, name FROM users");
             ResultSet rs = ps.executeQuery()) {
            System.out.println("List of users:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                System.out.println(id + ": " + username + " (" + name + ")");
            }
            while (true) {
                System.out.print("Enter user ID to select: ");
                int selectedId = sc.nextInt();
                sc.nextLine();
                if (selectedId == activeUserId) {
                    System.out.println("User already selected. Please select a different user.");
                    continue;
                }
                try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE id = ?")) {
                    check.setInt(1, selectedId);
                    ResultSet rsCheck = check.executeQuery();
                    rsCheck.next();
                    if (rsCheck.getInt(1) == 1) {
                        activeUserId = selectedId;
                        System.out.println("User selected successfully, ID: " + activeUserId);
                        break;
                    } else {
                        System.out.println("Invalid user ID. Try again.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void createUser(Scanner sc) {
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter mobile number: ");
        String mobile = sc.nextLine().trim();
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please enter again.");
                continue;
            }
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                check.setString(1, username);
                ResultSet rs = check.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("Username not available. Please type a different username.");
                } else {
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement insert = conn.prepareStatement("INSERT INTO users(name, mobile, username) VALUES (?, ?, ?)")) {
            insert.setString(1, name);
            insert.setString(2, mobile);
            insert.setString(3, username);
            insert.executeUpdate();
            System.out.println("User created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void createGroup(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Please select a user first (option 1).");
            return;
        }
        System.out.print("Enter group name: ");
        String groupName = sc.nextLine().trim();
        if (groupName.isEmpty()) {
            System.out.println("Group name cannot be empty.");
            return;
        }
        String insertSQL = "INSERT INTO `groups` (name, created_by) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, groupName);
            ps.setInt(2, activeUserId);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int newGroupId = keys.getInt(1);
                    System.out.println("Group created successfully with ID: " + newGroupId);
                } else {
                    System.out.println("Group created but cannot retrieve group id");
                }
            } else {
                System.out.println("Group creation failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addUserToGroup(Scanner sc) {
        System.out.print("Enter Group ID to add user to: ");
        int groupId = sc.nextInt();
        sc.nextLine();
        String sql = "SELECT id, username, name FROM users WHERE id NOT IN (" +
                     "SELECT user_id FROM group_members WHERE group_id = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Users NOT in group " + groupId + ":");
            while (rs.next()) {
                int userId = rs.getInt("id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                System.out.println(userId + ": " + username + " (" + name + ")");
            }
            System.out.print("Enter User ID to add to group: ");
            int userIdToAdd = sc.nextInt();
            sc.nextLine();
            try (PreparedStatement check = conn.prepareStatement(
                    "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?")) {
                check.setInt(1, groupId);
                check.setInt(2, userIdToAdd);
                ResultSet checkRs = check.executeQuery();
                checkRs.next();
                if (checkRs.getInt(1) > 0) {
                    System.out.println("User already exists in the group.");
                    return;
                }
            }
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO group_members(group_id, user_id) VALUES (?, ?)")) {
                insert.setInt(1, groupId);
                insert.setInt(2, userIdToAdd);
                int rows = insert.executeUpdate();
                if (rows == 1) {
                    System.out.println("User added to group successfully.");
                } else {
                    System.out.println("Failed to add user to group.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addExpense(Scanner sc) {
        if (activeUserId == -1) {
            System.out.println("Please select a user first (option 1).");
            return;
        }
        try (Connection conn = DBUtil.getConnection()) {
            System.out.print("Enter group ID to add expense: ");
            int groupId = sc.nextInt();
            sc.nextLine();
            try (PreparedStatement checkGroup = conn.prepareStatement(
                    "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?")) {
                checkGroup.setInt(1, groupId);
                checkGroup.setInt(2, activeUserId);
                ResultSet rs = checkGroup.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.out.println("You are not a member of this group.");
                    return;
                }
            }
            System.out.print("Enter total expense amount: ");
            double totalAmount = sc.nextDouble();
            sc.nextLine();
            System.out.print("Enter expense description: ");
            String description = sc.nextLine();
            String insertExpenseSQL = "INSERT INTO expenses (group_id, paid_by, amount, description) VALUES (?, ?, ?, ?)";
            int expenseId;
            try (PreparedStatement ps = conn.prepareStatement(insertExpenseSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, groupId);
                ps.setInt(2, activeUserId);
                ps.setDouble(3, totalAmount);
                ps.setString(4, description);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    System.out.println("Failed to create expense.");
                    return;
                }
                expenseId = keys.getInt(1);
            }
            System.out.println("Select split type: 1. EQUAL  2. EXACT  3. PERCENT");
            int splitType = sc.nextInt();
            sc.nextLine();
            try (PreparedStatement psGroupMembers = conn.prepareStatement(
                    "SELECT user_id FROM group_members WHERE group_id = ?")) {
                psGroupMembers.setInt(1, groupId);
                ResultSet membersRs = psGroupMembers.executeQuery();
                List<Integer> memberIds = new ArrayList<>();
                while (membersRs.next()) {
                    memberIds.add(membersRs.getInt("user_id"));
                }
                String insertSplitSQL = "INSERT INTO expense_splits (expense_id, user_id, share) VALUES (?, ?, ?)";
                try (PreparedStatement insertSplitStmt = conn.prepareStatement(insertSplitSQL)) {
                    double totalSplitAmount = 0.0;
                    for (int userId : memberIds) {
                        double shareAmount = 0.0;
                        if (splitType == 1) {
                            shareAmount = totalAmount / memberIds.size();
                            System.out.printf("User %d share: %.2f\n", userId, shareAmount);
                        } else if (splitType == 2) {
                            System.out.printf("Enter exact share for user %d: ", userId);
                            shareAmount = sc.nextDouble();
                            sc.nextLine();
                        } else if (splitType == 3) {
                            System.out.printf("Enter percent share for user %d: ", userId);
                            double percent = sc.nextDouble();
                            sc.nextLine();
                            shareAmount = (percent / 100.0) * totalAmount;
                        }
                        totalSplitAmount += shareAmount;
                        insertSplitStmt.setInt(1, expenseId);
                        insertSplitStmt.setInt(2, userId);
                        insertSplitStmt.setDouble(3, shareAmount);
                        insertSplitStmt.addBatch();
                    }
                    if (Math.abs(totalSplitAmount - totalAmount) > 0.01) {
                        System.out.println("Error: The total of splits does not match total amount.");
                        return;
                    }
                    insertSplitStmt.executeBatch();
                }
            }
            System.out.println("Expense added and splits recorded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewMyGroups() {
        if (activeUserId == -1) {
            System.out.println("Please select a user first (option 1).");
            return;
        }
        String sql = "SELECT g.id, g.name, u.username AS creator " +
                     "FROM `groups` g " +
                     "JOIN users u ON g.created_by = u.id " +
                     "WHERE g.created_by = ? " +
                     "UNION " +
                     "SELECT g.id, g.name, u.username AS creator " +
                     "FROM `groups` g " +
                     "JOIN group_members gm ON g.id = gm.group_id " +
                     "JOIN users u ON g.created_by = u.id " +
                     "WHERE gm.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activeUserId);
            ps.setInt(2, activeUserId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your Groups:");
            boolean hasGroups = false;
            while (rs.next()) {
                hasGroups = true;
                int groupId = rs.getInt("id");
                String groupName = rs.getString("name");
                String creator = rs.getString("creator");
                System.out.println(groupId + ": " + groupName + " (Created by: " + creator + ")");
            }
            if (!hasGroups) {
                System.out.println("You do not belong to any groups yet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewGroupBalances(Scanner sc) {
        System.out.print("Enter group ID to view balances: ");
        int groupId = sc.nextInt();
        sc.nextLine();
        String sql = "SELECT u1.username AS user, u2.username AS owes_to, b.amount " +
                     "FROM balances b " +
                     "JOIN users u1 ON b.user_id = u1.id " +
                     "JOIN users u2 ON b.owes_to = u2.id " +
                     "WHERE b.group_id = ? AND b.amount > 0";
        boolean hasDues = false;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            System.out.println("Group Balances:");
            while (rs.next()) {
                hasDues = true;
                String user = rs.getString("user");
                String owesTo = rs.getString("owes_to");
                double amount = rs.getDouble("amount");
                System.out.printf("%s owes %s: %.2f\n", user, owesTo, amount);
            }
            if (!hasDues) {
                System.out.println("No pending dues.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void settleUp(Scanner sc) { }
}
