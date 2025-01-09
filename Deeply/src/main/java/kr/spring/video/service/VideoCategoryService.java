package kr.spring.video.service;

import java.util.List;

import kr.spring.video.vo.VideoCategoryVO;

public interface VideoCategoryService {
    List<VideoCategoryVO> getCategoriesByArtist(Long artistId); // 아티스트별 카테고리 가져오기
    VideoCategoryVO addCategory(Long artistId, String categoryName); // 새 카테고리 추가
}
