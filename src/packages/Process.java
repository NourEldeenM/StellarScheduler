import java.util.*;

class Process {
    String name;
    // initialized values maybe changed
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingBurstTime;
    int startTime = 0;
    int completionTime = 0;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int quantum; //  for FCAI scheduling

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
    }
}
