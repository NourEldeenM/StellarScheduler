package packages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class SRTFScheduler extends Scheduler {
    private PriorityQueue<Process> readyQueue;

    public SRTFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
        readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getBurstTime));
    }

    // function to return the process with least burst time
    public Process getLeastBurstTimProcess() {
        return readyQueue.peek();
    }

    // function to decrease arrival times of all processes by 1 until they reach
    // zero. Then insert the arrived process in readyQueue(priorityQueue).
    public void checkArrivedProcesses() {
        for (Process process : processes) {
            if (process.getArrivalTime() > 0)
                process.setArrivlTime(process.getArrivalTime() - 1);
            else {
                readyQueue.add(process);
                processes.remove(process);
            }
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

    private void getAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (Process process : readyQueue) {
            totalWaitingTime += process.getWaitingTime();
        }
        System.out.println("Average waiting time: " + (totalWaitingTime / readyQueue.size()));
    }

    private void getAverageTurnaroundTime() {
        int totalTurnaroundTime = 0;
        for (Process process : readyQueue) {
            totalTurnaroundTime += process.getWaitingTime();
        }
        System.out.println("Average turnaround time: " + (totalTurnaroundTime / readyQueue.size()));
    }

    @Override
    public void simulate() {
        int remainingProcesses = processes.size();

        while (remainingProcesses > 0) { // loop until finished processes is equal to the number of all
                                         // processes
            checkArrivedProcesses();
            Process currentProcess = getLeastBurstTimProcess();
            if (currentProcess != null) { // since we are in the loop, a process might not have arrived yet
                increaseTurnaroundTime(currentProcess);
                increaseOtherProcessesWaitingTime(currentProcess);
                currentProcess.setRemainingBurstTime(currentProcess.getBurstTime() - 1);
                if (currentProcess.getBurstTime() == 0) {
                    remainingProcesses -= 1;
                    printFinishedProcess(currentProcess);
                    currentProcess.setRemainingBurstTime(Integer.MAX_VALUE);
                }
            }
        }
        getAverageWaitingTime();
        getAverageTurnaroundTime();
    }
}