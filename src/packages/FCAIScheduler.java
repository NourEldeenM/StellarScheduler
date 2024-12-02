package packages;

import java.util.*;

public class FCAIScheduler extends Scheduler {
    private List<String> executionHistory = new ArrayList<>();

    public FCAIScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
        calculateV1V2();
    }

    private void calculateV1V2() {
        int lastArrivalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(1);
        int maxBurstTime = processes.stream().mapToInt(Process::getBurstTime).max().orElse(1);

        double V1 = lastArrivalTime / 10.0;
        double V2 = maxBurstTime / 10.0;

        for (Process p : processes) {
            p.setV1V2(V1, V2);
        }
    }

    @Override
    public void simulate() {
        Queue<Process> readyQueue = new LinkedList<>();
        PriorityQueue<Process> priorityQueue = new PriorityQueue<>(
                Comparator.comparingDouble(Process::getFCAIFactor).reversed()
        );

        int currentTime = 0;

        // Sort processes by arrival time and add them to the ready queue
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        readyQueue.addAll(processes);

        while (!readyQueue.isEmpty() || !priorityQueue.isEmpty()) {
            // Move processes to the priority queue if they have arrived
            while (!readyQueue.isEmpty() && readyQueue.peek().getArrivalTime() <= currentTime) {
                Process arrivedProcess = readyQueue.poll();
                priorityQueue.add(arrivedProcess);
            }

            if (priorityQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = priorityQueue.poll();
            int quantum = currentProcess.getQuantum();
            boolean preempted = false;

            // Execute the first 40% of the quantum non-preemptively
            int nonPreemptiveExecution = (int) Math.ceil(0.4 * quantum);
            nonPreemptiveExecution = Math.min(nonPreemptiveExecution, currentProcess.getRemainingBurstTime());
            currentTime += nonPreemptiveExecution;
            currentProcess.reduceRemainingBurstTime(nonPreemptiveExecution);

            executionHistory.add("Time " + (currentTime - nonPreemptiveExecution) + ": Process " + currentProcess.getName() +
                    " executed for " + nonPreemptiveExecution + " units.");
            executionHistory.add("Remaining burstTime: " + currentProcess.getRemainingBurstTime() + " units.");

            // Check for preemption after non-preemptive execution
            while (!readyQueue.isEmpty() && readyQueue.peek().getArrivalTime() <= currentTime) {
                Process arrivedProcess = readyQueue.poll();
                priorityQueue.add(arrivedProcess);
            }

            if (!priorityQueue.isEmpty()) {
                Process nextProcess = priorityQueue.peek();
                if (nextProcess.getFCAIFactor() > currentProcess.getFCAIFactor() ||
                        (nextProcess.getArrivalTime() <= currentTime && nonPreemptiveExecution >= 0.4 * quantum)) {
                    // Preempt current process
                    currentProcess.setQuantum(currentProcess.getQuantum() + (quantum - nonPreemptiveExecution));
                    priorityQueue.add(currentProcess);
                    executionHistory.add("Time " + currentTime + ": Process " + currentProcess.getName() + " preempted.");
                    currentTime += contextSwitchTime;
                    preempted = true;
                }
            }

            // If not preempted, continue executing the remaining quantum
            if (!preempted) {
                int remainingExecution = Math.min(quantum - nonPreemptiveExecution, currentProcess.getRemainingBurstTime());
                currentTime += remainingExecution;
                currentProcess.reduceRemainingBurstTime(remainingExecution);

                executionHistory.add("Time " + (currentTime - remainingExecution) + ": Process " + currentProcess.getName() +
                        " executed for " + remainingExecution + " units.");
            }

            // Check if process has completed
            if (currentProcess.getRemainingBurstTime() == 0) {
                calculateMetrics(currentProcess, currentTime);
                executionHistory.add("Time " + currentTime + ": Process " + currentProcess.getName() + " completed.");
            } else if (!preempted) {
                // Update quantum and requeue the process
                currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                priorityQueue.add(currentProcess);
            }

            // Add context switch time if not preempted
            if (!preempted) {
                currentTime += contextSwitchTime;
            }
        }

        printMetrics();
    }



    @Override
    protected void printMetrics() {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        // Print header
        System.out.println("Process    Arrival    Burst    Priority    Quantum    Waiting    Turnaround");
        for (Process p : processes) {
            System.out.println(
                    p.getName() + "          " +
                            p.getArrivalTime() + "         " +
                            p.getBurstTime() + "       " +
                            p.getPriority() + "          " +
                            p.getQuantum() + "          " +
                            p.getWaitingTime() + "          " +
                            p.getTurnaroundTime()
            );
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();

            // Print quantum history for FCAI Scheduler
            System.out.println("Quantum History for " + p.getName() + ": " + p.getQuantumHistory());
        }

        // Print averages
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());

        // Print execution order
        System.out.println("\nExecution Order:");
        for (String event : executionHistory) {
            System.out.println(event);
        }
    }
}
