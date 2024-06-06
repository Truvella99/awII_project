import React from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';
import {Navbar, Nav, Form, Button, Container} from 'react-bootstrap';
import { Link } from 'react-router-dom';


const Navigation = (props) => {
    return (
        <Navbar bg="primary"  variant="dark" fixed="top" className="py-1 px-3 " style={{ marginBottom: '150px' }}>
            <Link to="/ui">
                <Navbar.Brand>
                    <i className="bi bi-house"></i> Home
                </Navbar.Brand>
            </Link>
            <Nav className="ms-auto">
                <Navbar.Brand>
                    Job Placement FrontEnd
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
                    <Button variant="outline-light" onClick={() => window.location.href = props.me.loginUrl
                    }>Login</Button>
                )}
            </Nav>
        </Navbar>
    );
}

export { Navigation};
