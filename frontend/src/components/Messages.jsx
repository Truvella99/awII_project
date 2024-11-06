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

ModuleRegistry.registerModules([InfiniteRowModelModule]);


function Messages({loggedIn, role, unreadMessages, setUnreadMessages}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [messages, setMessages] = useState();
    const [nothing, setNothing] = useState(false);
    const [tableReady, setTableReady] = useState(false);
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
        // do an in memory sort of the data, across all the fields
        const resultOfSort = data.slice();
        resultOfSort.sort(function (a, b) {
            for (let k = 0; k < sortModel.length; k++) {
                const sortColModel = sortModel[k];
                const valueA = a[sortColModel.colId];
                const valueB = b[sortColModel.colId];
                // Date regex
                const regex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/(19|20)\d{2}$/;

                if (regex.test(valueA)) {
                    const [day1, month1, year1] = valueA.split("/");
                    const [day2, month2, year2] = valueB.split("/");
                    // Convert to Date objects
                    const dateObj1 = new Date(`${year1}-${month1}-${day1}`);
                    const dateObj2 = new Date(`${year2}-${month2}-${day2}`);

                    if (dateObj1 === dateObj2) {
                        continue;
                    }
                    const sortDirection = sortColModel.sort === 'asc' ? 1 : -1;
                    if (dateObj1 > dateObj2) {
                        return sortDirection;
                    } else {
                        return sortDirection * -1;
                    }
                } else {
                    // this filter didn't find a difference, move onto the next one
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
            }
            // no filters found a difference
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

            if (filterModel.subject) {
                if (filterModel.subject.type === 'contains') {
                    if ( !((item.subject.toLowerCase()).includes(filterModel.subject.filter)) )
                        continue;
                } else if (filterModel.subject.type === 'notContains') {
                    if ( (item.subject.toLowerCase()).includes(filterModel.subject.filter) )
                        continue;
                }
            }
            if (filterModel.contact) {
                if (filterModel.contact.type === 'contains') {
                    if ( !((item.contact.toLowerCase()).includes(filterModel.contact.filter)) )
                        continue;
                } else if (filterModel.contact.type === 'notContains') {
                    if ( (item.contact.toLowerCase()).includes(filterModel.contact.filter) )
                        continue;
                }
            }
            if (filterModel.channel) {
                if (filterModel.channel.type === 'contains') {
                    if ( !((item.channel.toLowerCase()).includes(filterModel.channel.filter)) )
                        continue;
                } else if (filterModel.channel.type === 'notContains') {
                    if ( (item.channel.toLowerCase()).includes(filterModel.channel.filter) )
                        continue;
                }
            }
            if (filterModel.date) {
                const dateAsString = item.date.toString();
                const dateParts = dateAsString.split("/");
                const date = new Date(Number(dateParts[2]), Number(dateParts[1]) - 1, Number(dateParts[0]));
                const filterDate = new Date((filterModel.date.dateFrom).replace(" ", "T"));
                if (filterModel.date.type === 'equals') {
                    if (date.getTime() !== filterDate.getTime()) {
                        continue;
                    }
                }
                if (filterModel.date.type === 'lessThan') {
                    if (date > filterDate) {
                        continue;
                    }
                }
                if (filterModel.date.type === 'greaterThan') {
                    if (date < filterDate) {
                        continue;
                    }
                }
                if (filterModel.date.type === 'inRange') {
                    const filterDateTo = new Date((filterModel.date.dateTo).replace(" ", "T"));
                    if ((date < filterDate) || (date > filterDateTo)) {
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
            if (filterModel.priority) {
                if (filterModel.priority.type === 'contains') {
                    if ( !((item.priority.toLowerCase()).includes(filterModel.priority.filter)) )
                        continue;
                } else if (filterModel.priority.type === 'notContains') {
                    if ( (item.priority.toLowerCase()).includes(filterModel.priority.filter) )
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
    const valueFormatter = (params) => {
        if (params.value)
            return params.value.charAt(0).toUpperCase() + params.value.slice(1);
    };
    const [columnDefs, setColumnDefs] = useState([
        { field: "subject", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true,
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
        { field: "contact", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true },
        { field: "channel", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true, valueFormatter: valueFormatter },
        { field: "date", filter: "agDateColumnFilter", filterParams: {filterOptions: ["equals", "lessThan", "greaterThan", "inRange"]}, suppressHeaderMenuButton: true },
        { field: "currentState", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, headerName: "Current State", suppressHeaderMenuButton: true, valueFormatter: valueFormatter },
        { field: "priority", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true, valueFormatter: valueFormatter }
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
    const getRowStyle = useCallback(function (params) {
        if (tableReady && params.node.data) {
            if (params.node.data.currentState === 'received')
                return {fontWeight: 'bold'}
        }
    }, [tableReady]);

    // Table data functions
    const onGridReady = useCallback((params) => {
        const fetchMessages = async (params) => {
            try {
                if (loggedIn) {
                    const messages = await API.getAllMessages(xsrfToken);
                    console.log(messages);

                    const modifiedMessages = messages.map(message => {
                        return {
                            ...message,
                            contact: message.email || message.telephone || message.address,
                            date: new Date(message.date).toLocaleDateString("default", {day: "2-digit", month: "2-digit", year: "numeric"})
                        };
                    });
                    modifiedMessages.forEach(message => {
                        delete message.email;
                        delete message.telephone;
                        delete message.address;
                    });
                    setMessages(modifiedMessages);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                modifiedMessages,
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

                            // If there are no messages, display the message
                            if (messages.length === 0) {
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

        fetchMessages(params);
    }, []);


    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '80vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages}/>
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
                <Col>
                    { nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Messages yet! </h4>
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
                                getRowStyle={getRowStyle}
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
                                onRowClicked={ useCallback(async (event) => {
                                    try {
                                        if (event.node.data.currentState !== 'received')
                                            navigate(`/ui/messages/${event.node.data.id}`)
                                        else {
                                            const message = {targetState: 'read', comment: 'Message read'};
                                            await API.updateMessageState(message, event.node.data.id, xsrfToken);
                                            setUnreadMessages(unreadMessages - 1);
                                            navigate(`/ui/messages/${event.node.data.id}`);
                                        }
                                    } catch (err) {
                                        console.log(err);
                                        handleError(err);
                                    }
                                }, [])}
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {Messages};