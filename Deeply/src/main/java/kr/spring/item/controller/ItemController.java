package kr.spring.item.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.spring.item.service.ItemService;
import kr.spring.item.vo.ItemVO;
import kr.spring.member.vo.ArtistVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/item")
public class ItemController {
	@Autowired
	private ItemService itemService;
	
	//자바빈(VO)초기화
	@ModelAttribute
	public ItemVO initCommand() {
		return new ItemVO();
	}

	/*==============================
	 *  상품 등록
	 * =============================*/
	// 등록 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String form() {
	    return "itemWrite"; // Tiles View 이름 반환
	}

	// 전송된 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String submit(@Valid ItemVO itemVO, 
						BindingResult result, 
						HttpServletRequest request,
						RedirectAttributes redirect,
						@AuthenticationPrincipal PrincipalDetails principal) throws IllegalStateException, IOException {

	    log.debug("<<상품 등록>> :" + itemVO);

	    // 유효성 체크 결과 오류가 있으면 폼 호출
	    if (result.hasErrors()) {
	    	result.getFieldErrors().forEach(error -> {
	            log.error("Field: " + error.getField() + ", Message: " + error.getDefaultMessage());
	        });
	    	log.debug("<<FORM 리다이렉트>>");
	        return form();
	    }

	    // 회원 번호 읽기
	    MemberVO vo = principal.getMemberVO();
	    itemVO.setUser_num(vo.getUser_num());

	    // 파일 업로드
	    itemVO.setFilename(FileUtil.createFile(request, itemVO.getUpload()));

	    // 상품 등록하기
	    itemService.insertItem(itemVO);
	    
	    redirect.addFlashAttribute("result", "success");
	    log.debug("성공");
	    return "redirect:/item/list";
	}

	
	
	
	/*==============================
	 * 	상품 목록
	 * =============================*/
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list")
	public String getList(@RequestParam(defaultValue="1") int pageNum,
						  @RequestParam(defaultValue="1") int order,
						  String keyfield,
						  String keyword,
						  Model model,
						  @AuthenticationPrincipal 
						  PrincipalDetails principal) {
		
		log.debug("<<PrincipalDetails 객체>>: " + principal);
		log.debug("<<게시판 목록 - order>> : " + order);
		
		//아티스트 정보
		ArtistVO artist = principal.getArtistVO();
		Long auser_num = artist.getUser_num();
		
		//유저 정보
		MemberVO member = principal.getMemberVO();
		Long user_num = member.getUser_num();
		
		//구독한 아티스트정보
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		map.put("user_num", user_num);
		
		boolean isartist = auser_num != null && auser_num == principal.getArtistVO().getUser_num();
		
		if(isartist) {
			map.put("artist",artist);
			model.addAttribute("artist", artist);
		}
			
		
		//전체/검색 레코드수 
		int count = itemService.selectRowCount(map);
		
		//페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,pageNum,count,12,10,"list","&order="+order);
		
		
		List<ItemVO> list = null;
		if(count > 0) {
			map.put("order", order);
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = itemService.selectList(map);
		}
		
		
		model.addAttribute("count", count);
		model.addAttribute("member", member);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		
		log.debug("Logged in user's name: " + member.getName());
		return "itemList";
		
		
	}
	
	/*==============================
	 * 	상품 상세
	 * =============================*/
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail")
	public String process(long item_num,
						  Model model,
						  @AuthenticationPrincipal 
						  PrincipalDetails principal){
		
		log.debug("<<PrincipalDetails 객체>>: " + principal);
		log.debug("<<상품 상세 - item_num>> : " + item_num);
		
		MemberVO member = principal.getMemberVO();
		
		ItemVO item = itemService.selectitem(item_num);
		
		model.addAttribute("item",item);	
		model.addAttribute("member", member);
		return "itemView";
	}

	
	
	
	/*==============================
	 * 	상품 글 수정
	 * =============================*/
	//수정폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/update")
	public String formUpdate(@RequestParam("item_num") long item_num,
							 Model model,
						     @AuthenticationPrincipal 
							 PrincipalDetails principal) {
		ItemVO itemVO = itemService.selectitem(item_num);
		log.debug("<<등록된 상품 정보>> : " +itemVO);
		
		//DB에 저장된 파일 정보 구하기
		if(principal.getMemberVO().getUser_num() != itemVO.getUser_num()) {
			return "redirect:.common/accessDenied";
		}
		
		model.addAttribute("itemVO",itemVO);
		log.debug("<<22등록된 상품 정보>> : " +itemVO);
		return "itemModify";
	}
	//수정폼에서 전송된 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/update")
	public String submitUpdate(@Valid ItemVO itemVO,
								BindingResult result,
								HttpServletRequest request,
								Model model,
								@AuthenticationPrincipal 
								PrincipalDetails principal)
						  throws IllegalStateException, IOException{
		log.debug("<<상품 등록 수정>> : " + itemVO);
		
		//DB에 저장된 파일 정보 구하기
		ItemVO db_item = itemService.selectitem(itemVO.getItem_num());
		
		//전송된 데이터 유효성 체크 결과 오류가 있으면 폼 호출 -> 코드 이유 노션에 적기
		if(result.hasErrors()) {
			itemVO.setFilename(db_item.getFilename());
			return "itemModify";
		}
		
		//로그인한 회원번호와 작성자 회원번호 일치 여부 체크
		if(principal.getMemberVO().getUser_num() != 
				db_item.getUser_num()){
			return "redirect:/common/accessDenied";
		}
		
		//파일명 셋팅(FileUtil.createFile에서 파일이 없으면 null처리 했음->파일을 무조건 올려야하니까 필요 없는거 아닌가?)
		//itemVO.setFilename(FileUtil.createFile(request, itemVO.getUpload()));
		
		
		//파일을 교체했을 경우 기존 파일을 삭제
		if(itemVO.getFilename() != null && !itemVO.getUpload().isEmpty()) {
			//기존 파일(수정 작업 전 파일) 삭제 처리
			FileUtil.removeFile(request, db_item.getFilename());
		}
		//view에 표시할 메세지
		model.addAttribute("message","글 수정 완료!!");
		model.addAttribute("url",request.getContextPath() + "/item/detail?item_num=" + itemVO.getItem_num());
		//글 수정
				itemService.updateItem(itemVO);
		
		return "common/resultAlert";
	}
	
	
	/*==============================
	 * 	상품 글 삭제
	 * =============================*/
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete")
	public String submitDelete(@RequestParam("item_num") long item_num,
								HttpServletRequest request,
								@AuthenticationPrincipal 
								PrincipalDetails principal) 
										throws IllegalStateException, IOException{
		log.debug("<<삭제할 상품 번호>> : " + item_num);
		
		//DB에 저장된 파일 정보 구하기
		ItemVO db_item = itemService.selectitem(item_num);
		
		//로그인 일치시에 삭제하기
		if(principal.getMemberVO().getUser_num() != db_item.getUser_num()) {
			return "redirect:/common/accessDenied";
		}
		
		//글 삭제
		itemService.deleteItem(item_num);
		
		//파일 삭제
		if(db_item.getFilename() != null) {
			FileUtil.removeFile(request, db_item.getFilename());
		}
		
		//삭제 후 알람 메시지 띄우기 -> jsp에서 
		
		
		return "redirect:/item/list";
	}
	
}






























