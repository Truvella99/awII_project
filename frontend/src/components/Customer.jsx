import React, {useContext, useEffect, useState} from 'react';
import {Container, Row, Col, Card, Button, Badge, Accordion} from 'react-bootstrap';
import API from "../API";
import {Link, useNavigate, useParams} from "react-router-dom";
import {FaEdit} from "react-icons/fa";
import {convertDuration} from "./Utils.jsx";
import {MessageContext} from "../messageCtx.js";
// Function to map jobOfferStatus to color
const getStatusBadgeVariant = (status) => {
    switch (status) {
        case jobOfferStatus.done:
            return 'success'; // green
        case jobOfferStatus.aborted:
            return 'danger'; // red
        case jobOfferStatus.selection_phase:
            return 'secondary'; // yellow
        case jobOfferStatus.candidate_proposal:
            return 'info'; // blue
        case jobOfferStatus.consolidated:
            return 'primary'; // dark blue
        case jobOfferStatus.created:
        default:
            return 'warning'; // grey
    }
};
// Enum for jobOfferStatus
const jobOfferStatus = {
    created: 'created',
    aborted: 'aborted',
    selection_phase: 'selection_phase',
    candidate_proposal: 'candidate_proposal',
    consolidated: 'consolidated',
    done: 'done'
};

const CustomerProfile = ({ xsrfToken,role}) => {
    const handleErrors = useContext(MessageContext);
    const {customerId} = useParams();
    console.log("Customer ID: ", customerId)
    // State for customer data and loading/error handling
    const [customer, setCustomer] = useState({}); // Cambiato da null a {}
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    // const [files, setFiles] = useState([]);
    const [filesData, setFilesData] = useState([]);

    // Raggruppa i file per fileName
    const groupedByFileName = filesData.reduce((acc, file) => {
        if (!acc[file.fileName]) {
            acc[file.fileName] = [];
        }
        acc[file.fileName].push(file);
        return acc;
    }, {});
    const downloadFile = (file) => {
        console.log("Download file: ", file);
        const { data, fileName, contentType } = file; // Estraggo i dati, nome e tipo dal file
        const blob = new Blob([data], { type: contentType }); // Crea un Blob dai dati del file
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);

    };
    // Fetch customer data when the component mounts
    useEffect(() => {
        const fetchCustomer = async () => {
            try {
                const fetchedCustomer = await API.getCustomerById(customerId, xsrfToken);
                console.log("Customer data fetched 1: ", fetchedCustomer);

                const fetchedFiles = await API.getDocumentByUserId(customerId, xsrfToken);
                console.log("Customer  data fetched 2: ", fetchedFiles);

                const filesData = await Promise.all(
                    fetchedFiles.map(async (file) => {
                        const fileData = await API.getDocumentData(file.documentId, xsrfToken);
                        return { ...file, data: fileData };
                    })
                );
                console.log("Customer data fetcheddaq");
                setFilesData(filesData);
                setCustomer(fetchedCustomer);
                console.log("Customer data fetched 3: ", fetchedCustomer);
                setLoading(false);
            } catch (err) {
                console.log("Error fetching customer data: ", err);
                setError(err.error || "Error fetching customer data");
                setLoading(false);
            }
        };

        fetchCustomer()
            .then(() => console.log("Customer data fetched successfully"))
            .catch((e) => console.log("Error fetching customer data: ", e));
    }, [customerId, xsrfToken]);

    // If loading, show a loading message
    if (loading) {
        return <div>Loading customer data...</div>;
    }

    // If error, show an error message
    if (error) {
        handleErrors({detail: error})
        setError(null);
    }

    const { name, surname, ssncode, category, emails, telephones, addresses, notes, jobOffers } = customer;

    return (
        <Container fluid className="py-5">

            <Row className="w-100">
                {/* Customer Info */}
                <Col lg={4} md={12} className="mb-4">
                    <Card className="shadow-lg h-100" style={{ borderRadius: '15px' }}>
                        <Card.Body className="text-center">
                            <Card.Title className="mb-1" style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>
                                {`${name} ${surname}`}
                            </Card.Title>
                            <Card.Subtitle className="text-muted mb-2">
                                <Badge bg="info" pill>
                                    {category.toUpperCase()}
                                </Badge>
                            </Card.Subtitle>
                            <Card.Text className="text-muted"><strong>SSN:</strong>{` ${ssncode}`}</Card.Text>

                            {/* Display Emails */}
                            {emails &&
                                emails.map((email, index) => (
                                    <a href={`mailto:${emails[index].email}`}>
                                    <Card.Text key={index} hidden={email.state !== "active"} className="text-dark">
                                        <i className="bi bi-envelope-fill"></i> {email.email}
                                    </Card.Text>
                                    </a>
                                ))}

                            {/* Display Telephones */}
                            {telephones &&
                                telephones.map((telephone, index) => (
                                    <Card.Text hidden={telephone.state !== "active"} key={index} className="text-dark">
                                        <i className="bi bi-telephone-fill"></i> {telephone.telephone}
                                    </Card.Text>
                                ))}

                            {/* Display Addresses */}
                            {addresses &&
                                addresses.map((address, index) => (
                                    <Card.Text key={index} hidden={address.state !== "active"} className="text-dark">
                                        <i className="bi bi-geo-alt-fill"></i> {address.address}
                                    </Card.Text>
                                ))}
                            {emails && emails.filter(email=> email.state==="active").length > 0 &&
                                <a href={`mailto:${emails.filter(email=> email.state==="active")[0]?.email}`}>
                                <Button variant="primary" className="mt-3 shadow-sm">
                                    Contact
                                </Button>
                            </a>
                            }

                            {/* <Card.Text className="mt-3">
                                Candidate for: <a href="#">Job Offer name</a>
                            </Card.Text> */}
                        </Card.Body>
                    </Card>
                </Col>

                {/* Additional Info */}
                <Col >
                    <Row>
                        <Col className="mb-4">
                            {/* Notes Section */}
                            <Card className="shadow-lg h-100" style={{ borderRadius: '15px' }}>
                                <Card.Header className="bg-primary text-white" style={{ borderRadius: '15px 15px 0 0' }}>
                                    <h5>Notes</h5>
                                </Card.Header>
                                <Card.Body className="bg-light">
                                    {notes && notes.filter(it=> it.state === "active").length
                                        ? notes.map((note, index) => (
                                              <p key={index} className="mb-2" hidden={note.state !== "active"}>
                                                  <i className="bi bi-chat-left-text"></i> {note.note}
                                              </p>
                                          ))
                                        : 'No notes available'}
                                </Card.Body>
                            </Card>
                        </Col>


                    </Row>

                    {/* Files Accordion */}
                    <Row className="mb-3">
                        <Col>
                            <Card className="shadow-lg" style={{ borderRadius: '15px' }}>
                                <Card.Header className="bg-primary text-white">
                                    <h5>Files</h5>
                                </Card.Header>
                                <Card.Body className="bg-light">
                                    {Object.keys(groupedByFileName).length ? (
                                        <Accordion defaultActiveKey="0">
                                            {Object.entries(groupedByFileName).map(([fileName, fileVersions], index) => {
                                                // Ordina le versioni per `keyVersion` in ordine decrescente
                                                const sortedVersions = fileVersions.sort((a, b) => b.keyVersion - a.keyVersion);

                                                return (
                                                    <Accordion.Item eventKey={index.toString()} key={index}>
                                                        {/* Header: Mostra solo il fileName */}
                                                        <Accordion.Header>
                                                            <span style={{ fontWeight: 'bold' }}>{fileName}</span>
                                                        </Accordion.Header>

                                                        {/* Body: Mostra tutte le versioni del file */}
                                                        <Accordion.Body>
                                                            {sortedVersions.map((file, versionIndex) => (
                                                                <div key={versionIndex} className="d-flex justify-content-between align-items-center" style={{
                                                                    borderBottom: versionIndex < sortedVersions.length - 1 ? '1px solid #ccc' : 'none',
                                                                    paddingBottom: '10px',
                                                                    marginBottom: '10px'
                                                                }}>
                                                                    {/* Colonna per la versione e la data */}
                                                                    <div style={{ flex: 1 }}>
                                                                        <p><strong>Version:</strong> {file.keyVersion}</p>
                                                                        <p>
                                                                            <strong>Upload Date:</strong>{" "}
                                                                            {new Date(file.creationTimestamp).toLocaleDateString('en-BG', {
                                                                                day: '2-digit',
                                                                                month: 'long',
                                                                                year: 'numeric'
                                                                            })} -{" "}
                                                                            {new Date(new Date(file.creationTimestamp).getTime() + 60 * 60 * 1000).toLocaleTimeString('en-BG', {
                                                                                hour: '2-digit',
                                                                                minute: '2-digit'
                                                                            })}
                                                                        </p>
                                                                    </div>

                                                                    {/* Colonna per il bottone di download */}
                                                                    <div>
                                                                        <Button
                                                                            variant="primary"
                                                                            onClick={() => downloadFile(filesData.find((f) => f.documentId === file.documentId) || {})}
                                                                            className="mt-2"
                                                                        >
                                                                            <i className="bi bi-download"></i> Download
                                                                        </Button>
                                                                    </div>

                                                                </div>
                                                            ))}
                                                        </Accordion.Body>

                                                    </Accordion.Item>
                                                );
                                            })}
                                        </Accordion>
                                    ) : (
                                        <div>No files available</div>
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                    {/* Job Offers History */}
                    <Row>
                        <Col>
                            <Card className="shadow-lg" style={{ borderRadius: '15px' }}>
                                <Card.Header className="bg-primary text-white">
                                    <h5>Job Offers History</h5>
                                </Card.Header>
                                <Card.Body className="bg-light" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                                    {jobOffers.length ? (
                                        <Row xs={1} sm={2} md={3} className="g-4" >
                                            {jobOffers.map((offer, index) => (
                                                <Col key={index} onClick={()=> navigate(`/ui/joboffers/${offer.id}`)}>
                                                    <Card className="shadow-sm h-100" style={{ minWidth: '200px', borderRadius: '10px' }} >
                                                        <Card.Body>
                                                            <Card.Title >{offer.name}</Card.Title >
                                                            <Card.Subtitle className="mb-2 text-muted">
                                                                {offer.description}
                                                            </Card.Subtitle>
                                                            <Badge
                                                                bg={getStatusBadgeVariant(offer.currentState)}
                                                                className="mb-2"
                                                            >
                                                                {offer.currentState.toUpperCase().replace('_', ' ')}
                                                            </Badge>
                                                            <Card.Text>
                                                                <strong>Note:</strong> {offer.currentStateNote}
                                                            </Card.Text>
                                                            <Card.Text>
                                                                <strong>Duration:</strong> {convertDuration(offer.duration,true)}
                                                            </Card.Text>
                                                            {offer.value && (
                                                                <Card.Text>
                                                                    <strong>Value:</strong> €{offer.value}
                                                                </Card.Text>
                                                            )}
                                                            <Card.Text>
                                                                <strong>Profit Margin:</strong> {offer.profitMargin}%
                                                            </Card.Text>
                                                        </Card.Body>
                                                    </Card>
                                                </Col>
                                            ))}
                                        </Row>
                                    ) : (
                                        <div>No job offers available</div>
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>

                </Col>
            </Row>
            {/* FAB for Editing Professional */}
            {customerId && role !== "customer" &&
                <Link to={`/ui/customers/edit/${customerId}`}>
                    <Button
                        variant="primary"
                        className="shadow-lg"
                        style={{
                            position: 'fixed',
                            bottom: '20px',
                            right: '20px',
                            borderRadius: '50%',
                            width: '60px',
                            height: '60px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                        }}
                    >
                        <FaEdit size={30} />
                    </Button>
                </Link>
            }
        </Container>
    );
};

export default CustomerProfile;
