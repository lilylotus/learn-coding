package cn.nihility.api.controller;

import cn.nihility.api.dto.ValidateDto;
import cn.nihility.api.validate.ValidateAddGroup;
import cn.nihility.api.validate.ValidateEditGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nihility
 * @date 2022/03/04 13:45
 */
@RestController
public class ValidateController {

    private static final Logger log = LoggerFactory.getLogger(ValidateController.class);

    @PostMapping("/validate")
    public ValidateDto validatePost(@RequestBody @Validated(ValidateAddGroup.class) ValidateDto dto) {
        log.info("validate Post dto [{}]", dto);
        return dto;
    }

    @GetMapping("/validate")
    public ValidateDto validateGet(@Validated(ValidateEditGroup.class) ValidateDto dto) {
        log.info("validate Get dto [{}]", dto);
        return dto;
    }

}
