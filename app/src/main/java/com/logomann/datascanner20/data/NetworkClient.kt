package com.logomann.datascanner20.data

import com.logomann.datascanner20.data.network.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}