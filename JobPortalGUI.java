import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// --- Data Model ---
class Job {
    static int counter = 1;
    int id;
    String title;
    String company;

    public Job(String title, String company) {
        this.id = counter++;
        this.title = title;
        this.company = company;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | " + title + " at " + company;
    }
}

// --- Main GUI Class ---
public class JobPortalGUI extends JFrame {
    
    // In-memory Database
    private List<Job> jobsDb = new ArrayList<>();

    public JobPortalGUI() {
        // 1. Setup the Main Window (JFrame)
        setTitle("Job Portal Management System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window on screen

        // 2. Create a Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // 3. Create the Employer Tab
        JPanel employerPanel = createEmployerPanel();
        tabbedPane.addTab("Employer (Post Jobs)", employerPanel);

        // 4. Create the Job Seeker Tab
        JPanel seekerPanel = createSeekerPanel();
        tabbedPane.addTab("Job Seeker (View Jobs)", seekerPanel);

        // Add tabs to the main window
        add(tabbedPane);
    }

    // --- Panel Builders ---

    private JPanel createEmployerPanel() {
        // Use a grid layout: 4 rows, 2 columns
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create UI Elements
        JLabel titleLabel = new JLabel("Job Title:");
        JTextField titleField = new JTextField();
        
        JLabel companyLabel = new JLabel("Company Name:");
        JTextField companyField = new JTextField();
        
        JButton postButton = new JButton("Post Job");

        // Action Listener (What happens when the button is clicked)
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String company = companyField.getText();

                if (title.isEmpty() || company.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    jobsDb.add(new Job(title, company));
                    JOptionPane.showMessageDialog(null, "Job Posted Successfully!");
                    
                    // Clear the text fields
                    titleField.setText("");
                    companyField.setText("");
                }
            }
        });

        // Add elements to the panel
        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(companyLabel);
        panel.add(companyField);
        panel.add(new JLabel("")); // Empty placeholder for grid layout
        panel.add(postButton);

        return panel;
    }

    private JPanel createSeekerPanel() {
        // Use Border Layout
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create UI Elements
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false); // Users shouldn't type in the display area
        JScrollPane scrollPane = new JScrollPane(displayArea); // Add a scrollbar
        
        JButton refreshButton = new JButton("Refresh Job List");

        // Action Listener to load jobs into the text area
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.setText(""); // Clear current text
                
                if (jobsDb.isEmpty()) {
                    displayArea.append("No jobs available right now.\n");
                } else {
                    displayArea.append("--- Available Jobs ---\n\n");
                    for (Job job : jobsDb) {
                        displayArea.append(job.toString() + "\n");
                    }
                }
            }
        });

        // Add elements to the panel
        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- Main Method ---
    public static void main(String[] args) {
        // It's best practice to run Swing applications safely inside the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JobPortalGUI().setVisible(true);
            }
        });
    }
}