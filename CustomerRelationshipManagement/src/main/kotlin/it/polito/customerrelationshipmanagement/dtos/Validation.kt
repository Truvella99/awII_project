package it.polito.customerrelationshipmanagement.dtos

const val NOT_EMPTY_IF_NOT_NULL = "^\\s*\\S.*$"
const val SSN_CODE = "^(?!000|666|9\\d\\d)\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4}$"
const val EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
const val TELEPHONE = """^\+?\d{1,3}[-\s.]?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$"""
const val ADDRESS = "^[a-zA-Z0-9\\s.,'-]+$"
