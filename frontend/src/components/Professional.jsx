import React, {useContext, useEffect, useState} from 'react';
import {Container, Row, Col, Card, Button, Badge} from 'react-bootstrap';
import API from "../API";
import {Link, useNavigate, useParams} from "react-router-dom";
import { FaEdit } from "react-icons/fa";
import {convertDuration} from "./Utils.jsx";
import {MessageContext} from "../messageCtx.js"; // Icona per il FAB

// Function to map employmentState to badge color
const getEmploymentBadgeVariant = (state) => {
    switch (state) {
        case 'employed':
            return 'success'; // green
        case 'available':
            return 'warning'; // yellow
        default:
            return 'danger'; //
    }
};
const getStatusBadgeVariant = (status) => {
    switch (status) {
        case 'done':
            return 'success'; // verde
        case 'aborted':
            return 'danger'; // rosso
        case 'selection_phase':
            return 'secondary'; // grigio
        case 'candidate_proposal':
            return 'info'; // blu
        case 'consolidated':
            return 'primary'; // blu scuro
        case 'created':
        default:
            return 'warning'; // giallo
    }
};
const ProfessionalProfile = ({xsrfToken,role}) => {
    const {professionalId} = useParams();
    console.log("Professional ID: ", professionalId);

    // State for professional data and loading/error handling
    const [professional, setProfessional] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const handleErrors = useContext(MessageContext);

    // Fetch professional data when the component mounts
    useEffect(() => {

        const fetchProfessional = async () => {
            try {
                const fetchedProfessional = await API.getProfessionalById(professionalId, xsrfToken);
                console.log("Professional data fetched: ", fetchedProfessional);
                setProfessional(fetchedProfessional);
                setLoading(false);
            } catch (err) {
                setError(err.error || "Error fetching professional data");
                setLoading(false);
            }
        };
        fetchProfessional().then(r => console.log("Professional data fetched: ", r));
    }, [professionalId, xsrfToken]);

    // If loading, show a loading message
    if (loading) {
        return <div>Loading professional data...</div>;
    }

    // If error, show an error message
    if (error) {
        handleErrors({detail: error})
        setError(null);
    }


    // Destructure professional object
    const {
        name,
        surname,
        ssncode,
        category,
        emails,
        telephones,
        addresses,
        employmentState,
        geographicalLocation,
        dailyRate,
        skills,
        notes,
        jobOffer,            // Attuale job offer
        abortedJobOffers,     // Offerte annullate
        candidateJobOffers,
        jobOffers,  //Offerte history concluse bene
    } = professional;

    return (
        <Container fluid className="py-5">
            <Row className="w-100">
                {/* Professional Info */}
                <Col lg={4} md={12} className="mb-4">
                    <Card className="shadow-lg h-100" style={{borderRadius: '15px'}}>
                        <Card.Body className="text-center">
                            <Card.Title className="mb-1" style={{fontSize: '1.8rem', fontWeight: 'bold'}}>
                                {`${name} ${surname}`}
                            </Card.Title>
                            <Row>
                                <Card.Subtitle className="text-muted mb-2">
                                    <Badge bg="info" pill>
                                        {category.toUpperCase()}
                                    </Badge>
                                </Card.Subtitle>
                                {/* Display Employment State */}
                                {employmentState && (
                                    <Card.Subtitle className="text-muted mb-2">
                                        <Badge bg={getEmploymentBadgeVariant(employmentState)} >
                                            {employmentState.toUpperCase()}
                                        </Badge>
                                    </Card.Subtitle>
                                )}
                            </Row>

                            <Card.Text className="text-muted"><strong>SSN:</strong>{` ${ssncode}`}</Card.Text>
                            <Card.Text className="text-muted"><strong>Daily Rate:</strong>{` ${dailyRate} €`}</Card.Text>

                            {/* Display Emails */}
                            {emails &&
                                emails.map((email, index) => (
                                    <a href={`mailto:${emails[index].email}`}>
                                        <Card.Text key={index}   hidden={email.state !== "active"} className="text-dark mb-2">
                                            <i className="bi bi-envelope-fill"></i> {email.email}
                                        </Card.Text>
                                    </a>
                                ))}

                            {/* Display Telephones */}
                            {telephones &&
                                telephones.map((telephone, index) => (
                                    <Card.Text key={index}  hidden={telephone.state !== "active"} className="text-dark">
                                        <i className="bi bi-telephone-fill"></i> {telephone.telephone}
                                    </Card.Text>
                                ))}

                            {/* Display Addresses */}
                            {addresses &&
                                addresses.map((address, index) => (
                                    <Card.Text key={index}  hidden={address.state !== "active"} className="text-dark">
                                        <i className="bi bi-geo-alt-fill"></i> {address?.address}
                                    </Card.Text>
                                ))}
                            {emails && emails.length > 1 &&
                                <a href={`mailto:${emails[0].email}`}>
                                    <Button variant="primary" className="mt-3 shadow-sm">
                                        Contact
                                    </Button>
                                </a>
                            }



                            {/*/!* Display Geographical Location *!/*/}
                            {/*{geographicalLocation && (*/}
                            {/*    <Card.Text className="mt-2">*/}
                            {/*        <i className="bi bi-geo-alt-fill"></i> Address: {`Lat: ${geographicalLocation.first}, Lng: ${geographicalLocation.second}`}*/}
                            {/*    </Card.Text>*/}
                            {/*)}*/}

                            {/* Contact Button */}
                            <Row>

                                {emails && emails.filter(email=> email.state==="active").length > 0 && (
                                    <a href={`mailto:${emails.filter(email=> email.state==="active")[0]}`}>
                                        <Button variant="primary" className="mt-3 shadow-sm">
                                            Contact
                                        </Button>
                                    </a>
                                )}
                            </Row>


                                {candidateJobOffers && candidateJobOffers.length > 0 && (
                                    <Row>
                                    <h3 className="mt-4">Candidate For:</h3>
                                    <ul className="list-unstyled">
                                        {candidateJobOffers.map((offer, index) => (
                                            <li key={index} onClick={()=>navigate(`/ui/jobOffers/${offer.id}`)}>
                                                <i className="bi bi-check-circle-fill"></i> {offer.name}
                                            </li>
                                        ))}
                                    </ul>

                                    </Row>)
                                }

                        </Card.Body>
                    </Card>
                </Col>

                {/* Additional Info */}
                <Col lg={8} md={12}>
                    <Row>
                        <Col md={8} className="mb-4">
                            {/* Notes Section */}
                            <Card className="shadow-lg h-100" style={{borderRadius: '15px'}}>
                                <Card.Header className="bg-primary text-white" style={{borderRadius: '15px 15px 0 0'}}>
                                    <h5>Notes</h5>
                                </Card.Header>
                                <Card.Body className="bg-light">
                                    {notes && notes.filter(it=> it.state === "active").length
                                        ? notes.map((note, index) => (
                                            <p key={index} hidden={note.state !== "active"} className="mb-2">
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

                    {/* Skills Section */}
                    <Row>
                        <Col>
                            <Card className="shadow-lg" style={{borderRadius: '15px'}}>
                                <Card.Header className="bg-primary text-white">
                                    <h5>Skills</h5>
                                </Card.Header>
                                <Card.Body className="bg-light">
                                    {skills && skills.filter(it=> it.state === "active").length? (
                                        <ul className="list-unstyled">
                                            {skills.map((skill, index) => (
                                                <li hidden={skill.state !== "active"} key={index}>
                                                    <i className="bi bi-check-circle-fill"></i> {skill.skill}
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        'No skills available'
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                    {/* Sezione delle Offerte di Lavoro */}
                    <Col >

                        {/* Sezione delle Offerte Attuali */}
                        <Row className="mt-4" onClick={()=>navigate(`/ui/jobOffers/${jobOffer.id}`) }>
                            <Col>
                                <Card className="shadow-lg h-100" style={{ borderRadius: '15px' }}>
                                    <Card.Header className="bg-primary text-white">
                                        <h5>Current Job Offer</h5>
                                    </Card.Header>
                                    <Card.Body className="bg-light">
                                        {jobOffer   ? (
                                            <Row xs={1} sm={2} md={3} className="g-4">
                                                    <Col >
                                                        <Card className="shadow-sm h-100" style={{ minWidth: '200px', borderRadius: '10px' }}>
                                                            <Card.Body>
                                                                <Card.Title>{jobOffer.name}</Card.Title>
                                                                <Card.Subtitle className="mb-2 text-muted">
                                                                    {jobOffer.description}
                                                                </Card.Subtitle>
                                                                <Badge
                                                                    bg={getStatusBadgeVariant(jobOffer.currentState)}
                                                                    className="mb-2"
                                                                >
                                                                    {jobOffer.currentState.toUpperCase().replace('_', ' ')}
                                                                </Badge>
                                                                <Card.Text>
                                                                    <strong>Note:</strong> {jobOffer.currentStateNote}
                                                                </Card.Text>
                                                                <Card.Text>
                                                                    <strong>Duration:</strong> {convertDuration(jobOffer.duration,true)}
                                                                </Card.Text>
                                                                <Card.Text>
                                                                    <strong>Profit Margin:</strong> {jobOffer.profitMargin}%
                                                                </Card.Text>
                                                            </Card.Body>
                                                        </Card>
                                                    </Col>
                                            </Row>
                                        ) : (
                                            'No current job offer available'
                                        )}
                                    </Card.Body>
                                </Card>
                            </Col>
                        </Row>
                        {/* Sezione delle Offerte History */}
                        <Row className="mt-4">
                            <Col>
                                <Card className="shadow-lg" style={{ borderRadius: '15px' }}>
                                    <Card.Header className="bg-warning text-white">
                                        <h5>Concluded Job Offers</h5>
                                    </Card.Header>
                                    <Card.Body className="bg-light">
                                        {jobOffers && jobOffers.length > 0 ? (
                                            <Row xs={1} sm={2} md={3} className="g-4">
                                                {jobOffers.map((offer, index) => (
                                                    <Col key={index} onClick={()=>navigate(`/ui/jobOffers/${offer.id}`)}>
                                                        <Card className="shadow-sm h-100" style={{ minWidth: '200px', borderRadius: '10px' }}>
                                                            <Card.Body>
                                                                <Card.Title>{offer.name}</Card.Title>
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
                                                                <Card.Text>
                                                                    <strong>Profit Margin:</strong> {offer.profitMargin}%
                                                                </Card.Text>
                                                            </Card.Body>
                                                        </Card>
                                                    </Col>
                                                ))}
                                            </Row>
                                        ) : (
                                            'No job offers available'
                                        )}
                                    </Card.Body>
                                </Card>
                            </Col>
                        </Row>

                        {/* Sezione delle Offerte Annullate */}
            <Row className="mt-4">
                <Col>
                    <Card className="shadow-lg h-100" style={{ borderRadius: '15px' }}>
                        <Card.Header className="bg-danger text-white">
                            <h5>Aborted Job Offers</h5>
                        </Card.Header>
                        <Card.Body className="bg-light">
                            {abortedJobOffers && abortedJobOffers.length > 0 ? (
                                <Row xs={1} sm={2} md={3} className="g-4">
                                    {abortedJobOffers.map((offer, index) => (
                                        <Col key={index} onClick={()=>navigate(`/ui/jobOffers/${offer.id}`)}>
                                            <Card className="shadow-sm h-100" style={{ minWidth: '200px', borderRadius: '10px' }}>
                                                <Card.Body>
                                                    <Card.Title>{offer.name}</Card.Title>
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
                                                    <Card.Text>
                                                        <strong>Profit Margin:</strong> {offer.profitMargin}%
                                                    </Card.Text>
                                                </Card.Body>
                                            </Card>
                                        </Col>
                                    ))}
                                </Row>
                            ) : (
                                'No aborted job offers available'
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

                    </Col>
                </Col>

            </Row>
            {/* FAB for Editing Professional */}
            {professionalId && role !== "professional" &&
            <Link to={`/ui/professionals/edit/${professionalId}`}>
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


export default ProfessionalProfile;
