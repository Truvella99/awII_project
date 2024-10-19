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
        jobOffers: [
            {
                first: null,
                second: {
                    name: 'Software Engineer',
                    description: 'Develop and maintain web applications.',
                    currentState: 'done',
                    currentStateNote: 'Looking for suitable candidates',
                    duration: 6,
                    profitMargin: 10,
                    skills: [
                        { skill: "Skill 1" },
                        { skill: "Skill 2" }
                    ]
                }
            },
            {
                first: null,
                second: {
                    name: 'Data Scientist',
                    description: 'Analyze complex data sets to assist decision-making.',
                    currentState: 'aborted',
                    currentStateNote: 'Position filled',
                    duration: 12,
                    profitMargin: 15,
                    skills: [
                        { skill: "Skill 3" },
                        { skill: "Skill 4" }
                    ]
                }
            },
            {
                first: null,
                second: {
                    name: 'Data Scientist',
                    description: 'Analyze complex data sets to assist decision-making.',
                    currentState: 'candidate_proposal',
                    currentStateNote: 'Position filled',
                    duration: 12,
                    profitMargin: 15,
                    skills: [
                        { skill: "Skill 5" },
                        { skill: "Skill 6" }
                    ]
                }
            }
        ]
    };
    let createdCustomerId = 1;
    return (
        me && me.principal !== null ? (
            <Container>
                <Row className="mb-3">
                    <Col>

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