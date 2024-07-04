<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adc/gallery/read" var="readUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>




<!-- Page body -->


<!-- Page scripts  -->

<script type="text/javascript" src="/resources/vendor/lib/masonry/masonry.js"></script>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4" id="thumbnail-tabs">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#all" id="A">
			<i class="mr-1 fa-light fa-photo-film"></i>
			모든
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#progressing" id="P">
			<i class="mr-1 fa-light fa-bolt-lightning"></i>
			진행 중
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#recent-reg" id="R">
			<i class="mr-1 fa-light fa-asterisk"></i>
			최근 등록
		</a>
	</li>
</ul>

<div class="card" style="display: none;" id="gallery-no-data-div">
	<div class="card-body">
		<div class="form-row">
			<div class='container text-center my-4'>
				<div class='d-flex justify-content-center align-self-center'>
					<span class='fa-thin fa-diamond-exclamation fa-3x'></span>
					<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>등록된 광고 없음</span>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row form-row p-0" id="gallery-thumbnails" style="min-height: 100px;"></div>


<style>

/* 커스텀 링크 텍스트 */
.cust-link-a {
	color: inherit;
	text-decoration: none;
}
.cust-text-link {
	font-size: 14px;
}

</style>


<script id="itemViewTemplate" type="text/x-kendo-template">

<div class="creat-item card card-condenced mb-2" ukid="#: id #" data-tag="#: type #">
	<div class="card-body align-items-center p-0" style="width: 144px;">
		<div class="p-2">
			<a href='#= link #' target="_blank" class="img-thumbnail img-thumbnail-zoom-in"><Img name="thumb-img" src='/thumbs/#= thumbUri #' class='thumb-128'></a>
		</div>
		<div class="pt-2">
			<div class='d-flex justify-content-center mb-2'><a href='javascript:navToCreat(#= advId #, #= creatId #)' class="cust-link-a"><span class='text-link cust-text-link'>#: creatName #</span></a></div>
			<div class="text-muted small px-2">
				# if (mediaType == "V") { #
					<span data-toggle='tooltip' title="동영상"><span class="fa-solid fa-film text-primary"></span></span>
					<span data-toggle="tooltip" title="재생시간" class="pl-1 default-pointer">#: duration #</span>
				# } else if (mediaType == "I") { #
					<span data-toggle='tooltip' title="이미지"><span class="fa-solid fa-image text-primary"></span></span>
				# } #
				<span data-toggle="tooltip" title="파일크기" class="pl-1 default-pointer">#: fileLength #</span>
				<span data-toggle="tooltip" title="해상도" class="pl-1 default-pointer">#: resolution #</span>
				<span data-toggle="tooltip" title="등록" class="pl-1 default-pointer">#: dispRegDate #</span>
			</div>
		</div>
	</div>
</div>

</script>

<!--  Scripts -->

<script>

var msnry = null;

$(document).ready(function() {
	
	showWaitModal();
	
	$('#thumbnail-tabs a').on('shown.bs.tab', function(e){
		var id = $(e.target).attr("id");
		
		if (id == "A") {
			$('#gallery-thumbnails .creat-item').removeClass('d-none');
		} else {
			$("#gallery-thumbnails .creat-item[data-tag*='" + id + "']").removeClass('d-none');
			$("#gallery-thumbnails .creat-item:not([data-tag*='" + id + "'])").addClass('d-none');
		}
		
		if (msnry != null) {
		    // Relayout
		    msnry.layout();
		}
	});
	

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			
			//$('#gallery-thumbnails').html("<div class='gallery-sizer col-3 col-sm-4 col-md-2 col-xl-1 position-absolute'></div>");
			
			var template = kendo.template($("#itemViewTemplate").html());
			
			for(var i in data) {
				$('#gallery-thumbnails').append(template(data[i]));
			}
			
			if (data.length == 0) {
				$('#gallery-no-data-div').show();
			} else {
				
				msnry = new Masonry('#gallery-thumbnails', {
					itemSelector: '.creat-item:not(.d-none)',
					columnWidth: 153,
				});
				
				$('[data-toggle="tooltip"]').tooltip();
				
				setTimeout(function(){
				    // Relayout
				    msnry.layout();
				}, 500);
			}
			
			hideWaitModal();
		},
		error: function(e) {
			hideWaitModal();
			ajaxReadError(e);
		}
	});
	
});


function navToCreat(advId, id) {
	var path = "/adc/creative/files/" + advId + "/" + id;
	location.href = path;
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
