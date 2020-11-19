package com.example.devops_eksamen.db

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@ActiveProfiles("CardServiceTest,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class CardServiceTest{

    @Autowired
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var cardRepository: CardRepository

//    @LocalServerPort
//    protected var port = 0
//
//    @PostConstruct
//    fun init() {
//        RestAssured.baseURI = "http://localhost"
//        RestAssured.port = port
//        RestAssured.basePath = "/api/user-collections"
//        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
//    }

    @BeforeEach
    fun initTest(){
        cardRepository.deleteAll()
    }


    @Test
    fun testCreateUser(){
        val id = "foo"
        assertTrue(cardService.createCard(id))
        assertTrue(cardRepository.existsById(id))
    }

    @Test
    fun testFailCreateUserTwice(){
        val id = "foo"
        assertTrue(cardService.createCard(id))
        assertFalse(cardService.createCard(id))
    }

}
