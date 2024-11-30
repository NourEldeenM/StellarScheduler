package packages;


import java.util.List;

public class SJFScheduler extends Scheduler {
    public SJFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {

    }
}
