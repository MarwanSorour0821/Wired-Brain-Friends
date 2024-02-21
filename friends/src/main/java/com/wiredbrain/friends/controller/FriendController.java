package com.wiredbrain.friends.controller;

import com.wiredbrain.friends.model.Friend;
import com.wiredbrain.friends.service.FriendService;
import com.wiredbrain.friends.util.ErrorMessage;
import com.wiredbrain.friends.util.FieldErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FriendController {

    @Autowired
    FriendService friendService;

    //USE POSTMAN TO INSERT THE FUNCTIONS aka post, get, delete URL: localhost:8080/...

    //create a friend
    @PostMapping("/friends")
    Friend create(/*@Valid*/ @RequestBody Friend friend){
         return friendService.save(friend);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    List<FieldErrorMessage> exceptionHandler (MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List <FieldErrorMessage> fieldErrorMessages = fieldErrors.stream().map(fieldError -> new FieldErrorMessage(fieldError.getField(),
                fieldError.getDefaultMessage())).collect(Collectors.toList());

        return fieldErrorMessages;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    ErrorMessage exceptionHandler(ValidationException e){
        return new ErrorMessage("400", e.getMessage());
    }

    //Get all friends
    @GetMapping("/friends")
    Iterable<Friend> read(){
        return friendService.findAll();
    }


    @PutMapping("/friends")
    ResponseEntity<Friend> update(@RequestBody Friend friend){

        //If the friend we want to update is inside our friend list then we update him
        if (friendService.findById(friend.getId()).isPresent())
            return new ResponseEntity<>(friendService.save(friend), HttpStatus.OK);
            //Otherwise it is dealt as a bad request
            //The BAD_REQUEST is a response code for HTTP
            //https://en.wikipedia.org/wiki/List_of_HTTP_status_codes is a list of HTTP response codes
        else return new ResponseEntity<>(friend, HttpStatus.BAD_REQUEST);
    }



    //Delete a friend
    @DeleteMapping("/friends/{id}")
    void delete(@PathVariable Integer id){
        friendService.deleteById(id);
    }

    //find friend by id
    @GetMapping("/friends/{id}")
    Optional<Friend> readID(@PathVariable Integer id){
        return friendService.findById(id);
    }

    //find friend by first AND last name
    @GetMapping("/friends/search")
    //Takes in 2 request parameters which are first and last name
    //When you do required = false, it means that these values do not need to be present for it to search,
    // or they are just not required
    Iterable<Friend> findByQuery(@RequestParam(value = "first", required = false) String firstName, @RequestParam(value = "last", required = false) String lastName){

        //Check conditions for the parameters
        if (firstName != null && lastName != null) return friendService.findByFirstAndLastName(firstName, lastName);
        else if (firstName != null) return friendService.findByFirstName(firstName);
        else if (lastName != null) return friendService.findByLastName(lastName);
        else return friendService.findAll();


    }



}
