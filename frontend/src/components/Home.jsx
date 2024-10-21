import React, { useEffect, useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import API from '../API';
import {useNavigate} from "react-router-dom";
function Home({ me }) {
    const [data, setData] = useState('');
    const [crmResult, setCrmResult] = useState(null);
    const [cmResult, setCmResult] = useState(null);
    const [docStoreresult, setDocStoreResult] = useState(null);
    const navigate = useNavigate()
    
    useEffect(() => {
        const postData = async () => {
            const getAdminToken = async () => {
                const params = new URLSearchParams();
                params.append('client_id', 'crmclient'); // Client ID per l'amministratore
                params.append('username', 'admin'); // Username dell'amministratore
                params.append('password', 'password'); // Password dell'amministratore
                params.append('grant_type', 'password'); // Tipo di grant

                try {
                    const response = await fetch('http://localhost:9090/realms/CRMRealm/protocol/openid-connect/token', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: params
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(`Error fetching admin token: ${errorData.error_description || errorData}`);
                    }

                    const data = await response.json();
                    console.log("Admin token retrieved:", data);
                    return data.access_token; // Restituisce il token JWT
                } catch (error) {
                    console.error("Failed to retrieve admin token:", error);
                }
            };

// Esempio di utilizzo della funzione
            getAdminToken().then(token => {
                if (token) {
                    // Utilizza il token per altre operazioni, ad esempio registrare un utente
                    console.log("Retrieved admin token:", token);
                }
            });
            const registerUser = async (userData, role) => {
                // Step 1: Ottieni l'admin token
                const adminToken = await getAdminToken(); // Implementa questa funzione per ottenere un token admin

                // Step 2: Crea un nuovo utente
                const createUserResponse = await fetch('http://localhost:9090/admin/realms/CRMRealm/users', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${adminToken}`
                    },
                    body: JSON.stringify(userData)
                });

                if (!createUserResponse.ok) {
                    const errorData = await createUserResponse.json();
                    console.error('Error creating user:', errorData);
                    return;
                }

                // Step 3: Ottieni l'ID dell'utente
                const userIdResponse = await fetch('http://localhost:9090/admin/realms/CRMRealm/users?username=' + userData.username, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${adminToken}`
                    }
                });

                const users = await userIdResponse.json();
                const userId = users[0].id; // Assumendo che l'username sia unico

                // Step 4: Assegna il ruolo
                const assignRoleResponse = await fetch(`http://localhost:9090/admin/realms/CRMRealm/users/${userId}/role-mappings/realm`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${adminToken}`
                    },
                    body: JSON.stringify([{
                        id: role.id, // ID del ruolo
                        name: role.name // Nome del ruolo
                    }])
                });

                if (!assignRoleResponse.ok) {
                    const errorData = await assignRoleResponse.json();
                    console.error('Error assigning role:', errorData);
                } else {
                    console.log('User registered and role assigned successfully!');
                }
            };

// Esempio di utilizzo
            const newUserData = {
                username: 'newCustomer',
                enabled: true,
                firstName: 'John',
                lastName: 'Doe',
                email: 'john.doe@example.com',
                credentials: [
                    {
                        type: 'password',
                        value: 'password123',
                        temporary: false
                    }
                ]
            };

            const roleToAssign = { id: 'role-id-customer', name: 'Customer' }; // ID e nome del ruolo da assegnare

            registerUser(newUserData, roleToAssign);

            try {
                const res = await fetch('crm/data', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-XSRF-TOKEN': me?.xsrfToken,
                    }
                });
                const result = await res.json();
                setCrmResult(result);
            } catch (error) {
                setCrmResult({});
            }
            try {
                const res = await fetch('communicationManager/data', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-XSRF-TOKEN': me?.xsrfToken,
                    }
                });
                const result = await res.json();
                setCmResult(result);
            } catch (error) {
                setCmResult({});
            }
            try {
                const res = await fetch('docStore/data', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-XSRF-TOKEN': me?.xsrfToken,
                    }
                });
                const result = await res.json();
                setDocStoreResult(result);
            } catch (error) {
                setDocStoreResult({});
            }
        };
        postData().then();
    }, []);
    let customer = {
        name: "Gennaro",
        surname: "Customer",
        ssncode: "111-23-9025",
        category: "customer",
        email: "john.doe@example.com",
        telephone: "+391234567890",
        address: "Via Roma 1",
        notes: ['Has been a customer for 5 years', 'Very punctual with payments'],
        jobOffers: []
    };
    let createdCustomerId = 1;
    return (
        me && me.principal !== null ? (
            <Container>
                <Row className="mb-3">
                    <Col>
                    <Button onClick={() => navigate("/ui/Registration")}>Registration</Button>
                        <Button onClick={() => navigate("/ui/jobOffers/addJobOffer")}>add</Button>
                        <Button onClick={() => navigate("/ui/jobOffers/1")}>view</Button>
                        <Button onClick={()=>navigate("/ui/professionals/")}>professional</Button>
                        <Button onClick={()=>navigate("/ui/customers/1")}>Go to customer</Button>
                        <Button onClick={()=>navigate("/ui/professionals/1")}>Go to professional</Button>
                        <Button variant={"success"} onClick={()=>navigate("/ui/professionals/addProfessional")}>Add Professional Page</Button>
                        <Button variant={"success"} onClick={()=>navigate("/ui/customers/addCustomer")}>Add Customer Page</Button>
                        <Button variant={"warning"} onClick={()=>navigate("/ui/professionals/edit/1")}>Edit Professional Page</Button>
                        <Button variant={"warning"} onClick={()=>navigate("/ui/customers/edit/1")}>Edit Customer Page</Button>
                        <Button onClick={async () => {

                            console.log(JSON.stringify(customer, null, 2));

                             await API.createCustomer(customer,me.xsrfToken);

                        }}>add customer prefixed</Button>
                        <Button onClick={async () => {
                            let updatedCustomer = {
                                name: "Gennaro Updated afwsef",
                                surname: "Prof sfd",
                                ssncode: "111-23-9022",
                                email: "jj3@libero.it",
                                telephone: "+391234567892",
                                address: "Via Roma 23",
                                emailsToDelete: [1],
                                telephonesToDelete: [1],
                                addressesToDelete: [1],
                                notesToDelete: [1],
                            };

                            // Mostra i dati aggiornati del cliente nel log
                            console.log("Updating customer:", JSON.stringify(updatedCustomer, null, 2));

                            // Aggiornamento del cliente
                            await API.updateCustomer(createdCustomerId, updatedCustomer, me.xsrfToken);
                            console.log("Customer updated successfully");
                        }
                        }>update customer prefixed</Button>
                        <Button onClick={async () => {
                            let updateProfessional = {
                                name: "Gennaro Updated afwsef",
                                surname: "Prof sfd",
                                ssncode: "111-23-9022",
                                skills: [
                                    { skill: "Skill 1" },
                                    { skill: "Skill 2" }
                                ]
                            };

                            // Mostra i dati aggiornati del cliente nel log
                            console.log("Updating professional:", JSON.stringify(updateProfessional, null, 2));

                            // Aggiornamento del cliente
                            await API.updateProfessional(1, updateProfessional, me.xsrfToken);
                            console.log("professional updated successfully");
                        }
                        }>update prof prefixed</Button>
                        <Button onClick={async () => {
                            let professional = {
                                name: "Giuseppe",
                                surname: "Professional",
                                ssncode: "111-23-9025",
                                category: "professional",
                                email: null,
                                telephone: "+391234567890",
                                employmentState: "available",
                                geographicalLocation: { first: "41.06", second: "15.05"},
                                dailyRate: 45.1,
                                notes: ['Has been a customer for 5 years', 'Very punctual with payments'],
                                skills: [
                                    { skill: "Skill 1" },
                                    { skill: "Skill 2" }
                                ]
                            };


                            await API.createProfessional(professional,me.xsrfToken);
                        }}>add professional prefixed</Button>
                        <Button variant="primary" onClick={() => navigate("/ui/customers")}> Go to Home </Button>
                    </Col>
                </Row>
            </Container>
        ) : (
            <Container style={{ display: 'flex', justifyContent: 'center' }}>
                <h1>Please Log In</h1>
            </Container>
        )
    );
};

export default Home;