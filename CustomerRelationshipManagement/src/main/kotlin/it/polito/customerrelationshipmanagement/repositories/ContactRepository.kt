package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Contact
import it.polito.customerrelationshipmanagement.entities.Email
import it.polito.customerrelationshipmanagement.entities.Telephone
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository: JpaRepository<Contact,Long> {
    @Query("SELECT c FROM Contact c LEFT JOIN c.emails e LEFT JOIN c.telephones t WHERE (:email IS NULL OR e.email = :email) AND (:name IS NULL OR c.name = :name) AND (:telephone IS NULL OR t.telephone = :telephone)")
    fun findByEmailsOrNameOrTelephones(@Param("email") email: String?, @Param("name") name: String?, @Param("telephone") telephone: String?, p: PageRequest?): List<Contact>

    @Query("SELECT c FROM Contact c LEFT JOIN c.emails e LEFT JOIN c.telephones t WHERE (c.surname IS NULL) AND (c.ssncode IS NULL) AND (c.category IS NULL) AND (:email IS NULL OR e.email = :email) AND (:name IS NULL OR c.name = :name) AND (:telephone IS NULL OR t.telephone = :telephone)")
    fun findBySurnameNullSsnCodeNullCategoryNullAndEmailsOrNameOrTelephones(@Param("email") email: String?, @Param("name") name: String?, @Param("telephone") telephone: String?, p: PageRequest?): List<Contact>

}