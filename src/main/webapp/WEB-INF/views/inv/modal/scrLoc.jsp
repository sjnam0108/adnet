<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/adn/common/readScrLoc" var="readScrLocUrl" />





<!-- Page body -->


<!--  Forms -->

<div class="d-flex justify-content-between mt-2 mb-1">
	<div class="align-self-center">
		<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goPrevDate()">
			<span class="fa-regular fa-angle-left fa-lg"></span>
		</button>		
	</div>
	<div class="d-flex align-items-center">
		<input id="scr-loc-svcdate" type="text" class="form-control" style="width:150px;">
		<span style="display:inline; width:70px; text-align: center;"><span id="scr-loc-curr-date-day-str"></span></span>
	</div>
	<div class="align-self-center">
		<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goNextDate()">
			<span class="fa-regular fa-angle-right fa-lg"></span>
		</button>		
	</div>
</div>

<div class="mb-2">
	<div id="scr-loc-map" style="border: solid 1px #e4e4e4; background-color: white; height: 600px; width: 100%;">
	</div>
</div>

<div class="slider-primary mb-2" style="display: none;" id="scr-loc-slider-div">
	<input id="src-loc-slider" type="text" >
</div>


<style>

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

var piePrevDateStr = "", pieNextDateStr = "";
var pieMaxDate = new Date();

var srcLocData = [];

$(document).ready(function() {

	$("#scr-loc-svcdate").kendoDatePicker({
		dates: ${dates},
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd", "yyyyMMdd",
		],
		max: pieMaxDate,
		month: {
            content: '# if ($.inArray(+data.date, ${dates}) != -1) { #' +
            		 "<span style=\"position: relative;\"><span style=\"position:absolute;right:0px;margin-top:-11px;\">&bull;</span>#= data.value #</span>" +
         			 '# } else { #' +
         			 '#= data.value #' +
         			 '# } #',
		},
		change: function(e) {
			var url = "/adn/common/scrLoc?id=${value_id}&date=" + 
					kendo.toString($("#scr-loc-svcdate").data("kendoDatePicker").value(), "yyyy'-'MM'-'dd");
		    $("#scrLocModal .modal-body").load(url);
		}
	});

	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readScrLocUrl}",
		data: JSON.stringify({ id: ${value_id}, date: "${value_date}" }),
		success: function (data, status) {
			srcLocData = data.locItems;
			refreshData(data, status);
		},
		error: function(e) {
			if (parent) {
				parent.showReadErrorMsg();
			}
		}
	});
	
});


function getPeroidTime(time1, time2) {
	
	if (!time1 && !time2) {
		return "";
	}
	
	if (!time2) {
		return time1;
	}
	
	var s = "";
	if (time1.length == 8) {
		s = time1;
	} else {
		s = time1.substr(0, 8);
	}
	
	if (time2.length == 8) {
		s = s + " - " + time2;
	} else {
		s = s + " - " + time2.substr(11);
	}
	
	return s;
}


function refreshData(data, status) {
	
	$("#scr-loc-svcdate").data("kendoDatePicker").value(data.playDate);

	
    // 선택 날짜의 요일 표시
	$("#scr-loc-curr-date-day-str").text(kendo.toString($("#scr-loc-svcdate").data("kendoDatePicker").value(), "(dddd)"));

    // 하루 전, 다음 날 처리를 위한 계산
    const [y, m, d] = data.playDate.split("-");
    
    var nextDate = new Date(Number(y), Number(m) - 1, Number(d) + 1);
    var prevDate = new Date(Number(y), Number(m) - 1, Number(d) - 1);
    
    piePrevDateStr = toStringByFormatting(prevDate, "-");
    
    if (nextDate.getTime() > pieMaxDate.getTime()) {
        pieNextDateStr = toStringByFormatting(pieMaxDate, "-");
    } else {
        pieNextDateStr = toStringByFormatting(nextDate, "-");
    }

    if (data.locItems.length > 0) {
    	
        $("#src-loc-slider").slider({
        	range: true,
        	tooltip: "always",
        	max: data.locItems.length - 1,
        	value: [0, data.locItems.length - 1],
			formatter: function(value) {
				if (value && value.length && value.length == 2 && srcLocData.length > 0) {
					if (srcLocData.length > value[1]) {
						if (value[0] == value[1]) {
							return "시간: " + getPeroidTime(srcLocData[value[1]].title);
						} else {
							return "시간: " + getPeroidTime(srcLocData[value[0]].title, srcLocData[value[1]].title);
						}
					}
				}
				return '';
			}
        }).on('change', function(event) {
            var a = event.value.newValue;
            var b = event.value.oldValue;

            var changed = !($.inArray(a[0], b) !== -1 && 
                            $.inArray(a[1], b) !== -1 && 
                            $.inArray(b[0], a) !== -1 && 
                            $.inArray(b[1], a) !== -1 && 
                            a.length === b.length);

            if(changed ) {
            	for(var i = 0; i < markers.length; i ++) {
            		if (a[0] <= i && i <= a[1]) {
            			markers[i].setVisible(true);
            		} else {
            			markers[i].setVisible(false);
            		}
            	}
            }
        });
        $("#scr-loc-slider-div").show();
    }
	
	drawMap(data.locItems);
	
	setTimeout(function(){
		// Naver Map 버그 해결
		window.dispatchEvent(new Event('resize'));
		
		fitBounds();
	}, 500);
}


function leftPad(value) {
    if (value >= 10) {
        return value;
    }

    return "0" + value;
}


function toStringByFormatting(source, delimiter = '-') {
    const year = source.getFullYear();
    const month = leftPad(source.getMonth() + 1);
    const day = leftPad(source.getDate());
    
    return [year, month, day].join(delimiter);
}


function goPrevDate() {
	$("#scrLocModal .modal-body").load("/adn/common/scrLoc?id=${value_id}&date=" + piePrevDateStr);
}


function goNextDate() {
	$("#scrLocModal .modal-body").load("/adn/common/scrLoc?id=${value_id}&date=" + pieNextDateStr);
}


var infoWin = null;
var markers = [];

var map = null;
var bounds = [];

function drawMap(data) {
	
	// 기본 위치를 남산타워로
	map = new naver.maps.Map('scr-loc-map', {
		center: new naver.maps.LatLng(37.5512164, 126.98824864606178),
		zoom: 15
	}); 

	var titles = [];
	var lats = [];
	var lngs = [];
	
	if (data) {
		data.forEach(function (item) {
			titles.push(item.title);
			lats.push(item.lat);
			lngs.push(item.lng);
		});
	}
	
	//var markers = [];
	var infoWins = [];
	
	bounds = [];
	
	if (titles.length > 0) {
		for (var i = 0; i < titles.length; i++) {
			var marker = new naver.maps.Marker({
				position: new naver.maps.LatLng(lats[i], lngs[i]),
				map: map
			});
			
			markers.push(marker);
			
			var content = [
				'<div class="rounded-marker rounded-marker-white">',
		        	'<span class="rounded-marker-title">' + titles[i] + '</span>',
		        '</div>'
		    ].join('');
			
			infoWins.push(new naver.maps.InfoWindow({
			    content: content,
			    borderWidth: 0,
			    disableAnchor: true,
			    backgroundColor: 'transparent',
			    pixelOffset: new naver.maps.Point(0, 7),
			}));
			
			bounds.push(marker.position);
		}
	}
	

    function getClickHandler(seq) {
		
		return function(e) {  // 마커를 클릭하는 부분
			var marker = markers[seq], // 클릭한 마커의 시퀀스로 찾는다.
				infoWindow = infoWins[seq]; // 클릭한 마커의 시퀀스로 찾는다

			if (infoWindow.getMap()) {
				infoWindow.close();
				infoWin = null;
			} else {
				infoWin = infoWindow;
				infoWindow.open(map, marker);
			}
    	}
    };

	for(var i = 0; i < markers.length; i++){
		naver.maps.Event.addListener(markers[i], "click", getClickHandler(i));
	}
	
	naver.maps.Event.addListener(map, "click", function(e) {
		if (infoWin) {
    	    infoWin.close();
    	    infoWin = null;
		}
	});
};


function fitBounds() {
	
	if (bounds.length == 1) {
		map.setCenter(bounds[0]);
	} else {
		map.fitBounds(bounds);
	}
}

</script>

<style>

.rounded-marker {
	border-radius: 8px; 
	background: #46494d;
	font-weight: 300;
}

.rounded-marker-white {
	color: white;
}

.rounded-marker-yellow {
	color: yellow;
	font-weight: 400;
}

.rounded-marker-title {
	padding-right: 8px; 
	padding-left: 8px;
	font-size: 12px;
}

</style>

<!--  / Scripts -->


<!-- / Page body -->
