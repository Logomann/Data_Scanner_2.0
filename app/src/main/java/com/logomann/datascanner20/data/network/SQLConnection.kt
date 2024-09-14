package com.logomann.datascanner20.data.network

import android.os.StrictMode
import com.logomann.datascanner20.BuildConfig
import java.sql.Connection
import java.sql.DriverManager

class SQLConnection {

    private val dbIp = BuildConfig.SERVER_IP
    private val dbLogin = BuildConfig.DB_LOGIN
    private val dbPassword = BuildConfig.DB_PASSWORD
    private val dbName = BuildConfig.DB_NAME
    private val dbPort = BuildConfig.DB_PORT
    private val dbUri = "jdbc:postgresql://$dbIp$dbPort$dbName"


    fun getPostgreSQL(): Connection {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(
            dbUri, dbLogin,
            dbPassword
        )
    }

}
