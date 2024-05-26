package it.polito.communicationmanager.dtos

enum class channel {
    phonecall,
    textmessage,
    email
}

enum class priority {
    high,
    medium,
    low
}

enum class category {
    customer,
    professional,
    unknown
}

enum class state {
    received,
    read,
    discarded,
    processing,
    done,
    failed
}

enum class contactInfoState {
    deleted,
    active
}

enum class jobOfferStatus {
    created,
    aborted,
    selection_phase,
    candidate_proposal,
    consolidated,
    done
}

enum class employmentState {
    employed,
    available,
    not_available
}