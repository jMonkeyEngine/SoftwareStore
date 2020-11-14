package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.database.entity.message.Message;
import com.jayfella.website.database.entity.message.MessageReply;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.MessageReplyRepository;
import com.jayfella.website.database.repository.MessagesRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.http.request.message.NewMessageRequest;
import com.jayfella.website.http.request.message.NewReplyRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping(value = "/api/messages/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApiMessagesController {

    @Autowired private UserRepository userRepository;
    @Autowired private MessagesRepository messagesRepository;
    @Autowired private MessageReplyRepository replyRepository;

    @GetMapping("/{messageId}")
    public ResponseEntity getSingleMessage(ModelMap model,
                                           @PathVariable("messageId") long messageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        Message message = messagesRepository.findById(messageId).orElse(null);

        if (message == null) {
            return new ResponseEntity<>(new SimpleApiResponse("The requested message does not exist."), HttpStatus.NOT_FOUND);
        }

        if (!user.equals(message.getSender()) && !user.equals(message.getRecipient())) {
            return ApiResponses.insufficientPermission();
        }

        if (!message.isDelivered()) {
            message.setDelivered(true);
            messagesRepository.save(message);
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // USER get all messages
    @GetMapping
    public ResponseEntity getUserMessages(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        Iterable<Message> messages = messagesRepository.findByRecipientOrderByDateDesc(user);

        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // USER post new message
    @PostMapping
    public ResponseEntity createMessage(ModelMap model,
                                        @ModelAttribute @Valid NewMessageRequest newMessageRequest,
                                        BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        User recipient = userRepository.findByUsernameIgnoreCase(newMessageRequest.getRecipient()).orElse(null);

        if (recipient == null) {
            return new ResponseEntity<>(new SimpleApiResponse("Could not locate username: " + newMessageRequest.getRecipient()), HttpStatus.NOT_FOUND);
        }

        Message message = new Message();
        message.setDate(System.currentTimeMillis());
        message.setTitle(newMessageRequest.getTitle());
        message.setMessage(newMessageRequest.getContent());

        message.setSender(user);
        message.setRecipient(recipient);

        messagesRepository.save(message);

        return new ResponseEntity<>(new SimpleApiResponse("Message sent successfully."), HttpStatus.OK);

    }

    // USER create new reply
    @PostMapping("/reply")
    public ResponseEntity createReply(ModelMap model,
                                      @ModelAttribute @Valid NewReplyRequest newReplyRequest,
                                      BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        // check if the request has errors first so we can avoid a database lookup if it's wrong.
        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        Message message = messagesRepository.findById(newReplyRequest.getMessageId()).orElse(null);

        if (message == null) {
            return new ResponseEntity<>(new SimpleApiResponse("The specified message does not exist."), HttpStatus.NOT_FOUND);
        }

        if (!user.equals(message.getSender()) || !user.equals(message.getRecipient())) {
            return new ResponseEntity<>(new SimpleApiResponse("You do not have permission to participate in this message."), HttpStatus.FORBIDDEN);
        }

        MessageReply reply = new MessageReply();
        reply.setContent(newReplyRequest.getContent());
        reply.setDate(System.currentTimeMillis());
        reply.setMessage(message);
        reply.setUser(user);

        replyRepository.save(reply);

        return new ResponseEntity<>(reply, HttpStatus.OK);

    }

    // USER get replies to a message
    @GetMapping("/replies/{messageId}")
    public ResponseEntity getReplies(ModelMap model,
                                     @PathVariable("messageId") long messageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        Message message = messagesRepository.findById(messageId).orElse(null);

        if (message == null) {
            return new ResponseEntity<>(new SimpleApiResponse("This message does not exist."), HttpStatus.NOT_FOUND);
        }

        if (!user.equals(message.getSender()) || !user.equals(message.getRecipient())) {
            return new ResponseEntity<>(new SimpleApiResponse("You do not have permission to view the replies to this message"), HttpStatus.FORBIDDEN);
        }

        //List<MessageReply> replies = message.getReplies();
        Iterable<MessageReply> replies = replyRepository.findByMessage(message);

        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

}
