package it.polito.apigateway.exceptions

import org.springframework.http.HttpStatusCode

class DocStoreException(val status: HttpStatusCode, message: String) : RuntimeException(message) {
}