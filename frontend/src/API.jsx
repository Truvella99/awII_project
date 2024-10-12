/*
 * This function is used to create a new job offer
*/
async function createJobOffer(jobOffer, xsrfToken) {
    const response = await fetch('/crm/API/joboffers/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            name: jobOffer.name,
            description: jobOffer.description,
            currentState: jobOffer.currentState,
            currentStateNote: jobOffer.currentStateNote,
            duration: jobOffer.duration,
            profitMargin: jobOffer.profitMargin,
            customerId: jobOffer.customerId,
            skills: jobOffer.skills,
            skillsToDelete: jobOffer.skillsToDelete
        })),
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 201) {
        // 201 status code, parse and return the object
        const jobOffer = await response.json();
        return jobOffer;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to get a job offer by his Id
*/
async function getJobOfferById(jobOfferId, xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/${jobOfferId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const jobOffer = await response.json();
        return jobOffer;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to update a job offer by his Id
*/
async function updateJobOfferById(jobOffer, jobOfferId, xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/${jobOfferId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            name: jobOffer.name,
            description: jobOffer.description,
            currentState: jobOffer.currentState,
            currentStateNote: jobOffer.currentStateNote,
            duration: jobOffer.duration,
            profitMargin: jobOffer.profitMargin,
            customerId: jobOffer.customerId,
            skills: jobOffer.skills,
            skillsToDelete: jobOffer.skillsToDelete
        })),
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const jobOffer = await response.json();
        return jobOffer;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to create a new job offer
*/
async function updateJobOfferStatusbyId(jobOfferStatus, jobOfferId, xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/${jobOfferId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            targetStatus: jobOfferStatus.targetStatus,
            note: jobOfferStatus.note,
            professionalId: jobOfferStatus.professionalId,
            professionalsId: jobOfferStatus.professionalsId
        })),
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const jobOffer = await response.json();
        return jobOffer;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to get a job offer value by his Id
*/
async function getJobOfferValueById(jobOfferId, xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/${jobOfferId}/value`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const jobOfferValue = await response.json();
        return jobOfferValue;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to get a job offer history by his Id
*/
async function getJobOfferHistoryById(jobOfferId, xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/${jobOfferId}/history`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const jobOfferHistory = await response.json();
        return jobOfferHistory;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to create a customer
*/
async function createCustomer(customer, xsrfToken) {
    console.log(customer);
    const response = await fetch('/crm/API/customers/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify({
            name: customer.name,
            surname: customer.surname,
            ssncode: customer.ssncode,
            category: customer.category,
            email: customer.email,
            telephone: customer.telephone,
            address: customer.address,
            notes: customer.notes,
            jobOffers: customer.jobOffers
        }),
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 201) {
        // 201 status code, parse and return the object
        const customer = await response.json();
        return customer;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
}

async function updateCustomer(customerId, customer, xsrfToken) {
    console.log(customer); // Log the customer data being updated
    const response = await fetch(`/crm/API/customers/${customerId}`, {
        method: 'PUT', // Use PUT for updating existing resources
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify({
            name: customer.name,
            surname: customer.surname,
            ssncode: customer.ssncode,
            category: customer.category,
            email: customer.emails[0]?.email, // Assuming you want to take the first email
            telephone: customer.telephones[0]?.telephone, // Assuming you want to take the first telephone
            address: customer.addresses[0]?.address, // Assuming you want to take the first address
            notes: customer.notes,
            jobOffers: customer.jobOffers // Include jobOffers in the request body
        }),
    }).catch(() => {
        throw {error: "Connection Error"}; // Handle connection errors
    });

    if (!response.ok) {
        const error = await response.json();
        throw {error: error.message || 'Error updating customer'}; // Handle non-200 responses
    }

    return await response.json(); // Return the updated customer data
}

/*
 * This function is used to get a customer by his Id
*/
async function getCustomerById(customerId, xsrfToken) {
    const response = await fetch(`/crm/API/customers/${customerId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const customer = await response.json();
        console.log(customer);
        return customer;
    } else {
        // json object provided by the server with the error

        const error = await response.json();
        console.log(error);

        throw error;
    }
};

/*
 * This function is used to get a customer by his Id
*/
async function getCustomers(filter, xsrfToken) {
    const response = await fetch(`/crm/API/customers/filters/${filter}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const customers = await response.json();
        return customers;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to create a professional
*/
async function createProfessional(professional, xsrfToken) {
    const response = await fetch('/crm/API/professionals', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            name: professional.name,
            surname: professional.surname,
            ssncode: professional.ssncode,
            category: professional.category,
            email: professional.email,
            telephone: professional.telephone,
            address: professional.address,
            employmentState: professional.employmentState,
            geographicalLocation: professional.geographicalLocation,
            dailyRate: professional.dailyRate,
            skills: professional.skills,
            notes: professional.notes,
            jobOffers: professional.jobOffers
        })),
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 201) {
        // 201 status code, parse and return the object
        const professional = await response.json();
        return professional;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to get a customer by his Id
*/
async function getProfessionalById(professionalId, xsrfToken) {
    const response = await fetch(`/crm/API/professionals/${professionalId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const professional = await response.json();
        return professional;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

/*
 * This function is used to get a customer by his Id
*/
async function getProfessionals(filter, xsrfToken) {
    const response = await fetch(`/crm/API/professionals/filters/${filter}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 200) {
        // 200 status code, parse and return the object
        const professionals = await response.json();
        return professionals;
    } else {
        // json object provided by the server with the error
        const error = await response.json();
        throw error;
    }
};

export default {
    createJobOffer,
    getJobOfferById,
    updateJobOfferById,
    updateJobOfferStatusbyId,
    getJobOfferHistoryById,
    getJobOfferValueById,
    createCustomer,
    updateCustomer,
    getCustomerById,
    getCustomers,
    createProfessional,
    getProfessionalById,
    getProfessionals
};