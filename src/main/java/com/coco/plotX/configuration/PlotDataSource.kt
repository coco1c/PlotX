package com.coco.plotX.configuration

import com.coco.plotX.PlotX
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

class PlotDataSource() {
    private val configFilePath = PlotX.instance.dataFolder.path + File.separator + "config.yml"
    private val config = Configuration(configFilePath)
    private val dataSource = if (config.databaseType == "MySQL" && config.databaseEnabled) MySQLDataSource(config) else FileDataSource(config.dataFilePath)

    fun plotExists(regionID: String) = dataSource.plotExists(regionID)
    fun insertPlot(regionID: String) = dataSource.insertPlot(regionID)
    fun updatePlotOwner(regionID: String, playerID: String, expiryTime: LocalDateTime) = dataSource.updatePlotOwner(regionID, playerID, expiryTime)
    fun clearPlotOwner(regionID: String) = dataSource.clearPlotOwner(regionID)
    fun deletePlot(regionID: String) = dataSource.deletePlot(regionID)
    fun addPlotMember(regionID: String, playerID: String) = dataSource.addPlotMember(regionID, playerID)
    fun getPlotMembers(regionID: String) = dataSource.getPlotMembers(regionID)
    fun removePlotMember(regionID: String, playerID: String) = dataSource.removePlotMember(regionID, playerID)
    fun getPlotOwner(regionID: String) = dataSource.getPlotOwner(regionID)
    fun getPlotExpiry(regionID: String) = dataSource.getPlotExpiry(regionID)
    fun updatePlotExpiry(regionID: String, expiryTime: LocalDateTime) = dataSource.updatePlotExpiry(regionID, expiryTime)

    private interface DataSource {
        fun plotExists(regionID: String): Boolean
        fun insertPlot(regionID: String)
        fun updatePlotOwner(regionID: String, playerID: String, expiryTime: LocalDateTime)
        fun clearPlotOwner(regionID: String)
        fun deletePlot(regionID: String)
        fun addPlotMember(regionID: String, playerID: String)
        fun getPlotMembers(regionID: String): List<String>
        fun removePlotMember(regionID: String, playerID: String)
        fun getPlotOwner(regionID: String): String?
        fun getPlotExpiry(regionID: String): LocalDateTime?
        fun updatePlotExpiry(regionID: String, expiryTime: LocalDateTime)
    }

    private class MySQLDataSource(config: Configuration) : DataSource {
        private val connection: Connection

        init {
            val url = "jdbc:mysql://${config.host}:${config.port}/${config.databaseName}"
            connection = DriverManager.getConnection(url, config.username, config.password)
            createTableForPlot()
        }

        private fun createTableForPlot() {
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS Plots (
                    regionID VARCHAR(255) PRIMARY KEY,
                    ownerID VARCHAR(255),
                    expiryTime DATETIME,
                    members TEXT
                );
                """
            ).executeUpdate()
        }

        override fun plotExists(regionID: String): Boolean {
            val query = "SELECT regionID FROM Plots WHERE regionID = ?"
            val statement = connection.prepareStatement(query)
            statement.setString(1, regionID)
            val resultSet = statement.executeQuery()
            return resultSet.next()
        }

        override fun insertPlot(regionID: String) {
            val query = "INSERT INTO Plots (regionID) VALUES (?)"
            connection.prepareStatement(query).apply {
                setString(1, regionID)
                executeUpdate()
            }
        }

        override fun updatePlotOwner(regionID: String, playerID: String, expiryTime: LocalDateTime) {
            val query = "UPDATE Plots SET ownerID = ?, expiryTime = ? WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setString(1, playerID)
                setObject(2, expiryTime)
                setString(3, regionID)
                executeUpdate()
            }
        }

        override fun clearPlotOwner(regionID: String) {
            val query = "UPDATE Plots SET ownerID = NULL, expiryTime = NULL WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setString(1, regionID)
                executeUpdate()
            }
        }

        override fun deletePlot(regionID: String) {
            val query = "DELETE FROM Plots WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setString(1, regionID)
                executeUpdate()
            }
        }

        override fun addPlotMember(regionID: String, playerID: String) {
            val query = "UPDATE Plots SET members = JSON_ARRAY_APPEND(members, '$', ?) WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setString(1, playerID)
                setString(2, regionID)
                executeUpdate()
            }
        }

        override fun getPlotMembers(regionID: String): List<String> {
            val query = "SELECT members FROM Plots WHERE regionID = ?"
            val statement = connection.prepareStatement(query)
            statement.setString(1, regionID)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSet.getString("members").split(",") else emptyList()
        }

        override fun removePlotMember(regionID: String, playerID: String) {
            val query = "UPDATE Plots SET members = JSON_REMOVE(members, JSON_SEARCH(members, 'one', ?)) WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setString(1, playerID)
                setString(2, regionID)
                executeUpdate()
            }
        }

        override fun getPlotOwner(regionID: String): String? {
            val query = "SELECT ownerID FROM Plots WHERE regionID = ?"
            val statement = connection.prepareStatement(query)
            statement.setString(1, regionID)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSet.getString("ownerID") else null
        }

        override fun getPlotExpiry(regionID: String): LocalDateTime? {
            val query = "SELECT expiryTime FROM Plots WHERE regionID = ?"
            val statement = connection.prepareStatement(query)
            statement.setString(1, regionID)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSet.getObject("expiryTime", LocalDateTime::class.java) else null
        }

        override fun updatePlotExpiry(regionID: String, expiryTime: LocalDateTime) {
            val query = "UPDATE Plots SET expiryTime = ? WHERE regionID = ?"
            connection.prepareStatement(query).apply {
                setObject(1, expiryTime)
                setString(2, regionID)
                executeUpdate()
            }
        }
    }

    private class FileDataSource(private val dataFilePath: String) : DataSource {
        private val yaml = Yaml()
        private val dataFile = File(dataFilePath)
        private val data: MutableMap<String, MutableMap<String, Any?>> = loadData()

        init {
            if (!dataFile.exists()) dataFile.createNewFile()
        }

        private fun loadData(): MutableMap<String, MutableMap<String, Any?>> {
            return yaml.load(dataFile.inputStream()) ?: mutableMapOf()
        }

        private fun saveData() {
            dataFile.writeText(yaml.dump(data))
        }

        override fun plotExists(regionID: String): Boolean {
            return data.containsKey(regionID)
        }

        override fun insertPlot(regionID: String) {
            data[regionID] = mutableMapOf("ownerID" to null, "expiryTime" to null, "members" to mutableListOf<String>())
            saveData()
        }

        override fun updatePlotOwner(regionID: String, playerID: String, expiryTime: LocalDateTime) {
            data[regionID]?.apply {
                this["ownerID"] = playerID
                this["expiryTime"] = expiryTime.toString()
            }
            saveData()
        }

        override fun clearPlotOwner(regionID: String) {
            data[regionID]?.apply {
                this["ownerID"] = null
                this["expiryTime"] = null
            }
            saveData()
        }

        override fun deletePlot(regionID: String) {
            data.remove(regionID)
            saveData()
        }

        override fun addPlotMember(regionID: String, playerID: String) {
            val members = data[regionID]?.get("members") as? MutableList<String> ?: mutableListOf()
            if (!members.contains(playerID)) {
                members.add(playerID)
                data[regionID]?.set("members", members)
                saveData()
            }
        }

        override fun getPlotMembers(regionID: String): List<String> {
            return (data[regionID]?.get("members") as? List<String>) ?: emptyList()
        }

        override fun removePlotMember(regionID: String, playerID: String) {
            val members = data[regionID]?.get("members") as? MutableList<String> ?: mutableListOf()
            if (members.contains(playerID)) {
                members.remove(playerID)
                data[regionID]?.set("members", members)
                saveData()
            }
        }

        override fun getPlotOwner(regionID: String): String? {
            return data[regionID]?.get("ownerID") as? String
        }

        override fun getPlotExpiry(regionID: String): LocalDateTime? {
            val expiry = data[regionID]?.get("expiryTime") as? String
            return expiry?.let { LocalDateTime.parse(it) }
        }

        override fun updatePlotExpiry(regionID: String, expiryTime: LocalDateTime) {
            data[regionID]?.set("expiryTime", expiryTime.toString())
            saveData()
        }
    }
}