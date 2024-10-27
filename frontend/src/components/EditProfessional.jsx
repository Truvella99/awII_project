import React, {useContext, useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import API from "../API.jsx";
import {Alert, Button, Card, Col, Container, Form, InputGroup, Row} from "react-bootstrap";
import InputMask from "react-input-mask";
import PhoneInput from "react-phone-number-input";
import {AddressSelector} from "./Utils.jsx";
import {MessageContext} from "../messageCtx.js";
import {Eye, EyeSlash} from "react-bootstrap-icons";

const EditProfessional = ({ xsrfToken }) => {
    const [professional, setProfessional] = useState({
        name:  '',
        surname:  '',
        ssncode:  '',
        email: null,
        telephone: null,
        address: null,
        emails: [],//emails already present
        telephones: [],//telephones already present
        addresses: [],//addresses already present
        category: '',
        password: '',
        employmentState: '',
        geographicalLocation: {first: "0", second: "0"},
        dailyRate: 1,
        notes: [],
        skills: [{skill: ''}],
        emailsToDelete: [], // Add new fields
        addressesToDelete: [], // Add new fields
        telephonesToDelete: [], // Add new fields
        notesToDelete: [], // Add new fields
        skillsToDelete: [] // Add new fields
    });

    const formatEmploymentState = (state) => {
        switch (state) {
            case 'employed':
                return 'Employed';
            case 'available':
                return 'Available';
            case 'not_available':
                return 'Not Available';
            default:
                return state;
        }
    };

    const {professionalId} = useParams();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formErrors, setFormErrors] = useState({});
    const [showNewEmailField, setShowNewEmailField] = useState(false);
    const [showNewTelephoneField, setShowNewTelephoneField] = useState(false);
    const [showNewNoteField, setShowNewNoteField] = useState(false);
    const [showNewSkillField, setShowNewSkillField] = useState(false);
    const [showNewAddressField, setShowNewAddressField] = useState(false);
    const [address, setAddress] = useState({text: '', lat: 0.0, lng: 0.0, invalid: false});
    const navigate = useNavigate()
    const [newNotes, setNewNotes] = useState([]);
    const [newSkills, setNewSkills] = useState([]);
    // Regex patterns for validation
    const NOT_EMPTY_IF_NOT_NULL = /^\s*\S.*$/;
    const SSN_CODE = /^(?!000|666|9\d\d)\d{3}-(?!00)\d{2}-(?!0000)\d{4}$/;
    const EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const TELEPHONE = /^(\+?\d{1,3}[-\s.]?)?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$/;
    const ADDRESS = /^[a-zA-Z0-9\s.,'-]+$/;
    const handleErrors = useContext(MessageContext);
    const [showPassword, setShowPassword] = useState(false); // Stato per gestire visibilità password
    const togglePasswordVisibility = () => {
        setShowPassword(prevState => !prevState);
    };
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
            setProfessional((prevProfessional) => ({
                ...prevProfessional,
                geographicalLocation: {
                    latitude: location.lat,
                    longitude: location.lng
                }
            }));
            setProfessional((prevProfessional) => ({
                ...prevProfessional,
                address: response.data.results[0].formatted_address
            }));
            console.log("Geographical location fetched", professional.geographicalLocation);
        } catch (error) {
            console.error("Error fetching geographical location", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (professionalId) {
            const fetchProfessional = async () => {
                try {
                    setLoading(true);
                    const fetchedProfessional = await API.getProfessionalById(professionalId, xsrfToken);
                    console.log("fetchedProf", fetchedProfessional);
                    setProfessional(fetchedProfessional);
                    setLoading(false);
                } catch (err) {
                    setError(err.error || 'Error fetching professional data');
                    setLoading(false);
                }
            };
            fetchProfessional();
        }
    }, [professionalId, xsrfToken]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setProfessional((prevProfessional) => ({
            ...prevProfessional,
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
// // Gestisci il cambiamento di una skill
//     const handleSkillChange = (index, value) => {
//         const updatedSkills = [...newSkills];
//         updatedSkills[index] = value;
//         setNewSkills(updatedSkills);
//     };
//
// // Aggiungi una nuova skill
//     const handleAddSkill = () => {
//         setNewSkills([...newSkills, '']);
//     };
//
// // Rimuovi una skill
//     const handleRemoveSkill = (index) => {
//         const updatedSkills = newSkills.filter((_, i) => i !== index);
//         setNewSkills(updatedSkills);
//     };
    // Funzioni per gestire le skills
    const handleSkillChange = (index, value) => {
        const updatedSkills =newSkills.map((skill, i) =>
            i === index ? {...skill, skill: value} : skill
        );
        setNewSkills(updatedSkills);
    };

    const handleAddSkill = () => {
        setNewSkills([...newSkills, { skill: '' }]); // Aggiunge una nuova skill vuota
    };


    const handleRemoveSkill = (index) => {
        const updatedSkills = newSkills.filter((_, i) => i !== index); // Rimuove la skill all'indice specificato
        setNewSkills(updatedSkills);
    };

    const handleRemoveField = (field, index) => {
        setProfessional((prevProfessional) => {
            const updatedField = [...prevProfessional[field]];
            const removedItem = updatedField[index];

            // Cambia lo stato in "deleted" se esiste un ID
            if (removedItem.id) {
                updatedField[index] = { ...removedItem, state: 'deleted' }; // Imposta lo stato su "deleted"
                // Aggiungi l'ID alla lista corretta per la cancellazione
                if (field === 'emails') {
                    return {
                        ...prevProfessional,
                        [field]: updatedField,
                        emailsToDelete: prevProfessional.emailsToDelete ? [...prevProfessional.emailsToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'telephones') {
                    return {
                        ...prevProfessional,
                        [field]: updatedField,
                        telephonesToDelete: prevProfessional.telephonesToDelete ? [...prevProfessional.telephonesToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'addresses') {
                    return {
                        ...prevProfessional,
                        [field]: updatedField,
                        addressesToDelete: prevProfessional.addressesToDelete ? [...prevProfessional.addressesToDelete, removedItem.id] : [removedItem.id],
                    };
                } else if (field === 'notes') {
                    return {
                        ...prevProfessional,
                        [field]: updatedField,
                        notesToDelete: prevProfessional.notesToDelete ? [...prevProfessional.notesToDelete, removedItem.id] : [removedItem.id],
                    };
                }else if (field === 'skills') {
                    return {
                        ...prevProfessional,
                        [field]: updatedField,
                        skillsToDelete: prevProfessional.skillsToDelete ? [...prevProfessional.skillsToDelete, removedItem.id] : [removedItem.id],
                    };
                }
            } else {
                // Rimuovi nuovi campi aggiunti (non salvati nel database)
                updatedField.splice(index, 1);
                return { ...prevProfessional, [field]: updatedField };
            }
        });
    };
    const validateForm = async () => {
        const errors = {};
        //Password
        if (professional.password.length < 8) {
            errors.password = "Passwprd must be at least 8 characters long";
        }else if (!/[A-Z]/.test(professional.password)) {
            errors.password = "Password must contain at least one uppercase letter.";
        }else if (!/[a-z]/.test(professional.password)) {
            errors.password = "Password must contain at least one lowercase letter.";
        }else if (!/[0-9]/.test(professional.password)) {
            errors.password = "Password must contain at least one digit.";
        }
        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.name)) {
            errors.name = "Name cannot be empty.";
        }

        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.surname)) {
            errors.surname = "Surname cannot be empty.";
        }

        if (!SSN_CODE.test(professional.ssncode)) {
            errors.ssncode = "SSN Code must be valid in the format XXX-XX-XXXX.";
        }


        if (professional.email && !EMAIL.test(professional.email)) {
            errors.email = "Please enter a valid email address.";
        }
        if(professional.emails.some(email =>email.state==="active" &&  email.email === professional.email?.trim()) ){
            errors.email = "Email already present";
        }

        if (professional.telephone && !TELEPHONE.test(professional.telephone)) {
            errors.telephone = "Please enter a valid telephone number.";
        }
        if(professional.telephones.some(telephone =>telephone.state==="active" && telephone.telephone === professional.telephone)){
            errors.telephone = "Telephone already present";
        }

        if (professional.address && !ADDRESS.test(professional.address)) {
            errors.address = "Please enter a valid address.";
        }
        if(professional.addresses.some(address => address.state==="active" && address.address === professional.address)){
            errors.address = "Address already present";
        }
        try {
            if (address.text) {
                await addressValidation(address, setAddress);
                await fetchGeographicalLocation();
                if (address.invalid === true) {
                    errors.address = "Please enter a valid address.";
                }
                professional.address = address.text;
            } else {
                professional.address = null;
            }
        } catch (error) {
            errors.address = "Please enter a valid address.";
        }
        if (newNotes.some(note => !note)) {
            errors.notes = 'Notes cannot be empty';
        }
        if ( parseInt(professional.dailyRate) < 0) {
            errors.dailyRate = "Daily rate cannot be negative.";
        }

        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.dailyRate)) {
            errors.dailyRate = "Daily rate is required.";
        }

        professional.dailyRate = parseFloat(professional.dailyRate);

        if(professional.skills.some(skill =>skill.state==="active" && newSkills.map(skill => skill.skill).includes(skill.skill))){
            errors.skills = "Skill already present";
        }
        const skillValues = newSkills.map(newSkill => newSkill.skill.toLowerCase().trim());
        const hasDuplicates = skillValues.some((skill, index) => skillValues.indexOf(skill) !== index);
        if (hasDuplicates) {
            errors.skills = "Duplicate skills are not allowed.";
        }
        if (newSkills.some(skill => !skill)) {
            errors.skills = 'Skills cannot be empty';
        }
        if (professional.skills.filter(it =>it.state ==="active").length === 0&& newSkills.length === 0 || newSkills.some(skill => !skill.skill) ) {
            errors.skills = 'At least one skill is required and skills cannot be empty';
        }
        // Aggiungi controllo per address, telephone, email: almeno uno è richiesto
        if (!professional.email && !professional.telephone && (!professional.address) && professional.emails.filter(it =>it.state ==="active").length === 0 && professional.telephones.filter(it =>it.state ==="active").length === 0 && professional.addresses.filter(it =>it.state ==="active").length === 0) {
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
        if (!isValid) return;

        setLoading(true);
        try {
            // Only update professional functionality
            professional.notes = newNotes
            professional.skills = newSkills

            await API.updateProfessional(professionalId, professional, xsrfToken);
            setLoading(false);
            navigate("/ui")

            // Optionally, navigate back or display a success message
        } catch (err) {
            setError(err.error || 'Error saving professional');
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
                            <h3>Edit Professional</h3>
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
                                                value={professional.name}
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
                                                value={professional.surname}
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
                                                value={professional.ssncode}
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
                                    {/*<Col md={6}>*/}
                                    {/*    <Form.Group className="mb-3" controlId="category">*/}
                                    {/*        <Form.Label>Category</Form.Label>*/}
                                    {/*        <Form.Control*/}
                                    {/*            type="text"*/}
                                    {/*            name="category"*/}
                                    {/*            value={professional.category.toUpperCase()}*/}
                                    {/*            disabled={true}*/}
                                    {/*            required*/}
                                    {/*        />*/}
                                    {/*    </Form.Group>*/}
                                    {/*</Col>*/}
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="psw">
                                            <Form.Label>Password</Form.Label>
                                            <InputGroup>
                                                <Form.Control
                                                    type={showPassword ? 'text' : 'password'} // Alterna testo/password
                                                    name="password"
                                                    placeholder="Enter password"
                                                    value={professional.password}
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
                                </Row>
                                {/* Employment State and Daily Rate*/}
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="employmentState">
                                            <Form.Label>Employment State</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="EmploymentState"
                                                value={professional.employmentState.toUpperCase()}
                                                disabled={true}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="dailyRate">
                                            <Form.Label>Daily Rate (€)</Form.Label>
                                            <Form.Control
                                                type="number"
                                                name="dailyRate"
                                                min={1}
                                                placeholder="Enter daily rate"
                                                value={professional.dailyRate}
                                                onChange={handleInputChange}
                                                onKeyDown={(e) => {
                                                    if (e.key === '-') {
                                                        e.preventDefault(); // Previene l'inserimento del simbolo meno
                                                    }
                                                }}
                                                isInvalid={!!formErrors.dailyRate}
                                                required
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {formErrors.dailyRate}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>
                                {/* Emails */}

                                <Row>
                                    <Col lg={6}>
                                        <Form.Group>
                                            <Form.Label>Emails</Form.Label>
                                            <div className="text-secondary mb-3" hidden={professional.emails.some(email => email.state === "active") || showNewEmailField}>
                                                Professional has no emails
                                            </div>
                                            {professional.emails.map((email, index) => (
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
                                                    setProfessional((prev) => ({
                                                        ...prev,
                                                        email: '', // Reset the email field in your professional state
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
                                                    value={professional.email}
                                                    onChange={handleInputChange}
                                                    isInvalid={!!formErrors.email}
                                                />
                                                {/* Bottone "Remove" per la nuova email, visibile solo se showNewEmailField è true */}
                                                <Button
                                                    hidden={!showNewEmailField} // Nasconde il bottone se showNewEmailField è false
                                                    variant="danger"
                                                    onClick={() => {
                                                        setShowNewEmailField(false); // Nasconde il campo della nuova email
                                                        setProfessional((prev) => ({
                                                            ...prev,
                                                            email: null, // Svuota il campo professional.email
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
                                            <div className="text-secondary mb-3" hidden={professional.telephones.some(telephone => telephone.state === "active") || showNewTelephoneField}>
                                                Professional has no telephones number
                                            </div>
                                            {professional.telephones.map((telephone, index) => (
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
                                                    setProfessional((prev) => ({
                                                        ...prev,
                                                        telephone: '', // Reset the telephone field in your professional state
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
                                                    value={professional.telephone}
                                                    onChange={(value) =>
                                                        setProfessional((prev) => ({
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
                                                        setProfessional((prev) => ({
                                                            ...prev,
                                                            telephone: null, // Svuota il campo professional.telephone
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

                                            <div className="text-secondary mb-3" hidden={professional.addresses.some(address => address.state === "active") || showNewAddressField}>
                                                Professional has no addresses
                                            </div>
                                            {professional.addresses.map((address, index) => (
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
                                                    setAddress({text: '', lat: 0.0, lng: 0.0, invalid: false}); // Reset the address field in your professional state
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
                                                        setProfessional((prev) => ({
                                                            ...prev,
                                                            address: null, // Svuota il campo professional.address
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

                                    {/* Skills */}
                                    <Col md={6}>
                                        <Form.Group>
                                            <Form.Label className="mb-3">Skills</Form.Label>

                                            <div className="text-secondary mb-3" hidden={professional.skills.some(skill => skill.state === "active") || showNewSkillField}>
                                                Professional has no Skills
                                            </div>
                                            {professional.skills.map((skill, index) => (
                                                skill.state === "active" ? (
                                                    <div key={index} className="d-flex align-items-center mb-1">
                                                        <Form.Control
                                                            hidden={skill.state !== "active"} // Nasconde il campo se non attivo
                                                            disabled={true}
                                                            type="text"
                                                            placeholder="Add Skill"
                                                            value={skill.skill}
                                                        />
                                                        <Button
                                                            hidden={skill.state !== "active"} // Nasconde il bottone se la nota non è attiva
                                                            variant="danger"
                                                            onClick={() => handleRemoveField('skills', index)} // Funzione per rimuovere la nota
                                                            className="ms-2"
                                                        >
                                                            Remove
                                                        </Button>
                                                    </div>
                                                ) : null

                                            ))}
                                        </Form.Group>


                                        {/* Display new skills */}
                                        {newSkills.map((skill, index) => (

                                            <div key={index} className="d-flex align-items-center mb-1">
                                                <Form.Control
                                                    type="text"
                                                    placeholder="Enter New Skill"
                                                    value={skill.skill} // Usa il valore della nuova nota
                                                    onChange={(e) => handleSkillChange(index, e.target.value)} // Aggiorna il valore della nota specifica
                                                />
                                                <Button
                                                    variant="danger"
                                                    onClick={() => handleRemoveSkill(index)} // Funzione per rimuovere la nuova nota
                                                    className="ms-2"
                                                >
                                                    Remove
                                                </Button>
                                            </div>

                                        ))}
                                        {formErrors.skills && (
                                            <div className="text-danger mb-3">
                                                {formErrors.skills}
                                            </div>
                                        )}
                                        <div className="mt-2">
                                            <Button variant="primary" className="mb-3" onClick={handleAddSkill}>
                                                Add Skill
                                            </Button>
                                        </div>

                                    </Col>
                                </Row>
                                <Row>

                                    {/* Notes */}
                                    <Col>
                                        <Form.Group controlId="notes">
                                            <Form.Label>Notes</Form.Label>
                                            <div className="text-secondary mb-3" hidden={professional.notes.some(note => note.state === "active") || showNewNoteField}>
                                                Professional has no Notes
                                            </div>
                                            {professional.notes.map((note, index) => (
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

export default EditProfessional;
