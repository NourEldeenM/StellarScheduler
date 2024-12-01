package packages;

import java.util.List;

public abstract class Scheduler {
    List<Process> processes;
    int contextSwitchTime;

    public Scheduler(List<Process> processes, int contextSwitchTime) {
        this.processes = processes;
        this.contextSwitchTime = contextSwitchTime;
    }

    public abstract void simulate();   // All 4 Scheduler should implement this


    /*
Key Definitions:
Completion Time (CT):
    The time at which a process finishes execution.
    This is recorded when the CPU finishes the process after accounting for any waiting or execution time.

Turnaround Time (TAT):
    The total time spent by a process in the system (from arrival to completion).
    TAT = CT - AT;    completion time, arrival time

Waiting Time (WT):
    The total time a process spends waiting in the ready queue, excluding its execution time.
    Formula:
        WT=TAT−BT               Turnaround Time, Burst Time.
     */


    protected void calculateMetrics(Process p, int currentTime) {
        p.completionTime = currentTime;
        // TAT = Completion Time - Arrival Time
        p.turnaroundTime = p.completionTime - p.getArrivalTime();
        // WT = Turnaround Time - Burst Time
        p.waitingTime = p.turnaroundTime - p.getBurstTime();
        if (p.getWaitingTime() < 0) {
            p.waitingTime = 0;
        }
    }


    protected void printMetrics() {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

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
        }
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());
    }

}