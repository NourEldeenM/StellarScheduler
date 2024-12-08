package packages;

import java.util.*;

public class FCAIScheduler extends Scheduler {

    public FCAIScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    public static double getLastArrivalTime(List<Process> Processes) {
        double lastArrivalTime = 0;
        for (Process p : Processes) {
            lastArrivalTime = Math.max(lastArrivalTime, p.getArrivalTime());
        }
        return lastArrivalTime;
    }

    public static double getMaxBurstTime(List<Process> Processes) {
        double maxBurstTime = 0;
        for (Process p : Processes) {
            maxBurstTime = Math.max(maxBurstTime, p.getBurstTime());
        }
        return maxBurstTime;
    }

    public static int calculateFCAIFactor(Process process, List<Process> Processes) {
        double V1 = Math.max(getLastArrivalTime(Processes) / 10.0, 1.0);
        double V2 = Math.max(getMaxBurstTime(Processes) / 10.0, 1.0);
        return (int) ((10 - process.getPriority())
                + Math.ceil(process.getArrivalTime() / V1)
                + Math.ceil(process.getRemainingBurstTime() / V2));
    }

    @Override
    public void simulate() {
        List<Process> Processes = new ArrayList<>(processes);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        List<Process> readyQueue = new ArrayList<>();
        Map<String, List<Integer>> quantumLog = new HashMap<>();
        Map<String, List<Integer>> FCAIFactorLog = new HashMap<>();

        System.out.println();
        System.out.println("Processes:");
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%n",
                "Process", "Arrival", "Burst", "Priority", "Quantum", "Waiting", "Turnaround");
        for (Process p : Processes) {
            System.out.printf("%-10s%-10d%-10d%-10d%-10d%-10d%-10d%n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority(),
                    p.getQuantum(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());
        }

        System.out.println("\nDetailed Execution Timeline: ");
        for (Process p : Processes) {
            p.updateFCAIFactor(calculateFCAIFactor(p, Processes));
            quantumLog.put(p.getName(), new ArrayList<>(List.of(p.getQuantum())));
            FCAIFactorLog.put(p.getName(), new ArrayList<>(List.of(p.getFCAIFactor())));
        }

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            while (!processes.isEmpty() && processes.getFirst().getArrivalTime() <= currentTime) {
                Process nextProcess = processes.removeFirst();
                nextProcess.updateFCAIFactor(calculateFCAIFactor(nextProcess, Processes));
                if (!readyQueue.contains(nextProcess)) {
                    readyQueue.add(nextProcess);
                }
            }

            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.removeFirst();
                int executionTime = (int) Math.ceil(current.getQuantum() * 0.4);
                boolean preempted = false;
                int runningTime = 0;
                int startTime = currentTime;

                while (runningTime < current.getQuantum() && current.getRemainingBurstTime() > 0) {

                    while (!processes.isEmpty() && processes.getFirst().getArrivalTime() <= currentTime) {
                        Process nextProcess = processes.removeFirst();
                        nextProcess.updateFCAIFactor(calculateFCAIFactor(nextProcess, Processes));
                        if (!readyQueue.contains(nextProcess)) {
                            readyQueue.add(nextProcess);
                        }
                    }

                    if (runningTime >= executionTime && !readyQueue.isEmpty()) {
                        List<Process> temp = new ArrayList<>(readyQueue);
                        temp.sort(Comparator.comparingDouble(Process::getFCAIFactor));
                        Process nextProcess = temp.getFirst();
                        if (nextProcess.getFCAIFactor() < current.getFCAIFactor()) {
                            preempted = true;
                            current.quantum += (current.getQuantum() - runningTime);
                            readyQueue = temp;
                            readyQueue.remove(current);
                            readyQueue.add(current);
                            break;
                        }
                    }
                    currentTime++;
                    current.reduceRemainingBurstTime(1);
                    runningTime++;
                }

                int endTime = currentTime;
                System.out.println("Time " + startTime + "-" + endTime + " : " + current.getName() + " executes for " + (endTime - startTime) + " units of time");
                current.addExecutionSlice(startTime, endTime, endTime - startTime);

                if (current.getRemainingBurstTime() == 0 && !readyQueue.isEmpty()) {
                    Process nextProcess = readyQueue.getFirst();
                    if (nextProcess.getRemainingBurstTime() > 0 && !readyQueue.contains(nextProcess)) {
                        readyQueue.add(nextProcess);
                    }
                }

                quantumLog.putIfAbsent(current.getName(), new ArrayList<>());
                FCAIFactorLog.putIfAbsent(current.getName(), new ArrayList<>());

                if (current.getRemainingBurstTime() > 0) {
                    if (!preempted) {
                        current.quantum = current.quantum + 2;
                    }
                    current.updateFCAIFactor(calculateFCAIFactor(current, Processes));
                    quantumLog.get(current.getName()).add(current.quantum);
                    FCAIFactorLog.get(current.getName()).add(current.getFCAIFactor());
                    if (!readyQueue.contains(current)) {
                        readyQueue.add(current);
                    }
                } else {
                    System.out.println("Process " + current.getName() + ": " + "completed.");
                    current.turnaroundTime = currentTime - current.getArrivalTime();
                    current.waitingTime = current.turnaroundTime - current.getBurstTime();
                    if (current.waitingTime < 0) {
                        current.waitingTime = 0;
                    }
                    totalWaitingTime += current.waitingTime;
                    totalTurnaroundTime += current.turnaroundTime;
                }
            } else {
                currentTime++;
            }
        }

        double averageWaitingTime = (double) totalWaitingTime / Processes.size();
        double averageTurnAroundTime = (double) totalTurnaroundTime / Processes.size();

        printMetrics(Processes, averageWaitingTime, averageTurnAroundTime, quantumLog, FCAIFactorLog);

        String schedulerName = "FCAI Scheduler";
        new GUI(schedulerName, Processes, averageWaitingTime, averageTurnAroundTime);
    }


    public void printMetrics(
            List<Process> Processes,
            double averageWaitingTime,
            double averageTurnAroundTime,
            Map<String, List<Integer>> quantumLog,
            Map<String, List<Integer>> FCAIFactorLog) {

        System.out.println("\nProcesses:");
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%n",
                "Process", "Arrival", "Burst", "Priority", "Quantum", "Waiting", "Turnaround");
        for (Process p : Processes) {
            System.out.printf("%-10s%-10d%-10d%-10d%-10d%-10d%-10d%n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority(),
                    p.getQuantum(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());
        }

        System.out.println("\n\nAverage Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnAroundTime);

        System.out.println("\nQuantum History Updates For each process:");
        for (Map.Entry<String, List<Integer>> entry : quantumLog.entrySet()) {
            System.out.println("Process#: " + entry.getKey() + " | Quantum Times: " + entry.getValue());
        }

        System.out.println("\nFCAI History Updates For each process:");
        for (Map.Entry<String, List<Integer>> entry : FCAIFactorLog.entrySet()) {
            System.out.println("Process#: " + entry.getKey() + " | FCAI Factors: " + entry.getValue());
        }
    }

}

//P1 yellow 0 17 4 4
//P2 red 3 6 9 3
//P3 green 4 10 3 5
//P4 blue 29 4 8 2
