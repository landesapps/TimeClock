
import java.awt.List;
import java.util.ArrayList;
import java.util.Properties;

public class FileClock implements Clock {
    String project;
    boolean clockedIn = false;
    ArrayList<String> allProjects = new ArrayList<String>();
    
    public FileClock(Properties myProperties) {
        allProjects.add("SoftwareMedia");
        allProjects.add("zaZing");
    }

    public boolean clockIn(String project) {
        this.project = project;
        return clockIn();
    }

    public boolean clockIn() {
        clockedIn = true;
        return clockedIn;
    }

    public boolean clockOut() {
        clockedIn = false;
        return !clockedIn;
    }
    
    public String[] getProjects() {
        return allProjects.toArray(new String[allProjects.size()]);
    }
    
    public void addProject(String project) {
        allProjects.add(project);
    }
    
    public String getMainProject() {
        return project;
    }
    
    public boolean isClockedIn() {
        return clockedIn;
    }
}
