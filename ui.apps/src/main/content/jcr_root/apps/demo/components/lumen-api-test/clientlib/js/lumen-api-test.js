(function () {

    "use strict";

    const apiUrl =
        "https://www.lumen.com/blog/query-index.json";

    function fetchLumenArticles() {

        console.log("====================================");
        console.log("LUMEN API TEST STARTED");
        console.log("====================================");

        console.log("API URL:", apiUrl);

        fetch(apiUrl, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        })

        .then(function (response) {

            console.log("HTTP Status:", response.status);
            console.log("Response OK:", response.ok);

            if (!response.ok) {
                throw new Error(
                    "HTTP Error: " + response.status
                );
            }

            return response.json();

        })

        .then(function (data) {

            console.log("LUMEN API RESPONSE:", data);

            if (!data || !Array.isArray(data.data)) {
                throw new Error(
                    "Unexpected JSON response structure"
                );
            }

            console.log(
                "Total Articles:",
                data.data.length
            );

            console.log("ARTICLE DETAILS:");

            data.data.forEach(function (article, index) {

                console.log("------------------------------");

                console.log("Article Number:", index + 1);
                console.log("Path:", article.path);
                console.log("Title:", article.title);
                console.log("Description:", article.description);
                console.log("Tags:", article.tags);
                console.log("Keywords:", article.keywords);
                console.log("Image:", article.image);
                console.log("Image Alt:", article.imageAlt);
                console.log("Image Path:", article.imagePath);
                console.log(
                    "Published Date:",
                    article.blogPublishedDate
                );
                console.log("Author:", article.blogAuthor);
                console.log("Categories:", article.categories);
                console.log(
                    "Read Duration:",
                    article.blogReadDuration
                );

            });

            console.log("LUMEN API TEST COMPLETED");

        })

        .catch(function (error) {

            console.error(
                "LUMEN API REQUEST FAILED:",
                error
            );

        });

    }

    document.addEventListener(
        "DOMContentLoaded",
        fetchLumenArticles
    );

})();
