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
            consolidatedProfessionalId: jobOfferStatus.consolidatedProfessionalId,
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
            username: customer.username,
            password: customer.password,
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
    // console.log("API",customer); // Log the customer data being updated
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
            password: customer.password,
            category: customer.category,
            email: customer.email,
            telephone: customer.telephone,
            address: customer.address,
            notes: customer.notes,
            emailsToDelete: customer.emailsToDelete, // Include emailsToDelete in the request body
            addressesToDelete: customer.addressesToDelete, // Include addressesToDelete in the request body
            telephonesToDelete: customer.telephonesToDelete, // Include telephonesToDelete in the request body
            notesToDelete: customer.notesToDelete, // Include notesToDelete in the request body
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
async function updateProfessional(professionalId, professional, xsrfToken) {
    // console.log("API",customer); // Log the customer data being updated
    const response = await fetch(`/crm/API/professionals/${professionalId}`, {
        method: 'PUT', // Use PUT for updating existing resources
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify({
            name: professional.name,
            surname: professional.surname,
            ssncode: professional.ssncode,
            category: professional.category,
            email: professional.email,
            telephone: professional.telephone,
            password: professional.password,
            address: professional.address,
            // employmentState: professional.employmentState,
            geographicalLocation: professional.geographicalLocation,
            dailyRate: professional.dailyRate,
            skills: professional.skills,
            notes: professional.notes,
            emailsToDelete: professional.emailsToDelete, // Include emailsToDelete in the request body
            addressesToDelete: professional.addressesToDelete, // Include addressesToDelete in the request body
            telephonesToDelete: professional.telephonesToDelete, // Include telephonesToDelete in the request body
            notesToDelete: professional.notesToDelete, // Include notesToDelete in the request body
            skillsToDelete: professional.skillsToDelete, // Include notesToDelete in the request body
        }),
    }).catch(() => {
        throw {error: "Connection Error"}; // Handle connection errors
    });

    if (!response.ok) {
        const error = await response.json();
        throw {error: error.message || 'Error updating professional'}; // Handle non-200 responses
    }

    return await response.json(); // Return the updated customer data
}
/*
 * This function is used to get a customer by his Id
*/
async function getCustomerById(customerId, xsrfToken) {
    console.log(customerId);
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
    console.log(professional);
    const response = await fetch('/crm/API/professionals', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
            body: JSON.stringify({
            name: professional.name,
            surname: professional.surname,
            username: professional.username,
            password: professional.password,
            ssncode: professional.ssncode,
            category: professional.category,
            email: professional.email,
            telephone: professional.telephone,
            address: professional.address,
            employmentState: professional.employmentState,
            geographicalLocation: professional.geographicalLocation,
            dailyRate: professional.dailyRate,
            skills: professional.skills,
            notes: professional.notes
        }),
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

async function getAllCustomers(xsrfToken) {
    const response = await fetch(`/crm/API/customers/`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': xsrfToken,
      }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
      return await response.json();
    else
      throw await response.json();
}

async function getCustomersJobOffers(jobOffers, xsrfToken) {
    let params = new URLSearchParams();
    jobOffers.forEach(jobOffer => params.append('jobOffers', jobOffer));

    const response = await fetch(`/crm/API/customers/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}
  
async function getAllProfessionals(xsrfToken) {
    const response = await fetch(`/crm/API/professionals/`, {
        method: 'GET',
        headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getProfessionalSkillsJobOffers(skills, candidateJobOffers, abortedJobOffers, consolidatedJobOffers, completedJobOffers, xsrfToken) {
    let params = new URLSearchParams();
    skills.forEach(skill => params.append('skills', skill));
    candidateJobOffers.forEach(candidateProfessional => params.append('candidateProfessionals', candidateProfessional));
    abortedJobOffers.forEach(abortedProfessional => params.append('abortedProfessionals', abortedProfessional));
    consolidatedJobOffers.forEach(consolidatedProfessional => params.append('consolidatedProfessionals', consolidatedProfessional));
    completedJobOffers.forEach(completedProfessional => params.append('completedProfessionals', completedProfessional));

    const response = await fetch(`/crm/API/professionals/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getProfessionalsDistance(skills, candidateJobOffers, abortedJobOffers, consolidatedJobOffers, completedJobOffers, latitude, longitude, km, xsrfToken) {
    let params = new URLSearchParams();
    skills.forEach(skill => params.append('skills', skill));
    candidateJobOffers.forEach(candidateProfessional => params.append('candidateProfessionals', candidateProfessional));
    abortedJobOffers.forEach(abortedProfessional => params.append('abortedProfessionals', abortedProfessional));
    consolidatedJobOffers.forEach(consolidatedProfessional => params.append('consolidatedProfessionals', consolidatedProfessional));
    completedJobOffers.forEach(completedProfessional => params.append('completedProfessionals', completedProfessional));
    params.append('latitude', latitude);
    params.append('longitude', longitude);
    params.append('km', km);

    const response = await fetch(`/crm/API/professionals/distance/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getAllJobOffers(xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getProfessionalsInfo(candidateIds, abortedIds, consolidatedIds, completedIds, xsrfToken) {
    let params = new URLSearchParams();
    candidateIds.forEach(candidateId => params.append('candidateIds', candidateId));
    abortedIds.forEach(abortedId => params.append('abortedIds', abortedId));
    consolidatedIds.forEach(consolidatedId => params.append('consolidatedIds', consolidatedId));
    completedIds.forEach(completedId => params.append('completedIds', completedId));

    const response = await fetch(`/crm/API/professionals/info/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken) {
    let params = new URLSearchParams();
    skills.forEach(skill => params.append('skills', skill));
    candidateProfessionals.forEach(candidateProfessional => params.append('candidateProfessionals', candidateProfessional));
    abortedProfessionals.forEach(abortedProfessional => params.append('abortedProfessionals', abortedProfessional));
    consolidatedProfessionals.forEach(consolidatedProfessional => params.append('consolidatedProfessionals', consolidatedProfessional));
    completedProfessionals.forEach(completedProfessional => params.append('completedProfessionals', completedProfessional));

    const response = await fetch(`/crm/API/joboffers/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getOpenJobOffers(xsrfToken) {
    const response = await fetch(`/crm/API/joboffers/open/`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getOpenJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken) {
    let params = new URLSearchParams();
    skills.forEach(skill => params.append('skills', skill));
    candidateProfessionals.forEach(candidateProfessional => params.append('candidateProfessionals', candidateProfessional));
    abortedProfessionals.forEach(abortedProfessional => params.append('abortedProfessionals', abortedProfessional));
    consolidatedProfessionals.forEach(consolidatedProfessional => params.append('consolidatedProfessionals', consolidatedProfessional));
    completedProfessionals.forEach(completedProfessional => params.append('completedProfessionals', completedProfessional));

    const response = await fetch(`/crm/API/joboffers/open/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function createMessage(message, msg, bodyFlag, xsrfToken) {
    const response = await fetch('/crm/API/messages/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            channel: message.channel,
            priority: message.priority,
            subject: message.subject ? message.subject : null,
            body: bodyFlag ? msg : null,
            email: message.email ? message.email : null,
            telephone: message.telephone ? message.telephone : null,
            address: message.address ? message.address : null
        }))
    }).catch(() => {
        throw {error: "Connection Error"}
    });
    if (response.status === 201) {
        // 201 status code, parse and return the object
        return await response.json();
    } else {
        // json object provided by the server with the error
        throw await response.json();
    }
}

async function getMessagesReceived(xsrfToken) {
    let params = new URLSearchParams();
    params.append('state', 'received');

    const response = await fetch(`/crm/API/messages/?${params.toString()}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getAllMessages(xsrfToken) {
    const response = await fetch(`/crm/API/messages/`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getMessageById(id, xsrfToken) {
    const response = await fetch(`/crm/API/messages/${id}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function getMessageHistory(id, xsrfToken) {
    const response = await fetch(`/crm/API/messages/${id}/history`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        }
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function updateMessageState(message, id, xsrfToken) {
    const response = await fetch(`/crm/API/messages/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            targetState: message.targetState,
            comment: message.comment ? message.comment : null
        }))
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

async function updateMessagePriority(priority, id, xsrfToken) {
    const response = await fetch(`/crm/API/messages/${id}/priority`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken,
        },
        body: JSON.stringify(Object.assign({}, {
            priority: priority
        }))
    }).catch(() => { throw { error: "Connection Error" } });
    if (response.status === 200)
        return await response.json();
    else
        throw await response.json();
}

export default {
    createJobOffer,
    getJobOfferById,
    updateJobOfferById,
    updateJobOfferStatusbyId,
    getJobOfferHistoryById,
    getJobOfferValueById,
    createCustomer,
    updateCustomer,
    updateProfessional,
    getCustomerById,
    getCustomers,
    createProfessional,
    getProfessionalById,
    getProfessionals,
    getAllCustomers,
    getCustomersJobOffers,
    getAllProfessionals,
    getProfessionalSkillsJobOffers,
    getProfessionalsDistance,
    getAllJobOffers,
    getProfessionalsInfo,
    getJobOfferSkillsProfessionals,
    getOpenJobOffers,
    getOpenJobOfferSkillsProfessionals,
    createMessage,
    getMessagesReceived,
    getAllMessages,
    getMessageById,
    getMessageHistory,
    updateMessageState,
    updateMessagePriority
};
