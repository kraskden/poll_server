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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fields/")
@Slf4j
public class FieldController {

    private final FieldService fieldService;
    private final UserService userService;

    @Autowired
    public FieldController(FieldService fieldService, UserService userService) {
        this.fieldService = fieldService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addField(@RequestBody @Valid FieldDto fieldDto) {

        if (!isValidDto(fieldDto)) {
            return new ResponseEntity<String>("Field invalid", HttpStatus.BAD_REQUEST);
        }

        Field field = fieldFromDto(fieldDto);

        getUser().ifPresent(user -> {
            fieldService.addField(user, field);
        });

        return new ResponseEntity<String>("Added successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteField(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return new ResponseEntity<String>("Id not provided", HttpStatus.BAD_REQUEST);
        }
        getUser().ifPresent(user -> {
           fieldService.removeField(user, id);
        });

        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<GetResDto> getFields() {
        Optional<User> optionalUser =  getUser();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<FieldDto> fieldDtos =  fieldService.getFields(user).stream()
                    .map(this::dtoFromField).collect(Collectors.toList());
            GetResDto res = new GetResDto();
            res.setFields(fieldDtos);
            return ResponseEntity.ok().body(res);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateField(@PathVariable(value = "id") Long id, @RequestBody FieldDto fieldDto) {
        if (id == null || !isValidDto(fieldDto)) {
            return new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
        }

        Field field = fieldFromDto(fieldDto);
        getUser().ifPresent(user -> {
            fieldService.updateField(user, id, field);
        });
        return ResponseEntity.ok().body("Updated");
    }

    private Optional<User> getUser() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails details = (UserDetails)obj;

        return userService.findByEmail(details.getUsername());
    }

    // @Valid is not working, no idea why
    private Boolean isValidDto(FieldDto dto) {
        return  dto.getName() != null &&
                dto.getIsEnabled() != null &&
                dto.getIsRequired() != null &&
                dto.getFieldType() != null &&
                dto.getProperties() != null;
    }

    // TODO: move this methods to FieldDto class

    private Field fieldFromDto(FieldDto dto) {
        Field ret = new Field();

        ret.setName(dto.getName());
        ret.setIsActive(dto.getIsEnabled());
        ret.setIsRequired(dto.getIsRequired());
        ret.setFieldType(dto.getFieldType());

        List<FieldProperty> properties = dto.getProperties().stream()
                .map(propName -> {
                    FieldProperty prop = new FieldProperty();
                    prop.setField(ret);
                    prop.setName(propName);
                    return prop;
                }).collect(Collectors.toList());
        ret.setProperties(properties);
        return ret;
    }

    private FieldDto dtoFromField(Field f) {
        FieldDto dto = new FieldDto();

        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setFieldType(f.getFieldType());
        dto.setIsEnabled(f.getIsActive());
        dto.setIsRequired(f.getIsRequired());

        List<String> props = f.getProperties().stream()
                .map(FieldProperty::getName).collect(Collectors.toList());

        dto.setProperties(props);

        return dto;
    }
}
