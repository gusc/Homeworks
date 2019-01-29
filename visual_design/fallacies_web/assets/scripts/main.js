var sectionHeights = [];
var sections = [];

function recalculateHeights() {
    $(window).scrollTop(0);
    var sec = $('article');
    sections[0] = sec;
    sectionHeights[0] = sec.outerHeight(true);
    sec.removeClass('fixed').removeClass('fixed-last');
    sec.css({ marginTop: '0px'});
    $('section').each(function(i, item){
        var sec = $(item);
        sections[i + 1] = sec;
        sectionHeights[i + 1] = sec.outerHeight(true);
        sec.removeClass('fixed').removeClass('fixed-last');
        sec.css({ marginTop: '0px'});
    });
}
function updateFixed() {
    if ($(window).width() < 960)
    {
        return;
    }
    var scrollPos = $(window).scrollTop();
    var offset = 0;
    var i = 0;
    var lastFixed = 0;
    for (var i = 0; i < sections.length; i ++) {
        if (scrollPos >= offset)
        {
            if (i + 1 < sections.length)
            {
                sections[i].addClass('fixed');
                sections[i].css({ marginTop: '0px'});
                sections[i + 1].css({ marginTop: offset + sectionHeights[i] + 'px'});
            }
            else
            {
                sections[i].addClass('fixed-last');
            }
            lastFixed = i;
        }
        else
        {
            sections[i].removeClass('fixed').removeClass('fixed-last');
            if (i > lastFixed + 1)
            {
                sections[i].css({marginTop: '0px'});
            }
        }
        offset += sectionHeights[i];
    }
}

$(document).ready(function(){
    recalculateHeights();
    updateFixed();
    $(window).scroll(function(){
        updateFixed();
    });
    $(window).resize(function(){
        recalculateHeights();
        updateFixed();
    });
    if (window.location.hash) {
        window.location = window.location;
    }
    $('#switch-original').on('click', function(e){
        e.preventDefault();
        $('body').addClass('original');
    });
    $('#switch-typical').on('click', function(e){
        e.preventDefault();
        $('body').removeClass('original');
    });
});