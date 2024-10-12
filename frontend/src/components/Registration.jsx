import React, { useEffect, useState } from "react";
import { Form, Button, Container, Row, Col } from "react-bootstrap";

function RegistrationForm({ me }) {
	const [formData, setFormData] = useState({
		name: "",
		surname: "",
		ssnCode: "",
		email: "",
		telephone: "",
		address: "",
		company: "",
		password: "",
		confirmPassword: "",
	});
	const [errors, setErrors] = useState({});

	const handleChange = (e) => {
		const { name, value } = e.target;
		setFormData({
			...formData,
			[name]: value,
		});
	};

	const validateForm = () => {
		let formErrors = {};
		if (formData.password !== formData.confirmPassword) {
			formErrors.confirmPassword = "Passwords do not match";
		}
		if (formData.password.length < 8) {
			formErrors.password = "Password must be at least 8 characters long";
		}
		setErrors(formErrors);
		return Object.keys(formErrors).length === 0;
	};

	const handleSubmit = (e) => {
		e.preventDefault();
		if (validateForm()) {
			// Handle form submission
			console.log("Form submitted", formData);
		}
	};

	return (
		<>
		<Container>
			<Form onSubmit={handleSubmit}>
				<Row>
					<Col>
						<Form.Group controlId="formName" className="mb-3">
							<Form.Label>Name</Form.Label>
							<Form.Control type="text" name="name" value={formData.name} onChange={handleChange} />
						</Form.Group>
					</Col>
					<Col>
						<Form.Group controlId="formSurname" className="mb-3">
							<Form.Label>Surname</Form.Label>
							<Form.Control type="text" name="surname" value={formData.surname} onChange={handleChange} />
						</Form.Group>
					</Col>
				</Row>
				<Form.Group controlId="formSSNCode" className="mb-3">
					<Form.Label>SSN Code</Form.Label>
					<Form.Control type="text" name="ssnCode" value={formData.ssnCode} onChange={handleChange} />
				</Form.Group>
				<Form.Group controlId="formEmail" className="mb-3">
					<Form.Label>Email</Form.Label>
					<Form.Control type="email" name="email" value={formData.email} onChange={handleChange} />
				</Form.Group>
				<Form.Group controlId="formTelephone" className="mb-3">
					<Form.Label>Telephone Number</Form.Label>
					<Form.Control type="text" name="telephone" value={formData.telephone} onChange={handleChange} />
				</Form.Group>
				<Form.Group controlId="formAddress" className="mb-3">
					<Form.Label>Address</Form.Label>
					<Form.Control type="text" name="address" value={formData.address} onChange={handleChange} />
				</Form.Group>
				<Form.Group controlId="formCompany" className="mb-3">
					<Form.Label>Company Name</Form.Label>
					<Form.Control type="text" name="company" value={formData.company} onChange={handleChange} />
				</Form.Group>
				<Form.Group controlId="formPassword" className="mb-3">
					<Form.Label>Password</Form.Label>
					<Form.Control type="password" name="password" value={formData.password} onChange={handleChange} />
					{errors.password && <Form.Text className="text-danger">{errors.password}</Form.Text>}
				</Form.Group>
				<Form.Group controlId="formConfirmPassword" className="mb-3">
					<Form.Label>Confirm Password</Form.Label>
					<Form.Control type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} />
					{errors.confirmPassword && <Form.Text className="text-danger">{errors.confirmPassword}</Form.Text>}
				</Form.Group>
				<div className="d-flex justify-content-end">
					<Button variant="primary" type="submit">
						Register
					</Button>
				</div>
			</Form>
		</Container>
		</>
	);
}

export { RegistrationForm };
