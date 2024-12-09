import React from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';
import {Navbar, Nav, Form, Button, Container} from 'react-bootstrap';
import {Link, useNavigate} from 'react-router-dom';


const Navigation = (props) => {
    const navigate = useNavigate()
    const role = props.role
    const userId = props.me && props.me.principal && props.me.principal.subject;

    return (
        <Navbar bg="primary" variant="dark" fixed="top" className="py-1 px-3">
            <Link to="/ui">
                <Navbar.Brand>
                    <i className="bi bi-house"></i> Home
                </Navbar.Brand>
            </Link>
            <Nav className="ms-auto" style={props.me && props.me.principal != null ? {paddingLeft: '150px'} : {}}>
                <Navbar.Brand>
                    Job Placement
                </Navbar.Brand>
            </Nav>
            <Nav className="ms-auto">
                {props.me && props.me.principal != null && (
                    <Form action={props.me.logoutUrl} method="post" className="d-flex align-items-center">
                        <Navbar.Text className="me-2">
                            Welcome, {props.me.name}
                        </Navbar.Text>
                        {/* Icona profilo */}
                        {(role==="professional" || role==="customer") &&
                            <Button variant="outline-light" className="me-2" onClick={() => {
                                role === "professional" ? navigate(`/ui/professionals/${userId}`) : navigate(`/ui/customers/${userId}`)
                            }}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                                     className="bi bi-person-circle" viewBox="0 0 16 16">
                                    <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
                                    <path fill-rule="evenodd"
                                          d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1"/>
                                </svg>
                            </Button>
                        }

                        <input type="hidden" name="_csrf" value={props.me.xsrfToken}/>
                        <Button type="submit" onClick={() => {
                            props.handleLogout()
                        }} variant="outline-light">
                        Logout
                        </Button>
                    </Form>
                )}

                {props.me && props.me.principal == null && props.me.loginUrl != null && (
                    <Button variant="outline-light" onClick={() => window.location.href = props.me.loginUrl}>
                        Login
                    </Button>
                )}
            </Nav>
        </Navbar>


    );
}

export { Navigation};
