import React, { useState, useMemo, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Dropdown, DropdownButton, Container } from "react-bootstrap";
import { MessageContext, TokenContext } from "../messageCtx";
import { useContext } from "react";
import API from "../API";
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import zoomPlugin from 'chartjs-plugin-zoom';

function AnalyticsContainer({ loggedIn, role }) {
    const navigate = useNavigate();
    const [chartName, setChartName] = useState("Customers");
    const [data, setData] = useState([]);
    const handleErrors = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);

    useEffect(() => {
        if (!loggedIn || role !== "manager") {
            navigate("/ui");
        }
        async function getCustomerAnalytics() {
            try {
                const customers = await API.getCustomersAnalytics(xsrfToken);
                //console.log(customers);
                setData(customers);
            } catch (err) {
                //console.log(err);
                handleErrors({ detail: err.message });
            }
        }
        async function getProfessionalAnalytics() {
            try {
                const professionals = await API.getProfessionalsAnalytics(xsrfToken);
                //console.log(professionals);
                setData(professionals);
            } catch (err) {
                //console.log(err);
                handleErrors({ detail: err.message });
            }
        }
        if (chartName === "Customers") {
            getCustomerAnalytics();
        } else {
            getProfessionalAnalytics();
        }
    }, [chartName]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
            <DropdownButton id="dropdown-basic-button" title={`${chartName} Analytics`} onSelect={(eventKey) => {setChartName(eventKey);}}>
                <Dropdown.Item eventKey="Customers">Customers Analytics</Dropdown.Item>
                <Dropdown.Item eventKey="Professionals">Professionals Analytics</Dropdown.Item>
            </DropdownButton>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flex: 1 }}>
                <JobOffersChart analyticsData={data} chartName={chartName} />
                <KpiChart analyticsData={data} chartName={chartName} />
            </div>
        </div>
    );
}

// Registering chart components and zoom plugin
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, zoomPlugin);

const JobOffersChart = ({ analyticsData, chartName }) => {
    const [hoveredSeriesIndex, setHoveredSeriesIndex] = useState(null);

    // get data dependeing on chartName
    let labels;
    let series1, series2, series3;
    let data1,data2,data3;
    if (chartName === "Customers") {
        labels = analyticsData.map((item) => item.name + " " + item.surname);
        series1 = "abortedJobOffers";
        data1 = analyticsData.map((item) => item.abortedJobOffers);
        series2 = "completedJobOffers";
        data2 = analyticsData.map((item) => item.completedJobOffers);
        series3 = "createdJobOffers";
        data3 = analyticsData.map((item) => item.createdJobOffers);
    } else {
        labels = analyticsData.map((item) => item.name + " " + item.surname);
        series1 = "abortedJobOffers";
        data1 = analyticsData.map((item) => item.abortedJobOffers);
        series2 = "completedJobOffers";
        data2 = analyticsData.map((item) => item.completedJobOffers);
        series3 = "candidatedJobOffers";
        data3 = analyticsData.map((item) => item.candidatedJobOffers);
    }

    // Chart.js data
    const data = {
        labels: labels,
        datasets: [
            {
                label: series1,
                data: data1,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: hoveredSeriesIndex === 0 ? 'black' : 'rgba(75, 192, 192, 1)',
                borderWidth: hoveredSeriesIndex === 0 ? 2 : 1,
            },
            {
                label: series2,
                data: data2,
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: hoveredSeriesIndex === 1 ? 'black' : 'rgba(153, 102, 255, 1)',
                borderWidth: hoveredSeriesIndex === 1 ? 2 : 1,
            },
            {
                label: series3,
                data: data3,
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                borderColor: hoveredSeriesIndex === 2 ? 'black' : 'rgba(255, 159, 64, 1)',
                borderWidth: hoveredSeriesIndex === 2 ? 2 : 1,
            },
        ],
    };


    // Chart.js options
    const options = {
        responsive: true,
        plugins: {
            title: {
                display: true,
                text: `${chartName} Job Offers Analytics`,
            },
            zoom: {
                pan: {
                    enabled: true,
                    mode: 'x',  // Only enable panning on the x-axis
                },
                /*zoom: {
                  wheel: {
                    enabled: true,  // Enable wheel zooming
                  },
                  mode: 'x',  // Only enable zooming on the x-axis
                },*/
            },
        },
        scales: {
            x: {
                // Ensure the x-axis is large enough to scroll
                barThickness: 30,
                maxBarThickness: 50,
                grid: {
                    display: false,
                },
                // Adjusting for the horizontal scrolling effect
                min: 0, // Start the x-axis from 0
                max: 8,  // Max value to allow scroll
            },
            y: {
                beginAtZero: true, // Ensure the y-axis starts at zero
            },
        },
        onHover: (event, chartElement) => {
            if (chartElement.length) {
                const { datasetIndex } = chartElement[0];
                setHoveredSeriesIndex(datasetIndex);
            } else {
                setHoveredSeriesIndex(null);
            }
        },
    };

    return (
        <Container style={{ height: "80%", width: "50%" }}>
            <Bar data={data} options={options} />
        </Container>
    );
};

const KpiChart = ({ analyticsData, chartName }) => {
    const [hoveredSeriesIndex, setHoveredSeriesIndex] = useState(null);

    // get data
    let labels = analyticsData.map((item) => item.name + " " + item.surname);
    let kpidata = analyticsData.map((item) => item.kpi);

    const data = {
        labels: labels,
        datasets: [
            {
                label: 'KPI',
                data: kpidata,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: hoveredSeriesIndex === 0 ? 'black' : 'rgba(75, 192, 192, 1)',
                borderWidth: hoveredSeriesIndex === 0 ? 2 : 1,
            }
        ],
    };

    const options = {
        responsive: true,
        indexAxis: 'y', // Set the index axis to 'y' for horizontal bars
        plugins: {
            title: {
                display: true,
                text: `${chartName} KPI Analytics`,
            },
            zoom: {
                pan: {
                    enabled: true,
                    mode: 'y',  // Only enable panning on the y-axis
                },
            },
        },
        scales: {
            x: {
                beginAtZero: true, // Ensure the x-axis starts at zero
                max: 100,
            },
            y: {
                barThickness: 30,
                maxBarThickness: 50,
                grid: {
                    display: false,
                },
                min: 0, // Start the y-axis from 0
                max: 8,  // Max value to allow scroll
            },
        },
        onHover: (event, chartElement) => {
            if (chartElement.length) {
                const { datasetIndex } = chartElement[0];
                setHoveredSeriesIndex(datasetIndex);
            } else {
                setHoveredSeriesIndex(null);
            }
        },
    };

    return (
        <Container style={{ height: "80%", width: "50%" }}>
            <Bar data={data} options={options} />
        </Container>
    );
};


/*
const data = {
        labels: Array.from({ length: 30 }, (_, i) => `Month ${i + 1}`),
        datasets: [
            {
                label: 'Series 1',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) + 10),
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: hoveredSeriesIndex === 0 ? 'black' : 'rgba(75, 192, 192, 1)',
                borderWidth: hoveredSeriesIndex === 0 ? 2 : 1,
            },
            {
                label: 'Series 2',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) + 10),
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: hoveredSeriesIndex === 1 ? 'black' : 'rgba(153, 102, 255, 1)',
                borderWidth: hoveredSeriesIndex === 1 ? 2 : 1,
            },
            {
                label: 'Series 3',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) + 10),
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                borderColor: hoveredSeriesIndex === 2 ? 'black' : 'rgba(255, 159, 64, 1)',
                borderWidth: hoveredSeriesIndex === 2 ? 2 : 1,
            },
        ],
    };
    */

export default AnalyticsContainer;