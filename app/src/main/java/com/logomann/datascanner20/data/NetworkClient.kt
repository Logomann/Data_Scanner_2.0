package com.logomann.datascanner20.data

import com.logomann.datascanner20.data.network.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}