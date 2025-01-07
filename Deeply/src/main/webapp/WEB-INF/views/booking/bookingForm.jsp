<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="${pageContext.request.contextPath}/assets/js/jquery-3.7.1.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/booking.js"></script>


<div class="booking-artist-container font-white bold-title background-black align-center">${event.artist_num}</div>
<div class="booking-title bold-title">예매 정보 입력</div>
<div class="booking-info">
	<img>
	<div class="booking-div1 left-5">
		<!-- 아티스트 이름 -->
		<div class="booking-artist width-40">
			<div class="bold-title font-white font-1_5">아티스트</div>
			<hr>
			<div class="bold-title font-white font-1_5 top-5">${event.artist_num}</div>
		</div>
		<!-- 공연 장소 -->
		<div class="booking-place width-60">
			<div class="bold-title font-white font-1_5">공연 장소</div>
			<hr>
			<div class="bold-title font-white font-1 top-5">${event.hall_name}</div>
			<div class="font-white font-0_8 top-3">${event.location}</div>
		</div>
	</div>
	<!-- 공연 정보 -->
	<div class="booking-div2 left-5">
		<div class="bold-title font-white font-1_5">${event.perf_title}</div>
		<hr>
		<div class="font-white font-0_8 top-3">${event.perf_desc}</div>
	</div>
</div>
<div class="booking-main">
	<form:form modelAttribute="bookingVO" action="book"
		id="booking_process" enctype="multipart/form-data">
		<form:hidden path="perf_num" value="${event.perf_num}"></form:hidden>
		<form:hidden path="user_num" value="${member.user_num}"></form:hidden>
		<div>
			<p>
				<form:label path="booked_seat">예약 인원</form:label>
				<form:input path="booked_seat" type="number" />
				<form:errors path="booked_seat" cssClass="error-color"/>
			</p>
			<hr>
			<!-- 예매자 정보 -->
			<div>
				<div class="p_info">
					<p>예매자 1</p>
					<p>
						<form:label path="name">이름</form:label>
						<form:input path="name"/>
						<form:errors path="name" cssClass="error-color"/>
					</p>
					<p>
						<form:label path="phone">전화번호</form:label>
						<form:input path="phone" />
						<form:errors path="phone" cssClass="error-color"/>
					</p>
				</div>
				<div class="p_info" id="book2">
					<p>예매자 2</p>
					<p>
						<form:label path="name2">이름</form:label>
						<form:input path="name2" />
						<form:errors path="name2" cssClass="error-color"/>
					</p>
					<p>
						<form:label path="phone2">전화번호</form:label>
						<form:input path="phone2" />
						<form:errors path="phone2" cssClass="error-color"/>
					</p>
				</div>
			</div>
			<!-- 배송지 정보 -->
			<div>
				<h2>배송지 정보</h2>
				<p>
					<form:label path="deliver_name">이름</form:label>
					<form:input path="deliver_name" />
					<form:errors path="deliver_name" cssClass="error-color"/>
					
				</p>
				<p>
					<form:input path="zipcode" id="zipcode" class="Authentication2"
						type="text" placeholder="우편번호" />
					<form:errors path="zipcode" cssClass="error-color" />
					<input type="button" onclick="execDaumPostcode()" value="우편번호찾기"
						class="default-btn2">
				</p>
				<p>
					<form:input path="address1" id="address1" type="text"
						placeholder="주소" />
					<form:errors path="address1" cssClass="error-color" />
				</p>
				<p>
					<form:input path="address2" type="text" placeholder="상세주소" />
					<form:errors path="address2" cssClass="error-color" />
				</p>
				<p>
					<form:label path="request">요청사항</form:label>
					<form:input path="request"/>
				</p>
				<p>
					<form:input path="more_info" />
				</p>
			</div>
			<!-- 좌석 정보 -->
			<div>
				<h2>좌석 정보</h2>
				<h5>${event.hall_name}</h5>
				<div>
					<div class="seat-div">
						<div class="stage-div"></div>
						<c:forEach var="seats" items="${seats}" varStatus="status">
							<div class="seat-item" data-side="${status.index % 2 == 0 ? 'left' : 'right'}" data-price="${seats.price}">${seats.seat_num}</div>
						</c:forEach>
					</div>
					<form:input id="seat_n1" path="seat_num1"/>
					<form:input id="seat_n2" path="seat_num2"/>
					
				</div>
			</div>

			<!-- 선택 좌석 -->
			<div>
				<h2>선택 좌석</h2>
				<table class="receipt" width="100%" border="1">
					<thead>
					<tr>
						<td>좌석 번호</td>
						<td>좌석 금액</td>
						<td>총액</td>
					</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
				<form:hidden id="seat_price" path="total_price"/>
			</div>
		</div>
		<div class="align-center">
			<form:button>결제</form:button>
		</div>
	</form:form>
</div>

<!-- 다음 우편번호 API 시작 -->
<div id="layer"
	style="display: none; position: fixed; overflow: hidden; z-index: 1; -webkit-overflow-scrolling: touch;">
	<img
		src="//t1.daumcdn.net/localimg/localimages/07/postcode/320/close.png"
		id="btnCloseLayer"
		style="cursor: pointer; position: absolute; right: -3px; top: -3px; z-index: 1"
		onclick="closeDaumPostcode()" alt="닫기 버튼">
</div>
<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<script>
	// 우편번호 찾기 화면을 넣을 element
	var element_layer = document.getElementById('layer');

	function closeDaumPostcode() {
		// iframe을 넣은 element를 안보이게 한다.
		element_layer.style.display = 'none';
	}

	function execDaumPostcode() {
		new daum.Postcode(
				{
					oncomplete : function(data) {
						// 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

						// 각 주소의 노출 규칙에 따라 주소를 조합한다.
						// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
						var fullAddr = data.address; // 최종 주소 변수
						var extraAddr = ''; // 조합형 주소 변수

						// 기본 주소가 도로명 타입일때 조합한다.
						if (data.addressType === 'R') {
							//법정동명이 있을 경우 추가한다.
							if (data.bname !== '') {
								extraAddr += data.bname;
							}
							// 건물명이 있을 경우 추가한다.
							if (data.buildingName !== '') {
								extraAddr += (extraAddr !== '' ? ', '
										+ data.buildingName : data.buildingName);
							}
							// 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
							fullAddr += (extraAddr !== '' ? ' (' + extraAddr
									+ ')' : '');
						}

						// 우편번호와 주소 정보를 해당 필드에 넣는다.
						document.getElementById('zipcode').value = data.zonecode; //5자리 새우편번호 사용
						document.getElementById('address1').value = fullAddr;
						//document.getElementById('sample2_addressEnglish').value = data.addressEnglish;

						// iframe을 넣은 element를 안보이게 한다.
						// (autoClose:false 기능을 이용한다면, 아래 코드를 제거해야 화면에서 사라지지 않는다.)
						element_layer.style.display = 'none';
					},
					width : '100%',
					height : '100%',
					maxSuggestItems : 5
				}).embed(element_layer);

		// iframe을 넣은 element를 보이게 한다.
		element_layer.style.display = 'block';

		// iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
		initLayerPosition();
	}

	// 브라우저의 크기 변경에 따라 레이어를 가운데로 이동시키고자 하실때에는
	// resize이벤트나, orientationchange이벤트를 이용하여 값이 변경될때마다 아래 함수를 실행 시켜 주시거나,
	// 직접 element_layer의 top,left값을 수정해 주시면 됩니다.
	function initLayerPosition() {
		var width = 300; //우편번호서비스가 들어갈 element의 width
		var height = 400; //우편번호서비스가 들어갈 element의 height
		var borderWidth = 5; //샘플에서 사용하는 border의 두께

		// 위에서 선언한 값들을 실제 element에 넣는다.
		element_layer.style.width = width + 'px';
		element_layer.style.height = height + 'px';
		element_layer.style.border = borderWidth + 'px solid';
		// 실행되는 순간의 화면 너비와 높이 값을 가져와서 중앙에 뜰 수 있도록 위치를 계산한다.
		element_layer.style.left = (((window.innerWidth || documentcumentElement.clientWidth) - width) / 2 - borderWidth)
				+ 'px';
		element_layer.style.top = (((window.innerHeight || documentcumentElement.clientHeight) - height) / 2 - borderWidth)
				+ 'px';
	}
</script>
<!-- 다음 우편번호 API 끝 -->