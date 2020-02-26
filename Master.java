package semesterProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;

public class Master {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("****MASTER****");
		ArrayList<Task> tasks = new ArrayList<>();
		ArrayList<Task> completedTasks = new ArrayList<>();
		initializeConnection(args, tasks, completedTasks);

	}

	public static void initializeConnection(String[] args, ArrayList<Task> tasks, ArrayList<Task> completedTasks)
			throws IOException, InterruptedException {

		args = new String[] { "30000", "40000" };

		if (args.length != 2) {
			System.err.println("Usage: java NetworkServer <port number>");
			System.exit(1);
		}

		int portNumber1 = Integer.parseInt(args[0]);
		int portNumber2 = Integer.parseInt(args[1]);
		final int NUM_CLIENTS = 6;
		final int NUM_SLAVES = 2;

		try (ServerSocket masterServerSocket = new ServerSocket(portNumber1);
				ServerSocket masterSlaveSocket = new ServerSocket(portNumber2);) {
			ArrayList<Thread> clientthreads = new ArrayList<Thread>();
			for (int i = 0; i < NUM_CLIENTS; i++) {
				clientthreads.add(new Thread(new ServerThread(masterServerSocket, i, tasks, completedTasks)));
			}

			for (Thread t : clientthreads) {
				t.start();
			}

			ArrayList<ServerThread2> slavethreads = new ArrayList<ServerThread2>();
			ArrayList<Thread> slavethreads2 = new ArrayList<Thread>();

			for (int i = 0; i < NUM_SLAVES; i++) {
				slavethreads.add(new ServerThread2(masterSlaveSocket, i, tasks, completedTasks));
			}

			for (int i = 0; i < slavethreads.size(); i++) {
				slavethreads2.add(new Thread(slavethreads.get(i)));
			}

			for (Thread t : slavethreads2) {
				t.start();
			}

			// only enter the while if there's a task otherwise spin and check again.
			ArrayList<Integer> slaveIndexesZero = new ArrayList<>();
			boolean isTask = false;
			isTask = searchForAvailableTasks(tasks);
			// check runtimes of each slave
			while (isTask) {
				int lowestRuntime = 0;
				int lowestIndex = 0;
				for (int t = 0; t < slavethreads.size(); t++) {
					if (slavethreads.get(t).getSlaveRuntime() <= lowestRuntime && !slavethreads.get(t).slaveBlocked) {
						lowestRuntime = slavethreads.get(t).getSlaveRuntime();
						lowestIndex = t;
						if (slavethreads.get(t).getSlaveRuntime() == 0) {
							slaveIndexesZero.add(t);
						}
					}
				}
				if (slaveIndexesZero.size() != 0) {
					Random random = new Random();
					int index = random.nextInt(slaveIndexesZero.size());
					lowestIndex = slaveIndexesZero.remove(index);
				}
				int slaveNum = lowestIndex;
				System.out.println("Sending to slave: " + (slaveNum + 1));
				slavethreads.get(lowestIndex).setDoIt();
				Thread.sleep(2500);
				isTask = searchForAvailableTasks(tasks);
			}
		}

	}

	public static boolean searchForAvailableTasks(ArrayList<Task> tasks) {
		boolean isTask = (tasks.size() == 0 ? false : true);
		while (!isTask) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isTask = (tasks.size() == 0 ? false : true);
		}
		return isTask;
	}
}
