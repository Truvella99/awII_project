import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Dropdown, DropdownButton, Container } from "react-bootstrap";
import { MessageContext, TokenContext } from "../messageCtx";
import { useContext } from "react";
import API from "../API";
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, plugins } from 'chart.js';
import zoomPlugin from 'chartjs-plugin-zoom';
import ChartjsPluginScrollBar from 'chartjs-plugin-scroll-bar';

function AnalyticsContainer({ chartName, setIsEmpty }) {
    const [data, setData] = useState([]);
    const handleErrors = useContext(MessageContext);
    const xsrfToken = useContext(TokenContext);

    useEffect(() => {
        async function getCustomerAnalytics() {
            try {
                const customers = await API.getCustomersAnalytics(xsrfToken);
                //console.log(customers);
                setData(customers);
                setIsEmpty(customers.length === 0);
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
                setIsEmpty(professionals.length === 0);
            } catch (err) {
                //console.log(err);
                handleErrors({ detail: err.message });
            }
        }
        if (chartName.includes("Customers")) {
            getCustomerAnalytics();
        } else {
            getProfessionalAnalytics();
        }
    }, [chartName]);

    return (
        <div style={{ display: 'flex-column', justifyContent: 'space-between', alignItems: 'center', flex: 1 }}>
            {((chartName.includes("KPI")) ? <KpiChart analyticsData={data} chartName={chartName} />
            : <JobOffersChart analyticsData={data} chartName={chartName} />)}
        </div>
    );
}

function redirect(chartName, id) {
    if (chartName.includes("Customers")) {
        window.open(`/ui/customers/${id}`, '_blank');
    } else {
        window.open(`/ui/professionals/${id}`, '_blank');
    }
}

// Registering chart components and zoom plugin
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, zoomPlugin, ChartjsPluginScrollBar);// underLineKpiPlugin);

const JobOffersChart = ({ analyticsData, chartName }) => {
    const containerRef = useRef(null); // Reference to the container
    console.log(analyticsData);
    // get data dependeing on chartName
    let labels = analyticsData.map((item) => item.name + " " + item.surname);
    let ids = analyticsData.map((item) => item.id);
    let series1, series2, series3;
    let data1,data2,data3;
    if (chartName === "Customers") {
        series1 = "abortedJobOffers";
        data1 = analyticsData.map((item) => item.abortedJobOffers);
        series2 = "completedJobOffers";
        data2 = analyticsData.map((item) => item.completedJobOffers);
        series3 = "createdJobOffers";
        data3 = analyticsData.map((item) => item.createdJobOffers);
    } else {
        series1 = "abortedJobOffers";
        data1 = analyticsData.map((item) => item.abortedJobOffers);
        series2 = "completedJobOffers";
        data2 = analyticsData.map((item) => item.completedJobOffers);
        series3 = "candidatedJobOffers";
        data3 = analyticsData.map((item) => item.candidatedJobOffers);
    }

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
        const xAxisBottom = xScale.bottom - 10;

        // Determine the height of the x-axis labels
        // const labelHeight = (xAxisBottom - xAxisTop) / chart.data.labels.length;

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
        const xAxisBottom = xScale.bottom - 10;
    
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
                    //const clickedLabel = chart.data.labels[labelIndex];
                    //alert(`You clicked on label: ${clickedLabel}`);
                    redirect(chartName, ids[labelIndex]);
                }
            }
        }
    };

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
            scrollBar: {enable: true, scrollType: 'Horizontal'}
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
                    callback: function(value, index, ticks) {
                        // if no data return empty label
                        if (analyticsData.length === 0) {
                            return "";
                        }
                        const labels = this.getLabelForValue(value).split(" "); // Use this method to get the label
                        return labels.map((label, index) => {
                            if (label.length > 21) {
                                return label.substring(0, 18) + '...';
                            }
                            return label;
                        })
                    },
                    maxRotation: 0, // Prevent rotation
                    minRotation: 0, // Prevent rotation
                    autoSkip: false, // Ensure no labels are skipped
                    padding: 10
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
        <Container ref={containerRef} onClick={handleClick} onMouseMove={handleMouseMove} style={{width: "100%" }}>
            <Bar data={data} options={options} />
        </Container>
    );
};

const KpiChart = ({ analyticsData, chartName }) => {
    const containerRef = useRef(null); // Ref for the container

    // get data
    let labels = analyticsData.map((item) => item.name + " " + item.surname);
    let ids = analyticsData.map((item) => item.id);
    let kpidata = analyticsData.map((item) => item.kpi);

    const handleClick = (event) => {
        const canvas = containerRef.current.querySelector('canvas');
        if (!canvas) return;
    
        const chart = ChartJS.getChart(canvas);
        if (!chart) return;
    
        const yScale = chart.scales.y;
        const chartArea = chart.chartArea;
    
        const rect = canvas.getBoundingClientRect();
        const offsetX = event.clientX - rect.left; // X-coordinate relative to canvas
        const offsetY = event.clientY - rect.top; // Y-coordinate relative to canvas
    
        // Compute the longest label
        let longestName = 0;
        yScale.ticks.forEach((tick) => {
            const label = tick.label;
            if (label.length > longestName) {
                longestName = label.length;
            }
        });
    
        // Shrink the clickable area based on the longest label length
        const shrinkAmount = Math.round(80 * longestName / 10);
        const reducedLeftBoundary = chartArea.left - shrinkAmount;
    
        // Check if the click is within the reduced label area
        const yAxisTop = yScale.top;
        const yAxisBottom = yScale.bottom;
    
        const isWithinYAxisLabels =
            offsetX >= reducedLeftBoundary &&
            offsetX < chartArea.left &&
            offsetY >= yAxisTop &&
            offsetY <= yAxisBottom;
    
        if (isWithinYAxisLabels) {
            // Check which label was clicked
            yScale.ticks.forEach((tick, labelIndex) => {
                const tickPosition = yScale.getPixelForTick(labelIndex); // Get pixel position of the tick
                if (Math.abs(offsetY - tickPosition) < 10) { // Check proximity to the tick
                    //const label = tick.label; // Get the label text
                    //alert(`You clicked on label: ${label}`);
                    redirect(chartName, ids[labelIndex]);
                }
            });
        }
    };    

    const handleMouseMove = (event) => {
        const canvas = containerRef.current.querySelector('canvas');
        if (!canvas) return;
    
        const chart = ChartJS.getChart(canvas);
        if (!chart) return;
    
        const yScale = chart.scales.y;
        const mouseX = event.nativeEvent.offsetX;
        const mouseY = event.nativeEvent.offsetY;
    
        const chartArea = chart.chartArea;
    
        // compute longest name
        let longestName = 0;
        yScale.ticks.forEach((tick, index) => {
            const label = tick.label; // Get the label text
            if (label.length > longestName) {
                longestName = label.length;
            }
        });
        // Shrink the hover area based on the longest name
        const shrinkAmount = Math.round(80 * longestName/10);
        // Reduce the hover area for y-axis labels
        const reducedLeftBoundary = chartArea.left - shrinkAmount;
        const yAxisTop = yScale.top;
        const yAxisBottom = yScale.bottom;
    
        const isOverYAxisLabels =
            mouseX >= reducedLeftBoundary &&
            mouseX < chartArea.left &&
            mouseY >= yAxisTop &&
            mouseY <= yAxisBottom;
    
        if (isOverYAxisLabels) {
            canvas.style.cursor = 'pointer';
        } else {
            canvas.style.cursor = 'default';
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
                text: `${chartName} Analytics`,
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
            scrollBar: {enable: true, scrollType: 'Vertical'},
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
                    padding: 20
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
        <Container ref={containerRef} onMouseMove={handleMouseMove} onClick={handleClick} style={{ width: "100%" }}>
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
underlineKpi: false,/* {
                yOffset: 5,
                lineWidth: 1,
                lineColor: 'blue'
            }*/

export {AnalyticsContainer};