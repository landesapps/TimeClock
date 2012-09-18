public interface Clock {
    public boolean clockIn(String project);
    public boolean clockIn();
    public boolean clockOut();
    public String[] getProjects();
    public void addProject(String project);
    public String getMainProject();
    public boolean isClockedIn();
}
