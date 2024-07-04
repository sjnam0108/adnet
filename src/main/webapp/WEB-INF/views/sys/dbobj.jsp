<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/sys/dbobj/update" var="updateUrl" />

<c:url value="/sys/dbobj/readAds" var="readAdUrl" />
<c:url value="/sys/dbobj/readScreens" var="readScreenUrl" />

<c:url value="/sys/dbobj/cleanUp" var="cleanUpUrl" />


<!-- Opening tags -->

<common:mediumPageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>





<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.css">

<script>$.fn.slider = null</script>
<script type="text/javascript" src="/resources/vendor/lib/bootstrap-slider/bootstrap-slider.js"></script>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#setting">
			<i class="mr-1 fa-light fa-wrench"></i>
			솔루션 설정
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#clean-up">
			<i class="mr-1 fa-light fa-broom-wide"></i>
			자료 정리
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="setting">
	
	



<form id="form-1">	
	<div class="mb-4">
		<div class="card">
			<div class="card-body">
				<div class="pb-3">
		    		<div class="float-left">
						커스텀 화면 해상도
						<span class="small text-muted pl-3">[매체], [게시 유형] 페이지에서의 커스텀 화면 해상도를 지정합니다. 항목 구분자는 '|'이며, 빈칸없이 명시합니다. 예) 200x40|376x57</span>
		    		</div>
				</div>
				<div class="pt-3">
					<input name="resos" type="text" class="form-control col-10 required">
				</div>
			</div>
		</div>
	
		<div class="text-right mt-3">
			<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
		</div>
	</div>
</form>
	
	
	
	
	
	</div>
	<div class="tab-pane" id="clean-up">
	
	
	
	

<div class="mb-4">
	<div class="card">
		<div class="card-body">
			<div class="pb-2">
		    	<div class="clearfix">
		    		<div class="float-left">
						광고 노출 자료 정리
						<span class="small text-muted pl-3">이미 진행된 특정 기간, 화면, 광고의 노출 자료를 삭제하고, 남은 자료에 대한 재계산을 수행합니다.</span>
		    		</div>
		    	</div>
			</div>
			<div class="mx-2">
			
				<div class="form-row">
					<div class="form-group col-sm-6">
						<label class="form-label">
							광고
						</label>
						<select name="cleanAds" class="form-control border-none"></select>
					</div>
					<div class="form-group col-sm-6">
						<label class="form-label">
							화면
						</label>
						<select name="cleanScreens" class="form-control border-none"></select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label class="form-label">
							날짜
						</label>
						<div class="slider-primary">
							<input name="cleanDates" type="text" data-slider-min="1" data-slider-max="10" data-slider-step="1" data-slider-value="5">
						</div>
					</div>
				</div>
				<div class="text-right mt-2">
					<button type="button" id="clean-start-btn" class="btn btn-primary btn-round">시작</button>
				</div>
			
			</div>
		</div>
	</div>
</div>


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Delete
	$("#delete-log-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid-log").data("kendoGrid");
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
						url: "${destroyRtUrl}",
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
	



	
	</div>
</div>


<!--  Root form container -->
<div id="formRoot"></div>


	
	

<!--  Scripts -->

<script>
$(document).ready(function() {
	
	// 솔루션 설정
	$("#form-1 input[name='resos']").val("${resos}");
	
	
	var dates = ${dates};
	
    $("select[name='cleanAds']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span title='광고'><span class='fa-regular fa-audio-description text-gray'></span></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-audio-description text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readAdUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {

        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
    $("select[name='cleanScreens']").kendoMultiSelect({
        dataTextField: "text",
        dataValueField: "value",
        tagTemplate: "<span title='화면'><span class='fa-regular fa-screen-users text-gray'></span></span>" + 
        			 "<span class='pl-2'>#:data.text#</span>",
        itemTemplate: "<span class='fa-regular fa-screen-users text-gray'></span>" +
        		      "<span class='pl-2'>#:data.text#</span>",
        dataSource: {
		    //serverFiltering: true,
            transport: {
                read: {
                    dataType: "json",
                    url: "${readScreenUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
        change: function(e) {

        },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });

    var dateSlider = $("input[name='cleanDates']").slider({
    	range: true,
    	tooltip: "always",
    	min: 0,
    	max: dates.length - 1,
    	value: [0, dates.length - 1],
		formatter: function(value) {
			if (value && value.length && value.length == 2) {
				if (value[0] == value[1]) {
					return dates[value[0]];
				} else {
					return dates[value[0]] + " ~ " + dates[value[1]];
				}
			}
			return '';
		}
    });
	
	$("#clean-start-btn").click(function(e) {
		
		var ads = $("select[name='cleanAds']").data("kendoMultiSelect").value();
		var screens = $("select[name='cleanScreens']").data("kendoMultiSelect").value();
		
		if (ads.length == 0 && screens.length == 0) {
			showAlertModal("danger", "광고 혹은 화면 중 한 항목은 반드시 선택되어야 합니다.");
			return;
		}

		var sliderValue = dateSlider.slider("getValue");
		if (sliderValue == null || sliderValue.length != 2) {
			return;
		}
		
		var sDate = dates[sliderValue[0]];
		var eDate = dates[sliderValue[1]];
		
		showConfirmModal("자료 정리 작업은 시간이 많이 걸릴 수 있습니다. 계속 진행하시겠습니까?", function(result) {
			if (result) {
				showWaitModal();
				
				var data = {
					ads: ads,
					screens: screens,
					start: sDate,
					end: eDate
				};
				
				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${cleanUpUrl}",
					data: JSON.stringify(data),
					success: function (form) {
						hideWaitModal();
						showOperationSuccessMsg();
						grid.dataSource.read();
					},
					error: function(e) {
						hideWaitModal();
						ajaxOperationError(e);
					}
				});
			}
		});
	});
});	
</script>


<script>

function saveForm1() {
	
	var data = {
		resos: $.trim($("#form-1 input[name='resos']").val()),
	};

   	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${updateUrl}",
		data: JSON.stringify(data),
		success: function (form) {
			showAlertModal("success", "설정이 변경되었습니다.");
		},
		error: ajaxSaveError
	});
}

</script>

<!--  / Scripts -->


<!--  Forms -->


<style>

</style>


<!--  / Forms -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
