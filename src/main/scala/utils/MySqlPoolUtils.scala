package utils

import java.sql.{Connection, ResultSet, Statement}

import com.jinp.videobigdata.entity.{CarSurveillance, WifiSurveillance}
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}

import scala.collection.mutable

object MySqlPoolUtils {

  private val config = new BoneCPConfig()

  var conn: Connection = _

  config.setUsername(PropertiesUtils.JDBC_USER)
  config.setPassword(PropertiesUtils.JDBC_PASSWORD)
  config.setJdbcUrl(PropertiesUtils.JDBC_URL)
  config.setMinConnectionsPerPartition(2)
  config.setMaxConnectionsPerPartition(5)
  config.setCloseConnectionWatch(PropertiesUtils.JDBC_IS_CLOSE_CONNECTION_WATCH)

  val pool = new BoneCP(config)

  Class.forName(PropertiesUtils.JDBC_DRIVER)

  def getConnection: Connection = {
    if (conn == null || conn.isClosed) {
      conn = pool.getConnection
    }
    conn
  }

  def getWifiSurveillance: mutable.Map[String, WifiSurveillance] = {
    val connection = getConnection
    val statement = connection.createStatement()
    val set = statement.executeQuery(PropertiesUtils.WIFI_SUR_SQL)
    val map = mutable.Map[String, WifiSurveillance]()
    while (set.next()) {
      val surId = set.getInt(1)
      val macAddress = set.getString(2)
      val deviceIds = set.getString(3).split(",")
      val source = set.getInt(4)
      val dealDepart = set.getString(5)
      val surName = set.getString(6)
      val personName = set .getString(7)
      val personId = set .getString(8)
      import scala.collection.JavaConverters._
      map += (macAddress -> new WifiSurveillance(surId,macAddress, deviceIds.toList.asJava,source,dealDepart,surName,personName,personId))
    }
    close(connection, statement, set)
    map
  }

  def getCarSurveillance: mutable.Map[String, CarSurveillance] = {
    val connection = getConnection
    val statement = connection.createStatement()
    val set = statement.executeQuery(PropertiesUtils.CAR_SUR_SQL)
    val map = mutable.Map[String, CarSurveillance]()
    while (set.next()) {
      val surId = set.getInt(1)
      val carNo = set.getString(2)
      val deviceIds = set.getString(3).split(",")
      val source = set.getInt(4)
      val dealDepart = set.getString(5)
      val surName = set.getString(6)
      val personName = set .getString(7)
      val personId = set .getString(8)
      import scala.collection.JavaConverters._
      map += (carNo -> new CarSurveillance(surId,carNo, deviceIds.toList.asJava,source,dealDepart,surName,personName,personId))
    }
    close(connection, statement, set)
    map
  }

  def close(connection: Connection): Unit = {
    import java.sql.SQLException
    if (connection != null || !connection.isClosed) try
      connection.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  def close(connection: Connection, stmt: Statement, rs: ResultSet): Unit = {
    import java.sql.SQLException
    if (connection != null || !connection.isClosed) try
      connection.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
    if (stmt != null || !stmt.isClosed) try
      stmt.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
    if (rs != null || !rs.isClosed) try
      rs.close()
    catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

}
