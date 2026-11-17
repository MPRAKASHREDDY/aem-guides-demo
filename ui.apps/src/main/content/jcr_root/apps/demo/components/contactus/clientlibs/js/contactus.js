(function () {
    'use strict';

    var EMAIL = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
    var MOBILE = /^\d{10}$/;

    function ready(fn) {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', fn);
        } else {
            fn();
        }
    }

    function buildQuery(params) {
        var parts = [];
        for (var key in params) {
            if (Object.prototype.hasOwnProperty.call(params, key)) {
                parts.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
            }
        }
        return parts.join('&');
    }

    function init(root) {
        if (root.getAttribute('data-initialized') === 'true') {
            return;
        }
        root.setAttribute('data-initialized', 'true');

        var form = root.querySelector('.demo-contact__form');
        var button = root.querySelector('.demo-contact__submit');
        var message = root.querySelector('.demo-contact__message');
        var servlet = root.getAttribute('data-servlet');

        function field(name) {
            return form.querySelector('[name="' + name + '"]');
        }

        function show(text, isError) {
            message.textContent = text;
            message.style.color = isError ? '#b3261e' : '#1e7b34';
        }

        function onClick(event) {
            event.preventDefault();

            var id = root.getAttribute('data-id');
            var data = {
                action: id ? 'update' : 'create',
                name: field('name').value.trim(),
                email: field('email').value.trim(),
                mobile: field('mobile').value.trim()
            };
            if (id) {
                data.id = id;
            }

            if (!data.name) {
                show('Enter a name', true);
                return;
            }
            if (!EMAIL.test(data.email)) {
                show('Enter a valid email', true);
                return;
            }
            if (!MOBILE.test(data.mobile)) {
                show('Enter a 10-digit mobile number', true);
                return;
            }

            var xhr = new XMLHttpRequest();
            xhr.open('GET', servlet + '?' + buildQuery(data), true);
            xhr.onreadystatechange = function () {
                if (xhr.readyState !== 4) {
                    return;
                }
                var result = {};
                try {
                    result = JSON.parse(xhr.responseText);
                } catch (e) {
                    result = {};
                }
                if (xhr.status === 200 && result.status === 'ok') {
                    show(result.message, false);
                    if (!id) {
                        form.reset();
                    }
                    document.dispatchEvent(new CustomEvent('demo:contacts-changed'));
                } else {
                    show(result.message || 'Couldn\'t reach the server. Try again.', true);
                }
            };
            xhr.send();
        }

        button.addEventListener('click', onClick);
        form.addEventListener('submit', onClick);
    }

    ready(function () {
        var roots = document.querySelectorAll('.demo-contact');
        for (var i = 0; i < roots.length; i++) {
            init(roots[i]);
        }
    });
}());