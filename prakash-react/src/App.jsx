
import { useEffect, useState } from 'react';
import './App.css';

function App() {
    const [employees, setEmployees] = useState([]);
    const [search, setSearch] = useState('');
    const [location, setLocation] = useState('All');
    const [department, setDepartment] = useState('All');

    useEffect(() => {

        fetch('/content/experience-fragments/demo/us/en/CompanyDetailExp/master.model.json')
            .then(response => response.json())
            .then(data => {

                const root = data[":items"].root;

                const employeeComponents = root[":itemsOrder"].map(key => {
                    return root[":items"][key];
                });

                const employeeData = employeeComponents.map(component => {

                    const elements = component.elements;

                    return {
                        name: elements.name.value,
                        email: elements.email.value,
                        location: elements.location.value,
                        department: elements.department.value,
                        designation: elements.designation.value
                    };

                });

                setEmployees(employeeData);

            })
            .catch(error => console.error("Error fetching employees:", error));

    }, []);

    // Search and filter employees
    const filteredEmployees = employees.filter(employee => {

        const matchesSearch = employee.name
            .toLowerCase()
            .includes(search.toLowerCase());

        const matchesLocation =
            location === 'All' || employee.location === location;

        const matchesDepartment =
            department === 'All' || employee.department === department;

        return matchesSearch && matchesLocation && matchesDepartment;

    });

    // Get unique locations and departments
    const locations = [...new Set(employees.map(emp => emp.location))];
    const departments = [...new Set(employees.map(emp => emp.department))];

    return (

        <div className="container">

            <h1>Employee Management Dashboard</h1>

            <h3>Total Employees: {employees.length}</h3>

            <div className="filters">

                <input
                    type="text"
                    placeholder="Search employee name..."
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />

                <select
                    value={location}
                    onChange={e => setLocation(e.target.value)}
                >
                    <option value="All">All Locations</option>

                    {locations.map(loc => (
                        <option key={loc} value={loc}>{loc}</option>
                    ))}

                </select>

                <select
                    value={department}
                    onChange={e => setDepartment(e.target.value)}
                >
                    <option value="All">All Departments</option>

                    {departments.map(dept => (
                        <option key={dept} value={dept}>{dept}</option>
                    ))}

                </select>

            </div>

            <div className="employee-list">
                {filteredEmployees.length > 0 ? (

                    filteredEmployees.map((employee, index) => (

                        <div className="employee-card" key={employee.email}>

                            <h2>{employee.name}</h2>

                            <p><strong>Email:</strong> {employee.email}</p>
                            <p><strong>Location:</strong> {employee.location}</p>
                            <p><strong>Department:</strong> {employee.department}</p>
                            <p><strong>Designation:</strong> {employee.designation}</p>

                        </div>

                    ))

                ) : (

                    <p>No employees found.</p>

                )}

            </div>

            <p>Showing {filteredEmployees.length} of {employees.length} employees</p>

        </div>

    );

}

export default App;
