import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MySQLClock implements Clock {
    String project;
    String connUrl;
    String user;
    String pass;
    int projectID;
    boolean clockedIn = false;
    int clockedInID;
    HashMap<Integer, String> allProjects = new HashMap<Integer, String>();
    Properties myProperties;
    
    public MySQLClock(Properties myProperties) {
        this.myProperties = myProperties;
        
        StringBuilder conString = new StringBuilder("jdbc:mysql://");
        conString.append(myProperties.getProperty("host", "localhost"));
        conString.append("/");
        conString.append(myProperties.getProperty("schema", "timeclock"));
        connUrl = conString.toString();
        user = myProperties.getProperty("user", "root");
        pass = myProperties.getProperty("password", "admin");
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(connUrl, user, pass);
            Class.forName("com.mysql.jdbc.Driver");
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM projects");
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                allProjects.put(Integer.parseInt(result.getString("id")), result.getString("name"));
                
                if(Integer.parseInt(result.getString("default_selection")) == 1) {
                    project = result.getString("name");
                    projectID = Integer.parseInt(result.getString("id"));
                }
            }
            
        } catch(SQLException exception) {
            exception.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean clockIn(String project) {
        this.project = project;
        
        for(Map.Entry<Integer, String> entry : allProjects.entrySet()) {
            if(project.equals(entry.getValue())) {
                projectID = entry.getKey();
                break;
            }
        }
        
        return clockIn();
    }

    public boolean clockIn() {
        Connection con = null;
        
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            con = DriverManager.getConnection(connUrl, user, pass);
            PreparedStatement statement = con.prepareStatement("INSERT INTO timesheet(clockIn, projectID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, df.format(new Date()));
            statement.setString(2, String.valueOf(projectID));
            statement.executeUpdate();
            
            ResultSet timeID = statement.getGeneratedKeys();
            
            if(timeID.next()) {
                clockedInID = timeID.getInt(1);
                clockedIn = true;
            } else {
                clockedIn = false;
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
            clockedIn = false;
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return clockedIn;
    }

    public boolean clockOut() {
        Connection con = null;
        
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            con = DriverManager.getConnection(connUrl, user, pass);
            PreparedStatement statement = con.prepareStatement("UPDATE timesheet SET clockOut = ? WHERE id = ?");
            
            statement.setString(1, df.format(new Date()));
            statement.setString(2, String.valueOf(clockedInID));
            if(statement.executeUpdate() >= 1) {
                clockedIn = false;
            } else {
                clockedIn = true;
            }
            
        } catch(SQLException exception) {
            exception.printStackTrace();
            clockedIn = true;
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return !clockedIn;
    }
    
    public String[] getProjects() {
        String[] projects = new String[allProjects.size()];
        int iter = 0;
        
        for(String project : allProjects.values())
            projects[iter++] = project;
        
        return projects;
    }
    
    public void addProject(String project) {
        try (Connection con = DriverManager.getConnection(connUrl, user, pass)) {
            PreparedStatement statement = 
                    con.prepareStatement("INSERT INTO projects(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, project);
            
            if(statement.executeUpdate() >= 1) {
                ResultSet newKey = statement.getGeneratedKeys();
                if(newKey.next()) {
                    allProjects.put(newKey.getInt(1), project);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    
    public String getMainProject() {
        return project;
    }
    
    public boolean isClockedIn() {
        return clockedIn;
    }
}
