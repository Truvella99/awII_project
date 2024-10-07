import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import React, { useState, useEffect, useContext } from 'react';
import { Container, Toast } from 'react-bootstrap/';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Home from './components/Home';
import {Navigation} from "./components/Navigation";
import {MessageContext,  TokenContext } from "./messageCtx";
import './App.css';
import { JobOfferContainer } from './components/JobOffer';

function App() {
  const [me, setMe] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  //const [user, setUser] = useState(null);
  //const [data, setData] = useState('');
  const [message, setMessage] = useState('');
  
  // function to handle the application errors, all displayed into the Alert under the NavHeader
  function handleErrors(err) {
    let errMsg = 'Unkwnown error';
    if (err.detail) {
      errMsg = err.detail;
    }
  
    setMessage(errMsg);
    //setDirty(true);
  }

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await fetch('/me');
        const me = await res.json();
        //console.log(me);
        setMe(me);
        if(me.principal !== null) {
          setLoggedIn(true);
        }
      } catch (error) {
        setMe(null);
      }
    };
    fetchMe()
  }, []);

  const handleLogout = () => {
    // Perform logout actions
    setLoggedIn(false);
    //setUser(null);
  };

  const handleLogin = (credentials) => {
    // Perform login actions
    // After successful login, set the state accordingly
  };

  return (
      <BrowserRouter>
      <TokenContext.Provider value={ me?.xsrfToken }>
        <MessageContext.Provider value={ handleErrors }>
          <Container style={{ display: 'flex', justifyContent: 'center' }}>
            <Toast show={message !== ''} onClose={() => setMessage('')} delay={4000} autohide bg="danger" style={{ width: '600px' }}>
              <Toast.Body>{message}</Toast.Body>
            </Toast>
          </Container>
          <Navigation  me={me} logout={handleLogout} loggedIn={loggedIn} />
          <Container fluid className="mt-5">
            <Routes>
              <Route path="/ui" element={<Home me={me}/>} />
              <Route path="/ui/professionals" element={<></>} /> // Ale Costa
              <Route path="/ui/professionals/professionalId" element={<></>} /> // Gaetano view and edit
              <Route path="/ui/customers" element={<></>} /> // Ale Costa
              <Route path="/ui/customers/customerId" element={<></>} /> // Gaetano view and edit
              <Route path="/ui/jobOffers" element={<></>} /> // Ale Costa
              <Route path="/ui/jobOffers/addJobOffer" element={<JobOfferContainer loggedIn={loggedIn}/>} /> // Minicucc
              <Route path="/ui/jobOffers/:jobOfferId" element={<JobOfferContainer loggedIn={loggedIn}/>} />  // Minicucc view and edit
              <Route path="/ui/Registration" element={<></>} /> // Giuseppe
              <Route path="/ui/Analytics" element={<></>} />
            </Routes>
          </Container>
        </MessageContext.Provider>
        </TokenContext.Provider>
      </BrowserRouter>
  );
}

export default App;
