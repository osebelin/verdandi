/*******************************************************************************
 * Copyright 2010 Olaf Sebelin
 * 
 * This file is part of Verdandi.
 * 
 * Verdandi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Verdandi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Verdandi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package verdandi.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Locale;

public class DBConversion {

  private Connection oldDb, newDb;
  
  
  public DBConversion() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    oldDb = DriverManager.getConnection("jdbc:postgresql:verdandi", "osebelin", "osebelin123");
    newDb = DriverManager.getConnection("jdbc:derby:/home/osebelin/.verdandi/verdandidb/");
  }
  
  
  public void convertProjects() throws SQLException {
     
     
     Statement delStmt = newDb.createStatement();
     delStmt.executeUpdate("delete from myrecords");
     delStmt.executeUpdate("delete from projects");
     
     Statement stmt = oldDb.createStatement();
    
    ResultSet rs = stmt.executeQuery("Select * from projects");
    
    PreparedStatement pstmt = newDb.prepareStatement(
        "insert into projects (id,name,description,active)values(?,?,?,?)");
    
    while (rs.next()) {
      int pid = rs.getInt("id");
      String sdesc=rs.getString("name");
      String ldesc=rs.getString("description");
      boolean isactive = rs.getBoolean("active");
      System.out.println(pid+"->"+sdesc);
      
      pstmt.setString(1, Integer.toString(pid));
      pstmt.setString(2, sdesc);
      pstmt.setString(3, ldesc);
      pstmt.setBoolean(4, isactive);
      
      try {
      pstmt.executeUpdate();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void convertWorkRecords() throws SQLException {
     Statement delStmt = newDb.createStatement();
     delStmt.executeUpdate("delete from myrecords");

     Statement stmt = oldDb.createStatement();
    ResultSet rs = stmt.executeQuery("Select id, starttime, duration,  projectid, annotation from workrecords");
    
    PreparedStatement pstmt = newDb.prepareStatement(
        "insert into myrecords (id, starttime,duration,projectid,annotation) values(?,?,?,?,?)");

    while (rs.next()) {
      int pid = rs.getInt("id");
      Timestamp tStart = rs.getTimestamp("starttime");
      int minutes = rs.getInt("duration");

      String projectId = rs.getString("projectid"); 
      String annotation = rs.getString("annotation");
      
      pstmt.setInt(1, pid);
      pstmt.setTimestamp(2, new Timestamp(tStart.getTime()));
      pstmt.setInt(3, minutes);
      pstmt.setString(4, projectId);
      pstmt.setString(5, annotation);
      System.err.println("Record "+pid+"from " + tStart);
      try {
      pstmt.executeUpdate();
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex.getMessage());
      }
    }
    
    stmt = newDb.createStatement();
    rs = stmt.executeQuery("select max(id) from myrecords");
    if (!rs.next()) {
       throw new RuntimeException("no max");
    }
    
    int max = rs.getInt(1);
    rs.close();
    stmt = newDb.createStatement();
    //stmt.executeUpdate("Update SYS.SYSCOLUMNS SET AUTOINCREMENTVALUE="+(max+1)+" WHERE COLUMNNAME='ID' and referenceid in (select tableid from SYS.SYSTABLES where TABLENAME='myrecords')");
    stmt.executeUpdate("alter table myrecords alter column id restart with "+ (max+1));
    
    
  }

  
  
  public void check() throws SQLException {
     Statement stmt = newDb.createStatement();
     ResultSet rs = stmt.executeQuery("Select * from myrecords");
     while (rs.next()) {
        System.err.println("Contained " +rs.getInt("id"));
     }
     rs.close();
  }
  
  public void derbyInfo() throws SQLException {
     Statement stmt = newDb.createStatement();
     
     ResultSet rs = stmt.executeQuery("Select TABLENAME, COLUMNNAME, AUTOINCREMENTVALUE from SYS.SYSCOLUMNS sc, SYS.SYSTABLES st where sc.referenceid=st.tableid ORDER BY TABLENAME");
     while (rs.next()) {
        System.err.println(rs.getString(1)+"."+rs.getString(2) + ":"+ rs.getString(3));
        
     }
     System.err.println("-----");
     
     rs = stmt.executeQuery("Select max(id)  from myrecords");
     while (rs.next()) {
        System.err.println(rs.getInt(1));
     }
     
     rs = stmt.executeQuery("Select *  from myrecords where id =1");
     while (rs.next()) {
        System.err.println(rs.getTimestamp("starttime"));
     }
     
//     rs = stmt.executeQuery("Select *  from myrecords where starttime >= all (select starttime from myrecords)");
//     while (rs.next()) {
//        System.err.println("No. "+rs.getInt("id")+" @ "+ rs.getTimestamp("starttime"));
//     }
//     
     
//     DatabaseMetaData derbyMd = newDb.getMetaData();
//     ResultSet rs = derbyMd.getColumns(null,null,"SYS.SYSCOLUMNS","AUTOINCREMENTVALUE");
//     while (rs.next()) {
//        System.out.println(rs.get);
//     }
  }
  
  public static void main(String[] args) {
     Locale.setDefault(Locale.UK);
    try {
      DBConversion convert = new DBConversion();
      
//      convert.convertProjects();
//      convert.convertWorkRecords();
      convert.derbyInfo();
//      convert.check()
      ;
    } catch (Exception e) {
e.printStackTrace();
System.err.println(":::"+ e.getLocalizedMessage());
    }
  }
  
}
