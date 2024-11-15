import Logger.Logger;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        loginUser(new AdminLogin("admin", "admin"));
    }

    public static void loginUser(AdminLogin adminLogin) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();

        if (adminLogin.loginUser(username, password)) {
            Logger.info("Logged in successfully as " + username);
            ControlPanel controlPanel = new ControlPanel();
            controlPanel.startOptions();
        } else {
            Logger.warning("Login failed. Invalid username or password. Please try again.\n");
            loginUser(adminLogin);
        }

        scanner.close();

    }
}
