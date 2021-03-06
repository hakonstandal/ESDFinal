/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.XMLReader;
import Database.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

/**
 *
 * @author t2-sheedy
 */
public class Journey {

    private int ID;
    private String Destination;
    private int Distance;
    private int CustomerID;
    private String DriversRegistration;
    private Date Date;
    private Time Time;
    private double JourneyPrice;

    // <editor-fold desc="Constructor">
    public Journey() {

    }

    public Journey(int id) {
        this.ID = id;
        SetDetails();
    }

    public Journey(int id, String dest, int dist, int custID, String drivReg, Date date, Time time) {
        this.ID = id;
        this.Destination = dest;
        this.Distance = dist;
        this.CustomerID = custID;
        this.DriversRegistration = drivReg;
        this.Date = date;
        this.Time = time;
    }

    public Journey(String dest, int dist, int custID, String drivReg, Date date, Time time) {
        this.Destination = dest;
        this.Distance = dist;
        this.CustomerID = custID;
        this.DriversRegistration = drivReg;
        this.Date = date;
        this.Time = time;
    }

    // </editor-fold>
    // <editor-fold desc="Properties">
    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String Destination) {
        this.Destination = Destination;
    }

    public int getDistance() {
        return this.Distance;
    }

    //calculates distance between two locations by google API
    public int getDistance(String start, String end) throws MalformedURLException, IOException {
        try {
            start = start.replace(" ", "%20");
            end = end.replace(" ", "%20");

            String Surl = "https://maps.googleapis.com/maps/api/distancematrix/xml?"
                    + "origins=" + start
                    + "&destinations=" + end;
            XMLReader reader = null;
            int o = reader.getDistance(Surl);

            this.Distance = o;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return -1;
    }
    
    public void setDistance(int distance) {
        this.Distance = distance;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int CustomerID) {
        this.CustomerID = CustomerID;
    }

    public String getDriversRegistration() {
        return DriversRegistration;
    }

    public void setDriversRegistration(String DriversRegistration) {
        this.DriversRegistration = DriversRegistration;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date Date) {
        this.Date = Date;
    }

    public Time getTime() {
        return Time;
    }

    public void setTime(Time Time) {
        this.Time = Time;
    }

    public double getJourneyPrice() {
        return JourneyPrice;
    }

    private void setJourneyPrice(double price) {
        this.JourneyPrice = price;
    }

    // </editor-fold>
    // <editor-fold desc="GetDetail">
    public void calculatePricing(int distance) throws SQLException, ClassNotFoundException {
        Price p = new Price(distance);
        setJourneyPrice(p.getPrice());
    }

    //set driver details by registration
    public void SetDetails() {

        Connection con;
        Statement state;
        ResultSet rs;

        Properties p;
        p = new Properties();

        try {

            Class.forName(p.Driver());
            con = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            state = con.createStatement();
            String query = GetDetailsByIDQuery();
            if (query.length() > 0) {

                rs = state.executeQuery(query);

                int id = -1;
                String dest = "";
                int dist = -1;
                int custID = -1;
                String drivReg = "";
                Date date = null;
                Time time = null;
                int rowCount = 0;

                while (rs.next()) {
                    rowCount = rowCount + 1;
                    id = rs.getInt("id");
                    dest = rs.getString("Destination");
                    dist = rs.getInt("Distance");
                    custID = rs.getInt("Customer.id");
                    drivReg = rs.getString("Drivers.Registration");
                    date = rs.getDate("Date");
                    time = rs.getTime("Time");
                }

                rs.close();
                state.close();
                con.close();

                if (rowCount == 1) {
                    setID(id);
                    setDestination(dest);
                    setDistance(dist);
                    setCustomerID(custID);
                    setDriversRegistration(drivReg);
                    setDate(date);
                    setTime(time);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }//tryerrr
    }

    // </editor-fold>
    // <editor-fold desc="List">
    public ArrayList<Journey> List() {

        ArrayList<Journey> results = new ArrayList<Journey>();

        Connection con;
        Statement state;
        ResultSet rs;

        Properties p;
        p = new Properties();

        try {

            Class.forName(p.Driver());
            con = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            state = con.createStatement();
            String query = GetListQuery();

            if (!"".equals(query)) {

                rs = state.executeQuery(query);

                int id = -1;
                String dest = "";
                int dist = -1;
                int custID = -1;
                String drivReg = "";
                Date date = null;
                Time time = null;
                int rowCount = 0;

                while (rs.next()) {
                    rowCount = rowCount + 1;
                    id = rs.getInt("id");
                    dest = rs.getString("Destination");
                    dist = rs.getInt("Distance");
                    custID = rs.getInt("Customer.id");
                    drivReg = rs.getString("Drivers.Registration");
                    date = rs.getDate("Date");
                    time = rs.getTime("Time");
                    results.add(new Journey(id, dest, dist, custID, drivReg, date, time));
                }

                rs.close();
                state.close();
                con.close();

                return results;
            }

        } catch (Exception e) {
            System.err.println("Error: " + e);
        };
        return results;
    }

    public ArrayList<Journey> ListByDate(Date dateFilter) {

        ArrayList<Journey> results = new ArrayList<Journey>();

        Connection con;
        Statement state;
        ResultSet rs;

        Properties p;
        p = new Properties();

        try {

            Class.forName(p.Driver());
            con = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            state = con.createStatement();
            String query = GetListByDateQuery(dateFilter);

            if (!"".equals(query)) {

                rs = state.executeQuery(query);

                int id = -1;
                String dest = "";
                int dist = -1;
                int custID = -1;
                String drivReg = "";
                Date date = null;
                Time time = null;
                int rowCount = 0;

                while (rs.next()) {
                    rowCount = rowCount + 1;
                    id = rs.getInt("id");
                    dest = rs.getString("Destination");
                    dist = rs.getInt("Distance");
                    custID = rs.getInt("Customer.id");
                    drivReg = rs.getString("Drivers.Registration");
                    date = rs.getDate("Date");
                    time = rs.getTime("Time");
                    results.add(new Journey(id, dest, dist, custID, drivReg, date, time));
                }

                rs.close();
                state.close();
                con.close();

                return results;
            }

        } catch (Exception e) {
            System.err.println("Error: " + e);
        };
        return results;
    }

    // </editor-fold>
    // <editor-fold desc="WriteToDB">
    public int WriteToDB() throws SQLException {
        int id = -1;
        
        try {
            Properties p;
            p = new Properties();
            Connection connection = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            String query = GetWriteToDBQuery();
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 1) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                
                if (generatedKeys.next()) {
                    id = (generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        };
        
        return id;
    }
    
    
    // </editor-fold>
    // <editor-fold desc="Update">
    public boolean Update() {
        Connection con;
        Statement state;

        Properties p;
        p = new Properties();

        try {

            Class.forName(p.Driver());
            con = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            state = con.createStatement();
            String query = GetUpdateQuery();

            if (!"".equals(query)) {

                state.executeUpdate(query);

                state.close();
                con.close();

                return true;
            }

        } catch (Exception e) {
            System.err.println("Error: " + e);
        };
        return false;
    }

    // </editor-fold>
    // <editor-fold desc="Delete">
    public boolean Delete() {
        Connection con;
        Statement state;

        Properties p;
        p = new Properties();

        try {
            Class.forName(p.Driver());
            con = DriverManager.getConnection(p.URL(), p.Username(), p.Password());
            state = con.createStatement();
            String query = GetDeleteQuery();

            if (!"".equals(query)) {
                state.executeUpdate(query);

                state.close();
                con.close();

                return true;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        };
        
        return false;
    }
    // </editor-fold>
    // <editor-fold desc="DB">
    public String GetWriteToDBQuery() {
        String dest = getDestination();
        int dist = getDistance();
        String distStr = "Distance";
        int custID = getCustomerID();
        String custIDStr = "Customers.id";
        String drivReg = getDriversRegistration();
        Date date = getDate();
        String dateStr = "";
        Time time = getTime();
        String timeStr = "";

        if (dest != null) {
        } else {
            dest = "";
        }
        if (drivReg != null) {
        } else {
            drivReg = "";
        }
        if (date != null) {
            dateStr = "'" + date.toString() + "'";
        } else {
        }
        if (time != null) {
            timeStr = "'" + time.toString() + "'";
        } else {
        }
        if (dist > 0) {
            distStr = Integer.toString(dist);
        } else {
        }
        if (custID > 0) {
            custIDStr = Integer.toString(custID);
        } else {
        }

        String query = "";
        query = query + "INSERT INTO Journey";
        query = query + " (`Destination`, `Distance`, `Customer.id`, `Drivers.Registration`, `Date`, `Time`)";
        query = query + " VALUES";
        query = query + " ('" + dest + "'," + distStr + "," + custIDStr + ",'" + drivReg + "'," + dateStr + "," + timeStr + ");";

        return query;
    }

    public String GetUpdateQuery() {
        int id = getID();

        String dest = getDestination();
        int dist = getDistance();
        String distStr = "Distance";
        int custID = getCustomerID();
        String custIDStr = "Customers.id";
        String drivReg = getDriversRegistration();
        Date date = getDate();
        String dateStr = "";
        Time time = getTime();
        String timeStr = "";
        if (dest == null) {
            dest = "";
        }
        if (drivReg == null) {
            drivReg = "";
        }
        if (date == null) {
            dateStr = "'" + date.toString() + "'";
        }
        if (time == null) {
            timeStr = "'" + time.toString() + "'";
        }
        if (dist > 0) {
            distStr = Integer.toString(dist);
        } 
        if (custID > 0) {
            custIDStr = Integer.toString(custID);
        }

        String query = "";
        
        if (id > 0) {
            query = query + "UPDATE Journey";
            query = query + " SET Destination = '" + dest + "', Distance = " + distStr + ", Custromer.id = " + custIDStr;
            query = query + ", Drivers.Registration = '" + drivReg + "', Date = " + dateStr + ", Time = " + timeStr + ";";
            query = query + " WHERE id = " + id + ";";

        }
        return query;
    }

    public String GetDeleteQuery() {
        String query = "";
        int id = getID();

        if (id > 0) {

            query = query + "DELETE FROM Journey";
            query = query + " WHERE id = " + id + ";";

        }

        return query;
    }
    
    public String GetListQuery() {
        String query = "SELECT * FROM Journey;";
        return query;
    }

    public String GetDetailsByIDQuery() {
        String query = "SELECT * FROM Journey WHERE id = " + getID() + ";";
        return query;
    }

    public String GetListByDateQuery(Date date) {
        String query = "SELECT * FROM Journey WHERE Date='" + date + "';";
        return query;
    }
    // </editor-fold>
}
