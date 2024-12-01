package packages;

import java.util.*;

public class Process {
    String name;
    // initialized values maybe changed
    //For GUI, add other parameters later

    int arrivalTime;
    int burstTime;
    int priority;
    int remainingBurstTime;
    int startTime = -1;
    int completionTime = -1;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int quantum; //  for FCAI scheduling

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
    }
}
