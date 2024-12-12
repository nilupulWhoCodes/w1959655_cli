

import cli.Configuration;
import cli.Customer;
import cli.TicketPool;
import cli.Vendor;

import java.io.*;
import java.util.Scanner;

public class Main {
    private static TicketPool ticketPool = null;

    public static void main(String[] args) {
        TicketPool ticketPool = null;
        Thread vendorThread = null, customerThread = null, monitorThread = null;
        Scanner scanner = new Scanner(System.in);
        Configuration config = new Configuration();

        while (true) {
            System.out.println("1. Configure System Settings");
            System.out.println("2. Start Ticketing Operations");
            System.out.println("3. Save Configuration to File");
            System.out.println("4. Load Configuration from File");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = getValidatedChoice(scanner);

            switch (choice) {
                case 1:
                    configureSystem(scanner, config);
                    ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());
                    System.out.println("TicketPool initialized with max capacity: " + config.getMaxTicketCapacity() +
                            " and total tickets: " + config.getTotalTickets());
                    break;
                case 2:
                    if (ticketPool == null) {
                        System.out.println("Please configure the system before starting operations.");
                        break;
                    }

                    startTicketingOperations(config, ticketPool);
                    break;
                case 3:
                    saveFile(config, scanner);
                    break;
                case 4:
                    ticketPool = loadConfig(config, scanner, ticketPool);
                    break;
                case 5:
                    System.out.println("Logging you out. Thank you for using our system. Have a nice day!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static int getValidatedChoice(Scanner scanner) {
        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next();
            }
        }
    }

    private static void startTicketingOperations(Configuration config, TicketPool ticketPool) {

        Thread vendorThread1 = new Thread(new Vendor(ticketPool, "ABC Movie", config.getTicketReleaseRate(), 1));
        Thread vendorThread2 = new Thread(new Vendor(ticketPool, "ABC Concert", config.getTicketReleaseRate(), 2));


        Thread customerThread1 = new Thread(new Customer(ticketPool, "ABC Movie", config.getCustomerRetrievalRate(), 8));
        Thread customerThread2 = new Thread(new Customer(ticketPool, "ABC Concert", config.getCustomerRetrievalRate(), 9));

        vendorThread1.start();
        vendorThread2.start();
        customerThread1.start();
        customerThread2.start();

        System.out.println("Ticketing operations started. Logs will display below...");

        while (!ticketPool.isPoolEmptyAndLimitReached()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Operation interrupted while waiting for the pool to empty and limit to be reached.");
                Thread.currentThread().interrupt();
                break;
            }
        }

        vendorThread1.interrupt();
        vendorThread2.interrupt();
        customerThread1.interrupt();
        customerThread2.interrupt();

        try {
            vendorThread1.join();
            vendorThread2.join();
            customerThread1.join();
            customerThread2.join();
        } catch (InterruptedException e) {
            System.out.println("Error while waiting for threads to terminate: " + e.getMessage());
        }

        System.out.println("Global ticket limit reached and pool is empty. Returning to the main menu...");
    }

    private static void saveFile(Configuration config, Scanner scanner) {
        if (config.getTotalTickets() == 0) {
            System.out.println("No configuration to save. Please configure the system first.");
        } else {
            saveConfiguration(config, scanner);
        }
    }

    private static TicketPool loadConfig(Configuration config, Scanner scanner, TicketPool ticketPool) {
        TicketPool loadedPool = loadConfiguration(config, scanner);
        if (loadedPool != null) {
            ticketPool = loadedPool;
            System.out.println("Configuration loaded and applied successfully.");
            System.out.println("Current configuration:");
            viewConfiguration(config);
        } else {
            System.out.println("Failed to load configuration. Using the previous configuration.");
        }
        return ticketPool;
    }


    private static void saveConfiguration(Configuration config, Scanner scanner) {
        try {
            System.out.print("Enter the file name to save the configuration (e.g., config.txt): ");
            String fileName = scanner.nextLine().trim();
            File directory = new File("resources");
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Total Tickets: " + config.getTotalTickets() + "\n");
                writer.write("Ticket Release Rate: " + config.getTicketReleaseRate() + "\n");
                writer.write("Customer Retrieval Rate: " + config.getCustomerRetrievalRate() + "\n");
                writer.write("Max Ticket Capacity: " + config.getMaxTicketCapacity() + "\n");
                System.out.println("Configuration saved to '" + file.getAbsolutePath() + "' successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }


    private static TicketPool loadConfiguration(Configuration config, Scanner scanner) {
        try {
            File directory = new File("resources");
            if (!directory.exists()) {
                System.out.println("The 'resources' directory does not exist. No configurations to load.");
                return null;
            }

            String[] files = directory.list((dir, name) -> name.endsWith(".txt"));

            if (files == null || files.length == 0) {
                System.out.println("No configuration files found in the 'resources' directory.");
                return null;
            }

            System.out.println("Available configuration files:");
            for (String file : files) {
                System.out.println("- " + file);
            }

            System.out.print("Enter the file name to load the configuration: ");
            String fileName = scanner.nextLine().trim();

            File file = new File(directory, fileName);
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        switch (parts[0]) {
                            case "Total Tickets":
                                config.setTotalTickets(Integer.parseInt(parts[1].trim()));
                                break;
                            case "Ticket Release Rate":
                                config.setTicketReleaseRate(Integer.parseInt(parts[1].trim()));
                                break;
                            case "Customer Retrieval Rate":
                                config.setCustomerRetrievalRate(Integer.parseInt(parts[1].trim()));
                                break;
                            case "Max Ticket Capacity":
                                config.setMaxTicketCapacity(Integer.parseInt(parts[1].trim()));
                                break;
                            default:
                                System.out.println("Unknown configuration parameter: " + parts[0]);
                        }
                    }
                }
                System.out.println("Configuration loaded from '" + file.getAbsolutePath() + "' successfully.");
                return new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading configuration: " + e.getMessage());
        }
        return null;
    }
    private static void configureSystem(Scanner scanner, Configuration config) {
        int totalTickets = 0;
        int ticketReleaseRate = 0;
        int customerRetrievalRate = 0;
        int maxTicketCapacity = 0;

        while (true) {
            totalTickets = getPositiveInput(scanner, "Enter the total number of tickets to be released:");
            if (totalTickets > 0) {
                break;
            }
            System.out.println("Total tickets must be a positive integer. Please try again.");
        }

        while (true) {
            ticketReleaseRate = getPositiveInput(scanner, "Enter the ticket release rate (tickets/second):");
            if (ticketReleaseRate > 0 && ticketReleaseRate <= totalTickets) {
                break;
            }
            System.out.println("Ticket release rate must be a positive integer and less than or equal to the total number of tickets. Please try again.");
        }

        while (true) {
            customerRetrievalRate = getPositiveInput(scanner, "Enter the customer retrieval rate (tickets/second):");
            if (customerRetrievalRate > 0 && customerRetrievalRate <= ticketReleaseRate) {
                break;
            }
            System.out.println("Customer retrieval rate must be a positive integer and less than or equal to the ticket release rate. Please try again.");
        }

        while (true) {
            maxTicketCapacity = getPositiveInput(scanner, "Enter the maximum ticket capacity:");
            if (maxTicketCapacity >= ticketReleaseRate && maxTicketCapacity >= customerRetrievalRate && maxTicketCapacity <= totalTickets) {
                break;
            }
            System.out.println("Max ticket capacity must be greater than or equal to ticket release rate and customer retrieval rate, and less than or equal to the total number of tickets. Please try again.");
        }

        config.setTotalTickets(totalTickets);
        config.setTicketReleaseRate(ticketReleaseRate);
        config.setCustomerRetrievalRate(customerRetrievalRate);
        config.setMaxTicketCapacity(maxTicketCapacity);
    }

    private static int getPositiveInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                if (value > 0) {
                    return value;
                }
            }
            System.out.println("Invalid input. Please enter a positive integer.");
            scanner.next();
        }
    }

    private static void viewConfiguration(Configuration config) {
        System.out.println("\nCurrent Configuration:");
        System.out.println("Total Tickets: " + config.getTotalTickets());
        System.out.println("Ticket Release Rate: " + config.getTicketReleaseRate());
        System.out.println("Customer Retrieval Rate: " + config.getCustomerRetrievalRate());
        System.out.println("Max Ticket Capacity: " + config.getMaxTicketCapacity());
    }
}
