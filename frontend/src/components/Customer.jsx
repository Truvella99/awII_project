import React, {useEffect, useState} from 'react';
import { Container, Row, Col, Card, Button, Badge } from 'react-bootstrap';
import API from "../API";
import {Link, useNavigate, useParams} from "react-router-dom";
import {FaEdit} from "react-icons/fa";
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

const CustomerProfile = ({ xsrfToken,handleErrors}) => {
    const {customerId} = useParams();
    console.log("Customer ID: ", customerId)
    // State for customer data and loading/error handling
    const [customer, setCustomer] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    // Fetch customer data when the component mounts
    useEffect(() => {

        const fetchCustomer = async () => {
            try {
                const fetchedCustomer = await API.getCustomerById(customerId, xsrfToken);
                console.log("Customer data fetched: ", fetchedCustomer)
                setCustomer(fetchedCustomer);
                setLoading(false);
            } catch (err) {
                setError(err.error || "Error fetching customer data");
                setLoading(false);
            }
        };
        fetchCustomer().then(r => console.log("Customer data fetched: ", r))
    }, [customerId, xsrfToken]);

    // If loading, show a loading message
    if (loading) {
        return <div>Loading customer data...</div>;
    }

    // If error, show an error message
    if (error) {
        setError(error);
    }
    //TODO

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
                <Col lg={8} md={12}>
                    <Row>
                        <Col md={8} className="mb-4">
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

                        <Col md={4} className="mb-4 ms-auto text-end">
                        {/* Download CV Section */}
                        <Button variant="success" className="shadow-sm">
                            <i className="bi bi-download"></i> Download CV
                        </Button>
                    </Col>
                    </Row>

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
                                                                <strong>Duration:</strong> {offer.duration} months
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
            {customerId &&
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
