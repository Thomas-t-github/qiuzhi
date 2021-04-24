package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.model.Message;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.model.ViewObject;
import com.tyt.qiuzhi.service.MessageService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/msg")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);


    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/addMessage",method = RequestMethod.POST)
    public String addMessage(Model model,@RequestParam("uid") int uid,
                             @RequestParam("content") String content){

        if (hostHolder.getUser() == null){
            return "user/login";
        }

        try {

            int fromId = hostHolder.getUser().getId();
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(uid);
            message.setHasRead(0);
            message.setConversationId(fromId < uid ? String.format("%d_%d", fromId, uid) : String.format("%d_%d", uid, fromId));
            messageService.addMessage(message);
        }catch (Exception e){
            logger.error("发送私信失败："+e.getMessage());
            model.addAttribute("status",1);
            model.addAttribute("msg","发送私信失败");
            return "redirect:/user/homepage/"+uid;
        }
        model.addAttribute("status",0);
        model.addAttribute("msg","发送私信成功");
        return "forward:/user/toLogin";
    }

    @RequestMapping(path = {"/list"}, method = {RequestMethod.GET})
    public String getConversationList(Model model){

        try {
            if (hostHolder.getUser() == null){
                return "redirect:/user/toLogin";
            }
            List<Message> conversationList = messageService.getConversationList(hostHolder.getUser().getId(), 0, 10);
            List<ViewObject> vos = new ArrayList<>();

            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                //vo.set("message",message);
                vo.set("conversationId",message.getConversationId());
                int targetId = hostHolder.getUser().getId() == message.getFromId() ? message.getToId() : message.getFromId();
                vo.set("listUser",userService.selectById(targetId));
                //vo.set("unread",messageService.getConvesationUnreadCount(hostHolder.getUser().getId(),message.getConversationId()));
                vo.set("messageCount",messageService.getMessageCount(message.getConversationId()));
                vos.add(vo);
            }
            String conversationId = QiuzhiUtils.SYSTEM_USERID+"_"+hostHolder.getUser().getId();
            List<Message> systemMessages = messageService.getConversationDetail(conversationId, 0, 10);
            model.addAttribute("conversations",vos);
            model.addAttribute("systemMessages",systemMessages);
            model.addAttribute("conversationId",conversationId);
            if (hostHolder.getUser() != null){
                model.addAttribute("detailUser",hostHolder.getUser());
            }
        } catch (Exception e) {
            logger.error("获取消息列表失败："+e.getMessage());
        }
        return "user/message";
    }

    @RequestMapping(path = {"/detail/{conversationId}"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @PathVariable("conversationId") String conversationId){

        try {
            if (hostHolder.getUser() == null){
                return "redirect:/user/toLogin";
            }
            List<Message> conversationList = messageService.getConversationList(hostHolder.getUser().getId(), 0, 10);
            List<ViewObject> vos1 = new ArrayList<>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                int targetId = hostHolder.getUser().getId() == message.getFromId() ? message.getToId() : message.getFromId();

                vo.set("conversationId",message.getConversationId());
                vo.set("listUser",userService.selectById(targetId));

                vo.set("messageCount",messageService.getMessageCount(message.getConversationId()));
                vos1.add(vo);
            }
            model.addAttribute("conversations",vos1);
            for (ViewObject viewObject : vos1) {
                System.out.println("数据："+viewObject.get("listUser"));
            }
            List<Message> conversationDetail = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> vos = new ArrayList<>();
            int detailUserId = 1;
            for (Message message : conversationDetail) {
                ViewObject vo = new ViewObject();
                detailUserId = hostHolder.getUser().getId() == message.getFromId() ? message.getToId() : message.getFromId();
                vo.set("message",message);
                User user = userService.selectById(message.getFromId());
                vo.set("detailUser",user);
                vos.add(vo);
            }
            model.addAttribute("detailUser",userService.selectById(detailUserId));
            model.addAttribute("detailConversations",vos);
            model.addAttribute("conversationId",conversationId);
        } catch (Exception e) {
            logger.error("获取消息列表失败："+e.getMessage());
        }
        return "user/message";
    }

    @RequestMapping(value = "/deleteMessage", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String deleteMessage(@RequestParam("mid") int mid){
        messageService.updateMessageStatus(mid);
        return QiuzhiUtils.getJSONString(0,"删除成功");
    }

    @RequestMapping(value = "/deleteAllMessage", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String deleteAllMessage(@RequestParam("conversationId") String conversationId){
        messageService.updateMessageByConversationIdStatus(conversationId);
        return QiuzhiUtils.getJSONString(0,"删除成功");
    }



}
