import React, { useEffect, useState } from 'react';
import './App.css';

function App() {

    const [fragment, setFragment] = useState(null);

    useEffect(() => {

        fetch('/content/experience-fragments/demo/us/en/employee-exp/master.model.json')

            .then(response => response.json())

            .then(data => {

                console.log('AEM JSON:', data);

                const contentFragment =
                    data[":items"].root[":items"].contentfragment;

                setFragment(contentFragment);

            })

            .catch(error => {
                console.error('Error fetching AEM data:', error);
            });

    }, []);

    if (!fragment) {
        return <h2>Loading Employee Data...</h2>;
    }

    return (
        <div className="container">

            <h1>Employee Information</h1>

            <div className="card">

                <h2>{fragment.title}</h2>

                {fragment.elementsOrder.map(key => (

                    <p key={key}>
                        <strong>{fragment.elements[key].title}: </strong>
                        {fragment.elements[key].value}
                    </p>

                ))}

            </div>

        </div>
    );
}

export default App;
