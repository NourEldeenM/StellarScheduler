package packages;

import java.util.*;

public class Process {
    String name;
    // initialized values maybe changed
    //chatGPT says  int startTime = 0 and int completionTime = 0 should be initialized with -1 (don't know)
    //For GUI, add other parameters later

    int arrivalTime;
    int burstTime;
    int priority;
    int remainingBurstTime;
    int startTime = 0;
    int completionTime = 0;
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
