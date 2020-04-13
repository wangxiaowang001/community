package life.majiang.community.service;

import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // service注解的作用？
public class QuestionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    // 由于需要获得question内容 和 user，分开处理是比较麻烦的，于是重新创建了一个中间数据传输层 QuestionDTO累
    // 整合了question和user类，而后创建了service类来组合操作把 question 和user数据整合到QuestionDTO中
    public List<QuestionDTO> list() {

        List<Question> questions = questionMapper.list();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findByID(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);

        }
        return questionDTOList;
    }
}
