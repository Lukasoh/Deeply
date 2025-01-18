package kr.spring.item.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

import kr.spring.item.vo.CartVO;
import kr.spring.item.vo.ItemVO;
import kr.spring.item.vo.OrderVO;
import kr.spring.member.vo.AgroupVO;
import kr.spring.member.vo.MemberVO;

@Mapper
public interface ItemMapper {
	//부모글
	public List<ItemVO> selectList(Map<String,Object> map);
	@Select("SELECT * FROM shop_item WHERE user_num=#{user_num}")
	public List<ItemVO> selectListByUserNum(Long user_num);
	public int selectRowCount(Map<String,Object> map);
	public int insertItem(ItemVO Item);
	@Select("SELECT * FROM shop_item WHERE item_num=#{item_num}")
	public ItemVO selectitem(Long item_num);
	public void updateItem(ItemVO Item);
	@Delete("DELETE FROM shop_item WHERE item_num=#{item_num}")
	public void deleteItem(Long item_num);
	public List<ItemVO> showListByGroup();
	public List<ItemVO> showListGroup();
	//그룹 번호 가져오기
	@Select("SELECT * FROM agroup WHERE group_name=#{group_name}")
	public AgroupVO selectGroup(String group_name);
	
	//----------구매자(사용자)-------------
	public void insertOrder(OrderVO ordervo); //주문등록
	@Select("SELECT order_num_seq.nextval FROM dual")
	public Long selectBook_num();
	public int selectOrderRowCount(Map<String,Object> map);//전체 주문 개수/검색 주문 개수
	public List<OrderVO> selectOrder(Map<String,Object> map);//주문 목록(List형태)
	public OrderVO selectOrderDetail(OrderVO ordervo);//1건으로 불러올 개별 상품 목록
	public MemberVO selectUserInfo(MemberVO memberVO);//배송지 정보 가져오기
	public void deleteOrder(Long Order_num); //주문 취소
	@Update("UPDATE shop_item SET item_stock=item_stock-#{quantity} WHERE item_num=#{item_num}")
	public void updateStock(int quantity, long item_num); // 재고 수 업데이트
	@Update("UPDATE shop_order SET pay_num=#{pay_num} WHERE order_num=#{order_num}")
	public void updatePayNum(long pay_num, long order_num);
	
	//----------장바구니-------------
	public void deleteCart(Long Cart_num);//장바구니에 담긴 상품 삭제
	public int insertCart(CartVO cart);//장바구니 정보 등록
	public List<CartVO> selectCart(long user_num);//장바구니 목록가져오기
	int updateCartByItem_num(@Param("item_num") long item_num, 
            			     @Param("user_num") long user_num, 
            			     @Param("order_quantity") long order_quantity); // 동일 상품 수량 합산 (최대 3개 제한) 
	
	@Select("SELECT * FROM shop_cart WHERE user_num=#{user_num} AND item_num=#{item_num}")
	public CartVO getCurrentQuantity(long user_num, long item_num);//유저, 아이템 넘버를 통한 현재 카트 주문량 구하기
	
	@Update("UPDATE shop_cart SET order_quantity=#{total_quantity} WHERE cart_num=#{cart_num}")
	public void updateTotalQuantity(int total_quantity, long cart_num);
	
	

	
	//----------관리자(아티스트)-------------
	//전체 주문 개수/검색 주문 개수
	//주문 목록(List형태)
	//관리자/사용자 - 주문 상세
	//배송상태 수정
	//주문 삭제(삭제시 재고를 원상 복귀시키지 하지 않으면서 주문취소일 때 재고 수량 원상 복귀)
	
	
	
	
	
	//----------좋아요----------
	
	
	


}
