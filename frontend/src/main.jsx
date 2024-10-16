import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import {API_KEY} from "./components/Utils.jsx";
import {LoadScript} from "@react-google-maps/api";
const libraries = ['places']; // Include Places Library

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <LoadScript googleMapsApiKey={API_KEY} libraries={libraries}>
            <App />
        </LoadScript>
    </React.StrictMode>
)
