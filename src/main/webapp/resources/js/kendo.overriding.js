// 드랍다운 모달창 가려지는 문제 해결
$(document).ready(function () {
    // 인풋폼 안에 있는 dropdown 제외하기 위한 변수
    var fixedDropDownMenuList = [];
    var fixedDropDownToggleList = [];
    var dropDownToggle = $('.dropdown-toggle');
    dropDownToggle.each(function () {
        var toggleBtn = $(this);
        var dropDownMenu = toggleBtn.next('.dropdown-menu');

        if (!toggleBtn.parent().parent().hasClass('clearfix') && !toggleBtn.parent().parent().parent().hasClass('clearfix')) {
            return true;
            // .clearfix 클래스가 없는 경우 다음 반복으로 넘어감
        }

        fixedDropDownMenuList.push(dropDownMenu);
        fixedDropDownToggleList.push(toggleBtn);
        $("body").append(dropDownMenu);

        toggleBtn.on('click', function (event) {
            // event.preventDefault(); // 이벤트 전파 방지
            // event.stopPropagation(); // 이벤트 버블링 방지

            // 현재 버튼 위치를 기준으로 메뉴 위치 설정
            var rect = toggleBtn[0].getBoundingClientRect();
            var menuWidth = dropDownMenu.outerWidth();
            var menuHeight = dropDownMenu.outerHeight();
            var viewportWidth = $(window).width();
            var viewportHeight = $(window).height();
            var leftPosition = rect.left;
            var topPosition = rect.bottom;
            // $('.dropdown-menu').not(dropDownMenu).removeClass('show');

            // 드롭다운 메뉴가 화면 밖으로 넘어가는지 확인
            if (rect.left + menuWidth > viewportWidth) {
                leftPosition = viewportWidth - menuWidth - 10; // 10px 여유
            }
            if (rect.bottom + menuHeight > viewportHeight) {
                topPosition = rect.top - menuHeight;
            }
            dropDownMenu.css({
                top: topPosition + 2 + 'px',
                left: leftPosition - 1 + 'px',
            });

            // 클릭된 버튼의 메뉴와 버튼의 동기화 처리
            if (!dropDownMenu.hasClass('show')) {
                dropDownMenu.addClass('show');
                fixedDropDownMenuList.forEach(function (e) {
                        if (e[0] !== dropDownMenu[0]) {
                            e.removeClass('show');
                        }
                    }
                );


            } else {
                dropDownMenu.removeClass('show');
            }

        });
    });
    // 메뉴 외부 클릭 시 메뉴 닫기
    $(document).on('click', function (event) {
        if (!$(event.target).hasClass('dropdown-toggle') && !$(event.target).hasClass('dropdown-menu')) {
            fixedDropDownMenuList.forEach(function (e) {
                e.removeClass('show');
            })
        }
    });

    // 다른 드롭다운 버튼 클릭시 버그 픽스된 드롭다운 리스트 닫기
    dropDownToggle.each(function () {
        var toggleBtn = $(this);

        // fixedDropDownMenuList에서 toggleBtn이 존재하면 다음 반복으로 넘어가기
        var skip = false;
        fixedDropDownToggleList.forEach(function (e) {
            if (e[0] === toggleBtn[0]) {
                skip = true;
                return false; // forEach 내부에서 return false는 실제로 루프를 중단하지 않으므로, skip 플래그를 사용
            }
        });

        if (skip) {
            return true; // .each()의 다음 반복으로 넘어감
        }
        toggleBtn.on('click', function () {
            fixedDropDownMenuList.forEach(function (e) {
                e.removeClass('show');
                console.log('클로즈 함수 실행')
            });
        });
    });
});

// filter icon 변경
$(document).ready(function () {
    var e = $("span.k-icon.k-svg-icon.k-svg-i-filter");
    e.removeClass('k-icon k-svg-icon k-svg-i-filter').addClass('fa-light fa-filter');
});

// filter dropdown button icon 변경

