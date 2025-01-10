import React, {useCallback, useContext, useEffect, useMemo, useRef, useState} from "react";
import {Button, Col, Row} from "react-bootstrap";
import {CustomLoadingOverlay, SideBar} from "./Utils.jsx";
import { AgGridReact } from 'ag-grid-react';
import { ModuleRegistry } from '@ag-grid-community/core';
import { InfiniteRowModelModule } from '@ag-grid-community/infinite-row-model';
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-quartz.css";
import {MessageContext, TokenContext} from "../messageCtx.js";
import API from "../API.jsx";
import {useNavigate} from "react-router-dom";
import {Container} from "react-bootstrap/";
import Select from "react-select";
import makeAnimated from "react-select/animated";

ModuleRegistry.registerModules([InfiniteRowModelModule]);


function Customers({loggedIn, role, unreadMessages, pending, setPending, setUnreadMessages}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [customers, setCustomers] = useState();
    const [optionJobOffers, setOptionJobOffers] = useState();
    const [selectedJobOffers, setSelectedJobOffers] = useState([]);
    const [nothing, setNothing] = useState(false);
    const [tableReady, setTableReady] = useState(false);
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
                    if ( !((item.name.toLowerCase()).includes(filterModel.name.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.name.type === 'notContains') {
                    if ( (item.name.toLowerCase()).includes(filterModel.name.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.surname) {
                if (filterModel.surname.type === 'contains') {
                    if ( !((item.surname.toLowerCase()).includes(filterModel.surname.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.surname.type === 'notContains') {
                    if ( (item.surname.toLowerCase()).includes(filterModel.surname.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.ssncode) {
                if (filterModel.ssncode.type === 'contains') {
                    if ( !((item.ssncode.toLowerCase()).includes(filterModel.ssncode.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.ssncode.type === 'notContains') {
                    if ( (item.ssncode.toLowerCase()).includes(filterModel.ssncode.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.email) {
                if (filterModel.email.type === 'contains') {
                    if ( !item.email || !((item.email.toLowerCase()).includes(filterModel.email.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.email.type === 'notContains') {
                    if ( item.email && (item.email.toLowerCase()).includes(filterModel.email.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.telephone) {
                if (filterModel.telephone.type === 'contains') {
                    if ( !item.telephone || !((item.telephone.toLowerCase()).includes(filterModel.telephone.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.telephone.type === 'notContains') {
                    if ( item.telephone && (item.telephone.toLowerCase()).includes(filterModel.telephone.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.address) {
                if (filterModel.address.type === 'contains') {
                    if ( !item.address || !((item.address.toLowerCase()).includes(filterModel.address.filter.toLowerCase())) )
                        continue;
                } else if (filterModel.address.type === 'notContains') {
                    if ( item.address && (item.address.toLowerCase()).includes(filterModel.address.filter.toLowerCase()) )
                        continue;
                }
            }
            if (filterModel.jobs) {
                const jobs = item.jobs;
                const allowedJobs = parseInt(filterModel.jobs.filter);
                if (filterModel.jobs.type === 'equals') {
                    if (jobs !== allowedJobs) {
                        continue;
                    }
                } else if (filterModel.jobs.type === 'lessThan') {
                    if (jobs >= allowedJobs) {
                        continue;
                    }
                } else if (filterModel.jobs.type === 'greaterThan') {
                    if (jobs <= allowedJobs) {
                        continue;
                    }
                } else if (filterModel.jobs.type === 'inRange') {
                    if ((jobs < parseInt(filterModel.jobs.filter)) || (jobs > parseInt(filterModel.jobs.filterTo))) {
                        continue;
                    }
                }
            }

            resultOfFilter.push(item);
        }
        return resultOfFilter;
    };

    // Table settings
    const containerStyle = useMemo(() => ({ width: "100%", height: "100%" }), []);
    const gridStyle = useMemo(() => ({ height: "100%", width: "100%" }), []);
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
        { field: "jobs", filter: "agNumberColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, headerName: "N. Open Job Offers", suppressHeaderMenuButton: true }
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
    const filterJobOffers = async (jobOffers) => {
        setTableReady(false);
        try {
            if (loggedIn) {
                const customers = await API.getCustomersJobOffers(jobOffers, xsrfToken);

                const modifiedCustomers = customers.map(customer => {
                    return {
                        ...customer,
                        jobs: customer.jobOffers.length,
                        email: customer.emails && customer.emails.length > 0
                            ? customer.emails[0].email
                            : null,
                        telephone: customer.telephones && customer.telephones.length > 0
                            ? customer.telephones[0].telephone
                            : null,
                        address: customer.addresses && customer.addresses.length > 0
                            ? customer.addresses[0].address
                            : null
                    };
                });
                modifiedCustomers.forEach(customer => {
                    delete customer.emails;
                    delete customer.telephones;
                    delete customer.addresses;
                });
                setCustomers(modifiedCustomers);

                const dataSource = {
                    rowCount: undefined,
                    getRows: (params) => {
                        console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                        // Call the server
                        // take a slice of the total rows
                        const dataAfterSortingAndFiltering = sortAndFilter(
                            modifiedCustomers,
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
        const fetchCustomers = async (params) => {
            try {
                if (loggedIn) {
                    const customers = await API.getAllCustomers(xsrfToken);
                    console.log(customers);
                    //
                    const jobOffers = [...new Map(customers.flatMap(customer => customer.jobOffers.map(j => [j.id, j.name]))).entries()].map(([id, name]) => ({ value: id, label: name }));
                    setOptionJobOffers(jobOffers);
                    //
                    const modifiedCustomers = customers.map(customer => {
                        return {
                            ...customer,
                            jobs: customer.jobOffers.length,
                            email: customer.emails && customer.emails.length > 0
                                ? customer.emails[0].email
                                : null,
                            telephone: customer.telephones && customer.telephones.length > 0
                                ? customer.telephones[0].telephone
                                : null,
                            address: customer.addresses && customer.addresses.length > 0
                                ? customer.addresses[0].address
                                : null
                        };
                    });
                    modifiedCustomers.forEach(customer => {
                        delete customer.emails;
                        delete customer.telephones;
                        delete customer.addresses;
                    });
                    setCustomers(modifiedCustomers);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                modifiedCustomers,
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

                            // If there are no customers, display the message
                            if (customers.length === 0) {
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

        fetchCustomers(params);
    }, []);

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


    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '90vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                    <Row style={{marginBottom: '100px'}}>
                        {(role === "operator" || role === "manager") ?
                            <Col className="d-flex justify-content-center">
                                <Button variant="info" onClick={() => navigate('/ui/customers/addCustomer')}> <i
                                    className="bi bi-plus-lg"></i> Add customer </Button>
                            </Col>
                            : <></>
                        }
                    </Row>
                    <h6> Filter by job offers: </h6>
                    <Select
                        options={optionJobOffers}
                        value={selectedJobOffers}
                        onChange={ev => {
                            setSelectedJobOffers(ev);
                            filterJobOffers(ev.map(job => job.value));
                        }}
                        isMulti
                        closeMenuOnSelect={false}
                        isSearchable={true}
                        isClearable={true}
                        placeholder="Choose or search jobs"
                        components={animatedComponents}
                        styles={{
                            container: base => ({
                                ...base,
                                width: '250px'
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
                </Col>
                <Col>
                    {nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Customers yet! </h4>
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
                                onRowClicked={useCallback((event) => {navigate(`/ui/customers/${event.node.data.id}`)}, [])}
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {Customers};