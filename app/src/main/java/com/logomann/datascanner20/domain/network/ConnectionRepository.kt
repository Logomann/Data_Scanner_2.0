package com.logomann.datascanner20.domain.network

import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.util.Resource
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
   suspend fun update(model: ConnectionModel) : Resource<String>

}