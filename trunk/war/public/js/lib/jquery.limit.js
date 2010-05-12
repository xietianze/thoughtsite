(function($) {
    $.fn
            .extend( {
                limit : function(limit, element) {

                    var interval, f;
                    var self = $(this);

                    $(this)
                            .keypress(function(event) {
                                // get element
                                    var val = $(self).val();
                                    // Get the code of key pressed
                                    var keyCode = event.keyCode;
                                    // Check if it has a selected text
                                    var hasSelection = document.selection ? document.selection
                                            .createRange().text.length > 0
                                            : this.selectionStart != this.selectionEnd;
                                    // return false if can't write more
                                    if ((val.length >= limit
                                            && (keyCode > 50 || keyCode == 32
                                                    || keyCode == 0 || keyCode == 13)
                                            && !event.ctrlKey && !event.altKey && !hasSelection)) {
                                        return false;
                                    }
                                });

                    $(this).keyup(function(event) {
                        // If the keypress fail and allow write more text that
                            // required, this event will remove it
                            substring();
                        });

                    substringFunction = "function substring(){ var val = $(self).val();var length = val.length;if(length > limit){$(self).val($(self).val().substring(0,limit));}";
                    if (typeof element != 'undefined')
                        substringFunction += "if($(element).html() != limit-length){$(element).html((limit-length<=0)?'0':limit-length);}"

                    substringFunction += "}";

                    eval(substringFunction);
                    // run first time when it loads
                    substring();
                }
            });
})(jQuery);