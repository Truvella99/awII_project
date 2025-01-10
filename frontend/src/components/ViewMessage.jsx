import {useNavigate, useParams} from "react-router-dom";
import React, {useContext, useEffect, useRef, useState} from "react";
import {MessageContext, TokenContext} from "../messageCtx.js";
import API from "../API.jsx";
import {Badge, Button, Card, Col, FloatingLabel, Row, Form} from "react-bootstrap";
import {CustomLoadingOverlay, SideBar} from "./Utils.jsx";
import {Container} from "react-bootstrap/";
import Select from "react-select";
import PostalMime from "postal-mime";


function ViewMessage({loggedIn, role, unreadMessages, pending, setPending, setUnreadMessages}) {
    const { messageId } = useParams();
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [message, setMessage] = useState();
    const [emailData, setEmailData] = useState();
    const [histories, setHistories] = useState();
    const [state, setState] = useState();
    const [priority, setPriority] = useState();
    const [comment, setComment] = useState('');
    const [editState, setEditState] = useState(false);
    const [savable, setSavable] = useState(false);
    const [editPriority, setEditPriority] = useState(false);
    const [optionState, setOptionState] = useState([]);
    const [selectedState, setSelectedState] = useState({});
    const [selectedPriority, setSelectedPriority] = useState({});
    const [loaded, setLoaded] = useState(false);
    const stateReadOptions = [
        { value: 'processing', label: 'Processing' },
        { value: 'done', label: 'Done' },
        { value: 'discarded', label: 'Discarded' },
        { value: 'failed', label: 'Failed' }
    ];
    const stateProcessingOptions = [
        { value: 'done', label: 'Done' },
        { value: 'failed', label: 'Failed' }
    ];
    const priorityOptions = [
        { value: 'low', label: 'Low' },
        { value: 'medium', label: 'Medium' },
        { value: 'high', label: 'High' }
    ];

    // Fetching the message and setting the dropdowns
    useEffect(() => {
        const fetchMessage = async () => {
            try {
                if (loggedIn) {
                    // Fetch message
                    const message = await API.getMessageById(messageId, xsrfToken);
                    console.log(message);
                    setMessage(message);
                    if (message.body) {
                        // Reverting URL-safe Base64 encoding
                        const modifiedEmail = message.body.replace(/-/g, '+').replace(/_/g, '/');
                        // Decoding the Base64 string
                        const decodedEmail = atob(modifiedEmail);
                        console.log(decodedEmail);
                        // Parsing the email
                        const finalBody = await PostalMime.parse(decodedEmail);
                        const attachments = finalBody.attachments.map(attachment => ({
                            filename: attachment.filename,
                            mimeType: attachment.mimeType,
                            data: attachment.content
                        }));
                        setEmailData({
                            textBody: finalBody.text,
                            htmlBody: finalBody.html,
                            attachments: attachments
                        });
                    }
                    // Setting the dropdowns
                    if (message.currentState === "read")
                        setOptionState(stateReadOptions);
                    else
                        setOptionState(stateProcessingOptions);
                    setState(message.currentState);
                    setSelectedState({value: message.currentState, label: message.currentState.charAt(0).toUpperCase() + message.currentState.slice(1)});
                    setPriority(message.priority);
                    setSelectedPriority({value: message.priority, label: message.priority.charAt(0).toUpperCase() + message.priority.slice(1)});
                    setLoaded(true);
                } else {
                    navigate("/ui")
                }
            } catch (err) {
                console.log(err);
                handleError(err);
            }
        };

        fetchMessage();
    }, [messageId, xsrfToken]);

    // Fetch and update state history
    useEffect(() => {
        const fetchUpdateHistory = async () => {
            try {
                if (loggedIn) {
                    const histories = await API.getMessageHistory(messageId, xsrfToken);
                    setHistories(histories);
                } else {
                    navigate("/ui")
                }
            } catch (err) {
                console.log(err);
                handleError(err);
            }
        };

        fetchUpdateHistory();
    }, [state, xsrfToken]);

    // Refresh unread messages and pending
    useEffect(() => {
        const fetchMessagesData = async () => {
            try {
                if (role === "manager" || role === "operator") {
                    // Refresh unread messages
                    const messages = await API.getMessagesReceived(xsrfToken);
                    setUnreadMessages(messages.length);
                    // Refresh pending contacts
                    const pendings = await API.getPendingContacts(xsrfToken);
                    setPending(pendings.length);
                }
            } catch (error) {
                console.error("Error refreshing unread messages and pendings data:", error);
            }
        };
        fetchMessagesData();
    }, []);


    return(
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '80vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                    <Row style={{marginBottom: '100px'}}>
                        { (role === "operator" || role === "manager") ?
                            <Col className="d-flex justify-content-center">
                                <Button variant="info" onClick={() => navigate('/ui/messages/addMessage')}> <i className="bi bi-plus-lg"></i> Add message </Button>
                            </Col>
                            : <></>
                        }
                    </Row>
                </Col>
                { loaded ? (
                    <>
                    <Col style={{maxWidth: "63vw"}}>
                        <Card className="shadow-lg">
                            { message.subject &&
                                <Card.Header style={{fontWeight: "bold"}}>
                                    {message.subject}
                                </Card.Header>
                            }
                            <Card.Body>

                                {/*Sender and date*/}
                                <Card.Title style={{marginBottom: "20px"}}>
                                    <div className="d-flex justify-content-between">
                                        <div className="d-flex align-items-center">
                                            <span className="text-muted">
                                                From:
                                            </span>
                                            { message.channel === "email" ? (
                                                <a href={`mailto:${message.email}`}>
                                                    <span className="text-dark" style={{fontSize: "large", marginLeft: "10px"}}>
                                                        <i className="bi bi-envelope-fill"></i> {message.email}
                                                    </span>
                                                </a>
                                            ) : message.channel === "phonecall" ? (
                                                <span className="text-dark" style={{fontSize: "large", marginLeft: "10px"}}>
                                                    <i className="bi bi-telephone-fill"></i> {message.telephone}
                                                </span>
                                            ) : (
                                                <span className="text-dark" style={{fontSize: "large", marginLeft: "10px"}}>
                                                    <i className="bi bi-geo-alt-fill"></i> {message.address}
                                                </span>
                                            ) }
                                        </div>
                                        <div className="d-flex align-items-center">
                                            <span className="text-muted">
                                                On:
                                            </span>
                                            <div style={{fontSize: "large", marginLeft: "5px"}}>
                                                {new Date(message.date).toLocaleDateString("default", {
                                                    day: "2-digit",
                                                    month: "2-digit",
                                                    year: "numeric",
                                                    hour: "2-digit",
                                                    minute: "2-digit",
                                                    hour12: false
                                                })}
                                            </div>
                                        </div>
                                    </div>
                                </Card.Title>

                                {/*Current state*/}
                                <Card.Title style={{marginBottom: "15px"}}>
                                    <div className="d-flex align-items-center">
                                        <span className="text-muted" style={{paddingRight: "5px"}}>
                                            Current state:
                                        </span>
                                        {editState ? (
                                            <>
                                                <Select
                                                    options={optionState}
                                                    value={selectedState}
                                                    onChange={ev => {
                                                        setSavable(true);
                                                        setSelectedState(ev);
                                                    }}
                                                    closeMenuOnSelect={true}
                                                    isSearchable={true}
                                                    isClearable={false}
                                                    styles={{
                                                        container: base => ({
                                                            ...base,
                                                            width: '180px'
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
                                                <Form.Control
                                                    as="textarea"
                                                    placeholder="Comment..."
                                                    style={{maxWidth: "150px", height: "35px", marginLeft: "10px", marginRight: "10px"}}
                                                    value={comment}
                                                    onChange={(ev) => setComment(ev.target.value)}
                                                />
                                                <Badge bg={savable ? "success" : ""} pill style={savable ? {marginLeft: "10px", cursor: "pointer"} : {marginLeft: "10px", backgroundColor: "#286c4c", color: "#b8b4bc", cursor: "default"}}
                                                       onClick={async () => {
                                                           if (savable) {
                                                               try {
                                                                   const message = {targetState: selectedState.value, comment: comment};
                                                                   await API.updateMessageState(message, messageId, xsrfToken);
                                                                   if (selectedState.value === "processing")
                                                                       setOptionState(stateProcessingOptions);
                                                                   setState(selectedState.value);
                                                                   setComment('');
                                                                   setSavable(false);
                                                                   setEditState(false);
                                                               } catch (err) {
                                                                   console.log(err);
                                                                   handleError(err);
                                                               }
                                                           }
                                                       }}>
                                                    <i className="bi bi-check-lg"></i> Save
                                                </Badge>
                                                <Badge bg="danger" pill style={{marginLeft: "10px", cursor: "pointer"}}
                                                       onClick={() => {
                                                           setComment('');
                                                           setSavable(false);
                                                           setSelectedState({value: state, label: state.charAt(0).toUpperCase() + state.slice(1)});
                                                           setEditState(false);
                                                       }}>
                                                    <i className="bi bi-x-lg"></i> Cancel
                                                </Badge>
                                            </>
                                        ) : (
                                            <>
                                                {state.charAt(0).toUpperCase() + state.slice(1)}
                                                { (state === "read" || state === "processing") &&
                                                    <Badge bg="primary" pill style={{marginLeft: "10px", cursor: "pointer"}}
                                                           onClick={() => setEditState(true)}>
                                                        <i className="bi bi-pencil-square"></i> Edit
                                                    </Badge>
                                                }
                                            </>
                                        )}
                                    </div>
                                </Card.Title>

                                {/*Priority*/}
                                <Card.Title style={{marginBottom: "50px"}}>
                                    <div className="d-flex align-items-center">
                                        <span className="text-muted" style={{paddingRight: "5px"}}>
                                            Priority:
                                        </span>
                                        {editPriority ? (
                                            <>
                                                <Select
                                                    options={priorityOptions}
                                                    value={selectedPriority}
                                                    onChange={ev => {
                                                        setSelectedPriority(ev)
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
                                                <Badge bg="success" pill style={{marginLeft: "10px", cursor: "pointer"}}
                                                       onClick={async () => {
                                                           try {
                                                               await API.updateMessagePriority(selectedPriority.value, messageId, xsrfToken);
                                                               setPriority(selectedPriority.value);
                                                               setEditPriority(false);
                                                           } catch (err) {
                                                               console.log(err);
                                                               handleError(err);
                                                           }}}>
                                                    <i className="bi bi-check-lg"></i> Save
                                                </Badge>
                                                <Badge bg="danger" pill style={{marginLeft: "10px", cursor: "pointer"}}
                                                       onClick={ () => {
                                                            setSelectedPriority({value: priority, label: priority.charAt(0).toUpperCase() + priority.slice(1)});
                                                            setEditPriority(false);
                                                       }}>
                                                    <i className="bi bi-x-lg"></i> Cancel
                                                </Badge>
                                            </>
                                        ) : (
                                            <>
                                                {priority.charAt(0).toUpperCase() + priority.slice(1)}
                                                <Badge bg="primary" pill style={{marginLeft: "10px", cursor: "pointer"}}
                                                       onClick={() => setEditPriority(true)}>
                                                    <i className="bi bi-pencil-square"></i> Edit
                                                </Badge>
                                            </>
                                        )}
                                    </div>
                                </Card.Title>

                                {/*Message body*/}
                                { message.body && (emailData.textBody || emailData.htmlBody) ?
                                    emailData.htmlBody ?
                                        <Card.Text style={{maxHeight: "42vh", overflowY: "auto"}} dangerouslySetInnerHTML={{__html: emailData.htmlBody}} />
                                        : <Card.Text style={{maxHeight: "42vh", overflowY: "auto" }}>
                                            {emailData.textBody}
                                          </Card.Text>
                                    : <Card.Text style={{fontStyle: "italic", fontSize: "larger"}}>
                                        There is no message body!
                                    </Card.Text>
                                }
                            </Card.Body>
                            {emailData && emailData.attachments.length > 0 && (
                                <Card.Footer>
                                    <Card.Text style={{fontSize: "large"}} className="text-muted">
                                        Attachments:
                                    </Card.Text>
                                    <Row>
                                        {emailData.attachments.map((attachment, index) => (
                                            <Col xs={"auto"}>
                                                <Button variant="primary" onClick={() => {
                                                    const blob = new Blob([attachment.data], {type: attachment.mimeType});
                                                    const url = URL.createObjectURL(blob);
                                                    const a = document.createElement('a');
                                                    a.href = url;
                                                    a.download = attachment.filename;
                                                    document.body.appendChild(a);
                                                    a.click();
                                                    document.body.removeChild(a);
                                                    URL.revokeObjectURL(url);
                                                }}>
                                                    <i className="bi bi-download"></i> {attachment.filename}
                                                </Button>
                                            </Col>
                                        ))}
                                    </Row>
                                </Card.Footer>
                            )}
                        </Card>
                    </Col>
                    <Col style={{ width: "250px", maxWidth: "250px"}}>
                        <Card className="shadow-lg">
                            <Card.Header className="d-flex justify-content-center text-muted">
                                State history:
                            </Card.Header>
                            <Card.Body style={{maxHeight: "80vh", overflowY: "auto"}}>
                                { histories && histories.map((history, index) => (
                                    <Card key={index} style={{marginBottom: "15px"}}>
                                        <Card.Body>
                                            <Card.Title style={{fontStyle: "italic"}}>
                                                State changed to
                                                <span style={{marginTop: "5px", marginBottom: "10px"}} className="d-flex justify-content-center">
                                                <Badge pill bg={
                                                    history.state === "read" ? "primary" :
                                                        history.state === "processing" ? "secondary" :
                                                            history.state === "done" ? "success" :
                                                                history.state === "discarded" ? "dark" :
                                                                    "danger"
                                                }>
                                                    {history.state.charAt(0).toUpperCase() + history.state.slice(1)}
                                                </Badge>
                                            </span>
                                            </Card.Title>
                                            <Card.Subtitle className="text-muted d-flex justify-content-center">
                                                {new Date(history.date).toLocaleDateString("default", {
                                                    day: "2-digit",
                                                    month: "2-digit",
                                                    year: "numeric",
                                                    hour: "2-digit",
                                                    minute: "2-digit",
                                                    hour12: false
                                                })}
                                            </Card.Subtitle>
                                            <Card.Text style={{marginTop: "10px"}} >
                                                {history.comment}
                                            </Card.Text>
                                        </Card.Body>
                                    </Card>
                                ))}
                            </Card.Body>
                        </Card>
                    </Col>
                    </>
                ) : (
                    <Col style={{
                        height: '330px',
                        width: '330px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        paddingTop: '10px'
                    }}>
                        <div>
                            <img style={{maxHeight: '20%', maxWidth: '20%', paddingRight: '5px'}}
                                 src="https://media.tenor.com/-n8JvVIqBXkAAAAM/dddd.gif"/>
                            <span style={{fontSize: "larger"}}> Loading... </span>
                        </div>
                    </Col>
                )}
            </Row>
        </Container>
    );
}

export {ViewMessage};