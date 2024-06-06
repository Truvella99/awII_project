import React from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';
import {Navbar, Nav, Form, Button, Container} from 'react-bootstrap';
import { Link } from 'react-router-dom';

function TopBar(props) {
    return (
        <Navbar bg="primary" variant="dark" className="justify-content-end" >
            <Container>
                <Navbar.Collapse className="justify-content-end">
                    {props.me && props.me.principal != null && (
                        <Form action={props.me.logoutUrl} method="post" inline>
                            <Navbar.Text className="me-2">Welcome, {props.me.name}</Navbar.Text>
                            <input type="hidden" name="_csrf" value={props.me.xsrfToken} />
                            <Button type="submit" variant="outline-light">Logout</Button>
                        </Form>
                    )}
                    {props.me && props.me.principal == null && props.me.loginUrl != null && (
                        <Button variant="outline-light" onClick={() => window.location.href = props.me.loginUrl}>Login</Button>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

const Navigation = (props) => {
    return (
        <Navbar bg="primary"  variant="dark" fixed="top" className="py-1 px-3 " style={{ marginBottom: '150px' }}>
            <Link to="/">
                <Navbar.Brand>
                    <i className="bi bi-house"></i> Home
                </Navbar.Brand>
            </Link>
            <Nav className="ms-auto">
                <Navbar.Brand>
                    Customer Relationship Management
                </Navbar.Brand>
            </Nav>
            <Nav className="ms-auto">
                        {props.me && props.me.principal != null && (
                                <Form action={props.me.logoutUrl} method="post" inline>
                                    <Navbar.Text className="me-2">Welcome, {props.me.name}</Navbar.Text>
                                    <input type="hidden" name="_csrf" value={props.me.xsrfToken} />
                                    <Button type="submit" variant="outline-light">Logout</Button>
                                </Form>
                            )}

                {props.me && props.me.principal == null && props.me.loginUrl != null && (
                    <Button variant="outline-light" onClick={() => window.location.href = props.me.loginUrl}>Login</Button>
                )}
            </Nav>
        </Navbar>
    );
}

export { Navigation, TopBar};
