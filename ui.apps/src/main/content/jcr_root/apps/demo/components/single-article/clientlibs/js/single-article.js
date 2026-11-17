(function ($, document) {
    "use strict";
    $(document).on("foundation-contentloaded", function (event) {
        var $dialog = $(event.target);
        var $checkbox = $dialog.find("[name='./enablePII']");
        var $cssClass = $dialog.find("[name='./cssClass']");

        if (!$checkbox.length || !$cssClass.length) {
            return;
        }

        function updateField() {
            var isChecked = $checkbox.prop("checked");

            if (isChecked) {
                $cssClass.prop("disabled", false);
            } else {
                $cssClass.val("");
                $cssClass.prop("disabled", true);
            }
        }

        $checkbox.on("change", updateField);

        updateField();
    });
})(Granite.$, document);