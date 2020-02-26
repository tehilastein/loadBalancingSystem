package semesterProject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Slave {

	private ArrayList<Task> queue;
	private Task currTask;
	private boolean blocked;

	public Slave() {
		blocked = false;
		queue = new ArrayList<Task>();

	}

	public static void main(String[] args) throws Exception, IOException {
		System.out.println("****SLAVE****");
		Slave slave = new Slave();
		System.out.println("Waiting for connection...");
		args = new String[] { "127.0.0.1", "40000" };

		if (args.length != 2) {
			System.err.println("Usage: java NetworkClient <host name> <port number> ");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		try (Socket slaveSocket = new Socket(hostName, portNumber);
				ObjectOutputStream requestWriter = new ObjectOutputStream(slaveSocket.getOutputStream());
				ObjectInputStream requestReader = new ObjectInputStream(slaveSocket.getInputStream())) {
			System.out.println("Connected.");
			boolean execute = true;
			while (execute) {
				requestWriter.writeObject((Boolean) slave.blocked);
				if (!slave.blocked) {
					slave.currTask = (Task) requestReader.readObject();
					if (slave.currTask.getRuntime() > 4000) {
						slave.block();
					}
					System.out.println("I GOT A TASK.");
					slave.addToQueue(slave.currTask);
					Integer sum = slave.doNextTask();
					requestWriter.writeObject(sum);
				}
				while (slave.blocked) {
					if(slave.queue.size()!=0) {
						Integer sum = slave.doNextTask();
						requestWriter.writeObject(sum);
					}
					if (slave.getTotalRuntime() < (.75 * slave.currTask.getRuntime())) {
						slave.unblock();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Integer length() {
		return queue.size();
	}

	public Integer getTotalRuntime() {
		Integer totalRuntime = 0;
		for (int i = 0; i < queue.size(); i++) {
			totalRuntime += queue.get(i).getRuntime();
		}
		return totalRuntime;
	}

	public void addToQueue(Task task) {
		queue.add(task);
	}

	public synchronized Integer doNextTask() {
		currTask = queue.remove(0);
		try {
			Thread.sleep(currTask.getRuntime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return currTask.getDigitOne() + currTask.getDigitTwo();
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		blocked = true;
	}

	public void unblock() {
		blocked = false;
	}

	public Task removeTask() {
		return queue.remove(0);
	}
}
