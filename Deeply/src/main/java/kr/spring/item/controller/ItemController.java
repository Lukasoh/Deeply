package kr.spring.item.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import kr.spring.member.service.ArtistService;
import kr.spring.member.vo.AgroupVO;
import kr.spring.member.vo.ArtistVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/item")
public class ItemController {
	@Autowired
	private ItemService itemService;
	@Autowired
	private ArtistService artistService;
	
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

	    //파일 유효성 체크
	    if(itemVO.getUpload() == null || itemVO.getUpload().isEmpty()) {
	    	result.rejectValue("upload", "file.required");
	    }
	    
	    // 유효성 체크 결과 오류가 있으면 폼 호출
	    if (result.hasErrors()) {
	    	ValidationUtil.printErrorFields(result);
	    	log.debug("<<FORM 리다이렉트>>");
	        return form();
	    }

	    // 회원 번호 읽기
	    ArtistVO vo = principal.getArtistVO();
	    String group_name = vo.getGroup_name();
	    AgroupVO agroupVO = itemService.selectGroup(group_name);
	    itemVO.setUser_num(agroupVO.getGroup_num());
	    // 파일 업로드
	    itemVO.setFilename(FileUtil.createFile(request, itemVO.getUpload()));
	    
	    
	    // 상품 등록하기
	    itemService.insertItem(itemVO);
	    	
	    redirect.addFlashAttribute("result", "success");
	    log.debug("성공");
	    return "redirect:/item/main";
	}

	
	/*==============================
	 * 	상품 메인
	 * =============================*/
	
	 @GetMapping("/main")
	 public String getMain(String keyfield,
			 			   String keyword,
			 			   Model model,
			 			   @AuthenticationPrincipal PrincipalDetails principal) {

		log.debug("<<PrincipalDetails 객체>>: " + principal);

		Map<String,Object> map = new HashMap<String,Object>();
		
		if(principal != null) {
			//아티스트 계정으로 접속
			if(principal.getArtistVO() != null) {
				ArtistVO artist = principal.getArtistVO();
				Long auser_num = artist.getUser_num();
				map.put("user_num", auser_num);
				model.addAttribute("auser_num", auser_num);
			}
			
			//유저 계정으로 접속
			else if(principal.getMemberVO() != null) {
				MemberVO member = principal.getMemberVO();
				Long duser_num = member.getUser_num();
				map.put("user_num", duser_num);
				model.addAttribute("member", member);
			}
		}
		
		List<ItemVO> list = itemService.showListByGroup();
		List<ItemVO> group = itemService.showListGroup();

		model.addAttribute("list", list);
		model.addAttribute("group", group);

		return "itemMain";
	}
	 
	
	/*==============================
	 * 	상품 목록
	 * =============================*/
	@GetMapping("/list")
	public String getList(@RequestParam(defaultValue="1") int pageNum,
						  @RequestParam(defaultValue="1") int order,
						  Long user_num,
						  String keyfield,
						  String keyword,
						  Model model,
						  @AuthenticationPrincipal PrincipalDetails principal) {

		log.debug("<<PrincipalDetails 객체>>: " + principal);
		log.debug("<<게시판 목록 - order>> : " + order);
		log.debug("<<user_num>> : " + user_num);

		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(principal != null) {
		//아티스트 계정으로 접속
		if(principal.getArtistVO() != null) {
			ArtistVO artist = principal.getArtistVO();
			Long auser_num = artist.getUser_num();
			map.put("user_num", auser_num);
			model.addAttribute("auser_num", auser_num);
		}
		
		//유저 계정으로 접속
		else if(principal.getMemberVO() != null) {
			MemberVO member = principal.getMemberVO();
			Long duser_num = member.getUser_num();
			map.put("user_num", duser_num);
			model.addAttribute("member", member);
		  }
		}
		
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		map.put("user_num", user_num);

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
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		return "itemList";
	}
	
	/*==============================
	 * 	상품 상세
	 * =============================*/
	//@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail")
	public String process(long item_num,
						  Model model,
						  @AuthenticationPrincipal 
						  PrincipalDetails principal){
		
		log.debug("<<PrincipalDetails 객체>>: " + principal);
		log.debug("<<상품 상세 - item_num>> : " + item_num);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		
		
		ItemVO item = itemService.selectitem(item_num);
		AgroupVO agroup = artistService.selectArtistDetail(item.getUser_num());
		
		model.addAttribute("item",item);
		model.addAttribute("agroup", agroup);
		
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
		log.debug("<<getArtistVO 객체>>: " + principal.getArtistVO());
		
		long artist_num = principal.getArtistVO().getUser_num();
		AgroupVO agroupVO = artistService.selectAgroupByArtistNum(artist_num);
		
		ItemVO itemVO = itemService.selectitem(item_num);
		log.debug("AgroupVO 정보 : " + agroupVO);
		log.debug("<<등록된 상품 정보>> : " +itemVO);
		log.debug("<<list에서 받아온 item_num>> : " +item_num);
		log.debug("<<상품 작성자 유저 번호>> : " + itemVO.getUser_num());
		log.debug("<<등록할 떄 ItemVO filename>>: " + itemVO.getFilename());
		
		
		//DB에 저장된 파일 정보 구하기
		if(agroupVO.getGroup_num() != itemVO.getUser_num()) {
			return "redirect:common/accessDenied";
		}else {		
		model.addAttribute("item",itemVO);
		log.debug("<<등록된 상품 정보>> : " +itemVO);
		log.debug("<<등록 완료한 ItemVO filename>>: " + itemVO.getFilename());
		return "itemModify";
		}	
	}
	
	
	//수정폼에서 전송된 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/update")
	public String submitUpdate(@Valid ItemVO itemVO,
					            BindingResult result,
					            @RequestParam("item_num") long item_num,
					            Long user_num,
					            HttpServletRequest request,
					            Model model,
					            @AuthenticationPrincipal
					            PrincipalDetails principal)
					            		throws IllegalStateException, IOException{
		
		if(principal != null) {
			//아티스트 계정으로 접속
			if(principal.getArtistVO() != null) {
				ArtistVO artist = principal.getArtistVO();
				Long auser_num = artist.getUser_num();
			
			
			}
			
			
		}
		log.debug("<<글 수정 item_num>> : " + item_num);
		log.debug("<<글 수정>> : " + itemVO);
		
		 //DB에 저장된 파일 정보 구하기
        ItemVO db_item = itemService.selectitem(itemVO.getItem_num());
        log.debug("<<db_item 정보>> : " + db_item);
        
        //파일 유효성 체크
       if(itemVO.getUpload() == null || itemVO.getUpload().isEmpty()) {
    	   if(db_item.getFilename() != null) {
    		   itemVO.setFilename(db_item.getFilename());
    	   }
       }else {
    	   itemVO.setFilename(FileUtil.createFile(request, itemVO.getUpload()));
       }
	    
	   
	    
	    // 유효성 체크 결과 오류가 있으면 폼 호출
	    if (result.hasErrors()) {
	    	ValidationUtil.printErrorFields(result);
	  	log.debug("<<FORM 리다이렉트>>");
	        //return form();
	  		model.addAttribute("item", db_item); // 기존 데이터 유지
	  		return "itemModify"; // 수정 폼 반환
	    }
	    
        
        


        //파일을 교체했을 경우 기존 파일을 삭제
       // if(itemVO.getFilename() != null && !itemVO.getUpload().isEmpty()) {
            //기존 파일(수정 작업 전 파일) 삭제 처리
            //FileUtil.removeFile(request, db_item.getFilename());
        //}
        
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
								Model model,
								HttpServletRequest request,
								@AuthenticationPrincipal 
								PrincipalDetails principal) 
										throws IllegalStateException, IOException{
		log.debug("<<삭제할 상품 번호>> : " + item_num);
		
		//DB에 저장된 파일 정보 구하기
		ItemVO db_item = itemService.selectitem(item_num);
		
		long artist_num = principal.getArtistVO().getUser_num();
		AgroupVO agroupVO = artistService.selectAgroupByArtistNum(artist_num);
		
		//로그인 일치시에 삭제하기->
		if(agroupVO.getGroup_num() != db_item.getUser_num()) {
		//if(principal.getArtistVO().getUser_num() != db_item.getUser_num()) {
			
			log.debug("<<getAgroupVO>> : " + agroupVO.getGroup_num());
			log.debug("<<getItemVO user_num>> : " + db_item.getUser_num());
			return "redirect:/common/accessDenied";
		}
		
		//글 삭제
		itemService.deleteItem(item_num);
		
		//파일 삭제
		if(db_item.getFilename() != null) {
			FileUtil.removeFile(request, db_item.getFilename());
		}
		
		//삭제 후 알람 메시지 띄우기 -> Ajax에서 
		
		model.addAttribute("message","글 삭제 완료");
	    model.addAttribute("url",request.getContextPath() + "/item/main");
		return "common/resultAlert";
	}
	
}






























