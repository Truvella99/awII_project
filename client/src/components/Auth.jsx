import React, { useState } from 'react';
import { Form, Button, Col, Row, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

function LoginLayout(props) {
    return (
        <Row className="vh-100">
            <Col md={12} className="below-nav">
                <LoginForm login={props.login} />
            </Col>
        </Row>
    );
}

function LoginForm(props) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [show, setShow] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (event) => {
        event.preventDefault();
        const credentials = { username, password };

        props.login(credentials)
            .then(() => navigate("/"))
            .catch((err) => {
                setErrorMessage(err.error);
                setShow(true);
            });
    };

    return (
        <Row className="vh-100 justify-content-md-center">
            <Col md={4}>
                <h1>Login</h1>
                <Form onSubmit={handleSubmit}>
                    <Alert
                        dismissible
                        show={show}
                        onClose={() => setShow(false)}
                        variant="danger">
                        {errorMessage}
                    </Alert>
                    <Form.Group className="mb-3" controlId="username">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="text"
                            value={username} placeholder="Example: john.doe"
                            onChange={(ev) => setUsername(ev.target.value)}
                            required={true}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            value={password} placeholder="Enter the password."
                            onChange={(ev) => setPassword(ev.target.value)}
                            required={true} minLength={6}
                        />
                    </Form.Group>
                    <Button className="mt-3" type="submit">Login</Button>
                </Form>
            </Col>
        </Row>
    )
}


/*
function LoginButton(props) {
    const navigate = useNavigate();
    return (
        <Button variant="outline-light" onClick={() => navigate('/login')}>Login</Button>
    )
}
*/
export { LoginLayout };
