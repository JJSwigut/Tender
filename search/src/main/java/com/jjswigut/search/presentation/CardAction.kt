package com.jjswigut.search.presentation

import com.jjswigut.data.models.BusinessList


sealed class CardAction {
    data class CardSwiped(val position: Int, val restaurant: BusinessList.Businesses) : CardAction()
    data class CardRewound(val position: Int, val restaurant: BusinessList.Businesses) :
        CardAction()
}
