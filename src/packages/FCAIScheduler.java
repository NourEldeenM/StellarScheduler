package packages;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.LinkedList;

public class FCAIScheduler extends Scheduler {

    public FCAIScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }


    @Override
    public void simulate() {
    }
}