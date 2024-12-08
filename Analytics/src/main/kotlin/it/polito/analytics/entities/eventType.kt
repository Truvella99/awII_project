package it.polito.analytics.entities

enum class eventType(val type: Short) {
    None(0),
    CreateCustomer(1),
    UpdateCustomer(2),
    CreateProfessional(3),
    UpdateProfessional(4),
    CreateJobOffer(5),
    UpdateJobOffer(6);

    companion object {
        fun fromType(type: Short): eventType {
            return entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("Unknown event type: $type")
        }
    }
}