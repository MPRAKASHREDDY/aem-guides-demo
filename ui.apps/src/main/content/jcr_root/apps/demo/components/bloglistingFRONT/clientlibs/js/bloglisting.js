
document.addEventListener("DOMContentLoaded", function () {

    // API URL
    const apiURL = "https://www.lumen.com/blog/query-index.json";

    // Get HTML elements
    const blogGrid = document.querySelector(".blog-listing-grid");
    const loadingMessage = document.querySelector(".blog-loading");
    const errorMessage = document.querySelector(".blog-error");

    // Check whether the component exists on the page
    if (!blogGrid) {
        return;
    }

    // Fetch JSON data from API
    fetch(apiURL)

        // Convert response into JSON
        .then(function (response) {

            if (!response.ok) {
                throw new Error("API request failed: " + response.status);
            }

            return response.json();
        })

        // Receive JSON data
        .then(function (data) {

            console.log("Complete API Response:", data);

            // Hide loading message
            loadingMessage.hidden = true;

            // Get blog records
            const blogs = Array.isArray(data.data)
                ? data.data
                : [];

            console.log("Blog Data:", blogs);

            if (blogs.length === 0) {
                blogGrid.innerHTML = "<p>No blogs found.</p>";
                return;
            }

            // Loop through blog records
            blogs.forEach(function (blog) {

                // Create blog card
                const blogCard = document.createElement("div");
                blogCard.classList.add("blog-card");

                // Create blog title
                const title = document.createElement("h3");
                title.textContent = blog.title || blog.Title || "Untitled Blog";

                // Create description
                const description = document.createElement("p");
                description.textContent =
                    blog.description ||
                    blog.Description ||
                    blog.excerpt ||
                    "No description available.";

                // Create image if available
                const image = document.createElement("img");

                const imageURL =
                    blog.image ||
                    blog.imageURL ||
                    blog.Image ||
                    "";

                if (imageURL) {
                    image.src = imageURL;
                    image.alt = title.textContent;
                    image.loading = "lazy";
                    blogCard.appendChild(image);
                }

                // Create Read More link
                const link = document.createElement("a");

                link.textContent = "Read More";

                const blogPath = blog.path || blog.url || "";

                if (blogPath) {
                    link.href = blogPath;
                } else {
                    link.href = "#";
                }

                // Append elements to blog card
                blogCard.appendChild(title);
                blogCard.appendChild(description);
                blogCard.appendChild(link);

                // Display card on frontend
                blogGrid.appendChild(blogCard);

            });

        })

        // Handle errors
        .catch(function (error) {

            console.error("Error fetching JSON:", error);

            loadingMessage.hidden = true;
            errorMessage.hidden = false;

        });

});
