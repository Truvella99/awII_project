import React, { useEffect, useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
function Home({ me }) {
    const [data, setData] = useState('');
    const [result, setResult] = useState(null);

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
                setResult(result);
            } catch (error) {
                setResult({});
            }
        };
        postData().then();
    }, []);
    return (
        me && me.principal !== null && (
            <Container>
                <Row className="mb-3">
                    <Col>
                        <div style={{ border: 'dashed 1px gray', padding: '1em' }}>
                            <p>User Infos:</p>
                            <pre>{JSON.stringify(me.principal.userInfo,null,4)}</pre>
                            <p>User Role (run CRM microservice to see):</p>
                            <pre>{result? JSON.stringify(result["roles"],null,4): null}</pre>
                        </div>
                    </Col>
                </Row>
            </Container>
        )
    );
};

export default Home;