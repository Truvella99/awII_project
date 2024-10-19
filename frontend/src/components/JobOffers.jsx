import React, {useCallback, useContext, useEffect, useMemo, useRef, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {SideBar} from "./Utils.jsx";
import {useNavigate} from "react-router-dom";
import {MessageContext, TokenContext} from "../messageCtx.js";
import API from "../API.jsx";
import {AgGridReact} from "ag-grid-react";
import Select from "react-select";
import makeAnimated from "react-select/animated";


function JobOffers({loggedIn}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [jobOffers, setJobOffers] = useState();
    const [optionSkills, setOptionSkills] = useState();
    const [selectedSkills, setSelectedSkills] = useState([]);
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
            if (filterModel.duration) {
                const duration = item.duration;
                const allowedDuration = parseInt(filterModel.duration.filter);
                if (filterModel.duration.type === 'equals') {
                    if (duration !== allowedDuration) {
                        continue;
                    }
                } else if (filterModel.duration.type === 'lessThan') {
                    if (duration >= allowedDuration) {
                        continue;
                    }
                } else if (filterModel.duration.type === 'greaterThan') {
                    if (duration <= allowedDuration) {
                        continue;
                    }
                } else if (filterModel.duration.type === 'inRange') {
                    if ((duration < parseInt(filterModel.duration.filter)) || (duration > parseInt(filterModel.duration.filterTo))) {
                        continue;
                    }
                }
            }
            if (filterModel.profitMargin) {
                const profitMargin = item.profitMargin;
                const allowedProfitMargin = parseInt(filterModel.profitMargin.filter);
                if (filterModel.profitMargin.type === 'equals') {
                    if (profitMargin !== allowedProfitMargin) {
                        continue;
                    }
                } else if (filterModel.profitMargin.type === 'lessThan') {
                    if (profitMargin >= allowedProfitMargin) {
                        continue;
                    }
                } else if (filterModel.profitMargin.type === 'greaterThan') {
                    if (profitMargin <= allowedProfitMargin) {
                        continue;
                    }
                } else if (filterModel.profitMargin.type === 'inRange') {
                    if ((profitMargin < parseInt(filterModel.profitMargin.filter)) || (profitMargin > parseInt(filterModel.profitMargin.filterTo))) {
                        continue;
                    }
                }
            }
            if (filterModel.nCandidates) {
                const nCandidates = item.nCandidates;
                const allowedNCandidates = parseInt(filterModel.nCandidates.filter);
                if (filterModel.nCandidates.type === 'equals') {
                    if (nCandidates !== allowedNCandidates) {
                        continue;
                    }
                } else if (filterModel.nCandidates.type === 'lessThan') {
                    if (nCandidates >= allowedNCandidates) {
                        continue;
                    }
                } else if (filterModel.nCandidates.type === 'greaterThan') {
                    if (nCandidates <= allowedNCandidates) {
                        continue;
                    }
                } else if (filterModel.nCandidates.type === 'inRange') {
                    if ((nCandidates < parseInt(filterModel.nCandidates.filter)) || (nCandidates > parseInt(filterModel.nCandidates.filterTo))) {
                        continue;
                    }
                }
            }
            if (filterModel.currentState) {
                if (filterModel.currentState.type === 'contains') {
                    if ( !((item.currentState.toLowerCase()).includes(filterModel.currentState.filter)) )
                        continue;
                } else if (filterModel.currentState.type === 'notContains') {
                    if ( (item.currentState.toLowerCase()).includes(filterModel.currentState.filter) )
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
        if (params.value === "created")
            return "Created";
        else if (params.value === "aborted")
            return "Aborted";
        else if (params.value === "selection_phase")
            return "Selection Phase";
        else if (params.value === "candidate_proposal")
            return "Candidate Proposal";
        else if (params.value === "consolidated")
            return "Consolidated";
        else if (params.value === "done")
            return "Done";
        else
            return "";
    };
    const [columnDefs, setColumnDefs] = useState([
        { field: "name", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, headerName: "Job", suppressHeaderMenuButton: true,
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
        { field: "duration", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, suppressHeaderMenuButton: true },
        { field: "profitMargin", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "Profit Margin", suppressHeaderMenuButton: true },
        { field: "nCandidates", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "N. Candidates", suppressHeaderMenuButton: true },
        { field: "currentState", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, headerName: "Current State", suppressHeaderMenuButton: true, valueFormatter: stateFormatter }
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
                const jobOffers = await API.getJobOfferSkills(skills, xsrfToken);
                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
                        setTimeout(function () {
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                jobOffers,
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
        const fetchJobOffers = async (params) => {
            try {
                if (loggedIn) {
                    const jobOffers = await API.getAllJobOffers(xsrfToken);
                    // console.log(jobOffers);
                    const uniqueSkills = [...new Set(jobOffers.map(jobOffer => jobOffer.skills.map(s => s.skill)).flat())].map(skill => ({value: skill, label: skill}));
                    // console.log(uniqueSkills);
                    setOptionSkills(uniqueSkills);

                    const modifiedJobOffers = jobOffers.map(jobOffer => {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    });
                    setJobOffers(modifiedJobOffers);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
                            setTimeout(function () {
                                // take a slice of the total rows
                                const dataAfterSortingAndFiltering = sortAndFilter(
                                    modifiedJobOffers,
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

                                // If there are no job offers, display the message
                                if (jobOffers.length === 0) {
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

        fetchJobOffers(params);
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
                            <Button variant="info" onClick={() => navigate('/ui/jobOffers/addJobOffer')}> <i className="bi bi-plus-lg"></i> Add job offer </Button>
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
                </Col>
                <Col>
                    { nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Job Offers yet! </h4>
                        </div>
                    }
                    <div style={containerStyle}>
                        <div style={gridStyle} className={"ag-theme-quartz"}>
                            <AgGridReact
                                ref={gridRef}
                                columnDefs={columnDefs}
                                defaultColDef={defaultColDef}
                                gridOptions={gridOptions}
                                rowStyle={{cursor: 'pointer'}}
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
                                onRowClicked={useCallback((event) => {navigate(`/ui/jobOffers/${event.node.data.id}`)}, [])}
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {JobOffers};