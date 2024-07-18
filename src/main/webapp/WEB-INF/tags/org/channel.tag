<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- URL -->

<c:url value="/org/channel/setScreenAuto" var="setScreenAutoUrl" />


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements pl-2">
		<span class="lead">
			<span class="fa-stack fa-xs" title="광고 채널" style="vertical-align: top; color: #a0a0a0;">
				<span class="fa-solid fa-circle fa-stack-2x"></span>
				<span class="fa-regular fa-tower-cell fa-stack-1x fa-inverse fa-lg" data-fa-transform="right-1"></span>
			</span>
			<span id="channel-name"></span>
		</span>
		<div class="card-header-elements ml-auto p-0 m-0">
			<span class="lead mr-4">${Channel.shortName}</span>
			
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-channel" title="광고 채널 목록">
				<i class="fa-light fa-tower-cell fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-1" id="qlb-screen" title="화면 목록">
				<i class="fa-light fa-screen-users fa-lg"></i>
			</button>
			<button type="button" class="btn btn-outline-dark btn-round icon-btn btn-custom mr-3" id="qlb-syncpack" title="동기화 화면 묶음 목록">
				<i class="fa-light fa-rectangle-vertical-history fa-lg"></i>
			</button>
			<div class="btn-group">
				<button type="button" class="btn btn-outline-secondary btn-round icon-btn btn-custom" data-toggle="dropdown" title="이 광고 채널에 대해 ...">
					<i class="fa-light fa-ellipsis-stroke fa-lg"></i>
				</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="javascript:void(0)" id="qlb-set-screen">
						<i class="fa-light fa-screen-users fa-fw"></i><span class="pl-2">기준 화면 자동 설정</span>
					</a>
				</div>    			
			</div>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">

<c:choose>
<c:when test="${not empty Channel.viewTypeCode}">

				<span class="fa-thin fa-sidebar fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">게시 유형</div>
					<div class="text-large">
						<span class="pr-2">${Channel.viewTypeCode}</span><small><span class="text-muted">${Channel.resolutionDisp}</span></small>
					</div>
				</div>

</c:when>
<c:otherwise>

				<span class="my-2">
					<span class='badge badge-outline-secondary' style='font-weight: 300; font-size: 24px;'>px</span>
				</span>
				<div class="ml-3">
					<div class="text-muted small">해상도</div>
					<div class="text-large">
						${Channel.resolutionDisp}
					</div>
				</div>

</c:otherwise>
</c:choose>

			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">

<c:choose>
<c:when test="${Channel.appendMode == 'A'}">

				<span class="fa-thin fa-robot fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 추가 모드</div>
					<div class="text-large">자율선택</div>
				</div>

</c:when>
<c:when test="${Channel.appendMode == 'P'}">

				<span class="fa-thin fa-list-ol fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">광고 추가 모드</div>
					<div class="text-large">재생목록</div>
				</div>

</c:when>
</c:choose>

			</div>
		</div>
		<div class="col-sm-6 col-md-4">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-users fa-3x text-gray fa-fw"></span>
				<div class="ml-3">
					<div class="text-muted small">구독 수</div>
					<div class="text-large">
						${Channel.subCount}
					</div>
				</div>
			</div>
		</div>
	</div>

<c:if test="${not empty Channel.reqScreen}">
		
	<hr class="m-0">
	<div class="col-sm-12 col-md-12">
		<div class="d-flex align-items-center justify-content-center py-2">
			<span class="text-muted">자율 광고선택 기준 화면:</span><span class="pl-3">${Channel.reqScreen}</span>
		</div>
	</div>
		
</c:if>

</div>


<!-- Form button actions  -->

<script>
$(document).ready(function() {

	// Quick Link Buttons
	$("#qlb-channel").click(function(e) {
		e.preventDefault();
		
		location.href = "/org/channel";
	});

	$("#qlb-screen").click(function(e) {
		e.preventDefault();
		
		location.href = "/inv/screen";
	});

	$("#qlb-syncpack").click(function(e) {
		e.preventDefault();
		
		location.href = "/inv/syncpack";
	});

	$("#qlb-set-screen").click(function(e) {
		e.preventDefault();
		
		if ("${Channel.appendMode}" == "P") {
			showAlertModal("info", "광고 추가 모드가 [자율 광고선택]인 경우에만 유효합니다.");
		} else {
			
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${setScreenAutoUrl}",
				data: JSON.stringify({ id: ${Channel.id} }),
				success: function (data, status, xhr) {
					showOperationSuccessMsg();
					location.reload();
				},
				error: function(e) {
					var msg = JSON.parse(e.responseText).error;
					if (msg == "OperationError") {
						showOperationErrorMsg();
					} else {
						showAlertModal("danger", msg);
					}
				}
			});
		}
	});
	// / Quick Link Buttons

	var typeTitle = "<span>${Channel.name}</span>" +
			"<span title='우선순위' class='pl-2'><span class='fa-solid fa-crown fa-xs text-yellow'></span><span class='small'>${Channel.priority}</span></span>";
	
	$("#channel-name").html(typeTitle);
});
</script>


<style>

/* 아웃라인 버튼 파란색 */
.btn-outline-blue {
    border-color: rgba(72,125,242,0.9);
    background: transparent;
    color: rgba(72,125,242,0.9);
}
.btn-outline-blue:hover {
    border-color: transparent;
    background: rgba(72,125,242,0.9);
    color: #fff;
}


/* 그리드 행의 높이 지정 */
.card-table tbody tr, .k-grid tbody tr td
{
    height: 40px;
}

</style>

<!-- / Form button actions  -->

<!--  / HTML tags -->

