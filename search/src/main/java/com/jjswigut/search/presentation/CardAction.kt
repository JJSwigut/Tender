package com.jjswigut.search.presentation

import com.jjswigut.data.models.MinimalRestaurant


sealed class CardAction {
    data class CardSwiped(val position: Int, val restaurant: MinimalRestaurant) : CardAction()
    data class CardRewound(val position: Int, val restaurant: MinimalRestaurant) :
        CardAction()
}
