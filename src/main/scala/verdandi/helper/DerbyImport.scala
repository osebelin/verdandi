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
package verdandi.helper

import verdandi.event.ApplicationShutdown
import verdandi.event.EventBroadcaster
import verdandi.model.persistence.SerializedFilePersistence
import verdandi.model.CostUnit
import scala.collection.mutable.HashMap
import java.sql.DriverManager

object DerbyImport {
  
  val url = "jdbc:derby:"+System.getProperty("user.home")+"/.verdandi/derbyjpa"

  val driver="org.apache.derby.jdbc.EmbeddedDriver"
  
  def conn = DriverManager.getConnection(url)

  val costUnitsById: HashMap[String, CostUnit] = {
    val res = new HashMap[String, CostUnit]()
    SerializedFilePersistence.getCostUnits.foreach(c => res+= (c.id -> c))
    res
  }
  
  def getCostUnit(id:String):CostUnit = {
    costUnitsById.get(id).getOrElse(new CostUnit(id, id, ""+id))
  }
  
  def showMetadata() {
    val md = conn.getMetaData()
    val rs = md.getColumns(null, null, "workrecord","");
    println("Columns of workRecord")
    while (rs.next) {
      println(rs.getString("4"))
    }
  }
  
  
  def migrateworkRecords() {
    val stmt = conn.createStatement
    val rs = stmt.executeQuery("Select * from workrecord")
    while (rs.next) {
      val id = rs.getInt("id") // not used, we're using our own IDs
      
      println(rs.getTimestamp("startTime"))
      
      val startTime = rs.getTimestamp("startTime").asInstanceOf[java.util.Date]
      val duration = rs.getInt("duration")
      val costUnitId=  rs.getString("project_id")
      val annotation=  rs.getString("annotation")
      
      val cu = costUnitsById.get(costUnitId).getOrElse(new CostUnit(costUnitId, costUnitId, "Deleted cost unit: "+costUnitId))
      
      val wr = SerializedFilePersistence.newWorkRecord(cu, startTime, duration)
      wr.annotation =annotation
      SerializedFilePersistence.save(wr)
      println("imported:" + wr)
    }
  }
  
  def main(args:Array[String]) {
    println("Derby Import using conn: "+conn + ", driver:"+driver)
    migrateworkRecords()
    EventBroadcaster.publish(ApplicationShutdown())
  }
  
  
}
