// 드랍다운 모달창 가려지는 문제 해결
$(document).ready(function () {
    // 인풋폼 안에 있는 dropdown 제외하기 위한 변수
    var fixedDropDownMenuList = [];
    var fixedDropDownToggleList = [];
    var nonFiexdDropDownMenuList = [];
    var nonFiexdDropDownToggleList = [];
    var dropDownToggle = $('.dropdown-toggle');
    // var filterLinkClass = '.k-svg-i-filter';
    dropDownToggle.each(function () {
        var toggleBtn = $(this);
        var dropDownMenu = toggleBtn.next('.dropdown-menu');

        // .clearfix 클래스가 없는 경우 다음 반복으로 넘어감
        if (!toggleBtn.parent().parent().hasClass('clearfix') && !toggleBtn.parent().parent().parent().hasClass('clearfix')) {
            nonFiexdDropDownToggleList.push(toggleBtn)
            nonFiexdDropDownMenuList.push(dropDownMenu)
            return true;
        }

        // grid 안에 있지 않는경우 다음 반복으로 넘어감
        var hasGridClass = toggleBtn.parentsUntil('body').filter(function () {
            return $(this).hasClass('k-grid');
        }).length > 0;

        if (!hasGridClass) {
            nonFiexdDropDownToggleList.push(toggleBtn)
            nonFiexdDropDownMenuList.push(dropDownMenu)
            return true;
        }

        $("body").append(dropDownMenu);
        fixedDropDownMenuList.push(dropDownMenu);
        fixedDropDownToggleList.push(toggleBtn);

        toggleBtn.on('click', function (event) {
            event.stopPropagation();
            event.preventDefault();

            // 현재 버튼 위치를 기준으로 메뉴 위치 설정
            var rect = toggleBtn[0].getBoundingClientRect();
            var menuWidth = dropDownMenu.outerWidth();
            var menuHeight = dropDownMenu.outerHeight();
            var viewportWidth = $(window).width();
            var viewportHeight = $(window).height();
            var leftPosition = rect.left;
            var topPosition = rect.bottom;

            // 드롭다운 메뉴가 화면 밖으로 넘어가는지 확인
            if (rect.left + menuWidth > viewportWidth) {
                leftPosition = viewportWidth - menuWidth - 5; // 10px 여유
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
                nonFiexdDropDownMenuList.forEach(function (e) {
                    e.removeClass('show')
                });
            } else {
                dropDownMenu.removeClass('show');
            }
        });
    });

    // 드롭다운매뉴 외부 클릭 시 메뉴 닫기
    $(document).on('click', function (event) {
        if (!$(event.target).hasClass('dropdown-toggle') && !$(event.target).hasClass('dropdown-menu')) {
            fixedDropDownMenuList.forEach(function (e) {
                e.removeClass('show');
            })
        }
    });

    // 다른 드롭다운 버튼 클릭시 버그 픽스된 드롭다운 리스트 닫기
    // dropDownToggle.each(function () {
    //     var toggleBtn = $(this);
    //
    //     // fixedDropDownMenuList에서 toggleBtn이 존재하면 다음 반복으로 넘어가기
    //     var skip = false;
    //     fixedDropDownToggleList.forEach(function (e) {
    //         if (e[0] === toggleBtn[0]) {
    //             skip = true;
    //             return false; // forEach 내부에서 return false는 실제로 루프를 중단하지 않으므로, skip 플래그를 사용
    //         }
    //     });
    //
    //     if (skip) {
    //         return true; // .each()의 다음 반복으로 넘어감
    //     }
    //
    //     toggleBtn.on('click', function () {
    //         fixedDropDownMenuList.forEach(function (e) {
    //             e.removeClass('show');
    //         });
    //     });
    // });

    nonFiexdDropDownToggleList.forEach(function (e) {
        e.on('click', function () {
            fixedDropDownMenuList.forEach(function (e) {
                e.removeClass('show');
            });
        });
    });

    // // 필터 링크 클릭 시 드롭다운 메뉴 닫기
    // $(document).on('click', filterLinkClass, function () {
    //     fixedDropDownMenuList.forEach(function (e) {
    //         e.removeClass('show');
    //     });
    //     nonFiexdDropDownMenuList.forEach(function (e) {
    //         e.removeClass('show');
    //     });
    // });
});

// KendoUI Form X 버튼 로직
$(document).ready(function () {
    // KendoUI AutoComplete x버튼 로직 수정
    $(document).on('mouseenter', '#formRoot div[name="siteShortName-con"]', function () {
        if ($('input[name="siteShortName"]').val()) {
            $(this).find('.k-clear-value').removeClass('k-hidden');
        }
    });

    $(document).on('mouseleave', '#formRoot div[name="siteShortName-con"]', function () {
        $(this).find('.k-clear-value').addClass('k-hidden');
    });

    $(document).on('mouseenter', '#formRoot div[name="siteName-con"]', function () {
        if ($('input[name="siteName"]').val()) {
            $(this).find('.k-clear-value').removeClass('k-hidden');
        }
    });

    $(document).on('mouseleave', '#formRoot div[name="siteName-con"]', function () {
        $(this).find('.k-clear-value').addClass('k-hidden');
    });


    // multiSelect x버튼 로직
    var isMultiSelectFocused = false;

    $(document).on('focus', '#formRoot .k-input-inner', function (e) {
        isMultiSelectFocused = true;
    });
    $(document).on('blur', '#formRoot .k-input-inner', function (e) {
        isMultiSelectFocused = false;
        $('#formRoot .k-multiselect.k-input').find('.k-clear-value').addClass('k-hidden');
    });

    $(document).on('mouseenter', '#formRoot .k-multiselect.k-input', function () {
        if ($(this).find(".k-chip-solid-base").length > 0) {
            $(this).find('.k-clear-value').removeClass('k-hidden');
        }
    });

    $(document).on('mouseleave', '#formRoot .k-multiselect.k-input', function () {
        if (!isMultiSelectFocused) {
            $(this).find('.k-clear-value').addClass('k-hidden');
        }
    });
});

$(document).ready(function() {
    setTimeout(function() {
        var tooltipElements = $(".adnet-default-tooltip");

        tooltipElements.each(function(index) {
            // 각 요소에 고유한 아이디 부여 및 개별 툴팁 생성
            var uniqueId = 'tooltip-' + index;
            var arrowId = uniqueId + '-arrow';
            $(this).attr('data-tooltip-id', uniqueId);

            const tooltipText = $(this).attr('data-tooltip');
            if (!tooltipText) return;

            // 툴팁 div 생성 및 설정
            let tooltipElement = document.createElement('div');
            tooltipElement.id = uniqueId;
            tooltipElement.className = 'tooltip';
            tooltipElement.innerHTML = tooltipText;

            // 화살표 역할을 하는 div 생성 및 설정
            let tooltipAfterElement = document.createElement('div');
            tooltipAfterElement.id = arrowId;
            tooltipAfterElement.className = 'tooltip-arrow';

            // 툴팁과 화살표 요소를 body에 추가
            document.body.appendChild(tooltipElement);
            document.body.appendChild(tooltipAfterElement);

            const rect = this.getBoundingClientRect();
            const tooltipRect = tooltipElement.getBoundingClientRect();

            // 툴팁 위치 설정
            tooltipElement.style.position = 'absolute';
            tooltipElement.style.top = `${rect.top - (tooltipRect.height + 10)}px`;
            tooltipElement.style.left = `${rect.left + (rect.width / 2) - (tooltipRect.width / 2)}px`;

            // 화살표 위치 설정
            tooltipAfterElement.style.top = `${rect.top - (tooltipAfterElement.getBoundingClientRect().height)}px`;
            tooltipAfterElement.style.left = `${rect.left + (rect.width / 2)}px`;

            // 초기 상태에서는 show 클래스 없이 생성
            tooltipElement.classList.remove('show');

            // 이벤트 리스너 등록
            $(this).on('mouseenter', function(e) {
                e.stopPropagation();
                $('#' + uniqueId).addClass('show');
                $('#' + arrowId).addClass('show');
            });

            $(this).on('mouseleave', function(e) {
                e.stopPropagation();
                $('#' + uniqueId).removeClass('show');
                $('#' + arrowId).removeClass('show');
            });
        });
    }, 500); // 1초 후 실행
});