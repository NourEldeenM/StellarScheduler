package packages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SJFScheduler extends Scheduler {
    private static final int AGING_FACTOR = 1;
    private static final int MAX_WAIT_THRESHOLD = 20;

    public SJFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime) // to sort each process first by arrival time
                .thenComparingInt(Process::getBurstTime));  // and if multiple processes arrive at the same time, it sorts them by shortest job first
        int currentTime = 0;
        List<Process> scheduledProcesses = new ArrayList<>();
        while (!processes.isEmpty()) {
            List<Process> availableProcesses = new ArrayList<>();
            // put all current processes in the availability list
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime) {
                    availableProcesses.add(process);
                }
            }
            if (availableProcesses.isEmpty()) {
                currentTime = processes.get(0).getArrivalTime();
                continue;
            }
            for (Process process : processes) {
                if (!availableProcesses.contains(process)) {
                    continue;
                }
                int waitingTime = currentTime - process.getArrivalTime();
                if (waitingTime > MAX_WAIT_THRESHOLD) {
                    int agingPriority = (waitingTime / MAX_WAIT_THRESHOLD) * AGING_FACTOR;
                    process.setPriority(agingPriority);
                }
            }
            Process selectedProcess = Collections.min(availableProcesses,
                    Comparator.comparingInt(p -> p.getBurstTime() - p.getPriority()));
            int startTime = currentTime;
            selectedProcess.setStartTime(currentTime);
            currentTime += selectedProcess.getBurstTime();
            int endTime = currentTime;

            selectedProcess.addExecutionSlice(startTime, endTime, endTime - startTime);
            calculateMetrics(selectedProcess, currentTime);
            scheduledProcesses.add(selectedProcess);
            processes.remove(selectedProcess);
        }

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process process : scheduledProcesses) {
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        double averageWaitingTime = totalWaitingTime / scheduledProcesses.size();
        double averageTurnAroundTime = totalTurnaroundTime / scheduledProcesses.size();
        this.processes = scheduledProcesses;
        printMetrics();

        String schedulerName = "SJF Scheduler";
        new GUI(schedulerName, scheduledProcesses, averageWaitingTime, averageTurnAroundTime);
    }
}
