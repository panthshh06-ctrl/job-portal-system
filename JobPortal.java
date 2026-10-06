import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

// --- 1. Data Models ---
class Job {
    static int counter = 1;
    int id;
    String title;
    String company;

    // Constructor for creating a NEW job
    public Job(String title, String company) {
        this.id = counter++;
        this.title = title;
        this.company = company;
    }

    // Constructor for LOADING a job from the text file
    public Job(int id, String title, String company) {
        this.id = id;
        this.title = title;
        this.company = company;
        if (id >= counter) {
            counter = id + 1;
        }
    }

    public String toCSV() {
        return id + "," + title + "," + company;
    }

    @Override
    public String toString() {
        return "Job ID: " + id + " | Title: " + title + " | Company: " + company;
    }
}

class Application {
    int jobId;
    String applicantName;

    public Application(int jobId, String applicantName) {
        this.jobId = jobId;
        this.applicantName = applicantName;
    }

    public String toCSV() {
        return jobId + "," + applicantName;
    }
}

// --- 2. Main System ---
public class JobPortal {
    // In-memory lists acting as our database
    static List<Job> jobsDb = new ArrayList<>();
    static List<Application> applicationsDb = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Load data from files when program starts
        loadData();

        while (true) {
            System.out.println("\n=== JOB PORTAL MANAGEMENT SYSTEM ===");
            System.out.println("1. Employer: Post a Job");
            System.out.println("2. Employer: View Applications");
            System.out.println("3. Job Seeker: View All Jobs");
            System.out.println("4. Job Seeker: Apply for a Job");
            System.out.println("5. Exit & Save Data");
            
            int choice = -1;
            
            // Error Handling for invalid menu inputs
            try {
                System.out.print("Choose your role/action (1-5): ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input! Please enter a number between 1 and 5.\n");
                scanner.nextLine(); // Clear the bad input from the buffer
                continue; // Restart the loop
            }

            switch (choice) {
                case 1:
                    if (authenticateEmployer()) {
                        postJob();
                    }
                    break;
                case 2:
                    if (authenticateEmployer()) {
                        viewApplications();
                    }
                    break;
                case 3:
                    viewAllJobs();
                    break;
                case 4:
                    applyForJob();
                    break;
                case 5:
                    saveData();
                    System.out.println("Data saved successfully. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    // --- 3. Security ---
    private static boolean authenticateEmployer() {
        System.out.print("Enter Employer Password to continue: ");
        String enteredPassword = scanner.nextLine();
        
        if (enteredPassword.equals("admin123")) {
            return true;
        } else {
            System.out.println("Error: Incorrect password. Access denied.\n");
            return false;
        }
    }

    // --- 4. Core Features ---
    private static void postJob() {
        System.out.print("Enter Job Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Company Name: ");
        String company = scanner.nextLine();
        
        jobsDb.add(new Job(title, company));
        System.out.println("Success! Job posted.");
    }

    private static void viewAllJobs() {
        System.out.println("\n--- Available Jobs ---");
        if (jobsDb.isEmpty()) {
            System.out.println("No jobs available right now.");
            return;
        }
        for (Job job : jobsDb) {
            System.out.println(job.toString());
        }
    }

    private static void applyForJob() {
        viewAllJobs();
        if (jobsDb.isEmpty()) return;

        System.out.print("\nEnter the Job ID you want to apply for: ");
        
        int jobId = -1;
        try {
            jobId = scanner.nextInt();
            scanner.nextLine(); 
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid numerical ID.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();

        // Check if the Job ID actually exists in our list
        int finalJobId = jobId; // Required for lambda expression below
        boolean jobExists = jobsDb.stream().anyMatch(j -> j.id == finalJobId);
        
        if (jobExists) {
            applicationsDb.add(new Application(jobId, name));
            System.out.println("Success! Application submitted.");
        } else {
            System.out.println("Error: Job ID not found.");
        }
    }

    private static void viewApplications() {
        System.out.println("\n--- All Applications ---");
        if (applicationsDb.isEmpty()) {
            System.out.println("No applications received yet.");
            return;
        }
        for (Application app : applicationsDb) {
            System.out.println("Applicant Name: " + app.applicantName + " -> Applied for Job ID: " + app.jobId);
        }
    }

    // --- 5. File I/O (Saving & Loading) ---
    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("jobs.txt"))) {
            for (Job job : jobsDb) {
                writer.println(job.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving jobs: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("applications.txt"))) {
            for (Application app : applicationsDb) {
                writer.println(app.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving applications: " + e.getMessage());
        }
    }

    private static void loadData() {
        try {
            File jobFile = new File("jobs.txt");
            if (jobFile.exists()) {
                Scanner fileScanner = new Scanner(jobFile);
                while (fileScanner.hasNextLine()) {
                    String[] parts = fileScanner.nextLine().split(",");
                    if (parts.length == 3) {
                        jobsDb.add(new Job(Integer.parseInt(parts[0]), parts[1], parts[2]));
                    }
                }
                fileScanner.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading jobs: " + e.getMessage());
        }

        try {
            File appFile = new File("applications.txt");
            if (appFile.exists()) {
                Scanner fileScanner = new Scanner(appFile);
                while (fileScanner.hasNextLine()) {
                    String[] parts = fileScanner.nextLine().split(",");
                    if (parts.length == 2) {
                        applicationsDb.add(new Application(Integer.parseInt(parts[0]), parts[1]));
                    }
                }
                fileScanner.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
    }
}