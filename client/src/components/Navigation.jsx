import React from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';
import {Navbar, Nav, Form, Button, Container} from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { LogoutButton, LoginButton } from './Auth';

function TopBar({ me }) {
    return (
        <Navbar bg="primary" variant="dark" className="justify-content-end" style={{ padding: '4px' }}>
            <Container>
                <Navbar.Collapse className="justify-content-end">
                    {me && me.principal != null && (
                        <Form action={me.logoutUrl} method="post" inline>
                            <Navbar.Text className="me-2">Welcome, {me.name}</Navbar.Text>
                            <input type="hidden" name="_csrf" value={me.xsrfToken} />
                            <Button type="submit" variant="outline-light">Logout</Button>
                        </Form>
                    )}

                    {me && me.principal == null && me.loginUrl != null && (
                        <Button variant="outline-light" onClick={() => window.location.href = me.loginUrl}>Login</Button>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

const Navigation = (props) => {
    return (
        <Navbar bg="primary" expand="sm" variant="dark" fixed="top" className="py-1 px-3 ">
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
                <Navbar.Text className="mx-2">
                    {props.user && props.user.name && `Welcome, ${props.user.name}!`}
                </Navbar.Text>
                <Form className="mx-2">
                    {props.loggedIn ? <LogoutButton logout={props.logout} /> : <LoginButton />}
                </Form>
            </Nav>
        </Navbar>
    );
}

export { Navigation, TopBar};
