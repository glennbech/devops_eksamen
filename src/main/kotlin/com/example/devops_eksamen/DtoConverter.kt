package com.example.devops_eksamen

import com.example.devops_eksamen.cards.GameDto
import com.example.devops_eksamen.db.Card

object DtoConverter {

    fun transform(user: Card) : GameDto {
        return GameDto().apply {
            id = user.cardId
        }
    }
}
