package packages;

import packages.Scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.Integer.compare;


public class PriorityScheduler extends Scheduler {
    public PriorityScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    private void log(String s) {
//        System.out.printf(s);
    }

    @Override
    public void simulate() {

        //sort processes by arrival time to enter it in ready queue
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        //ready queue declaration
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt((Process p) -> p.getPriority())
                        .thenComparingInt(Process::getArrivalTime));

        int currentTime = 0, completedProcesses = 0;
        Process processing = null;


        //simulate clock cycles untill processess are empty
        for (currentTime = 0; processing != null || completedProcesses < processes.size() || !readyQueue.isEmpty(); currentTime++) {
            //print current time
            log(currentTime + "\t");

            //handle completed process
            if (processing != null && processing.getRemainingBurstTime() == 0) {
                //compute process parameters
                processing.setCompletionTime(currentTime);
                processing.setTurnAroundTime(currentTime - processing.getArrivalTime());
                processing.setWaitingTime(processing.getTurnaroundTime() - processing.getBurstTime());
                //printing and logic
                log(processing.getName() + " completed\t");
                processing = null;

            }

            //extract all processes that are ready from processes to ready queue
            while (completedProcesses < processes.size() && processes.get(completedProcesses).getArrivalTime() == currentTime) {
                readyQueue.add(processes.get(completedProcesses++));
            }

            //extract the chosen process from queue
            if (!readyQueue.isEmpty()) {

                if (processing == null) {
                    processing = readyQueue.poll();
                } else {
                    //extract which has the most priority
                    Process expectedToRun = readyQueue.peek();
                    if (processing != null && expectedToRun.getPriority() < processing.getPriority()) {
                        readyQueue.add(processing);
                        processing = readyQueue.poll();
                    }
                }
            }

            //check if cpu has chose process to run or not
            if (processing != null) {
                log(processing.getName() + "\n");
                int startTime = currentTime;
                int endTime = startTime + 1;
                processing.setRemainingBurstTime(processing.getRemainingBurstTime() - 1);

                processing.addExecutionSlice(startTime, endTime, endTime - startTime);
            } else {
                log("CPU idle\n");
            }

        }

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("Priority Scheduler Metrics");
        System.out.println("Process\t\tArrival\t\tBurst\tCompletion\tWaiting\t\tTurnaround\tQuantum\t\tPriority");
        for (Process p : processes) {
            System.out.println(
                    p.getName() + "\t\t\t" +
                            p.getArrivalTime() + "\t\t\t" +
                            p.getBurstTime() + "\t\t" +
                            p.getCompletionTime() + "\t\t\t" +
                            p.getWaitingTime() + "\t\t\t" +
                            p.getTurnaroundTime() + "\t\t\t" +
                            p.getQuantum() + "\t\t\t" +
                            p.getPriority());
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());

        String schedulerName = "Priority Scheduler";
        new GUI(schedulerName, processes, (double) totalWaitingTime / processes.size(), (double) totalTurnaroundTime / processes.size());
    }
}
