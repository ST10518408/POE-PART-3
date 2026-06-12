package com.mycompany.message;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class Message {

    // LOGIN CLASS
    static class Login {
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String cellPhoneNumber;

        public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
        }

        public boolean checkUserName() {
            return username.contains("_") && username.length() <= 5;
        }

        public boolean checkPasswordComplexity() {
            if (password.length() < 8) return false;

            boolean hasCapital = false, hasNumber = false, hasSpecial = false;

            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasCapital = true;
                else if (Character.isDigit(c)) hasNumber = true;
                else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            }

            return hasCapital && hasNumber && hasSpecial;
        }

        public boolean checkCellPhoneNumber() {
            // REGEX SOURCED AND ADAPTED FROM:
            // HTTPS://WWW.BAELDUNG.COM/JAVA-REGEX-VALIDATE-PHONE-NUMBERS
            return cellPhoneNumber.matches("^\\+27\\d{9}$");
        }

        public boolean loginUser(String enteredUsername, String enteredPassword) {
            return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
        }

        public String returnLoginStatus(String enteredUsername, String enteredPassword) {
            if (loginUser(enteredUsername, enteredPassword)) {
                return "Welcome " + firstName + " " + lastName + ", it is great to see you again!";
            } else {
                return "Username or password incorrect, please try again.";
            }
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    // MESSAGE CLASS
    static class MessageData {

        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        private String flag;

        static int totalSent = 0;

        static ArrayList<MessageData> sentMessages = new ArrayList<>();
        static ArrayList<MessageData> storedMessages = new ArrayList<>();
        static ArrayList<MessageData> disregardMessages = new ArrayList<>();
        static ArrayList<String> messageHashes = new ArrayList<>();
        static ArrayList<String> messageIDs = new ArrayList<>();

        public MessageData(int messageNumber, String recipient, String messageText) {
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageID = generateMessageID();
            this.messageHash = createMessageHash();
        }

        // CONSTRUCTOR WITH FIXED ID USED FOR PRELOADED TEST DATA
        public MessageData(int messageNumber, String recipient, String messageText, String fixedID) {
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageID = fixedID;
            this.messageHash = createMessageHash();
        }

        private String generateMessageID() {
            Random rand = new Random();
            long id = (long)(rand.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
            return String.valueOf(id);
        }

        public boolean checkMessageID() {
            return messageID.length() <= 10;
        }

        public String checkRecipientCell() {
            if (recipient.startsWith("+") && recipient.length() <= 13) {
                return "CELL PHONE NUMBER SUCCESSFULLY CAPTURED.";
            } else {
                return "CELL PHONE NUMBER IS INCORRECTLY FORMATTED OR DOES NOT CONTAIN AN INTERNATIONAL CODE. PLEASE CORRECT THE NUMBER AND TRY AGAIN.";
            }
        }

        public String createMessageHash() {
            String[] words = messageText.trim().split("\\s+");
            String firstWord = words[0];
            String lastWord = words[words.length - 1];
            String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));
            return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        }

        public String checkMessageLength() {
            if (messageText.length() <= 250) {
                return "MESSAGE READY TO SEND.";
            } else {
                int over = messageText.length() - 250;
                return "MESSAGE EXCEEDS 250 CHARACTERS BY " + over + "; PLEASE REDUCE THE SIZE.";
            }
        }

        public String sentMessage(int choice) {
            switch (choice) {
                case 1:
                    flag = "Sent";
                    totalSent++;
                    sentMessages.add(this);
                    messageHashes.add(messageHash);
                    messageIDs.add(messageID);
                    return "MESSAGE SUCCESSFULLY SENT.";

                case 2:
                    flag = "Disregarded";
                    disregardMessages.add(this);
                    return "PRESS 0 TO DELETE THE MESSAGE.";

                case 3:
                    flag = "Stored";
                    storedMessages.add(this);
                    messageHashes.add(messageHash);
                    messageIDs.add(messageID);
                    return "MESSAGE SUCCESSFULLY STORED.";

                default:
                    return "INVALID OPTION.";
            }
        }

        public static String printMessages() {
            if (sentMessages.isEmpty()) return "NO MESSAGES SENT.";

            StringBuilder sb = new StringBuilder();

            for (MessageData m : sentMessages) {
                sb.append("MESSAGE ID: ").append(m.messageID)
                        .append(" | HASH: ").append(m.messageHash)
                        .append(" | RECIPIENT: ").append(m.recipient)
                        .append(" | MESSAGE: ").append(m.messageText)
                        .append("\n");
            }

            return sb.toString();
        }

        public static int returnTotalMessages() {
            return totalSent;
        }

        // STORES STORED MESSAGES TO JSON FILE
        // FILE WRITING APPROACH ADAPTED FROM:
        // HTTPS://WWW.BAELDUNG.COM/JAVA-WRITE-TO-FILE
        public static void storeMessage(String filename) {
            StringBuilder json = new StringBuilder();
            json.append("[\n");

            for (int i = 0; i < storedMessages.size(); i++) {
                MessageData m = storedMessages.get(i);

                json.append(" {\n");
                json.append(" \"messageID\": \"").append(m.messageID).append("\",\n");
                json.append(" \"messageHash\": \"").append(m.messageHash).append("\",\n");
                json.append(" \"recipient\": \"").append(m.recipient).append("\",\n");
                json.append(" \"message\": \"").append(m.messageText).append("\",\n");
                json.append(" \"flag\": \"").append(m.flag).append("\"\n");
                json.append(" }");

                if (i < storedMessages.size() - 1) json.append(",");

                json.append("\n");
            }

            json.append("]");

            try (FileWriter fw = new FileWriter(filename)) {
                fw.write(json.toString());
                System.out.println("MESSAGES SUCCESSFULLY STORED TO " + filename);
            } catch (IOException e) {
                System.out.println("ERROR WRITING JSON FILE: " + e.getMessage());
            }
        }

        // PRELOAD TEST DATA
        public static void preloadTestData() {
            MessageData m1 = new MessageData(1, "+27834557896", "Did you get the cake?", "1000000001");
            MessageData m2 = new MessageData(2, "+27838884567", "Where are you? You are late! I have asked you to be on time.", "1000000002");
            MessageData m3 = new MessageData(3, "+27834484567", "Yohoooo, I am at your gate.", "1000000003");
            MessageData m4 = new MessageData(4, "0838884567", "It is dinner time !", "1000000004");
            MessageData m5 = new MessageData(5, "+27838884567", "Ok, I am leaving without you.", "1000000005");

            m1.sentMessage(1);
            m2.sentMessage(3);
            m3.sentMessage(2);
            m4.sentMessage(1);
            m5.sentMessage(3);
        }

        // DISPLAY STORED MESSAGES INFO
        public static String displaySenderAndRecipient() {
            if (storedMessages.isEmpty()) return "NO STORED MESSAGES.";

            StringBuilder sb = new StringBuilder();

            for (MessageData m : storedMessages) {
                sb.append("MESSAGE NUMBER: ").append(m.messageNumber)
                        .append(" | RECIPIENT: ").append(m.recipient)
                        .append("\n");
            }

            return sb.toString();
        }

        // FIND LONGEST MESSAGE
        public static String displayLongestMessage() {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);
            all.addAll(disregardMessages);

            if (all.isEmpty()) return "NO MESSAGES AVAILABLE.";

            MessageData longest = all.get(0);

            for (MessageData m : all) {
                if (m.messageText.length() > longest.messageText.length()) {
                    longest = m;
                }
            }

            return longest.messageText;
        }

        // SEARCH BY MESSAGE ID
        public static String searchByMessageID(String searchID) {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);

            for (MessageData m : all) {
                if (m.messageID.equals(searchID)) {
                    return "RECIPIENT: " + m.recipient + "\nMESSAGE: " + m.messageText;
                }
            }

            return "MESSAGE ID NOT FOUND.";
        }

        // SEARCH BY RECIPIENT
        public static String searchByRecipient(String searchRecipient) {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);

            StringBuilder sb = new StringBuilder();

            for (MessageData m : all) {
                if (m.recipient.equals(searchRecipient)) {
                    sb.append(m.messageText).append("\n");
                }
            }

            if (sb.length() == 0) return "NO MESSAGES FOUND FOR THAT RECIPIENT.";

            return sb.toString();
        }

        // DELETE BY HASH
        public static String deleteMessageByHash(String hash) {
            ArrayList<MessageData> all = new ArrayList<>();
            all.addAll(sentMessages);
            all.addAll(storedMessages);

            for (MessageData m : all) {
                if (m.messageHash.equalsIgnoreCase(hash)) {
                    String deletedText = m.messageText;
                    sentMessages.remove(m);
                    storedMessages.remove(m);
                    messageHashes.remove(m.messageHash);
                    messageIDs.remove(m.messageID);
                    return "MESSAGE \"" + deletedText + "\" SUCCESSFULLY DELETED.";
                }
            }

            return "HASH NOT FOUND. NO MESSAGE DELETED.";
        }

        // FULL SENT REPORT
        public static String displayReport() {
            if (sentMessages.isEmpty()) return "NO SENT MESSAGES TO REPORT.";

            StringBuilder sb = new StringBuilder();

            for (MessageData m : sentMessages) {
                sb.append("MESSAGE HASH: ").append(m.messageHash)
                        .append(" | RECIPIENT: ").append(m.recipient)
                        .append(" | MESSAGE: ").append(m.messageText)
                        .append("\n");
            }

            return sb.toString();
        }

        public String getMessageID() { return messageID; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }
        public String getFlag() { return flag; }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Login user = null;

        // WELCOME SCREEN
        boolean welcomed = true;

        while (welcomed) {
            System.out.println("\nWELCOME TO QUICKCHAT");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("Choose option: ");

            int welcome;

            try {
                welcome = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("INVALID OPTION.");
                continue;
            }

            switch (welcome) {
                case 1:
                    System.out.println("\nREGISTRATION");
                    System.out.print("First Name: ");
                    String regFirst = scanner.nextLine();
                    System.out.print("Last Name: ");
                    String regLast = scanner.nextLine();

                    String regUsername;

                    while (true) {
                        System.out.print("Username: ");
                        regUsername = scanner.nextLine();

                        Login tempU = new Login(regFirst, regLast, regUsername, "", "+27000000000");

                        if (tempU.checkUserName()) {
                            System.out.println("USERNAME SUCCESSFULLY CAPTURED.");
                            break;
                        } else {
                            System.out.println("USERNAME INVALID (MUST CONTAIN '_' AND BE ≤ 5 CHARACTERS).");
                        }
                    }

                    String regPassword;

                    while (true) {
                        System.out.print("Password: ");
                        regPassword = scanner.nextLine();

                        Login tempP = new Login(regFirst, regLast, regUsername, regPassword, "+27000000000");

                        if (tempP.checkPasswordComplexity()) {
                            System.out.println("PASSWORD SUCCESSFULLY CAPTURED.");
                            break;
                        } else {
                            System.out.println("PASSWORD INVALID (8+ CHARS, CAPITAL, NUMBER, SPECIAL CHAR).");
                        }
                    }

                    String regCell;

                    while (true) {
                        System.out.print("Cell number (+27): ");
                        regCell = scanner.nextLine();

                        Login tempC = new Login(regFirst, regLast, regUsername, regPassword, regCell);

                        if (tempC.checkCellPhoneNumber()) {
                            System.out.println("CELL NUMBER SUCCESSFULLY CAPTURED.");
                            break;
                        } else {
                            System.out.println("INVALID CELL NUMBER FORMAT (+27 REQUIRED).");
                        }
                    }

                    user = new Login(regFirst, regLast, regUsername, regPassword, regCell);
                    System.out.println("REGISTRATION SUCCESSFUL!");
                    break;

                case 2:
                    if (user == null) {
                        System.out.println("PLEASE REGISTER FIRST.");
                        break;
                    }

                    System.out.println("\nLOGIN");
                    System.out.print("Username: ");
                    String enteredUsername = scanner.nextLine();
                    System.out.print("Password: ");
                    String enteredPassword = scanner.nextLine();

                    System.out.println(user.returnLoginStatus(enteredUsername, enteredPassword));

                    if (user.loginUser(enteredUsername, enteredPassword)) {
                        welcomed = false;
                    }
                    break;

                case 3:
                    System.out.println("GOODBYE!");
                    scanner.close();
                    return;

                default:
                    System.out.println("INVALID OPTION.");
            }
        }

        // PRELOAD TEST DATA
        MessageData.preloadTestData();

        System.out.print("\nHOW MANY MESSAGES WOULD YOU LIKE TO SEND? ");
        int numMessages = 0;

        try {
            numMessages = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("INVALID NUMBER. DEFAULTING TO 0.");
        }

        boolean running = true;

        while (running) {

            System.out.println("\nWELCOME TO QUICKCHAT");
            System.out.println("1) Send Messages");
            System.out.println("2) Show Recently Sent Messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose option: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("INVALID OPTION.");
                continue;
            }

            switch (choice) {

                case 1:
                    for (int i = 1; i <= numMessages; i++) {

                        System.out.println("\n--- MESSAGE " + i + " OF " + numMessages + " ---");
                        System.out.print("Recipient: ");
                        String recipient = scanner.nextLine().trim();

                        String messageText;

                        while (true) {
                            System.out.print("Message: ");
                            messageText = scanner.nextLine();

                            if (messageText.length() <= 250) {
                                System.out.println("MESSAGE READY TO SEND.");
                                break;
                            } else {
                                System.out.println("MESSAGE TOO LONG. REDUCE SIZE.");
                            }
                        }

                        MessageData msg = new MessageData(i, recipient, messageText);

                        System.out.println(msg.checkRecipientCell());
                        System.out.println("MESSAGE ID: " + msg.getMessageID());
                        System.out.println("MESSAGE HASH: " + msg.getMessageHash());

                        System.out.println("\n1) Send");
                        System.out.println("2) Disregard");
                        System.out.println("3) Store");
                        System.out.print("Choose: ");

                        int sendChoice;

                        try {
                            sendChoice = Integer.parseInt(scanner.nextLine().trim());
                        } catch (NumberFormatException e) {
                            sendChoice = 2;
                        }

                        System.out.println(msg.sentMessage(sendChoice));
                    }

                    System.out.println("\nTOTAL MESSAGES SENT: " + MessageData.returnTotalMessages());

                    if (!MessageData.storedMessages.isEmpty()) {
                        MessageData.storeMessage("stored_messages.json");
                    }

                    break;

                case 2:
                    System.out.println(MessageData.printMessages());
                    break;

                case 3:
                    boolean storedMenu = true;

                    while (storedMenu) {
                        System.out.println("\nSTORED MESSAGES MENU");
                        System.out.println("a) Display sender and recipient");
                        System.out.println("b) Longest message");
                        System.out.println("c) Search by ID");
                        System.out.println("d) Search by recipient");
                        System.out.println("e) Delete by hash");
                        System.out.println("f) Sent report");
                        System.out.println("x) Back");

                        String sub = scanner.nextLine().toLowerCase();

                        switch (sub) {

                            case "a":
                                System.out.println(MessageData.displaySenderAndRecipient());
                                break;

                            case "b":
                                System.out.println(MessageData.displayLongestMessage());
                                break;

                            case "c":
                                System.out.print("Enter ID: ");
                                System.out.println(MessageData.searchByMessageID(scanner.nextLine()));
                                break;

                            case "d":
                                System.out.print("Enter recipient: ");
                                System.out.println(MessageData.searchByRecipient(scanner.nextLine()));
                                break;

                            case "e":
                                System.out.print("Enter hash: ");
                                System.out.println(MessageData.deleteMessageByHash(scanner.nextLine()));
                                break;

                            case "f":
                                System.out.println(MessageData.displayReport());
                                break;

                            case "x":
                                storedMenu = false;
                                break;

                            default:
                                System.out.println("INVALID OPTION.");
                        }
                    }
                    break;

                case 4:
                    System.out.println("GOODBYE!");
                    running = false;
                    break;

                default:
                    System.out.println("INVALID OPTION.");
            }
        }

        scanner.close();
    }
}