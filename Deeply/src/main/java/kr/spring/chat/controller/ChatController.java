package kr.spring.chat.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.spring.chat.service.ChatService;
import kr.spring.chat.vo.ChatVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private ChatService chatService;
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public ChatVO initCommand() {
		return new ChatVO();
	}
	
	/*========================
	 * 채팅방 만들기
	 *========================*/
	//채팅방 등록 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/chWrite")
	public String form() {
		return "chatWrite";
	}
	
	//형성된 채팅방 데이터 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/chWrite")
	public String submit(@Valid ChatVO chvo, BindingResult result,
			HttpServletRequest request, RedirectAttributes redirect,
			 @AuthenticationPrincipal 
             PrincipalDetails principal)throws IllegalStateException, IOException{
		
		log.debug("<<채팅방 형성>>" + chvo);
		//유효성 체크결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}
		//현재는 그냥 회원이면 방을 만들 수 있도록 설정
		//채팅방 만드는 회원번호 설정
		MemberVO vo = principal.getMemberVO();
		chvo.setAuser_num(vo.getUser_num());
		//제목(title=chat_name은 jsp에서 써서 전송할 예정)
		
		//채팅방 만들기
		try {
		    chatService.insertChatroom(chvo);
		} catch (Exception e) {
		    log.error("Error inserting chatroom", e);
		    throw new RuntimeException("Failed to create chatroom");
		}
		
		//브라우저에 데이터를 전송하지만 URI상에는 보이지 않는 숨겨진
		//데이터의 형태로 전달
		redirect.addFlashAttribute("result","success");
		

		return "redirect:/chat/chRoom";
	}
	
	//@GetMapping("/chRoom")
	//public String getList(
		//	) {}
	
	
	

}
