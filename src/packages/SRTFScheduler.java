package packages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class SRTFScheduler extends Scheduler {
    private final PriorityQueue<Process> readyQueue;
    List<Process> endList = new ArrayList<>();
    int remainingProcesses = processes.size();


    public SRTFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
        readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingBurstTime));
    }

    // function to decrease arrival times of all processes by 1 until they reach
    // zero, then insert the arrived process in readyQueue(priorityQueue).
    public void checkArrivedProcesses() {
        for (Process process : processes) {
            // if the process arrived, and not in ready queue, and not served yet -> add it to ready queue
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
                process.setWaitingTime(process.getWaitingTime() + 1); // increment waiting time
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

    private void checkProcessFinished(Process process) {
        if (process.getRemainingBurstTime() <= 0) {
            remainingProcesses -= 1;
            process.setTurnAroundTime(process.getTurnaroundTime() + process.getWaitingTime());
            printFinishedProcess(process);
            endList.add(process);
            readyQueue.remove(process);
        }
    }

    private Process getAgedProcess() {
        for (Process process : readyQueue) {
            if (process.waitingTime % 50 == 0) { // check if a process is waiting for a long time (i.e. 50 units of time)
                return process;
            }
        }
        return null;
    }

    @Override
    public void simulate() {
        int startTime = 0;
        int endTime;
        int currentTime = 0;


        // loop until finished processes is equal to the number of all processes
        Process previousProcess = null;
        while (remainingProcesses > 0) {
            checkArrivedProcesses();
            if (readyQueue.peek() != null)
            { // a process might not have arrived yet
                Process currentProcess = readyQueue.peek();
                Process agedProcess = getAgedProcess(); // check if there is a process waiting for a very long time
                currentProcess = (agedProcess == null) ? currentProcess : agedProcess;
                if (previousProcess == null)
                    previousProcess = currentProcess;
                else if (currentProcess != previousProcess)
                {
                    // increase previous process context switch time (not made yet)
                    previousProcess.addExecutionSlice(startTime, currentTime, currentTime - startTime); // put previous process in gui
                    previousProcess = currentProcess; // previous process equals current process
                    currentTime += contextSwitchTime;
                    startTime = currentTime; // start time of current process = current time
                }
                currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - 1);
                currentTime++;
                increaseTurnaroundTime(currentProcess);
                increaseOtherProcessesWaitingTime(currentProcess);
                checkProcessFinished(currentProcess);
                if (remainingProcesses == 0)
                    currentProcess.addExecutionSlice(startTime, currentTime, currentTime - startTime);
            }
        }

        String schedulerName = "SRTF Scheduler";
        int averageWaitingTime = getAverageWaitingTime(endList);
        int averageTurnAroundTime = getAverageTurnaroundTime(endList);
        System.out.println("Average waiting time: " + averageWaitingTime);
        System.out.println("Average turnaround time: " + averageTurnAroundTime);
        new GUI(schedulerName, endList, averageWaitingTime, averageTurnAroundTime);

    }

}