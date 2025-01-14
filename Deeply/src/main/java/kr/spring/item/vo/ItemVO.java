package kr.spring.item.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ItemVO {
	private long item_num;        			// 상품 고유 번호 (PK)
	private long user_num;        			// 유저 번호 (FK)
	@NotBlank
	private String item_name;		  		// 상품 이름
	private MultipartFile upload;				//파일
	private String filename;					//파일명
	@NotNull
	private int item_price;     			    // 상품 가격
	@NotBlank
	private String item_description; 		// 상품 상세 설명 (CLOB)
	private String desc_photo1;
	private String desc_photo2;
	private String desc_photo3;
	private MultipartFile upload1;	
	private MultipartFile upload2;	
	private MultipartFile upload3;	
	
	private java.util.Date item_regdate;		// 상품 등록일 (자동 설정)
	private java.util.Date item_modifydate;	// 상품 수정일
	@NotNull
	private int item_stock;      			// 상품 재고 수량
	
	private long group_num;
	private String group_name; 				//아티스트 이름
	private String group_photo;	//아티스크 사진
	private int rnum;
	private int group_cnt;
	
}
