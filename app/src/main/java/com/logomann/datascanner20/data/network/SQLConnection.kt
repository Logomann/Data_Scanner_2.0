package com.logomann.datascanner20.data.network

import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager

class SQLConnection {
    companion object {
        private const val DB_IP = "192.162.242.157"
        private const val DB_LOGIN = "postgres"
        private const val DB_PASSWORD = "7feuq4HJ"
        private const val DB_NAME = "TSD"
        private const val DB_PORT = ":5432/"
        private const val DB_URI = "jdbc:postgresql://$DB_IP$DB_PORT$DB_NAME"
    }

    fun getPostgreSQL(): Connection {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(
            DB_URI, DB_LOGIN,
            DB_PASSWORD
        )
    }

}
