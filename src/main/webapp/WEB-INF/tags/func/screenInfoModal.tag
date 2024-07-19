<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- Screen Info Modal -->

<div class="modal fade modal-level-plus-1" id="screenInfoModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header move-cursor">
                <h5 class="modal-title" rowid="-1">
                	<span id="screenInfoModalTitle"></span>
                	<span id="screenInfoModalTitleTag" class="pl-1"></span>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body py-1">
				<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>
            </div>
            <div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="navToScrWork()">
                	<span class="fa-light fa-arrow-down-left"></span>
                	<span class="ml-1">화면 업무</span>
				</button>
                <button type="button" class="btn btn-primary" id="btn-screen-info-ok" data-dismiss="modal">확인</button>
            </div>
        </div>
    </div>
</div>

<!-- / Screen Info Modal -->


<!--  Scripts -->

<script>

var screenInfoDate = null;
var scrWorkId = null;

function showScreen(id, name, date) {
	$("#screenInfoModalTitle").attr("rowid", id);
	$("#screenInfoModalTitle").text(name);
	
    $("#screenInfoModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>");
    
    if (date) {
    	screenInfoDate = date;
    }
    
    scrWorkId = id;

	$('#screenInfoModal .modal-dialog').draggable({ handle: '.modal-header' });
	$("#screenInfoModal").modal();
}


function navToScrWork() {

	if (scrWorkId) {
		var path = "/inv/screen/" + scrWorkId;
		location.href = path;
	}
}


$(document).ready(function() {
	$("#screenInfoModal").on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
		
		if (screenInfoDate) {
			var tmpDate = screenInfoDate;
			screenInfoDate = null;
			$("#screenInfoModal .modal-body").load("/adn/common/screenInfo?id=" + 
					$("#screenInfoModalTitle").attr("rowid") + "&date=" + tmpDate);
		} else {
			$("#screenInfoModal .modal-body").load("/adn/common/screenInfo?id=" + 
					$("#screenInfoModalTitle").attr("rowid"));
		}
	});
});

</script>

<!--  / Scripts -->
