import { useState, useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { MessageContext, TokenContext } from "../messageCtx";
import { useContext } from "react";
import { Button, Col, Row, Form, Card, Container, ListGroup, InputGroup, CloseButton, Badge } from "react-bootstrap";
import Select, { components } from 'react-select';
import MultiValueLabel from 'react-select';
import CreatableSelect from 'react-select/creatable';
import { ReactSearchAutocomplete } from 'react-search-autocomplete';
import API from "../API";
import validator from 'validator';
import InputMask from "react-input-mask";

function JobOfferContainer({ loggedIn }) {
    const [mode, setMode] = useState('');
    const [jobOffer, setJobOffer] = useState({});
    const jobOfferId = useParams().jobOfferId;
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);

    function convertDuration(duration) {
        let hours = Math.floor(duration / 60);
        let minutes = duration % 60;
        return `${hours.toString().padStart(4, '0')}:${minutes.toString().padStart(2, '0')}`;
    }

    useEffect(() => {
        // function used to retrieve page information in detail
        async function getJobOfferById(jobOfferId) {
            try {
                if (loggedIn) {
                    // logged user
                    if (jobOfferId) {
                        // get the jobOffer
                        let jobOffer = await API.getJobOfferById(jobOfferId, xsrfToken);
                        console.log(jobOffer)
                        let customer = await API.getCustomerById(jobOffer.customerId, xsrfToken);
                        let jobOfferValue = null;
                        let professional = null;
                        if (jobOffer.professionalId) {
                            jobOfferValue = await API.getJobOfferValueById(jobOfferId, xsrfToken);
                            professional = await API.getProfessionalById(jobOffer.professionalId, xsrfToken);
                        }
                        const abortedProfessionalPromises = jobOffer.abortedProfessionalsId.map(id => API.getProfessionalById(id));
                        let abortedProfessionals = (await Promise.all(abortedProfessionalPromises));
                        abortedProfessionals = abortedProfessionals.map(professional => Object.assign({}, professional, { label: professional.name }));
                        const candidateProfessionalPromises = jobOffer.candidateProfessionalsId.map(id => API.getProfessionalById(id));
                        let candidateProfessionals = await Promise.all(candidateProfessionalPromises);
                        candidateProfessionals = candidateProfessionals.map(professional => Object.assign({}, professional, { label: professional.name }));
                        let history = await API.getJobOfferHistoryById(jobOfferId, xsrfToken);
                        setMode('view');
                        setJobOffer(Object.assign({}, jobOffer, { duration: convertDuration(jobOffer.duration), professionals: abortedProfessionals.concat(candidateProfessionals), professional: professional, customer: customer, value: jobOfferValue, history: history }));
                    } else {
                        // add jobOffer
                        setMode('add');
                    }
                } else {
                    // MUST LOG IN
                    navigate("/ui")
                }
            } catch (err) {
                console.log(err);
                // show error message
                handleError(err);
            }
        };

        getJobOfferById(jobOfferId);
    }, []);

    function capitalizeFirstLetter(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    return (
        <>
            <h1 style={{ textAlign: "center" }}>{capitalizeFirstLetter(mode)} JobOffer</h1>
            <JobOfferForm mode={mode} setMode={setMode} jobOffer={jobOffer} />
        </>
    );
}

function JobOfferForm({ mode, setMode, jobOffer }) {
    const handleErrors = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const navigate = useNavigate();
    // jobOffer States
    const oldprofessional = jobOffer.professional || null;
    const oldprofessionals = jobOffer.professionals || [];
    const [id, setId] = useState(jobOffer.id || null);
    const [name, setName] = useState(jobOffer.name || '');
    const [description, setDescription] = useState(jobOffer.description || '');
    const [currentState, setCurrentState] = useState(jobOffer.currentState || 'created');
    const [currentStateNote, setCurrentStateNote] = useState(jobOffer.currentStateNote || null);
    const [duration, setDuration] = useState(jobOffer.duration || '');
    const [invalidDuration, setInvalidDuration] = useState(false);
    const [value, setValue] = useState(jobOffer.value || '');
    const [profitMargin, setProfitMargin] = useState(jobOffer.profitMargin || '');
    const [invalidProfitMargin, setInvalidProfitMargin] = useState(false);
    const [customer, setCustomer] = useState(jobOffer.customer || {});
    const [searchedCustomers, setSearchedCustomers] = useState([]);
    const [skills, setSkills] = useState(jobOffer.skills || []);
    const [skillsToDelete, setSkillsToDelete] = useState([]);
    const [professional, setProfessional] = useState(jobOffer.professional || {});
    // all the professionals of the job offer (candidates, declined)
    // TODO filter from here the selected one (shown in the separate field)
    const [professionals, setProfessionals] = useState(jobOffer.professionals || []);
    const [searchedProfessionals, setSearchedProfessionals] = useState([]);
    const [history, setHistory] = useState(jobOffer.history || []);

    const [validated, setValidated] = useState(false);
    const readOnlyBoolean = (mode === 'add' || mode === 'edit' && (jobOffer.currentState && jobOffer.currentState !== 'aborted')) ? false : true;

    // Update states when jobOffer changes
    useEffect(() => {
        if (jobOffer.name) {
            setId(jobOffer.id || null);
            setName(jobOffer.name || '');
            setDescription(jobOffer.description || '');
            setCurrentState(jobOffer.currentState || 'created');
            setCurrentStateNote(jobOffer.currentStateNote || null);
            setDuration(jobOffer.duration.toString() || '');
            setValue((jobOffer.value) ? jobOffer.value.toString() : '');
            setProfitMargin(jobOffer.profitMargin.toString() || '');
            setCustomer(jobOffer.customer || {});
            setSkills(jobOffer.skills.filter(skill => skill.state === 'active').map(skill => Object.assign({}, skill, { label: skill.skill, value: skill.skill, __isNew__: true })) || []);
            setProfessional(jobOffer.professional);
            setProfessionals(jobOffer.professionals || []);
            setHistory(jobOffer.history || []);
        }
    }, [jobOffer]); // Depend on jobOffer to re-run effect whenever it changes

    function removeDuplicatesByPropertyCaseInsensitive(array, property) {
        const uniqueItems = new Map();
        array.forEach(item => {
            const key = typeof item[property] === 'string'
                ? item[property].toLowerCase()
                : item[property];
            uniqueItems.set(key, item);
        });
        return Array.from(uniqueItems.values());
    }



    function handleRequiredSkill(changedSkills) {
        changedSkills = changedSkills.filter(skill => skill.value.trim() !== '').map(skill => ({ id: skill.id, label: skill.label.trim(), value: skill.value.trim(), __isNew__: true }));
        if (changedSkills.length > skills.length) {
            // add new elements
            setSkills(changedSkills);
        } else {
            // add removed elements to deleted ones
            let removedSkills = skills.filter(skill => !changedSkills.find((item) => item.value === skill.value))[0];
            setSkillsToDelete([...skillsToDelete, removedSkills]);
            // update skill without the removed elements
            setSkills(changedSkills);
        }
    }

    function handleDeletedSkill(changedSkills) {
        changedSkills = changedSkills.filter(skill => skill.value.trim() !== '').map(skill => ({ id: skill.id, label: skill.label.trim(), value: skill.value.trim(), __isNew__: true }));
        if (changedSkills.length < skillsToDelete.length) {
            // add back elements to skills
            let addedSkills = skillsToDelete.filter(skill => !changedSkills.find((item) => item.value === skill.value))[0];
            setSkills([...skills, addedSkills]);
            // update skillToDelete without the added elements
            setSkillsToDelete(changedSkills);
        } else {
            // add removed elements
            setSkillsToDelete(changedSkills);
        }
    }

    const handleOnSearchCustomers = async (string, results) => {
        // onSearch will have as the first callback parameter
        // the string searched and for the second the results.
        if (string !== '') {
            let searchedCustomers = await API.getCustomers(string, xsrfToken);
            setSearchedCustomers(searchedCustomers);//.map(customer => ({id: customer.id, name: customer.name})));
        }
    }

    const handleOnSelectCustomers = (item) => {
        // the item selected
        setCustomer(item);
    }

    const handleOnSearchProfessionals = async (string, results) => {
        // onSearch will have as the first callback parameter
        // the string searched and for the second the results.
        if (string !== '') {
            let searchedProfessionals = await API.getProfessionals(string, xsrfToken);
            setSearchedProfessionals(searchedProfessionals.map(professional => Object.assign({}, professional, { label: professional.name, value: professional.name })));
        }
    }

    const handleOnSelectProfessionals = (item) => {
        // the item selected
        setProfessionals(removeDuplicatesByPropertyCaseInsensitive([...professionals, item], 'id'));
    }

    const formatResult = (item) => {
        return (
            <><span style={{ display: 'block', textAlign: 'left' }}>{item.name}</span></>
        )
    }

    // CustomMultiValue components to customize the Select
    const CustomMultiValueSelect = (props) => {
        let professional = props.data;
        return (
            <components.MultiValue {...props} >
                <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <components.MultiValueLabel />
                    <components.MultiValueRemove {...props} />
                    <Button size="sm" onClick={() => { 
                        setProfessional(professional);
                        setProfessionals((oldProfessionals) => oldProfessionals.filter((p) => p.id !== professional.id));
                    }}>
                        Select for Placement
                    </Button>
                </span>
            </components.MultiValue>
        );
    };
    const CustomMultiValueDefinitive = (props) => {
        let professional = props.data;

        return (
            <components.MultiValue {...props}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <components.MultiValueRemove {...props} />
                    {
                        // else candidated because even if candidatedjoboffers does not contain the id on the frontend
                        // was added for that purpose (to be candidated)
                        (professional.abortedJobOffers.filter(abortedJobOffer => abortedJobOffer.id === id).length === 1) ?
                            <Badge bg="danger">Declined</Badge>
                            : <Badge bg="success">Candidated</Badge>}
                </span>
            </components.MultiValue>
        );
    };

    function handleProfessionals(newProfessionals) {
        setProfessionals(newProfessionals);
    }

    function handleJobOfferNumbers() {
        let valid = true;
        let time = duration.split(":")
        let hours = time[0];
        let minutes = time[1];
        if (!validator.isInt(hours) || !validator.isInt(minutes) || parseInt(minutes) >= 60) {
            setInvalidDuration(true);
            valid = false;
        } else {
            setInvalidDuration(false);
        }
        if (!validator.isInt(profitMargin.toString()) || parseInt(profitMargin.toString()) <= 0 || parseInt(profitMargin.toString()) > 100) {
            setInvalidProfitMargin(true);
            valid = false;
        } else {
            setInvalidProfitMargin(false);
        }
        return valid;
    }

    async function handleSubmit(event) {
        let valid = false;
        event.preventDefault();
        event.stopPropagation();
        try {
            // FORM VALIDATION:
            // currentStateNote is optional, so it is not validated
            // skills and skillstodelete need no validation
            if (handleJobOfferNumbers() && name.length > 0 && description.length > 0 && customer.id) {
                valid = true;
            }

            if (valid) {
                // convert all in minutes to send
                let time = duration.split(":")
                let hours = time[0];
                let minutes = time[1];
                console.log(parseInt(parseInt(hours) * 60 + parseInt(minutes)))
                let jobOffer = {
                    name: name.trim(),
                    description: description.trim(),
                    currentState: currentState,
                    currentStateNote: (currentStateNote) ? currentStateNote.trim() : null,
                    duration: parseInt(parseInt(hours) * 60 + parseInt(minutes)),
                    profitMargin: parseInt(profitMargin.trim()),
                    customerId: customer.id,
                    professionalId: professional && professional.id ? professional.id : null,
                    skills: skills.filter(skill => skill.id === undefined).map(skill => ({ skill: skill.value })),
                    skillsToDelete: skillsToDelete.filter(skill => skill.id !== undefined).map(skill => skill.id)
                };

                if (mode === 'edit') {
                    await API.updateJobOfferById(jobOffer, id, xsrfToken);
                    // handle also joboffer status
                    let updateStatus = {
                        targetStatus: currentState,
                        note: (currentStateNote) ? currentStateNote.trim() : null,
                        consolidatedProfessionalId: professional && professional.id ? professional.id : null,
                        professionalsId: professionals.map(professional => professional.id)
                    }
                    console.log(updateStatus)
                    await API.updateJobOfferStatusbyId(updateStatus, id, xsrfToken);
                } else {
                    await API.createJobOffer(jobOffer, xsrfToken);
                }
                navigate("/ui/jobOffers");
            }
        } catch (err) {
            console.log(err)
            // display error message
            handleErrors(err);
        }
        setValidated(true);
    };

    return (
        <Form noValidate onSubmit={handleSubmit}>
            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control value={name} isInvalid={name.length === 0 && validated} onChange={e => setName(e.target.value)} required placeholder="Enter JobOffer Name" disabled={readOnlyBoolean} />
                    <Form.Control.Feedback type="invalid">Please insert JobOffer Name.</Form.Control.Feedback>
                </Form.Group>

                <Form.Group as={Col} controlId="formGridDuration">
                    <Form.Label>Duration (Working Hours)</Form.Label>
                    {(readOnlyBoolean) ?
                        <Form.Control type="text" value={duration.replace(/^0*(\d{1,2}):/, (match, p1) => p1.padStart(2, '0') + ':')} disabled={readOnlyBoolean} />
                        :
                        <InputMask
                            mask="9999:99"
                            value={duration}
                            onChange={e => setDuration(e.target.value)}
                            placeholder="Enter JobOffer Duration"
                            required
                            className={`form-control ${(invalidDuration) ? 'is-invalid' : ''}`}
                            name="duration"
                        />}
                    <Form.Control.Feedback type="invalid">Please insert the duration in the format HHHH:MM, e.g., 0036:30 for 36 hours and 30 minutes.</Form.Control.Feedback>
                </Form.Group>

                <Form.Group as={Col} controlId="formGridProfit">
                    <Form.Label>Profit Margin (in Percentage)</Form.Label>
                    <Form.Control type="number" step="1" min={1} max={100} isInvalid={invalidProfitMargin} required value={profitMargin} onChange={e => setProfitMargin(e.target.value)} placeholder="Enter JobOffer Profit Margin" disabled={readOnlyBoolean} />
                    <Form.Control.Feedback type="invalid">Please insert a valid positive number as JobOffer Profit Margin.</Form.Control.Feedback>
                </Form.Group>
            </Row>

            <Row className="mb-3">
                <Form.Group className="mb-3" as={Col} controlId="formGridState">
                    <Form.Label>JobOffer Current State</Form.Label>
                    <Form.Select value={currentState} onChange={e => setCurrentState(e.target.value)} disabled={readOnlyBoolean || mode === 'add'}>
                        <option>created</option>
                        <option>selection_phase</option>
                        <option>candidate_proposal</option>
                        <option>consolidated</option>
                        <option>done</option>
                        <option>aborted</option>
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">Please insert JobOffer Current State.</Form.Control.Feedback>
                </Form.Group>

                <Form.Group as={Col} className="mb-3" controlId="formGridDescription">
                    <Form.Label>JobOffer Current State Note</Form.Label>
                    <Form.Control value={currentStateNote} onChange={e => setCurrentStateNote(e.target.value)} as="textarea" rows={1} placeholder="Enter Current State Note" disabled={readOnlyBoolean} />
                </Form.Group>
            </Row>

            <Form.Group className="mb-3" controlId="formGridDescription">
                <Form.Label>JobOffer Description</Form.Label>
                <Form.Control value={description} isInvalid={description.length === 0 && validated} onChange={e => setDescription(e.target.value)} required as="textarea" rows={3} placeholder="Enter JobOffer Description" disabled={readOnlyBoolean} />
                <Form.Control.Feedback type="invalid">Please insert JobOffer Description.</Form.Control.Feedback>
            </Form.Group>

            <Row className="mb-3 justify-content-center align-items-center">
                {(mode !== 'view') ?
                    <Form.Group className="mb-3" as={Col} controlId="formGridSearchCustomer">
                        <Form.Label>Find Customer</Form.Label>
                        <ReactSearchAutocomplete placeholder="Search ..." items={searchedCustomers} onSearch={handleOnSearchCustomers} onSelect={handleOnSelectCustomers} autoFocus formatResult={formatResult} />
                    </Form.Group> : ''}
                <Form.Group className="mb-3" as={Col} controlId="formGridCustomer">
                    <Form.Label>Customer Selected</Form.Label>
                    <InputGroup>
                        <Form.Control
                            style={{ color: '#0d6efd', textDecoration: 'underline', cursor: 'pointer' }}
                            onClick={() => { if (customer.id) window.open(`/ui/customers/${customer.id}`, '_blank'); }}
                            type="text"
                            value={customer.name}
                            isInvalid={customer.id === undefined && validated}
                            required
                            onKeyDown={(e) => { e.preventDefault(); e.stopPropagation(); }}
                        />
                        {(mode !== 'view') ?
                        <InputGroup.Text>
                            <CloseButton disabled={readOnlyBoolean || currentState === 'consolidated' || currentState === 'done'} onClick={() => setCustomer({})} />
                        </InputGroup.Text> : ''}
                        <Form.Control.Feedback type="invalid">Please find The Customer.</Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>
            </Row>

            {(professional && professional.id && currentState !== 'created' && currentState !== 'selection_phase') ?
                <Row className="mb-3 justify-content-center align-items-center">
                    {(mode !== 'view') ?
                        <Form.Group className="mb-3" as={Col} controlId="formGridSearchProfessionals">
                            <Form.Label>Find Candidate Professionals</Form.Label>
                            <ReactSearchAutocomplete placeholder="Search ..." items={searchedProfessionals} onSearch={handleOnSearchProfessionals} onSelect={handleOnSelectProfessionals} autoFocus formatResult={formatResult} />
                        </Form.Group> : ''}
                    <Form.Group className="mb-3" as={Col} controlId="formGridChosenProfessional">
                        <Form.Label>ChosenProfessional</Form.Label>
                        <InputGroup>
                            <Form.Control
                                value={professional.name}
                                type="text"
                                onKeyDown={(e) => { e.preventDefault(); e.stopPropagation(); }}
                                onClick={() => { if (professional.id) window.open(`/ui/professionals/${professional.id}`, '_blank'); }}
                                //disabled={readOnlyBoolean}
                                style={{ color: '#0d6efd', textDecoration: 'underline', cursor: 'pointer' }}
                            />
                            {(mode !== 'view') ?
                            <InputGroup.Text>
                                <CloseButton disabled={readOnlyBoolean || currentState === 'consolidated' || currentState === 'done'} onClick={() => {setProfessionals((oldProfessionals) => [...oldProfessionals,professional]); setProfessional({});}} />
                            </InputGroup.Text> : ''}
                        </InputGroup>
                    </Form.Group>
                    <Form.Group className="mb-3" as={Col} controlId="formGridProfessionals">
                        <Form.Label>Professionals Selected</Form.Label>
                        <Select onChange={(event) => handleProfessionals(event)} components={{ MultiValue: CustomMultiValueDefinitive }} value={professionals/* TODO professionals.filter((p) => p.id !== professional.id)*/} isMulti className="basic-multi-select" classNamePrefix="select" isDisabled={readOnlyBoolean} />
                    </Form.Group>
                </Row>
                :
                <Row className="mb-3 justify-content-center align-items-center">
                    {(mode !== 'view' && currentState !== 'created' && currentState !== 'selection_phase') ?
                        <Form.Group className="mb-3" as={Col} controlId="formGridSearchProfessionals">
                            <Form.Label>Find Candidate Professionals</Form.Label>
                            <ReactSearchAutocomplete placeholder="Search ..." items={searchedProfessionals} onSearch={handleOnSearchProfessionals} onSelect={handleOnSelectProfessionals} autoFocus formatResult={formatResult} />
                        </Form.Group> : ''}
                    {(currentState !== 'created' && currentState !== 'selection_phase') ?
                    <Form.Group className="mb-3" as={Col} controlId="formGridProfessionals">
                        <Form.Label>Candidate Professionals Selected</Form.Label>
                        <Select onChange={(event) => handleProfessionals(event)} components={(mode === 'edit') ? { MultiValue: CustomMultiValueSelect }: {}} value={professionals} isMulti className="basic-multi-select" classNamePrefix="select" isDisabled={readOnlyBoolean} />
                    </Form.Group> : ''}
                </Row>
            }

            <Row className="mb-3">
                <Form.Group as={Col} controlId="formGridRequiredSkills">
                    <Form.Label>Required Skills</Form.Label>
                    <CreatableSelect isMulti value={skills} options={skills} isDisabled={readOnlyBoolean} onChange={(event) => handleRequiredSkill(event)} />
                </Form.Group>

                {(mode !== 'view') ?
                    <Form.Group as={Col} controlId="formGridDeletedSkills">
                        <Form.Label>Deleted Skills</Form.Label>
                        <Select isMulti value={skillsToDelete} options={skillsToDelete} isDisabled={readOnlyBoolean} onChange={(event) => handleDeletedSkill(event)} />
                    </Form.Group> : ''}
            </Row>

            {(mode === 'view' && history.length > 0) ? (
                <>
                    <h4>JobOffer History:</h4>
                    <ListGroup>
                        {history
                            .sort((a, b) => new Date(b.date) - new Date(a.date)) // Sorts in descending order by date
                            .map((historyItem) => (
                                <Card key={historyItem.id}>
                                    <Card.Body>
                                        <Card.Title>{historyItem.state}</Card.Title>
                                        <Card.Subtitle className="mb-2 text-muted small">
                                            {new Date(historyItem.date).toLocaleString()}
                                        </Card.Subtitle>
                                        <Card.Text className="small">
                                            {historyItem.note || 'No notes available.'}
                                        </Card.Text>
                                    </Card.Body>
                                </Card>
                            ))}
                    </ListGroup>
                </>
            ) : ''}


            <Container className="d-flex justify-content-between">
                {(mode === 'view') ?
                    <Button variant="warning" onClick={() => setMode('edit')}>
                        Edit
                    </Button>
                    :
                    <Button variant="danger" onClick={() => navigate("/ui/jobOffers")}>
                        Cancel
                    </Button>}

                {(mode !== 'view') ?
                    <Button variant="primary" type="submit">
                        Submit
                    </Button> : ''}

            </Container>
        </Form>
    )
}

export { JobOfferContainer };