import React, {useContext, useEffect, useState} from 'react';
import {Container, Row, Col, Card, Button, Form, Alert, InputGroup} from 'react-bootstrap';
import API from '../API'; // API per gestire il salvataggio e il recupero dei dati
import 'react-phone-number-input/style.css';
import PhoneInput from 'react-phone-number-input';
import {DropdownButton, Dropdown} from "react-bootstrap";
import {AddressSelector} from "./Utils.jsx";
import axios from "axios";
import {useLocation, useNavigate} from "react-router-dom";
import InputMask from "react-input-mask";
import {MessageContext} from "../messageCtx.js";
import { Eye, EyeSlash } from 'react-bootstrap-icons';

const AddProfessional = ({xsrfToken}) => {
    const [professional, setProfessional] = useState({
        name: '',
        surname: '',
        username: '',
        ssncode: '',
        category: 'professional',
        password: '',
        employmentState: 'available',
        geographicalLocation: {first: "0", second: "0"},
        dailyRate: 1,
        email: "",
        telephone: '',
        address: '',
        skills: [{skill: ''}],
        notes: []
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
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formErrors, setFormErrors] = useState({});
    const [address, setAddress] = useState({text: '', lat: 0.0, lng: 0.0, invalid: false});
    const navigate = useNavigate();
    const location = useLocation();
    // Pending Id and contact
    const pendingId = location.state?.id;
    const pendingContact = location.state?.contact;
    const [files, setFiles] = useState([]);
    const [fileError, setFileError] = useState(null);
    // Regex patterns for validation
    const NOT_EMPTY_IF_NOT_NULL = /^\s*\S.*$/;
    const SSN_CODE = /^(?!000|666|9\d\d)\d{3}-(?!00)\d{2}-(?!0000)\d{4}$/;
    const EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const TELEPHONE = /^(\+?\d{1,3}[-\s.]?)?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$/;
    const ADDRESS = /^[a-zA-Z0-9\s.,'-]+$/;
    const handleErrors = useContext(MessageContext);
    const [showPassword, setShowPassword] = useState(false); // Stato per gestire visibilità password
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

    }
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

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setProfessional((prevProfessional) => ({
            ...prevProfessional,
            [name]: value,
        }));
    };
    // Funzione per gestire la selezione del Dropdown
    const handleEmploymentStateChange = (selectedState) => {
        setProfessional((prevState) => ({
            ...prevState,
            employmentState: selectedState
        }));
    };
    const handleMultiInputChange = (e, index, field, key) => {
        const {value} = e.target;
        setProfessional((prevProfessional) => {
            const updatedField = [...prevProfessional[field]];
            updatedField[index] = {...updatedField[index], [key]: value};
            return {
                ...prevProfessional,
                [field]: updatedField,
            };
        });
    };
// Funzioni per gestire le note
    const handleNoteChange = (index, value) => {
        const updatedNotes = [...professional.notes];
        updatedNotes[index] = value; // Imposta direttamente la stringa
        setProfessional({...professional, notes: updatedNotes});
    };

    const handleAddNote = () => {
        setProfessional({...professional, notes: [...professional.notes, '']}); // Aggiungi una nuova stringa vuota
    };

    const handleRemoveNote = (index) => {
        const updatedNotes = professional.notes.filter((_, i) => i !== index);
        setProfessional({...professional, notes: updatedNotes});
    };

    // Funzioni per gestire le skills
    const handleSkillChange = (index, value) => {
        const updatedSkills = professional.skills.map((skill, i) =>
            i === index ? {...skill, skill: value} : skill
        );
        setProfessional({...professional, skills: updatedSkills});
    };

    const handleAddSkill = () => {
        setProfessional({...professional, skills: [...professional.skills, {skill: ''}]});
    };

    const handleRemoveSkill = (index) => {
        const updatedSkills = professional.skills.filter((_, i) => i !== index);
        setProfessional({...professional, skills: updatedSkills});
    };

    const validateForm = async () => {
        const errors = {};
        //Password
        if (professional.password.length < 8) {
            errors.password = "Password must be at least 8 characters long";
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
        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.username)) {
            errors.username = "Username cannot be empty.";
        }
        if (!SSN_CODE.test(professional.ssncode)) {
            errors.ssncode = "SSN Code must be valid in the format XXX-XX-XXXX.";
        }

        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.employmentState)) {
            errors.employmentState = "Employment state is required.";
        }
        if ( parseInt(professional.dailyRate) < 0) {
            errors.dailyRate = "Daily rate cannot be negative.";
        }

        if (!NOT_EMPTY_IF_NOT_NULL.test(professional.dailyRate)) {
            errors.dailyRate = "Daily rate is required.";
        }

        professional.dailyRate = parseFloat(professional.dailyRate);

        if (professional.email && !EMAIL.test(professional.email)) {
            errors.email = "Please enter a valid email address.";
        }

        if (professional.telephone && !TELEPHONE.test(professional.telephone)) {
            errors.telephone = "Please enter a valid telephone number.";
        }
        if (professional.telephone == "") {
            //altrimenti non funziona createProfessional
            professional.telephone = null;
        }
        if (professional.email == "") {
            //altrimenti non funziona createProfessional
            professional.email = null;
        }
        if (professional.address && !ADDRESS.test(professional.address)) {
            errors.address = "Please enter a valid address.";
        }

        try {
            if (address.text) {
                // Wait for the promise to settle before moving to the next line
                await addressValidation(address, setAddress);
                await fetchGeographicalLocation();
                if (address.invalid === true) {
                    errors.address = "Please enter a valid address.";
                }
                professional.address = address.text;
                professional.geographicalLocation = {first: address.lat.toString(), second: address.lng.toString()};
            } else {
                professional.address = null;
            }
        } catch (error) {
            errors.address = "Please enter a valid address.";
        }
        if (!professional.skills.length || professional.skills.some(skill => !skill.skill)) {
            errors.skills = 'At least one skill is required and it cannot be empty';
        }
        if (professional.notes.some(note => !note)) {
            errors.notes = 'Notes cannot be empty';
        }
        // Aggiungi controllo per address, telephone, email: almeno uno è richiesto
        if (!professional.email && !professional.telephone && !professional.address) {
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

            // Chiamata API per creare un nuovo Professional
            const profCreated = await API.createProfessional(professional, xsrfToken);

            if (files.length > 0) {
                // Chiamata API per caricare i file
                console.log("files", files)
                for (const file of files) {
                    console.log(file)
                    await API.postDocument(profCreated.id,file, xsrfToken);
                }
            }

            setLoading(false);
            navigate("/ui")
        } catch (err) {
            setError(err.detail || 'Error saving professional');
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
                            <h3>Add Professional</h3>
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
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="username">
                                            <Form.Label>Username</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="username"
                                                placeholder="Enter username"
                                                value={professional.username}
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
                                    {/*            value={professional.category}*/}
                                    {/*            disabled={true}*/}
                                    {/*            required*/}
                                    {/*        />*/}
                                    {/*    </Form.Group>*/}
                                    {/*</Col>*/}
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="employmentState">
                                            <Form.Label>Employment State</Form.Label>
                                            <DropdownButton
                                                id="employmentStateDropdown"
                                                title={formatEmploymentState(professional.employmentState || 'available')}
                                                onSelect={handleEmploymentStateChange} // Funzione per gestire la selezione
                                            >
                                                <Dropdown.Item eventKey="employed">Employed</Dropdown.Item>
                                                <Dropdown.Item eventKey="available">Available</Dropdown.Item>
                                                <Dropdown.Item eventKey="not_available">Not Available</Dropdown.Item>
                                            </DropdownButton>

                                            {formErrors.employmentState && (
                                                <Form.Control.Feedback type="invalid">
                                                    {formErrors.employmentState}
                                                </Form.Control.Feedback>
                                            )}
                                        </Form.Group>
                                    </Col>

                                {/* Daily Rate */}

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
                                <Row>
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
                                {/* Email */}
                                    <Col>
                                        <Form.Group className="mb-3" controlId="email">
                                            <Form.Label>Email</Form.Label>
                                            <Form.Control
                                                type="email"
                                                name="email"
                                                placeholder="Enter Email"
                                                value={professional.email}
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
                                    <Col>
                                        <Form.Group className="mb-3" controlId="telephone">
                                            <Form.Label>Telephone</Form.Label>
                                            <PhoneInput
                                                className={professional.telephone ? '' : 'is-invalid'}
                                                name="telephone"
                                                value={professional.telephone}
                                                isInvalid={!!formErrors.telephone}
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
                                    <Col>
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
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="mb-3">Notes (optional)</Form.Label>
                                                {professional.notes.map((note, index) => (
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
                                                    <Button variant="warning" onClick={handleAddNote}>
                                                        Add Note
                                                    </Button>
                                                </div>
                                            </Form.Group>
                                        </Col>

                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="mb-3">Skills </Form.Label>
                                                {professional.skills.map((skill, index) => (
                                                    <InputGroup key={index} className="mb-3">
                                                        <Form.Control
                                                            type="text"
                                                            value={skill.skill}
                                                            placeholder="Enter a skill"
                                                            isInvalid={!!formErrors.skills}
                                                            onChange={(e) => handleSkillChange(index, e.target.value)}
                                                        />
                                                        <Button variant="danger" hidden={index === 0}
                                                                onClick={() => handleRemoveSkill(index)}>
                                                            Remove
                                                        </Button>
                                                        <Form.Control.Feedback type="invalid">
                                                            {formErrors.skills}
                                                        </Form.Control.Feedback>
                                                    </InputGroup>
                                                ))}
                                                <Button variant="warning" className="mb-3" onClick={handleAddSkill}>
                                                    Add Skill
                                                </Button>
                                            </Form.Group>
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

export default AddProfessional;
