<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- Screen Info Modal -->

<div class="modal fade modal-level-plus-1" id="scrLocModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header move-cursor">
                <h5 class="modal-title" rowid="-1">
                	<span id="scrLocModalTitle"></span>
                	<span class="font-weight-light pl-1">이동형 화면 위치</span>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body py-1">
				<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="btn-screen-info-ok" data-dismiss="modal">확인</button>
            </div>
        </div>
    </div>
</div>

<!-- / Screen Info Modal -->


<!--  Scripts -->

<script>

var scrLocDate = null;


function showScrLoc(id, name, date) {
	$("#scrLocModalTitle").attr("rowid", id);
	$("#scrLocModalTitle").text(name);
	
    $("#scrLocModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>잠시 기다려 주십시오</div>");
    
    if (date) {
    	scrLocDate = date;
    }
    
	$('#scrLocModal .modal-dialog').draggable({ handle: '.modal-header' });
	$("#scrLocModal").modal();
}


$(document).ready(function() {
	$("#scrLocModal").on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
		
		if (scrLocDate) {
			var tmpDate = scrLocDate;
			scrLocDate = null;
			$("#scrLocModal .modal-body").load("/adn/common/scrLoc?id=" + 
					$("#scrLocModalTitle").attr("rowid") + "&date=" + tmpDate);
		} else {
			$("#scrLocModal .modal-body").load("/adn/common/scrLoc?id=" + 
					$("#scrLocModalTitle").attr("rowid"));
		}
	});
});

</script>

<!--  / Scripts -->
