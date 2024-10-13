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
    return (
        me && me.principal !== null ? (
            <Container>
                <Row className="mb-3">
                    <Col>

                        <Button onClick={()=>navigate("/ui/customers/8")}>Go to customer</Button>
                        <Button onClick={()=>navigate("/ui/professionals/15")}>Go to professional</Button>
                        <Button onClick={()=>navigate("/ui/professionals/addProfessional")}>Add Professional Page</Button>

                        <Button onClick={async () => {
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

                            console.log(JSON.stringify(customer, null, 2));

                            await API.createCustomer(customer,me.xsrfToken);
                        }}>add customer</Button>
                        <Button onClick={async () => {
                            let professional = {
                                name: "Giuseppe",
                                surname: "Professional",
                                ssncode: "111-23-9025",
                                category: "professional",
                                email: "john.doe2@example.com",
                                employmentState: "available",
                                geographicalLocation: { first: "12.0", second: "45.7"},
                                dailyRate: 45.1,
                                notes: ['Has been a customer for 5 years', 'Very punctual with payments'],
                                skills: [
                                    { skill: "Skill 1" },
                                    { skill: "Skill 2" }
                                ]
                            };


                            await API.createProfessional(professional,me.xsrfToken);
                        }}>add professional</Button>
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