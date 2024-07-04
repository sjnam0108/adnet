<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="사이트" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-solid fa-map-pin fa-stack-1x fa-inverse fa-lg"></span>
			</span>
			<span id="site-name"></span>
		</span>
		<div class="card-header-elements ml-auto p-0 m-0">
			<span class="lead mr-4">${Site.shortName}</span>
			
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-site" title="사이트 목록">
				<i class="fa-light fa-map-pin fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom" id="qlb-screen" title="화면 목록">
				<i class="fa-light fa-screen-users fa-lg"></i>
			</button>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-5">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-mountain-city fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">시/군/구</div>
					<div class="text-large">${Site.regionName}</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-location-crosshairs fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">입지 유형</div>
					<div class="text-large">
						${Site.siteCond.name}
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-3">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-screen-users fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">화면 수</div>
					<div class="text-large">
						${Site.screenCount}
					</div>
				</div>
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

	var siteTitle = "<span>${Site.name}</span>";
	if ('${Site.reqStatus}' == '6') {
		siteTitle += "<span title='10분내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-blue'></span></span>";
	} else if ('${Site.reqStatus}' == '5') {
		siteTitle += "<span title='1시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-green'></span></span>";
	} else if ('${Site.reqStatus}' == '4') {
		siteTitle += "<span title='6시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-yellow'></span></span>";
	} else if ('${Site.reqStatus}' == '3') {
		siteTitle += "<span title='24시간내 요청' class='p-1'><span class='fa-solid fa-flag-swallowtail fa-fw text-orange'></span></span>";
	} else if ('${Site.reqStatus}' == '1') {
		siteTitle += "<span title='24시간내 없음' class='p-1'><span class='fa-solid fa-flag-pennant fa-fw text-danger'></span></span>";
	} else if ('${Site.reqStatus}' == '0') {
		siteTitle += "<span title='기록 없음' class='p-1'><span class='fa-solid fa-flag-pennant fa-fw text-secondary'></span></span>";
	}
	
	$("#site-name").html(siteTitle);
	
});
</script>

<!-- / Form button actions  -->

<!--  / HTML tags -->

