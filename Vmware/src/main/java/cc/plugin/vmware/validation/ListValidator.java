/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 设备名称注解
 *
 * @since 2019 -01-01
 */
public class ListValidator implements ConstraintValidator<ListString, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isExist(value);
    }

    private boolean isExist(Object value) {
        List<String> params = (List<String>) value;
        return params.stream().allMatch(StringUtils::isNotEmpty);
    }
}
