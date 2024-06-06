import React, { useState } from 'react';
import { Form, Button, Container, Row, Col } from 'react-bootstrap';
function Home({ me }) {
    const [data, setData] = useState('');
    const [result, setResult] = useState(null);

    return (
        me && me.principal !== null && (
            <Container>
                <Row className="mt-5 mb-3">
                    <Col>
                        <Form style={{ border: 'dashed 1px gray', padding: '1em' }}>
                            <Form.Group as={Row} controlId="formData">
                                <Col>
                                    <Form.Control
                                        type="text"
                                        value={data || ''}
                                        onChange={(e) => setData(e.target.value)}
                                    />
                                </Col>
                                <Col>
                                    <Button
                                        onClick={() => {
                                            const postData = async () => {
                                                try {
                                                    const res = await fetch('/data', {
                                                        method: 'POST',
                                                        headers: {
                                                            'Content-Type': 'application/json',
                                                            'X-XSRF-TOKEN': me?.xsrfToken,
                                                        },
                                                        body: JSON.stringify({ data: data }),
                                                    });
                                                    const result = await res.json();
                                                    setResult(result);
                                                } catch (error) {
                                                    setResult({});
                                                }
                                            };
                                            postData().then();
                                        }}
                                    >
                                        Send
                                    </Button>
                                </Col>
                            </Form.Group>
                        </Form>
                    </Col>
                </Row>
                <Row className="mb-3">
                    <Col>
                        <div style={{ border: 'dashed 1px gray', padding: '1em' }}>
                            <pre>{JSON.stringify(result, null, 4)}</pre>
                        </div>
                    </Col>
                </Row>
            </Container>
        )
    );
};

export default Home;