package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Contact
import it.polito.customerrelationshipmanagement.entities.Email
import it.polito.customerrelationshipmanagement.entities.Telephone
import it.polito.customerrelationshipmanagement.entities.category
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

    @Query("""
    SELECT c FROM Contact c 
    LEFT JOIN c.emails e 
    LEFT JOIN c.addresses a 
    LEFT JOIN c.telephones t 
    WHERE c.category = :category 
    AND (
        (:name IS NULL OR UPPER(c.name) LIKE UPPER(CONCAT('%', :name, '%'))) 
        OR (:surname IS NULL OR UPPER(c.surname) LIKE UPPER(CONCAT('%', :surname, '%'))) 
        OR (:email IS NULL OR UPPER(e.email) LIKE UPPER(CONCAT('%', :email, '%'))) 
        OR (:address IS NULL OR UPPER(a.address) LIKE UPPER(CONCAT('%', :address, '%'))) 
        OR (:telephone IS NULL OR UPPER(t.telephone) LIKE UPPER(CONCAT('%', :telephone, '%')))
    )
""")
    fun findByCategoryAndCustomFilter(
        @Param("category") category: category,
        @Param("name") name: String?,
        @Param("surname") surname: String?,
        @Param("email") email: String?,
        @Param("address") address: String?,
        @Param("telephone") telephone: String?
    ): List<Contact>

    @Query("SELECT c FROM Contact c LEFT JOIN c.emails e LEFT JOIN c.telephones t LEFT JOIN c.addresses a WHERE (:email IS NULL OR e.email = :email) AND (:address IS NULL OR a.address = :address) AND (:telephone IS NULL OR t.telephone = :telephone)")
    fun findNewPending(@Param("email") email: String?, @Param("address") address: String?, @Param("telephone") telephone: String?): Contact?
}