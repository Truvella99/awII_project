import React, {useContext, useState} from 'react';
import {Form, Button, InputGroup, Col, Alert, Container, Row, Card, DropdownButton, Dropdown} from 'react-bootstrap';
import API from "../API.jsx";
import {useLocation, useNavigate} from "react-router-dom";
import axios from "axios";
import PhoneInput from "react-phone-number-input";
import {AddressSelector} from "./Utils.jsx";
import InputMask from 'react-input-mask';
import {MessageContext} from "../messageCtx.js";
import { Eye, EyeSlash } from 'react-bootstrap-icons'; // Impor

const CreateCustomer = ({xsrfToken}) => {
    const [customer, setCustomer] = useState({
        name: '',
        surname: '',
        username: '',
        ssncode: '',
        category: 'customer',
        email: '',
        telephone: '',
        address: '',
        password: '',
        notes: [],
    });

    const [formErrors, setFormErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [address, setAddress] = useState({text: '', lat: 0.0, lng: 0.0, invalid: false});
    const navigate = useNavigate();
    const location = useLocation();
    // Pending Id and contact
    const pendingId = location.state?.id;
    const pendingContact = location.state?.contact;
    const [error, setError] = useState(null);
    const handleErrors = useContext(MessageContext);
    const [showPassword, setShowPassword] = useState(false); // Stato per gestire visibilità password
    const [files, setFiles] = useState([]);
    const [fileError, setFileError] = useState(null);
    // Regex patterns for validation
    const NOT_EMPTY_IF_NOT_NULL = /^\s*\S.*$/;
    const SSN_CODE = /^(?!000|666|9\d\d)\d{3}-(?!00)\d{2}-(?!0000)\d{4}$/;
    const EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const TELEPHONE = /^(\+?\d{1,3}[-\s.]?)?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$/;
    const ADDRESS = /^[a-zA-Z0-9\s.,'-]+$/;
    // Funzione per alternare la visibilità della password
    const togglePasswordVisibility = () => {
        setShowPassword(prevState => !prevState);
    };
    const handleFiles = (ev) => {
        setFiles([]); // Reset file list (to avoid duplicates

        const filesArray = [...ev.target.files];
        setFileError(null);
        // Check if the file is too large
        const tooLargeFiles = filesArray.filter(file => file.size > 50000000);
        if (tooLargeFiles.length > 0) {
            setFileError(`File ${tooLargeFiles[0].name} is too large (maximum size is 50MB).`);
        }
        setFiles(filesArray);



        // filesArray.forEach((file, index) => {
        //     console.log(file);
        //     // Read and save file content
        //     const reader = new FileReader();
        //     reader.onload = (ev) => {
        //         if (file.size > 50000000) {
        //             setFileError(`File ${file.name} is too large (maximum size is 50MB).`);
        //         } else {
        //             setFiles((prev) => [
        //                 ...prev,
        //                 {
        //                     name: file.name,
        //                     type: file.type,
        //                     content: file
        //                 }
        //             ]);
        //         }
        //     };
        //     reader.onerror = (ev) => {
        //         console.error(`Error reading ${file.name}:`, ev);
        //         setFileError(`Error reading ${file.name}: ${ev}`);
        //     };
        //     reader.readAsDataURL(file);
        // });
    }
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

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCustomer((prev) => ({ ...prev, [name]: value }));
    };

// Funzioni per gestire le note
    const handleNoteChange = (index, value) => {
        const updatedNotes = [...customer.notes];
        updatedNotes[index] = value; // Imposta direttamente la stringa
        setCustomer({...customer, notes: updatedNotes});
    };

    const handleAddNote = () => {
        setCustomer({...customer, notes: [...customer.notes, '']}); // Aggiungi una nuova stringa vuota
    };

    const handleRemoveNote = (index) => {
        const updatedNotes = customer.notes.filter((_, i) => i !== index);
        setCustomer({...customer, notes: updatedNotes});
    };


    const validateForm = async () => {
        const errors = {};

        //Password
        if (customer.password.length < 8) {
            errors.password = "Password must be at least 8 characters long";
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
        if (!NOT_EMPTY_IF_NOT_NULL.test(customer.username)) {
            errors.username = "Username cannot be empty.";
        }

        if (!SSN_CODE.test(customer.ssncode)) {
            errors.ssncode = "SSN Code must be valid in the format XXX-XX-XXXX.";
        }


        if (customer.email && !EMAIL.test(customer.email)) {
            errors.email = "Please enter a valid email address.";
        }

        if (customer.telephone && !TELEPHONE.test(customer.telephone)) {
            errors.telephone = "Please enter a valid telephone number.";
        }
        if (customer.telephone == "") {
            customer.telephone = null;
        }
        if (customer.email == "") {
            customer.email = null;
        }
        if (customer.address && !ADDRESS.test(customer.address)) {
            errors.address = "Please enter a valid address.";
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
        if (customer.notes.some(note => !note)) {
            errors.notes = 'Notes cannot be empty';
        }
        // Aggiungi controllo per address, telephone, email: almeno uno è richiesto
        if (!customer.email && !customer.telephone && !customer.address) {
            errors.contact = 'Please provide at least one valid contact method: email, telephone, or address.';
        }
        setFormErrors(errors);
        console.log(errors);
        return Object.keys(errors).length === 0;
    };


    const handleSubmit = async (e) => {
        e.preventDefault();
        e.stopPropagation();


        if (fileError)
            return;

        const isValid = await validateForm().then((res) => res).catch((err) => false);
        console.log(isValid);
        if (!isValid) return;

        setLoading(true);
        try {
            const customerCreated = await API.createCustomer(customer, xsrfToken);

            // Chiamata API per creare un nuovo Document
            if (files.length > 0) {
                // Chiamata API per caricare i file
                console.log("files", files)
                for (const file of files) {
                    console.log(file)
                    await API.postDocument(customerCreated.id,file, xsrfToken);
                }
            }

            setLoading(false);
            navigate("/ui")
        } catch (err) {
            setError(err.detail || 'Error saving customer');
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
                            <h3>Add Customer</h3>
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
                                        <Form.Group className="mb-3" controlId="username">
                                            <Form.Label>Username</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="username"
                                                placeholder="Enter username"
                                                value={customer.username}
                                                onChange={handleInputChange}
                                                isInvalid={!!formErrors.username}
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.username}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    {/*<Col md={6}>*/}
                                    {/*    <Form.Group className="mb-3" controlId="category">*/}
                                    {/*        <Form.Label>Category</Form.Label>*/}
                                    {/*        <Form.Control*/}
                                    {/*            type="text"*/}
                                    {/*            name="category"*/}
                                    {/*            value={customer.category}*/}
                                    {/*            disabled={true}*/}
                                    {/*            required*/}
                                    {/*        />*/}
                                    {/*    </Form.Group>*/}
                                    {/*</Col>*/}
                                </Row>
                                    <Row>
                                        <Col md={6}>
                                            <Form.Group className="mb-3" controlId="psw">
                                                <Form.Label>Password</Form.Label>
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
                                {/* Email */}
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="email">
                                            <Form.Label>Email</Form.Label>
                                            <Form.Control
                                                type="email"
                                                name="email"
                                                placeholder="Enter Email"
                                                value={customer.email}
                                                onChange={handleInputChange}
                                                isInvalid={!!formErrors.email}
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.email}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>
                                {/* Telephone */}
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="telephone">
                                            <Form.Label >Telephone</Form.Label>
                                            <PhoneInput
                                                className={customer.telephone ? '' : 'is-invalid'}
                                                name="telephone"
                                                value={customer.telephone}
                                                defaultCountry='IT'
                                                placeholder="Enter telephone"
                                                onChange={(value) =>
                                                    handleInputChange({
                                                        target: {name: 'telephone', value},
                                                    })
                                                }
                                            />
                                            {formErrors.telephone && (
                                                <div className="text-danger">
                                                    {formErrors.telephone}
                                                </div>
                                            )}
                                        </Form.Group>
                                    </Col>
                                {/* Address */}
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="address">
                                            <Form.Label>Address</Form.Label>
                                            <AddressSelector address={address} setAddress={setAddress}/>

                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.address}
                                            </Form.Control.Feedback>
                                            {formErrors.contact && (
                                                <div className="text-danger mb-3">
                                                    {formErrors.contact}
                                                </div>
                                            )}
                                        </Form.Group>

                                    </Col>
                                    {/* Notes */}
                                    <Row>
                                        <Col >
                                            <Form.Group>
                                                <Form.Label className="mb-3">Notes (optional)</Form.Label>
                                                {customer.notes.map((note, index) => (
                                                    <InputGroup key={index} className="mb-3">
                                                        <Form.Control
                                                            type="text"
                                                            value={note} // Usa la stringa direttamente
                                                            placeholder="Enter a note"
                                                            onChange={(e) => handleNoteChange(index, e.target.value)} // Passa direttamente la stringa
                                                            isInvalid={!!formErrors.notes}
                                                        />
                                                        <Button variant="danger"
                                                                onClick={() => handleRemoveNote(index)}>
                                                            Remove
                                                        </Button>
                                                        <Form.Control.Feedback type="invalid">
                                                            {formErrors.notes}
                                                        </Form.Control.Feedback>
                                                    </InputGroup>
                                                ))}
                                                <div className="mt-2">
                                                    <Button variant="warning" className="mb-3" onClick={handleAddNote}>
                                                        Add Note
                                                    </Button>
                                                </div>
                                            </Form.Group>
                                        </Col>

                                    </Row>
                                    {/*Files*/}
                                    <Row>
                                        <Col>
                                            <Form.Group className="mb-3" controlId="files">
                                                <Form.Label>Attachments (Optional)</Form.Label>
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

                                </Row>

                                <Button variant="primary" type="submit">
                                    Save
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default CreateCustomer;
