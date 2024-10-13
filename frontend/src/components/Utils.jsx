import React, { useContext, useRef } from 'react';
import {MessageContext} from "../messageCtx.js";
import {Form} from "react-bootstrap";
import {LoadScript, StandaloneSearchBox} from "@react-google-maps/api";

const API_KEY = 'AIzaSyCO5hFwnkcQjDkoivao8qpJbKvITf_vb1g';
const libraries = ['places']; // Include Places Library

import API from "../API.jsx";
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
    const { address, setAddress } = props;
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
                    isInvalid={address.invalid}
                    type="text"
                    placeholder="Enter Address"
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

export { address_string_to_object, address_object_to_string, AddressSelector , API_KEY};
