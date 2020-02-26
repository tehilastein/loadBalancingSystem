package semesterProject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread2 implements Runnable {
	private ServerSocket serverSocket = null;
	int id;
	private ArrayList<Task> tasks;
	private ArrayList<Task> completedTasks;
	public Integer slaveRuntime;
	private boolean doIt;
	public boolean slaveBlocked;

	public ServerThread2(ServerSocket s, int id, ArrayList<Task> tasks, ArrayList<Task> completedTasks) {
		serverSocket = s;
		this.id = id;
		this.slaveRuntime = 0;
		this.tasks = tasks;
		this.doIt = false;
		this.completedTasks = completedTasks;
	}

	@Override
	public void run() {
		// This thread accepts its own client socket from the shared server socket
		try (Socket clientSocket = serverSocket.accept();
				ObjectOutputStream responseWriter = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream requestReader = new ObjectInputStream(clientSocket.getInputStream());) {
			while (true) {
				Task task;
				slaveBlocked = (Boolean) requestReader.readObject();
				while (!doIt || tasks.size() == 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				synchronized (tasks) {
					task = tasks.get(0);
					slaveRuntime += task.getRuntime();
				}
				responseWriter.writeObject(task);
				synchronized (tasks) {
					tasks.remove(0);
				}
				task.setSum((Integer) requestReader.readObject());
				synchronized (completedTasks) {
					completedTasks.add(task);
					slaveRuntime -= task.getRuntime();
				}
				doIt = false;
			}

		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + serverSocket.getLocalPort()
					+ " or listening for a connection");
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setDoIt() {
		doIt = true;
	}

	public int getSlaveRuntime() {
		return slaveRuntime;
	}

}
