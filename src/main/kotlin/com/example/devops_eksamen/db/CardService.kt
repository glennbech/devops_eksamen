package com.example.devops_eksamen.db

import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.LockModeType

@Repository
interface CardRepository : CrudRepository<Card, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Card u WHERE u.cardId = :id")
    fun lockedFind(@Param("id") cardId: String): Card?

}

@Service
@Transactional
class CardService(
        private val cardRepository: CardRepository
) {

    fun findByIdEager(userId: String) : Card?{

        return cardRepository.findById(userId).orElse(null)
    }


    fun createCard(userId: String) : Boolean{

        if(cardRepository.existsById(userId)){
            return false
        }

        val card = Card()
        card.cardId = userId

        cardRepository.save(card)
        return true
    }

}
