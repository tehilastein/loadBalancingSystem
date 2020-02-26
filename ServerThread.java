package semesterProject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable {

	private ServerSocket serverSocket = null;
	int id;
	private ArrayList<Task> tasks;
	private ArrayList<Task> completedTasks;

	public ServerThread(ServerSocket s, int id, ArrayList<Task> tasks, ArrayList<Task> completedTasks) {
		serverSocket = s;
		this.id = id;
		this.tasks = tasks;
		this.completedTasks = completedTasks;
	}

	@Override
	public void run() {

		// This thread accepts its own client socket from the shared server socket
		try (Socket clientSocket = serverSocket.accept();
				PrintWriter responseWriter = new PrintWriter(clientSocket.getOutputStream(), true);
				ObjectInputStream requestReader = new ObjectInputStream(clientSocket.getInputStream());) {
			Task requestString;
			do {
				requestString = (Task) requestReader.readObject();
				requestString.setClient(id);
				synchronized (tasks) {
					tasks.add(requestString);
				}
				boolean found = false;
				while (!found) {
					while (completedTasks.isEmpty()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					synchronized (completedTasks) {
						for (int i = 0; i < completedTasks.size(); i++) {
							if (completedTasks.size() != 0 && completedTasks.get(i).getClient() == id) {
								responseWriter.println(completedTasks.get(i).getSum());
								completedTasks.remove(i);
								found = true;
								break;
							}
						}
					}
				}
			} while (requestString != null);

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Exception caught when trying to listen on port " + serverSocket.getLocalPort()
					+ " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

}
