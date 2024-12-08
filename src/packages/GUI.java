package packages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame {
    public GUI(
            String schedulerName,
            List<Process> processes,
            double avgWaitingTime,
            double avgTurnaroundTime
    ) {

        setTitle(schedulerName + " - CPU Scheduling Results");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(schedulerName + " - CPU Scheduling Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        JPanel tablePanel = createProcessTablePanel(processes);
        mainPanel.add(tablePanel);

        JPanel timelinePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawExecutionTimeline(g, processes);
            }
        };
        timelinePanel.setPreferredSize(new Dimension(5000, 250));
        timelinePanel.setBorder(BorderFactory.createTitledBorder("Processes Execution Timeline"));
        mainPanel.add(timelinePanel);

        JPanel summaryPanel = createSummaryPanel(schedulerName, avgWaitingTime, avgTurnaroundTime);
        mainPanel.add(summaryPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);

        setVisible(true);
    }

    private JPanel createProcessTablePanel(List<Process> processes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Process", "Arrival Time", "Burst Time", "Priority", "Waiting Time", "Turnaround Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable processTable = new JTable(tableModel);

        for (Process process : processes) {
            tableModel.addRow(new Object[]{
                    process.getName(),
                    process.getArrivalTime(),
                    process.getBurstTime(),
                    process.getPriority(),
                    process.getWaitingTime(),
                    process.getTurnaroundTime()
            });
        }

        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Processes Table"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel(String schedulerName, double avgWaitingTime, double avgTurnaroundTime) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Summary"));

        panel.add(new JLabel("Scheduler Name: " + schedulerName));
        panel.add(new JLabel("Average Waiting Time: " + avgWaitingTime));
        panel.add(new JLabel("Average Turnaround Time: " + avgTurnaroundTime));


        return panel;
    }

    private JPanel createQuantumHistoryPanel(Map<String, List<Integer>> quantumHistory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            String processName = entry.getKey();
            List<Integer> quantumValues = entry.getValue();
            panel.add(new JLabel("Process " + processName + ": " + quantumValues));
        }

        return panel;
    }

    private void drawExecutionTimeline(Graphics g, List<Process> processes) {
        int x = 50;
        int y = 50;
        int blockWidth = 100;
        int blockHeight = 50;
        int gap = 10;

        List<Process.ExecutionSlice> timelineEvents = new ArrayList<>();

        for (Process process : processes) {
            for (Process.ExecutionSlice slice : process.getExecutionHistory()) {
                timelineEvents.add(new Process.ExecutionSlice(slice.getStartTime(), slice.getEndTime(), slice.getExecutionTime(), slice.getProcessName()));
            }
        }

        timelineEvents.sort(Comparator.comparingInt(Process.ExecutionSlice::getStartTime));


        for (Process.ExecutionSlice event : timelineEvents) {
            int startTime = event.getStartTime();
            int endTime = event.getEndTime();
            int executionTime = event.getExecutionTime();
            String processName = event.getProcessName();
            Process process = findProcessByName(processes, processName);
            Color color = getColorByName(process.getColor());
            g.setColor(color);
            g.fillRect(x, y, blockWidth, blockHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, blockWidth, blockHeight);

            g.drawString(processName, x + 10, y + 25);

            String timeRange = startTime + " - " + endTime + " (" + executionTime + " units)";
            g.drawString(timeRange, x + 5, y - 5);

            x += blockWidth + gap;
        }
    }

    private Process findProcessByName(List<Process> processes, String processName) {
        for (Process process : processes) {
            if (process.getName().equals(processName)) {
                return process;
            }
        }
        return null;
    }

    private Color getColorByName(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            case "green" -> Color.GREEN;
            case "yellow" -> Color.YELLOW;
            case "orange" -> Color.ORANGE;
            case "pink" -> Color.PINK;
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "cyan" -> Color.CYAN;
            case "magenta" -> Color.MAGENTA;
            case "gray", "grey" -> Color.GRAY;
            default -> {
                System.out.println("Unknown color: " + colorName + ". Defaulting to gray.");
                yield Color.GRAY;
            }
        };
    }
}
