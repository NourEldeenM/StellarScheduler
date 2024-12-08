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

    private void getAverageWaitingTime(List<Process> p) {
        int totalWaitingTime = 0;
        for (Process process : p) {
            totalWaitingTime += process.getWaitingTime();
        }
        System.out.println("Average waiting time: " + (totalWaitingTime / p.size()));
    }

    private void getAverageTurnaroundTime(List<Process> p) {
        int totalTurnaroundTime = 0;
        for (Process process : p) {
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        System.out.println(
                "Average turnaround time: " + totalTurnaroundTime + " / " + p.size() + " = "
                        + (totalTurnaroundTime / p.size()));
    }

    @Override
    public void simulate() {
        int remainingProcesses = processes.size();
        List<Process> endList = new ArrayList<>();

        while (remainingProcesses > 0) { // loop until finished processes is equal to the number of all
                                         // processes
            checkArrivedProcesses(endList);
            if (readyQueue.peek() != null) { // since we are in the loop, a process might not have arrived yet
                increaseTurnaroundTime(readyQueue.peek());
                increaseOtherProcessesWaitingTime(readyQueue.peek());

                readyQueue.peek().setRemainingBurstTime(readyQueue.peek().getRemainingBurstTime() - 1);
                if (readyQueue.peek().getRemainingBurstTime() == 0) {
                    remainingProcesses -= 1;
                    readyQueue.peek().setTurnAroundTime(
                            readyQueue.peek().getTurnaroundTime() + readyQueue.peek().getWaitingTime());
                    printFinishedProcess(readyQueue.peek());
                    endList.add(readyQueue.peek());
                    readyQueue.remove(readyQueue.peek());
                }
            }
        }
        getAverageWaitingTime(endList);
        getAverageTurnaroundTime(endList);
    }
}