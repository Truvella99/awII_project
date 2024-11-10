import React, {useContext, useState} from 'react';
import {
    Form,
    Button,
    InputGroup,
    Col,
    Alert,
    Container,
    Row,
    Card,
    DropdownButton,
    Dropdown,
    Spinner
} from 'react-bootstrap';
import API from "../API.jsx";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import PhoneInput from "react-phone-number-input";
import {AddressSelector, SideBar} from "./Utils.jsx";
import {MessageContext, TokenContext} from "../messageCtx.js";
import Select from "react-select";


function MessageForm({role, unreadMessages, setUnreadMessages, pending, setPending}) {
    const [message, setMessage] = useState({
        channel: 'email',
        priority: 'low',
        subject: '',
        body: '',
        email: '',
        telephone: '',
        address: ''
    });
    const [formErrors, setFormErrors] = useState({});
    const [address, setAddress] = useState({text: '', lat: 0.0, lng: 0.0, invalid: false});
    const [files, setFiles] = useState([]);
    const [fileError, setFileError] = useState(null);
    const [showFileError, setShowFileError] = useState(false);
    const [selectedChannel, setSelectedChannel] = useState({value: message.channel, label: message.channel.charAt(0).toUpperCase() + message.channel.slice(1)});
    const [selectedPriority, setSelectedPriority] = useState({value: message.priority, label: message.priority.charAt(0).toUpperCase() + message.priority.slice(1)});
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    // Regex patterns for validation
    const NOT_EMPTY_IF_NOT_NULL = /^\s*\S.*$/;
    const EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const TELEPHONE = /^(\+?\d{1,3}[-\s.]?)?\(?\d{3}\)?[-\s.]?\d{3}[-\s.]?\d{4}$/;
    const ADDRESS = /^[a-zA-Z0-9\s.,'-]+$/;
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

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
    };

    const fetchGeographicalLocation = async () => {
        try {
            const API_KEY = 'AIzaSyCO5hFwnkcQjDkoivao8qpJbKvITf_vb1g';  // Inserisci la tua chiave API di Google Maps
            const response = await axios.get(
                `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address.text)}&key=${API_KEY}`
            );
            const location = response.data.results[0].geometry.location;

            setMessage((prev) => ({
                ...prev,
                address: response.data.results[0].formatted_address
            }));
        } catch (error) {
            console.error("Error fetching geographical location", error);
        }
    };

    const handleInputChange = (ev) => {
        const { name, value } = ev.target;
        setMessage((prev) => ({ ...prev, [name]: value }));
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

    const resetInputChannel = () => {
        setMessage((prev) => ({
            ...prev,
            email: '',
            telephone: '',
            address: ''
        }));
    };


    const validateForm = async () => {
        const errors = {};

        if (message.email && !EMAIL.test(message.email)) {
            errors.email = "Please enter a valid email address.";
        }
        if (message.telephone && !TELEPHONE.test(message.telephone)) {
            errors.telephone = "Please enter a valid telephone number.";
        }
        if (message.telephone === "") {
            message.telephone = null;
        }
        if (message.email === "") {
            message.email = null;
        }
        if (message.address && !ADDRESS.test(message.address)) {
            errors.address = "Please enter a valid address.";
        }

        try {
            if (address.text) {
                await addressValidation(address, setAddress);
                await fetchGeographicalLocation();
                if (address.invalid === true) {
                    errors.address = "Please enter a valid address.";
                }
                message.address = address.text;
            } else {
                message.address = null;
            }
        } catch (error) {
            errors.address = "Please enter a valid address.";
        }
        // Controllo per address, telephone, email: almeno uno è richiesto
        if (!message.email && !message.telephone && !message.address) {
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

        let finalMessage = '';
        let bodyFlag = false;

        const isValid = await validateForm().then((res) => res).catch((err) => false);
        if (!isValid)
            return;
        else if (message.body || files.length > 0) {
            let mimeMessage = `MIME-Version: 1.0\r\n`;
            bodyFlag = true;
            mimeMessage += `From: ${message.email || message.telephone || message.address}\r\n`;
            mimeMessage += `To: aw2g52024@gmail.com\r\n`;
            mimeMessage += `Subject: ${message.subject || 'No subject'}\r\n`;
            if (files.length === 0) {
                // Body only
                const boundaryAlt = Math.random().toString(36).slice(2);
                mimeMessage += `Content-Type: multipart/alternative; boundary="${boundaryAlt}"\r\n\r\n`;
                const html = message.body.replace(/\n/g, '<br>');
                mimeMessage += `--${boundaryAlt}\r\n`;
                mimeMessage += `Content-Type: text/plain; charset="UTF-8"\r\n\r\n`;
                mimeMessage += `${message.body}\r\n\r\n`;
                mimeMessage += `--${boundaryAlt}\r\n`;
                mimeMessage += `Content-Type: text/html; charset="UTF-8"\r\n\r\n`;
                mimeMessage += `<div>${html}</div>\r\n\r\n`;
                mimeMessage += `--${boundaryAlt}--`;
            } else if (!message.body) {
                // Attachments only
                if (files.length === 1) {
                    const attachment = files[0];
                    mimeMessage += `Content-Type: ${attachment.type}; name="${attachment.name}"\r\n`;
                    mimeMessage += `Content-Disposition: attachment; filename="${attachment.name}"\r\n`;
                    mimeMessage += `Content-Transfer-Encoding: base64\r\n\r\n`;
                    mimeMessage += `${attachment.content}\r\n`;
                } else {
                    const boundaryMixed = Math.random().toString(36).slice(2);
                    mimeMessage += `Content-Type: multipart/mixed; boundary="${boundaryMixed}"\r\n\r\n`;
                    files.forEach((attachment) => {
                        mimeMessage += `--${boundaryMixed}\r\n`;
                        mimeMessage += `Content-Type: ${attachment.type}; name="${attachment.name}"\r\n`;
                        mimeMessage += `Content-Disposition: attachment; filename="${attachment.name}"\r\n`;
                        mimeMessage += `Content-Transfer-Encoding: base64\r\n\r\n`;
                        mimeMessage += `${attachment.content}\r\n`;
                    });
                    mimeMessage += `--${boundaryMixed}--`;
                }
            } else {
                const boundaryAlt = Math.random().toString(36).slice(2);
                const boundaryMixed = Math.random().toString(36).slice(2);
                const html = message.body.replace(/\n/g, '<br>');
                mimeMessage += `Content-Type: multipart/mixed; boundary="${boundaryMixed}"\r\n\r\n`;
                mimeMessage += `--${boundaryMixed}\r\n`;
                mimeMessage += `Content-Type: multipart/alternative; boundary="${boundaryAlt}"\r\n\r\n`;
                mimeMessage += `--${boundaryAlt}\r\n`;
                mimeMessage += `Content-Type: text/plain; charset="UTF-8"\r\n\r\n`;
                mimeMessage += `${message.body}\r\n\r\n`;
                mimeMessage += `--${boundaryAlt}\r\n`;
                mimeMessage += `Content-Type: text/html; charset="UTF-8"\r\n\r\n`;
                mimeMessage += `<div>${html}</div>\r\n\r\n`;
                mimeMessage += `--${boundaryAlt}--\r\n`;
                files.forEach((attachment) => {
                    mimeMessage += `--${boundaryMixed}\r\n`;
                    mimeMessage += `Content-Type: ${attachment.type}; name="${attachment.name}"\r\n`;
                    mimeMessage += `Content-Disposition: attachment; filename="${attachment.name}"\r\n`;
                    mimeMessage += `Content-Transfer-Encoding: base64\r\n\r\n`;
                    mimeMessage += `${attachment.content}\r\n`;
                });
                mimeMessage += `--${boundaryMixed}--`;
            }
            finalMessage = btoa(mimeMessage).replace(/\+/g, '-').replace(/\//g, '_');
        }
        setLoading(true);

        try {
            // API call
            await API.createMessage(message, finalMessage, bodyFlag, xsrfToken);
            // Update pendings
            let newPending;
            if (message.email) {
                newPending = await API.updatePendingContacts(message.email, null, null, xsrfToken);
            } else if (message.telephone) {
                newPending = await API.updatePendingContacts(null, message.telephone, null, xsrfToken);
            } else if (message.address) {
                newPending = await API.updatePendingContacts(null, null, message.address, xsrfToken);
            }
            if (newPending) {
                setPending(pending + 1);
            }
            setUnreadMessages(unreadMessages + 1);
            navigate("/ui/messages");
        } catch (err) {
            setError(err.detail || 'Error saving message.');
        }
    };
    // If error, show an error message
    if (error) {
        handleError(error)
        setError(null);
    }

    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '80vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                </Col>
                <Col className="mx-auto">
                    <Card className="shadow-lg">
                        <Card.Header className="bg-primary text-white d-flex justify-content-center">
                            <h3>Add Message</h3>
                        </Card.Header>
                        <Card.Body>
                            <Form noValidate onSubmit={handleSubmit}>

                                {/*Channel*/}
                                <Row>
                                    <Col xs={"auto"}>
                                        <Form.Group className="mb-3" controlId="channel">
                                            <Form.Label>Channel</Form.Label>
                                            <Select
                                                options={[
                                                    {value: 'email', label: 'Email'},
                                                    {value: 'phonecall', label: 'Telephone'},
                                                    {value: 'textmessage', label: 'Address'}
                                                ]}
                                                value={selectedChannel}
                                                onChange={ev => {
                                                    setSelectedChannel(ev);
                                                    const value = ev.value;
                                                    handleInputChange({
                                                        target: {name: 'channel', value},
                                                    })
                                                    resetInputChannel();
                                                }}
                                                closeMenuOnSelect={true}
                                                isSearchable={true}
                                                isClearable={false}
                                                styles={{
                                                    container: base => ({
                                                        ...base,
                                                        width: '150px'
                                                    })
                                                }}
                                                // theme={(theme) => ({
                                                //     ...theme,
                                                //     colors: {
                                                //         ...theme.colors,
                                                //         primary25: '#D1E7DD',
                                                //         primary: '#34ce57',
                                                //     },
                                                // })}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col>
                                        { selectedChannel.value === 'email' ? (
                                            <Form.Group className="mb-3" controlId="email">
                                                <Form.Label>Email</Form.Label>
                                                <Form.Control
                                                    type="email"
                                                    name="email"
                                                    placeholder="Enter email..."
                                                    value={message.email}
                                                    onChange={handleInputChange}
                                                    isInvalid={!!formErrors.email}
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    {formErrors.email}
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        ) : selectedChannel.value === 'phonecall' ? (
                                            <Form.Group className="mb-3" controlId="telephone">
                                                <Form.Label>Telephone</Form.Label>
                                                <PhoneInput
                                                    className={message.telephone ? '' : 'is-invalid'}
                                                    name="telephone"
                                                    value={message.telephone}
                                                    defaultCountry='IT'
                                                    placeholder="Enter phone number..."
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
                                        ) : (
                                            <Form.Group className="mb-3" controlId="address">
                                                <Form.Label>Address</Form.Label>
                                                <AddressSelector address={address} setAddress={setAddress}/>
                                                <Form.Control.Feedback type="invalid">
                                                    {formErrors.address}
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        )}
                                    </Col>
                                </Row>

                                {/*Priority and Subject*/}
                                <Row>
                                    <Col xs={"auto"}>
                                        <Form.Group className="mb-3" controlId="priority">
                                            <Form.Label>Priority</Form.Label>
                                            <Select
                                                options={[
                                                    { value: 'low', label: 'Low' },
                                                    { value: 'medium', label: 'Medium' },
                                                    { value: 'high', label: 'High' }
                                                ]}
                                                value={selectedPriority}
                                                onChange={ev => {
                                                    setSelectedPriority(ev);
                                                    const value = ev.value;
                                                    handleInputChange({
                                                        target: {name: 'priority', value},
                                                    })
                                                }}
                                                closeMenuOnSelect={true}
                                                isSearchable={true}
                                                isClearable={false}
                                                styles={{
                                                    container: base => ({
                                                        ...base,
                                                        width: '150px'
                                                    })
                                                }}
                                                // theme={(theme) => ({
                                                //     ...theme,
                                                //     colors: {
                                                //         ...theme.colors,
                                                //         primary25: '#D1E7DD',
                                                //         primary: '#34ce57',
                                                //     },
                                                // })}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col>
                                        <Form.Group className="mb-3" controlId="subject">
                                            <Form.Label>Subject (optional)</Form.Label>
                                            <Form.Control
                                                as="textarea"
                                                name="subject"
                                                value={message.subject}
                                                onChange={handleInputChange}
                                                placeholder="Enter subject..."
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                {/*Body*/}
                                <Row>
                                    <Col>
                                        <Form.Group className="mb-3" controlId="body">
                                            <Form.Label>Body (optional)</Form.Label>
                                            <Form.Control
                                                as="textarea"
                                                name="body"
                                                value={message.body}
                                                onChange={handleInputChange}
                                                placeholder="Enter message..."
                                                style={{height: "100px"}}
                                            />
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
                                </Row>

                                {/*Confirm button*/}
                                <Row className="d-flex justify-content-center">
                                    <Col className="d-flex justify-content-center">
                                        { fileError ?
                                            <div className="text-danger mb-3">
                                                {fileError}
                                            </div>
                                            : formErrors.contact ? (
                                                <div className="text-danger mb-3">
                                                    {formErrors.contact}
                                                </div>
                                            ) : <></>
                                        }
                                    </Col>
                                </Row>
                                <Row style={!formErrors.contact ? {marginTop: "40px"} : {}}>
                                    <Col className="d-flex justify-content-center">
                                        <Button variant="primary" type="submit" disabled={loading} >
                                            { loading ?
                                                <>
                                                    <Spinner
                                                        as="span"
                                                        animation="border"
                                                        size="sm"
                                                        role="status"
                                                        aria-hidden="true"
                                                    />
                                                    <span> Loading...</span>
                                                </>
                                                :
                                                <>
                                                    <i className="bi bi-check-lg"></i> Confirm
                                                </>
                                            }
                                        </Button>
                                    </Col>
                                </Row>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export {MessageForm};
