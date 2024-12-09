import React, {useCallback, useContext, useEffect, useMemo, useRef, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {CustomLoadingOverlay, SideBar} from "./Utils.jsx";
import {useNavigate} from "react-router-dom";
import {MessageContext, TokenContext} from "../messageCtx.js";
import API from "../API.jsx";
import {AgGridReact} from "ag-grid-react";
import Select from "react-select";
import makeAnimated from "react-select/animated";
import {ModuleRegistry} from "@ag-grid-community/core";
import {InfiniteRowModelModule} from "@ag-grid-community/infinite-row-model";

ModuleRegistry.registerModules([InfiniteRowModelModule]);


function JobOffers({loggedIn, role, unreadMessages, pending}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [jobOffers, setJobOffers] = useState();
    const [optionSkills, setOptionSkills] = useState();
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [optionCandidates, setOptionCandidates] = useState();
    const [selectedCandidates, setSelectedCandidates] = useState([]);
    const [optionAborted, setOptionAborted] = useState();
    const [selectedAborted, setSelectedAborted] = useState([]);
    const [optionConsolidated, setOptionConsolidated] = useState();
    const [selectedConsolidated, setSelectedConsolidated] = useState([]);
    const [optionCompleted, setOptionCompleted] = useState();
    const [selectedCompleted, setSelectedCompleted] = useState([]);
    const [nothing, setNothing] = useState(false);
    const [tableReady, setTableReady] = useState(false);
    const animatedComponents = makeAnimated();
    const filtersRef = useRef([]);
    const containerRef = useRef(null);
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
            if (filterModel.value) {
                const value = item.value;
                const allowedValue = parseInt(filterModel.value.filter);
                if (filterModel.value.type === 'equals') {
                    if (value !== allowedValue) {
                        continue;
                    }
                } else if (filterModel.value.type === 'lessThan') {
                    if (value >= allowedValue) {
                        continue;
                    }
                } else if (filterModel.value.type === 'greaterThan') {
                    if (value <= allowedValue) {
                        continue;
                    }
                } else if (filterModel.value.type === 'inRange') {
                    if ((value < parseInt(filterModel.value.filter)) || (value > parseInt(filterModel.value.filterTo))) {
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
    const durationFormatter = (params) => {
        if (params.value) {
            let days = Math.floor(params.value / 24);
            let hours = params.value % 24;
            return `${days.toString()}d ${hours.toString()}h`;
        } else
            return ""
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
        { field: "duration", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, suppressHeaderMenuButton: true, valueFormatter: durationFormatter },
        { field: "profitMargin", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "Profit Margin", suppressHeaderMenuButton: true },
        { field: "nCandidates", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "N. Candidates", suppressHeaderMenuButton: true },
        { field: "value", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, suppressHeaderMenuButton: true },
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
        setTableReady(false);
        try {
            if (loggedIn) {
                let jobOffers;
                const candidateProfessionals = selectedCandidates.map(professional => professional.value);
                const abortedProfessionals = selectedAborted.map(professional => professional.value);
                const consolidatedProfessionals = selectedConsolidated.map(professional => professional.value);
                const completedProfessionals = selectedCompleted.map(professional => professional.value);
                // Role check
                if (role === "manager" || role === "operator")
                    jobOffers = await API.getJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken);
                else if (role === "professional")
                    jobOffers = await API.getOpenJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken);

                const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                    if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                        const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                        return {
                            ...jobOffer,
                            value: value,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    } else {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    }
                }));
                setJobOffers(modifiedJobOffers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
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
                        //
                        setTableReady(true);
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
    const filterCandidates = async (candidates) => {
        setTableReady(false);
        try {
            if (loggedIn) {
                let jobOffers;
                const skills = selectedSkills.map(skill => skill.value);
                const abortedProfessionals = selectedAborted.map(professional => professional.value);
                const consolidatedProfessionals = selectedConsolidated.map(professional => professional.value);
                const completedProfessionals = selectedCompleted.map(professional => professional.value);
                // Role check
                if (role === "manager" || role === "operator")
                    jobOffers = await API.getJobOfferSkillsProfessionals(skills, candidates, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken);
                else if (role === "professional")
                    jobOffers = await API.getOpenJobOfferSkillsProfessionals(skills, candidates, abortedProfessionals, consolidatedProfessionals, completedProfessionals, xsrfToken);

                const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                    if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                        const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                        return {
                            ...jobOffer,
                            value: value,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    } else {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    }
                }));
                setJobOffers(modifiedJobOffers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
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
                        //
                        setTableReady(true);
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
    const filterAborteds = async (aborteds) => {
        setTableReady(false);
        try {
            if (loggedIn) {
                let jobOffers;
                const skills = selectedSkills.map(skill => skill.value);
                const candidateProfessionals = selectedCandidates.map(professional => professional.value);
                const consolidatedProfessionals = selectedConsolidated.map(professional => professional.value);
                const completedProfessionals = selectedCompleted.map(professional => professional.value);
                // Role check
                if (role === "manager" || role === "operator")
                    jobOffers = await API.getJobOfferSkillsProfessionals(skills, candidateProfessionals, aborteds, consolidatedProfessionals, completedProfessionals, xsrfToken);
                else if (role === "professional")
                    jobOffers = await API.getOpenJobOfferSkillsProfessionals(skills, candidateProfessionals, aborteds, consolidatedProfessionals, completedProfessionals, xsrfToken);

                const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                    if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                        const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                        return {
                            ...jobOffer,
                            value: value,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    } else {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    }
                }));
                setJobOffers(modifiedJobOffers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
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
                        //
                        setTableReady(true);
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
    const filterConsolidated = async (consolidated) => {
        setTableReady(false);
        try {
            if (loggedIn) {
                let jobOffers;
                const skills = selectedSkills.map(skill => skill.value);
                const candidateProfessionals = selectedCandidates.map(professional => professional.value);
                const abortedProfessionals = selectedAborted.map(professional => professional.value);
                const completedProfessionals = selectedCompleted.map(professional => professional.value);
                // Role check
                if (role === "manager" || role === "operator")
                    jobOffers = await API.getJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidated, completedProfessionals, xsrfToken);
                else if (role === "professional")
                    jobOffers = await API.getOpenJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidated, completedProfessionals, xsrfToken);

                const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                    if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                        const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                        return {
                            ...jobOffer,
                            value: value,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    } else {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    }
                }));
                setJobOffers(modifiedJobOffers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
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
                        //
                        setTableReady(true);
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
    const filterCompleted = async (completed) => {
        setTableReady(false);
        try {
            if (loggedIn) {
                let jobOffers;
                const skills = selectedSkills.map(skill => skill.value);
                const candidateProfessionals = selectedCandidates.map(professional => professional.value);
                const abortedProfessionals = selectedAborted.map(professional => professional.value);
                const consolidatedProfessionals = selectedConsolidated.map(professional => professional.value);
                // Role check
                if (role === "manager" || role === "operator")
                    jobOffers = await API.getJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completed, xsrfToken);
                else if (role === "professional")
                    jobOffers = await API.getOpenJobOfferSkillsProfessionals(skills, candidateProfessionals, abortedProfessionals, consolidatedProfessionals, completed, xsrfToken);

                const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                    if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                        const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                        return {
                            ...jobOffer,
                            value: value,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    } else {
                        return {
                            ...jobOffer,
                            nCandidates: jobOffer.candidateProfessionalsId.length
                        };
                    }
                }));
                setJobOffers(modifiedJobOffers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
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
                        //
                        setTableReady(true);
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
                    let jobOffers;
                    let candidateIds;
                    let abortedIds;
                    let consolidatedIds;
                    let completedIds;
                    // Role check
                    if (role === "manager" || role === "operator")
                        jobOffers = await API.getAllJobOffers(xsrfToken);
                    else if (role === "professional")
                        jobOffers = await API.getOpenJobOffers(xsrfToken);
                    console.log(jobOffers);
                    // Professionals for filtering
                    candidateIds = [...new Set(jobOffers.flatMap(jobOffer => jobOffer.candidateProfessionalsId || []))];
                    abortedIds = [...new Set(jobOffers.flatMap(jobOffer => jobOffer.abortedProfessionalsId || []))];
                    consolidatedIds = [...new Set(jobOffers.map(jobOffer => jobOffer.consolidatedProfessionalId).filter(id => id != null))];
                    completedIds = [...new Set(jobOffers.map(jobOffer => jobOffer.completedProfessionalId).filter(id => id != null))];
                    const professionals = await API.getProfessionalsInfo(candidateIds, abortedIds, consolidatedIds, completedIds, xsrfToken);
                    // console.log(professionals);
                    setOptionCandidates(professionals.candidate.map(professional => ({value: professional.first, label: professional.second})));
                    setOptionAborted(professionals.aborted.map(professional => ({value: professional.first, label: professional.second})));
                    setOptionConsolidated(professionals.consolidated.map(professional => ({value: professional.first, label: professional.second})));
                    setOptionCompleted(professionals.completed.map(professional => ({value: professional.first, label: professional.second})));

                    // Skills for filtering
                    const uniqueSkills = [...new Set(jobOffers.map(jobOffer => jobOffer.skills.map(s => s.skill)).flat())].map(skill => ({value: skill, label: skill}));
                    // console.log(uniqueSkills);
                    setOptionSkills(uniqueSkills);

                    const modifiedJobOffers = await Promise.all(jobOffers.map(async jobOffer => {
                        if (jobOffer.completedProfessionalId || jobOffer.consolidatedProfessionalId) {
                            const value = await API.getJobOfferValueById(jobOffer.id, xsrfToken);
                            return {
                                ...jobOffer,
                                value: value,
                                nCandidates: jobOffer.candidateProfessionalsId.length
                            };
                        } else {
                            return {
                                ...jobOffer,
                                nCandidates: jobOffer.candidateProfessionalsId.length
                            };
                        }
                    }));
                    setJobOffers(modifiedJobOffers);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
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
                            //
                            setTableReady(true);
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
                <Col xs={'auto'} style={{height: '90vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px', maxWidth: '250px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                    <Row style={role === "professional" ? {marginBottom: '58px'} : {marginBottom: '50px'}}>
                        { (role === "operator" || role === "manager") ?
                            <Col className="d-flex justify-content-center">
                                <Button style={{marginRight: '15px'}} variant="info" onClick={() => navigate('/ui/jobOffers/addJobOffer')}> <i className="bi bi-plus-lg"></i> Add job offer </Button>
                            </Col>
                            : <></>
                        }
                    </Row>
                    <Row>
                        <h5> Filter by </h5>
                    </Row>
                    <Row>
                        <Col ref={containerRef} style={role === "professional" ? {maxHeight: '68vh', overflowY: "auto", borderBottom: '1px solid #ccc'} : {maxHeight: '29.3vh', overflowY: "auto", borderBottom: '1px solid #ccc'}}>
                            <h6 key={0} ref={(f) => filtersRef.current[0] = f} >
                                Skills:
                            </h6>
                            <Select
                                options={optionSkills}
                                value={selectedSkills}
                                onChange={ev => {
                                    setSelectedSkills(ev);
                                    filterSkills(ev.map(skill => skill.value));
                                }}
                                isMulti
                                closeMenuOnSelect={false}
                                isSearchable={true}
                                isClearable={true}
                                onMenuOpen={() => {
                                    const container = containerRef.current;
                                    if (container) {
                                        container.scrollTo({
                                            top: filtersRef.current[0].offsetTop - container.offsetTop,
                                            behavior: "smooth",
                                        });
                                    }
                                }}
                                placeholder="Choose or search skills"
                                components={animatedComponents}
                                styles={{
                                    container: base => ({
                                        ...base,
                                        width: '250px',
                                        marginBottom: '30px'
                                    })
                                }}
                                maxMenuHeight={145}
                                // theme={(theme) => ({
                                //     ...theme,
                                //     colors: {
                                //         ...theme.colors,
                                //         primary25: '#D1E7DD',
                                //         primary: '#34ce57',
                                //     },
                                // })}
                            />
                            <h6 key={1} ref={(f) => filtersRef.current[1] = f}>
                                Candidate professionals:
                            </h6>
                            <Select
                                options={optionCandidates}
                                value={selectedCandidates}
                                onChange={ev => {
                                    setSelectedCandidates(ev);
                                    filterCandidates(ev.map(professional => professional.value));
                                }}
                                isMulti
                                closeMenuOnSelect={false}
                                isSearchable={true}
                                isClearable={true}
                                onMenuOpen={() => {
                                    const container = containerRef.current;
                                    if (container) {
                                        container.scrollTo({
                                            top: filtersRef.current[1].offsetTop - container.offsetTop,
                                            behavior: "smooth",
                                        });
                                    }
                                }}
                                placeholder="Choose or search profe..."
                                components={animatedComponents}
                                styles={{
                                    container: base => ({
                                        ...base,
                                        width: '250px',
                                        marginBottom: '30px'
                                    })
                                }}
                                maxMenuHeight={145}
                            />
                            <h6 key={2} ref={(f) => filtersRef.current[2] = f}>
                                Consolidated professionals:
                            </h6>
                            <Select
                                options={optionConsolidated}
                                value={selectedConsolidated}
                                onChange={ev => {
                                    setSelectedConsolidated(ev);
                                    filterConsolidated(ev.map(professional => professional.value));
                                }}
                                isMulti
                                closeMenuOnSelect={false}
                                isSearchable={true}
                                isClearable={true}
                                onMenuOpen={() => {
                                    const container = containerRef.current;
                                    if (container) {
                                        container.scrollTo({
                                            top: filtersRef.current[2].offsetTop - container.offsetTop,
                                            behavior: "smooth",
                                        });
                                    }
                                }}
                                placeholder="Choose or search profe..."
                                components={animatedComponents}
                                styles={{
                                    container: base => ({
                                        ...base,
                                        width: '250px',
                                        marginBottom: '30px'
                                    })
                                }}
                                maxMenuHeight={145}
                            />
                            <h6 style={{maxWidth: '250px'}} key={3} ref={(f) => filtersRef.current[3] = f}>
                                Professionals who have completed a job:
                            </h6>
                            <Select
                                options={optionCompleted}
                                value={selectedCompleted}
                                onChange={ev => {
                                    setSelectedCompleted(ev);
                                    filterCompleted(ev.map(professional => professional.value));
                                }}
                                isMulti
                                closeMenuOnSelect={false}
                                isSearchable={true}
                                isClearable={true}
                                onMenuOpen={() => { setTimeout(() => {
                                    const container = containerRef.current;
                                    if (container) {
                                        container.scrollTo({
                                            top: filtersRef.current[3].offsetTop - container.offsetTop,
                                            behavior: "smooth",
                                        });
                                    }
                                }, 0)
                                }}
                                placeholder="Choose or search profe..."
                                components={animatedComponents}
                                styles={{
                                    container: base => ({
                                        ...base,
                                        width: '250px',
                                        marginBottom: '30px'
                                    })
                                }}
                                maxMenuHeight={145}
                            />
                            <h6 key={4} ref={(f) => filtersRef.current[4] = f}>
                                Aborted professionals:
                            </h6>
                            <Select
                                options={optionAborted}
                                value={selectedAborted}
                                onChange={ev => {
                                    setSelectedAborted(ev);
                                    filterAborteds(ev.map(professional => professional.value));
                                }}
                                isMulti
                                closeMenuOnSelect={false}
                                isSearchable={true}
                                isClearable={true}
                                onMenuOpen={() => { setTimeout(() => {
                                    const container = containerRef.current;
                                    if (container) {
                                        container.scrollTo({
                                            top: filtersRef.current[4].offsetTop - container.offsetTop,
                                            behavior: "smooth",
                                        });
                                    }
                                }, 0)
                                }}
                                placeholder="Choose or search profe..."
                                components={animatedComponents}
                                styles={{
                                    container: base => ({
                                        ...base,
                                        width: '250px',
                                        marginBottom: '30px'
                                    })
                                }}
                                maxMenuHeight={145}
                            />
                        </Col>
                    </Row>
                </Col>
                <Col>
                    {nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Job Offers yet! </h4>
                        </div>
                    }
                    <div style={containerStyle}>
                        <div style={gridStyle} className={"ag-theme-quartz"}>
                            <AgGridReact
                                loading={!tableReady}
                                loadingOverlayComponent={CustomLoadingOverlay}
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
                                onRowClicked={useCallback((event) => {
                                    navigate(`/ui/jobOffers/${event.node.data.id}`)
                                }, [])}
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {JobOffers};