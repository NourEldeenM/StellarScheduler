package packages;

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

    public int getIndexOfLeastBurstTime() {
        return IntStream.range(0, processes.size())
                .filter(i -> processes.get(i).getBurstTime() > 0)
                .boxed()
                .min(Comparator.comparingInt(i -> processes.get(i).getBurstTime()))
                .orElse(-1);
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

    // function to increase waiting time for each process, and processing time for
    // the current process in cpu
    public void increaseWaitingTime(Process currentProcess) {
        for (Process process : readyQueue) {
            if (process.equals(currentProcess)) {
                process.setTurnAroundTime(process.getTurnaroundTime() + 1); // increment processing time
            } else {
                process.setWaitingTime(process.getWaitingTime() + 1); // increment waiting time
            }
        }
    }

    @Override
    public void simulate() {
        checkArrivedProcesses();
        int remainingProcesses = processes.size();

        while (remainingProcesses > 0) { // loop until finished processes is equal to the number of all
                                         // processes
            Process currentProcess = getLeastBurstTimProcess();
            if (currentProcess != null) { // since we are in the loop, a process might not have arrived yet
                increaseWaitingTime(currentProcess);
                currentProcess.setRemainingBurstTime(currentProcess.getBurstTime() - 1);
                if (currentProcess.getBurstTime() == 0) {
                    remainingProcesses -= 1;
                    System.out.println("**Process: " + currentProcess.getName() + " finished execution");
                    System.out.println("\tWaiting time: " + currentProcess.getWaitingTime());
                    System.out.println("\tTurnaround time: " + currentProcess.getTurnaroundTime());
                    readyQueue.remove();
                }
            }
        }
    }
}