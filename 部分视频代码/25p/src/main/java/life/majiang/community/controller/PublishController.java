package life.majiang.community.controller;

import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    /**
     *通过form表单里的地址找到该处，同时获得三个参数title，description，tag
     * @return
     */
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                     @RequestParam("description") String description,
                     @RequestParam("tag") String tag,
                     HttpServletRequest request,
                     Model model){

        // 当提示不为空错误时，填写的内容回显到页面
        // model的作用就是与前端进行交互
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        // 当输入框为空的时候，就要发起提示
        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }

        if(description == null || description ==""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }

        if(tag == null || tag == ""){
            model.addAttribute("eror","标签不能为空");
            return "publish";
        }


// 通过cookie的方式获取数据库中的user信息，如果user信息存在就绑定到session上
        User user = null;
        System.out.println("进来了1");
        Cookie[] cookies = request.getCookies();
        if(cookies !=null && cookies.length!=0){
            for (Cookie cookie : cookies) {
                System.out.println("进来了2");
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);

                    if(user!=null){
                        // 绑定user信息到session上
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }

//　若用户不存在就把信息返回到前端去，model的作用就是与前端的交互
        if(user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        // 全部完成后构建一个question对象
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmt_create(System.currentTimeMillis());
        question.setGmt_modified(question.getGmt_create());

        questionMapper.create(question);
        return "redirect:/";
    }
}
