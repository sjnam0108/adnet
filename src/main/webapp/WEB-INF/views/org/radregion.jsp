<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/org/radregion/create" var="createUrl" />
<c:url value="/org/radregion/update" var="updateUrl" />
<c:url value="/org/radregion/read" var="readUrl" />
<c:url value="/org/radregion/destroy" var="destroyUrl" />

<c:url value="/org/radregion/readGeo" var="readGeoUrl" />


<!-- Opening tags -->

<common:mediumPageOpeningNaverMap />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/smartwizard/smartwizard.css">
<script src="/resources/vendor/lib/smartwizard/smartwizard.js"></script>

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";

	String noRecordsTemplate =
			"<div class='container text-center my-4'>" +
				"<div class='d-flex justify-content-center align-self-center'>" +
					"<span class='fa-thin fa-hexagon-exclamation fa-3x'></span>" +
					"<span class='pl-3 mt-2' style='font-weight: 300; font-size: 1.3rem;'>해당 자료 없음</span>" +
				"</div>" +
			"</div>";
			
	String radiusTemplate = 
			"# if (radius < 1000) { #" +
				"<span>#= radius #m</span>" +
			"# } else { #" +
				"<span>#= radius / 1000 #km</span>" +
			"# } #";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true">
    <kendo:grid-selectable mode="multiple, raw"/>
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-filterable extra="false" />
	<kendo:grid-noRecords template="<%= noRecordsTemplate %>" />
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="지역명" field="name" width="200" />
		<kendo:grid-column title="중심" field="center" width="300" filterable="false" sortable="false" />
		<kendo:grid-column title="반경" field="radius" width="100" template="<%= radiusTemplate %>" />
		<kendo:grid-column title="활성화 상태" field="activeStatus" width="200"
				template="#=activeStatus ? \"<span class='fa-light fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="name" dir="asc"/>
		</kendo:dataSource-sort>
       	<kendo:dataSource-filter>
      		<kendo:dataSource-filterItem field="medium.id" operator="eq" logic="and" value="${sessionScope['currMediumId']}" >
      		</kendo:dataSource-filterItem>
  	    </kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="radius" type="number" />
					<kendo:dataSource-schema-model-field name="activeStatus" type="boolean" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>



</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initSmartWizard();

		
		$('#wizard-modal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#wizard-modal").modal();
	});
	// / Add
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var delRows = [];
		
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.id);
		});
		
		if (delRows.length > 0) {
			showDelConfirmModal(function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${destroyUrl}",
						data: JSON.stringify({ items: delRows }),
						success: function (form) {
        					showDeleteSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxDeleteError
					});
				}
			}, true, delRows.length);
		}
	});
	// / Delete
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="sw-template" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="wizard-modal">
	<div class="modal-dialog modal-lg">
		<form class="modal-content" id="wizard-form" rowid="-1" url="${createUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle">${form_add}</span></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div name="smartWizard">
					<ul>
						<li name="tab-0">
							<a href="\\\#sw-step-1" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-map"></span></span>
								위치
								<div class="text-muted small">중심 및 반경</div>
							</a>
						</li>
						<li name="tab-1">
							<a href="\\\#sw-step-2" class="mb-3">
								<span class="sw-done-icon"><span class="fa-regular fa-check"></span></span>
								<span class="sw-icon"><span class="fa-regular fa-pen-field"></span></span>
								폼 입력
								<div class="text-muted small">지역명</div>
							</a>
						</li>
					</ul>

					<div class="">
						<div id="sw-step-1" class="card animated fadeIn">
							<div class="card-body p-0">
							
								<div id="radius-map-setting" style="background-color: white; height: 600px; width: 100%;">
								</div>

								<div class="d-flex justify-content-center my-0">
									<div id="map-action-btn-div" style="display: none;" class="py-2">
										<div class="card-body p-0">
											<button class="btn btn-outline-secondary btn-sm" id="radius-btn">
												<span class="fa-solid fa-circle-dot text-success"></span>
												<span class="pl-1">반경 설정 시작</span>
											</button>
											<button class="btn btn-outline-secondary btn-sm ml-2" id="cancel-marker-btn">
												<span class="fa-regular fa-delete-left"></span>
												<span class="pl-1">마커 취소</span>
											</button>
										</div>
									</div>
									<div id="radius-slider-div" style="width: 100%; display: none;" class="py-2">
										<div class="card-body p-0 d-flex px-2">
											<button class="btn btn-outline-secondary btn-sm" id="cancel-proc-btn">
												<span class="fa-regular fa-delete-right"></span>
												<span class="pl-1">작업 취소</span>
											</button>
											<div class="slider-primary mx-3 flex-grow-1">
												<input id="radius-slider" type="text" >
											</div>
											<button class="btn btn-outline-secondary btn-sm" id="go-next-btn">
												<span class="fa-regular fa-arrow-right text-success"></span>
												<span class="pl-1">위치 작업 완료</span>
											</button>
										</div>
									</div>
								</div>
							
							</div>
						</div>
						<div id="sw-step-2" class="card animated fadeIn py-3">
						
							<div class="card-body py-0">
								<div class="form-row mb-3">
									<div class="form-group col-12 mb-0">
										<label class="form-label">
											이름
											<span class="text-danger">*</span>
										</label>
										<input name="name" type="text" maxlength="100" class="form-control required">
									</div>
								</div>
								<div class="form-row mb-3">
									<div class="col-sm-12">
										<label class="form-label">
											중심 정보
										</label>
										<input name="center" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="form-row mb-3">
									<div class="col-sm-8">
										<label class="form-label">
											위도, 경도
										</label>
										<input name="loc" type="text" class="form-control" readonly="readonly">
									</div>
									<div class="col-sm-4">
										<label class="form-label">
											반경
										</label>
										<input name="radius" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="form-row">
									<div class="col-sm-12">
										<label class="form-label">
											활성화 상태
										</label>
										<div class="pt-1">
											<label class="switcher switcher-lg">
												<input type="checkbox" class="switcher-input" name="activeStatus" checked>
												<span class="switcher-indicator">
													<span class="switcher-yes">
														<span class="fa-solid fa-check"></span>
													</span>
													<span class="switcher-no">
														<span class="fa-solid fa-xmark"></span>
													</span>
												</span>
											</label>
										</div>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default ml-auto" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary disabled" onclick='saveWizardForm()' name="saveBtn">저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

var radSlider = null;
var map = null;
var marker = null;
var circle = null;

var reviewLat = null;
var reviewLng = null;
var reviewRad = null;

function initSmartWizard(subtitle) {
	
	$("#formRoot").html(kendo.template($("#sw-template").html()));
	
	$("#wizard-form div[name='smartWizard']").smartWizard({
		showStepURLhash: false,
		toolbarSettings: {
			showNextButton: false,
			showPreviousButton: false,
		}
	}).on('showStep', function(e, anchorObject, stepNumber, stepDirection) {
		
		if (stepNumber == 1) {
			
			setReviewValues();

			$("#wizard-form input[name='loc']").val(reviewLat + ", " + reviewLng);
			$("#wizard-form input[name='radius']").val(getHumanRadiusBySliderValue(radSlider.slider("getValue")));
			
			if (!$("#wizard-form input[name='center']").val()) {
				
				$("#wizard-form input[name='center']").val("가져오는 중...");
				

				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${readGeoUrl}",
					data: JSON.stringify({ lat: marker.position.lat(), lng: marker.position.lng() }),
					success: function (data) {
						
						$("#wizard-form input[name='center']").val(data);
					},
					error: ajaxReadError
				});
			}
		}
	}).on("leaveStep", function(e, anchorObject, currentStepIndex, nextStepIndex, stepDirection) {
		
		if (currentStepIndex == 0) {
			
			setReviewValues();
			
			var nextAllowed = $("#radius-slider-div").is(":visible");
			if (nextAllowed) {
				$("#wizard-form button[name='save-btn']").removeClass("disabled");
			} else {
				$("#wizard-form button[name='save-btn']").addClass("disabled");
			}
			
			return nextAllowed;
		} else {
			
			$("#wizard-form button[name='save-btn']").addClass("disabled");
			
			return true;
		}
	});
	
	$("#wizard-form span[name='subtitle']").text(subtitle ? subtitle : "추가");
	

	$("#wizard-form").validate({
		rules: {
		}
	});
	
	radSlider = $("#radius-slider").slider({
    	tooltip: "always",
    	min: 0,
    	max: 10,
    	value: 3,
    	formatter: function(value) {
    		return getHumanRadiusBySliderValue(value);
    	},
    }).on('change', function(event) {
    	drawCircle();
    });
    
	
	$("#radius-btn").click(function(e) {
		e.preventDefault();
		
		radSlider.slider("setValue", 3);

		$("#radius-slider-div").show();
		$("#map-action-btn-div").hide();
		
		map.setCenter(marker.getPosition());
		map.setZoom(15);
		
		drawCircle();
	});
    
	
	$("#cancel-marker-btn").click(function(e) {
		e.preventDefault();

		marker.setPosition(null);
		marker.setVisible(false);

		circle.setVisible(false);

		$("#radius-slider-div").hide();
		$("#map-action-btn-div").hide();
	});
    
	
	$("#cancel-proc-btn").click(function(e) {
		e.preventDefault();

		marker.setPosition(null);
		marker.setVisible(false);

		circle.setVisible(false);

		$("#radius-slider-div").hide();
		$("#map-action-btn-div").hide();
	});
    
	
	$("#go-next-btn").click(function(e) {
		e.preventDefault();

		$("#wizard-form div[name='smartWizard']").smartWizard("next");
	});
	
	drawMap();
}


function getSliderRadius(value) {
	
	if (value == 0) { return 50; }
	else if (value == 1) { return 100; }
	else if (value == 2) { return 200; }
	else if (value == 3) { return 300; }
	else if (value == 4) { return 500; }
	else if (value == 5) { return 750; }
	else if (value == 6) { return 1000; }
	else if (value == 7) { return 1500; }
	else if (value == 8) { return 2000; }
	else if (value == 9) { return 3000; }
	else if (value == 10) { return 5000; }
	else { return null; }
}


function getHumanRadiusBySliderValue(value) {

	return getHumanRadiusByRadius(getSliderRadius(value));
}


function getSliderValueFromRadius(radius) {
	
	if (radius) {
		if (radius == 50) { return 0; }
		else if (radius == 100) { return 1; }
		else if (radius == 200) { return 2; }
		else if (radius == 300) { return 3; }
		else if (radius == 500) { return 4; }
		else if (radius == 750) { return 5; }
		else if (radius == 1000) { return 6; }
		else if (radius == 1500) { return 7; }
		else if (radius == 2000) { return 8; }
		else if (radius == 3000) { return 9; }
		else if (radius == 5000) { return 10; }
	}
	
	return 0;
}


function getHumanRadiusByRadius(radius) {
	
	if (radius) {
		if (radius < 1000) {
			return radius + "m";
		} else {
			return (radius / 1000) + "km";
		}
	}
	
	return "";
}


function setReviewValues() {
	
	if (marker == null || marker.position == null) {
		reviewLat = null;
		reviewLng = null;
		reviewRad = null;
	} else {
		reviewLat = marker.position.lat();
		reviewLng = marker.position.lng();
		reviewRad = getSliderRadius(radSlider.slider("getValue"));
	}
}


function saveWizardForm() {

	if (!$("#wizard-form button[name='save-btn']").hasClass("disabled") && $("#wizard-form").valid()) {
		
    	var data = {
       		id: Number($("#wizard-form").attr("rowid")),
       		lat: reviewLat,
       		lng: reviewLng,
       		radius: reviewRad,
       		name: $.trim($("#wizard-form input[name='name']").val()),
       		activeStatus: $("#wizard-form input[name='activeStatus']").is(':checked'),
       	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#wizard-form").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#wizard-modal").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function edit(id) {
	
	initSmartWizard("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#wizard-form").attr("rowid", dataItem.id);
	$("#wizard-form").attr("url", "${updateUrl}");
	
	$("#wizard-form input[name='name']").val(dataItem.name);


	$("#wizard-form input[name='activeStatus']").prop("checked", dataItem.activeStatus);
	
    marker.setPosition(new naver.maps.LatLng(dataItem.lat, dataItem.lng));
	marker.setVisible(true);
	
	$("#wizard-form input[name='center']").val("");
	
	circle.setVisible(false);
	
	radSlider.slider("setValue", getSliderValueFromRadius(dataItem.radius));

	$("#radius-slider-div").show();
	$("#map-action-btn-div").hide();
	
	
	var zoomLevel = 15;
	if (dataItem.radius <= 100) {
		zoomLevel = 18;
	} else if (dataItem.radius <= 200) {
		zoomLevel = 17;
	} else if (dataItem.radius <= 300) {
		zoomLevel = 16;
	} else if (dataItem.radius <= 500) {
		zoomLevel = 15;
	} else if (dataItem.radius <= 2000) {
		zoomLevel = 14;
	} else if (dataItem.radius <= 3000) {
		zoomLevel = 13;
	} else {
		zoomLevel = 12;
	}
	
	map.setCenter(marker.getPosition());
	map.setZoom(zoomLevel);
	
	drawCircle();
	
	setTimeout(function(){
		$("#wizard-form div[name='smartWizard']").smartWizard("next");
	}, 2000);

	
	$('#wizard-modal .modal-dialog').draggable({ handle: '.modal-header' });
	$("#wizard-modal").modal();
}


function drawMap() {
	
	var position = new naver.maps.LatLng(37.5512164, 126.98824864606178);

	map = new naver.maps.Map('radius-map-setting', {
	    center: position,
	    zoom: 11
	});

	marker = new naver.maps.Marker({
	    map: map,
	    visible: false
	});

	circle = new naver.maps.Circle({
	    map: map,
	    fillColor: '#FFFF00',
	    fillOpacity: 0.1,
	    visible: false
	});
	
	naver.maps.Event.addListener(map, 'click', function(e) {
	    marker.setPosition(e.coord);
		marker.setVisible(true);
		
		$("#radius-slider-div").hide();
		$("#map-action-btn-div").show();
		
		$("#wizard-form input[name='center']").val("");
		
		circle.setVisible(false);
	});	
	
	setTimeout(function(){
		// Naver Map 버그 해결
		window.dispatchEvent(new Event('resize'));
	}, 500);

	
}


function drawCircle() {
	
	var radius = getSliderRadius(radSlider.slider("getValue"));
	
	if (radius > 0) {
		circle.setCenter(marker.getPosition());
		circle.setRadius(radius);
		circle.setVisible(true);
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
