package com.sun.jsondemo

data class ResponseData (
    val name: String,
    val professional_id : ProfessionalId,
    val address: List<Address>
)

data class ProfessionalId(
    val linkedin_id: String,
    val github_id: String,
    val twitter_id: String
)

data class Address(
    val city: String,
    val pin: Int
)