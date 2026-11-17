
(function ($, $document) {
    "use strict";

    function showMessage(message) {
        window.alert(message);
    }

    function getFieldLabel(wrapper) {
        var label = wrapper.querySelector(
            ".coral-Form-fieldlabel, label"
        );

        return label ? label.textContent.trim() : "";
    }

    function getFieldValue(wrapper) {

        // Rich text / editable content
        var editable = wrapper.querySelector(
            '[contenteditable="true"]'
        );

        if (editable) {
            return editable.innerText.trim();
        }

        // Checkbox
        var checkbox = wrapper.querySelector(
            'input[type="checkbox"]'
        );

        if (checkbox) {
            return checkbox.checked ? checkbox.value : "";
        }

        // Normal inputs, dropdowns and textareas
        var field = wrapper.querySelector(
            'input:not([type="hidden"]):not([type="checkbox"]), textarea, select'
        );

        if (field) {
            return field.value.trim();
        }

        return "";
    }

    function validateDialog(event) {

        var button = event.target.closest(".cq-dialog-submit");

        if (!button) {
            return;
        }

        var dialog = button.closest("coral-dialog");

        if (!dialog) {
            return;
        }

        var wrappers = dialog.querySelectorAll(
            ".coral-Form-fieldwrapper"
        );

        var missingFields = [];
        var totalFields = 0;
        var authoredFields = 0;

        wrappers.forEach(function (wrapper) {

            var label = getFieldLabel(wrapper);

            if (!label) {
                return;
            }

            totalFields++;

            var value = getFieldValue(wrapper);

            if (!value) {
                missingFields.push(label);
            } else {
                authoredFields++;
            }

        });

        if (totalFields === 0 || authoredFields === 0) {

            event.preventDefault();
            event.stopImmediatePropagation();

            showMessage(
                "None of the fields are authored in the cq:dialog."
            );

            return;
        }

        if (missingFields.length > 0) {

            event.preventDefault();
            event.stopImmediatePropagation();

            var message;

            if (missingFields.length === 1) {
                message = missingFields[0] +
                    " is not authored in the cq:dialog.";
            } else {
                message = missingFields.join(", ") +
                    " are not authored in the cq:dialog.";
            }

            showMessage(message);
        }
    }

    document.addEventListener(
        "click",
        validateDialog,
        true
    );

})(Granite.$, Granite.$(document));
