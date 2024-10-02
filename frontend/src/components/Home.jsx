import React, { useEffect, useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
function Home({ me }) {
    const [data, setData] = useState('');
    const [crmResult, setCrmResult] = useState(null);
    const [cmResult, setCmResult] = useState(null);
    const [docStoreresult, setDocStoreResult] = useState(null);

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
                        <div style={{ border: 'dashed 1px gray', padding: '1em' }}>
                            <p>User Infos:</p>
                            <pre>{JSON.stringify(me.principal.userInfo,null,4)}</pre>
                            <p>User Role (Customer Relationships Management Microservice):</p>
                            <pre>{crmResult? JSON.stringify(crmResult["roles"],null,4): null}</pre>
                            <p>User Role (Communication Manager Microservice):</p>
                            <pre>{cmResult? JSON.stringify(cmResult["roles"],null,4): null}</pre>
                            <p>User Role (Document Store Microservice):</p>
                            <pre>{docStoreresult? JSON.stringify(docStoreresult["roles"],null,4): null}</pre>
                        </div>
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