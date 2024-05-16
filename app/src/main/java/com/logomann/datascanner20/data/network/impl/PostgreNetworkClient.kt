package com.logomann.datascanner20.data.network.impl

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.logomann.datascanner20.data.NetworkClient
import com.logomann.datascanner20.data.network.ConnectionRequest
import com.logomann.datascanner20.data.network.ConnectionResponse
import com.logomann.datascanner20.data.network.Response
import com.logomann.datascanner20.data.network.SQLConnection
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.util.SCANNER_ID
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostgreNetworkClient(
    private val application: Application,
    sharedPreferences: SharedPreferences
) : NetworkClient {
    private val tsdID = sharedPreferences.getInt(SCANNER_ID, 1)

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        val response = if (dto is ConnectionRequest) {
            when (dto.request) {
                is ConnectionModel.Car -> {
                    requestVIN(dto.request)
                }

                is ConnectionModel.Container -> {
                    setContainerAddress(dto.request)
                }

                is ConnectionModel.User -> {
                    authorizeUser(dto.request)
                }

                is ConnectionModel.Address -> {
                    requestCell(dto.request)
                }

                is ConnectionModel.Lot -> {
                    setLotLocation(dto.request)
                }

                is ConnectionModel.Driver -> {
                    setDriverLot(dto.request)
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
        return response

    }

    private fun authorizeUser(user: ConnectionModel.User): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val statement = getConnectionStatement()
        statement.execute(
            "INSERT INTO \"AUTHORIZATION_IN\"(\"PIN\",\"TRANSACTION_ID\",\"DEVICE_ID\") " +
                    "VALUES('${user.pinCode}','$transactionID',$tsdID)"
        )
        val request = "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\" " +
                "FROM \"AUTHORIZATION_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
        var resp = statement.executeQuery(request)
        while (!resp.next()) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        var resultCodeID = 0
        var results = ""
        if (resp != null) {
            resultCodeID = resp.getInt("RESULT_CODE")
            results = resp.getString("RESULT_COMMENT")
        }
        return when (resultCodeID) {
            2 -> {
                deleteFromDBAuthorization(statement, transactionID)
                ConnectionResponse(1, results).apply { resultCode = 1 }
            }

            1 -> {
                deleteFromDBAuthorization(statement, transactionID)
                ConnectionResponse(400, results).apply { resultCode = 400 }
            }

            else -> {
                ConnectionResponse(-1, results).apply { resultCode = -1 }
            }
        }
    }

    private fun requestVIN(car: ConnectionModel.Car): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val statement = getConnectionStatement()
        var request = ""
        when (car.code) {
            2 -> {
                statement.execute(
                    "INSERT INTO \"INVENTORY_TRACKING_IN\"(\"OPERATION_CODE\",\"TRANSACTION_ID\"," +
                            "\"DEVICE_ID\",\"VIN\",\"PRINTER_ID\", \"ZONE\", \"SECTOR\", \"PLACE\") " +
                            "VALUES(${car.code},'$transactionID',$tsdID,'${car.vin}',0,'${car.address!!.field}'" +
                            ",'${car.address.row}','${car.address.cell}')"
                )
                request =
                    "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\" " +
                            "FROM \"INVENTORY_TRACKING_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
            }

            4 -> {
                statement.execute(
                    "INSERT INTO \"INVENTORY_TRACKING_IN\"(\"OPERATION_CODE\",\"TRANSACTION_ID\"," +
                            "\"DEVICE_ID\",\"VIN\",\"PRINTER_ID\") " +
                            "VALUES(${car.code},'$transactionID',$tsdID,'${car.vin}',0)"
                )
                request =
                    "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\", \"ZONE\", \"SECTOR\", \"PLACE\" " +
                            "FROM \"INVENTORY_TRACKING_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
            }
        }

        var resp = statement.executeQuery(request)

        while (!resp.next()) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        val resCode: Int
        var results = ""
        if (resp != null) {
            resCode = resp.getInt("RESULT_CODE")
            if (car.code == 4) {
                return if (resCode == 1) {
                    val zone = resp.getString("ZONE")
                    val sector = resp.getString("SECTOR")
                    val place = resp.getString("PLACE")
                    results = "$zone + $sector + $place"
                    deleteFromDBInventory(statement, transactionID)
                    ConnectionResponse(1, results).apply { resultCode = 1 }

                } else {
                    results = resp.getString("RESULT_COMMENT")
                    deleteFromDBInventory(statement, transactionID)
                    ConnectionResponse(1, results).apply { resultCode = 400 }
                }
            } else {
                return if (resCode == 1) {
                    results = resp.getString("RESULT_COMMENT")
                    deleteFromDBInventory(statement, transactionID)
                    ConnectionResponse(1, results).apply { resultCode = 1 }
                } else {
                    results = resp.getString("RESULT_COMMENT")
                    deleteFromDBInventory(statement, transactionID)
                    ConnectionResponse(1, results).apply { resultCode = 400 }
                }

            }
        } else {
            return ConnectionResponse(1, results).apply { resultCode = -1 }
        }

    }

    private fun deleteFromDBInventory(statement: Statement, transactionID: Long) {
        statement.execute(
            "DELETE FROM \"INVENTORY_TRACKING_OUT\" " +
                    "WHERE \"TRANSACTION_ID\"='$transactionID'"
        )
        statement.close()
    }

    private fun deleteFromDBAuthorization(statement: Statement, transactionID: Long) {
        statement.execute(
            "DELETE FROM \"AUTHORIZATION_OUT\" " +
                    "WHERE \"TRANSACTION_ID\"='$transactionID'"
        )
        statement.close()
    }


    private fun isConnected(): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    private fun getConnectionStatement(): Statement {
        val connection = SQLConnection().getPostgreSQL()
        return connection.createStatement()
    }

    private fun requestCell(address: ConnectionModel.Address): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val statement = getConnectionStatement()
        statement.execute(
            "INSERT INTO \"INVENTORY_TRACKING_IN\"(\"OPERATION_CODE\",\"TRANSACTION_ID\"," +
                    "\"DEVICE_ID\",\"PRINTER_ID\", \"ZONE\", \"SECTOR\", \"PLACE\", \"VIN\") " +
                    "VALUES(${address.code},'$transactionID',$tsdID,0,'${address.field}'" +
                    ",'${address.row}','${address.cell}','')"
        )
        val request: String = if (address.code == 11) {
            "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\" " +
                    "FROM \"INVENTORY_TRACKING_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
        } else {
            "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\", \"VIN\" " +
                    "FROM \"INVENTORY_TRACKING_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
        }

        var resp = statement.executeQuery(request)
        while (!resp.next()) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        var resultCodeID = 0
        var results = ""
        if (resp != null) {
            resultCodeID = resp.getInt("RESULT_CODE")
            results = resp.getString("RESULT_COMMENT")
        }
        return when (resultCodeID) {
            1 -> {
                if (address.code == 5) {
                    results = resp.getString("VIN")
                }
                deleteFromDBInventory(statement, transactionID)
                ConnectionResponse(1, results).apply { resultCode = 1 }
            }

            else -> {
                deleteFromDBInventory(statement, transactionID)
                ConnectionResponse(400, results).apply { resultCode = 400 }
            }
        }
    }

    private fun setLotLocation(lot: ConnectionModel.Lot): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val statement = getConnectionStatement()
        statement.execute(
            "INSERT INTO \"lot_in\"(\"operation_code\",\"transaction_id\"," +
                    "\"device_id\", \"lot_num\", \"batch_address\" ) " +
                    "VALUES(${lot.code},'$transactionID',$tsdID, '${lot.number}', '${lot.row}')"
        )
        val request: String =
            "SELECT \"result_code\", \"result_comment\" " +
                    "FROM \"lot_out\" WHERE \"transaction_id\"='$transactionID'"


        var resp = statement.executeQuery(request)
        while (!resp.next()) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        var resultCodeID = 0
        var results = ""
        if (resp != null) {
            resultCodeID = resp.getInt("RESULT_CODE")
            results = resp.getString("RESULT_COMMENT")
        }
        return when (resultCodeID) {
            1 -> {
                deleteFromLotOut(statement, transactionID)
                ConnectionResponse(1, results).apply { resultCode = 1 }
            }

            else -> {
                deleteFromLotOut(statement, transactionID)
                ConnectionResponse(400, results).apply { resultCode = 400 }
            }
        }

    }

    private fun deleteFromLotOut(statement: Statement, transactionID: Long) {
        statement.execute(
            "DELETE FROM \"lot_out\" " +
                    "WHERE \"transaction_id\"='$transactionID'"
        )
        statement.close()
    }

    private fun setContainerAddress(container: ConnectionModel.Container): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val statement = getConnectionStatement()
        when (container.code) {
            15 -> {
                statement.execute(
                    "INSERT INTO \"CONTAINER_TRACKING_IN\"(\"OPERATION_CODE\",\"TRANSACTION_ID\"," +
                            "\"DEVICE_ID\", \"CONTAINER\", \"WAGON\", \"PRINTER_ID\") " +
                            "VALUES(${container.code},'$transactionID',$tsdID, '${container.number}'" +
                            ", '${container.wagon}', 0)"
                )
            }

            2 -> {
                statement.execute(
                    "INSERT INTO \"CONTAINER_TRACKING_IN\"(\"OPERATION_CODE\",\"TRANSACTION_ID\"," +
                            "\"DEVICE_ID\", \"CONTAINER\", \"ZONE\", \"SECTOR\", \"PLACE\", \"PRINTER_ID\") " +
                            "VALUES(${container.code},'$transactionID',$tsdID, '${container.number}'" +
                            ", '${container.address?.field}', '${container.address?.row}'" +
                            ", '${container.address?.cell}', 0)"
                )
            }
        }

        val request =
            "SELECT \"RESULT_CODE\", \"RESULT_COMMENT\" " +
                    "FROM \"CONTAINER_TRACKING_OUT\" WHERE \"TRANSACTION_ID\"='$transactionID'"
        var resp = statement.executeQuery(request)
        while (!resp.next()) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        var resultCodeID = 0
        var results = ""
        if (resp != null) {
            resultCodeID = resp.getInt("RESULT_CODE")
            results = resp.getString("RESULT_COMMENT")
        }
        return when (resultCodeID) {
            1 -> {
                deleteFromContainer(statement, transactionID)
                ConnectionResponse(1, results).apply { resultCode = 1 }
            }

            else -> {
                deleteFromContainer(statement, transactionID)
                ConnectionResponse(400, results).apply { resultCode = 400 }
            }
        }

    }

    private fun deleteFromContainer(statement: Statement, transactionID: Long) {
        statement.execute(
            "DELETE FROM \"CONTAINER_TRACKING_OUT\" " +
                    "WHERE \"TRANSACTION_ID\"='$transactionID'"
        )
        statement.close()
    }

    private fun setDriverLot(driver: ConnectionModel.Driver): Response {
        val transactionID = System.currentTimeMillis() / 1000L
        val dateFormatLot = SimpleDateFormat("yyMMddHHmm", Locale.ENGLISH)
        val dateLot = Date()
        val lotNum: String = dateFormatLot.format(dateLot)
        val statement = getConnectionStatement()
        val lot = StringBuilder()
        for (it in driver.lot) {
            lot.append(",\'").append(it).append("\'")
        }
        statement.execute(
            "INSERT INTO \"lot_in\"(\"operation_code\",\"transaction_id\"," +
                    "\"device_id\",\"lot_num\",\"driver\") " +
                    "VALUES(${driver.code},'$transactionID',$tsdID, ${lotNum}, '${driver.number}')"

        )
        val request: String =
            "SELECT \"result_code\", \"result_comment\" " +
                    "FROM \"lot_out\" WHERE \"transaction_id\"='$transactionID'"


        var resp = statement.executeQuery(request)
        while (!resp.next()) {

            if (Thread.currentThread().isInterrupted) {
                break
            }
            resp = statement.executeQuery(request)
        }
        var resultCodeID = 0
        var results = ""
        if (resp != null) {
            resultCodeID = resp.getInt("RESULT_CODE")
            results = resp.getString("RESULT_COMMENT")
        }
        return when (resultCodeID) {
            1 -> {
                deleteFromLotOut(statement, transactionID)
                ConnectionResponse(1, results).apply { resultCode = 1 }
            }

            else -> {
                deleteFromLotOut(statement, transactionID)
                ConnectionResponse(400, results).apply { resultCode = 400 }
            }
        }
    }

}