package it.polito.customerrelationshipmanagement.dtos

data class ProvaDTO(
    val id: Long,
    val name: String,
    val surname: String,
){
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this(0L, "", "")
}


fun ProvaDTO.toDTO(): ProvaDTO =
    ProvaDTO(
        this.id,
        this.name,
        this.surname
    )

