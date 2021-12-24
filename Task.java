import java.io.*;
import java.util.*;

public class Task {
	public static String work_dir;
	public static File tasks, completed;
	public static HashMap<Integer, List<String>> map_task;
	public static List<Integer> priority_task;
	public static HashMap<Integer, List<String>> map_completed;
	public static List<Integer> priority_completed;
	public static int priority_task_counter = 0;
	public static int priority_completed_counter = 0;

	public static void main(String args[]) {

		work_dir = System.getProperty("user.dir");
		tasks = new File("task.txt");
		completed = new File("completed.txt");

		if (!(args.length == 0)) {
			if (args[0].equals("help")) {
				help();
			} else if (args[0].startsWith("add")) {
				add(args);
			} else if (args[0].equals("ls")) {
				ls();
			} else if (args[0].startsWith("del")) {
				del(args);
			} else if (args[0].startsWith("done")) {
				done(args);
			} else if (args[0].startsWith("report")) {
				report();
			} else {
				System.out.println("\nCommand not found\n");
				help();
			}
		} else {
			help();
		}

	}

	public static void help() {
		System.out.println("Usage :- \n" +
				"$ ./task add 2 hello world		# Add a new item with priority 2 and text \"hello world\" to the list \n" +
				"$ ./task ls				# Show incomplete priority list items sorted by priority in ascending order \n" +
				"$ ./task del NUMBER			# Delete the incomplete item with the given priority number\n" +
				"$ ./task done NUMBER 			# Mark the incomplete item with the given PRIORITY_NUMBER as complete\n" +
				"$ ./task help 				# Show usage\n" +
				"$ ./task report				# Statistics");
	}

	public static void add(String[] add) {
		try {
			if (add[0].equals("add")) {
				int i = Integer.parseInt(add[1]);
				if (i >= 0) {
					String data = add[2];
					if (tasks.createNewFile()) {
						FileWriter fw = new FileWriter("task.txt", false);
						String s = i + " " + data + "\n";
						fw.write(s);
						fw.flush();fw.close();
					} else {
						//if task.txt exists
						generatingDatabaseTask();


						if (priority_task.contains(i)) {
							List<String> list = map_task.get(i);
							if(!list.contains(data)) {
								list.add(data);
								map_task.put(i, list);
							}
							else{
								System.out.println("Task already exists");
							}
						} else {
							priority_task.add(i);

							List<String> list = new ArrayList<>();
							list.add(data);

							map_task.put(i, list);
						}

						Collections.sort(priority_task);
						FileWriter fw = new FileWriter("task.txt", false);
						for (int l : priority_task) {
							List<String> list = map_task.get(l);
							if (list.size() > 1) {
								priorityWriter(list, l, fw);
							} else {
								String d = l + " " + list.get(0) + "\n";
								fw.write(d);
							}
						}
						fw.flush();
						fw.close();

					}
				}
			} else {
				System.out.println("Command invalid \n" +
						"$ ./task help 				# Show usage\n");
			}
		} catch (Exception e) {
			System.out.println("Command invalid \n" +
					"$ ./task help 				# Show usage\n");
		}
	}

	public static void ls() {
		try {
			if (tasks.exists()) {
				int j = 1;
				generatingDatabaseTask();

				for (int l : priority_task) {
					List<String> list = map_task.get(l);
					if (list.size() > 1) {
						priorityPrinter(list, l, j);
						j = j + list.size();
					} else {
						String s = j + ". " + list.get(0) + " [" + l + "]";
						System.out.println(s);
						j++;
					}

				}
				System.out.println();
			} else {
				System.out.println("No task added yet \n" +
						"$ ./task add 2 hello world		# Add a new item with priority 2 and text \"hello world\" to the list \n");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void del(String[] del) {
		try {
			if (del[0].equals("del")) {
				int i = Integer.parseInt(del[1]);

				if (tasks.exists()) {
					generatingDatabaseTask();
					i = i - 1;
					if (priority_task_counter >= i && i >= 0) {

						//duplication check
						int m = 0;
						String data = "";
						int num = 0;
						Scanner sc = new Scanner(tasks);
						while (sc.hasNextLine() && m<=i) {
							String line = sc.nextLine();
							String[] split = line.split(" ");
							data = line.replace(split[0] + " ", "");
							num = Integer.parseInt(split[0]);
							m++;
						}

						List<String> check = map_task.get(num);
						if(check.size()>1){
							check.remove(data);
							map_task.put(num,check);
						}
						else{
							System.out.println(priority_task);
							priority_task.remove(i);
							Collections.sort(priority_task);
						}

						FileWriter fw = new FileWriter("task.txt", false);
						for (int l : priority_task) {
							List<String> list = map_task.get(l);
							if (list.size() > 1) {
								priorityWriter(list, l, fw);
							} else {
								String s = l + " " + list.get(0);
								fw.write(s);
							}
						}
						fw.flush();
						fw.close();
					}
				} else {
					System.out.println("No task added yet \n" +
							"$ ./task add 2 \"hello world\"		# Add a new item with priority 2 and text \"hello world\" to the list \n");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void done(String[] done) {
		try {
			if (done[0].equals("done")) {

				generatingDatabaseTask();
				int i = Integer.parseInt(done[1]);
				i = i-1;

				if (priority_task_counter >= i && i >= 0) {

					//variable init
					int j = priority_task.get(i);
					String data1 = (map_task.get(j)).get(0);
					String s = j + " " + data1;

					//File creation
					if (completed.createNewFile()) {
						FileWriter fw = new FileWriter("completed.txt", false);
						fw.write(s);
						fw.flush();fw.close();
						priority_task.remove(i);
					} else {
						//if completed.txt exists
						generatingDatabaseCompleted();


						int m = 0;
						String data = "";
						int num = 0;
						Scanner sc = new Scanner(tasks);
						while (sc.hasNextLine() && m!=i) {
							String line = sc.nextLine();
							String[] split = line.split(" ");
							data = line.replace(split[0] + " ", "");
							num = Integer.parseInt(split[0]);
							m++;
						}

						if (priority_completed.contains(num)) {
							List<String> list = map_completed.get(num);
							list.add(data);
							map_completed.put(num, list);

						} else {
							priority_completed.add(num);

							List<String> list = new ArrayList<>();
							list.add(data);

							map_completed.put(num, list);

						}

						List<String> task_list = map_task.get(num);
						if(task_list.size() > 1){
							task_list.remove(data);
							map_task.put(num,task_list);
						}
						else
						{
							priority_task.remove(num);
							System.out.println(priority_task);
						}

						Collections.sort(priority_completed);
						Collections.sort(priority_task);

						FileWriter fw = new FileWriter("completed.txt", false);
						for (int l : priority_completed) {
							List<String> list = map_completed.get(l);
							if (list.size() > 1) {
								priorityWriter(list, l, fw);
							} else {
								String d = l + " " + list.get(0);
								fw.write(d);
							}
						}
						fw.flush();
						fw.close();

					}
					//execute deletion
					doneTask(priority_task);
				} else {

				}

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void doneTask(List<Integer> tasks ) {
		try {
			FileWriter fw = new FileWriter("task.txt", false);

			for (int l : tasks) {
				List<String> list = map_task.get(l);
				if (list.size() > 1) {
					priorityWriter(list, l, fw);
				} else {
					String s = l + " " + list.get(0) +"\n";
					fw.write(s);
				}
			}
			fw.flush();
			fw.close();
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	public static void report() {
		lsPending();
		lsCompleted();
	}

	public static void lsCompleted() {
		try {
			if (completed.exists()) {
				int j = 1;
				generatingDatabaseCompleted();
				System.out.println("Completed tasks : " + priority_completed_counter + "\n");

				for (int l : priority_completed) {
					List<String> list = map_completed.get(l);
					if (list.size() > 1) {
						priorityPrinter(list, l, j);
						j++;
					} else {
						String s = j + ". " + list.get(0) + " [" + l + "]";
						System.out.println(s);
						j++;
					}
				}
				System.out.println();
			}
			else{
				System.out.println("Completed tasks : 0");
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public static void lsPending(){
		try {
			if (tasks.exists()) {
				int j = 1;
				generatingDatabaseTask();
				System.out.println("Pending tasks : " + priority_task_counter +"\n");

				for(int l : priority_task){
					List<String> list = map_task.get(l);
					if(list.size()>1){
						priorityPrinter(list, l, j);
						j++;
					}
					else{
						String s = j + ". " + list.get(0) + " [" + l + "]";
						System.out.println(s);
					    j++;
					}
				}
				System.out.println();
			} else {
				System.out.println("Pending tasks : 0");
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public static void generatingDatabaseTask(){
		priority_task_counter = 0;
		map_task = new HashMap<>();
		priority_task = new ArrayList<>();
		try {
			Scanner sc = new Scanner(tasks);
			while (sc.hasNextLine()) {
				priority_task_counter++;
				String line = sc.nextLine();
				String[] split = line.split(" ");
				String data = line.replace(split[0]+" ","");
				int num = Integer.parseInt(split[0]);
				
				//Duplicate priority check
				if(priority_task.contains(num)){
					List<String> list = map_task.get(num);
					list.add(data);
					map_task.put(num,list);
				}
				else{
					priority_task.add(num);

					List<String> list = new ArrayList<>();
					list.add(data);

					map_task.put(num,list);
				}
				
			}
		sc.close();
		Collections.sort(priority_task);
		}
		catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}

	public static void generatingDatabaseCompleted(){
		priority_completed_counter = 0;
		map_completed = new HashMap<>();
		priority_completed = new ArrayList<>();
		try {
			Scanner sc = new Scanner(completed);
			while (sc.hasNextLine()) {
				priority_completed_counter++;
				String line = sc.nextLine();
				String[] split = line.split(" ");
				String data = line.replace(split[0]+" ","");
				int num = Integer.parseInt(split[0]);
				//Duplicate priority check
			if(priority_completed.contains(num)){
				List<String> list = map_completed.get(num);
				list.add(data);
				map_completed.put(num,list);
			}
			else{
				priority_completed.add(num);

				List<String> list = new ArrayList<>();
				list.add(data);

				map_completed.put(num,list);
			}
			
		}
		sc.close();
		Collections.sort(priority_completed);
			}
		catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}

	public static void priorityWriter(List<String> list, int priority, FileWriter fw){
		try {
			for (String l : list) {
				String s = priority + " " + l + "\n";
				fw.write(s);
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public static void priorityPrinter(List<String> list, int priority, int j){
		for(int k = 0; k<list.size();k++){
			String s = j+k + ". " + list.get(k) + " [" + priority + "]";
			System.out.println(s);
		}

	}

}
