(function ($, document) {
    "use strict";

    $(document).on("foundation-contentloaded", function (event) {

        var dialog = event.target;

        var select = dialog.querySelector(
            "coral-select[name='./image']"
        );

        if (!select || select.dataset.imagesLoaded) {
            return;
        }

        select.dataset.imagesLoaded = "true";

        $.ajax({
            url: "/bin/demo/images",
            type: "GET",
            dataType: "json"

        }).done(function (images) {

            images.forEach(function (image) {

                var option = document.createElement(
                    "coral-select-item"
                );

                option.setAttribute("value", image.path);

                option.textContent = image.name;

                select.appendChild(option);

            });

            console.log("Images loaded successfully:", images);

        }).fail(function (error) {

            console.error(
                "Failed to fetch DAM images",
                error
            );

        });

    });

})(Granite.$, document);
