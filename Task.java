package semesterProject;

import java.util.Random;

public class Task implements java.io.Serializable{

	private static Integer taskNum = 0;
	private Integer taskID;
	private Integer runtime;
	private Random random;
	private int digitOne;
	private int digitTwo;
	private int sum;
	private int clientID;
	
	public Task(int one, int two) {
		taskID = taskNum++;
		random = new Random();
		runtime = random.nextInt((5000-1)+1);
		digitOne= one; 
		digitTwo = two;
		sum = 0;
	}
	
	public Integer getID() {
		return this.taskID;
	}
	
	public Integer getRuntime() {
		return this.runtime;
	}
	
	public int getDigitOne() {
		return digitOne;
	}
	
	public int getDigitTwo() {
		return digitTwo;
	}
	
	public void setSum(int sum) {
		this.sum = sum;
	}
	
	public int getSum() {
		return sum;
	}
	
	public void setClient(int clientID) {
		this.clientID = clientID;
	}
	
	public int getClient() {
		return clientID;
	}
}
