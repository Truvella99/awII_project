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
import CustomerProfile from "./components/Customer.jsx";
import ProfessionalProfile from "./components/Professional.jsx";
import ProfessionalForm from "./components/ProfessionalForm.jsx";
import CreateCustomer from "./components/CustomerForm.jsx";
import EditCustomer from "./components/EditCustomer.jsx";
import EditProfessional from "./components/EditProfessional.jsx";
import { RegistrationForm } from './components/Registration';
import {Customers} from "./components/Customers.jsx";
import {Professionals} from "./components/Professionals.jsx";
import {JobOffers} from "./components/JobOffers.jsx";
import {Messages} from "./components/Messages.jsx";
import API from "./API.jsx";
import {ViewMessage} from "./components/ViewMessage.jsx";
import {MessageForm} from "./components/MessageForm.jsx";
import {Pending} from "./components/Pending.jsx";


function App() {
  const [me, setMe] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  //const [user, setUser] = useState(null);
  //const [data, setData] = useState('');
  const [role, setRole] = useState('');
  const Roles = ["professional", "customer", "operator", "manager"];
  const [unreadMessages, setUnreadMessages] = useState(0);
  const [pending, setPending] = useState(0);
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
        if (me.principal !== null) {
          setLoggedIn(true);
        }
      } catch (error) {
        setMe(null);
      }
    };
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
        const roles = result["roles"];
        const foundRole = roles.find(role => Roles.includes(role));
        setRole(foundRole);
        // Get the unread messages
        await getUnreadMessages(foundRole);
        // Get the pending messages
        await getPendingMessages(foundRole);
      } catch (error) {
        setRole('');
      }
    };
    //
    const getUnreadMessages = async (foundRole) => {
      try {
        if (foundRole === "manager" || foundRole === "operator") {
          const messages = await API.getMessagesReceived(me?.xsrfToken);
          setUnreadMessages(messages.length);
        }
      } catch (error) {
        console.log(error);
      }
    };
    const getPendingMessages = async (foundRole) => {
      try {
        if (foundRole === "manager" || foundRole === "operator") {
          const pendings = await API.getPendingContacts(me?.xsrfToken);
          setPending(pendings.length);
        }
      } catch (error) {
        console.log(error);
      }
    };

    fetchMe().then(() => postData());
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
      <BrowserRouter >
      <TokenContext.Provider value={ me?.xsrfToken }>
        <MessageContext.Provider value={ handleErrors }>
          <Container style={{ display: 'flex', justifyContent: 'center' }}>
            <Toast show={message !== ''} onClose={() => setMessage('')} delay={4000} autohide bg="danger" style={{ width: '600px' }}>
              <Toast.Body>{message}</Toast.Body>
            </Toast>
          </Container>
          <Navigation  me={me} role={role} logout={handleLogout} loggedIn={loggedIn} />
          <Container fluid /*className="mt-5"*/>
            <Routes>
              <Route path="/ui" element={<Home me={me}/>} />
              <Route path="/ui/professionals" element={<Professionals loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} pending={pending}/>} /> // Ale Costa
              <Route path="/ui/professionals/:professionalId" element={loggedIn  && (role !== "customer" )?( <ProfessionalProfile  role={role} xsrfToken={me?.xsrfToken}/>  ) : (<Navigate to="/ui" /> )} /> // Gaetano view and edit
              <Route path="/ui/professionals/edit/:professionalId" element={loggedIn && (role === "operator" || role === "manager" )?(<EditProfessional xsrfToken={me?.xsrfToken}/> ) : (<Navigate to="/ui" /> )} />  // Gaetano view and edit
              <Route path="/ui/professionals/addProfessional" element={loggedIn && (role === "operator" || role === "manager" ) ?( <ProfessionalForm xsrfToken={me?.xsrfToken}/> ) : (<Navigate to="/ui" /> )} />  // Gaetano view and edit
              <Route path="/ui/customers" element={<Customers loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} pending={pending}/>} /> // Ale Costa
              <Route path="/ui/customers/:customerId" element={loggedIn && role !== "professional"?(<CustomerProfile role={role} xsrfToken={me?.xsrfToken}/> ) : (<Navigate to="/ui" /> )} /> // Gaetano view and edit
              <Route path="/ui/customers/edit/:customerId" element={loggedIn && (role === "operator" || role === "manager" )?(<EditCustomer  xsrfToken={me?.xsrfToken}/> ) : (<Navigate to="/ui" /> )} />  // Gaetano view and edit
              <Route path="/ui/customers/addCustomer" element={loggedIn && (role === "operator" || role === "manager" )?(<CreateCustomer  xsrfToken={me?.xsrfToken}/> ) : (<Navigate to="/ui" /> )} />  // Gaetano view and edit
              <Route path="/ui/jobOffers" element={<JobOffers loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} pending={pending}/>} /> // Ale Costa
              <Route path="/ui/jobOffers/addJobOffer" element={<JobOfferContainer loggedIn={loggedIn} role={role}/>} /> // Minicucc
              <Route path="/ui/jobOffers/:jobOfferId" element={<JobOfferContainer loggedIn={loggedIn} role={role}/>} />  // Minicucc view and edit
              <Route path="/ui/messages" element={<Messages loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} setUnreadMessages={setUnreadMessages} pending={pending}/>} />
              <Route path="/ui/messages/:messageId" element={<ViewMessage loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} pending={pending}/>} />
              <Route path="/ui/messages/addMessage" element={loggedIn && (role === "operator" || role === "manager" )?(<MessageForm role={role} unreadMessages={unreadMessages} setUnreadMessages={setUnreadMessages} pending={pending} setPending={setPending}/>) : (<Navigate to="/ui" /> )} />
              <Route path="/ui/pending" element={<Pending loggedIn={loggedIn} role={role} unreadMessages={unreadMessages} pending={pending}/>} />
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
