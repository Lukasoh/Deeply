package kr.spring.letter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.letter.vo.LetterVO;
import kr.spring.letter.vo.ReplyVO;

@Mapper
public interface LetterMapper {
	
	//편지vo
	public List<LetterVO> selectLetterByUser(Map<String,Object> map);
	public int countLetterByUser(Map<String,Object> map);
	public void postLetter(LetterVO letterVO);
	@Select("SELECT * FROM letter WHERE letter_num=#{letter_num}")
	public LetterVO showLetterDetail(long letter_num);
	@Update("UPDATE letter SET replied=1 WHERE letter_num=#{letter_num}")
	public void updateReplied(long letter_num);
	
	//Reply
	public int countReply(Map<String,Object> map);
	public List<ReplyVO> showReplyForUser(Map<String,Object> map);
	
	//아티스트
	public int countLetterForArtist(Map<String,Object> map);
	public List<LetterVO> selectLetterForArtist(Map<String, Object> map);
	public int countReplyForArtist(long artist_num);
	public List<ReplyVO> showReplyForArtist(Map<String, Object> map);
	
	//아티스트 답장
	public void postReply(ReplyVO replyVO);
	@Select("SELECT * FROM reply JOIN duser USING(user_num) WHERE reply_num=#{reply_num}")
	public ReplyVO showReplyDetail(long reply_num);
	
}
