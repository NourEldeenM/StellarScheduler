package packages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class SRTFScheduler extends Scheduler {
    private final PriorityQueue<Process> readyQueue;

    public SRTFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
        readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingBurstTime));
    }

    // function to return the process with least burst time
    public Process getLeastBurstTimProcess() {
        return readyQueue.peek();
    }

    // function to decrease arrival times of all processes by 1 until they reach
    // zero. Then insert the arrived process in readyQueue(priorityQueue).
    public void checkArrivedProcesses(List<Process> endList) {
        for (Process process : processes) {
            if (process.getArrivalTime() <= 0 && !readyQueue.contains(process) && !endList.contains(process))
                readyQueue.add(process);
            else if (process.getArrivalTime() > 0)
                process.setArrivlTime(process.getArrivalTime() - 1);
        }
    }

    // increase processing time for the current process in cpu
    public void increaseTurnaroundTime(Process currentProcess) {
        for (Process process : readyQueue) {
            if (process.equals(currentProcess)) {
                process.setTurnAroundTime(process.getTurnaroundTime() + 1); // increment processing time
                return;
            }
        }
    }

    // increase waiting time for processes not in cpu
    public void increaseOtherProcessesWaitingTime(Process currentProcess) {
        for (Process process : readyQueue) {
            if (!process.equals(currentProcess)) {
                process.setWaitingTime(process.getWaitingTime() + 1); // increment processing time
            }
        }
    }

    public void printFinishedProcess(Process finishedProcess) {
        System.out.println("**Process: " + finishedProcess.getName() + " finished execution");
        System.out.println("\tWaiting time: " + finishedProcess.getWaitingTime());
        System.out.println("\tTurnaround time: " + finishedProcess.getTurnaroundTime());
    }

    private int getAverageWaitingTime(List<Process> p) {
        int totalWaitingTime = 0;
        for (Process process : p) {
            totalWaitingTime += process.getWaitingTime();
        }
        return (totalWaitingTime / p.size());
    }

    private int getAverageTurnaroundTime(List<Process> p) {
        int totalTurnaroundTime = 0;
        for (Process process : p) {
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        return (totalTurnaroundTime / p.size());
    }

    // current.addExecutionSlice(startTime, endTime, endTime - startTime);

    @Override
    public void simulate() {
        int startTime;
        int endTime;
        int currentTime = 0;
        int remainingProcesses = processes.size();
        List<Process> endList = new ArrayList<>();

        int currentTime = 0;  // Keep track of current time in the simulation

        while (remainingProcesses > 0) { // loop until finished processes is equal to the number of all processes
            checkArrivedProcesses(endList);
            if (readyQueue.peek() != null) { // a process might not have arrived yet
                Process currentProcess = readyQueue.peek();

                // Track the execution slice: record start time, end time, and execution duration
                int startTime = currentTime;

                // Decrease remaining burst time (1 unit of execution)
                currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - 1);
                currentTime++; // Move forward in time

                // Add the execution slice to the process history
                currentProcess.addExecutionSlice(startTime, currentTime, 1); // 1 unit of time executed

                increaseTurnaroundTime(currentProcess);
                increaseOtherProcessesWaitingTime(currentProcess);

                // Check if process is finished
                if (currentProcess.getRemainingBurstTime() == 0) {
                    remainingProcesses -= 1;
                    currentProcess.setTurnAroundTime(currentProcess.getTurnaroundTime() + currentProcess.getWaitingTime());
                    printFinishedProcess(currentProcess);
                    endList.add(currentProcess);
                    readyQueue.remove(currentProcess);
                }
            }
        }
        getAverageWaitingTime(endList);
        getAverageTurnaroundTime(endList);

        String schedulerName = "SRTF Scheduler";
        new GUI(schedulerName, endList, 6, 15);

        int averageWaitingTime = getAverageWaitingTime(endList);
        int averageTurnAroundTime = getAverageTurnaroundTime(endList);
        System.out.println("Average waiting time: " + averageWaitingTime);
        System.out.println("Average turnaround time: " + averageTurnAroundTime);
        String schedulerName = "SRTF Scheduler";
        new GUI(schedulerName, endList, averageWaitingTime, averageTurnAroundTime);
        
    }

}