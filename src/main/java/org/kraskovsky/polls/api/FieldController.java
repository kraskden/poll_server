package org.kraskovsky.polls.api;

import lombok.extern.slf4j.Slf4j;
import org.kraskovsky.polls.dto.field.FieldDto;
import org.kraskovsky.polls.dto.field.GetResDto;
import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldProperty;
import org.kraskovsky.polls.model.User;
import org.kraskovsky.polls.service.FieldService;
import org.kraskovsky.polls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fields/")
@Slf4j
@Validated
public class FieldController {

    private final FieldService fieldService;
    private final UserService userService;

    @Autowired
    public FieldController(FieldService fieldService, UserService userService) {
        this.fieldService = fieldService;
        this.userService = userService;
    }

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void test(@Validated @RequestBody FieldDto fieldDto) {
        log.info("{}", fieldDto);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addField(@RequestBody @Valid FieldDto fieldDto) {
        Field field = fieldDto.toField();

        userService.getUserFromSecurityContext().ifPresent(user -> {
            fieldService.addField(user, field);
        });

        return new ResponseEntity<String>("Added successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteField(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return new ResponseEntity<String>("Id not provided", HttpStatus.BAD_REQUEST);
        }
        userService.getUserFromSecurityContext().ifPresent(user -> {
           fieldService.removeField(user, id);
        });

        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }

    private GetResDto getFieldsForUser(User user) {
        List<FieldDto> fieldDtos =  fieldService.getFields(user).stream()
                .map(FieldDto::fromField).collect(Collectors.toList());
        GetResDto res = new GetResDto();
        res.setFields(fieldDtos);
        return res;
    }

    @GetMapping("/")
    public ResponseEntity<GetResDto> getFields() {
        Optional<User> optionalUser = userService.getUserFromSecurityContext();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok().body(getFieldsForUser(user));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/poll/{id}")
    public  ResponseEntity<GetResDto> getFieldsForPoll(@PathVariable(value = "id") Long pollOwnerId) {
        Optional<User> optionalUser = userService.findById(pollOwnerId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok().body(getFieldsForUser(user));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateField(@PathVariable(value = "id") Long id, @RequestBody FieldDto fieldDto) {
        if (id == null) {
            return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
        }

        Field field = fieldDto.toField();
        userService.getUserFromSecurityContext().ifPresent(user -> {
            fieldService.updateField(user, id, field);
        });
        return ResponseEntity.ok().body("Updated");
    }
    

}
