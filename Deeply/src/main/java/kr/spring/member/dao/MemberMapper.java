package kr.spring.member.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	 @Select("SELECT SEQ_USER_NUM.nextval FROM dual")
	    long selectUser_num();

	    // 사용자 정보를 DB에 삽입하는 메소드
	    @Insert("INSERT INTO duser(user_num, id, nick_name) VALUES (#{user_num, jdbcType=BIGINT}, #{id}, #{nick_name})")
	    void insertMember(MemberVO member);
	public void insertMember_detail(MemberVO member);
	public MemberVO selectIdAndNickName(Map<String,String> map);

	public MemberVO selectCheckMember(String id);
	Optional<MemberVO> findByEmail(String email);
	@Select("SELECT * FROM duser JOIN duser_detail USING (user_num) WHERE user_num=#{user_num}")
	public MemberVO selectMember(Long mem_num);
	void updateMember(MemberVO memberVO);
	
	@Select("SELECT d.id FROM duser d JOIN duser_detail dd ON d.user_num = dd.user_num WHERE dd.email = #{email} AND dd.name = #{name}")
	public String findIdByNameAndEmail(@Param("name") String name, @Param("email") String email);
	@Select("SELECT d.id FROM auser d JOIN auser_detail dd ON d.user_num = dd.user_num WHERE dd.email = #{email} AND dd.name = #{name}")
	public String findIdByNameAndEmail2(@Param("name") String name, @Param("email") String email);
	
	
	@Select("SELECT d.id FROM duser d JOIN duser_detail dd ON d.user_num = dd.user_num WHERE dd.email = #{email}")
	public String findPasswdByNameAndEmail(@Param("email") String email);
	@Select("SELECT d.id FROM auser d JOIN auser_detail dd ON d.user_num = dd.user_num WHERE dd.email = #{email}")
	public String findPasswdByNameAndEmail2( @Param("email") String email);
	@Update("UPDATE duser_detail SET passwd_hash = #{newPassword} WHERE email = #{email}")
	public void resetPassword(String email ,String newPassword);
	
	@Update("UPDATE auser_detail SET passwd_hash = #{newPassword} WHERE email = #{email}")
	public void resetPassword2(String email ,String newPassword);
	
	public int selectRowCount(Map<String,Object> map);
	public List<MemberVO> selectList(Map<String,Object> map);
}










