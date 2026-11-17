(function () {
    'use strict';

    function ready(fn) {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', fn);
        } else {
            fn();
        }
    }

    function findRow(node, root) {
        while (node && node !== root) {
            if (node.tagName === 'TR' && node.getAttribute('data-id')) {
                return node;
            }
            node = node.parentNode;
        }
        return null;
    }

    function init(root) {
        if (root.getAttribute('data-initialized') === 'true') {
            return;
        }
        root.setAttribute('data-initialized', 'true');

        var servlet = root.getAttribute('data-servlet');

        function selectRow(row) {
            var rows = root.querySelectorAll('tbody tr');
            for (var i = 0; i < rows.length; i++) {
                rows[i].style.backgroundColor = '';
            }
            row.style.backgroundColor = '#fdecea';
        }

        function deleteRow(row) {
            if (!window.confirm('Delete this contact?')) {
                return;
            }
            var xhr = new XMLHttpRequest();
            xhr.open('GET', servlet + '?action=delete&id=' + encodeURIComponent(row.getAttribute('data-id')), true);
            xhr.onreadystatechange = function () {
                if (xhr.readyState !== 4) {
                    return;
                }
                if (xhr.status === 200) {
                    row.parentNode.removeChild(row);
                    if (!root.querySelector('tbody tr')) {
                        window.location.reload();
                    }
                } else {
                    window.alert('Couldn\'t delete the contact. Try again.');
                }
            };
            xhr.send();
        }

        root.addEventListener('click', function (event) {
            var row = findRow(event.target, root);
            if (!row) {
                return;
            }
            selectRow(row);
            if (event.target.className === 'demo-table__delete') {
                deleteRow(row);
            }
        });
    }

    document.addEventListener('demo:contacts-changed', function () {
        window.setTimeout(function () {
            window.location.reload();
        }, 800);
    });

    ready(function () {
        var roots = document.querySelectorAll('.demo-table');
        for (var i = 0; i < roots.length; i++) {
            init(roots[i]);
        }
    });
}());