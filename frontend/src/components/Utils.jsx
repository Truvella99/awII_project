import React, { useContext, useRef, useState } from 'react';
import {MessageContext} from "../messageCtx.js";
import {Container, Form, Col, Badge} from "react-bootstrap";
import {LoadScript, StandaloneSearchBox} from "@react-google-maps/api";
import {Menu, menuClasses, MenuItem, Sidebar} from "react-pro-sidebar";
import {Link, useLocation} from "react-router-dom";
import jobOffersImage from "../icons/jobOffers.png";
import customersImage from "../icons/customers.png";
import professionalsImage from "../icons/professionals.png";
import messagesImage from "../icons/messages.png";
import { FaInfoCircle } from "react-icons/fa";

const API_KEY = 'AIzaSyCO5hFwnkcQjDkoivao8qpJbKvITf_vb1g';
const libraries = ['places']; // Include Places Library

function convertDuration(duration,asString=true) {
    let days = Math.floor(duration / 24);
    let hours = duration % 24;
    if (asString) {
        return `${days.toString()}d ${hours.toString()}h`;
    } else {
        return { days: days, hours: hours };
    }
}

const address_string_to_object = (addr) => {
    const main_infos = addr.split(';');
    const lat = parseFloat(main_infos[1].split(':')[1]);
    const lng = parseFloat(main_infos[2].split(':')[1]);
    return {
        text: main_infos[0],
        lat: lat,
        lng: lng
    };
};

const address_object_to_string = (addr) => {
    return addr.text + ';lat:' + addr.lat + ";lng:" + addr.lng;
};

/**
 * React state to use and pass to this component as props:
 * const [address, setAddress] = useState({ text: '', lat: 0.0, lng: 0.0, invalid: false });
 */
function AddressSelector(props) {
    const handleError = useContext(MessageContext);
    const { address, setAddress, hidden } = props;
    const inputRef = useRef();

    const handlePlaceChanged = async () => {
        const [place] = inputRef.current.getPlaces();
        console.log(place);
        if (place) {
            setAddress({
                text: place.formatted_address,
                lat: place.geometry.location.lat(),
                lng: place.geometry.location.lng(),
                invalid: address.invalid
            });
            const location = place.formatted_address + ';lat:' + place.geometry.location.lat() + ";lng:" + place.geometry.location.lng();
            //console.log(updatedUser);
            console.log(location);
            // Now call the updateUser API with the updated user information
            try {

                //console.log("User updated successfully:", result);
                // You might want to do something with the result or updated user here
            } catch(error) {
                handleError({error: `Failed to update user:${error.error}`});
                // Handle the error appropriately
            }
        }
    };


    return (
        <StandaloneSearchBox onLoad={(ref) => (inputRef.current = ref)} onPlacesChanged={handlePlaceChanged}>
            <>
                <Form.Control
                    hidden={hidden}
                    isInvalid={address.invalid}
                    type="text"
                    placeholder="Enter address..."
                    className="form-control-green-focus"
                    // HERE NOT TO AVOID CALL TOO MUCH THE API onChange={(event) => addressValidation({text: event.target.value, lat:address.lat, lng:address.lng, invalid: address.invalid},setAddress)}
                    onChange={(event) => { setAddress({ text: event.target.value, lat: address.lat, lng: address.lng, invalid: false }); }}
                    onBlur={(event) => { setAddress({ text: event.target.value.trim(), lat: address.lat, lng: address.lng, invalid: false }); }}
                    value={(address) ? address.text : ''}
                />
                <Form.Control.Feedback type="invalid">Please Insert a Valid Address</Form.Control.Feedback>
            </>
        </StandaloneSearchBox>

    );
}

// Home sidebar functions
const sidebarThemes = {
    light: {
        sidebar: {
            backgroundColor: '#ffffff',
            color: '#607489',
        },
        menu: {
            menuContent: '#fbfcfd',
            icon: '#0098e5',
            hover: {
                backgroundColor: '#c5e4ff',
                color: '#44596e',
            },
            disabled: {
                color: '#9fb6cf',
            },
            active: {
                backgroundColor: '#76d8ff',
                color: '#44596e'
            }
        },
    },
    dark: {
        sidebar: {
            backgroundColor: '#0b2948',
            color: '#8ba1b7',
        },
        menu: {
            menuContent: '#082440',
            icon: '#59d0ff',
            hover: {
                backgroundColor: '#00458b',
                color: '#b6c8d9',
            },
            disabled: {
                color: '#3e5e7e',
            },
        },
    },
};
// hex to rgba converter
const hexToRgba = (hex, alpha) => {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);

    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

function SideBar({role, unreadMessages}) {
    const path = useLocation().pathname;

    const menuItemStyles = {
        root: {
            fontSize: '18px',
            fontWeight: 400,
        },
        icon: {
            color: sidebarThemes["light"].menu.icon,
            [`&.${menuClasses.disabled}`]: {
                color: sidebarThemes["light"].menu.disabled.color,
            },
        },
        SubMenuExpandIcon: {
            color: '#b6b7b9',
        },
        subMenuContent: ({ level }) => ({
            backgroundColor:
                level === 0
                    ? hexToRgba(sidebarThemes["light"].menu.menuContent, 1)
                    : 'transparent',
        }),
        button: {
            [`&.${menuClasses.disabled}`]: {
                color: sidebarThemes["light"].menu.disabled.color,
            },
            '&:hover': {
                backgroundColor: hexToRgba(sidebarThemes["light"].menu.hover.backgroundColor, 1),
                color: sidebarThemes["light"].menu.hover.color,
            },
        },
        label: ({ open }) => ({
            fontWeight: open ? 600 : undefined,
        }),
    }

    return(
        <Sidebar
            backgroundColor={hexToRgba(sidebarThemes["light"].sidebar.backgroundColor, 1)}
            rootStyles={{color: sidebarThemes["light"].sidebar.color}}
        >
            <Menu menuItemStyles={menuItemStyles}>
                { (role === "manager" || role === "operator") ?
                    <MenuItem icon={<img src={customersImage} height={28} width={28}/>} style={{
                        backgroundColor: path === "/ui/customers" ? hexToRgba(sidebarThemes["light"].menu.active.backgroundColor, 1) : '',
                        color: path === "/ui/customers" ? sidebarThemes["light"].menu.active.color : ''
                    }} component={<Link to="/ui/customers" />}> Customers </MenuItem> : ''
                }
                { (role === "manager" || role === "operator" || role === "customer") ?
                    <MenuItem icon={<img src={professionalsImage} height={28} width={28}/>} style={{
                        backgroundColor: path === "/ui/professionals" ? hexToRgba(sidebarThemes["light"].menu.active.backgroundColor, 1) : '',
                        color: path === "/ui/professionals" ? sidebarThemes["light"].menu.active.color : ''
                    }} component={<Link to="/ui/professionals" />}> Professionals </MenuItem> : ''
                }
                { (role === "manager" || role === "operator") ?
                    <MenuItem icon={<img src={jobOffersImage} height={28} width={28}/>} style={{
                        backgroundColor: path === "/ui/jobOffers" ? hexToRgba(sidebarThemes["light"].menu.active.backgroundColor, 1) : '',
                        color: path === "/ui/jobOffers" ? sidebarThemes["light"].menu.active.color : ''
                    }} component={<Link to="/ui/jobOffers" />}> Job Offers </MenuItem> : ''
                }
                { (role === "manager" || role === "operator") ?
                    <MenuItem icon={<img src={messagesImage} height={28} width={28}/>} style={{
                        backgroundColor: path === "/ui/messages" ? hexToRgba(sidebarThemes["light"].menu.active.backgroundColor, 1) : '',
                        color: path === "/ui/messages" ? sidebarThemes["light"].menu.active.color : ''
                    }} suffix={(unreadMessages > 0) ? <Badge pill bg="danger"> {unreadMessages} </Badge> : ''} component={<Link to="/ui/messages" />}> Messages </MenuItem> : ''
                }
                { (role === "professional") ?
                    <MenuItem icon={<img src={jobOffersImage} height={28} width={28}/>} style={{
                        backgroundColor: path === "/ui/jobOffers" ? hexToRgba(sidebarThemes["light"].menu.active.backgroundColor, 1) : '',
                        color: path === "/ui/jobOffers" ? sidebarThemes["light"].menu.active.color : ''
                    }} component={<Link to="/ui/jobOffers" />}> Open Job Offers </MenuItem> : ''
                }
            </Menu>
        </Sidebar>
    );
}


// SEARCH BAR COMPONENTS
function SearchList({ searchedContent, handleItemSelection, setShowResults, setSearchText }) {
    return (
        <Container
            style={{
                position: 'absolute', // Overlay behavior
                backgroundColor: 'white', // Background for visibility
                zIndex: 10, // Ensure it's on top of other elements
                boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)', // Shadow for depth
                width: '47%', // Full width
                maxHeight: '100px',
                overflowY: 'scroll'
            }}
            onMouseDown={(e) => e.preventDefault()} // Prevent blur on click
        >
            {searchedContent.map((item, index) => (
                <div key={index}>
                    <span style={{cursor: 'pointer'}} onClick={() => {
                        setSearchText('');
                        setShowResults(false);
                        handleItemSelection(item);
                    }}>{item.name + ' ' + item.surname + ' '}
                    </span>
                    <span onClick={() => window.open(`/ui/customers/${item.id}`, '_blank')} style={{ color: '#0d6efd', textDecoration: 'underline', cursor: 'pointer' }}>
                        More Info<FaInfoCircle />
                    </span>
                    <hr style={{ margin: 2 }} />
                </div>
            ))}
        </Container>
    );
}

function SearchBar({ handleChange, setShowResults, Subject, searchText, setSearchText }) {
    return (
        <Form.Control type='search' value={searchText} onChange={e => {
            if (e.target.value === '') {
                setShowResults(false);
            } else {
                setShowResults(true);
            }
            setSearchText(e.target.value);
            handleChange(e.target.value);
        }} onBlur={() => setShowResults(false)} placeholder={`Find ${Subject} ...`} />
    );
}

function SearchContainer({ searchedContent, handleChange, handleItemSelection, Subject }) {
    const [showResults, setShowResults] = useState(true);
    const [searchText, setSearchText] = useState('');
    return (
        <Form.Group className="mb-3" as={Col} controlId="searchBar">
            <Form.Label>Find {Subject}</Form.Label>
            <SearchBar handleChange={handleChange} setShowResults={setShowResults} Subject={Subject} searchText={searchText} setSearchText={setSearchText}/>
            {searchedContent && searchedContent.length > 0 && showResults ?
                <SearchList searchedContent={searchedContent} handleItemSelection={handleItemSelection} setShowResults={setShowResults} setSearchText={setSearchText} /> : ''}
        </Form.Group>
    );
}

function CustomLoadingOverlay() {
    return (
        <div className="ag-overlay-loading-center" role="presentation">
            <div
                role="presentation"
                className="custom-loading-overlay"
                style={{
                    height: 100,
                    width: 100,
                    background:
                        'url(https://www.ag-grid.com/images/ag-grid-loading-spinner.svg) center / contain no-repeat',
                    margin: '0 auto',
                }}
            ></div>
            <div aria-live="polite" aria-atomic="true" style={{fontSize: "large"}}>
                Loading...
            </div>
        </div>
    );
}

export {convertDuration, SearchContainer, address_string_to_object, address_object_to_string, AddressSelector , API_KEY, SideBar, CustomLoadingOverlay};
