package com.example.devops_eksamen

import com.example.devops_eksamen.db.CardRepository
import com.example.devops_eksamen.db.CardService
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import io.restassured.http.ContentType
import junit.framework.Assert.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.annotation.PostConstruct

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestAPITest.Companion.Initializer::class)])
internal class RestAPITest {

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var userService: CardService

    @Autowired
    private lateinit var userRepository: CardRepository


    @PostConstruct
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/user-collections"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    companion object {

        private lateinit var wiremockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun initClass() {
            wiremockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().notifier(ConsoleNotifier(true)))
            wiremockServer.start()


            val dto = WrappedResponse<Void>(code = 200).validated()
            val json = ObjectMapper().writeValueAsString(dto)

            wiremockServer.stubFor(
                    WireMock.get(WireMock.urlMatching("/api/cards/collection_.*"))
                            .willReturn(WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json; charset=utf-8")
                                    .withBody(json)))
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wiremockServer.stop()
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of("cardServiceAddress: localhost:${wiremockServer.port()}")
                        .applyTo(configurableApplicationContext.environment)
            }
        }
    }



    @BeforeEach
    fun initTest() {
        userRepository.deleteAll()
    }

    @Test
    fun testGetUser(){

        val id = "foo"
        userService.createCard(id)

        RestAssured.given().get("/$id")
                .then()
                .statusCode(200)
    }

    @Test
    fun testCreateUser() {
        val id = "foo"

        RestAssured.given().post("/$id")
                .then()
                .statusCode(201)

        assertTrue(userRepository.existsById(id))
    }

}
