var tags = [];
var removeTags = [];
$(function () {
    $.each($('#tagdisp').children('span'), function (i, label) {
        tags.push($(label).text().replace("x", ""));
    });

    $("#tag").change(function () {
        var inputVal = $(this).val();
        if (inputVal.length == 0) {
            return;
        }

        var exists = false;
        $.each(tags, function (i, value) {
            if (value == inputVal) {
                exists = true;
                return;
            }
        });

        var removeExists = false;
        var index = -1;
        $.each(removeTags, function (i, value) {
            if (value == inputVal) {
                removeExists = true;
                index = i;
                return;
            }
        });

        if (exists && removeExists) {
            var removeParam = $("#hidden" + inputVal + "-remove");
            $(removeParam).remove();
            removeTags.splice(index, 1);

            var span = $("<span>", {
                class: 'label label-info',
            }).text($(this).val());

            spanremove = $("<span>", {
                text: 'x',
                class: 'init-tag-remove'
            }).click(function() {
                initTagRemove($(this));
            });
            spanremove.appendTo(span);

            span.appendTo('#tagdisp');
            return;
        } else if (exists) {
            return;
        }

        $("<input>", {
            id: 'hidden' + $(this).val(),
            type: 'hidden',
            name: 'tags[]',
            value: $(this).val()
        }).appendTo('#tagdisp');

        var span = $("<span>", {
            class: 'label label-info',
        }).text($(this).val());

        spanremove = $("<span>", {
            text: 'x',
            class: 'tag-remove'
        }).click(function () {
            var label = $(this).closest(".label-info");
            $("#hidden" + label.text().replace("x", "")).remove();
            $(label).remove();
        });
        spanremove.appendTo(span);

        span.appendTo('#tagdisp');
    });

    $("span.init-tag-remove").click(function () {
        initTagRemove($(this));
    });
});

function initTagRemove(self) {
    var label = $(self).closest(".label-info");
    $("<input>", {
        id: 'hidden' + $(label).text().replace("x", "") + "-remove",
        type: 'hidden',
        name: 'tags[]',
        value: $(label).text().replace("x", "") + "-remove"
    }).appendTo('#tagdisp');
    removeTags.push($(label).text().replace("x", ""));
    $(label).remove();
}