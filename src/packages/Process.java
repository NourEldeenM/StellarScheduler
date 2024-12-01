package packages;

public class Process {
    // initialized values maybe changed
    //For GUI, add other parameters later


    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingBurstTime;
    private int startTime = -1;
    int completionTime = -1;
    int waitingTime = 0;
    int turnaroundTime = 0;
    private int quantum; // For FCAI scheduling
    private double V1; // Last arrival time of all processes divided by 10
    private double V2; // Max burst time of all processes divided by 10

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void reduceRemainingBurstTime(int time) {
        this.remainingBurstTime -= time;
    }


    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }


    public void setV1V2(double V1, double V2) {
        this.V1 = V1;
        this.V2 = V2;
    }

    public int getFCAIFactor() {
        return (int) Math.ceil((10 - priority) +
                (arrivalTime / V1) +
                (remainingBurstTime / V2));
    }
}
