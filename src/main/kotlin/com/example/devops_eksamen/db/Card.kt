package com.example.devops_eksamen.db

import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
class Card(

        @get:Id
        @get:NotBlank
        var cardId: String? = null

)
