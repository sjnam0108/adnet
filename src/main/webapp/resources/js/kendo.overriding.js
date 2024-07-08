// $(document).ready(function() {
//     function observeWidthChange(element) {
//         // MutationObserver 인스턴스를 생성합니다.
//         var observer = new MutationObserver(function(mutations) {
//             mutations.forEach(function(mutation) {
//                 if (mutation.attributeName === 'style') {
//                     var newWidth = $(element).width();
//                     // width 값이 변할 때마다 50px 증가시킵니다.
//                     $(element).width(newWidth + 50);
//                     console.log("감지되어 50 추가됨");
//                 }
//             });
//         });
//
//         // 관찰할 대상과 옵션을 설정합니다.
//         observer.observe(element, {
//             attributes: true, // 속성 변화를 감지합니다.
//             attributeFilter: ['style'] // style 속성의 변화를 감지합니다.
//         });
//     }
//
//     // 동적으로 생성된 요소에 대해 MutationObserver 설정
//     function addDivWithObserver() {
//         var newDiv = $('<div class="k-child-animation-container">새 DIV</div>');
//         $('#container').append(newDiv);
//         observeWidthChange(newDiv[0]);
//     }
//
//     // DIV 추가 버튼 클릭 시 새로운 DIV 추가 및 MutationObserver 설정
//     $('.k-grid-filter-menu ').on('click', function() {
//         addDivWithObserver();
//     });
//
//     // 초기 로드 시 존재하는 .k-child-animation-container 요소들에 대해 MutationObserver 설정
//     $('.k-child-animation-container').each(function() {
//         observeWidthChange(this);
//     });
// });