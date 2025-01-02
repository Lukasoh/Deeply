package kr.spring.video.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.video.vo.VideoVO;

@Mapper
public interface VideoMapper {
    @Select("SELECT * FROM video ORDER BY created_at DESC")
    public List<VideoVO> selectList(Map<String, Object> map);

    @Select("SELECT COUNT(*) FROM video")
    public Integer selectRowCount(Map<String, Object> map);

    @Select("SELECT * FROM video WHERE video_id = #{videoId}")
    public VideoVO selectVideo(Long videoId);
    
    public void insertVideo(VideoVO video);

    @Update("UPDATE video SET views = views + 1 WHERE video_id = #{videoId}")
    public void updateHit(Long videoId);

    @Update("UPDATE video SET title = #{title}, description = #{description}, is_exclusive = #{isExclusive}, media_url = #{mediaUrl}, category_id = #{categoryId} WHERE video_id = #{videoId}")
    public void updateVideo(VideoVO video);

    @Delete("DELETE FROM video WHERE video_id = #{videoId}")
    public void deleteVideo(Long videoId);
    
    @Select("SELECT VIDEO_SEQ.NEXTVAL FROM dual")
    Long getNextVideoId();

	

}
