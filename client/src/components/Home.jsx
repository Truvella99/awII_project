import {Button, Container, Form, Navbar} from "react-bootstrap";
import React from "react";

function Home({ me }) {
    return (
        <>
            {me && me.principal !== null && (
                <Form action={me.logoutUrl} method="post" inline>
                    <Navbar.Text className="me-2">Welcome, {me.name}</Navbar.Text>
                    <input type="hidden" name="_csrf" value={me.xsrfToken} />
                    <Button type="submit" variant="outline-light">Logout</Button>
                </Form>
            )}

            {me && me.principal === null && me.loginUrl !== null && (
                <Button variant="outline-light" onClick={() => (window.location.href = me.loginUrl)}>
                    Login
                </Button>
            )}
            );
        </>
    )
}

export default Home;