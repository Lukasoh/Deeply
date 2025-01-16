$(function () {
	/* =========================
	 *  사진 등록시 이미지 바로 띄우기
	 * ========================= */
	let quantity = 1;
	
	$('#upload_btn').click(function(e){
		e.preventDefault();
		$('#upload').click();
	});
	
	$('#upload').change(function(){
		let my_photo = this.files[0];
		if(!my_photo){
		//선택을 취소하면 원래 처음 화면으로 되돌림
		$('.items-img').attr('src',photo_path);
		alert('파일 선택 안됨');
		return;
		}
		//화면에서 이미지 미리보기
		const reader = new FileReader();
		reader.readAsDataURL(my_photo);
									
		reader.onload=function(){
		$('.items-img').attr('src',reader.result);
						
		};		
	});
	
	/* ======================
	 *  상품 수량 변경 +,- 이용
	 * ====================== */
	
	$('#minus_btn').click(function(){
	    
	    if (quantity <= 1) {
	        quantity = 1;
	    } else {
	        quantity--;
	    }
	    $('#quantity').data('value', quantity);
	    $('#quantity').html(quantity);

	    const totalPrice = $('#price_total').data('price') * quantity;
	    $('#price_total').html(formatNumber(totalPrice) + '원');
	});

	$('#plus_btn').click(function(){
	    
	    if (quantity >= 3) {
	        quantity = 3;
	    } else {
	        quantity++;
	    }
	    $('#quantity').data('value', quantity);
	    $('#quantity').html(quantity);

	    const totalPrice = $('#price_total').data('price') * quantity;
	    $('#price_total').html(formatNumber(totalPrice) + '원');
	});

	/* ======================
	 *  숫자 세자리마다 , 넣어짐
	 * ====================== */
	function formatNumber(num) {
	    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
	}
	
	$('#order_btn').click(function(){
		const item_num = $('#item_num').data('num');
		location.href='../item/order?item_num='+item_num+'&quantity='+quantity;
	});
	
	/* ======================
	 *  장바구니 바로 추가
	 * ====================== */
	
	$('#add_cart').click(function () {
	    let item_num = $('#item_num').val();
	    let order_quantity = $('#quantity').data('value');

	    let csrfToken = $("meta[name='_csrf']").attr("content");
	    let csrfHeader = $("meta[name='_csrf_header']").attr("content");

	    $.ajax({
	        url: '/item/addCart',
	        type: 'POST', // POST 요청
	        contentType: 'application/json', // JSON 데이터 전달
	        data: JSON.stringify({ item_num: item_num, order_quantity: order_quantity }),
	        beforeSend: function (xhr) {
	            xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰 포함
	        },
	        dataType: 'json',
	        success: function (param) {
	            if (param.result == 'logout') {
	                alert('사용자 계정으로 로그인 후 이용해 주세요');
	            } else if (param.result == 'success') {
	               	$('#addCart_alert').show();
	            } else {
	                alert('장바구니 등록 오류 발생');
	            }
	        },
	        error: function () {
	            alert('네트워크 오류');
	        }
	    });
	});
	$('#alert_x_btn').click(function(){
		$('#addCart_alert').hide();
	});

	
});