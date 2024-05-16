package com.logomann.datascanner20.domain.network

import com.logomann.datascanner20.domain.models.ConnectionModel


interface ConnectionInteractor {
    fun request(model: ConnectionModel, onComplete: (String?, Int) -> Unit)

}