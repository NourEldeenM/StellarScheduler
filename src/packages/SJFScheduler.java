package packages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SJFScheduler extends Scheduler {
    public SJFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime).thenComparingInt(Process::getBurstTime));

        int currentTime = 0;
        List<Process> scheduledProcesses = new ArrayList<>();

        while (!processes.isEmpty()) {
            List<Process> availableProcesses = new ArrayList<>();
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime) {
                    availableProcesses.add(process);
                }
            }
            if (availableProcesses.isEmpty()) {
                currentTime = processes.get(0).getArrivalTime();
                currentTime += contextSwitchTime;
                continue;
            }
            for (Process process : processes) {
                if (!availableProcesses.contains(process)) {
                    process.setPriority(process.getPriority() - 1);
                }
            }
            Process shortestProcess = Collections.min(availableProcesses, Comparator.comparingInt(Process::getBurstTime));
            shortestProcess.setStartTime(currentTime);
            currentTime += shortestProcess.getBurstTime();
            calculateMetrics(shortestProcess, currentTime);
            scheduledProcesses.add(shortestProcess);
            processes.remove(shortestProcess);
            currentTime += contextSwitchTime;
        }
        this.processes = scheduledProcesses;
        printMetrics();
    }
}
