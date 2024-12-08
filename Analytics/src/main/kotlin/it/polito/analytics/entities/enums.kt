package it.polito.analytics.entities

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.convert.WritingConverter
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.dialect.R2dbcDialect

// processing (one of the middle state of the professional jobOffer that for analytics does not matter)
// removed (candidate proposal -> selection_phase case): remove the candidates so in the analytics the same
enum class professionalJobOfferState(val value: Short) {
    Completed(0),Aborted(1),Candidated(2),Processing(3),Removed(4);

    companion object {
        fun fromValue(value: Short): professionalJobOfferState {
            return entries.first { it.value == value }
        }
    }
}

@WritingConverter
class ProfessionalJobOfferStateToShortConverter : Converter<professionalJobOfferState, Short> {
    override fun convert(source: professionalJobOfferState): Short {
        return source.value
    }
}

@ReadingConverter
class ShortToProfessionalJobOfferStateConverter : Converter<Short, professionalJobOfferState> {
    override fun convert(source: Short): professionalJobOfferState {
        return professionalJobOfferState.fromValue(source)
    }
}

// processing (one of the middle state of the customer jobOffer that for analytics does not matter)
enum class customerJobOfferState(val value: Short) {
    Completed(0),Aborted(1),Created(2),Processing(3);

    companion object {
        fun fromValue(value: Short): customerJobOfferState {
            return entries.first { it.value == value }
        }
    }
}

@WritingConverter
class CustomerJobOfferStateToShortConverter : Converter<customerJobOfferState, Short> {
    override fun convert(source: customerJobOfferState): Short {
        return source.value
    }
}

@ReadingConverter
class ShortToCustomerJobOfferStateConverter : Converter<Short, customerJobOfferState> {
    override fun convert(source: Short): customerJobOfferState {
        return customerJobOfferState.fromValue(source)
    }
}

@Configuration
class R2dbcConfig(private val databaseClient: DatabaseClient) {
    @Bean
    fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters = listOf(
            CustomerJobOfferStateToShortConverter(),
            ShortToCustomerJobOfferStateConverter(),
            ProfessionalJobOfferStateToShortConverter(),
            ShortToProfessionalJobOfferStateConverter()
        )
        val dialect: R2dbcDialect = DialectResolver.getDialect(databaseClient.connectionFactory)
        return R2dbcCustomConversions.of(dialect, converters)
    }
}