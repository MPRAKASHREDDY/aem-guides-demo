(function () {

    "use strict";

    // STEP 1: EDS API URL
    const BLOG_INDEX_URL =
        "https://wordpress.org/news/wp-json/wp/v2/posts";

    // STEP 2: Execute when HTML is ready
    document.addEventListener("DOMContentLoaded", function () {

        const containers =
            document.querySelectorAll(".blog-component");

        containers.forEach(function (container) {

            fetchBlogs(container);

        });

    });


    // STEP 3: Fetch blog articles
    async function fetchBlogs(container) {

        const loading =
            container.querySelector("#blog-loading");

        const error =
            container.querySelector("#blog-error");

        const empty =
            container.querySelector("#blog-empty");

        const blogContainer =
            container.querySelector("#blog-container");

        try {

            // STEP 4: Send GET API request
            const response = await fetch(BLOG_INDEX_URL, {

                method: "GET",

                headers: {
                    "Accept": "application/json"
                }

            });

            // STEP 5: Check HTTP response
            if (!response.ok) {

                throw new Error(
                    "HTTP Error: " + response.status
                );

            }

            // STEP 6: Convert response into JSON
            const result = await response.json();

            console.log("Complete API Response:", result);

            // STEP 7: Get data array
            const articles = result.data || [];

            console.log("Total Articles:", articles.length);

            loading.hidden = true;

            // STEP 8: Check whether articles exist
            if (articles.length === 0) {

                empty.hidden = false;
                return;

            }

            // STEP 9: Display articles
            displayArticles(articles, blogContainer);

        }

        catch (err) {

            console.error("API Calling Error:", err);

            loading.hidden = true;

            error.textContent =
                "Unable to load blog articles: " + err.message;

            error.hidden = false;

        }

    }


    // STEP 10: Display articles
    function displayArticles(articles, container) {

        container.innerHTML = "";

        articles.forEach(function (article) {

            // Create blog card
            const card = document.createElement("div");

            card.className = "blog-card";

            // Create image
            const image = document.createElement("img");

            const imageUrl =
                article.image || article.imagePath || "";

            if (imageUrl) {

                try {

                    image.src = new URL(
                        imageUrl,
                        BLOG_INDEX_URL
                    ).href;

                    image.alt = article.imageAlt || article.title || "Blog image";

                } catch (error) {

                    console.warn("Invalid image URL:", imageUrl);

                }

            }

            // Create title
            const title = document.createElement("h2");

            title.textContent = article.title || "Untitled";

            // Create description
            const description = document.createElement("p");

            description.textContent =
                article.description || "";

            // Create author
            const author = document.createElement("p");

            author.textContent =
                "Author: " + (article.blogAuthor || "Unknown");

            // Create categories
            const categories = document.createElement("p");

            categories.textContent =
                "Categories: " + formatValue(article.categories);

            // Create tags
            const tags = document.createElement("p");

            tags.textContent =
                "Tags: " + formatValue(article.tags);

            // Create keywords
            const keywords = document.createElement("p");

            keywords.textContent =
                "Keywords: " + formatValue(article.keywords);

            // Create published date
            const publishedDate = document.createElement("p");

            publishedDate.textContent =
                "Published: " + (article.blogPublishedDate || "");

            // Create read duration
            const duration = document.createElement("p");

            duration.textContent =
                "Read Duration: " + (article.blogReadDuration || "");

            // Append elements to card
            if (imageUrl && image.src) {
                card.appendChild(image);
            }

            card.appendChild(title);
            card.appendChild(description);
            card.appendChild(author);
            card.appendChild(categories);
            card.appendChild(tags);
            card.appendChild(keywords);
            card.appendChild(publishedDate);
            card.appendChild(duration);

            // Append card to main container
            container.appendChild(card);

        });

    }


    // STEP 11: Format arrays or strings
    function formatValue(value) {

        if (Array.isArray(value)) {

            return value.join(", ");

        }

        return value || "";

    }

})();
