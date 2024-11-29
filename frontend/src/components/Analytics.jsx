import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Dropdown, DropdownButton, Container } from "react-bootstrap";
import { MessageContext, TokenContext } from "../messageCtx";
import { useContext } from "react";
import API from "../API";
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, plugins } from 'chart.js';
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

const underLineKpiPlugin = {
    id: 'underlineKpi',
    afterDraw: (chart, args, opts) => {
        const { ctx } = chart;
        ctx.save();

        const yScale = chart.scales.y;
        if (yScale && yScale.ticks) {

            yScale.ticks.forEach((tick, index) => {
                const tickPosition = yScale.getPixelForTick(index);

                ctx.strokeStyle = opts.lineColor || 'blue';
                ctx.lineWidth = opts.lineWidth || 1;
                ctx.beginPath();

                const offset_start = 3;
                const offset_end = 8;
                // Adjust the start and end positions by offset
                ctx.moveTo(yScale.left + offset_start, tickPosition + (opts.yOffset || 0)); // Start slightly after
                ctx.lineTo(yScale.right - offset_end, tickPosition + (opts.yOffset || 0)); // End slightly before

                ctx.stroke();
            });
        }

        ctx.restore();
    },
};

// Registering chart components and zoom plugin
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, zoomPlugin, underLineKpiPlugin);

const JobOffersChart = ({ analyticsData, chartName }) => {
    const containerRef = useRef(null); // Reference to the container

    // Handle mouse move over the container to check if the cursor is over the x-axis labels
    const handleMouseMove = (event) => {
        const canvas = containerRef.current.querySelector('canvas'); // Find the canvas using the ref
        if (!canvas) return;

        const chart = ChartJS.getChart(canvas);
        const xScale = chart.scales.x;
        const mouseX = event.nativeEvent.offsetX;
        const mouseY = event.nativeEvent.offsetY;

        const xAxisLeft = xScale.left;
        const xAxisRight = xScale.right;
        const xAxisTop = xScale.top;
        const xAxisBottom = xScale.bottom;

        // Determine the height of the x-axis labels
        const labelHeight = (xAxisBottom - xAxisTop) / chart.data.labels.length;

        const isHoveringOverLabel =
            mouseX >= xAxisLeft && mouseX <= xAxisRight && mouseY >= xAxisTop && mouseY <= xAxisBottom;

        if (isHoveringOverLabel) {
            canvas.style.cursor = 'pointer'; // Change to pointer when hovering over labels
        } else {
            canvas.style.cursor = 'default'; // Reset cursor when not hovering over labels
        }
    };

    // Handle click event to trigger actions on the x-axis label click
    const handleClick = (event) => {
        const canvas = containerRef.current.querySelector('canvas');
        if (!canvas) return;
    
        const chart = ChartJS.getChart(canvas);
        const xScale = chart.scales.x;
        const mouseX = event.nativeEvent.offsetX;
        const mouseY = event.nativeEvent.offsetY;
    
        // Get the range of the x-axis scale (left to right)
        const xAxisLeft = xScale.left;
        const xAxisRight = xScale.right;
        const xAxisTop = xScale.top;
        const xAxisBottom = xScale.bottom;
    
        // Ensure that the click is within the x-axis label area
        if (mouseY >= xAxisTop && mouseY <= xAxisBottom) {
            // Ensure the mouse is within the horizontal bounds of the x-axis
            if (mouseX >= xAxisLeft && mouseX <= xAxisRight) {
                // Use getValueForPixel to get the actual value at the mouse position on the x-axis
                const valueAtMouse = xScale.getValueForPixel(mouseX);
    
                // Get the nearest label index based on the value
                const labelIndex = Math.round(valueAtMouse);
    
                // Ensure the label index is within the range of the dataset
                if (labelIndex >= 0 && labelIndex < chart.data.labels.length) {
                    const clickedLabel = chart.data.labels[labelIndex];
                    alert(`You clicked on label: ${clickedLabel}`);
                }
            }
        }
    };

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
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
            },
            {
                label: series2,
                data: data2,
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgba(153, 102, 255, 1)',
                borderWidth: 1,
            },
            {
                label: series3,
                data: data3,
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                borderColor: 'rgba(255, 159, 64, 1)',
                borderWidth: 1,
            },
        ],
    };


    // Chart.js options
    const options = {
        responsive: true,
        barThickness: 'flex',
        maxBarThickness: 25,
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
            underlineKpi: false
        },
        scales: {
            x: {
                ticks: {
                    // Custom font styling for labels
                    font: {
                        size: 12, // Set font size
                        weight: 'normal', // Set font weight (normal, bold, etc.)
                        family: 'Arial, sans-serif', // Font family
                        lineHeight: 1.5, // Line height for spacing
                    },
                    // Set the label color and underline style (if possible)
                    color: 'blue', // Set label text color
                    // Unfortunately, there is no built-in way to underline with Chart.js, but this is the closest approach
                    /*callback: function(value, index, ticks) {
                        const label = this.getLabelForValue(value); // Use this method to get the label
                        return label + label;
                    },*/
                },
                // Ensure the x-axis is large enough to scroll
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
            const canvas = containerRef.current.querySelector('canvas');
            if (!canvas) return;
    
            const chart = ChartJS.getChart(canvas);
            if (!chart || !chartElement.length) return;
    
            const { datasetIndex } = chartElement[0];
    
            // Update borderColor for the hovered series
            chart.data.datasets.forEach((dataset, index) => {
                dataset.borderColor = index === datasetIndex ? 'black' : dataset.borderColor;
                dataset.borderWidth = index === datasetIndex ? 2 : 1;
            });
    
            chart.update(); // Update the chart to reflect the changes
        },
    };

    return (
        <Container ref={containerRef} onClick={handleClick} onMouseMove={handleMouseMove} style={{ height: "80%", width: "50%" }}>
            <Bar data={data} options={options} />
        </Container>
    );
};

const KpiChart = ({ analyticsData, chartName }) => {
    const containerRef = useRef(null); // Ref for the container

    // get data
    let labels = analyticsData.map((item) => item.name + " " + item.surname);
    let kpidata = analyticsData.map((item) => item.kpi);

    const handleClick = (event) => {
        const canvas = containerRef.current.querySelector('canvas'); // Find the canvas using the ref
        if (!canvas) return;

        const rect = canvas.getBoundingClientRect(); // Get canvas position
        const offsetX = event.clientX - rect.left; // X-coordinate relative to the canvas
        const offsetY = event.clientY - rect.top; // Y-coordinate relative to the canvas

        // Get the Chart.js instance
        const chart = ChartJS.getChart(canvas);
        if (!chart) {
            return;
        }

        const chartArea = chart.chartArea; // Chart's drawing area
        const yScale = chart.scales.y; // Access the y-axis scale

        // Check if the click is outside the chart area but near the y-axis labels
        if (offsetX < chartArea.left) {
            // Loop through y-axis ticks and check if the click is near a label
            yScale.ticks.forEach((tick, index) => {
                const tickPosition = yScale.getPixelForTick(index); // Get pixel position of the tick
                if (Math.abs(offsetY - tickPosition) < 10) { // Check proximity to tick
                    const label = tick.label; // Get the label text
                    alert(`You clicked on label: ${label}`);
                    // Replace the alert with your custom action
                }
            });
        }
    };

    const handleMouseMove = (event) => {
        const canvas = containerRef.current.querySelector('canvas'); // Find the canvas using the ref
        if (!canvas) return;

        const chart = ChartJS.getChart(canvas); // Get chart instance
        const yScale = chart.scales.y;

        // Get mouse position relative to the canvas
        const mouseX = event.nativeEvent.offsetX;
        const mouseY = event.nativeEvent.offsetY;

        // Check if the mouse is within the y-axis range
        const yAxisLeft = yScale.left;
        const yAxisRight = yScale.right;
        const yAxisTop = yScale.top;
        const yAxisBottom = yScale.bottom;

        // Calculate label area height
        const labelAreaHeight = (yAxisBottom - yAxisTop) / chart.data.labels.length;
        const labelHoverBottom = yAxisTop + labelAreaHeight * chart.data.labels.length;

        const isOverYAxisLabels =
            mouseX >= yAxisLeft && mouseX <= yAxisRight && mouseY >= yAxisTop && mouseY <= labelHoverBottom;

        // Change cursor style if hovering over y-axis labels
        if (isOverYAxisLabels) {
            canvas.style.cursor = 'pointer'; // Change to pointer (like a clickable link)
        } else {
            canvas.style.cursor = 'default'; // Reset to default cursor
        }
    };

    const data = {
        labels: labels,
        datasets: [
            {
                label: 'KPI',
                data: kpidata,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
            }
        ],
    };

    const options = {
        responsive: true,
        indexAxis: 'y', // Set the index axis to 'y' for horizontal bars
        barThickness: 'flex',
        maxBarThickness: 25,
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
            tooltip: {
                callbacks: {
                    label: function (tooltipItem) {
                        const datasetLabel = tooltipItem.dataset.label || '';
                        const value = tooltipItem.raw; // Access the raw data value
                        return `${datasetLabel}: ${value} %`; // Add percentage formatting
                    },
                },
            },
            underlineKpi: false,/* {
                yOffset: 5,
                lineWidth: 1,
                lineColor: 'blue'
            }*/
        },
        scales: {
            x: {
                beginAtZero: true, // Ensure the x-axis starts at zero
                max: 100,
            },
            y: {
                ticks: {
                    // Custom font styling for labels
                    font: {
                        size: 12, // Set font size
                        weight: 'normal', // Set font weight (normal, bold, etc.)
                        family: 'Arial, sans-serif', // Font family
                        lineHeight: 1.5, // Line height for spacing
                    },
                    // Set the label color and underline style (if possible)
                    color: 'blue', // Set label text color
                    // Unfortunately, there is no built-in way to underline with Chart.js, but this is the closest approach
                    /*callback: function(value, index, ticks) {
                        // Return label with underline using font style
                        return `Month ${value}`;
                    },*/
                },
                grid: {
                    display: false,
                },
                min: 0, // Start the y-axis from 0
                max: 8,  // Max value to allow scroll
            },
        },
        /*onClick: (event) => { FOR CLICK ON BARS (NOW ONLY ON LABELS)
            const chart = ChartJS.getChart(event.native.target); // Get the chart instance from the event
            const yScale = chart.scales.y; // Access the y-axis scale
            const yValue = yScale.getValueForPixel(event.native.offsetY); // Get value for clicked position
            const yLabel = yScale.getLabelForValue(yValue); // Get label for the value
    
            if (yLabel) {
                alert(`You clicked on label: ${yLabel}`);
                // Replace this with your desired custom action
            }
        },*/
    };

    return (
        <Container ref={containerRef} onMouseMove={handleMouseMove} onClick={handleClick} style={{ height: "80%", width: "50%" }}>
            <Bar data={data} options={options}/>
        </Container>
    );
};


/*
const data = {
        labels: Array.from({ length: 30 }, (_, i) => `Month ${i + 1}`),
        datasets: [
            {
                label: 'Series 1',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) ),
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: hoveredSeriesIndex === 0 ? 'black' : 'rgba(75, 192, 192, 1)',
                borderWidth: hoveredSeriesIndex === 0 ? 2 : 1,
            },
            {
                label: 'Series 2',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) ),
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: hoveredSeriesIndex === 1 ? 'black' : 'rgba(153, 102, 255, 1)',
                borderWidth: hoveredSeriesIndex === 1 ? 2 : 1,
            },
            {
                label: 'Series 3',
                data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 100) ),
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                borderColor: hoveredSeriesIndex === 2 ? 'black' : 'rgba(255, 159, 64, 1)',
                borderWidth: hoveredSeriesIndex === 2 ? 2 : 1,
            },
        ],
    };
    */

export default AnalyticsContainer;