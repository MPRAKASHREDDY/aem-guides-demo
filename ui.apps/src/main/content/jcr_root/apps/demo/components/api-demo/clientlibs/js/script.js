
document.addEventListener("DOMContentLoaded", function () {

    const button = document.getElementById("loadUsers");
    const container = document.getElementById("user-container");

    if (!button || !container) {
        return;
    }

    button.addEventListener("click", async function () {

        container.textContent = "Loading users...";

        try {
            // Step 1: Make HTTP GET request
            const response = await fetch(
                "https://jsonplaceholder.typicode.com/users"
            );

            // Step 2: Check API response
            if (!response.ok) {
                throw new Error("HTTP Error: " + response.status);
            }

            // Step 3: Convert response into JSON
            const users = await response.json();

            // Step 4: Check JSON data in console
            console.log("API Response:", users);

            // Step 5: Display data in HTML
            container.replaceChildren();

            users.forEach(function (user) {

                const card = document.createElement("div");
                card.className = "user-card";

                const name = document.createElement("h3");
                name.textContent = user.name;

                const email = document.createElement("p");
                email.textContent = "Email: " + user.email;

                const city = document.createElement("p");
                city.textContent = "City: " + user.address.city;

                card.appendChild(name);
                card.appendChild(email);
                card.appendChild(city);

                container.appendChild(card);
            });

        } catch (error) {

            console.error("API Error:", error);

            container.textContent = "Unable to fetch users.";
        }
    });

});
