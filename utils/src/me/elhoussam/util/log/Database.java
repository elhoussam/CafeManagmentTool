package me.elhoussam.util.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

  private static Database obj = null ;
  private Connection con = null ;
  private String userName = "";
  private String userPass = "";
  private String driverName = "";
  private String databaseName = "";
  private String databaseUrl = "";


  public static Database getInstance() {
    if( obj == null ) {
      obj = new Database();
    }
    return obj;
  }

  public void setUserCredential(String userName, String userPass) {
    this.userName = userName;
    this.userPass = userPass;
  }

  private void getConnection() {
    if( con == null ) {
      try {
        // load the driver
        Class.forName(this.driverName);
        // getConnection according to the given parameter
        con = (( this.userName.isEmpty() && this.userPass.isEmpty() )?
            DriverManager.getConnection( this.databaseUrl+":"+this.databaseName):
              DriverManager.getConnection(
                  this.databaseUrl+":"+this.databaseName,
                  this.userName,
                  this.userPass
                  ) ) ;


      } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public void insertRecord(String tableName, Object ... args ) {
    // get connection if doesnt exist
    if ( con == null ) {
      getConnection();
    }
    // contruct the parameterize query
    String query = "INSERT INTO "+tableName+" values(null,";
    for(byte ind =0 ; ind < args.length ; ind++) {
      query+="?,";
    }
    query = query.substring(0, query.length()-1 )+")";
    // insert values into the prepStatement
    PreparedStatement prep2;
    try {
      prep2 = con.prepareStatement(query);

      for(byte ind =0, i =1 ; ind < args.length ; ind++,i++) {
        Object value =   args[ind]   ;
        if( value instanceof String )
          prep2.setString(i, args[ind].toString());
        else
          prep2.setInt( i, Integer.valueOf( args[ind].toString() ) );
      }
      // finally run it
      prep2.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    /* finally {

      try {
        if ( con != null )
          con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }*/

  }
  public ResultSet executeQuery(String query) throws SQLException {
    if ( con == null ) {
      getConnection();
    }
    Statement state = con.createStatement();
    ResultSet res = state.executeQuery( query );
    return res;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserPass() {
    return userPass;
  }

  public void setDriverName(String driverName) {
    this.driverName = driverName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getDatabaseUrl() {
    return databaseUrl;
  }

  public void setDatabaseUrl(String databaseUrl) {
    this.databaseUrl = databaseUrl;
  }

}
