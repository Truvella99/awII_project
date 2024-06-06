import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import React, { useState, useEffect, useContext } from 'react';
import { Container, Toast } from 'react-bootstrap/';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import {Navigation, TopBar} from "./components/Navigation";
import { LoginLayout } from "./components/Auth";
import MessageContext from "./messageCtx";
import './App.css';

function App() {
  const [me, setMe] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
  const [data, setData] = useState('');
  const [message, setMessage] = useState('');
  const [handleErrors, setHandleErrors] = useState(null);

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await fetch('/me');
        const me = await res.json();
        console.log(me);
        setMe(me);
      } catch (error) {
        setMe(null);
      }
    };
    fetchMe()
  }, []);

  const handleLogout = () => {
    // Perform logout actions
    setLoggedIn(false);
    setUser(null);
  };

  const handleLogin = (credentials) => {
    // Perform login actions
    // After successful login, set the state accordingly
  };

  return (
      <BrowserRouter>
        <MessageContext.Provider value={{ handleErrors }}>
          <Navigation  me={me} logout={handleLogout} user={user} loggedIn={loggedIn} />
          <Container fluid className="mt-5">
            <Routes>
              <Route path="/" element={<Home me={me}/>} />
              <Route path="/login" element={!loggedIn ? <LoginLayout login={handleLogin} /> : <Navigate replace to='/' />} />
            </Routes>
            <Toast show={message !== ''} onClose={() => setMessage('')} delay={4000} autohide bg="danger">
              <Toast.Body>{message}</Toast.Body>
            </Toast>
          </Container>
        </MessageContext.Provider>
      </BrowserRouter>
  );
}

export default App;
