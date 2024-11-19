package be.sddevelopment.validation.dsl;

import be.sddevelopment.commons.access.AccessProtectionUtils;
import be.sddevelopment.commons.annotations.Utility;
import be.sddevelopment.validation.core.ModularRuleset;

import java.nio.file.Path;

import static be.sddevelopment.commons.access.AccessProtectionUtils.utilityClassConstructor;

@Utility
public final class ValidatorParser {

    private ValidatorParser() {
        utilityClassConstructor();
    }

    public static ModularRuleset fromSpecification(Path validationSpec) {
        return null;
    }
}
