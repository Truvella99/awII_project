package it.polito.analytics.consumers

data class ProvaDTO(
    val id: Long,
    val name: String,
    val surname: String
) {
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this(0L, "", "")
}


