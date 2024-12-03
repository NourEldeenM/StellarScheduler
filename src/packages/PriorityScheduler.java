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

    @Override
    public void simulate() {


        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getPriority));

        int currentTime=0;
        Process processing= null;


        for (currentTime = 0; processing!=null||!processes.isEmpty() || !readyQueue.isEmpty(); currentTime++) {
            System.out.printf("%d\t",currentTime);

            if (processing != null && processing.getRemainingBurstTime() == 0) {
                System.out.printf("%s completed\t", processing.getName());
                processing = null;
            }

            while (!processes.isEmpty() && processes.get(0).getArrivalTime() == currentTime) {
                readyQueue.add(processes.remove(0));
                if (processing != null && processing.getPriority() > readyQueue.peek().getPriority()) {
                    readyQueue.add(processing);
                    processing=readyQueue.poll();
                }
            }


            if (processing == null && !readyQueue.isEmpty()) {
                processing = readyQueue.poll();
            }
            if (processing != null) {
                System.out.printf("%s\n", processing.getName());
                processing.setRemainingBurstTime(processing.getRemainingBurstTime() - 1);
            } else {
                System.out.println("CPU idle");
            }
        }

    }
}
