import React, { useEffect, useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
import API from '../API';
import {useNavigate} from "react-router-dom";
function Home({ me, role }) {
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
        name: "Giuseppe",
        surname: "Customer",
        username: "giuseppe5",
        password: "Password12345",
        ssncode: "111-23-9025",
        category: "customer",
        email: "john.doe50@example.com",
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
                    name: 'Sniper',
                    description: 'Analyze complex snipers.',
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
    const [files, setFiles] = useState([]);
    const [documentData, setDocumentData] = useState();
    const handleFiles = (ev) => {
        const filesArray = [...ev.target.files];
        // setFileError(null);

        filesArray.forEach((file, index) => {
            console.log(file);
            // Read and save file content
            const reader = new FileReader();
            reader.onload = (ev) => {
                if (file.size > 50000000) {
                    // setFileError(`File ${file.name} is too large (maximum size is 50MB).`);
                } else {
                    setFiles((prev) => [
                        ...prev,
                        {
                            name: file.name,
                            type: file.type,
                            content: file
                        }
                    ]);
                    setDocumentData({
                        name: file.name,
                        type: file.type,
                        content: file,
                        userId: me.principal.subject,
                        creationTimestamp: new Date().toISOString()
                    });

                }
            };
            reader.onerror = (ev) => {
                console.error(`Error reading ${file.name}:`, ev);
                // setFileError(`Error reading ${file.name}: ${ev}`);
            };
            reader.readAsDataURL(file);
        });
    }

    let createdCustomerId = 1;

    // Navigate/redirect
    if (!me || me.principal === null) {
        return (
            <Container style={{ display: 'flex', justifyContent: 'center' }}>
                <h1>Please Log In</h1>
            </Container>
        );
    } else {
        if (role === "customer") {
            navigate("/ui/professionals");
            return null;
        } else if (role === "professional") {
            navigate("/ui/jobOffers");
            return null;
        } else if (role === "operator" || role === "manager") {
            navigate("/ui/customers");
            return null;
        }
    }

    // return (
    //     me && me.principal !== null ? (
    //         <Container>
    //             <Row className="mb-3">
    //                 <Col>
    //                     <Button onClick={() => navigate("/ui/messages/2")}>Message</Button>
    //                     <Button onClick={() => navigate("/ui/Registration")}>Registration</Button>
    //                     <Button onClick={() => navigate("/ui/jobOffers/addJobOffer")}>add</Button>
    //                     <Button onClick={() => navigate("/ui/jobOffers/1")}>view</Button>
    //                     <Button onClick={()=>navigate("/ui/professionals/")}>professional</Button>
    //                     <Button onClick={()=>navigate("/ui/customers/2")}>Go to customer</Button>
    //                     <Button onClick={()=>navigate("/ui/professionals/2")}>Go to professional</Button>
    //                     <Button variant={"success"} onClick={()=>navigate("/ui/professionals/addProfessional")}>Add Professional Page</Button>
    //                     <Button variant={"success"} onClick={()=>navigate("/ui/customers/addCustomer")}>Add Customer Page</Button>
    //                     <Button variant={"warning"} onClick={()=>navigate("/ui/professionals/edit/1")}>Edit Professional Page</Button>
    //                     <Button variant={"warning"} onClick={()=>navigate("/ui/customers/edit/1")}>Edit Customer Page</Button>
    //                     <Button onClick={async () => {
    //                         console.log(me?.principal?.subject);
    //
    //                         console.log(JSON.stringify(customer, null, 2));
    //
    //                          await API.createCustomer(customer,me.xsrfToken);
    //
    //                     }}>add customer prefixed</Button>
    //
    //                     {/*<Col>*/}
    //                     {/*    <Form.Group className="mb-3" controlId="files">*/}
    //                     {/*        <Form.Label>Attachments (optional)</Form.Label>*/}
    //                     {/*        <Form.Control*/}
    //                     {/*            type="file"*/}
    //                     {/*            name="files"*/}
    //                     {/*            multiple*/}
    //                     {/*            onChange={handleFiles}*/}
    //                     {/*        />*/}
    //                     {/*    </Form.Group>*/}
    //                     {/*    <input type="file" id="fileInput" />*/}
    //
    //                     {/*    <Button*/}
    //                     {/*        onClick={async () => {*/}
    //                     {/*            try {*/}
    //
    //                     {/*                // Assuming 'fileInput' is a reference to a file input element*/}
    //
    //                     {/*                const userId = me?.principal.subject;*/}
    //                     {/*                const result = await API.postDocument(userId, me.xsrfToken);*/}
    //
    //                     {/*                console.log('Document created successfully:', result);*/}
    //                     {/*            } catch (error) {*/}
    //                     {/*                console.error('Failed to create document:', error);*/}
    //                     {/*            }*/}
    //                     {/*        }}*/}
    //                     {/*    >*/}
    //                     {/*        Add Document*/}
    //                     {/*    </Button>*/}
    //                     {/*</Col>*/}
    //                     <Button onClick={async () => {
    //                         let updatedCustomer = {
    //                             name: "Gennaro Updated afwsef",
    //                             surname: "Prof sfd",
    //                             ssncode: "111-23-9022",
    //                             password: "password",
    //                             email: "jj3@libero.it",
    //                             telephone: "+391234567892",
    //                             address: "Via Roma 23"
    //                         };
    //
    //                         // Mostra i dati aggiornati del cliente nel log
    //                         console.log("Updating customer:", JSON.stringify(updatedCustomer, null, 2));
    //
    //                         // Aggiornamento del cliente
    //                         await API.updateCustomer(createdCustomerId, updatedCustomer, me.xsrfToken);
    //                         console.log("Customer updated successfully");
    //                     }
    //                     }>update customer prefixed</Button>
    //                     <Button onClick={async () => {
    //                         let updateProfessional = {
    //                             name: "Gennaro Updated afwsef",
    //                             surname: "Prof sfd",
    //                             ssncode: "111-23-9022",
    //                             skills: [
    //                                 { skill: "Skill 1" },
    //                                 { skill: "Skill 2" }
    //                             ]
    //                         };
    //
    //                         // Mostra i dati aggiornati del cliente nel log
    //                         console.log("Updating professional:", JSON.stringify(updateProfessional, null, 2));
    //
    //                         // Aggiornamento del cliente
    //                         await API.updateProfessional(1, updateProfessional, me.xsrfToken);
    //                         console.log("professional updated successfully");
    //                     }
    //                     }>update prof prefixed</Button>
    //                     <Button onClick={async () => {
    //                         let professional = {
    //                             name: "Giuseppe",
    //                             surname: "Professional",
    //                             username: "giuseppe6",
    //                             ssncode: "111-23-9025",
    //                             category: "professional",
    //                             password: "password",
    //                             email: null,
    //                             telephone: "+391234567890",
    //                             employmentState: "available",
    //                             geographicalLocation: { first: "41.06", second: "15.05"},
    //                             dailyRate: 45.1,
    //                             notes: ['Has been a customer for 5 years', 'Very punctual with payments'],
    //                             skills: [
    //                                 { skill: "Skill 1" },
    //                                 { skill: "Skill 2" }
    //                             ]
    //                         };
    //
    //
    //                         await API.createProfessional(professional,me.xsrfToken);
    //                     }}>add professional prefixed</Button>
    //
    //                     <Button onClick={async () => {
    //                         let message = {
    //                             channel: "email",
    //                             priority: "high",
    //                             email: "john.doe@example.com",
    //                             telephone: null,
    //                             address: null,
    //                             subject: "Test message 2",
    //                             body: "This is the second test message"
    //                         };
    //                         await API.createMessage(message, me.xsrfToken);
    //                     }}>add message prefixed</Button>
    //                     <Button variant="primary" onClick={() => navigate("/ui/customers")}> Go to Home </Button>
    //                     <Button variant="primary" onClick={() => navigate("/ui/analytics")}> Analytics </Button>
    //                 </Col>
    //             </Row>
    //         </Container>
    //     ) : (
    //         <Container style={{ display: 'flex', justifyContent: 'center' }}>
    //             <h1>Please Log In</h1>
    //         </Container>
    //     )
    // );
};

export default Home;