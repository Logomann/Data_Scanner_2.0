package com.logomann.datascanner20.domain.impl.network


import com.logomann.datascanner20.domain.models.ConnectionModel
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.domain.network.ConnectionRepository
import com.logomann.datascanner20.util.Resource
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors


class ConnectionInteractorImpl(private val repository: ConnectionRepository) :
    ConnectionInteractor {

    private val executor = Executors.newCachedThreadPool()
    private val timer = Timer()
    override fun request(model: ConnectionModel, onComplete: (String?, Int) -> Unit) {

        val future = executor.submit {
            when (val resource = repository.update(model)) {
                is Resource.Error -> {
                    onComplete(resource.message, 0)
                }

                is Resource.Success -> {
                    onComplete(resource.data, 1)
                }
            }
        }

        timer.schedule(object : TimerTask() {

            override fun run() {
                if (!future.isDone) {
                    future.cancel(true)
                    onComplete("", 2)
                }

            }
        }, 10000L)

    }

}