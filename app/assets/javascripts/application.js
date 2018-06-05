function formWithZeroJumpNumber(form) {
    return _.map(form.serializeArray(), function (entry) {
        if (entry.name === 'jumpNumber') {
            entry.value = 0;
        }
        return entry;
    });
}

function openPopup(url) {
    window.open(url, 'reportpopup', 'top=200,height=760,width=820,resizable=yes,scrollbars=yes,location=no,menubar=no,status=yes,toolbar=no').focus();
}

function replaceTextArea(textArea) {
    var name = textArea.attr('name')
    var placeHolder = textArea.attr('placeholder')
    var value = textArea.val()
    var editor = $('<div>'+value+'</div>')
    $.each(textArea[0].attributes, function(index, element) {
        var name = this.name;
        var value = this.value;
        if (name !== 'name' && name !== 'placeHolder' ) {
            editor.attr(name, value)
        }
    });

    textArea.replaceWith(editor)
    editor.after('<input type="hidden" name="'+name+'" value=""/>')
    editor.addClass('text-area-editor')
    return {placeHolder: placeHolder, name: name}

}

function convertToEditor(textArea) {
    var id = '#' + textArea.attr('id')
    var areaAttributes = replaceTextArea(textArea)
    var name = areaAttributes.name;
    var placeHolder = areaAttributes.placeHolder

    var editor = new Quill(id, {
        placeholder: placeHolder,
        theme: 'snow',
        formats: ['bold', 'italic', 'underline', 'list'],
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                ['clean']
            ]
        }
    })

    function cleanHtml() {
        return '<!-- RICH_TEXT -->' +  editor.root.innerHTML.replace(/<br>/gm,'<br/>')
    }

    function hasAnyText() {
        return editor.getText().trim()
    }

    function transferValueToInput() {
        if (hasAnyText()) {
            $("input[name='"+ name + "']").val(cleanHtml())
        } else {
            $("input[name='"+ name + "']").val('')
        }
    }
    editor.on('text-change', function(delta, oldDelta, source) {
        if (source == 'user') {
            transferValueToInput()
        }
    })

    transferValueToInput()

    // swap toolbar to below editor
    var toolbar = $(id).prev()
    toolbar.before($(id));

    // show/hide toolbar with focus change
    editor.on('selection-change', function(range) {
        if (range) {
            $(id).next().css('visibility', 'visible')
        } else {
            $(id).next().css('visibility', 'hidden')
        }
    })
    // add classes to reduce margins
    $(id).closest('.form-group').addClass('small-margin-bottom')
    $(id).closest('.form-group').next('hr').addClass('small-margin-top')

    // remove tab key binding for editor, toolbar (and for IE11 svg)
    delete editor.getModule('keyboard').bindings[9];

    toolbar.find(':button').attr('tabindex', '-1')
    toolbar.find('svg').attr('focusable', 'false')
    toolbar.find('button').addClass('tooltip')
    toolbar.find('.ql-bold svg').after(withModifier('<span>Bold (⌘B)</span>'))
    toolbar.find('.ql-italic svg').after(withModifier('<span>Italic (⌘I)</span>'))
    toolbar.find('.ql-underline svg').after(withModifier('<span>Underline (⌘U)</span>'))
    toolbar.find('.ql-list[value="ordered"] svg').after('<span>Numbered List</span>')
    toolbar.find('.ql-list[value="bullet"] svg').after('<span>Bulleted List</span>')
    toolbar.find('.ql-clean svg').after('<span>Remove Formatting</span>')

}

function withModifier(text) {
    if (/Mac/i.test(navigator.platform)) {
        return text
    }
    return text.replace('⌘', 'Ctrl-')
}

(function ($) {

    'use strict';

    $(function () {

        // Show/hide content
        var showHideContent = new GOVUK.ShowHideContent();

        showHideContent.init();

        // Stick at top when scrolling
        GOVUK.stickAtTopWhenScrolling.init();

        /**
         *
         * @param parent
         * @param child
         */
        function showHint(parent, child) {
            child.hasClass('js-hidden') ? child.removeClass('js-hidden') : child.addClass('js-hidden');

            if (child.hasClass('js-hidden')) {
                parent.removeClass('active');
                parent.attr('aria-expanded', 'false');
                child.attr('aria-hidden', 'true');
            } else {
                parent.addClass('active');
                parent.attr('aria-expanded', 'true');
                child.attr('aria-hidden', 'false');
            }
        }

        /**
         * Start save icon
         * @param elem
         */
        function startSaveIcon(elem) {
            var saveIcon = $('#save_indicator'),
                spinner = $('.spinner', saveIcon);

            saveIcon.removeClass('js-hidden');
            spinner.removeClass('error');
            spinner.addClass('active');
        }

        /**
         * End save icon
         * @param elem
         * @param error
         */
        function endSaveIcon(elem, error) {
            var saveIcon = $('#save_indicator'),
                spinner = $('.spinner', saveIcon),
                errorMessage = $('#' + elem.attr('id') + '-autosave_error'),
                formGroup = $(elem).closest('.form-group');

            if (error) {
                saveIcon.addClass('js-hidden');
                spinner.removeClass('active');
                errorMessage.removeClass('js-hidden');
                formGroup.addClass('form-group-autosave-error');
            } else {
                // remove all autosave errors on this page
                $('.form-group-autosave-error').removeClass('form-group-autosave-error');
                $('.autosave-error-message').addClass('js-hidden');
                spinner.removeClass('active');
            }
        }

        /**
         * Save
         */
        function saveProgress(elem) {
            if ($('form').length) {
                startSaveIcon(elem);
                $.ajax({
                    type: 'POST',
                    url: $('form').attr('action') + '/save',
                    data: formWithZeroJumpNumber($('form')),
                    complete: function(response) {
                        _.delay(endSaveIcon, 500, elem, response.status !== 200)
                    }
                });
            }
        }

        var quietSaveProgress = _.debounce(saveProgress, 500);

        function smartenEditor(editor, lengthCalculator) {
            quietSaveProgress($(editor));

            var textArea = $(editor),
                limit = textArea.data('limit'),
                current = lengthCalculator(editor),
                messageHolder = $('#' + textArea.attr('id') + '-countHolder'),
                messageTarget = $('#' + textArea.attr('id') + '-count');

            if (limit && current > 0) {
                messageHolder.removeClass('js-hidden');
                messageTarget.text(limit + ' recommended characters, you have used ' + current);
            } else {
                messageHolder.addClass('js-hidden');
            }

        }


        /**
         * Textarea elements
         */
         $('textarea').keyup(function () {
             var editor = this
             smartenEditor(editor, function() {
                 return $(editor).val().length
             })
        })

        /**
         * Navigation items
         */
        $('.nav-item').click(function (e) {

            e.preventDefault();

            var target = $(this).data('target');
            if (target && !$(this).hasClass('active')) {
                $('#jumpNumber').val(target);
                $('form').submit();
            }
        });

        /**
         * Feedback link - change form action and submit
         */
        $('.feedback-link').click(function (e) {
            e.preventDefault();

            var form = $('form');
            form.attr('action', form.attr('action') + '/feedback').submit();
        });

        /**
         * Ensure jumpNumber is cleared if next after clicking browser back button
         */
        $('#nextButton:not(.popup-launcher)').click(function () {
            $('#jumpNumber').val('');
        });

        /**
         * Save and exit
         */
        $('#exitLink').click(function (e) {
            e.preventDefault();
            $('#jumpNumber').val(0);
            $('form').submit();
        });

        /**
         *
         */
        $('a.expand-content').each(function (i, elem) {
            var parent = $(this),
                child = $('#' + elem.getAttribute('data-target'));
            parent.attr('aria-controls', elem.getAttribute('data-target'));
            parent.click(function () {
                showHint(parent, child)
            });
        });

        // Progressive enhancement for browsers > IE8
        if (!$('html').is('.lte-ie8')) {
            // Autocomplete
            var autoComplete = document.querySelector('.auto-complete');
            if (autoComplete) {
                accessibleAutocomplete.enhanceSelectElement({
                    selectElement: autoComplete,
                    name: autoComplete.id,
                    defaultValue: '',
                    required: true
                });
            }

            // Date picker
            $('.date-picker').datepicker({
                dateFormat: 'dd/mm/yy'
            }).parent().addClass('date-wrapper');


            // htmlunit no longer supports IE8 or conditionals so also check agent to rule out IE8
            // not needing once we upgrade away from HTMLUnit
            if (navigator.userAgent.indexOf('MSIE 8.0') === -1) {
                $('textarea').each(function (i, elem) {
                    convertToEditor($(elem))
                })
            } else {
                $('textarea').each(function (i, elem) {
                    $(elem).css('visibility', 'visible')
                })
            }
            // Autosize all Textarea elements (does not support IE8).
            autosize(document.querySelectorAll('textarea'));

        }

        $('.text-area-editor').keyup(function () {
            var editor = this
            smartenEditor(editor, function() {
                return Quill.find(editor).getText().trim().length
            })
        })


    });

    /**
     * Reveal or hide the other role section when 'Other' is chosen in the role drop down
     */
    $(document).on('change','#role',function(e){
        if ($('#role option:selected').text() === 'Other') {
            $('#roleother-section').removeClass('js-hidden')
        } else {
            $('#roleother-section').addClass('js-hidden');
        }
    });
})(window.jQuery);
