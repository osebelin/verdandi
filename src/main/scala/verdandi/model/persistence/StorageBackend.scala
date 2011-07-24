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
package verdandi.model.persistence

import java.util.Date
import java.io.FileOutputStream
import verdandi.event.ApplicationShutdown
import java.io.{ File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, Serializable }
import verdandi.VerdandiHelper
import scala.actors.Actor
import scala.swing.Reactor
import verdandi.event._
import verdandi.model._
import scala.collection.mutable.{ ListBuffer, HashMap }
import java.util.concurrent.atomic.AtomicLong
import com.weiglewilczek.slf4s.Logging

private[persistence] class IdGenerator extends Serializable {
  val workRecordId = new AtomicLong(1)
}

trait StorageBackend extends Logging {

  def writeCostUnits(data: ListBuffer[CostUnit])
  def writeWorkRecords(data: ListBuffer[WorkRecord])
  def writeOwnSelection(data: ListBuffer[String])
  def writeIdGenerator(data: IdGenerator)

  def shutdown()

  def loadCostUnits(): ListBuffer[CostUnit]
  def loadOwnSelection(): ListBuffer[String]
  def loadWorkRecords(): ListBuffer[WorkRecord]
  def loadIdGenerator(): IdGenerator
}

trait FlatFileBackend extends StorageBackend with Logging {

  val basedir = VerdandiModel.conf.getBasedir
  val costUnitFile = new File(basedir, "costunits.ser")
  val ownCostUnitSelectionFile = new File(basedir, "ownSelection.ser")
  val workRecordsFile = new File(basedir, "workRecords.ser")
  val idGeneratorFile = new File(basedir, "idgenerator.ser")

  protected def write[T](data: T, to: File) = {
    VerdandiHelper.withCloseable(new ObjectOutputStream(new FileOutputStream(to))) { oOut =>
      oOut.writeObject(data)
    }
  }

  def loadCostUnits(): ListBuffer[CostUnit] = loadOrElse(costUnitFile, new ListBuffer[CostUnit])
  def loadOwnSelection(): ListBuffer[String] = loadOrElse(ownCostUnitSelectionFile, new ListBuffer[String])
  def loadWorkRecords(): ListBuffer[WorkRecord] = loadOrElse(workRecordsFile, new ListBuffer[WorkRecord])
  def loadIdGenerator(): IdGenerator = loadOrElse(idGeneratorFile, new IdGenerator)

  protected def loadOrElse[T](dataFile: File, orElse: => T): T = {
    var res = null.asInstanceOf[T]

    if (dataFile.exists) {
      try {
        VerdandiHelper.withCloseable(new ObjectInputStream(new FileInputStream(dataFile))) { oIn: ObjectInputStream =>
          {
            res = oIn.readObject.asInstanceOf[T]
          }
        }
      } catch {
        case e: Exception => {
          logger.error("Cannot read " + dataFile.getAbsolutePath, e)
          res = orElse
        }
      }
    } else {
      res = orElse
    }
    res
  }
}

object InstantWriteFlatFileBackend extends FlatFileBackend with Logging {
  def writeCostUnits(data: ListBuffer[CostUnit]) = write(data, costUnitFile)
  def writeWorkRecords(data: ListBuffer[WorkRecord]) = write(data, workRecordsFile)
  def writeOwnSelection(data: ListBuffer[String]) = write(data, ownCostUnitSelectionFile)
  def writeIdGenerator(data: IdGenerator) = write(data, idGeneratorFile)

  def shutdown() {}
}

trait DeferredWriteBackend {
  protected var costUnits: Option[ListBuffer[CostUnit]] = None
  protected var workRecords: Option[ListBuffer[WorkRecord]] = None
  protected var ownSelection: Option[ListBuffer[String]] = None
  protected var idGen: Option[IdGenerator] = None

  def writeCostUnits(data: ListBuffer[CostUnit]) = costUnits = Some(data)
  def writeWorkRecords(data: ListBuffer[WorkRecord]) = workRecords = Some(data)
  def writeOwnSelection(data: ListBuffer[String]) = ownSelection = Some(data)
  def writeIdGenerator(data: IdGenerator) = idGen = Some(data)
}

object DeferredWriteFlatFileBackend extends FlatFileBackend with Logging {

  case class WriteData(val data: AnyRef, val to: File)
  case class Shutdown()

  object DirtyWrite extends Actor with Logging {

    def act() {
      react {
        // FIXME: There may be write orders left!
        case Shutdown => logger.debug("Aborting Actor")
        case dirty: WriteData => {
          logger.debug("Writing " + dirty.to)
          write(dirty.data, dirty.to)
          act()
        }
      }
    }
  }
  DirtyWrite.start()

  def shutdown() = DirtyWrite ! Shutdown()

  def writeCostUnits(data: ListBuffer[CostUnit]) = DirtyWrite ! WriteData(data, costUnitFile)
  def writeWorkRecords(data: ListBuffer[WorkRecord]) = DirtyWrite ! WriteData(data, workRecordsFile)
  def writeOwnSelection(data: ListBuffer[String]) = DirtyWrite ! WriteData(data, ownCostUnitSelectionFile)
  def writeIdGenerator(data: IdGenerator) = DirtyWrite ! WriteData(data, idGeneratorFile)

}

object WriteOnShutdownFlatFileBackend extends FlatFileBackend with DeferredWriteBackend with Logging {

  private def write[T](_data: Option[T], to: File) = _data match {
    case Some(data) => {
      VerdandiHelper.withCloseable(new ObjectOutputStream(new FileOutputStream(to))) { oOut =>
        oOut.writeObject(data)
      }
    }
    case None => {}
  }
  def shutdown() {
    write(costUnits, costUnitFile)
    write(workRecords, workRecordsFile)
    write(ownSelection, ownCostUnitSelectionFile)
    write(idGen, idGeneratorFile)
  }
}

object XMLFileBackend extends StorageBackend with DeferredWriteBackend with Logging {
  import scala.xml._

  val basedir = VerdandiModel.conf.getBasedir
  val datafile = new File(basedir, "verdandidata.xml")

  private var saved = false

  val root = if (datafile.isFile) {
    XML.loadFile(datafile)
  } else {
    <verdandidata/>
  }
  require(root.label == "verdandidata", "Unexpected root elem: " + root.label)

  lazy val _costUnits: ListBuffer[CostUnit] = {
    val res = new ListBuffer[CostUnit]()
    def unmarshal(elem: Node): CostUnit = {
      val res = new CostUnit((elem \ "@id").text, (elem \ "name").text, (elem \ "description").text)
      res.active = (elem \ "@active").text.toBoolean
      res
    }
    (root \ "costunits" \ "costunit").foreach(elem => res += unmarshal(elem))
    res
  }

  private lazy val _costUnitById: HashMap[String, CostUnit] = {
    val res = new HashMap[String, CostUnit]()
    _costUnits.foreach(cu => res += (cu.id -> cu))
    res
  }

  costUnits = Some(loadCostUnits)
  workRecords = Some(_loadWorkRecords)
  ownSelection = Some(_loadOwnSelection)
  idGen = Some(_loadIdGenerator)

  def toXml(costunit: CostUnit): Node = {
    <costunit>
      <name>{ costunit.name }</name>
      <description>{ costunit.description }</description>
    </costunit> % Attribute(None, "id", Text(costunit.id),
      Attribute(None, "active", Text(costunit.active.toString), Null))
  }
  def toXml(wr: WorkRecord): Node = {
    <workrecord>
      <costunit>{ wr.costUnit.name }</costunit>
      <annotation>{ wr.annotation }</annotation>
    </workrecord> % Attribute(None, "id", Text(wr.id.toString),
      Attribute(None, "start", Text(wr.start.getTime.toString),
        Attribute(None, "duration", Text(wr.duration.toString),
          Attribute(None, "costunitid", Text(wr.costUnit.id), Null))))
  }
  def seltoxml(cuid: String): Node = {
    <costunit/> % Attribute(None, "id", Text(cuid), Null)
  }

  def toXml(): Node = {
    <verdandidata>
      <costunits>
        { costUnits.getOrElse(new ListBuffer()).map(toXml(_)) }
      </costunits>
      <ownselection>
        { ownSelection.getOrElse(new ListBuffer()).map(s => seltoxml(s)) }
      </ownselection>
      <workrecords>
        { workRecords.getOrElse(new ListBuffer()).map(toXml(_)) }
      </workrecords>
    </verdandidata> % Attribute(None, "nextid", Text(idGen.getOrElse(new IdGenerator()).workRecordId.get.toString), Null)
  }

  def shutdown() {
    if (!saved) {
      logger.debug("writing data to " + datafile.getAbsolutePath)
      XML.save(datafile.getAbsolutePath, toXml(), "UTF-8", true, null)
      saved = true
    }
  }

  private def _loadOwnSelection(): ListBuffer[String] = {
    val res = new ListBuffer[String]()
    (root \ "ownselection" \ "costunit").foreach(elem => res += (elem \ "@id").text)
    res
  }
  private def _loadWorkRecords(): ListBuffer[WorkRecord] = {
    val res = new ListBuffer[WorkRecord]()
    def unmarshal(elem: Node): WorkRecord = {
      val id = (elem \ "@id").text.toInt
      val cuId = ((elem \ "@costunitid").text)
      val cu = _costUnitById.getOrElse(cuId, new CostUnit(cuId, cuId, null))
      val startDate = new Date((elem \ "@start").text.toLong)
      val duration = (elem \ "@duration").text.toInt
      val res = new WorkRecord(id, cu, startDate, duration)
      res.annotation = (elem \ "annotation").text
      res
    }
    (root \ "workrecords" \ "workrecord").foreach(elem => res += unmarshal(elem))
    res
  }
  private def _loadIdGenerator(): IdGenerator = {
    val idGenerator = new IdGenerator()
    idGenerator.workRecordId.set((root \ "@nextid").text.toLong)
    idGenerator
  }

  def loadCostUnits(): ListBuffer[CostUnit] = _costUnits
  def loadOwnSelection(): ListBuffer[String] = ownSelection.getOrElse(new ListBuffer())
  def loadWorkRecords(): ListBuffer[WorkRecord] = workRecords.getOrElse(new ListBuffer())
  def loadIdGenerator(): IdGenerator = idGen.getOrElse(new IdGenerator())

}
