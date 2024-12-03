package packages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class FCAIScheduler extends Scheduler {

    public FCAIScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {
        int currentTime = 0;
        int totalProcesses = processes.size();
        int completedProcesses = 0;

        // Calculate V1 and V2 (last arrival time and max burst time)
        int lastArrivalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(1);
        int maxBurstTime = processes.stream().mapToInt(Process::getBurstTime).max().orElse(1);

        double V1 = lastArrivalTime / 10.0;
        double V2 = maxBurstTime / 10.0;

        // Set V1 and V2 for all processes and calculate the initial FCAI factors
        for (Process process : processes) {
            process.setV1V2(V1, V2);
        }

        // Priority Queue to manage processes based on FCAI factor (ascending order)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingDouble(Process::getFCAIFactor));

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completedProcesses < totalProcesses) {
            // Add processes that have arrived to the readyQueue
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime && process.getRemainingBurstTime() > 0 && !readyQueue.contains(process)) {
                    readyQueue.add(process);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Select process with the lowest FCAI factor
            Process currentProcess = readyQueue.poll();
            if (currentProcess.getStartTime() == -1) {
                currentProcess.setStartTime(currentTime); // Mark the start time
            }

            // Execute the process for 1 unit of time
            int executionTime = 1;
            int start = currentTime;
            currentTime += executionTime;
            int end = currentTime;
            currentProcess.reduceRemainingBurstTime(executionTime);

            // Log execution details
            System.out.printf("Time %d-%d: %s has executed for %d units of time.%n", start, end, currentProcess.getName(), executionTime);

            // Update FCAI Factor after execution
            currentProcess.updateFCAIFactor();

            // **After 40% of quantum, start continuous checking for preemption**
            if (currentProcess.getRemainingBurstTime() <= currentProcess.getQuantum() * 0.6) {
                while (currentProcess.getRemainingBurstTime() > 0) {
                    boolean preempted = false;

                    // Recheck the readyQueue every time unit for a process with a lower FCAI factor
                    for (Process process : readyQueue) {
                        if (process.getFCAIFactor() < currentProcess.getFCAIFactor()) {
                            // Preempt current process if another process has a lower FCAI factor
                            System.out.printf("%s is interrupted due to %s having a lower FCAI factor.%n", currentProcess.getName(), process.getName());
                            currentProcess.setQuantum(currentProcess.getQuantum() + 2); // Add 2 if the process completed 40%
                            readyQueue.add(currentProcess); // Re-add the current process
                            preempted = true;
                            break;
                        }
                    }

                    if (preempted) {
                        // Exit the loop if the process is preempted
                        break;
                    }

                    // If the process is not preempted, execute it for 1 unit of time
                     executionTime = 1;  // Execute for 1 time unit
                    currentTime += executionTime;  // Increment time by 1 unit
                    currentProcess.reduceRemainingBurstTime(executionTime);  // Reduce burst time

                    // Log execution details
                    System.out.printf("Time %d-%d: %s has executed for 1 unit of time.%n", currentTime - 1, currentTime, currentProcess.getName(), executionTime);

                    // Update FCAI Factor after execution
                    currentProcess.updateFCAIFactor();

                    // If the process is still not finished, continue checking
                }
            }

            // If the process is not preempted, check for interruption or completion
            if (currentProcess.getRemainingBurstTime() > 0) {
                // Process is interrupted and added back with updated quantum
                System.out.println("New Process " + currentProcess.getName() + " quantum: " + currentProcess.getQuantum());
                System.out.println("New FCAI factor: " + currentProcess.getFCAIFactor());
                currentProcess.setQuantum(currentProcess.getQuantum() + 2);  // Add 2 if it's preempted
                readyQueue.add(currentProcess);
            } else {
                // Process is completed
                calculateMetrics(currentProcess, currentTime);
                System.out.printf("%s is done.%n", currentProcess.getName());
                completedProcesses++;
            }

        }
    }


    @Override
    protected void printMetrics() {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("\nProcess Execution Details:");
        System.out.println("Process    Arrival    Burst    Priority    Quantum History    Waiting    Turnaround");
        for (Process p : processes) {
            System.out.println(
                    p.getName() + "          " +
                            p.getArrivalTime() + "         " +
                            p.getBurstTime() + "       " +
                            p.getPriority() + "          " +
                            p.getQuantumHistory() + "          " +
                            p.getWaitingTime() + "          " +
                            p.getTurnaroundTime()
            );
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }
        System.out.println("\nAverage Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());
    }
}
