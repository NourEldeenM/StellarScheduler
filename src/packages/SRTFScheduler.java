package packages;

import java.util.List;

public class SRTFScheduler extends Scheduler {
    public SRTFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public void simulate() {

    }
}
