import React, {useContext, useEffect, useState} from 'react';
import {Container, Row, Col, Card, Button, Form, Alert, InputGroup} from 'react-bootstrap';
import API from '../API'; // API to handle saving and fetching data
import 'react-phone-number-input/style.css';
import PhoneInput from 'react-phone-number-input';
import {useNavigate, useParams} from "react-router-dom";
import InputMask from "react-input-mask";
import axios from "axios";
import {AddressSelector} from "./Utils.jsx";
import {MessageContext} from "../messageCtx.js";
import {Eye, EyeSlash} from "react-bootstrap-icons";

const EditCustomer = ({ xsrfToken }) => {
    const [customer, setCustomer] = useState({
        name: '',
        surname: '',
        ssncode: '',
        category: 'Customer',
        email: null,
        telephone: null,
        address: null,
        emails: [],//emails already present
        telephones: [],//telephones already present
        addresses: [],//addresses already present
        emailsToDelete: [],
        password: '',
        telephonesToDelete: [],
        addressesToDelete: [],
        notesToDelete: [],
        notes: ['']
    });
    const {customerId} = useParams();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formErrors, setFormErrors] = useState({});
    const [showNewEmailField, setShowNewEmailField] = useState(false);
    const [showNewTelephoneField, setShowNewTelephoneField] = useState(false);
    const [showNewNoteField, setShowNewNoteField] = useState(false);
    const [showNewAddressField, setShowNewAddressField] = useState(false);
    const [address, setAddress] = useState({text: '', lat: 0.0, lng: 0.0, invalid: false});
    const navigate = useNavigate()
    const [newNotes, setNewNotes] = useState([]);
    const handleErrors = useContext(MessageContext);
    const [showPassword, setShowPassword] = useState(false); // Stato per gestire visibilità password
    const [files, setFiles] = useState([]);
    const [fileError, setFileError] = useState(null);
    const togglePasswordVisibility = () => {
        setShowPassword(prevState => !prevState);
    };
    // Regex patterns for validation
    const NOT_EMPTY_IF_NOT_NULL = /^\s*\S.*$/;
    const SSN_CODE = /^(?!000|666|9\d\d)\d{3}-(?!00)\d{2}-(?!0000)\d{4}$/;
    const EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const TELEPHONE = /^(\+?\d{1,3}[-\s.]?)?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$/;
    const ADDRESS = /^[a-zA-Z0-9\s.,'-]+$/;
    function addressValidation(address, setAddress) {
        return new Promise((resolve, reject) => {
            // Create a Geocoder instance
            const geocoder = new google.maps.Geocoder();

            // Define the Geocoding request
            const geocodeRequest = {
                address: address.text,
            };

            // Perform Geocoding
            geocoder.geocode(geocodeRequest, (results, status) => {

                if (status === google.maps.GeocoderStatus.OK && results.length > 0) {

                    // Address is valid
                    setAddress({text: address.text, lat: address.lat, lng: address.lng, invalid: false});
                    console.log("Address is valid", address);

                    resolve(undefined); // Resolve with undefined for a valid address
                } else {
                    // Address is invalid
                    setAddress({text: address.text, lat: address.lat, lng: address.lng, invalid: true});
                    reject(true); // Resolve with true for an invalid address
                }
            });
        });
    }

    const fetchGeographicalLocation = async () => {


        setLoading(true);

        try {
            const API_KEY = 'AIzaSyCO5hFwnkcQjDkoivao8qpJbKvITf_vb1g';  // Inserisci la tua chiave API di Google Maps
            const response = await axios.get(
                `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address.text)}&key=${API_KEY}`
            );

            const location = response.data.results[0].geometry.location;

            setCustomer((prevCustomer) => ({
                ...prevCustomer,
                address: response.data.results[0].formatted_address
            }));
        } catch (error) {
            console.error("Error fetching geographical location", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (customerId) {
            const fetchCustomer = async () => {
                try {
                    setLoading(true);
                    const fetchedCustomer = await API.getCustomerById(customerId, xsrfToken);
                    console.log("fetchedCustomer", fetchedCustomer);
                    setCustomer(fetchedCustomer);
                    setLoading(false);
                } catch (err) {
                    setError(err.error || 'Error fetching customer data');
                    setLoading(false);
                }
            };
            fetchCustomer();
        }
    }, [customerId, xsrfToken]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCustomer((prevCustomer) => ({
            ...prevCustomer,
            [name]: value,
        }));
    };
    const handleNoteChange = (index, value) => {
        const updatedNotes = [...newNotes];
        updatedNotes[index] = value; // Aggiorna la nota specifica
        setNewNotes(updatedNotes);
    };

    const handleAddNote = () => {
        setNewNotes([...newNotes, '']); // Aggiungi una nuova stringa vuota
        setShowNewNoteField(true)
    };

    const handleRemoveNote = (index) => {
        const updatedNotes = newNotes.filter((_, i) => i !== index);
        setNewNotes(updatedNotes);
        if (updatedNotes.length === 0)
            setShowNewNoteField(false)

    };
    const handleFiles = (ev) => {
        const filesArray = [...ev.target.files];
        setFileError(null);

        filesArray.forEach((file, index) => {
            console.log(file);
            // Read and save file content
            const reader = new FileReader();
            reader.onload = (ev) => {
                if (file.size > 50000000) {
                    setFileError(`File ${file.name} is too large (maximum size is 50MB).`);
                } else {
                    setFiles((prev) => [
                        ...prev,
                        {
                            name: file.name,
                            type: file.type,
                            content: ev.target.result.split("base64,")[1]
                        }
                    ]);
                }
            };
            reader.onerror = (ev) => {
                console.error(`Error reading ${file.name}:`, ev);
                setFileError(`Error reading ${file.name}: ${ev}`);
            };
            reader.readAsDataURL(file);
        });
    }

    const handleRemoveField = (field, index) => {
        setCustomer((prevCustomer) => {
            const updatedField = [...prevCustomer[field]];
            const removedItem = updatedField[index];

            // Cambia lo stato in "deleted" se esiste un ID
            if (removedItem.id) {
                updatedField[index] = { ...removedItem, state: 'deleted' }; // Imposta lo stato su "deleted"
                // Aggiungi l'ID alla lista corretta per la cancellazione
                if (field === 'emails') {
                    return {
                        ...prevCustomer,
                        [field]: updatedField,
                        emailsToDelete: prevCustomer.emailsToDelete ? [...prevCustomer.emailsToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'telephones') {
                    return {
                        ...prevCustomer,
                        [field]: updatedField,
                        telephonesToDelete: prevCustomer.telephonesToDelete ? [...prevCustomer.telephonesToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'addresses') {
                    return {
                        ...prevCustomer,
                        [field]: updatedField,
                        addressesToDelete: prevCustomer.addressesToDelete ? [...prevCustomer.addressesToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'notes') {
                    return {
                        ...prevCustomer,
                        [field]: updatedField,
                        notesToDelete: prevCustomer.notesToDelete ? [...prevCustomer.notesToDelete, removedItem.id] : [removedItem.id],
                    };
                }
            } else {
                // Rimuovi nuovi campi aggiunti (non salvati nel database)
                updatedField.splice(index, 1);
                return { ...prevCustomer, [field]: updatedField };
            }
        });
    };

    const validateForm = async () => {
        const errors = {};
        console.log("customer", customer.password);
        //Password
        if(!customer.password){
            delete customer.password;
        }else
        if (customer.password.length < 8) {
            errors.password = "Passwprd must be at least 8 characters long";
        }else if (!/[A-Z]/.test(customer.password)) {
            errors.password = "Password must contain at least one uppercase letter.";
        }else if (!/[a-z]/.test(customer.password)) {
            errors.password = "Password must contain at least one lowercase letter.";
        }else if (!/[0-9]/.test(customer.password)) {
            errors.password = "Password must contain at least one digit.";
        }
        if (!NOT_EMPTY_IF_NOT_NULL.test(customer.name)) {
            errors.name = "Name cannot be empty.";
        }

        if (!NOT_EMPTY_IF_NOT_NULL.test(customer.surname)) {
            errors.surname = "Surname cannot be empty.";
        }

        if (!SSN_CODE.test(customer.ssncode)) {
            errors.ssncode = "SSN Code must be valid in the format XXX-XX-XXXX.";
        }


        if (customer.email && !EMAIL.test(customer.email)) {
            errors.email = "Please enter a valid email address.";
        }
        if(customer.emails.some(email =>email.state==="active" &&  email.email === customer.email?.trim()) ){
            errors.email = "Email already present";
        }

        if (customer.telephone && !TELEPHONE.test(customer.telephone)) {
            errors.telephone = "Please enter a valid telephone number.";
        }
        if(customer.telephones.some(telephone =>telephone.state==="active" && telephone.telephone === customer.telephone)){
            errors.telephone = "Telephone already present";
        }

        if (customer.address && !ADDRESS.test(customer.address)) {
            errors.address = "Please enter a valid address.";
        }
        if(customer.addresses.some(address => address.state==="active" && address.address === customer.address)){
            errors.address = "Address already present";
        }
        try {
            if (address.text) {
                await addressValidation(address, setAddress);
                await fetchGeographicalLocation();
                if (address.invalid === true) {
                    errors.address = "Please enter a valid address.";
                }
                customer.address = address.text;
            } else {
                customer.address = null;
            }
        } catch (error) {
            errors.address = "Please enter a valid address.";
        }
        if (newNotes.some(note => !note)) {
            errors.notes = 'Notes cannot be empty';
        }
        // Aggiungi controllo per address, telephone, email: almeno uno è richiesto
        if (!customer.email && !customer.telephone && (!customer.address) && customer.emails.filter(it =>it.state ==="active").length === 0 && customer.telephones.filter(it =>it.state ==="active").length === 0 && customer.addresses.filter(it =>it.state ==="active").length === 0) {
            errors.contact = 'Please provide at least one valid contact method: email, telephone, or address.';
        }
        setFormErrors(errors);

        return Object.keys(errors).length === 0;
    };



    const handleSubmit = async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const isValid = await validateForm();
        console.log("isValid", isValid);
        console.log("customer", customer);
        if (!isValid) return;
        if (fileError)
            return;

        setLoading(true);
        try {
            // Only update customer functionality
            customer.notes = newNotes

            await API.updateCustomer(customerId, customer, xsrfToken);
            setLoading(false);
            navigate("/ui")

            // Optionally, navigate back or display a success message
        } catch (err) {
            setError(err.error || 'Error saving customer');
            setLoading(false);
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }
    // If error, show an error message
    if (error) {
        handleErrors({detail: error})
        setError(null);
    }


    return (
        <Container fluid className="py-5">
            <Row>
                <Col className="mx-auto">
                    <Card className="shadow-lg">
                        <Card.Header className="bg-primary text-white">
                            <h3>Edit Customer</h3>
                        </Card.Header>
                        <Card.Body>
                            <Form noValidate onSubmit={handleSubmit}>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="name">
                                            <Form.Label>Name</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="name"
                                                value={customer.name}
                                                onChange={handleInputChange}
                                                isInvalid={!!formErrors.name}
                                                placeholder="Enter name"
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.name}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="surname">
                                            <Form.Label>Surname</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="surname"
                                                placeholder="Enter surname"
                                                value={customer.surname}
                                                onChange={handleInputChange}
                                                isInvalid={!!formErrors.surname}
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.surname}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="ssncode">
                                            <Form.Label>SSN Code</Form.Label>
                                            <InputMask
                                                mask="999-99-9999"
                                                value={customer.ssncode}
                                                onChange={handleInputChange}
                                                placeholder="Enter SSN code"
                                                required
                                                className={`form-control ${formErrors.ssncode ? 'is-invalid' : ''}`}
                                                name="ssncode"
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.ssncode}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="psw">
                                            <Form.Label>Password (Optional)</Form.Label>
                                            <InputGroup>
                                                <Form.Control
                                                    type={showPassword ? 'text' : 'password'} // Alterna testo/password
                                                    name="password"
                                                    placeholder="Enter password"
                                                    value={customer.password}
                                                    onChange={handleInputChange}
                                                    isInvalid={!!formErrors.password} // Mostra errore se esiste
                                                    required
                                                />
                                                {/* Aggiungi icona per mostrare/nascondere la password */}
                                                <InputGroup.Text onClick={togglePasswordVisibility} style={{ cursor: 'pointer' }}>
                                                    {showPassword ? <EyeSlash /> : <Eye />} {/* Icona cambia dinamicamente */}
                                                </InputGroup.Text>
                                                <Form.Control.Feedback type="invalid">
                                                    {formErrors.password}
                                                </Form.Control.Feedback>
                                            </InputGroup>
                                        </Form.Group>
                                    </Col>
                                    {/*<Col md={6}>*/}
                                    {/*    <Form.Group className="mb-3" controlId="category">*/}
                                    {/*        <Form.Label>Category</Form.Label>*/}
                                    {/*        <Form.Control*/}
                                    {/*            type="text"*/}
                                    {/*            name="category"*/}
                                    {/*            value={customer.category.toUpperCase()}*/}
                                    {/*            disabled={true}*/}
                                    {/*            required*/}
                                    {/*        />*/}
                                    {/*    </Form.Group>*/}
                                    {/*</Col>*/}
                                </Row>

                                {/* Emails */}

                                <Row>
                                    <Col lg={6}>
                                        <Form.Group>
                                            <Form.Label>Emails</Form.Label>
                                            <div className="text-secondary mb-3" hidden={customer.emails.some(email => email.state === "active") || showNewEmailField}>
                                                Customer has no emails
                                            </div>
                                            {customer.emails.map((email, index) => (
                                                email.state === "active" ? (
                                                    <div key={index} className="d-flex align-items-center mb-1">
                                                    <Form.Control
                                                        hidden={email.state === "deleted"} // Nasconde il campo se marcato come "deleted"
                                                        disabled={true}
                                                        type="email"
                                                        name={`email${index}`}
                                                        value={email.email}
                                                        required
                                                    />
                                                    <Button hidden={email.state === "deleted"} variant="danger" onClick={() => handleRemoveField('emails', index)} className="ms-2"> {/* Aggiunto className per margine a sinistra */}
                                                        Remove
                                                    </Button>
                                                </div>
                                                ) : null
                                            ))}
                                            {/* Bottone per aggiungere una nuova email */}
                                            <Button
                                                variant="primary"
                                                onClick={() => {
                                                    setShowNewEmailField(true); // Show the new email field
                                                    setCustomer((prev) => ({
                                                        ...prev,
                                                        email: '', // Reset the email field in your customer state
                                                    }));
                                                }}
                                                hidden={showNewEmailField === true}
                                            >
                                                Add Email
                                            </Button>
                                            <div className="d-flex align-items-center ">
                                                {/* Campo per la nuova email, visibile solo se showNewEmailField è true */}
                                                <Form.Control
                                                    type="email"
                                                    hidden={!showNewEmailField} // Nasconde il campo se showNewEmailField è false
                                                    name="email"
                                                    placeholder="Enter Email"
                                                    value={customer.email}
                                                    onChange={handleInputChange}
                                                    isInvalid={!!formErrors.email}
                                                />
                                                {/* Bottone "Remove" per la nuova email, visibile solo se showNewEmailField è true */}
                                                <Button
                                                    hidden={!showNewEmailField} // Nasconde il bottone se showNewEmailField è false
                                                    variant="danger"
                                                    onClick={() => {
                                                        setShowNewEmailField(false); // Nasconde il campo della nuova email
                                                        setCustomer((prev) => ({
                                                            ...prev,
                                                            email: null, // Svuota il campo customer.email
                                                        }));
                                                    }}
                                                    className="ms-2" // Aggiunto className per margine a sinistra
                                                >
                                                    Remove
                                                </Button>
                                            </div>
                                            {formErrors.email && (
                                                <div className="text-danger mb-3">
                                                    {formErrors.email}
                                                </div>
                                            )}
                                        </Form.Group>
                                    </Col>
                                    {/* Telephones */}
                                    <Col lg={6}>
                                        <Form.Group>
                                            <Form.Label>Telephones</Form.Label>
                                            <div className="text-secondary mb-3" hidden={customer.telephones.some(telephone => telephone.state === "active") || showNewTelephoneField}>
                                                Customer has no telephones number
                                            </div>
                                            {customer.telephones.map((telephone, index) => (
                                                telephone.state === "active" ? (
                                                    <div key={index} className="d-flex align-items-center mb-1">
                                                    <Form.Control
                                                        hidden={telephone.state === "deleted"} // Nasconde il campo se marcato come "deleted"
                                                        disabled={true}
                                                        value={telephone.telephone}
                                                        required
                                                    />
                                                    <Button hidden={telephone.state === "deleted"} variant="danger" onClick={() => handleRemoveField('telephones', index)} className="ms-2">
                                                        Remove
                                                    </Button>
                                                </div>
                                                ) : null
                                            ))}
                                            {/* Bottone per aggiungere un nuovo telefono */}
                                            <Button
                                                variant="primary"
                                                onClick={() => {
                                                    setShowNewTelephoneField(true); // Show the new telephone field
                                                    setCustomer((prev) => ({
                                                        ...prev,
                                                        telephone: '', // Reset the telephone field in your customer state
                                                    }));
                                                }}
                                                hidden={showNewTelephoneField === true}
                                            >
                                                Add Telephone
                                            </Button>
                                            <div className="d-flex align-items-center s">
                                                {/* Campo per il nuovo telefono, visibile solo se showNewTelephoneField è true */}
                                                <PhoneInput
                                                    style={{ display: showNewTelephoneField ? 'flex' : 'none' }} // Usa display per nascondere completamente il campo
                                                    defaultCountry="IT"
                                                    value={customer.telephone}
                                                    onChange={(value) =>
                                                        setCustomer((prev) => ({
                                                            ...prev,
                                                            telephone: value,
                                                        }))
                                                    }
                                                    placeholder="Enter Telephone"
                                                />
                                                {/* Bottone "Remove" per il nuovo telefono */}
                                                <Button
                                                    hidden={!showNewTelephoneField}
                                                    variant="danger"
                                                    onClick={() => {
                                                        setShowNewTelephoneField(false); // Nasconde il campo del nuovo telefono
                                                        setCustomer((prev) => ({
                                                            ...prev,
                                                            telephone: null, // Svuota il campo customer.telephone
                                                        }));
                                                    }}
                                                    className="ms-2" // Aggiunto className per margine a sinistra
                                                >
                                                    Remove
                                                </Button>
                                            </div>
                                            {formErrors.telephone && (
                                                <div className="text-danger mb-3">
                                                    {formErrors.telephone}
                                                </div>
                                            )}
                                        </Form.Group>
                                    </Col>
                                </Row>


                                {/* Addresses */}
                                <Row>
                                    <Col lg={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Addresses</Form.Label>

                                            <div className="text-secondary mb-3" hidden={customer.addresses.some(address => address.state === "active") || showNewAddressField}>
                                                Customer has no addresses
                                            </div>
                                            {customer.addresses.map((address, index) => (
                                                address.state === "active" ? (
                                                    <div key={index} className="d-flex align-items-center mb-2">
                                                    <Form.Control
                                                        hidden={address.state === "deleted"} // Nasconde il campo se marcato come "deleted"
                                                        disabled={true}
                                                        type="text"
                                                        value={address.address}
                                                        required

                                                    />
                                                    <Button className="ms-2" hidden={address.state === "deleted"} variant="danger" onClick={() => handleRemoveField('addresses', index)}>
                                                        Remove
                                                    </Button>
                                                </div>
                                                ) : null
                                            ))}

                                            {/* Bottone per aggiungere un nuovo indirizzo */}
                                            <Button
                                                variant="primary"
                                                onClick={() => {
                                                    setShowNewAddressField(true); // Show the new address field
                                                    setAddress({text: '', lat: 0.0, lng: 0.0, invalid: false}); // Reset the address field in your customer state
                                                }}
                                                hidden={showNewAddressField === true}
                                            >
                                                Add Address
                                            </Button>
                                            <div className="d-flex align-items-center mb-2">
                                                {/* Campo per il nuovo indirizzo, visibile solo se showNewAddressField è true */}
                                                <AddressSelector hidden={!showNewAddressField} address={address} setAddress={setAddress}/>

                                                {/* Bottone "Remove" per il nuovo indirizzo */}
                                                <Button
                                                    hidden={!showNewAddressField}
                                                    variant="danger"
                                                    onClick={() => {
                                                        setCustomer((prev) => ({
                                                            ...prev,
                                                            address: null, // Svuota il campo customer.address
                                                        }));
                                                        setShowNewAddressField(false); // Nasconde il campo del nuovo indirizzo

                                                    }}
                                                    className="ms-2"
                                                >
                                                    Remove
                                                </Button>
                                            </div>
                                            {/*{formErrors.address && (*/}
                                            {/*    <div className="text-danger mb-3">*/}
                                            {/*        {formErrors.address}*/}
                                            {/*    </div>*/}
                                            {/*)}*/}
                                            {formErrors.contact && (
                                                <div className="text-danger mb-3">
                                                    {formErrors.contact}
                                                </div>
                                            )}
                                        </Form.Group>
                                    </Col>

                                    {/* Notes */}
                                    <Col>
                                        <Form.Group controlId="notes">
                                            <Form.Label>Notes</Form.Label>
                                            <div className="text-secondary mb-3" hidden={customer.notes.some(note => note.state === "active") || showNewNoteField}>
                                                Customer has no Notes
                                            </div>
                                            {customer.notes.map((note, index) => (
                                            note.state === "active" ? (
                                                <div key={index} className="d-flex align-items-center mb-1">
                                                    <Form.Control
                                                        hidden={note.state !== "active"} // Nasconde il campo se non attivo
                                                        disabled={true}
                                                        type="text"
                                                        placeholder="Add note"
                                                        value={note.note}
                                                    />
                                                    <Button
                                                        hidden={note.state !== "active"} // Nasconde il bottone se la nota non è attiva
                                                        variant="danger"
                                                        onClick={() => handleRemoveField('notes', index)} // Funzione per rimuovere la nota
                                                        className="ms-2"
                                                    >
                                                        Remove
                                                    </Button>
                                                </div>
                                            ) : null

                                            ))}
                                        </Form.Group>


                                        {newNotes.map((note, index) => (

                                            <div key={index} className="d-flex align-items-center mb-1">
                                                <Form.Control
                                                    type="text"
                                                    placeholder="Enter New Note"
                                                    value={note} // Usa il valore della nuova nota
                                                    onChange={(e) => handleNoteChange(index, e.target.value)} // Aggiorna il valore della nota specifica
                                                />
                                                <Button
                                                    variant="danger"
                                                    onClick={() => handleRemoveNote(index)} // Funzione per rimuovere la nuova nota
                                                    className="ms-2"
                                                >
                                                    Remove
                                                </Button>
                                            </div>

                                        ))}
                                        {formErrors.notes && (
                                            <div className="text-danger mb-3">
                                                {formErrors.notes}
                                            </div>
                                        )}
                                        <div className="mt-2">
                                            <Button variant="primary" className="mb-3" onClick={handleAddNote}>
                                                Add Note
                                            </Button>
                                        </div>


                                    </Col>

                                </Row>
                                {/*Files*/}
                                <Row>
                                    <Col>
                                        <Form.Group className="mb-3" controlId="files">
                                            <Form.Label>Attachments (optional)</Form.Label>
                                            <Form.Control
                                                type="file"
                                                name="files"
                                                multiple
                                                onChange={handleFiles}
                                            />
                                        </Form.Group>
                                    </Col>
                                    { fileError?
                                        <div className="text-danger mb-3">
                                            {fileError}
                                        </div>
                                        : <></>
                                    }
                                </Row>


                                {/* Submit Button */}
                                <Button type="submit" className="btn-primary w-100">Save Changes</Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default EditCustomer;
/*
IN EMAIL, TELEPHONE E ADDRESS NON SI SVUOTANO QUANDO FACCIO REMOVE E POI ADD. PERCHE?

 */
