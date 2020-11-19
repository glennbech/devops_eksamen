package com.example.devops_eksamen.cardsapi

import com.example.devops_eksamen.DtoConverter
import com.example.devops_eksamen.WrappedResponse
import com.example.devops_eksamen.cards.GameDto
import com.example.devops_eksamen.db.CardService
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@Api(value = "/api/user-collections", description = "Operations on card collections owned by users")
@RequestMapping(
        path = ["/api/user-collections"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestAPI(
        private val userService: CardService
) {

    private var meterRegistry: MeterRegistry? = null
    private val logger = LoggerFactory.getLogger(RestAPI::class.java)

    @Autowired
    fun BankAccountController(meterRegistry: MeterRegistry?) {
        this.meterRegistry = meterRegistry
    }

    @ApiOperation("Retrieve card collection information for a specific user")
    @GetMapping(path = ["/{userId}"])
    fun getUserInfo(
            @PathVariable("userId") userId: String
    ) : ResponseEntity<WrappedResponse<GameDto>>{

        val user = userService.findByIdEager(userId)
        if(user == null){
            logger.error("User not found")
            return ResponseEntity.status(404).body(WrappedResponse<GameDto>(404, message = "Card $userId not found."))
        }
        logger.info("Loaded user")
        return ResponseEntity.status(200).body(WrappedResponse(200, data = DtoConverter.transform(user)).validated())
    }


    var userAmount = 0
    @PostMapping(path = ["/updateInfo"], consumes = ["application/json"], produces = ["application/json"])
    fun addMember() {
        //meterRegistry!!.counter("txcount2", "currency", tx.getCurrency()).increment()
        meterRegistry!!.gauge("UserAmount", userAmount)
        meterRegistry!!.counter("UserAmountCounter", "Amount", userAmount.toString()).increment()
        DistributionSummary.builder("my.ratio")
                .scale(100.0)
                .sla(70, 80, 90)
                .register(meterRegistry!!)
        val timer: Timer = Timer
                .builder("my.timer")
                .description("a description of what this timer does") // optional
                .tags("region", "test") // optional
                .register(meterRegistry!!)

        println(userAmount)
    }

    @ApiOperation("Create a new user, with the given id")
    @PostMapping(path = ["/{userId}"])
    fun createUser(
            @PathVariable("userId") userId: String
    ): ResponseEntity<WrappedResponse<Void>> {
        val ok = userService.createCard(userId)
        if(!ok) {
            logger.error("User already exists")
            return ResponseEntity.status(400).body(WrappedResponse<Void>(code = 400, message = "User already exists").validated())
        }
        else {
            logger.info("Added user")
            ++userAmount
            return ResponseEntity.status(201).body(WrappedResponse<Void>(201).validated())

        }
    }

}
