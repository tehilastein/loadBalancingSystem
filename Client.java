package semesterProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws IOException {
		System.out.println("****CLIENT****");
		initializeConnection(args);
	}

	private static int acceptDigit(Scanner keyboard) {
		int digit = keyboard.nextInt();
		keyboard.nextLine();
		return digit;
	}

	public static void initializeConnection(String[] args) throws IOException {

		args = new String[] { "127.0.0.1", "30000" };

		if (args.length != 2) {
			System.err.println("Usage: java NetworkClient <host name> <port number> ");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try (Socket clientSocket = new Socket(hostName, portNumber);
				ObjectOutputStream requestWriter = new ObjectOutputStream(clientSocket.getOutputStream());
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));) {
			Scanner keyboard = new Scanner(System.in);
			char quit = 'n';
			while (quit != 'y' && quit != 'Y') {
				System.out.print("Enter the first digit to compute: ");
				int digitOne = acceptDigit(keyboard);
				System.out.print("Enter the second digit to compute: ");
				int digitTwo = acceptDigit(keyboard);
				Task task = new Task(digitOne, digitTwo);
				requestWriter.writeObject(task);
				requestWriter.flush();
				System.out.println("Response: " + responseReader.readLine());
				System.out.println("Enter any key to compute another example, or y to exit");
				quit = keyboard.nextLine().charAt(0);
			}
		}
	}

}
