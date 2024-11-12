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


function Pending({loggedIn, role, unreadMessages, setUnreadMessages, pending}) {
    const navigate = useNavigate();
    const handleError = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);
    const [pendings, setPendings] = useState();
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

            if (filterModel.contact) {
                if (filterModel.contact.type === 'contains') {
                    if ( !((item.contact.toLowerCase()).includes(filterModel.contact.filter)) )
                        continue;
                } else if (filterModel.contact.type === 'notContains') {
                    if ( (item.contact.toLowerCase()).includes(filterModel.contact.filter) )
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
    const [columnDefs, setColumnDefs] = useState([
        { field: "contact", filter: "agTextColumnFilter", filterParams: {filterOptions: ["contains", "notContains"]}, suppressHeaderMenuButton: true,
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
        { field: "navigate", filter: false, sortable: false, headerName: "Add to customers or professionals", suppressHeaderMenuButton: true,
            cellRenderer: (props) => {
                return (
                    <div style={{display: "flex", gap: "15px"}}>
                        <Button style={{marginTop: "5px"}} variant="success" size={"sm"} onClick={() => {
                            navigate("/ui/customers/addCustomer", {state: {id: props.data.id, contact: props.data.contact, channel: props.data.channel}});
                        }}> Customer <i className="bi bi-caret-right-fill"></i> </Button>
                        {' '}
                        <Button style={{marginTop: "5px"}} variant="primary" size={"sm"} onClick={() => {
                            navigate("/ui/professionals/addProfessional", {state: {id: props.data.id, contact: props.data.contact , channel: props.data.channel}});
                        }}> Professional <i className="bi bi-caret-right-fill"></i> </Button>
                    </div>
                );
            }
        }
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
    const onGridReady = useCallback((params) => {
        const fetchContacts = async (params) => {
            try {
                if (loggedIn) {
                    const pendings = await API.getPendingContacts(xsrfToken);
                    console.log(pendings);

                    const modifiedPendings = pendings.map(pending => {
                        let contact;
                        let channel;
                        if (pending.emails.length > 0) {
                            contact = pending.emails[0].email;
                            channel = "email";
                        } else if (pending.telephones.length > 0) {
                            contact = pending.telephones[0].telephone;
                            channel = "telephone";
                        } else if (pending.addresses.length > 0) {
                            contact = pending.addresses[0].address;
                            channel = "address";
                        }
                        return {
                            ...pending,
                            contact: contact,
                            channel: channel
                        };
                    });
                    setPendings(modifiedPendings);

                    // Define the data source for AG-Grid
                    const dataSource = {
                        rowCount: undefined,
                        getRows: (params) => {
                            console.log('asking for ' + params.startRow + ' to ' + params.endRow);
                            // Call the server
                            // take a slice of the total rows
                            const dataAfterSortingAndFiltering = sortAndFilter(
                                modifiedPendings,
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
                            if (pendings.length === 0) {
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

        fetchContacts(params);
    }, []);


    return (
        <Container fluid>
            <Row>
                <Col xs={'auto'} style={{height: '80vh', borderRight: '1px solid #ccc', display: "flex", flexDirection: "column"}}>
                    <div style={{borderBottom: '1px solid #ccc', borderTop: '1px solid #ccc', marginBottom: '30px'}}>
                        <SideBar role={role} unreadMessages={unreadMessages} pending={pending}/>
                    </div>
                </Col>
                <Col>
                    { nothing &&
                        <div style={{position: "fixed", zIndex: 1, paddingLeft: "500px", paddingTop: "250px"}}>
                            <h4> No Pending Contacts yet! </h4>
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
                            />
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export {Pending};