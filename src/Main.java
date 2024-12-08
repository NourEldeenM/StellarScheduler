import packages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import packages.PriorityScheduler;
import packages.SJFScheduler;
import packages.SRTFScheduler;
import packages.FCAIScheduler;
import packages.Process;


//P1 yellow 0 17 4 4
//P2 red 3 6 9 3
//P3 green 4 10 3 5
//P4 blue 29 4 8 2

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number of processes:");
        int n = scanner.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println(
                    "Enter details for Process " + i + " (name color arrivalTime burstTime priority quantum):");
            String name = scanner.next();
            String color = scanner.next();
            int arrivalTime = scanner.nextInt();
            int burstTime = scanner.nextInt();
            int priority = scanner.nextInt();
            int quantum = scanner.nextInt();

            Process p = new Process(name, color, arrivalTime, burstTime, priority, quantum);
            processes.add(p);
        }

        System.out.println("Enter context switching time:");
        int contextSwitchTime = scanner.nextInt();

        System.out.println("Enter Round Robin Time Quantum:");
        int roundRobinTimeQuantum = scanner.nextInt();

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
