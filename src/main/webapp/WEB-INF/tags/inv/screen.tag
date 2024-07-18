<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="화면" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-screen-users fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="screen-name"></span>
		</span>
		<div class="card-header-elements ml-auto p-0 m-0">
			<span class="lead mr-4">${Screen.shortName}</span>
			
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-site" title="사이트 목록">
				<i class="fa-light fa-map-pin fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom" id="qlb-screen" title="화면 목록">
				<i class="fa-light fa-screen-users fa-lg"></i>
			</button>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300; font-size: 24px;'>px</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">해상도</div>
					<div class="text-large">${Screen.resolutionDisp}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300; font-size: 24px;'>ver</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">플레이어 Ver.</div>
					<div class="text-large">
						${Screen.playerVer}
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">

<c:choose>
<c:when test="${not empty Screen.syncPackName}">

				<span class="fa-thin fa-rectangle-vertical-history fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">동기화 화면 묶음</div>
					<div class="text-large">
						${Screen.syncPackName}
					</div>
				</div>

</c:when>
<c:otherwise>

				<span class="fa-thin fa-box-taped fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">화면 묶음</div>
					<div class="text-large">
						${Screen.scrPackName}
					</div>
				</div>

</c:otherwise>
</c:choose>

			</div>
		</div>
	</div>
</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	// Quick Link Buttons
	$("#qlb-site").click(function(e) {
		e.preventDefault();
		
		location.href = "/inv/site";
	});

	$("#qlb-screen").click(function(e) {
		e.preventDefault();
		
		location.href = "/inv/screen";
	});
	// / Quick Link Buttons

	var screenTitle = "<span>${Screen.name}</span>";
	if ('${Screen.reqStatus}' == '6') {
		screenTitle += "<span title='10분내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>";
	} else if ('${Screen.reqStatus}' == '5') {
		screenTitle += "<span title='1시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>";
	} else if ('${Screen.reqStatus}' == '4') {
		screenTitle += "<span title='6시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>";
	} else if ('${Screen.reqStatus}' == '3') {
		screenTitle += "<span title='24시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>";
	} else if ('${Screen.reqStatus}' == '1') {
		screenTitle += "<span title='24시간내 없음' class='p-1'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>";
	} else if ('${Screen.reqStatus}' == '0') {
		screenTitle += "<span title='기록 없음' class='p-1'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>";
	}
	
	$("#screen-name").html(screenTitle);
	
});
</script>

<!-- / Form button actions  -->

<!--  / HTML tags -->

