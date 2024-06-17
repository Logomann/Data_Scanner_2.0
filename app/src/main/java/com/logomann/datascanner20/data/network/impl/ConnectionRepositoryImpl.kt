package com.logomann.datascanner20.data.network.impl

import com.logomann.datascanner20.data.NetworkClient
import com.logomann.datascanner20.data.network.ConnectionRequest
import com.logomann.datascanner20.data.network.ConnectionResponse
import com.logomann.datascanner20.domain.network.ConnectionRepository
import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.util.Resource


class ConnectionRepositoryImpl(private val networkClient: NetworkClient) : ConnectionRepository {

    override suspend fun update(model: ConnectionModel): Resource<String> {
        val response = networkClient.doRequest(ConnectionRequest(model))

        val message = when (response.resultCode) {
            -1 -> {
                Resource.Error(null)
            }

            400 -> {
                Resource.Error((response as ConnectionResponse).results)
            }

            else -> {
                Resource.Success((response as ConnectionResponse).results)
            }
        }
        return message
    }

}