import Logger.Logger;
import Class.Configuration;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ControlPanel {

    public void startOptions() {
        Scanner scanner = new Scanner(System.in);
        String filepath = "";
        int userChoice = -1;

        while (true) {
            System.out.println("\nWelcome! to the Book Tickets. What would you like to do?\n");
            System.out.println("1. Enter new configuration values");
            System.out.println("2. Load from document");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice: ");

            try {
                userChoice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                Logger.warning("Invalid choice. Please enter a valid option (1-3).");
                scanner.nextLine();
                continue;
            }

            switch (userChoice) {
                case 1:
                    configureOptions();
                    return;
                case 2:
                    System.out.print("Enter the file name (Ex: configuration.txt): ");
                    filepath = scanner.nextLine().trim();

                    if (filepath.isEmpty()) {
                        Logger.warning("File name cannot be empty. Please try again.");
                        continue;
                    }

                    Logger.info("\nImporting values from " + filepath + "...");

                    Configuration config = importConfigurationFromFile(filepath);

                    Logger.info("\nLoaded configuration from " + config + ".");

                    return;
                case 3:
                    Logger.info("Exiting the program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    Logger.warning("Invalid choice. Please enter a valid option (1-3).");
                    break;
            }
        }
    }


    public void configureOptions() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter the total number of tickets: ");
        int totalNumberOfTickets = validateValues("Total number of Tickets", scanner);

        System.out.print("Please enter the maximum ticket capacity: ");
        int maxTicketCapacity = validateValues("Maximum ticket capacity", scanner, totalNumberOfTickets);

        System.out.print("Please enter a rate for ticket release: ");
        int ticketReleaseRate = validateValues("Ticket release rate", scanner, maxTicketCapacity, totalNumberOfTickets);

        System.out.print("Please enter a rate for customer retrieval: ");
        int customerRetrievalRate = validateValues("Customer retrieval rate", scanner, maxTicketCapacity, totalNumberOfTickets);

        Configuration config = new Configuration(totalNumberOfTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
        System.out.println(config.toString());

        selectSystemOptions(config);
    }

    /**
     * Validate input for totalNumberOfTickets.
     */
    private int validateValues(String type, Scanner scanner, int maxLimit) {
        return validateValues(type, scanner, maxLimit, Integer.MAX_VALUE);
    }

    /**
     * Validate inputs for totalNumberOfTickets and maxTicketCapacity
     */
    private int validateValues(String type, Scanner scanner, int maxLimit1, int maxLimit2) {
        int value = -1;

        while (true) {
            try {
                value = scanner.nextInt();

                if (value > 0 && value <= maxLimit1 && value <= maxLimit2) {
                    break;
                } else if (value > maxLimit1 || value > maxLimit2) {
                    Logger.warning(type + " cannot exceed the amount of the total number of tickets ("
                            + maxLimit1 + ") or maximum ticket capacity. Please try again.");
                } else {
                    Logger.warning(type + " needs to be greater than 0. Please try again.");
                }
            } catch (InputMismatchException e) {
                Logger.warning("Invalid input. Please enter a number for " + type + ".");
                scanner.next();
            }
            System.out.print("Enter a valid " + type + ": ");
        }

        return value;
    }

    /**
     * Validate input for totalNumberofTickets.
     */
    private int validateValues(String type, Scanner scanner) {
        return validateValues(type, scanner, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private void selectSystemOptions(Configuration config) {
        Scanner scanner = new Scanner(System.in);
        int userChoice = -1;
        String filePath = "config.txt";

        while (true) {
            System.out.println("\nSuccessfully gathered configuration inputs. What would you like to do next?\n");
            System.out.println("1. Start the system");
            System.out.println("2. Reenter configuration values");
            System.out.println("3. Save the configuration file");
            System.out.println("4. Exit");
            System.out.print("\nEnter your choice: ");

            try {
                userChoice = scanner.nextInt();
            } catch (InputMismatchException e) {
                Logger.warning("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine();
                continue;
            }

            switch (userChoice) {
                case 1:
                    startSystem();
                    return;
                case 2:
                    Logger.info("\nResetting the configuration form...");
                    configureOptions();
                    return;
                case 3:
                    Logger.info("\nSaving the configuration file...");
                    saveConfigurationToTextFile(config, filePath);
                    return;
                case 4:
                    Logger.info("Exiting the program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    Logger.warning("Invalid choice. Please enter a valid option (1-4).");
                    break;
            }
        }
    }

    public void saveConfigurationToTextFile(Configuration config, String baseFilePath) {
        String filePath = baseFilePath;
        int counter = 1;

        while (new File(filePath).exists()) {
            filePath = baseFilePath.replace(".txt", "") + " " + counter + ".txt";
            counter++;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(config.toString());
            System.out.println("Configuration saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    public Configuration importConfigurationFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null && line.startsWith("Configuration : {")) {

                String content = line.replace("Configuration : {", "").replace("}", "").trim();

                String[] keyValuePairs = content.split(",");
                int totalNumberOfTickets = 0;
                int ticketReleaseRate = 0;
                int customerRetrievalRate = 0;
                int maxTicketCapacity = 0;

                for (String pair : keyValuePairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        int value = Integer.parseInt(keyValue[1].trim());

                        switch (key) {
                            case "Total Number of Tickets":
                                totalNumberOfTickets = value;
                                break;
                            case "Ticket Release Rate":
                                ticketReleaseRate = value;
                                break;
                            case "Customer Retrieval Rate":
                                customerRetrievalRate = value;
                                break;
                            case "Max Ticket Capacity":
                                maxTicketCapacity = value;
                                break;
                            default:
                                Logger.warning("Unknown key: " + key);
                        }
                    }
                }

                return new Configuration(totalNumberOfTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
            } else {
                Logger.error("Invalid configuration format in file.");
            }
        } catch (IOException e) {
            Logger.error("Error reading configuration file: " + e.getMessage());

        } catch (NumberFormatException e) {
            Logger.error("Error parsing number from configuration file: " + e.getMessage());
        }
        return null;
    }


    private void startSystem() {

    }


}
