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

        //sort processes by arraival time to enter it in ready queue
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        //ready queue declaration
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt((Process p) -> p.getPriority())
                        .thenComparingInt(Process::getArrivalTime));

        int currentTime=0;
        Process processing= null;

        //simulate clock cycles untill processess are empty
        for (currentTime = 0; processing!=null || !processes.isEmpty() || !readyQueue.isEmpty(); currentTime++) {
            //print current time
            System.out.printf("%d\t",currentTime);

            //handel completed process
            if (processing != null && processing.getRemainingBurstTime() == 0) {
                System.out.printf("%s completed\t", processing.getName());
                processing = null;
            }

            //extract all processes that are ready from processes to ready queue
            while (!processes.isEmpty() && processes.get(0).getArrivalTime() == currentTime) {
                readyQueue.add(processes.remove(0));
            }

            //extract the chosen process from queue
            if (!readyQueue.isEmpty()) {

                if(processing==null){
                    processing=readyQueue.poll();
                }else{
                    //extract which has the most priority
                    Process expectedToRun = readyQueue.peek();
                    if(processing!=null && expectedToRun.getPriority()<processing.getPriority()){
                        readyQueue.add(processing);
                        processing=readyQueue.poll();
                    }
                }
            }

            //check if cpu has chose process to run or not
            if (processing != null) {
                System.out.printf("%s\n", processing.getName());
                processing.setRemainingBurstTime(processing.getRemainingBurstTime() - 1);
            } else {
                System.out.println("CPU idle");
            }

        }

    }
}
