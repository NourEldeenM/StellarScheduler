package packages;

import packages.Scheduler;

import java.util.List;

public class PriorityScheduler extends Scheduler {
    public PriorityScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {

    }
}
