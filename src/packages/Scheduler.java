abstract class Scheduler {
    List<Process> processes;
    int contextSwitchTime;

    public Scheduler(List<Process> processes, int contextSwitchTime) {
        this.processes = processes;
        this.contextSwitchTime = contextSwitchTime;
    }

    abstract void simulate();


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

//        NOTES:  (From Lab, check others)
//    ⇨ In case there is a Context Switching cost:
//        Waiting time = Turnaround time - Processing time
    }

    protected void printMetrics() {

    }
}
