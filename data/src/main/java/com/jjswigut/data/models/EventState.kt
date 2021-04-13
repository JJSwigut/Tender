package com.jjswigut.data.models

sealed class EventState {
    data class isStarted(val event: MatchingEvent) : EventState()
    data class isFinished(val event: MatchingEvent) : EventState()
}
