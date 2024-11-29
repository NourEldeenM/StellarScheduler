public class Main {   //or rename
    public static void main(String[] args) {


        //change depending on how we input processes
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number of processes:");
        int n = scanner.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Enter details for Process %d (name color arrivalTime burstTime priority):\n", i + 1);
            String name = scanner.next();
            String color = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            processes.add(new Process(name, color, arrivalTime, burstTime, priority));
        }

        System.out.println("Enter context switching time:");
        int contextSwitchTime = scanner.nextInt();

        System.out.println("Select scheduler: 1. Priority 2. SJF 3. SRTF 4. FCAI");
        int choice = scanner.nextInt();

        Scheduler scheduler = null;
        switch (choice) {
            case 1:
                scheduler = new PriorityScheduler(processes, contextSwitchTime);
                break;
            case 2:
                scheduler = new SJFScheduler(processes, contextSwitchTime);
                break;
            case 3:
                scheduler = new SRTFScheduler(processes, contextSwitchTime);
                break;
            case 4:
                scheduler = new FCAIScheduler(processes, contextSwitchTime);
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        scheduler.simulate();
    }
}
