package life.majiang.community.mapper;

import life.majiang.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


// 根据mybatis官网给的代码，进行插入操作

@Mapper
public interface UserMapper {

        @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{account_id},#{token},#{gmt_create},#{gmt_modified},#{avatar_url})")
        void insert(User user);

        @Select("select * from user where token = #{token}")
        User findByToken(@Param("token") String token);

        @Select("select * from user where id = #{id}")
        User findByID(Integer id);
}



