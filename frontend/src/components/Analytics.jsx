import React, {useContext, useEffect, useState} from "react";
import {MessageContext, TokenContext} from "../messageCtx.js";
import {Button, Col, Container, Row} from "react-bootstrap";
import {SideBar} from "./Utils.jsx";
import Select from "react-select";
import {AnalyticsContainer} from "./AnalyticsContainer.jsx";
import {useNavigate} from "react-router-dom";
import API from "../API.jsx";


function Analytics({loggedIn, role, unreadMessages, pending, setPending, setUnreadMessages}) {
    const navigate = useNavigate();
    const xsrfToken = useContext(TokenContext);
    const [isEmpty, setIsEmpty] = useState(false);
    const optionCharts = [
        {value: "Customers", label: "Customers job offers"},
        {value: "Customers KPI", label: "Customers KPI"},
        {value: "Professionals", label: "Professionals job offers"},
        {value: "Professionals KPI", label: "Professionals KPI"}
    ];
    const [selectedChart, setSelectedChart] = useState(optionCharts[0]);

    // Refresh unread messages and pending
    useEffect(() => {
        const fetchMessagesData = async () => {
            try {
                if (role === "manager") {
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

    if (!loggedIn || role !== "manager") {
        navigate("/ui");
    }
    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '90vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '100px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                    <h6> Select chart: </h6>
                    <Select
                        options={optionCharts}
                        value={selectedChart}
                        onChange={ev => { setSelectedChart(ev) }}
                        closeMenuOnSelect={true}
                        isSearchable={true}
                        isClearable={false}
                        styles={{
                            container: base => ({
                                ...base,
                                width: '250px'
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
                </Col>
                <Col>
                    {isEmpty ?
                        <div style={{ position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px" }}>
                            <h4> No Analytics yet! </h4>
                        </div>
                        :
                        <div style={{width: '90%', height: '90%'}}>
                            <AnalyticsContainer chartName={selectedChart.value} setIsEmpty={setIsEmpty} />
                        </div>
                    }
                </Col>
            </Row>
        </Container>
    );
}

export {Analytics};