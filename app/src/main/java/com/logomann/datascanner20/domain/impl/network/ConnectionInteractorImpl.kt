package com.logomann.datascanner20.domain.impl.network

import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.domain.network.ConnectionRepository
import com.logomann.datascanner20.util.Resource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConnectionInteractorImpl(private val repository: ConnectionRepository) :
    ConnectionInteractor {

    @OptIn(DelicateCoroutinesApi::class)
    override fun request(model: ConnectionModel, onComplete: (String?, Int) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val job = launch {
                when (val resource = repository.update(model)) {
                    is Resource.Error -> {
                        onComplete(resource.message, 0)
                    }

                    is Resource.Success -> {
                        onComplete(resource.data, 1)
                    }
                }
            }
            delay(10000L)
            if (job.isActive) {
                job.cancel()
                onComplete("", 2)
            }
        }

    }
}

