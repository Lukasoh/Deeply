package kr.spring.letter.service;

import java.util.List;
import java.util.Map;

import kr.spring.letter.vo.LetterVO;

public interface LetterService {
	public List<LetterVO> selectLetterByUser(Map<String,Object> map);
	public int countLetterByUser(Map<String,Object> map);
	public void postLetter(LetterVO letterVO);
	public LetterVO showLetterDetail(long letter_num);
}
