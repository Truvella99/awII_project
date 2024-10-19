import React, {useCallback, useContext, useEffect, useMemo, useRef, useState} from "react";
import {Button, Col, Container, Row, Form} from "react-bootstrap";
import {SideBar} from "./Utils.jsx";
import {useNavigate} from "react-router-dom";
import {MessageContext, TokenContext} from "../messageCtx.js";
import API from "../API.jsx";
import {AgGridReact} from "ag-grid-react";
import Select from "react-select";
import makeAnimated from "react-select/animated";
import 'bootstrap/dist/css/bootstrap.css';
import 'react-bootstrap-range-slider/dist/react-bootstrap-range-slider.css';
import RangeSlider from "react-bootstrap-range-slider";


function Professionals({loggedIn}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [professionals, setProfessionals] = useState();
    const [optionSkills, setOptionSkills] = useState();
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [latitude, setLatitude] = useState();
    const [longitude, setLongitude] = useState();
    const [km, setKm] = useState(0);
    const [nothing, setNothing] = useState(false);
    const animatedComponents = makeAnimated();
    const gridRef = useRef();

    // Sort and filter in the table
    const sortAndFilter = (data, sortModel, filterModel) => {
        return sortData(sortModel, filterData(filterModel, data));
    };
    const sortData = (sortModel, data) => {
        const sortPresent = sortModel && sortModel.length > 0;
        if (!sortPresent) {
            return data;
        }
        const resultOfSort = data.slice();
        resultOfSort.sort(function (a, b) {
            for (let i = 0; i < sortModel.length; i++) {
                const sortColModel = sortModel[i];
                const valueA = a[sortColModel.colId];
                const valueB = b[sortColModel.colId];
                // if this filter didn't find a difference move onto the next one
                if (valueA === valueB) {
                    continue;
                }
                const sortDirection = sortColModel.sort === 'asc' ? 1 : -1;
                if (valueA > valueB) {
                    return sortDirection;
                } else {
                    return sortDirection * -1;
                }
            }
            // if no filters found a difference
            return 0;
        });
        return resultOfSort;
    };
    const filterData = (filterModel, data) => {
        const filterPresent = filterModel && Object.keys(filterModel).length > 0;
        if (!filterPresent) {
            return data;
        }
        const resultOfFilter = [];
        for (let i = 0; i < data.length; i++) {
            const item = data[i];

            if (filterModel.name) {
                if (filterModel.name.type === 'contains') {
                    if ( !((item.name.toLowerCase()).includes(filterModel.name.filter)) )
                        continue;
                } else if (filterModel.name.type === 'notContains') {
                    if ( (item.name.toLowerCase()).includes(filterModel.name.filter) )
                        continue;
                }
            }
            if (filterModel.surname) {
                if (filterModel.surname.type === 'contains') {
                    if ( !((item.surname.toLowerCase()).includes(filterModel.surname.filter)) )
                        continue;
                } else if (filterModel.surname.type === 'notContains') {
                    if ( (item.surname.toLowerCase()).includes(filterModel.surname.filter) )
                        continue;
                }
            }
            if (filterModel.ssncode) {
                if (filterModel.ssncode.type === 'contains') {
                    if ( !((item.ssncode.toLowerCase()).includes(filterModel.ssncode.filter)) )
                        continue;
                } else if (filterModel.ssncode.type === 'notContains') {
                    if ( (item.ssncode.toLowerCase()).includes(filterModel.ssncode.filter) )
                        continue;
                }
            }
            if (filterModel.email) {
                if (filterModel.email.type === 'contains') {
                    if ( !((item.email.toLowerCase()).includes(filterModel.email.filter)) )
                        continue;
                } else if (filterModel.email.type === 'notContains') {
                    if ( (item.email.toLowerCase()).includes(filterModel.email.filter) )
                        continue;
                }
            }
            if (filterModel.telephone) {
                if (filterModel.telephone.type === 'contains') {
                    if ( !((item.telephone.toLowerCase()).includes(filterModel.telephone.filter)) )
                        continue;
                } else if (filterModel.telephone.type === 'notContains') {
                    if ( (item.telephone.toLowerCase()).includes(filterModel.telephone.filter) )
                        continue;
                }
            }
            if (filterModel.address) {
                if (filterModel.address.type === 'contains') {
                    if ( !((item.address.toLowerCase()).includes(filterModel.address.filter)) )
                        continue;
                } else if (filterModel.address.type === 'notContains') {
                    if ( (item.address.toLowerCase()).includes(filterModel.address.filter) )
                        continue;
                }
            }
            if (filterModel.dailyRate) {
                const dailyRate = item.dailyRate;
                const allowedDailyRate = parseInt(filterModel.dailyRate.filter);
                if (filterModel.dailyRate.type === 'equals') {
                    if (dailyRate !== allowedDailyRate) {
                        continue;
                    }
                } else if (filterModel.dailyRate.type === 'lessThan') {
                    if (dailyRate >= allowedDailyRate) {
                        continue;
                    }
                } else if (filterModel.dailyRate.type === 'greaterThan') {
                    if (dailyRate <= allowedDailyRate) {
                        continue;
                    }
                } else if (filterModel.dailyRate.type === 'inRange') {
                    if ((dailyRate < parseInt(filterModel.dailyRate.filter)) || (dailyRate > parseInt(filterModel.dailyRate.filterTo))) {
                        continue;
                    }
                }
            }
            if (filterModel.employmentState) {
                if (filterModel.employmentState.type === 'contains') {
                    if ( !((item.employmentState.toLowerCase()).includes(filterModel.employmentState.filter)) )
                        continue;
                } else if (filterModel.employmentState.type === 'notContains') {
                    if ( (item.employmentState.toLowerCase()).includes(filterModel.employmentState.filter) )
                        continue;
                }
            }

            resultOfFilter.push(item);
        }
        return resultOfFilter;
    };

    // Table settings
    const containerStyle = useMemo(() => ({ width: "100%", height: "100%" }), []);
    const gridStyle = useMemo(() => ({ height: "100%", width: "100%" }), []);
    const stateFormatter = (params) => {
        if (params.value === "available")
            return "Available";
        else if (params.value === "employed")
            return "Employed";
        else if (params.value === "not_available")
            return "Not available";
        else
            return "";
    };
    const [columnDefs, setColumnDefs] = useState([
        { field: "name", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true,
            cellRenderer: (props) => {
                if (props.value === undefined) {
                    return (
                        <div style={{height: '33px', width: '33px', display: 'flex', alignItems: 'center', paddingTop: '10px'}}>
                            <img style={{maxHeight: '100%', maxWidth: '100%', paddingRight: '5px'}} src="https://media.tenor.com/-n8JvVIqBXkAAAAM/dddd.gif"/>
                            <span> Loading... </span>
                        </div>
                    );
                } else {
                    return props.value;
                }
            },
        },
        { field: "surname", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true },
        { field: "ssncode", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, headerName: "SSN", suppressHeaderMenuButton: true },
        { field: "email", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true },
        { field: "telephone", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true },
        { field: "address", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true },
        { field: "dailyRate", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "Daily Rate", suppressHeaderMenuButton: true },
        { field: "employmentState", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, headerName: "Employment State", suppressHeaderMenuButton: true, valueFormatter: stateFormatter }
    ]);
    const defaultColDef = useMemo(() => {
        return {
            flex: 1,
            minWidth: 150,
            floatingFilter: true,
            cellStyle: {fontSize: '15px'}
        };
    }, []);
    const gridOptions = {
        suppressCellFocus: true
    };
    const getRowId = useCallback(function (params) {
        return params.data.id;
    }, []);

    // Table data functions
    const filterSkills = async (skills) => {
        try {
            if (loggedIn) {
                const professionals = await API.getProfessionalSkills(skills, xsrfToken);
                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
                        setTimeout(function () {
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                professionals,
                                params.sortModel,
                                params.filterModel
                            );
                            const rowsThisPage = dataAfterSortingAndFiltering.slice(params.startRow, params.endRow);
                            // if on or after the last page, work out the last row.
                            let lastRow = -1;
                            if (dataAfterSortingAndFiltering.length <= params.endRow) {
                                lastRow = dataAfterSortingAndFiltering.length;
                            }
                            // call the success callback
                            params.successCallback(rowsThisPage, lastRow);
                        }, 500);
                    },
                };
                gridRef.current?.api.setGridOption('datasource', dataSource);

            } else {
                navigate("/ui")
            }
        } catch (error) {
            console.log(error);
            handleError(error);
        }
    };

    const filterDistance = async () => {
        try {
            if (loggedIn) {
                const skills = selectedSkills.map(skill => skill.value);
                // console.log(latitude, longitude);
                const professionals = await API.getProfessionalsDistance(skills, latitude, longitude, km, xsrfToken);
                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
                        setTimeout(function () {
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                professionals,
                                params.sortModel,
                                params.filterModel
                            );
                            const rowsThisPage = dataAfterSortingAndFiltering.slice(params.startRow, params.endRow);
                            // if on or after the last page, work out the last row.
                            let lastRow = -1;
                            if (dataAfterSortingAndFiltering.length <= params.endRow) {
                                lastRow = dataAfterSortingAndFiltering.length;
                            }
                            // call the success callback
                            params.successCallback(rowsThisPage, lastRow);
                        }, 500);
                    },
                };
                gridRef.current?.api.setGridOption('datasource', dataSource);

            } else {
                navigate("/ui")
            }
        } catch (error) {
            console.log(error);
            handleError(error);
        }
    };

    const onGridReady = useCallback((params) => {
        const fetchProfessionals = async (params) => {
            try {
                if (loggedIn) {
                    const professionals = await API.getAllProfessionals(xsrfToken);
                    // console.log(professionals);
                    const uniqueSkills = [...new Set(professionals.map(professional => professional.skills.map(s => s.skill)).flat())].map(skill => ({value: skill, label: skill}));
                    // console.log(uniqueSkills);
                    setOptionSkills(uniqueSkills);

                    const modifiedProfessionals = professionals.map(professional => {
                        return {
                            ...professional,
                            email: professional.emails && professional.emails.length > 0
                                ? professional.emails[0].email
                                : null,
                            telephone: professional.telephones && professional.telephones.length > 0
                                ? professional.telephones[0].telephone
                                : null,
                            address: professional.addresses && professional.addresses.length > 0
                                ? professional.addresses[0].address
                                : null
                        };
                    });
                    modifiedProfessionals.forEach(professional => {
                        delete professional.emails;
                        delete professional.telephones;
                        delete professional.addresses;
                    });
                    setProfessionals(modifiedProfessionals);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
                            setTimeout(function () {
                                // take a slice of the total rows
                                const dataAfterSortingAndFiltering = sortAndFilter(
                                    modifiedProfessionals,
                                    params.sortModel,
                                    params.filterModel
                                );
                                const rowsThisPage = dataAfterSortingAndFiltering.slice(params.startRow, params.endRow);
                                // if on or after the last page, work out the last row.
                                let lastRow = -1;
                                if (dataAfterSortingAndFiltering.length <= params.endRow) {
                                    lastRow = dataAfterSortingAndFiltering.length;
                                }
                                // call the success callback
                                params.successCallback(rowsThisPage, lastRow);

                                // If there are no professionals, display the message
                                if (professionals.length === 0) {
                                    setNothing(true);
                                }
                            }, 500);
                        },
                    };
                    params.api.setGridOption('datasource', dataSource);

                } else {
                    navigate("/ui")
                }
            } catch (error) {
                console.log(error);
                handleError(error);
            }
        };

        // Get current position
        navigator.geolocation.getCurrentPosition((position) => {
            setLatitude(position.coords.latitude);
            setLongitude(position.coords.longitude);
        });

        // Fetch professionals
        fetchProfessionals(params);
    }, []);


    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '80vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar/>
                    </div>
                    <Row style={{marginBottom: '100px'}}>
                        <Col className="d-flex justify-content-center">
                            <Button variant="info" onClick={() => navigate('/ui/professionals/addProfessional')}> <i className="bi bi-plus-lg"></i> Add professional </Button>
                        </Col>
                    </Row>
                    <h6> Filter by skills: </h6>
                    <Select
                        options={optionSkills}
                        value={selectedSkills}
                        onChange={ev => {
                            setSelectedSkills(ev);
                            filterSkills(ev.map(skill => skill.value));
                        }}
                        isMulti
                        closeMenuOnSelect={true}
                        isSearchable={true}
                        isClearable={true}
                        placeholder="Choose or search skills"
                        components={animatedComponents}
                        // theme={(theme) => ({
                        //     ...theme,
                        //     colors: {
                        //         ...theme.colors,
                        //         primary25: '#D1E7DD',
                        //         primary: '#34ce57',
                        //     },
                        // })}
                    />
                    <div style={{paddingTop: '50px'}}>
                        <h6> Filter near me: </h6>
                    </div>
                    <div>
                        <RangeSlider tooltip={"auto"} tooltipPlacement={"top"} value={km} min={0} max={50} onChange={(event) => setKm(event.target.value)}/>
                    </div>
                    <Row>
                        <div className="d-flex justify-content-between">
                            <span> 0 km </span>
                            <span> 50 km </span>
                        </div>
                    </Row>
                    <Row>
                        <Col className="d-flex justify-content-center">
                            <Button variant="primary" size={"sm"} onClick={filterDistance} disabled={nothing}> Search </Button>
                        </Col>
                    </Row>
                </Col>
                <Col>
                { nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Professionals yet! </h4>
                        </div>
                    }
                    <div style={containerStyle}>
                        <div style={gridStyle} className={"ag-theme-quartz"}>
                            <AgGridReact
                                ref={gridRef}
                                columnDefs={columnDefs}
                                defaultColDef={defaultColDef}
                                gridOptions={gridOptions}
                                rowStyle = {{cursor:'pointer'}}
                                rowModelType={'infinite'}
                                cacheBlockSize={100}
                                cacheOverflowSize={2}
                                maxConcurrentDatasourceRequests={2}
                                infiniteInitialRowCount={1}
                                maxBlocksInCache={2}
                                pagination={true}
                                paginationAutoPageSize={true}
                                getRowId={getRowId}
                                onGridReady={onGridReady}
                                onRowClicked={useCallback((event) => {navigate(`/ui/professionals/${event.node.data.id}`)}, [])}
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {Professionals};