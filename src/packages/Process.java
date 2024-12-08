package packages;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private String name;
    private String color;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingBurstTime;
    private int startTime = -1;
    int completionTime = -1;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int quantum; // For FCAI scheduling
    private double V1; // Last arrival time of all processes divided by 10
    private double V2; // Max burst time of all processes divided by 10
    private List<Integer> quantumHistory = new ArrayList<>();
    private int FCAIFactor;
    private List<ExecutionSlice> executionHistory;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
        this.quantum = quantum;
        this.executionHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivlTime(int time) {
        arrivalTime = time;
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

    public void setRemainingBurstTime(int t) {
        remainingBurstTime = t;
    }

    public void reduceRemainingBurstTime(int time) {
        this.remainingBurstTime -= time;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int t) {
        completionTime=t;
    }

    public void setWaitingTime(int unitsOfTime) {
        waitingTime = unitsOfTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setTurnAroundTime(int time) {
        turnaroundTime = time;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantumHistory.add(quantum);
        this.quantum = quantum;
    }

    public List<Integer> getQuantumHistory() {
        return quantumHistory;
    }

    public void setV1V2(double V1, double V2) {
        this.V1 = V1;
        this.V2 = V2;
    }

    public void updateFCAIFactor(int factor) {
        this.FCAIFactor = factor;
    }

    public int getFCAIFactor() {
        return FCAIFactor;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getColor() {
        return color;
    }

    public void addExecutionSlice(int startTime, int endTime, int executionTime) {
        ExecutionSlice slice = new ExecutionSlice(startTime, endTime, executionTime, this.name);
        this.executionHistory.add(slice);
    }

    public List<ExecutionSlice> getExecutionHistory() {
        return executionHistory;
    }

    public static class ExecutionSlice {
        private int startTime;
        private int endTime;
        private int executionTime;
        private String processName;

        public ExecutionSlice(int startTime, int endTime, int executionTime, String processName) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.executionTime = executionTime;
            this.processName = processName;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public int getExecutionTime() {
            return executionTime;
        }

        public String getProcessName() {
            return processName;
        }
    }
}
