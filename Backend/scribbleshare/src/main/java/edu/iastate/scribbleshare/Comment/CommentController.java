package edu.iastate.scribbleshare.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import antlr.collections.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.iastate.scribbleshare.ScribbleshareApplication;
import edu.iastate.scribbleshare.Frame.Frame;
import edu.iastate.scribbleshare.Frame.FrameRepository;
import edu.iastate.scribbleshare.Post.Post;
import edu.iastate.scribbleshare.User.User;
import edu.iastate.scribbleshare.User.UserRepository;
import edu.iastate.scribbleshare.helpers.Status;
import io.swagger.annotations.ApiOperation;

@RestController
public class CommentController {
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FrameRepository frameRepository;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static String uploadPath = "/uploadedFiles/";

    private static final Logger logger = LoggerFactory.getLogger(ScribbleshareApplication.class);

    @ApiOperation(value = "Create New Comment. Returns the post where the comment was created.", response = Post.class, tags= "Comments")
    @PutMapping(path="/comment")
    public Post addNewComment(HttpServletResponse response, @RequestParam("username") String username, @RequestParam("frameId") int frameId, @RequestParam("image") MultipartFile imageFile) throws IllegalStateException, IOException{
        Optional<User> optionalUser = userRepository.findById(username);
        if(!optionalUser.isPresent()){
            Status.formResponse(response, HttpStatus.BAD_REQUEST, "username: " + username + " not found!");
            return null;
        }
        
        Optional<Frame> optionalFrame = frameRepository.findById(frameId);
        if(!optionalFrame.isPresent()){
            Status.formResponse(response, HttpStatus.BAD_REQUEST, "frameId: " + frameId + " not found!");
            return null;
        }
        User user = optionalUser.get();
        Frame frame = optionalFrame.get();

        String fullPath = httpServletRequest.getServletContext().getRealPath(uploadPath);

        if (!new File(fullPath).exists()){
            new File(fullPath).mkdir();
        }

        Comment comment = new Comment(user);
        comment.setFrame(frame);
        commentRepository.save(comment);
        frame.getComments().add(comment);
        frameRepository.save(frame);
        fullPath += "comment_" + comment.getID() + "_" + imageFile.getOriginalFilename();
        comment.setPath(fullPath);

        user.getComments().add(comment);
        userRepository.save(user);

        //write file to disk
        File tempFile = new File(fullPath);
        imageFile.transferTo(tempFile);
        
        commentRepository.save(comment);

        logger.info("created comment with id: " + comment.getID() + " and path: " + comment.getPath());
        return frame.getPost();
    }

    @ApiOperation(value = "Get Comment Image by comment Id", response = ResponseEntity.class, tags= "Comments")
    @GetMapping(path="/comment/{id}/image")
    public ResponseEntity<ByteArrayResource> getCommentImage(HttpServletResponse response, @PathVariable int id) throws IOException{
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if(!optionalComment.isPresent()){Status.formResponse(response, HttpStatus.NOT_FOUND, "Post with id: " + id + " not found!"); return null;}
        
        Comment comment = optionalComment.get();
        if(comment.getPath() == null){
            Status.formResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Comment image path is null");
            return null;
        }

        String pathStr = comment.getPath();
        File file = new File(pathStr);
        if(!file.exists()){
            Status.formResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Comment image path: " + pathStr + " not found!");
            return null;
        }

        String[] splitPath = pathStr.split("/");
        String fileName = splitPath[splitPath.length - 1];

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource data = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(data);
    }

    @ApiOperation(value = "Get Comments for Frame by Frame Id", response = Iterable.class, tags= "Comments")
    @GetMapping(path="/comment/{frameId}")
    public Iterable<Comment> getCommentsForFrame(HttpServletResponse response, @PathVariable int frameId){
        Optional<Frame> optionalFrame = frameRepository.findById(frameId);
        if(!optionalFrame.isPresent()){
            Status.formResponse(response, HttpStatus.BAD_REQUEST, "frameId: " + frameId + " not found!");
            return null;
        }

        return optionalFrame.get().getComments();
    }

    @PostMapping(path="/comment/like")
    public Comment likeComment(HttpServletResponse response, @RequestParam int comment_id, @RequestParam String username){
        Optional<Comment> optionalComment = commentRepository.findById(comment_id);
        Optional<User> optionalUser = userRepository.findById(username);

        if(!optionalComment.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "Comment with id: " + comment_id + " not found!");
            return null;
        }

        if(!optionalUser.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "User with username: " + username + " not found!");
            return null;
        }

        Comment comment = optionalComment.get();
        User user = optionalUser.get();

        if(user.getLikedComments().contains(comment)){
            return comment;
        }

        user.getLikedComments().add(comment);
        userRepository.save(user);
        comment.getLikedUsers().add(user);
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);

        return comment;

    }

    @DeleteMapping(path="/comment/like")
    public Comment removeLikeComment(HttpServletResponse response, @RequestParam int comment_id, @RequestParam String username){
        Optional<Comment> optionalComment = commentRepository.findById(comment_id);
        Optional<User> optionalUser = userRepository.findById(username);

        if(!optionalComment.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "Comment with id: " + comment_id + " not found!");
            return null;
        }

        if(!optionalUser.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "User with username: " + username + " not found!");
            return null;
        }

        Comment comment = optionalComment.get();
        User user = optionalUser.get();

        if(!user.getLikedComments().contains(comment)){
            return comment;
        }

        user.getLikedComments().remove(comment);
        userRepository.save(user);
        comment.getLikedUsers().remove(user);
        comment.setLikeCount(comment.getLikeCount() - 1);
        commentRepository.save(comment);

        return comment;

    }
    
    @GetMapping(path="/comment/{comment_id}/likedBy/{username}")
    public String getIfPostLikedByUser(HttpServletResponse response, @PathVariable int comment_id, @PathVariable String username){
        Optional<Comment> optionalComment = commentRepository.findById(comment_id);
        Optional<User> optionalUser = userRepository.findById(username);

        if(!optionalComment.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "Comment with id: " + comment_id + " not found!");
            return null;
        }

        if(!optionalUser.isPresent()){
            Status.formResponse(response, HttpStatus.NOT_FOUND, "User with username: " + username + " not found!");
            return null;
        }

        Comment comment = optionalComment.get();
        User user = optionalUser.get();
        // {postId: 5, isLiked: true}
        boolean isLiked = commentRepository.getCommentLikedByUser(comment.getID(), user.getUsername()) > 0;
        return "{commentId:" + comment.getID() + ", isLiked: " + isLiked + "}";
    }

}
